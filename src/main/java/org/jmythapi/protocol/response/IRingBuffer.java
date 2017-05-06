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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_20;

import java.io.IOException;

import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to control a MythTV ringbuffer.
 * <p>
 * This interface represents the result of a {@link IRecorder#annotateRingBuffer} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRingBuffer.Props properties-list} for all properties of this interface.
 * 
 * @deprecated {@mythProtoVersion 20}
 */
@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
public interface IRingBuffer extends ITransferable<IRingBuffer.Props> {
	/**
	 * The properties of an {@link IRingBuffer} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
	public static enum Props {
		/**
		 * The ringbuffer file.
		 * <p>
		 * See MythtV setting {@code LiveBufferDir} for the prefix:
		 * 
		 * @see IRingBuffer#getUrl()
		 */
		URL,
		
		/**
		 * Thr ringbuffer size - part1.
		 * <p>
		 * @see IRingBuffer#getSize()
		 */
		SIZE1,
		
		/**
		 * The ringbuffer size - part2.
		 * <p>
		 * See MythTV setting {@code BufferSize}.
		 * 
		 * @see IRingBuffer#getSize()
		 */
		@MythParameterType(Long.class)
		SIZE2,
		
		/**
		 * The fillamount - part1.
		 * <p>
		 * See MythTV setting {@code MaxBufferFill}.
		 * 
		 * @see IRingBuffer#getAmount()
		 */
		@MythParameterType(Long.class)
		AMOUNT1,
		
		/**
		 * The fillamount - part2.
		 * <p>
		 * See MythTV setting {@code MaxBufferFill}.
		 * @see IRingBuffer#getAmount()
		 */
		@MythParameterType(Long.class)
		AMOUNT2
	}

	public abstract String getUrl();

	public abstract long getSize();

	public abstract long getAmount();

	/**
	 * Seeks to the given position in the ring buffer.
	 * @param currentPos
	 * 		the current position
	 * @param whence
	 * 		the seek mode
	 * @param newPos
	 * 		the new position
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SEEK_RINGBUF QUERY_RECORDER_SEEK_RINGBUF
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_20)
	public abstract long seek(long currentPos, long newPos, ITransferable.Seek whence) throws IOException;

	/**
	 * Requests a block from the ring-buffer.
	 * <p>
	 * 
	 * @param buffer 
	 * 		the byte array that should be filled with data
	 * @param requestedSize
	 * 		the amount of bytes to read.
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_REQUEST_BLOCK_RINGBUF QUERY_RECORDER_REQUEST_BLOCK_RINGBUF
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_20)
	public abstract int readBlock(byte[] buffer, int requestedSize) throws IOException;	
		
	/**
	 * Queries bytes available to read.
	 * <p>
	 * This function returns the number of bytes beyond "totalreadpos" it is safe to read.

	 * @param totalReadPos
	 * 		amount of already read bytes
	 * @return
	 * 		the number of bytes beyond "totalReadPos" it is save to read or {@code -1} if "totalReadPos" is past the "safe read" position
	 * 		of the file.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_FREE_SPACE QUERY_RECORDER_GET_FREE_SPACE
	 * 
	 * @mythProtoVersionRange
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20)
	public long getFreeSpace(long totalReadPos) throws IOException;	
	
	/**
	 * Tells the recorder to free the ring buffer.
	 * 
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_20)
	public abstract boolean done() throws IOException;
	
	/**
	 * Closes the ringbuffer connection.
	 * @throws IOException
	 * 		on communication errors
	 */
	public void close() throws IOException;
	
	/**
	 * Queries whether the ring buffer connection is open.
	 * 
	 * @return
	 * 		{@code true} if the socket is open.
	 * @throws IOException
	 * 		on communication errors
	 */
	public abstract boolean isOpen() throws IOException;
}