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
package org.jmythapi.protocol.response;

import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * The response to a {@link IRecorder#getFreeInputs()} or {@link IRemoteEncoder#getFreeInputs()} request.
 * <p>
 * 
 * <i>A response example:</i>
 * <br>
 * {@mythResponseExample 
 * 		Example Hauppauge HVR-1700:
 * 		<pre><0>INPUT_NAME: DVBInput | <1>SOURCE_ID: 1 | <2>INPUT_ID: 1 | <3>CARD_ID: 1 | <4>MULTIPLEX_ID: 0</pre>
 * 		Example Hauppauge PVR-150:
 * 		<pre><0>INPUT_NAME: Tuner 1 | <1>SOURCE_ID: 1 | <2>INPUT_ID: 1 | <3>CARD_ID: 1 | <4>MULTIPLEX_ID: 0</pre>
 * }
 * <br>
 * 
 * @see IRecorder#getFreeInputs()
 * @see IRemoteEncoder#getFreeInputs()
 * @see IMythCommand#QUERY_REMOTEENCODER_GET_FREE_INPUTS QUERY_REMOTEENCODER_GET_FREE_INPUTS
 * @see IMythCommand#QUERY_RECORDER_GET_FREE_INPUTS QUERY_RECORDER_GET_FREE_INPUTS
 */
public 	interface IFreeInputList extends Iterable<IInputInfoFree>, IVersionable {

	/**
	 * Gets the free-inputs as list
	 * 
	 * @return
	 * 		the input devices as list
	 */
	public List<IInputInfoFree> asList();

	/**
	 * Gets the input at the given position.
	 * 
	 * @param idx
	 * 		the input index
	 * @return
	 * 		the input
	 */
	public IInputInfoFree get(int idx);

	/**
	 * The size of this list
	 * @return
	 * 		the size of this list
	 */
	public int size();

	/**
	 * Checks if this list is empty.
	 * 
	 * @return
	 * 		{@code true} if the list is empty.
	 */
	public boolean isEmpty();
}