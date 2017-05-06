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
package org.jmythapi.protocol.response;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_70;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.impl.FileTransfer;

/**
 * An interface to transfer files from and to a backend.
 * <p>
 * This interface represents the response to a {@link IBackend#annotateFileTransfer IBackend.annotateFileTransfer} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IFileTransfer.Props properties-list} for all properties of this interface.
 * 
 * <h4>Request example:</h4>
 * 
 * {@mythCodeExample <pre>
 *    // getting previously recorded programs
 *    IProgramInfoList programs = backend.queryRecordings();
 *    
 *    // getting the recording to transfer (we just use the first recording)
 *    IProgramInfo program = programs.get(0);
 *    		
 *    // annotate a new file transfer
 *    IFileTransfer transfer = backend.annotateFileTransfer(program);	
 *    if (transfer.isOpen()) &#123;
 *       // set fast timeout
 *       transfer.setTimeout(true);
 *       
 *       // copy data
 *       File tempFile = File.createTempFile("fileTransfer", ".mpg");
 *       transfer.transferTo(tempFile);
 *    &#125;
 *    		
 *    // close file transfer
 *    transfer.close();
 * </pre>}
 * 
 * <h3>Response example:</h3>
 * {@mythResponseExample 
 * 		Protocol version less than 66:
 * 		<pre><0>STATUS: OK | <1>SOCKET_ID: 38 | <2>FILESIZE1: 0 | <3>FILESIZE2: 86681</pre>
 * 		Protocol version 66 example:
 * 		<pre><0>STATUS: OK | <1>SOCKET_ID: 38 | <2>FILESIZE: 86681</pre>
 * }
 * 
 * 
 * @see IBackend#annotateFileTransfer(IProgramInfo)
 * @see IMythCommand#ANN_FILE_TRANSFER ANN_FILE_TRANSFER
 */
public interface IFileTransfer extends ITransferable<IFileTransfer.Props> {

	/**
	 * The properties of an {@link IFileTransfer} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * Status Code.
		 * <p>
		 * {@code OK} or {@code ERROR}
		 * @see FileTransfer#getStatus()
		 */
		STATUS,
		
		/**
		 * Socket ID.
		 * 
		 * @see FileTransfer#getSocketID()
		 */
		@MythParameterType(Integer.class)
		SOCKET_ID,
		
		/**
		 * File size.
		 * 
		 * @see FileTransfer#getFileSize()
		 * 
		 * @deprecated {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_66)
		FILESIZE1,
		
		/**
		 * File size.
		 * 
		 * @see FileTransfer#getFileSize()
		 * 
		 * @deprecated {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_66)
		FILESIZE2,
		
		/**
		 * File size.
		 * 
		 * @see FileTransfer#getFileSize()
		 * 
		 * @since {@mythProtoVersion 66}
		 */		
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_66)
		FILESIZE
	}

	/**
	 * Gets the file transfer status.
	 * 
	 * @return
	 * 		{@code OK} or {@code ERROR}
	 */
	public abstract String getStatus();

	/**
	 * Gets the file transfer socket id.
	 * @return
	 * 		the socket id
	 */
	public abstract int getSocketID();

	/**
	 * Gets the current file size.
	 * <p>
	 * For a in progress recording this file size may increase.<br>
	 * Use {@link #updateFileSize()} to update the file size.
	 * 
	 * @return
	 * 		the current file size
	 */
	public abstract long getFileSize();
	
	/**
	 * Updates the file information of the file-transfer object.
	 * <p>
	 * If the transfer object was initiated from an {@link IProgramInfo} object 
	 * (see {@link IBackend#annotateFileTransfer(IProgramInfo)}), then this function 
	 * uses {@link IMythCommand#FILL_PROGRAM_INFO} to determine the new file size.
	 * <p>
	 * If the transfer object was initialized using a file name, and the current protocol version is
	 * greater or equal {@mythProtoVersion 44}, then {@link IMythCommand#QUERY_SG_FILEQUERY} is used 
	 * to determine the new file size. But this method may fail if the requested file is not stored
	 * in a file group, e.g. a channel icon.
	 *  
	 * @return 
	 * 		{@code true} on success.
	 * @throws IOException
	 * 		on communication errors
	 */
	public boolean updateFileSize() throws IOException;

	/**
	 * Requests a new block from backend and reads the block.
	 * <p>
	 * This method combines {@link #requestBlock(int)} and {@link #readData(byte[], int)}.
	 * 
	 * @param buffer
	 * 		the byte buffer to fill with data
	 * @param requestedSize
	 * 		the amount of bytes to read
	 * @return
	 * 		the read byte count
	 * @throws IOException
	 * 		on communication errors
	 */
	public int readBlock(byte[] buffer, int requestedSize) throws IOException;
	
	/**
	 * Requests data from the backend.
	 * 
	 * @param size
	 * 		the amount of bytes to request. 
	 * @return 
	 * 		the amount of returned bytes. this may be less than the requested size.
	 * 
	 * @throws IOException
	 * 		on communication errors
	 */
	public abstract int requestBlock(int size) throws IOException;

	/**
	 * Reads the given amount of data from the backend.
	 * 
	 * @param buffer
	 * 		the buffer to fill with data
	 * @param reportedLength
	 * 		the amount of data to read
	 * @return
	 * 		the actually amount of read data
	 * @throws IOException
	 * 		on communication errors
	 */
	public int readData(byte[] buffer, int reportedLength) throws IOException;
	
	/**
	 * <p>This command wraps the file "seek" function.</p>
	 * The pos and curpos fields are combined into a larger "long long" data type. 
	 * This returns two values, a split "long long" value for the result. 
	 * 
	 * @param currentPos
	 * 		the current position
	 * @param newPos
	 * 		the new position
	 * @param whence
	 * 		the seek mode
	 * 
	 * @return 
	 * 		the new position
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILETRANSFER_SEEK QUERY_FILETRANSFER_SEEK
	 */
	public abstract long seek(long currentPos, long newPos, ITransferable.Seek whence) throws IOException;

	/**
	 * Queries whether a file socket is currently open.
	 * 
	 * @return
	 * 		{@code true} if the socket is open.
	 * @throws IOException
	 * 		on communication errors
	 */
	public abstract boolean isOpen() throws IOException;

	/**
	 * Sets the timout mode.
	 * <p>
	 * This command sets whether reading from a file should have a fast or slow timeout.<br>
	 * Slow timeouts are used for live TV ring buffers, and is three seconds. Fast timeouts are 
	 * used for static files, and are 0.12 seconds. 
	 * 
	 * @since {@mythProtoVersion 28}
	 * 
	 * @see IMythCommand#QUERY_FILETRANSFER_SET_TIMEOUT QUERY_FILETRANSFER_SET_TIMEOUT
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
	public abstract boolean setTimeout(boolean fast) throws IOException;

	/**
	 * Closes the file transfer connection.
	 * 
	 * @return
	 * 		{@code true} on success.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILETRANSFER_DONE QUERY_FILETRANSFER_DONE
	 * 
	 */
	public abstract boolean done() throws IOException;
	
	/**
	 * Reopens the remote file.
	 * 
	 * @since {@mythProtoVersion 70}
	 * @see IMythCommand#QUERY_FILETRANSFER_REOPEN QUERY_FILETRANSFER_REOPEN
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_70)
	public abstract boolean reopen(String filename) throws IOException;

	/**
	 * Copies the remote file into the local file.
	 * <p>
	 * This function transfers the full remote file into the specified target file.
	 * 
	 * @param target
	 * 		the file to write data to.
	 * @throws IOException 
	 * 		on communication errors
	 */
	public abstract void transferTo(File target) throws IOException;
	
	/**
	 * Copies the remote file into the given output stream.
	 * <p>
	 * This function transfers the full remote file into the specified target stream.
	 * 
	 * @param target
	 * 		the output stream
	 * @throws IOException 
	 * 		on communication errors
	 */
	public abstract void transferTo(OutputStream target) throws IOException;	
	
	/**
	 * Gets an input stream to read the file data.
	 * <p>
	 * This function creates an input-stream that can be used to read the data of this file-transfer object.
	 * 
	 * @return
	 * 		an input stream to read the file
	 * @throws IOException
	 * 		on communication errors
	 */
	public InputStream getInputStream() throws IOException;
	
	/**
	 * Gets an input stream to read the file data.
	 * <p>
	 * This function creates an input-stream that can be used to read the data of this file-transfer object.<br>
	 * 	 * The maximum size of the data chunks that should be fetched by the stream can be spezified.
	 * 
	 * @param chunkSize
	 * 		the maximum size of the data chunks to fetch
	 * @return
	 * 		an input stream to read data
	 * @throws IOException
	 * 		on communication errors
	 */
	public InputStream getInputStream(int chunkSize) throws IOException;
}