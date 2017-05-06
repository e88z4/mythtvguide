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
package org.jmythapi;

import java.net.ProtocolException;

import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.ProtocolVersion;

/**
 * Marks an object to be MythTv -protocol aware.
 * 
 * <h3>Protocol Version:</h3>
 * This interface provides a method to get the MythTv-protocol
 * a given object was created for. Depending on this version, the content of an object 
 * may be different, e.g. if properties were introduced or removed in a specific version.
 * <p>
 * See {@link ProtocolVersion} for a list of known protocol versions and a list of
 * changes done between different protocol versions.
 * 
 * <h3>Protocol Version Differences:</h3>
 * Because the amount, position or values of protocol command-arguments or response-values
 * may have been changed between different protocol versions, you can not use objects or commands
 * created for one version to communicate with a backend, speaking a different version. 
 * The {@link IBackendConnection} will check this and will throws an {@link ProtocolException}.
 * 
 * <h3>Usage example</h3>
 * An example how to get the protocol version of a recorder and how to compare it with a different protocol versions.
 * <br> 
 * {@mythCodeExample <pre>
 *    // the connected recorder
 *    IRecorder recorder = ...;
 *    
 *    // switch to the next recorder input
 *    if(recorder.getVersionNr().compareTo(PROTO_VERSION_27)<0) &#123;
 *       recorder.toggleInputs();
 *    &#125; else &#123;
 *       recorder.switchToNextInput();
 *    &#125;
 * </pre>}
 * @see ProtocolVersion
 */
public interface IVersionable {
	/**
	 * Gets the protocol-version of an object.
	 * 
	 * @return
	 * 		the version a given object was created for.
	 */
	public ProtocolVersion getVersionNr();
}
