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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IInputInfoTuned;
import org.jmythapi.protocol.response.IRemoteEncoderBusyStatus;
import org.jmythapi.protocol.utils.EnumUtils;

public class RemoteEncoderBusyStatus extends AMythResponse<IRemoteEncoderBusyStatus.Props> implements IRemoteEncoderBusyStatus {
	
	public RemoteEncoderBusyStatus(IMythPacket packet) {
		super(IRemoteEncoderBusyStatus.Props.class, packet);
	}
	
	@Override
	protected int getExpectedSize(List<String> responseArgs) {
		int inputInfoLength = 1;		
		if (this.protoVersion.compareTo(PROTO_VERSION_37)>=0) {
			inputInfoLength += EnumUtils.getEnumLength(IInputInfoTuned.Props.class, this.protoVersion);
		}
		return inputInfoLength;
	}
	
	public boolean isBusy() {
		final String busyVal = this.getPropertyValue(Props.IS_BUSY);
		return !busyVal.equals("0");
	}
	
	/**
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public InputInfoTuned getInputInfo() {
		if(this.respArgs.size()<=1) return null;
		else if(!this.isBusy()) return null;
		
		// return the busy input
		final List<String> inputInfoArgs = this.respArgs.subList(1, this.respArgs.size());
		return new InputInfoTuned(this.protoVersion,inputInfoArgs);
	}
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		buff.append(super.toString());
		if (this.protoVersion.compareTo(PROTO_VERSION_37)>=0) {
			buff.append(" | ");
			buff.append(this.getInputInfo().toString());
		}
		return buff.toString();
	}
}
