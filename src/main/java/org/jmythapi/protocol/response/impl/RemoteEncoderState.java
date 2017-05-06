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
package org.jmythapi.protocol.response.impl;


import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.response.IRemoteEncoderState;
import org.jmythapi.protocol.utils.EnumUtils;

public class RemoteEncoderState extends AMythResponse<IRemoteEncoderState.Props> implements IRemoteEncoderState {
	
	public RemoteEncoderState(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	public int getStateValue() {
		final String stateStr = this.getPropertyValue(Props.STATE);
		return Integer.valueOf(stateStr).intValue();
	}
	
	public boolean hasState(State state) {
		return this.hasState(new State[]{state});
	}
	
	public boolean hasState(State... states) {
		final State currentState = this.getState();		
		if(currentState == null) return false;
		
		boolean hasState = false;
		for(State state : states) {
			hasState |= currentState.equals(state);
		}
		return hasState;
	}
	
	/**
	 * @return {@code null} if there was an error or the {@link RemoteEncoderState} otherwise.
	 */
	public IRemoteEncoderState.State getState() {
		final int stateValue = this.getStateValue();
		if (stateValue == -1) return null;
		
		// getting the enums for the current protocol version
		return EnumUtils.getEnum(State.class, this.protoVersion, stateValue);		
	}
}
