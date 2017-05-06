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
import static org.jmythapi.protocol.response.IInputInfoFree.Props.*;

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IInputInfoFree;

@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
public class InputInfoFree extends AMythResponse<IInputInfoFree.Props> implements IInputInfoFree {
	
	public InputInfoFree(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	public InputInfoFree(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion, Props.class, responseArgs);
	}
	
	public String getInputName() {
		final String name = this.getPropertyValue(INPUT_NAME);
		if (name == null || name.equals(EMPTY_INPUT)) return null;
		return name;
	}
	
	public Integer getSourceID() {
		return this.getPropertyValueObject(SOURCE_ID);
	}
	
	public Integer getInputID() {
		return this.getPropertyValueObject(INPUT_ID);
	}
	
	public Integer getCardID() {
		return this.getPropertyValueObject(CARD_ID);
	}
	
	public Integer getMultiplexID() {
		return this.getPropertyValueObject(MULTIPLEX_ID);
	}
	
	public Integer getLiveTvOrder() {
		return this.getPropertyValueObject(LIVETV_ORDER);
	}
	
	public boolean isBusy() {
		return false;
	}

	public Integer getChannelId() {
		return this.getPropertyValueObject(CHANNEL_ID);
	}

	public String getDisplayName() {
		return this.getPropertyValueObject(DISPLAY_NAME);
	}

	public Integer getRecordingPriority() {
		return this.getPropertyValueObject(REC_PRIORITY);
	}

	public Integer getScheduleOrder() {
		return this.getPropertyValueObject(SCHEDULE_ORDER);
	}

	public boolean getQuickTune() {
		return this.getPropertyValueObject(QUICK_TUNE);
	}
}
