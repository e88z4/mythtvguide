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

import java.util.ArrayList;
import java.util.List;

import org.jmythapi.protocol.ProtocolVersion;

public class ClientErrorPacket extends MythPacket {
	private Exception exception = null;
	
	public ClientErrorPacket(ProtocolVersion protoVersion,Exception exception) {
		super(protoVersion, extractClientMessageArguments(exception));
		this.exception = exception;
	}

	public Exception getException() {
		return this.exception;
	}
	
	private static List<String> extractClientMessageArguments(Exception e) {
		final List<String> msgArgs = new ArrayList<String>();
		msgArgs.add("CLIENT_MESSAGE");
		msgArgs.add(e.getClass().getName());
		msgArgs.add(e.getMessage()==null?"":e.getMessage());
		
		Throwable clause = e;
		while((clause = clause.getCause())!=null) {
			msgArgs.add(clause.getClass().getName());
			msgArgs.add(clause.getMessage()==null?"":clause.getMessage());
		} 
		
		return msgArgs;
	}	
}
