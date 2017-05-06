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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_20;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_RECORDER;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_RECORDER_DONE_RINGBUF;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_RECORDER_GET_FREE_SPACE;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_RECORDER_REQUEST_BLOCK_RINGBUF;
import static org.jmythapi.protocol.request.IMythCommand.QUERY_RECORDER_SEEK_RINGBUF;
import static org.jmythapi.protocol.response.IRingBuffer.Props.AMOUNT1;
import static org.jmythapi.protocol.response.IRingBuffer.Props.AMOUNT2;
import static org.jmythapi.protocol.response.IRingBuffer.Props.SIZE1;
import static org.jmythapi.protocol.response.IRingBuffer.Props.SIZE2;
import static org.jmythapi.protocol.response.IRingBuffer.Props.URL;
import static org.jmythapi.protocol.utils.ResponseUtils.STATUS_OK;

import java.io.Closeable;
import java.io.IOException;

import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.AMythRequest;
import org.jmythapi.protocol.response.IRingBuffer;
import org.jmythapi.protocol.response.ITransferable;
import org.jmythapi.protocol.utils.ResponseUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * 
 * @deprecated {@mythProtoVersion 20}
 */
@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
public class RingBuffer extends AMythResponse<IRingBuffer.Props> implements IRingBuffer, Closeable {
	
	private IBackendConnection dataConnection;
	private final IBackendConnection commandConnection;
	private boolean done = false;
	
	/**
	 * The ID of this recorder
	 */
	private final int recorderId;
	
	public RingBuffer(IBackendConnection commandConnection, int recorderId, IMythPacket packet) {
		super(IRingBuffer.Props.class, packet);
		this.commandConnection = commandConnection;
		this.recorderId = recorderId;
	}
	
	public void setDataConnectin(IBackendConnection dataConnection) {
		this.dataConnection = dataConnection;
	}
	
	public String getUrl() {
		return this.getPropertyValue(URL);
	}	
	
	public long getSize() {
		return EncodingUtils.decodeLong(this.getPropertyValue(SIZE1), this.getPropertyValue(SIZE2));
	}
	
	public long getAmount() {
		return EncodingUtils.decodeLong(this.getPropertyValue(AMOUNT1), this.getPropertyValue(AMOUNT2));
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("FileTransfer objects can not be cloned");
	}

	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
	public boolean done() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			QUERY_RECORDER_DONE_RINGBUF
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) {
			// remember status
			this.done = true;
		}
		return this.done;
	}


	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
	public long seek(long currentPosition, long newPosition, ITransferable.Seek whence) throws IOException {
		if(whence == null) whence = Seek.ABSOLUTE;
		final String[] posParts = EncodingUtils.encodeLong(newPosition);
		final String[] currPosParts = EncodingUtils.encodeLong(currentPosition);
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			QUERY_RECORDER_SEEK_RINGBUF,
			posParts[0],
			posParts[1],
			Long.toString(whence.ordinal()),
			currPosParts[0],
			currPosParts[1]
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return EncodingUtils.decodeLong(resp.getPacketArg(0), resp.getPacketArg(1));
	}
	
	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
	public int readBlock(byte[] buffer, int requestedSize) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			QUERY_RECORDER_REQUEST_BLOCK_RINGBUF,
			Long.toString(requestedSize)
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		final int reportedLen = Integer.valueOf(resp.getPacketArg(0));
		if (reportedLen == -1) return -1;
		
		// read data
		int readLen = 0;
		while (readLen < reportedLen) {
			int len = this.dataConnection.readData(buffer, readLen, reportedLen - readLen);
			if (len == -1) break;
			readLen += len;
		}
		// TODO: readLen may be different to reportedLen
		
		return reportedLen;		
	}	
	
	
	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20)
	public long getFreeSpace(long totalReadPos) throws IOException {
		if(totalReadPos < 0) totalReadPos = 0;
		final String[] longLong = EncodingUtils.encodeLong(totalReadPos);
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				QUERY_RECORDER_GET_FREE_SPACE,
				Integer.toString(this.recorderId)
			),
			longLong[0],
			longLong[1]
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return EncodingUtils.decodeLong(resp.getPacketArg(0), resp.getPacketArg(1));		
	}	
	
	public boolean isOpen() throws IOException {
		if (this.done) return false;
		else if(this.dataConnection == null) return false;
		return !this.dataConnection.isClosed();
	}
	
	public void close() throws IOException {
		if (!this.done) {
			this.done();
		}

		if (!this.dataConnection.isClosed()) {
			this.dataConnection.close();
		}
	}
}
