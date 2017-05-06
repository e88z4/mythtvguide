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
 * This exception is thrown if a command is used outside of its valid protocol-version range.
 * <p>
 * If a given command is supported by at least known protocol version, but is going to 
 * be sent to a backend not supporting the command, the backend connection will throw 
 * this exception.
 * <p>
 * 
 * The following utility method can be used to determine the valid protocol-version range for
 * a given command:
 * <br>
 * {@mythCodeExample <pre>
 *    // the following will return the version range [9,-1)
 *    ProtocolVersionRange versionRange = CommandUtils.getCommandVersionRange(IMythCommand.GET_FREE_RECORDER_COUNT);
 *    System.out.println(versionRange);
 * </pre>}
 * <br>
 * 
 * In the above example the command {@code GET_FREE_RECORDER_COUNT} was introduced in protocol version {@code 09}
 * and is supported up to the current protocol version.<br>If the command is going to be send to a backend with a version
 * lower than {@code 09}, then this exception will be thrown.
 * 
 * @see ProtocolVersionRange
 * @see CommandUtils#getCommandVersionRange(String)
 */
public class UnsupportedCommandException extends ProtocolException {
	private static final long serialVersionUID = 1L;

	public UnsupportedCommandException() {
		super();
	}
	
	public UnsupportedCommandException(String message) {
		super(message);
	}
}
