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
package org.jmythapi.protocol;

import java.util.Date;
import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.utils.PacketUtils;

/**
 * This interface represents a protocol packet that can be send over network.
 * 
 * <h3>Package Structure:</h3>
 * In general a protocol packet just consists of a list of arguments. When this
 * arguments should be send over network, the argument values need to be separated
 * by the delimeter {@link IMythPacket#DELIM} and the first 8 bytes of the resulting
 * byte stream must contain the size of the payload. 
 * <br>
 * Take a look into the MythTV <a href="http://www.mythtv.org/wiki/Category:Myth_Protocol">Wiki</a> 
 * for more details.
 * <p>
 * 
 * The following example shows how a MythTV-packet looks like.
 * 
 * {@mythProtoExample notitle
 * A request packet
 * 24      QUERY_FREE_SPACE_SUMMARY
 * A response packet.
 * 32      0[]:[]18761596[]:[]0[]:[]2985552
 * }
 * 
 * <h3>Usage example:</h3>
 * 
 * The following is done internally by the a backend-connection when sending a request to
 * the backend and receiving a response packet:
 * 
 * {@mythCodeExample <pre>
 *    // convert a sendable object to a packet
 *    IMythPacket req = sendableObj.getPacket();
 *    
 *    // convert the packet into bytes and send it via the output stream
 *    PacketUtils.writeTo(req, socketOutputStream);
 * 
 *    // read the response packet from input stream
 *    IMythPacket resp = PacketUtils.readFrom(protoVersion, socketInputStream);
 * </pre>}
 * 
 * As shown in the above example the {@link PacketUtils} class provides convenient methods to read and write a packet.
 * 
 * @see PacketUtils#writeTo(IMythPacket, java.io.OutputStream) PacketUtils.writeTo
 * @see PacketUtils#readFrom(ProtocolVersion, String) PacketUtils.readFrom
 */
public interface IMythPacket extends IVersionable {
	/**
	 * The delimeter used to separate the packet arguments when
	 * sending them over network.
	 */
	public static final String DELIM = "[]:[]";
	
	/**
	 * Gets the packet arguments.
	 * 
	 * @return
	 * 		the packet arguments as list.
	 */
	public List<String> getPacketArgs();
	
	/**
	 * Gets the amount of the packet arguments.
	 * @return
	 * 		the amount of arguments.
	 */
	public int getPacketArgsLength();
	
	/**
	 * Gets the packet argument value at the given position.
	 * @param idx
	 * 		the position of the desired argument
	 * @return
	 * 		the argument value.
	 */
	public String getPacketArg(int idx);
	
	/**
	 * Gets the time when this packet was created.
	 * @return
	 * 		the packet creation time
	 */
	public Date getPacketCreationTime();
}
