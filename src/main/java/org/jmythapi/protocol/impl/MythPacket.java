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
package org.jmythapi.protocol.impl;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.utils.PacketUtils;

public class MythPacket implements IMythPacket {

	/**
	 * The packet arguments
	 */
	private final List<String> args;
	
	/**
	 * The MythTV-protocol version of this packet.
	 * @see IVersionable#getVersionNr()
	 */
	private ProtocolVersion protoVersion = ProtocolVersion.PROTO_VERSION_LATEST;
	
	private Date packetCreationTime = null;
	
	/**
	 * Creates a {@link IMythPacket} from the given string.
	 * 
	 * @param protoVersion the MythTV-protocol version this packet belongs to
	 * @param packetString the packet data as String, e.g. 
	 * 	    <code>QUERY_MEMSTATS</code> or 
	 * 		<code>1002[]:[]224[]:[]853[]:[]853</code>
	 */
	public MythPacket(ProtocolVersion protoVersion,String packetString) {
		this(protoVersion, PacketUtils.split(packetString));
	}
	
	/**
	 * Creates a {@link IMythPacket} from the given argument array.
	 * 
	 * @param protoVersion the MythTV-protocol version this packet belongs to
	 * @param packetArguments the packet data as Array, e.g. 
	 * 		<code>[QUERY_MEMSTATS]</code> or
	 * 		<code>[1002,224,853,853]</code>
	 */
	public MythPacket(ProtocolVersion protoVersion,String[] packetArguments) {
		this(protoVersion, Arrays.asList(packetArguments));
	}
	
	/**
	 * Creates a {@link IMythPacket} from the given argument list.
	 * 
	 * @param protoVersion the MythTV-protocol version this packet belongs to
	 * @param packetArguments the packet data as list, e.g. 
	 * 		<code>[QUERY_MEMSTATS]</code> or
	 * 		<code>[1002,224,853,853]</code>
	 */
	public MythPacket(ProtocolVersion protoVersion, List<String> packetArguments) {
		if (protoVersion == null) throw new NullPointerException("No protocol specified");
		if (packetArguments == null) throw new NullPointerException("The packet arguments must not be null");
		if (packetArguments.size() == 0) throw new NullPointerException("No packet arguments set");
		
		this.protoVersion = protoVersion;
		this.args = packetArguments;
		this.packetCreationTime = new Date();
	}
	
	public List<String> getPacketArgs() {
		return this.args;
	}	
	
	public int getPacketArgsLength() {
		if(this.args == null) return 0;
		return this.args.size();
	}
	
	public String getPacketArg(int idx) {
		if (this.args == null) return null;
		return this.args.get(idx);
	}
	
	public Date getPacketCreationTime() {
		return this.packetCreationTime;
	}
	
	public String toString() {
		try {
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PacketUtils.writeTo(this,bout);
			return bout.toString("UTF-8");
		} catch (Exception e) {
			assert(false) : "this should never occure";
			return null;
		}
	}

	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}
}
