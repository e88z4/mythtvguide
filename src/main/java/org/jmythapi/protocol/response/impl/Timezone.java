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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_42;
import static org.jmythapi.protocol.response.ITimezone.Props.CURRENT_DATE;
import static org.jmythapi.protocol.response.ITimezone.Props.TIMEZONE_ID;
import static org.jmythapi.protocol.response.ITimezone.Props.UTC_OFFSET;

import java.util.Date;
import java.util.TimeZone;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.impl.Backend;
import org.jmythapi.protocol.response.ITimezone;

/**
 * @see Backend#queryTimeZone()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_42)
public class Timezone extends AMythResponse<ITimezone.Props> implements ITimezone {
	
	public Timezone(IMythPacket packet) {
		super(ITimezone.Props.class, packet);
	}
	
	public String getTimeZoneID() {
		return this.getPropertyValue(TIMEZONE_ID);
	}
	
	public Integer getUTCOffset() {
		final String offset = this.getPropertyValue(UTC_OFFSET);
		if (offset == null) return null;
		return Integer.valueOf(offset);
	}

	public TimeZone getTimeZone() {
		final String timeZoneID = this.getPropertyValue(TIMEZONE_ID);
		if (timeZoneID == null) return null;
		return TimeZone.getTimeZone(timeZoneID);
	}
	
	public Date getCurrentDate() {
		return this.getPropertyValueObject(CURRENT_DATE);
	}
}
