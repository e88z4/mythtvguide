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

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.UnsupportedCommandException;

public class RecordingListChange {
	
	public static AMythEvent<?> valueOf(IMythPacket eventPacket) throws UnsupportedCommandException {
		final List<String> arguments = AMythEvent.extractArgumentsList(eventPacket);
		
		// a event packet with sub-type
		final String subEventType = arguments.isEmpty()?null:arguments.remove(0);
		if(null == subEventType) {
			return new RecordingListChangeAny(eventPacket.getVersionNr(),arguments);
		} else if("ADD".equals(subEventType)) {
			return new RecordingListChangeAdd(eventPacket.getVersionNr(),arguments);
		} else if ("UPDATE".equals(subEventType)) {
			return new RecordingListChangeUpdate(eventPacket.getVersionNr(),arguments);
		} else if ("DELETE".equals(subEventType)) {
			return new RecordingListChangeDelete(eventPacket.getVersionNr(),arguments);
		}
		
		throw new UnsupportedCommandException("Unknown event type " + subEventType);
	}
}
