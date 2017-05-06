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

import static org.jmythapi.protocol.events.IClientErrorEvent.Props.EXCEPTION_MESSAGE;
import static org.jmythapi.protocol.events.IClientErrorEvent.Props.EXCEPTION_NAME;

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.events.IClientErrorEvent;
import org.jmythapi.protocol.impl.ClientErrorPacket;

public class ClientErrorEvent extends AMythEvent<IClientErrorEvent.Props> implements IClientErrorEvent {

	private Exception exception;
	
	public ClientErrorEvent(ProtocolVersion protoVersion, Exception exception, List<String> args) {
		super(protoVersion,IClientErrorEvent.Props.class,"CLIENT_MESSAGE", args);
	}
	
	public Exception getException() {
		return this.exception;
	}

	public String getExceptionMessage() {
		return this.getPropertyValueObject(EXCEPTION_NAME);
	}

	public String getExceptionName() {
		return this.getPropertyValueObject(EXCEPTION_MESSAGE);
	}
	
	public static ClientErrorEvent valueOf(IMythPacket packet) {
		if(!(packet instanceof ClientErrorPacket)) return null;
		
		final List<String> packetArgs = packet.getPacketArgs();
		final Exception exeption = ((ClientErrorPacket)packet).getException();
		return new ClientErrorEvent(packet.getVersionNr(),exeption,packetArgs.subList(1,packetArgs.size()));
	}
}
