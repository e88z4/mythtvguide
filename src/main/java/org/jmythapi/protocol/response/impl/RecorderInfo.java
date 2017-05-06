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

import static org.jmythapi.IRecorderInfo.Props.HOSTNAME;
import static org.jmythapi.IRecorderInfo.Props.HOSTPORT;
import static org.jmythapi.IRecorderInfo.Props.RECORDER_ID;

import java.util.List;

import org.jmythapi.IRecorderInfo;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;

public class RecorderInfo extends AMythResponse<IRecorderInfo.Props> implements IRecorderInfo {	
	
	public RecorderInfo(IMythPacket packet) {
		super(IRecorderInfo.Props.class, packet);
	}
	
	public RecorderInfo(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion, IRecorderInfo.Props.class, responseArgs);
	}
	
	public int getRecorderID() {
		return (Integer)this.getPropertyValueObject(RECORDER_ID);
	}
	
	public String getHostName() {
		return this.getPropertyValue(HOSTNAME);
	}
	
	public int getHostPort() {
		return (Integer) this.getPropertyValueObject(HOSTPORT);
	}
	
	public static boolean recorderFound(IMythPacket packet) {
		return recorderFound(packet.getPacketArgs());
	}
	
	public static boolean recorderFound(List<String> responseArgs) {
		return !responseArgs.get(1).equalsIgnoreCase(ERROR_HOSTNAME);
	}

	public static RecorderInfo valueOf(IMythPacket packet) {
		if(!recorderFound(packet)) return null;
		return new RecorderInfo(packet);
	}
}
