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

import static org.jmythapi.database.IMythFillDatabaseSettings.Props.FILL_ENABLED;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.FILL_MAX_HOUR;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.FILL_MIN_HOUR;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.FILL_NEXT_SUGGESTED_RUNTIME;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.FILL_PERIOD;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.FILL_USE_SUGGESTED_RUNTIME;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.LAST_RUN_END;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.LAST_RUN_START;
import static org.jmythapi.database.IMythFillDatabaseSettings.Props.LAST_RUN_STATUS;

import java.util.Date;
import java.util.Map;

import org.jmythapi.ISetting;
import org.jmythapi.database.IMythFillDatabaseSettings;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.utils.EncodingUtils;

public class MythFillDatabaseStatus extends ASettingsGroup<IMythFillDatabaseSettings.Props> implements IMythFillDatabaseSettings {

	public MythFillDatabaseStatus(ProtocolVersion protoVersion, int dbVersion, Map<String, ISetting> settings) {
		super(protoVersion, dbVersion, Props.class, settings);
	}

	public Boolean isFillEnabled() {
		return this.getPropertyValueObject(FILL_ENABLED);
	}	
	
	public Integer getFillMinHour() {
		return this.getPropertyValueObject(FILL_MIN_HOUR);
	}
	
	public Integer getFillMaxHour() {
		return this.getPropertyValueObject(FILL_MAX_HOUR);
	}
	
	public Integer getFillPeriod() {
		return this.getPropertyValueObject(FILL_PERIOD);
	}	
	
	public Boolean useSuggestedRunTime() {
		return this.getPropertyValueObject(FILL_USE_SUGGESTED_RUNTIME);
	}
	
	public Date getSuggestedRunTime() {		
		
		final Date suggestedRunTime = this.getPropertyValueObject(FILL_NEXT_SUGGESTED_RUNTIME);
		if(suggestedRunTime == null) return null;
		
		final Date startTime = this.getLastRunStart();
		if (startTime != null && suggestedRunTime.compareTo(startTime)< 0) return null;
		return suggestedRunTime;		
	}	
	
	public Date getLastRunStart() {
		return this.getPropertyValueObject(LAST_RUN_START);
	}
	
	public Date getLastRunEnd() {
		return this.getPropertyValueObject(LAST_RUN_END);
	}
	
	public Integer getLastRunDuration() {
		final Date lastRunStart = this.getLastRunStart();
		if(lastRunStart == null) return null;
		
		final Date lastRunEnd = this.getLastRunEnd();
		if(lastRunEnd == null) return null;
		
		return Integer.valueOf(EncodingUtils.getMinutesDiff(lastRunStart,lastRunEnd));
	}
	
	public Boolean isRunning() {
		final Date startDate = this.getLastRunStart();
		if(startDate == null) return Boolean.FALSE;
		
		final Date endDate = this.getLastRunEnd();
		if(endDate != null && (startDate.before(endDate) || startDate.equals(endDate))) {
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	public String getLastRunStatus() {
		return this.getPropertyValueObject(LAST_RUN_STATUS);
	}
}
