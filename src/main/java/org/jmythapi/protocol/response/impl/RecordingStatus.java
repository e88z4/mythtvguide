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


import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_40;
import static org.jmythapi.protocol.response.IRecordingStatus.Props.LIVETV_IN_PROGRESS;
import static org.jmythapi.protocol.response.IRecordingStatus.Props.RECORDING_IN_PROGRESS;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IRecordingStatus;

public class RecordingStatus extends AMythResponse<IRecordingStatus.Props> implements IRecordingStatus {
	
	public RecordingStatus(IMythPacket packet) {
		super(IRecordingStatus.Props.class, packet);
	}
	
	public int getActiveRecordings() {
		return (Integer)this.getPropertyValueObject(RECORDING_IN_PROGRESS);
	}
	
	/**
	 * @since {@mythProtoVersion 40}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_40)
	public int getLiveTVRecordings() {
		final String temp = this.getPropertyValue(LIVETV_IN_PROGRESS);
		return (temp == null) ? -1 : Integer.valueOf(temp).intValue(); 
	}
	
	public boolean isRecording() {
		int count = this.getActiveRecordings();
		return count > 0;
	}
}
