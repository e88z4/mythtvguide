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
package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.events.IUpdateFileSize.Props.CHANNEL_ID;
import static org.jmythapi.protocol.events.IUpdateFileSize.Props.RECORDING_FILE_SIZE;
import static org.jmythapi.protocol.events.IUpdateFileSize.Props.RECORDING_START_TIME;

import java.util.Date;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IUpdateFileSize;
import org.jmythapi.utils.EncodingUtils;

public class UpdateFileSize extends AMythEvent<IUpdateFileSize.Props> implements IUpdateFileSize {
	public UpdateFileSize(IMythPacket packet) {
		super(Props.class, packet);
	}

	public Integer getChannelID() {
		return this.getPropertyValueObject(CHANNEL_ID);
	}

	public Date getRecordingStartTime() {
		return this.getPropertyValueObject(RECORDING_START_TIME);
	}

	public String getUniqueRecordingID() {
		return EncodingUtils.generateId(this.getChannelID(),this.getRecordingStartTime());
	}
	
	public Long getRecordingFileSize() {
		return this.getPropertyValueObject(RECORDING_FILE_SIZE);
	}
}
