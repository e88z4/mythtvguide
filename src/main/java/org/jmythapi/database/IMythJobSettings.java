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

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.annotation.MythParameterType;

public interface IMythJobSettings extends IPropertyAware<IMythJobSettings.Props>{
	public static enum Props implements ISettingsProperty {
		
		/**
		 * Job Queue Check frequency (in seconds).
		 * <p>
		 * When looking for new jobs to process, the Job Queue will wait this long between checks.
		 */
		@MythParameterType(Integer.class)
		JOBQUEUE_CHECK_FREQUENCY("JobQueueCheckFrequency"),
		
		
		/**
		 * Maximum simultaneous jobs on this backend.
		 * <p>
		 * The Job Queue will be limited to running this many simultaneous jobs on this backend.
		 */
		@MythParameterType(Integer.class)
		JOBQUEUE_MAX_SIMULTANEOUS_JOBS("JobQueueMaxSimultaneousJobs"),
		
		/**
		 * CPU Usage.
		 * <p>
		 * <ul>
		 * 	<li>{@code 0}: Low</li>
		 *  <li>{@code 1}: Medium</li>
		 *  <li>{@code 2}: High</li>
		 * </ul>
		 * <p>
		 * This setting controls approximately how much CPU jobs in the queue may consume.
		 * On 'High', all available CPU time may be used which could cause problems on slower systems.
		 */
		@MythParameterType(Integer.class)
		JOBQUEUE_CPU("JobQueueCPU"),
		
		/**
		 * Job Queue Start Time.
		 * <p>
		 * This setting controls the start of the Job Queue time window which determines when new jobs 
		 * will be started.
		 */
		JOBQUEUE_WINDOW_START("JobQueueWindowStart"),
		
		/**
		 * Job Queue End Time.
		 * <p>
		 * This setting controls the end of the Job Queue time window which determines when new jobs 
		 * will be started.
		 */
		JOBQUEUE_WINDOW_END("JobQueueWindowEnd"),
		
		/**
		 * Transcoder command.
		 * <p>
		 * The program used to transcode recordings. The default is {@code mythtranscode} if this setting is empty.
		 */
		JOBQUEUE_TRANSCODE_COMMAND("JobQueueTranscodeCommand"),
		
		/**
		 * Commercial Flagger command.
		 * <p>
		 * The program used to detect commercials in a recording.  
		 * The default is {@code mythcommflag} if this setting is empty.
		 */
		JOBQUEUE_COMMFLAG_COMMAND("JobQueueCommFlagCommand"),
		
		/**
		 * Run Jobs only on original recording backend.
		 * <p>
		 * If set, jobs in the queue will be required to run on the backend that made the
		 * original recording.
		 */
		JOB_RUN_ON_RECORD_HOST("JobsRunOnRecordHost"),
		
		/**
		 * Allow Commercial Detection jobs.
		 * <p>
		 * Allow jobs of this type to run on this backend.
		 */
		JOB_ALLOW_COMMFLAG("JobAllowCommFlag"),
		
		/**
		 * Allow Transcoding jobs.
		 * <p>
		 * Allow jobs of this type to run on this backend.
		 */
		JOB_ALLOW_TRANSCODING("JobAllowTranscode"),
		
		/**
		 * Allow User Job 1 jobs.
		 * <p>
		 * Allow jobs of this type to run on this backend.
		 */
		JOB_ALLOW_USERJOB1("JobAllowUserJob1"),
		
		/**
		 * Allow User Job 2 jobs.
		 * <p>
		 * Allow jobs of this type to run on this backend.
		 */		
		JOB_ALLOW_USERJOB2("JobAllowUserJob2"),
		
		/**
		 * Allow User Job 3 jobs.
		 * <p>
		 * Allow jobs of this type to run on this backend.
		 */			
		JOB_ALLOW_USERJOB3("JobAllowUserJob3"),
		
		/**
		 * Allow User Job 4 jobs.
		 * <p>
		 * Allow jobs of this type to run on this backend.
		 */	
		JOB_ALLOW_USERJOB4("JobAllowUserJob4"),
		
		/**
		 * User Job 1 Command.
		 * <p>
		 * The Description for this User Job.
		 */		
		USERJOB1_COMMAND("UserJob1"),
		
		/**
		 * User Job 2 Command.
		 * <p>
		 * The Description for this User Job.
		 */		
		USERJOB2_COMMAND("UserJob2"),
		
		/**
		 * User Job 3 Command.
		 * <p>
		 * The Description for this User Job.
		 */
		USERJOB3_COMMAND("UserJob3"),
		
		/**
		 * User Job 4 Command.
		 * <p>
		 * The Description for this User Job.
		 */
		USERJOB4_COMMAND("UserJob4"),
		
		/**
		 * User Job 1 Description.
		 * <p>
		 * The Description for this User Job.
		 */
		USERJOB1_DESCRIPTION("UserJobDesc1"),
		
		/**
		 * User Job 2 Description.
		 * <p>
		 * The Description for this User Job.
		 */
		USERJOB2_DESCRIPTION("UserJobDesc2"),
		
		/**
		 * User Job 3 Description.
		 * <p>
		 * The Description for this User Job.
		 */
		USERJOB3_DESCRIPTION("UserJobDesc3"),
		
		/**
		 * User Job 4 Description.
		 * <p>
		 * The Description for this User Job.
		 */
		USERJOB4_DESCRIPTION("UserJobDesc4")		
		;
		
		private String settingName;
		
		private Props(String settingName) {
			this.settingName = settingName;
		}
		
		public String getSettingName() {
			return this.settingName;
		}		
	}

}
