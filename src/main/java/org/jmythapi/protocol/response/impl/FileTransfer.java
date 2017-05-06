/*
 * Copyright (C) ${year} Martin Thelian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, please email thelian@users.sourceforge.net
 */
package org.jmythapi.protocol.response.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_44;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_59;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_FILETRANSFER;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_FILETRANSFER_DONE;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_FILETRANSFER_IS_OPEN;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_FILETRANSFER_REQUEST_BLOCK;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_FILETRANSFER_SEEK;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_FILETRANSFER_SET_TIMEOUT;
import static org.jmythapi.protocol.response.IFileTransfer.Props.FILESIZE;
import static org.jmythapi.protocol.response.IFileTransfer.Props.FILESIZE1;
import static org.jmythapi.protocol.response.IFileTransfer.Props.FILESIZE2;
import static org.jmythapi.protocol.response.IFileTransfer.Props.SOCKET_ID;
import static org.jmythapi.protocol.response.IFileTransfer.Props.STATUS;
import static org.jmythapi.protocol.utils.ResponseUtils.STATUS_OK;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.impl.Backend;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.AMythRequest;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IFileStatus;
import org.jmythapi.protocol.response.IFileTransfer;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IStorageGroupFile;
import org.jmythapi.protocol.utils.ResponseUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * @since {@mythProtoVersion 00}
 */
public class FileTransfer extends AMythResponse<IFileTransfer.Props> implements Closeable, IFileTransfer {
	public static final int DEFAULT_BUFFER_SIZE = 1 << 16;

	private IProgramInfo programInfo;
	private String fileName;
	private String storageGroup;

	private final IBackendConnection dataConnection;
	private final IBackendConnection commandConnection;
	private boolean done = false;

	public FileTransfer(IBackendConnection commandConnection, final IBackendConnection dataConnection, IMythPacket packet) {
		super(IFileTransfer.Props.class, packet);
		this.dataConnection = dataConnection;
		this.commandConnection = commandConnection;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("FileTransfer objects can not be cloned");
	}

	public void setProgramInfo(IProgramInfo programInfo) {
		this.programInfo = programInfo;
	}

	public void setFileInfo(String fileName, String storageGroup) {
		this.fileName = fileName;
		this.storageGroup = storageGroup;
	}

	public String getStatus() {
		return this.getPropertyValue(STATUS);
	}

	public int getSocketID() {
		return Integer.valueOf(this.getPropertyValue(SOCKET_ID));
	}

	@SuppressWarnings("deprecation")
	public long getFileSize() {
		if (this.protoVersion.compareTo(PROTO_VERSION_66) < 0) {
			final String size1 = this.getPropertyValue(FILESIZE1);
			final String size2 = this.getPropertyValue(FILESIZE2);
			return EncodingUtils.decodeLong(size1, size2);
		} else {
			return (Long) this.getPropertyValueObject(FILESIZE);
		}
	}

	@SuppressWarnings("deprecation")
	public boolean updateFileSize() throws IOException {
		if (this.programInfo == null && this.fileName == null) return false;
		
		// create a backend object using the existing connection
		final Backend backend = new Backend(this.commandConnection);			
		
		Long fileSize = null;
		if(this.programInfo != null) {
			// update the program info
			this.programInfo = backend.fillProgramInfo(this.programInfo);
			if(this.programInfo == null) return false;
			
			// read the new file size
			fileSize = this.programInfo.getFileSize();		
		} else if (this.protoVersion.compareTo(PROTO_VERSION_59)>=0){
			// query file infos
			final IFileStatus fileStatus = backend.queryFileExists(this.fileName,this.storageGroup);
			if(fileStatus == null) return false;
			
			// determine the file size
			fileSize = fileStatus.getFileSize();
		} else if (this.protoVersion.compareTo(PROTO_VERSION_44)>=0){
			// determine infos about the storage group file
			final IStorageGroupFile sgFile =  backend.queryStorageGroupFile(null,this.storageGroup,this.fileName);
			if(sgFile == null) return false;
			
			// determine the file size
			fileSize = sgFile.getFileSize();	
		} else {
			logger.warning("Unable to determine the new file size");
		}
		
		if(fileSize == null) return false;
		
		// updating the file size of the transfer object
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			final String[] longStr = EncodingUtils.encodeLong(fileSize);
			this.setPropertyValue(FILESIZE1, longStr[0]);
			this.setPropertyValue(FILESIZE2, longStr[1]);
		} else {
			this.setPropertyValue(FILESIZE,fileSize.toString());
		}
		
		return true;
	}

	// public int readBlock(byte[] buffer, int requestedSize) throws IOException
	// {
	// // requesting the next block
	// final int reportedLen = this.requestBlock(requestedSize);
	// if (reportedLen == -1) return -1;
	//		
	// // reading the block
	// final int readLength = this.readData(buffer, reportedLen);
	// return readLength;
	// }

	/**
	 * This method sends a block request to the backend, but does not wait for
	 * the response. We do because of the following reason:
	 * <p>
	 * <i>See DSMyth - RemoteFile.cpp:
	 * <hr/>
	 * The response from <code>REQUEST_BLOCK</code> is not sent until all of the
	 * requested data is sent. That means that we have to read from the data
	 * socket while waiting for the reply on the control socket. If we don't
	 * read data from the data socket the backend will timeout with an error.
	 * This means that the network must be fast enough so we can get the data
	 * before the backend times out.
	 * <hr/>
	 * </i>
	 */
	public int readBlock(byte[] buffer, int requestedSize) throws IOException {
		int readLength = 0;

		// send the request to the backend
		this.sendRequestBlockRequest(requestedSize);

		// start reading data until the response message is available
		boolean responseAvailable = this.commandConnection.canReadPacket();
		while (readLength < requestedSize && !responseAvailable) {
			// read the next chunk
			final int readCount = readData(buffer, readLength, requestedSize, false);
			if (readCount > 0) {
				readLength += readCount;
			} else {
				// sleep some time
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					final IOException ioe = new IOException("Interruption while reading data");
//					ioe.initCause(e);
//					throw ioe;
//				}
			}

			// check if the response is available
			responseAvailable = this.commandConnection.canReadPacket();
		}

		// reading response
		final int availableLength = this.readRequestBlockResponse();
		if (availableLength < readLength) {
			throw new IOException("More data read than requested.");
		}

		// read the rest of the data
		while (readLength < availableLength) {
			final int readCount = readData(buffer, readLength, availableLength, true);
			readLength += readCount;
		}

		return readLength;
	}

	public int requestBlock(int requestedLength) throws IOException {
		// send the request to the backend
		this.sendRequestBlockRequest(requestedLength);

		// read command response
		final int availableLength = this.readRequestBlockResponse();
		return availableLength;
	}

	private void sendRequestBlockRequest(int requestedLength) throws IOException {
		// write command message
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.commandConnection.getVersionNr(),
				QUERY_FILETRANSFER, 
				Integer.toString(this.getSocketID())
			),
			QUERY_FILETRANSFER_REQUEST_BLOCK, 
			Integer.toString(requestedLength))
		);
	}

	private int readRequestBlockResponse() throws IOException {
		final IMythPacket resp = this.commandConnection.readPacket();
		final int availableLength = Integer.valueOf(resp.getPacketArg(0));
		return availableLength;
	}

	public int readData(byte[] buffer, int reportedLength) throws IOException {
		return this.readData(buffer, 0, reportedLength, true);
	}

	private int readData(byte[] buffer, int readOffset, int expectedLength, boolean readAllData) throws IOException {
		// read data
		int readLength = readOffset;
		while (readLength < expectedLength) {
			// check if we should continue
//			if (!readAllData && !this.dataConnection.canReadData()) {
//				break;
//			}

			// calculate the next chunk length
			final int chunkSize = expectedLength - readLength;

			// read data and update counter
			final int len = this.dataConnection.readData(buffer, readLength,chunkSize);
			if (len == -1) throw new EOFException("Unable to read the whole data block");
			readLength += len;
		}

		if (readAllData && expectedLength != readLength) {
			throw new IOException(String.format(
				"%d bytes expected but only %d bytes available", 
				Integer.valueOf(expectedLength), 
				Integer.valueOf(readLength)
			));
		}

		return readLength - readOffset;
	}

	@SuppressWarnings("deprecation")
	public long seek(long currentPos, long newPos, IFileTransfer.Seek whence) throws IOException {
		if(whence == null) whence = Seek.ABSOLUTE;

		// write command message
		if (this.protoVersion.compareTo(PROTO_VERSION_66) < 0) {
			final String[] currentPosStr = EncodingUtils.encodeLong(currentPos);
			final String[] newPosStr = EncodingUtils.encodeLong(newPos);

			this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.commandConnection.getVersionNr(),
						QUERY_FILETRANSFER, 
						Integer.toString(this.getSocketID())
					),
					QUERY_FILETRANSFER_SEEK, 
					currentPosStr[0],
					currentPosStr[1], 
					Integer.toString(whence.ordinal()),
					newPosStr[0], 
					newPosStr[1])
				);
		} else {
			this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.commandConnection.getVersionNr(),
						QUERY_FILETRANSFER, 
						Integer.toString(this.getSocketID())
					),
					QUERY_FILETRANSFER_SEEK, 
					Long.toString(currentPos), 
					Integer.toString(whence.ordinal()), 
					Long.toString(newPos)
				));
		}

		// read command response
		final IMythPacket resp = this.commandConnection.readPacket();

		// parsing the response
		if (this.protoVersion.compareTo(PROTO_VERSION_66) < 0) {
			final String size1 = resp.getPacketArg(0);
			final String size2 = resp.getPacketArg(1);
			return EncodingUtils.decodeLong(size1, size2);
		} else {
			return EncodingUtils.decodeString(Long.class, this.protoVersion, resp.getPacketArg(0));
		}
	}

	public boolean isOpen() throws IOException {
		if (this.done) return false;

		// write command message
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.commandConnection.getVersionNr(),
				QUERY_FILETRANSFER, 
				Integer.toString(this.getSocketID())
			),
			QUERY_FILETRANSFER_IS_OPEN
		));

		// read command response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0).equals("1");
	}

	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public boolean setTimeout(boolean fast) throws IOException {
		// write command message
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.commandConnection.getVersionNr(),
				QUERY_FILETRANSFER, 
				Integer.toString(this.getSocketID())
			),
			QUERY_FILETRANSFER_SET_TIMEOUT, 
			fast ? "1" : "0"
		));

		// read command response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}

	public boolean done() throws IOException {
		// write command message
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.commandConnection.getVersionNr(),
				QUERY_FILETRANSFER, 
				Integer.toString(this.getSocketID())
			),
			QUERY_FILETRANSFER_DONE
		));

		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) {
			// remember status
			this.done = true;
		}

		return this.done;
	}

	public boolean reopen(String fileName) throws IOException {
		// write command message
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.commandConnection.getVersionNr(),
				IMythCommand.QUERY_FILETRANSFER, 
				Integer.toString(this.getSocketID())
			),
			IMythCommand.QUERY_FILETRANSFER_REOPEN, 
			fileName
		));

		// read command response
		final IMythPacket resp = this.commandConnection.readPacket();
		return EncodingUtils.decodeString(Boolean.class, this.getVersionNr(),resp.getPacketArg(0)).booleanValue();
	}

	public void close() throws IOException {
		if (!this.done) {
			this.done();
		}

		if (!this.dataConnection.isClosed()) {
			this.dataConnection.close();
		}
	}

	public void transferTo(File target) throws IOException {
		BufferedOutputStream fileOut = null;
		try {
			// create a new ouptut stream
			fileOut = new BufferedOutputStream(new FileOutputStream(target), DEFAULT_BUFFER_SIZE);
			
			// copy data
			this.transferTo(fileOut);
		} finally {
			// close this file transfer
			this.close();

			// close the file output stream
			if (fileOut != null)
				fileOut.close();
		}
	}
	
	public void transferTo(OutputStream fileOut) throws IOException {
		try {
			// check if the remote file was opened properly
			if (!this.isOpen()) {
				throw new FileNotFoundException("Remote file can not be opened");
			}

			// getting the file size
			final long fileSize = this.getFileSize();

			// reading data
			long currentPosition = 0;
			final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			while (currentPosition < fileSize) {
				// reading the next block
				final int chunkSize = (int) Math.min(buffer.length, fileSize - currentPosition);
				final int read = this.readBlock(buffer, chunkSize);
				if (read == -1) {
					// TODO: some logging
					break;
				}

				// writing the block into the file
				fileOut.write(buffer, 0, read);
				fileOut.flush();

				// update counter
				currentPosition += read;
			}
		} finally {
			// close this file transfer
			this.close();
		}
	}

	public InputStream getInputStream() throws IOException {
		return this.getInputStream(DEFAULT_BUFFER_SIZE);
	}

	public InputStream getInputStream(int bufferSize) throws IOException {
		// check if the remote file was openend properly
		if (!this.isOpen()) {
			throw new IOException("Remote file can not be opened");
		}

		return new FileTransferInputStream(bufferSize);
	}

	/**
	 * A stream that automatically uses
	 * {@link FileTransfer#requestBlock(byte[], int)} and
	 * {@link FileTransfer#seek(long, long, Seek)}
	 * 
	 */
	private class FileTransferInputStream extends InputStream {
		private int currentPosition = 0;
		private int maxPosition = 0;
		private long totalReads = 0;
		private long fileSize = FileTransfer.this.getFileSize();
		private byte[] buffer;

		private FileTransferInputStream() {
			this(DEFAULT_BUFFER_SIZE);
		}

		private FileTransferInputStream(int bufferSize) {
			buffer = new byte[bufferSize];
		}

		private int fill() throws IOException {
			final long blockSize = Math.min(fileSize-totalReads,(long)buffer.length);
			final int readSize = FileTransfer.this.readBlock(buffer,(int)blockSize);
			if (readSize == -1) {
				throw new EOFException();
			} 

			this.maxPosition = readSize;
			this.currentPosition = 0;
			return readSize;
		}

		@Override
		public int read() throws IOException {
			if (fileSize - totalReads == 0) {
				return -1;
			} else if (maxPosition - currentPosition <= 0) {
				// refill buffer
				int readAmount = 0;
				do {
					readAmount = this.fill();
				} while (readAmount == 0);				
			}

			final int readData = buffer[currentPosition];
			currentPosition++;
			totalReads++;
			return readData & 0xFF;

		}

		@Override
		public void close() throws IOException {
			FileTransfer.this.close();
		}
	}
}
