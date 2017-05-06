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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1309;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.database.DatabaseVersion;
import org.jmythapi.database.ISchedule;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IBasicProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingFilters;
import org.jmythapi.protocol.response.IProgramRecordingSearchType;
import org.jmythapi.protocol.response.IProgramRecordingType;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingFilters.Filters;
import org.jmythapi.protocol.response.IProgramRecordingSearchType.Type;
import org.jmythapi.protocol.response.impl.ProgramRecordingSearchType;
import org.jmythapi.protocol.response.impl.ProgramRecordingType;
import org.jmythapi.utils.EncodingUtils;

public class Schedule extends ADatabaseRow<ISchedule.Props> implements ISchedule, IBasicProgramInfo {
	private IProgramRecordingType fallbackRecType = null;
	
	public Schedule(ProtocolVersion protoVersion,int dbVersion) {
		super(protoVersion, dbVersion, Props.class);
	}
	
	public Schedule(ProtocolVersion protoVersion, int dbVersion, List<String> data) {
		super(protoVersion, dbVersion, Props.class, data);
	}

	/* ========================== RECORDING-ID ========================== */
	
	public Integer getRecordingId() {
		return this.getPropertyValueObject(Props.REC_ID);
	}

	public void setRecordingId(Integer recordId) {
		this.setPropertyValueObject(Props.REC_ID,recordId);
	}
	
	/* ========================== RECORDING-TYPE ========================== */
	
	public IProgramRecordingType getRecordingType() {
		return this.getPropertyValueObject(Props.REC_TYPE);
	}
	
	/**
	 * @see #onSaveDataItem()
	 */
	@SuppressWarnings("deprecation")
	public void setRecordingType(IProgramRecordingType recType) {
		if(this.getDatabaseVersion() >= DB_VERSION_1309.getVersion()) {
			final IProgramRecordingType currentRecType = getRecordingType();			
			if(currentRecType.hasEnum(
				IProgramRecordingType.Type.CHANNEL_RECORD,
				IProgramRecordingType.Type.TIMESLOT_RECORD,
				IProgramRecordingType.Type.WEEKSLOT_RECORD,
				IProgramRecordingType.Type.FIND_DAILY_RECORD,
				IProgramRecordingType.Type.FIND_WEEKLY_RECORD
			)) {
				this.fallbackRecType = recType;
			}
		}
		
		this.setPropertyValueObject(Props.REC_TYPE, recType);
	}
	
	/* ========================== CHANNEL ========================== */
	
	public Integer getChannelID() {
		return this.getPropertyValueObject(Props.CHANNEL_ID);
	}
	
	public void setChannelID(Integer channelId) {
		this.setPropertyValueObject(Props.CHANNEL_ID,channelId);
	}
	
	public String getChannelSign() {
		return this.getPropertyValueObject(Props.CHANNEL_SIGN);		
	}
	
	public void setChannelSign(String sign) {
		this.setPropertyValueObject(Props.CHANNEL_SIGN,sign);
	}
	
	public void setChannel(IBasicChannelInfo channelInfo) {
		this.setChannelSign(channelInfo.getChannelSign());
		this.setChannelID(channelInfo.getChannelID());
	}
	
	/* ========================== START DATE + TIME ========================== */
	
	public java.sql.Date getStartDate() {
		return this.getPropertyValueObject(Props.START_DATE);
	}
	
	public Time getStartTime() {
		return this.getPropertyValueObject(Props.START_TIME);
	}
	
	public Date getStartDateTime() {
		final boolean isUTC = (this.getDatabaseVersion() >= DatabaseVersion.DB_VERSION_1302.getVersion());
		return EncodingUtils.aggregateDateTime(
			this.getStartDate(), 
			this.getStartTime(),
			isUTC
		);
	}
	
	public void setStartDate(Date startDate) {
		this.setPropertyValueObject(Props.START_DATE,startDate);
	}
	
	public void setStartTime(Date startTime) {
		this.setPropertyValueObject(Props.START_TIME,startTime);
	}
	
	public void setStartDateTime(Date startDateTime) {		
		this.setStartDate(startDateTime);
		this.setStartTime(startDateTime);
	}
	
	/* ========================== END DATE + TIME ========================== */
	
	public java.sql.Date getEndDate() {
		return this.getPropertyValueObject(Props.END_DATE);
	}
	
	public Time getEndTime() {
		return this.getPropertyValueObject(Props.END_TIME);
	}
	
	public Date getEndDateTime() {
		final boolean isUTC = (this.getDatabaseVersion() >= DatabaseVersion.DB_VERSION_1302.getVersion());
		return EncodingUtils.aggregateDateTime(
			this.getEndDate(), 
			this.getEndTime(),
			isUTC
		);
	}
	
	public void setEndDate(Date endDate) {
		this.setPropertyValueObject(Props.END_DATE,endDate);
	}
	
	public void setEndTime(Date endDate) {
		this.setPropertyValueObject(Props.END_TIME,endDate);
	}
	
	public void setEndDateTime(Date endDateTime) {		
		this.setEndDate(endDateTime);
		this.setEndTime(endDateTime);
	}
	
	/* ========================== TITLE ========================== */
	
	public String getTitle() {
		return getPropertyValueObject(Props.TITLE);
	}
	
	public void setTitle(String title) {
		this.setPropertyValueObject(Props.TITLE,title);
	}
	
	public String getFullTitle() {
		return EncodingUtils.getFormattedTitle(this.getTitle(),this.getSubtitle());
	}	
	
	/* ========================== SUBTITLE ========================== */
	
	public String getSubtitle() {
		return getPropertyValueObject(Props.SUBTITLE);
	}
	
	public void setSubtitle(String subTitle) {
		this.setPropertyValueObject(Props.SUBTITLE,subTitle);
	}
	
	/* ========================== DESCRIPTION ========================== */
	
	public String getDescription() {
		return getPropertyValueObject(Props.DESCRIPTION);
	}
	
	public void setDescription(String description) {
		this.setPropertyValueObject(Props.DESCRIPTION,description);
	}
	
	/* ========================== CATEGORY ========================== */
	
	public String getCategory() {
		return getPropertyValueObject(Props.CATEGORY);
	}
	
	public void setCategory(String category) {
		this.setPropertyValueObject(Props.CATEGORY,category);
	}
	
	/* ========================== PROFILE ========================== */
	public String getProfile() {
		return getPropertyValueObject(Props.PROFILE);
	}
	
	public void setProfile(String profile) {
		this.setPropertyValueObject(Props.PROFILE,profile);
	}
	
	/* ========================== SEASON ========================== */
	
	public Integer getSeason() {
		return getPropertyValueObject(Props.SEASON);
	}
	
	public void setSeason(Integer season) {
		this.setPropertyValueObject(Props.SEASON,season);
	}
	
	/* ========================== EPISODE ========================== */
	public Integer getEpisode() {
		return getPropertyValueObject(Props.EPISODE);
	}
	
	public void setEpisode(Integer episode) {
		this.setPropertyValueObject(Props.EPISODE,episode);
	}
	
	/* ========================== PRIORITY ========================== */
	
	public Integer getRecordingPriority() {
		return getPropertyValueObject(Props.REC_PRIORITY);
	}
		
	public void setRecordingPriority(Integer priority) {
		this.setPropertyValueObject(Props.REC_PRIORITY,priority);
	}
	
	/* ========================== FIND DAY / TIME / ID ========================== */
	
	public Integer getFindDay() {
		return getPropertyValueObject(Props.FIND_DAY);
	}
	
	public void setFindDay(Date dayDateTime) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(dayDateTime);
		
		final int day = cal.get(Calendar.DAY_OF_WEEK);
		this.setFindDay(Integer.valueOf(day));
	}
	
	public void setFindDay(Integer day) {
		setPropertyValueObject(Props.FIND_DAY,day);
	}
	
	public Time getFindTime() {
		final String findTimeValue = getPropertyValue(Props.FIND_TIME);
		return EncodingUtils.parseTime(findTimeValue,false /* no tz conversion required */);
	}
	
	public void setFindTime(Date findTime) {
		final String findTimeValue = EncodingUtils.formatTime(findTime,false /* no tz conversion required */);
		setPropertyValue(Props.FIND_TIME,findTimeValue);
	}
	
	public Integer getFindId() {
		return getPropertyValueObject(Props.FIND_ID);
	}
	
	public void setFindId(Date startDateTime) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startDateTime.getTime());
		
		// calculate the id (see http://www.mythtv.org/wiki/Record_table)
		// TODO: this seems to be wrong
		long days = cal.getTimeInMillis()/1000/60/60/24;
		days += 719528;
		
		this.setFindId(Integer.valueOf((int)days));
	}
	
	public void setFindId(Integer findId) {
		setPropertyValueObject(Props.FIND_ID,findId);
	}
	
	/* ========================== SEARCH ========================== */
	
	public IProgramRecordingSearchType getSearch() {
		return getPropertyValueObject(Props.SEARCH);
	}
	
	public void setSearch(IProgramRecordingSearchType search) {
		setPropertyValueObject(Props.SEARCH,search);
	}
	
	/* ========================== PROGRAM + SERIES ID ========================== */
	
	public String getSeriesID() {
		return getPropertyValueObject(Props.SERIES_ID);
	}
	
	public void setSeriesID(String seriesId) {
		setPropertyValueObject(Props.SERIES_ID, seriesId);
	}
	
	public String getProgramID() {
		return getPropertyValueObject(Props.PROGRAM_ID);
	}
	
	public void setProgramID(String programId) {
		setPropertyValueObject(Props.PROGRAM_ID, programId);
	}
	
	/* ========================== START + END OFFSET ========================== */
	
	public Integer getStartOffset() {
		return this.getPropertyValueObject(Props.START_OFFSET);
	}
	
	public void setStartOffset(Integer startOffset) {
		this.setPropertyValueObject(Props.START_OFFSET,startOffset);
	}
	
	public Integer getEndOffset() {
		return this.getPropertyValueObject(Props.END_OFFSET);
	}
	
	public void setEndOffset(Integer endOffset) {
		this.setPropertyValueObject(Props.END_OFFSET,endOffset);
	}

	public int getDuration() {
		return EncodingUtils.getMinutesDiff(this.getStartDateTime(),this.getEndDateTime());
	}

	public int getMinutesAfterMidnight() {
		return EncodingUtils.getMinutesAfterMidnight(this.getStartDateTime());
	}

	public String getUniqueProgramId() {
		if(!isValid()) return null;
		
		return String.format(
			"%d_%2$tY%2$tm%2$td%2$tH%2$tM%2$tS",
			this.getChannelID(),
			this.getStartDateTime()
		);
	}

	public boolean isValid() {
		return (this.getStartDateTime() != null 
			&& this.getEndDateTime() != null 
			&& this.getChannelID() != null
			&& this.getTitle() != null
		);
	}
	
	/* ========================== OTHER SETTERS/GETTERS ========================== */
	public Integer getAutoExpire() {
		return getPropertyValueObject(Props.AUTOEXPIRE);
	}
	
	public void setAutoExpire(Integer autoExpire) {
		this.setPropertyValueObject(Props.AUTOEXPIRE,autoExpire);
	}
	
	public Integer getMaxEpisodes() {
		return getPropertyValueObject(Props.MAX_EPISODES);
	}
	
	public void setMaxEpisodes(Integer maxEpisodes) {
		this.setPropertyValueObject(Props.MAX_EPISODES,maxEpisodes);
	}
	
	public Integer getMaxNewest() {
		return getPropertyValueObject(Props.MAX_NEWEST);
	}
	
	public void setMaxNewest(Integer maxNewest) {
		this.setPropertyValueObject(Props.MAX_NEWEST,maxNewest);
	}
	
	public String getRecordingGroup() {
		return getPropertyValueObject(Props.REC_GROUP);
	}
	
	public void setRecordingGroup(String recordingGroup) {
		this.setPropertyValueObject(Props.REC_GROUP,recordingGroup);
	}
	
	public IProgramRecordingFilters getRecordingFilters() {
		return getPropertyValueObject(Props.FILTER);
	}
		
	public void setRecordingFilters(IProgramRecordingFilters filters) {
		this.setPropertyValueObject(Props.FILTER,filters);
	}
	
	/* ========================== UTILITY FUNCTIONS ========================== */
	@SuppressWarnings("deprecation")
	@Override
	public void onSaveDataItem() {
		if(this.getDatabaseVersion() >= DB_VERSION_1309.getVersion() && this.fallbackRecType != null) {
			final IProgramRecordingType currentRecType = this.fallbackRecType;
			
			if(this.fallbackRecType.hasEnum(
				IProgramRecordingType.Type.CHANNEL_RECORD,
				IProgramRecordingType.Type.TIMESLOT_RECORD,
				IProgramRecordingType.Type.WEEKSLOT_RECORD,
				IProgramRecordingType.Type.FIND_DAILY_RECORD,
				IProgramRecordingType.Type.FIND_WEEKLY_RECORD
			)) {
				IProgramRecordingType recType = currentRecType;
				IProgramRecordingFilters filters = this.getRecordingFilters();
				Integer channelId = getChannelID();
				String channelSign = getChannelSign();
				String title = getTitle();
				String subtitle = getSubtitle();
				String description = getDescription();
				IProgramRecordingSearchType searchType = getSearch();			
				
				if(recType.hasEnum(IProgramRecordingType.Type.CHANNEL_RECORD)) {
					// Fallback to ALL_RECORD and the 'This channel' filter.				
					recType = ProgramRecordingType.valueOf(this.getVersionNr(),IProgramRecordingType.Type.ALL_RECORD);
					filters.set(Filters.THIS_CHANNEL);
				} else if(recType.hasEnum(IProgramRecordingType.Type.TIMESLOT_RECORD)) {
					// Fallback to ALL_RECORD and the 'This channel' and 'This time' filters.
					recType = ProgramRecordingType.valueOf(this.getVersionNr(),IProgramRecordingType.Type.ALL_RECORD);
					filters.set(Filters.THIS_CHANNEL);
					filters.set(Filters.THIS_TIME);
				} else if(recType.hasEnum(IProgramRecordingType.Type.WEEKSLOT_RECORD)) {
					// Fallback to ALL_RECORD and the channel filter.
					recType = ProgramRecordingType.valueOf(this.getVersionNr(),IProgramRecordingType.Type.ALL_RECORD);
					filters.set(Filters.THIS_CHANNEL);
					filters.set(Filters.THIS_DAY_AND_TIME);
				} else if(recType.hasEnum(IProgramRecordingType.Type.FIND_DAILY_RECORD)) {
					// Fallback to DAILY RECORD
					recType = ProgramRecordingType.valueOf(this.getVersionNr(),IProgramRecordingType.Type.DAILY_RECORD);
					searchType = ProgramRecordingSearchType.valueOf(getVersionNr(),IProgramRecordingSearchType.Type.POWER_SEARCH);
					channelId = 0;
					channelSign = "";
					
					subtitle = "";
					description = String.format("program.title = '%s'",title.replaceAll("'", "''"));
					title = String.format("%s (Power Search)",title);
				} else if(recType.hasEnum(IProgramRecordingType.Type.FIND_WEEKLY_RECORD)) {
					// Fallback to WEEKLY RECORD
					recType = ProgramRecordingType.valueOf(this.getVersionNr(),IProgramRecordingType.Type.WEEKLY_RECORD);
					searchType = ProgramRecordingSearchType.valueOf(getVersionNr(),IProgramRecordingSearchType.Type.POWER_SEARCH);
					channelId = 0;
					channelSign = "";
					
					subtitle = "";
					description = String.format("program.title = '%s'",title.replaceAll("'", "''"));
					title = String.format("%s (Power Search)",title);			
				}
				
				this.setSearch(searchType);
				this.setChannelID(channelId);
				this.setChannelSign(channelSign);
				this.setTitle(title);
				this.setSubtitle(subtitle);
				this.setDescription(description);
				
				logger.warning(String.format(
					"Scheduling rule %d uses a deprecated type %s. Changing properties to new style:" +
					"\r\nRecording-Type: %s" +
					"\r\nTitle: %s" +
					"\r\nSubtitle: %s" +
					"\r\nDescription: %s" +
					"\r\nChannel-ID: %d" +
					"\r\nChannel-Sign: %s" +
					"\r\nFilters: %s",
					getRecordingId(), currentRecType,
					recType, title, subtitle, description, channelId, channelSign,filters
				));
			}
		}	
	}
	
	
	public static Schedule valueOf(
		ProtocolVersion protoVersion, int dbVersion, IRecorderNextProgramInfo nextProgramInfo
	) {
		return valueOf(protoVersion, dbVersion, nextProgramInfo, IProgramRecordingType.Type.SINGLE_RECORD);
	}
	
	public static Schedule valueOf(
		ProtocolVersion protoVersion, int dbVersion, IRecorderNextProgramInfo nextProgramInfo, IProgramRecordingType.Type recordingType
	) {
		if(protoVersion == null) throw new NullPointerException("Protocol version was null.");
		else if(nextProgramInfo == null) throw new NullPointerException("Program info was null.");
		else if(!nextProgramInfo.isValid()) throw new IllegalArgumentException("Program info is invalid.");
		
		// create a new schedule object
		final Schedule schedule = new Schedule(protoVersion,dbVersion);
		
		// TITLE / SUBTITLE
		schedule.setTitle(nextProgramInfo.getTitle());
		schedule.setSubtitle(nextProgramInfo.getSubtitle());
		
		// DESCRIPTION
		schedule.setDescription(nextProgramInfo.getDescription());
		
		// CHANNEL ID + CALL SIGN
		schedule.setChannel(nextProgramInfo);
		
		// RECORDING TYPE
		if(recordingType == null) recordingType = IProgramRecordingType.Type.SINGLE_RECORD;
		final ProgramRecordingType programRecordingType = ProgramRecordingType.valueOf(protoVersion,recordingType);
		schedule.setRecordingType(programRecordingType);
		
		// START DATE / END DATE
		schedule.setStartDateTime(nextProgramInfo.getStartDateTime());
		schedule.setEndDateTime(nextProgramInfo.getEndDateTime());
		
		// PRIORITY
		schedule.setRecordingPriority(0);

		// CATEGORY
		schedule.setCategory(nextProgramInfo.getCategory());
		
		// SERIES + PROGRAM ID
		schedule.setSeriesID(nextProgramInfo.getSeriesID());
		schedule.setProgramID(nextProgramInfo.getProgramID());
		
		// FIND DAY + TIME + ID
		schedule.setFindId(nextProgramInfo.getStartDateTime());
		schedule.setFindDay(nextProgramInfo.getStartDateTime());
		schedule.setFindTime(nextProgramInfo.getStartDateTime());
		
		// SEARCH
		schedule.setSearch(ProgramRecordingSearchType.valueOf(protoVersion,Type.NO_SEARCH));		
		
		return schedule;		
	}
}
