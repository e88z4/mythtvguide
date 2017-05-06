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


import static org.jmythapi.protocol.ProtocolVersion.*;
import static org.jmythapi.protocol.response.IProgramInfo.Props.*;

import java.util.Date;
import java.util.List;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.*;
import org.jmythapi.utils.EncodingUtils;
import org.jmythapi.utils.GenericEnumUtils;

public class ProgramInfo extends AProgramInfo<IProgramInfo.Props> implements IBasicChannelInfo, IBasicInputInfo, IProgramInfo {
	
	/**
	 * Constructs a program-info object from a MythTv-protocol packet.
	 * 
	 * @param packet
	 * 		the protocol packet received over network.
	 */
	public ProgramInfo(IMythPacket packet) {
		super(IProgramInfo.Props.class, packet);
	}
	
	/**
	 * Constructs a program-info object for the given protocol version with the 
	 * given parameters.
	 * 
	 * @param protoVersion
	 * 		the protocol version to use
	 * @param responseArgs
	 * 		the program-info arguments
	 */
	public ProgramInfo(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion, IProgramInfo.Props.class, responseArgs);
	}
	
	/**
	 * Constructs an empty program-info object for the given protocol version.
	 * 
	 * @param protoVersion
	 * 		the protocol version to use
	 */
	public ProgramInfo(ProtocolVersion protoVersion) {
		super(protoVersion, IProgramInfo.Props.class);
	}
	
	public String getUniqueRecordingId() {
		if(!isValid()) return null;
		
		return String.format(
			"%d_%2$tY%2$tm%2$td%2$tH%2$tM%2$tS",
			this.getChannelID(),
			this.getRecordingStartTime()
		);
	}	
	
	/* ======================================================================
	 * IBasicProgramInfo Methods
	 * ====================================================================== */	
	
	@Override
	public String getTitle() {
		return this.getPropertyValueObject(TITLE);
	}	
	
	@Override
	public String getSubtitle() {
		return this.getPropertyValueObject(SUBTITLE);
	}
	
	@Override
	public String getDescription() {
		return this.getPropertyValueObject(DESCRIPTION);
	}
	
	@Override
	public String getCategory() {
		return this.getPropertyValueObject(CATEGORY);
	}
	
	@Override
	public Date getStartDateTime() {
		return this.getPropertyValueObject(START_DATE_TIME);
	}
	
	@Override
	public Date getEndDateTime() {
		return this.getPropertyValueObject(END_DATE_TIME);
	}	
	
	@Override
	public String getSeriesID() {
		return this.getPropertyValueObject(SERIES_ID);
	}
	
	@Override
	public String getProgramID() {
		return this.getPropertyValueObject(PROGRAM_ID);
	}	
	
	/* ======================================================================
	 * IBasicChannelInfo Methods
	 * ====================================================================== */	
		
	@Override
	public Integer getChannelID() {
		return this.getPropertyValueObject(CHANNEL_ID);
	}	
	
	@Override
	public String getChannelSign() {
		return this.getPropertyValueObject(CHANNEL_SIGN);
	}	
	
	@Override
	public String getChannelNumber() {
		return this.getPropertyValueObject(CHANNEL_NUMBER);
	}		
	
	/* ======================================================================
	 * Additional Methods
	 * ====================================================================== */
	public Integer getFindID() {
		return this.getPropertyValueObject(FIND_ID);
	}
	
	public Integer getParentID() {
		return this.getPropertyValueObject(PARENT_ID);
	}
	
	public Integer getSeason() {
		return this.getPropertyValueObject(SEASON);
	}
	
	public Integer getEpisode() {
		return this.getPropertyValueObject(EPISODE);
	}

	public Integer getTotalEpisodes() { return this.getPropertyValueObject(TOTALEPISODES);}
	
	public String getSyndicatedEpisode() {
		return this.getPropertyValueObject(SYNDICATED_EPISODE);
	}
	
	public String getInetRef() {
		return this.getPropertyValueObject(Props.INETREF);
	}	
	
	public String getChannelName() {
		return this.getPropertyValueObject(CHANNEL_NAME);
	}
	
	public String getChannelOuputFilters() {
		return this.getPropertyValueObject(CHANNEL_OUTPUT_FILTERS);
	}
	
	public String getPathName() {
		return this.getPropertyValueObject(PATH_NAME);
	}
	
	public String getBaseName() {
		final String pathName = this.getPathName();
		if(pathName == null) return null;
		
		int idx = pathName.lastIndexOf('/');
		final String baseName = (idx == -1) ? pathName : pathName.substring(idx+1);
		return baseName;
	}

	public String getPreviewImageName() {
		final String pathName = this.getBaseName();
		if(pathName == null || pathName.length() == 0) return null;
		return pathName + ".png";
	}	
	
	public String getHostName() {
		return this.getPropertyValueObject(HOSTNAME);
	}	
	
	@SuppressWarnings("deprecation")
	public Long getFileSize() {
		if (this.getVersionNr().compareTo(PROTO_VERSION_57)>=0) {
			// as of MYTHTV_PROTO_VERSION >= 57
			return this.getPropertyValueObject(FILESIZE);
		} else {
			final String size1 = this.getPropertyValue(FILESIZE_HIGH);
			if (size1 == null || size1.length() == 0) return null;
			final String size2 = this.getPropertyValue(FILESIZE_LOW);
			if (size2 == null || size2.length() == 0) return null;
			return Long.valueOf(EncodingUtils.decodeLong(size1, size2));
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setFileSize(Long fileSize) {
		if(getVersionNr().compareTo(PROTO_VERSION_57)>=0) {
			this.setPropertyValueObject(FILESIZE,fileSize);
		} else {
			final String[] fileSizeParts = EncodingUtils.encodeLong(fileSize);
			this.setPropertyValueObject(FILESIZE_HIGH,fileSizeParts[0]);
			this.setPropertyValueObject(FILESIZE_LOW,fileSizeParts[1]);
		}		
	}
	
	public Date getOriginalAirdate() {
		return this.getPropertyValueObject(ORIGINAL_AIRDATE);
	}	
	
	public Date getRecordingStartTime() {
		return this.getPropertyValueObject(REC_START_TIME);
	}
	
	public Date getRecordingEndTime() {
		return this.getPropertyValueObject(REC_END_TIME);
	}
	
	public int getRecordingDuration() {
		final Date recStart = this.getRecordingStartTime();
		final Date recEnd = this.getRecordingEndTime();
		return EncodingUtils.getMinutesDiff(recStart, recEnd);
	}
	
	public int getRecordingStartOffset() {
		final Date recStart = this.getRecordingStartTime();
		final Date start = this.getStartDateTime();
		return EncodingUtils.getMinutesDiff(recStart, start);
	}
	
	public int getRecordingEndOffset() {
		final Date recEnd = this.getRecordingEndTime();
		final Date end = this.getEndDateTime();
		return EncodingUtils.getMinutesDiff(end,recEnd);		
	}
	
	public int getRecordingElapsedMinutes() {
		final Date now = new Date();
		final Date recStartDate = this.getRecordingStartTime();
		final Date recEndDate = this.getRecordingEndTime();
		
		if(now.before(recStartDate)) {
			return 0;
		} else if(now.after(recEndDate)) {
			return getRecordingDuration();
		} else {
			return EncodingUtils.getMinutesDiff(recStartDate,now);
		}
	}
	
	public int getRecordingElapsedPercent() {
		final int recLength = this.getRecordingDuration();
		if(recLength == -1) return 0;
		final int elapsedMinutes = this.getRecordingElapsedMinutes();
		final int donePercent = elapsedMinutes * 100 / recLength;
		return donePercent;
	}
	
	public Integer getRecordingPriority() {
		return this.getPropertyValueObject(REC_PRIORITY);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_25)
	public Integer getRecordingPriority2() {
		return this.getPropertyValueObject(REC_PRIORITY2);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public String getRecordingGroup() {
		return this.getPropertyValueObject(REC_GROUP);
	}
	
	public String getPlayGroup() {
		return this.getPropertyValueObject(PLAY_GROUP);
	}
	
	public String getStorageGroup() {
		return this.getPropertyValueObject(STORAGE_GROUP);
	}
	
	public ProgramFlags getProgramFlags() {
		return this.getPropertyValueObject(PROGRAM_FLAGS);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	public IProgramAudioProperties getAudioProperties() {
		return this.getPropertyValueObject(AUDIO_PROPERTIES);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	public IProgramVideoProperties getVideoProperties() {
		return this.getPropertyValueObject(VIDEO_PROPERTIES);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	public IProgramSubtitleType getSubtitleType() {
		return this.getPropertyValueObject(SUBTITLE_TYPE);
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_79)
	public IProgramCategoryType getCategoryType() { return this.getPropertyValueObject(CATEGORYTYPES);}
	
	public ProgramRecordingStatus getRecordingStatus() {
		return this.getPropertyValueObject(REC_STATUS);
	}
	
	public ProgramRecordingType getRecordingType() {
		return this.getPropertyValueObject(REC_TYPE);
	}
	
	public IProgramRecordingDupInType getRecordingDuplicationType() {
		return this.getPropertyValueObject(Props.DUP_IN);
	}
	
	public IProgramRecordingDupMethodType getRecordingDuplicationMethod() {
		return this.getPropertyValueObject(Props.DUP_METHOD);
	}
	
	public Integer getYear() {
		return this.getPropertyValueObject(YEAR);
	}
	
	public Integer getRecordingId() {
		return this.getPropertyValueObject(REC_ID);
	}
	
	@SuppressWarnings("deprecation")
	public Float getTimeStretch() {
		return this.getPropertyValueObject(TIMESTRETCH);
	}
	
	public Float getStars() {
		return this.getPropertyValueObject(STARS);
	}
	
	public Date getLastModified() {
		return this.getPropertyValueObject(LAST_MODIFIED);
	}
	
	public Integer getPartNumber() {
		return this.getPropertyValueObject(PART_NUMBER);
	}
	
	public Integer getPartTotal() {
		return this.getPropertyValueObject(PART_TOTAL);
	}
	
	/* ===============================================================================
	 * various FLAG methods
	 * ===============================================================================*/
	private Boolean isFlagSet(Props flag) {
		final Boolean flagValue = this.getPropertyValueObject(flag);
		if (flagValue == null) return null;
		return flagValue;
	}
	
	private <E extends Enum<E> & IFlag> Boolean isFlagSet(AEnumFlagGroup<E> flagGroup, E flag) {
		if (flagGroup == null) return Boolean.FALSE;
		else if (flag == null) return Boolean.FALSE;
		return flagGroup.isSet(flag);
	}
	
	/**
	 * @deprecated {@mythProtoVersion 57}, 
	 * 		       use {@link IProgramFlags#isSet(org.jmythapi.protocol.response.IProgramFlags.Flags)} instead.
	 */
	public Boolean isDuplicate() {
		return this.protoVersion.compareTo(PROTO_VERSION_57)<0 
			?isFlagSet(Props.DUPLICATE)
			:isFlagSet(this.getProgramFlags(),IProgramFlags.Flags.FL_DUPLICATE);
	}
	
	/**
	 * @deprecated {@mythProtoVersion 57}
	 */
	public Boolean isShareable() {
		return this.protoVersion.compareTo(PROTO_VERSION_57)<0 
			?isFlagSet(Props.SHAREABLE)
			:null;
	}
	
	/**
	 * @deprecated {@mythProtoVersion 57}, use {@link ProgramFlags#isSet(org.jmythapi.protocol.response.IProgramFlags.Flags)} instead.
	 */
	public Boolean isRepeat() {
		return this.protoVersion.compareTo(PROTO_VERSION_57)<0 
			?isFlagSet(Props.REPEAT)
			:isFlagSet(this.getProgramFlags(),IProgramFlags.Flags.FL_REPEAT);
	}
	
	/**
	 * @deprecated {@mythProtoVersion 57}, use {@link ProgramFlags#isSet(org.jmythapi.protocol.response.IProgramFlags.Flags)} instead.
	 */
	public Boolean isChannelCommFree() {
		if (this.protoVersion.compareTo(PROTO_VERSION_03)<0) {
			return Boolean.FALSE;
		} else if (this.protoVersion.compareTo(PROTO_VERSION_57)<0) {
			return isFlagSet(Props.CHAN_COMM_FREE);
		} else {
			return isFlagSet(this.getProgramFlags(),IProgramFlags.Flags.FL_CHANCOMMFREE);
		}
	}
	
	/**
	 * @deprecated {@mythProtoVersion 57}
	 */	
	public Boolean hasAirDate() {
		if (this.protoVersion.compareTo(PROTO_VERSION_12)<0) {
			return Boolean.FALSE;
		} else if (this.protoVersion.compareTo(PROTO_VERSION_57)<0) {
			return isFlagSet(Props.HAS_AIRDATE);
		} else {
			final String airDate = this.getPropertyValue(ORIGINAL_AIRDATE);
			return airDate != null && airDate.length() > 0;
		}
	}
	
	/* ===============================================================================
	 * INPUT INFO Methods
	 * ===============================================================================*/
	public Integer getCardID() {
		return this.getPropertyValueObject(CARD_ID);
	}

	public Integer getInputID() {
		return this.getPropertyValueObject(INPUT_ID);
	}

	public Integer getSourceID() {
		return this.getPropertyValueObject(SOURCE_ID);
	}
	
	public static ProgramInfo valueOf(RecorderNextProgramInfo nextProgram, InputInfoFree input) {		
		// create the target object
		final ProgramInfo programInfo = new ProgramInfo(nextProgram.getVersionNr());
		
		// copy properties
		GenericEnumUtils.copyEnumValues(nextProgram,programInfo);
		GenericEnumUtils.copyEnumValues(input,programInfo);
		
		return programInfo;
	}

	public int getRecordedId() { return this.getPropertyValueObject(RECORDEDID);}

	public String getInputName() {return this.getPropertyValueObject(INPUTNAME);}

	public Date getBookmarkUpdate() { return this.getPropertyValueObject(BOOKMARKUPDATE); }
}
