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
package org.jmythapi.database.impl;

import static org.jmythapi.database.IMythShutdownSettings.Props.IDLE_TIMEOUT;
import static org.jmythapi.database.IMythShutdownSettings.Props.IDLE_WAIT_FOR_RECORDING;
import static org.jmythapi.database.IMythShutdownSettings.Props.SHUTDOWN_LOCK;
import static org.jmythapi.database.IMythShutdownSettings.Props.SHUTDOWN_NEXT_SCHEDULED;
import static org.jmythapi.database.IMythShutdownSettings.Props.SHUTDOWN_WAKEUP_TIME;
import static org.jmythapi.database.IMythShutdownSettings.Props.STARTUP_BEFORE_RECORDING;

import java.util.Date;
import java.util.Map;

import org.jmythapi.ISetting;
import org.jmythapi.database.IMythShutdownSettings;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.utils.EncodingUtils;

public class MythShutdownStatus extends ASettingsGroup<IMythShutdownSettings.Props> implements IMythShutdownSettings {

	public MythShutdownStatus(ProtocolVersion protoVersion, int dbVersion, Map<String, ISetting> settings) {
		super(protoVersion, dbVersion, Props.class, settings);
	}
	
	public boolean isShutdownLocked() {
		final Integer lockedCount = this.getLockedCount();
		return lockedCount != null && lockedCount.intValue() > 0;
	}

	public Integer getLockedCount() {
		return this.getPropertyValueObject(SHUTDOWN_LOCK);
	}
	
	public boolean isShutdownEnabled() {
		final Integer idleTimeout = this.getIdleTimeout();
		return idleTimeout != null && idleTimeout.intValue() > 0;
	}
	
	public Integer getIdleTimeout() {
		return this.getPropertyValueObject(IDLE_TIMEOUT);
	}
	
	public Integer getIdleWaitForRecording() {
		return this.getPropertyValueObject(IDLE_WAIT_FOR_RECORDING);
	}
	
	public Integer getStartupBeforeRecording() {
		return this.getPropertyValueObject(STARTUP_BEFORE_RECORDING);
	}
	
	public Date getNextScheduledShutdown() {
		return this.getPropertyValueObject(SHUTDOWN_NEXT_SCHEDULED);
	}
	
	public Date getNextScheduledWakeup() {
		return this.getPropertyValueObject(SHUTDOWN_WAKEUP_TIME);
	}
	
	public StartupMode getStartupMode(Date startupTime) {
		final Date nextWakeupTime = this.getNextScheduledWakeup();
		if(nextWakeupTime != null && startupTime != null) {			
			// calculate minutes between uptime-date and planned wakeup date
			final int minDiff = EncodingUtils.getMinutesDiff(startupTime,nextWakeupTime);
			
	        // from mythshutdown: if we started within 15mins of the saved wakeup time assume we started
	        // automatically to record or for a daily wakeup/shutdown period
			if(Math.abs(minDiff) <= 15) {
				return StartupMode.AUTOMATICALLY;
			} else if (minDiff > 0){
				return StartupMode.MANUALLY;
			} else {
				return StartupMode.UNKNOWN;
			}
		} else {
			return StartupMode.UNKNOWN;
		}
	}
}
