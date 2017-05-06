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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1056;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1170;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IGuideDataThrough;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.ISetting;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.database.impl.JobQueue;
import org.jmythapi.database.impl.Schedule;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;

public interface IDatabase {
	public static final int DEFAULT_PORT = 3306;
	public static final String DEFAULT_USER = "mythtv";
	public static final String DEFAULT_DB = "mythconverg";
	
	public static final String SETTING_HOST_ANY = "*";
	
	/**
	 * Gets the current database version.
	 * 
	 * @return
	 * 		the database version
	 */
	public int getDbVersion();

	/* ==========================================================================
	 * CHANNEL related methods
	 * ========================================================================== */	
	
	/**
	 * Gets informations about the given channel
	 * 
	 * @param chanID
	 * 		the channel id
	 * @return
	 * 		informations about the given channel
	 * @throws IOException
	 * 		on communication errors
	 */
	public IRecorderChannelInfo getChannelInfo(Integer chanID) throws IOException;
	
	public List<IRecorderChannelInfo> getChannelInfosByCallSign(String callSign) throws IOException;
	
	public List<IRecorderChannelInfo> getChannelInfos() throws IOException;
	
	public List<IRecorderChannelInfo> getChannelInfos(IRecorderInfo recorder) throws IOException;
	
	public List<IRecorderChannelInfo> getChannelInfos(Integer recorderID) throws IOException;

	
	/* ==========================================================================
	 * RECORDER related methods
	 * ========================================================================== */	
	
	public IRecorderInfo getRecorderForNum(Integer recorderId) throws IOException;

	public List<IRecorderInfo> getRecorders() throws IOException;
	
	/* ==========================================================================
	 * SETTINGS related methods
	 * ========================================================================== */	
	
	public ISetting querySetting(String hostName,String settingName) throws IOException;
	
	public List<ISetting> querySettings(String hostName) throws IOException;
	
	public List<ISetting> querySettings(String hostName,String... settingsNames) throws IOException;	

	public Map<String,ISetting> querySettingsMap(String hostName,String... settingsNames) throws IOException;
	
	public IMythFillDatabaseSettings queryMythFillStatus() throws IOException;
	
	public IMythShutdownSettings queryMythShutdownStatus() throws IOException;
	
	/* ==========================================================================
	 * PROGRAM INFO related methods
	 * ========================================================================== */
	
	public IRecorderNextProgramInfo queryNextProgramInfo(IBasicChannelInfo channelInfo, Date startDateTime) throws IOException; 
	
	public IRecorderNextProgramInfo queryNextProgramInfo(Integer channelId, Date startDateTime) throws IOException; 
		
	public IGuideDataThrough queryGuideDataThrough() throws IOException;	
	
	/* ==========================================================================
	 * STORAGE GROUPS related methods
	 * ========================================================================== */
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)
	public List<IStorageGroup> getStorageGroups() throws IOException;
	
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)
	public List<IStorageGroupDirectory> getStorageGroupDirectories() throws IOException;
	
	/* ==========================================================================
	 * JOBS related methods
	 * ========================================================================== */
	
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)
	public List<IJobQueue> getJobs() throws IOException;
	
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)
	public IJobQueue getJob(Integer jobId) throws IOException;
	
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)
	public Integer addJob(JobQueue job) throws IOException;
	
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)
	public boolean controlJob(Integer jobId, IJobCommand command) throws IOException ;
	
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)
	public boolean deleteJob(Integer jobId) throws IOException;
	
	/* ==========================================================================
	 * SCHEDULE related methods
	 * ========================================================================== */
	
	public List<ISchedule> getSchedules() throws IOException;
	
	public ISchedule getSchedule(Integer scheduleId) throws IOException;
	
	public Integer addSchedule(Schedule schedule) throws IOException;
	
	public boolean deleteSchedule(Integer scheduleId) throws IOException;
}
