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

import static org.jmythapi.protocol.events.IShutdownCountdown.Props.SECONDS;

import java.util.Calendar;
import java.util.Date;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IShutdownCountdown;

public class ShutdownCountdown extends AMythEvent<IShutdownCountdown.Props> implements IShutdownCountdown {
	private Date date = null;
	
	public ShutdownCountdown(IMythPacket packet) {
		super(Props.class, packet);
		this.date = packet.getPacketCreationTime();
	}

	public Integer getSecondsTillShutdown() {
		return this.getPropertyValueObject(SECONDS);
	}
	
	public Date getShutdownTime() {
		final Integer secondsTillShutdown = this.getSecondsTillShutdown();
		if(secondsTillShutdown == null) return null;
		
		final Calendar cal = Calendar.getInstance();
		cal.setTime(this.date);
		cal.add(Calendar.SECOND,secondsTillShutdown.intValue());
		return cal.getTime();
	}
}
