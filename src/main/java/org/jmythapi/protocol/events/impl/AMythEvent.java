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
package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_75;

import java.util.ArrayList;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.events.IMythEvent;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.impl.AMythResponse;
import org.jmythapi.protocol.utils.CommandUtils;
import org.jmythapi.utils.EncodingUtils;

public abstract class AMythEvent <E extends Enum<E>> extends AMythResponse<E> implements IMythEvent<E> {

	private String eventName;
	
	public AMythEvent(Class<E> propsClass, IMythPacket packet) {
		super(packet.getVersionNr(), propsClass, extractArgumentsList(packet));
		this.eventName = extractEventName(packet);
	}
	
	public AMythEvent(ProtocolVersion protoVersion, Class<E> propsClass, String eventName, List<String> eventArguments) {
		super(protoVersion,propsClass,eventArguments);
		this.eventName = eventName;
	}
	
	public static List<String> extractArgumentsList(IMythPacket packet) {
		final ProtocolVersion protoVersion = packet.getVersionNr();
		
		final List<String> packetArgs = packet.getPacketArgs();
		if(packetArgs.size() < 2) {
			throw new IllegalArgumentException("To few arguments");
		} else if(packetArgs.get(0) == null || !packetArgs.get(0).equals(IMythCommand.BACKEND_MESSAGE)) {
			throw new IllegalArgumentException("Packet is not a backend message.");
		}
		
		final String commandString = packetArgs.get(1);
		final IMythCommand command = CommandUtils.readFrom(protoVersion, commandString);

		// add the command arguments
		final List<String> propValues = new ArrayList<String>();
		propValues.addAll(command.getCommandArguments());
		
		// add additional parameters
		if(packetArgs.size() > 2) {
			final List<String> extraInfo = packetArgs.subList(2, packetArgs.size());
			if("empty".equals(extraInfo.get(extraInfo.size()-1))) {
				extraInfo.remove(extraInfo.size()-1);
			}
			propValues.addAll(extraInfo);
		}
		
		return propValues;
	}
	
	public String extractEventName(IMythPacket packet) {
		return packet.getPacketArg(1).split(CommandUtils.DELIM)[0];
	}
	
	public String getEventName() {
		return this.eventName;
	}

	/* ====================================================================
	 * ENCODING/DECODING methods
	 * ==================================================================== */
	
	@Override
	protected <T> T decodeProperty(Class<T> dataType, String dataValue) {
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		return EncodingUtils.decodeString(
			dataType,
			this.protoVersion,
			-1,
			isUTC,
			dataValue
		);		
	}
	
	@Override
	protected <S, T> String encodeProperty(java.lang.Class<S> propObjectType, S propValue, java.lang.Class<T> propStringType, String propDefaultValue) {
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		return EncodingUtils.encodeObject(
			propObjectType, 
			this.protoVersion,
			-1,
			isUTC,
			propValue,
			propStringType,
			propDefaultValue
		);			
	};	
	
	/* ====================================================================
	 * ISendable Method
	 * ==================================================================== */	
	
	public IMythPacket getPacket() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		buff.append(this.getEventName());
		
		if(this.getPropertyCount() > 0) {
			buff.append(": ").append(super.toString());
		}
		
		return buff.toString();
	}
}
