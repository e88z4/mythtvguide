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

import static org.jmythapi.protocol.events.IDoneRecording.Props.RECORDED_FRAMES;
import static org.jmythapi.protocol.events.IDoneRecording.Props.RECORDED_SECONDS;
import static org.jmythapi.protocol.events.IDoneRecording.Props.RECORDER_ID;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IDoneRecording;

public class DoneRecording extends AMythEvent<IDoneRecording.Props> implements IDoneRecording {
	public DoneRecording(IMythPacket packet) {
		super(Props.class, packet);
	}

	public Long getRecordedFrames() {
		return this.getPropertyValueObject(RECORDED_FRAMES);
	}

	public Long getRecordedSeconds() {
		return this.getPropertyValueObject(RECORDED_SECONDS);
	}

	public Integer getRecorderID() {
		return this.getPropertyValueObject(RECORDER_ID);
	}

}
