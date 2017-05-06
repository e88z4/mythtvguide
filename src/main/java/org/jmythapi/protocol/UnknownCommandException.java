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

import org.jmythapi.protocol.utils.CommandUtils;

/**
 * This exception is thrown by the backend-connection, if an request with a command should be send,
 * that is not supported by any of the supported backend protocol-versions.
 * <p>
 * The function {@link CommandUtils#isKnownCommand} can be used to check if a given command is 
 * supported by any protocol version.
 * 
 * @see CommandUtils#isKnownCommand(String)
 */
public class UnknownCommandException extends ProtocolException {
	private static final long serialVersionUID = 1L;

	public UnknownCommandException() {
		super();
	}
	
	public UnknownCommandException(String message) {
		super(message);
	}
}
