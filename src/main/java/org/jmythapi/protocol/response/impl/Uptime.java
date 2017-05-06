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

import static org.jmythapi.protocol.response.IUptime.Props.UPTIME_SECONDS;

import java.util.Calendar;
import java.util.Date;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IUptime;
import org.jmythapi.utils.EncodingUtils;

public class Uptime extends AMythResponse<IUptime.Props> implements IUptime {
	
	public Uptime(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	public Uptime(ProtocolVersion protoVersion) {
		super(protoVersion, Props.class);
	}
	
	public Integer getUptimeSeconds() {
		return this.getUptimeSeconds(null);
	}
	
	public Integer getUptimeSeconds(Date uptimeLoadedDate) {
		final Integer uptimeSeconds = this.getPropertyValueObject(UPTIME_SECONDS);
		if(uptimeLoadedDate == null) return uptimeSeconds;
		
		final int uptimeOffset = EncodingUtils.getSecondsDiff(uptimeLoadedDate,new Date());
		return uptimeSeconds + uptimeOffset;
	}
	
	public void setUptimeSeconds(int seconds) {
		this.setPropertyValue(UPTIME_SECONDS, Integer.toString(seconds));
	}
	
	public Date getStartupTime() {
		return this.getStartupTime(null);
	}	
	
	public Date getStartupTime(Date uptimeLoadedDate) {
		final Integer uptimeSecs = this.getUptimeSeconds(uptimeLoadedDate);
		if(uptimeSecs == null) return null;

		// calculate the startup time
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND,uptimeSecs * -1);
		return cal.getTime();
	}
}
