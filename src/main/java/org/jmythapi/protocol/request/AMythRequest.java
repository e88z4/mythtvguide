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
package org.jmythapi.protocol.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.impl.MythPacket;
import org.jmythapi.protocol.utils.CommandUtils;
import org.jmythapi.protocol.utils.PacketUtils;

/**
 * The implementation of a MythTV-protocol request.
 */
public class AMythRequest implements IMythRequest {
	private final ProtocolVersion protoVersion;
	private final IMythCommand cmd;
	private final List<String> reqArgs;
	
	public AMythRequest(IMythCommand command) {
		this(command,new ArrayList<String>(0));
	}
	
	public AMythRequest(IMythCommand command, String... requestArgs) {
		this(command,requestArgs==null?null:Arrays.asList(requestArgs));
	}
	
	public AMythRequest(IMythCommand command, List<String> requestArgs) {
		if (command == null) throw new NullPointerException("The command must not be null");
		if (requestArgs == null) requestArgs = new ArrayList<String>(0);
		
		this.protoVersion = command.getVersionNr();
		this.cmd = command;
		this.reqArgs = requestArgs;
	}
	
	public IMythCommand getCommand() {
		return this.cmd;
	}
	
	public List<String> getRequestArguments() {
		return this.reqArgs;
	}
	
	public static final IMythRequest readFrom(ProtocolVersion protoVersion, String input) {		
		try {
			InputStream in = new ByteArrayInputStream(input.getBytes("UTF-8"));			
			return readFrom(protoVersion, in);
		} catch (UnsupportedEncodingException e) {
			assert(false) : "this should never occure";
			return null;
		} catch (IOException e) {
			assert(false) : "this should never occure";
			return null;
		}		
	}
	
	public static final IMythRequest readFrom(ProtocolVersion protoVersion, InputStream input) throws IOException {
		// read package
		final IMythPacket packet = PacketUtils.readFrom(protoVersion, input);
		
		// return request object
		return readFrom(packet);
	}
	
	public static final IMythRequest readFrom(IMythPacket packet) throws IllegalArgumentException {
		final List<String> packetArgs = packet.getPacketArgs();
		
		// parse the command
		final IMythCommand cmd = CommandUtils.readFrom(packet.getVersionNr(),packetArgs.get(0));
		
		// get request arguments
		List<String> reqArgs = null;
		if (packetArgs.size() > 1) {
			reqArgs = packetArgs.subList(1, packetArgs.size());
		} else {
			reqArgs = new ArrayList<String>();
		}
		
		// return result
		return new AMythRequest(cmd, reqArgs);
	}

	public IMythPacket getPacket() {
		final ArrayList<String> packetArgs = new ArrayList<String>();
		
		// add command
		packetArgs.add(this.cmd.toString());
		
		// add request args (if any)
		if (this.reqArgs != null) {
			packetArgs.addAll(this.reqArgs);
		}
		
		// return result
		return new MythPacket(this.protoVersion, packetArgs);
	}
	
	public String toString() {
		try {
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			final IMythPacket packet = this.getPacket();
			PacketUtils.writeTo(packet, bout);
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
