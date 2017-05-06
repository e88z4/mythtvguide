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
package org.jmythapi.database;

import java.util.Date;

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.annotation.MythParameterType;

/**
 * The MythFillDatabase Settings.
 * <p>
 * This object encapsulates all MythFillDatabase related settings stored in the MythTv settings table. 
 * <p>
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>FILL_ENABLED: true | <1>FILL_PERIOD: 1 | <2>FILL_MIN_HOUR: 18 | <3>FILL_MAX_HOUR: 22 | <4>FILL_USE_SUGGESTED_RUNTIME: true | <5>FILL_NEXT_SUGGESTED_RUNTIME: 1970-01-01 00:00:00 | <6>LAST_RUN_START: 2012-01-26 19:25:00 | <7>LAST_RUN_END: 2012-01-26 19:26:00 | <8>LAST_RUN_STATUS: Successfully.</pre>
 * }
 */
public interface IMythFillDatabaseSettings extends IPropertyAware<IMythFillDatabaseSettings.Props>{
	public static enum Props implements ISettingsProperty {
		/**
		 * Automatically run MythFillDatabase.
		 * <p>
		 * This enables the automatic execution of mythfilldatabase.
		 */
		@MythParameterType(Boolean.class)
		FILL_ENABLED("MythFillEnabled"),
		
		/**
		 * MythFillDatabase Run Frequency (Days).
		 * <p>
		 * The number of days between mythfilldatabase runs.
		 */
		@MythParameterType(Integer.class)
		FILL_PERIOD("MythFillPeriod"),
		
		/**
		 * MythFillDatabase Execution Start.
		 * <p>
		 * This setting and the following one define a time period when the mythfilldatabase process is 
		 * allowed to run.  For example, setting Start to 11 and End to 13 would mean that the process 
		 * would only run between 11:00 AM and 1:59 PM.
		 */
		@MythParameterType(Integer.class)
		FILL_MIN_HOUR("MythFillMinHour"),
		
		/**
		 * MythFillDatabase Execution End.
		 * <p>
		 * This setting and the preceding one define a time period when the mythfilldatabase process is
		 * allowed to run.  For example, setting Start to 11 and End to 13 would mean that the process 
		 * would only run between 11:00 AM and 1:59 PM.
		 */
		@MythParameterType(Integer.class)
		FILL_MAX_HOUR("MythFillMaxHour"),
		
		/**
		 * Run MythFillDatabase at time suggested by the grabber.
		 * <p>
		 * This setting allows a DataDirect guide data provider to specify the next download time in order to
		 * distribute load on their servers. If this setting is enabled, MythFillDatabase Execution Start/End 
		 * times are ignored.
		 */
		@MythParameterType(Boolean.class)
		FILL_USE_SUGGESTED_RUNTIME("MythFillGrabberSuggestsTime"),
		
		/**
		 * Next suggested runtime for MythFillDatabase.
		 */
		@MythParameterType(Date.class)
		FILL_NEXT_SUGGESTED_RUNTIME("MythFillSuggestedRunTime"),
		
		/**
		 * Last MythFillDatabase run start-time.
		 */
		@MythParameterType(Date.class)
		LAST_RUN_START("mythfilldatabaseLastRunStart"),
		
		/**
		 * Last MythFillDatabase run end-time.
		 */
		@MythParameterType(Date.class)
		LAST_RUN_END("mythfilldatabaseLastRunEnd"),
		
		/**
		 * Last MythFillDatabase status.
		 */
		@MythParameterType(String.class)
		LAST_RUN_STATUS("mythfilldatabaseLastRunStatus");
		
		private String settingName;
		
		private Props(String settingName) {
			this.settingName = settingName;
		}
		
		public String getSettingName() {
			return this.settingName;
		}
	}
	
	public Boolean isFillEnabled();
	
	public Integer getFillMinHour();
	
	public Integer getFillMaxHour();
	
	public Integer getFillPeriod();
	
	public Boolean useSuggestedRunTime();
	
	public Date getSuggestedRunTime();
	
	public Date getLastRunStart();
	
	public Date getLastRunEnd();
	
	public Integer getLastRunDuration();
	
	public String getLastRunStatus();
	
	public Boolean isRunning();
}
