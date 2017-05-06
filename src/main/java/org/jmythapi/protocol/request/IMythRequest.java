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

import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ISendable;

/**
 * This interface represents a MythTV-protocol request.
 * <p>
 * A MythTV-protocol request consists of a {@link IMythCommand command} with a name and optionally a list of command arguments, separated by space.<br>
 * Additionally request-parameters may be required, which are then separated by {@code []:[]}.
 * <p>
 * When sending a request-messages to the backend, it is transfered in the form of an {@link IMythPacket packet}.
 * 
 * <h3>Request Example:</h3>
 * 
 * The following MythTV-request consists of a command with the name {@code QUERY_REMOTEENCODER} and an additional command parameter {@code 1}, 
 * which is the id of a remote encoder. The command name and additional command parameters are separated by space. Additionally
 * the request contains a extra request parameter {@code IS_BUSY}, which in this case is a command to be processed by the remote encoder.
 * The command and additional request parameters are by {@code []:[]}.<br>
 * The number shown in front of the request-command is the packet payload size.
 * 
 * {@mythProtoExample notitle
 * 33      QUERY_REMOTEENCODER 1[]:[]IS_BUSY
 * }
 * 
 * <h3>Supported Commands:</h3>
 * 
 * The following page contains a list of request-commands supported by a MythTV-backend: {@link IMythCommand commands}.<br>
 * You can either send requests directly to a backend using a {@link IBackendConnection}, 
 * or you can use the more convenient functions provided by the {@link IBackend}, {@link IRecorder} or {@link IRemoteEncoder} classes.
 *
 * @see IMythCommand command
 * @see IMythPacket packet
 * @see IBackendConnection connection
 * @see IBackend backend
 */
public interface IMythRequest extends IVersionable, ISendable {
	/**
	 * Gets the request-command.
	 * <p>
	 * A command itself consists of a mandatory name and optional command arguments, 
	 * e.g.<code>GET_FREE_RECORDER_COUNT</code> or <code>ANN Playback MythTvPlayer 0</code>.
	 * 
	 * @return the command of the request object
	 */
	public IMythCommand getCommand();

	/**
	 * Gets additional request-arguments.
	 * 
	 * @return additional request arguments.
	 */
	public List<String> getRequestArguments();
}
