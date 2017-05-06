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
package org.jmythapi.protocol.utils;

import static org.jmythapi.protocol.request.IMythCommand.BACKEND_MESSAGE;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.UnknownCommandException;
import org.jmythapi.protocol.annotation.MythProtocolCmd;
import org.jmythapi.protocol.events.IMythEvent;
import org.jmythapi.protocol.request.IMythCommand;

public class EventUtils {
	/**
	 * For logging
	 */
	private static final Logger logger = Logger.getLogger(ResponseUtils.class.getName());	
	
	private static final <Event extends IMythEvent<?>> Method getValueOf(
		Class<Event> respClass
	) {
		try {
			final Method valueOf = respClass.getMethod("valueOf", new Class[]{IMythPacket.class});
			final Class<?> returnType = valueOf.getReturnType();
			if(returnType.equals(respClass)) return valueOf;
			else if(IMythEvent.class.isAssignableFrom(returnType)) return valueOf;
		} catch(NoSuchMethodException nsme) {
			// this is ok here
		}
		return null;
	}	
	
	public static final <Event extends IMythEvent<?>> Event readFrom(
		IMythPacket packet
	) throws IllegalArgumentException, UnknownCommandException {
		final List<String> packetArgs = packet.getPacketArgs();
		if(packetArgs.size() < 2) {
			throw new IllegalArgumentException("To few arguments");
		} else if(packetArgs.get(0) == null || !packetArgs.get(0).equals(IMythCommand.BACKEND_MESSAGE)) {
			throw new IllegalArgumentException("Packet is not a backend message.");
		}
		
		// getting the event name
		final String eventCmd = packetArgs.get(1);
		final String[] cmdParts = eventCmd.split(CommandUtils.DELIM);
		
		// determine the event class to use
		final Class<Event> eventClass = getResponseClass(cmdParts[0]);
		if(eventClass == null || eventClass.equals(Object.class)) return null;
		
		// read event
		return (Event) readFrom(eventClass,packet);
	}
	
	public static final <Event extends IMythEvent<?>> Event readFrom(
		Class<Event> respClass, IMythPacket packet
	) throws IllegalArgumentException {
		// the constructor of the msg 
		try {
			// first we test if there is an valueOf method
			final Method valueOf = getValueOf(respClass);
			if(valueOf != null) {
				@SuppressWarnings("unchecked")
				final Event msgObject = (Event) valueOf.invoke(null,packet);
				return msgObject;
			}

			// otherwise we try to use the proper constructor
			final Constructor<Event> msgClassConstructor = respClass.getConstructor(new Class[] { 
				IMythPacket.class
			});
	
			// creating a new message instance
			final Event msgObject = msgClassConstructor.newInstance(new Object[] {packet});        
			return msgObject;
		} catch (Exception e) {
			logger.log(Level.SEVERE,String.format(
				"Unable to create a event object '%s' from packet:\r\n%s",
				respClass,packet
			),e);
			return null; // TODO: should we throw an exception here?
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static <Event extends IMythEvent<?>> Class<Event> getResponseClass(
		String commandName
	) throws UnknownCommandException {
		final MythProtocolCmd cmdDeclaration = CommandUtils.getCommandDeclaration(BACKEND_MESSAGE,commandName);
		if(cmdDeclaration == null) return null;
		
		return (Class<Event>) cmdDeclaration.responseClass();
	}
}
