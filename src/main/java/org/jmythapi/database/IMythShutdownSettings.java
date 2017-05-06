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
 * The MythShutdown Settings.
 * <p>
 * This object encapsulates all MythShutdown related settings stored in the MythTv settings table. 
 */
public interface IMythShutdownSettings extends IPropertyAware<IMythShutdownSettings.Props>{
	public static enum Props implements ISettingsProperty {
		/**
		 * Idle shutdown timeout (secs).
		 * <p>
		 * The amount of time the master backend idles before it shuts down all backends. 
		 * Set to 0 to disable "auto shutdown.
		 */
		@MythParameterType(Integer.class)
		IDLE_TIMEOUT("idleTimeoutSecs"),
		
		/**
		 * Max. wait for recording (min).
		 * <p>
		 * The amount of time the master backend waits for a recording.  If it's idle but a recording starts
		 * within this time period, the backends won't shut down.
		 */
		@MythParameterType(Integer.class)
		IDLE_WAIT_FOR_RECORDING("idleWaitForRecordingTime"),
		
		/**
		 * Startup before rec. (secs).
		 * <p>
		 * The amount of time the master backend will be woken up before a recording starts.
		 */
		@MythParameterType(Integer.class)
		STARTUP_BEFORE_RECORDING("StartupSecsBeforeRecording"),
		
		/**
		 * MythShutdown Lock.
		 * <p>
		 * If this flag is set, a shutdown via MythShutdown is blocked by the user.
		 */
		@MythParameterType(Integer.class)
		SHUTDOWN_LOCK("MythShutdownLock"),
		
		/**
		 * MythShutdown - Next scheduled shutdown.
		 * <p>
		 * The time of the next shutdown, scheduled by MythShutdown.
		 */
		@MythParameterType(Date.class)
		SHUTDOWN_NEXT_SCHEDULED("MythShutdownNextScheduled"),
		
		/**
		 * MythShutdown - Next scheduled wakeup.
		 * <p>
		 * The time of the next wakeup, scheduled by MythShutdown.
		 */
		@MythParameterType(Date.class)
		SHUTDOWN_WAKEUP_TIME("MythShutdownWakeupTime");
		
		private String settingName;
		
		private Props(String settingName) {
			this.settingName = settingName;
		}
		
		public String getSettingName() {
			return this.settingName;
		}
	}
	
	public static enum StartupMode {
		UNKNOWN,
		AUTOMATICALLY,
		MANUALLY
	}
	
	/**
	 * Specifies if shutdown was locked by the user.
	 * <p>
	 * This method returns {@code true} if the shutdown-lock counter is greater than {@code 0}
	 * 
	 * @return
	 * 		{@code true} if shutdown was locked by the user.
	 */
	public boolean isShutdownLocked();

	public Integer getLockedCount();
	
	public boolean isShutdownEnabled();
	
	public Integer getIdleTimeout();
	
	public Integer getIdleWaitForRecording();
	
	public Integer getStartupBeforeRecording();
	
	public Date getNextScheduledShutdown();
	
	public Date getNextScheduledWakeup();
	
	public StartupMode getStartupMode(Date startupTime);
}
