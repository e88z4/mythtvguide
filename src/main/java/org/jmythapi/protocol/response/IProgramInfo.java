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
package org.jmythapi.protocol.response;

import static org.jmythapi.protocol.ProtocolConstants.PLAY_GROUP_DEFAULT;
import static org.jmythapi.protocol.ProtocolConstants.RECORDING_GROUP_DEFAULT;
import static org.jmythapi.protocol.ProtocolConstants.STORAGE_GROUP_DEFAULT;
import static org.jmythapi.protocol.ProtocolVersion.*;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import java.util.Date;
import java.util.List;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IPropertyAware;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.database.ISchedule;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythParameterDefaultValue;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.response.impl.ProgramAudioProperties;
import org.jmythapi.protocol.response.impl.ProgramFlags;
import org.jmythapi.protocol.response.impl.ProgramRecordingDupInType;
import org.jmythapi.protocol.response.impl.ProgramRecordingDupMethodType;
import org.jmythapi.protocol.response.impl.ProgramRecordingStatus;
import org.jmythapi.protocol.response.impl.ProgramRecordingType;
import org.jmythapi.protocol.response.impl.ProgramSubtitleType;
import org.jmythapi.protocol.response.impl.ProgramVideoProperties;
import org.jmythapi.protocol.response.impl.ProgramCategoryType;
import org.jmythapi.protocol.utils.MythWebUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * An interface to get infos about a MythTV recording.
 * <p>
 * This object represents a finished, running, pending or scheduled MythTV recording. Depending on the type of a recording 
 * and on the MythTV version, different recording properties may be returned.<br>
 * This interface is {@link IPropertyAware property-aware}. See the {@link IProgramInfo.Props properties-list} for all properties of this object.
 * <p>
 * See the <a href='#response'>response examples</a> on how a returned recording looks like.<br>
 * See the <a href='#usage'>usage examples</a> on how to fetch the different types of recording lists.<br>
 * 
 * <h3><a name='response'>A response example:</a></h3>
 * 
 * An example which data is returned for a recording:
 * 
 * {@mythResponseExample <pre> 
 * 		Protocol Version 56 example:
 * 		<00>TITLE: Fringe - Grenzfälle des FBI | <01>SUBTITLE: Kontrolle | <02>DESCRIPTION: Der Sohn eines hochrangigen Massive Dynamic-Mitarbeiters wird entführt... | <03>CATEGORY:  | <04>CHANNEL_ID: 11123 | <05>CHANNEL_NUMBER: 10123 | <06>CHANNEL_SIGN: PULS 4 | <07>CHANNEL_NAME: PULS 4 | <08>PATH_NAME: /var/lib/mythtv/recordings/11123_20110401233600.mpg | <09>FILESIZE_HIGH: 0 | <10>FILESIZE_LOW: 0 | <11>START_DATE_TIME: Fri Apr 01 23:04:58 CEST 2011 | <12>END_DATE_TIME: Fri Apr 01 23:58:24 CEST 2011 | <13>DUPLICATE: false | <14>SHAREABLE: false | <15>FIND_ID: 0 | <16>HOSTNAME: brain | <17>SOURCE_ID: 1 | <18>CARD_ID: 1 | <19>INPUT_ID: 1 | <20>REC_PRIORITY: 1 | <21>REC_STATUS: -2=> &#123;RECORDING&#125; | <22>REC_ID: 330 | <23>REC_TYPE: 1=> &#123;SINGLE_RECORD&#125; | <24>DUP_IN: 15=> &#123;DUPS_IN_RECORDED,DUPS_IN_OLD_RECORDED,DUPS_IN_ALL&#125; | <25>DUP_METHOD: 6=> &#123;DUP_CHECK_SUB,DUP_CHECK_DESC,DUP_CHECK_SUB_DESC&#125; | <26>REC_START_TIME: Fri Apr 01 23:36:00 CEST 2011 | <27>REC_END_TIME: Fri Apr 01 23:58:24 CEST 2011 | <28>REPEAT: false | <29>PROGRAM_FLAGS: 0=> &#123;&#125; | <30>REC_GROUP: Default | <31>CHAN_COMM_FREE: false | <32>CHANNEL_OUTPUT_FILTERS:  | <33>SERIES_ID:  | <34>PROGRAM_ID:  | <35>LAST_MODIFIED: Fri Apr 01 23:35:39 CEST 2011 | <36>STARS: 0.000000 | <37>ORIGINAL_AIRDATE:  | <38>HAS_AIRDATE: false | <39>PLAY_GROUP: Default | <40>REC_PRIORITY2: 0 | <41>PARENT_ID: 0 | <42>STORAGE_GROUP: Default | <43>AUDIO_PROPERTIES: 1=> &#123;AUD_STEREO&#125; | <44>VIDEO_PROPERTIES: 0=> &#123;&#125; | <45>SUBTITLE_TYPE: 0=> &#123;&#125; | <46>YEAR: 0
 * 
 *		Protocol Version 15 example: 	
 * 		<00>TITLE: Malcolm mittendrin | <01>SUBTITLE:  | <02>DESCRIPTION: Malcolm verteidigt Reese Serie mit Bryan Cranston, Christopher Masterson Programminformation nur noch bis Ende Dezember 2010 | <03>CATEGORY: Serie | <04>CHANNEL_ID: 1000 | <05>CHANNEL_NUMBER: 1 | <06>CHANNEL_SIGN: PRO7 | <07>CHANNEL_NAME: Pro7 | <08>PATH_NAME:  | <09>FILESIZE_HIGH: 0 | <10>FILESIZE_LOW: 0 | <11>START_DATE_TIME: Thu Apr 14 10:15:00 CEST 2011 | <12>END_DATE_TIME: Thu Apr 14 10:45:00 CEST 2011 | <13>DUPLICATE:  | <14>SHAREABLE: false | <15>FIND_ID: 0 | <16>HOSTNAME:  | <17>SOURCE_ID: 1 | <18>CARD_ID: 1 | <19>INPUT_ID: 1 | <20>REC_PRIORITY: 0 | <21>REC_STATUS: -2=> &#123;RECORDING&#125; | <22>REC_ID: 1 | <23>REC_TYPE: 1=> &#123;SINGLE_RECORD&#125; | <24>DUP_IN: 15=> &#123;DUPS_IN_RECORDED,DUPS_IN_OLD_RECORDED,DUPS_IN_BOTH,DUPS_IN_ALL,DUPS_NEW_EPI&#125; | <25>DUP_METHOD: 6=> &#123;DUP_CHECK_SUB,DUP_CHECK_DESC,DUP_CHECK_SUB_DESC&#125; | <26>REC_START_TIME: Thu Apr 14 10:39:00 CEST 2011 | <27>REC_END_TIME: Thu Apr 14 10:45:00 CEST 2011 | <28>REPEAT: false | <29>PROGRAM_FLAGS: 0=> &#123;&#125; | <30>REC_GROUP: Default | <31>CHAN_COMM_FREE: false | <32>CHANNEL_OUTPUT_FILTERS:  | <33>SERIES_ID: 227250734 | <34>PROGRAM_ID:  | <35>LAST_MODIFIED: Thu Apr 14 10:39:19 CEST 2011 | <36>STARS: 0.0 | <37>ORIGINAL_AIRDATE: Thu Apr 14 02:00:00 CEST 2011 | <38>HAS_AIRDATE: false
 * }
 * 
 * <h3><a name="usage">Usage examples</a></h3>
 * 
 * Examples how to get a list of recordings from the backend:
 * 
 * <ul>
 * <li>Get all recorded programs ({@link IBackend#queryRecordings() link})</li>
 * <li>Get all pending recordings ({@link IBackend#queryAllPending() link})</li>
 * <li>Get all scheduled recordings ({@link IBackend#queryAllScheduled() link})</li>
 * <li>Get all conflicting recordings ({@link IBackend#queryConflicting() link})</li>
 * <li>Get all expiring recordings ({@link IBackend#queryExpiring() link})</li>
 * </ul>
 * 
 * Examples how to operate on a list of recordings:
 * <ul>
 * <li>Filter recordings ({@link IProgramInfoList#filter link})</li>
 * <li>Group recordings ({@link IProgramInfoList#groupBy link})</li>
 * </ul>
 * 
 * 
 * Examples how to get the data of a single recording from the backend:
 * <ul>
 * <li>Get a recording by its file name ({@link IBackend#queryRecording(String) link})</li>
 * <li>Get a recording by its channel and recording start-time ({@link IBackend#queryRecording(IBasicChannelInfo, Date) link})</li>
 * <li>Get the current recording of a recorder ({@link IRecorder#getCurrentRecording() link})</li>
 * </ul>
 * 
 * Examples how to operate on a given recording:
 * <ul>
 * <li>Download a recording ({@link IBackend#annotateFileTransfer(IProgramInfo) link})</li>
 * <li>Delete a recording ({@link IBackend#deleteRecording(IProgramInfo) link})</li>
 * <li>Undelete a recording ({@link IBackend#undeleteRecording(IProgramInfo) link})</li>
 * <li>Check if a recording is currently being recorded ({@link IBackend#checkRecording(IProgramInfo)  link})</li>
 * <li>Determine the recorder, that is currently recording the recording ({@link IBackend#getRecorderForProgram(IProgramInfo) link})</li>
 * <li>Stop a currently active recording ({@link IBackend#stopRecording(IProgramInfo) link})</li> 
 * <li>Queries the bookmark for a recording ({@link IBackend#queryBookmark(IProgramInfo) link})</li>
 * <li>Set the bookmark for a recording ({@link IBackend#setBookmark(IProgramInfo, Long) link})</li>
 * <li>Update the file-name and -size for a recording ({@link IBackend#fillProgramInfo(IProgramInfo) link})</li>
 * <li>Check if a recording file exists ({@link IBackend#queryCheckFile(IProgramInfo, Boolean) link})</li>
 * <li>Generates a unique hash for a recording file ({@link IBackend#queryFileHash(IProgramInfo) link})</li>
 * <li>Generate a preview image for the recording ({@link IBackend#queryGenPixmap(IProgramInfo) link})</li>
 * <li>Check if the preview image for the recording has been modified ({@link IBackend#queryPixmapLastModified(IProgramInfo) link})</li>
 * <li>Get the URL to the Mythweb page of the recording ({@link MythWebUtils#getProgramDetailUrl(IProgramInfo) link})</li>
 * </ul>
 */
public interface IProgramInfo extends IBasicChannelInfo, IBasicInputInfo, IBasicProgramInfo, IVersionable, IPropertyAware<IProgramInfo.Props> {

	/**
	 * The properties of an {@link IProgramInfo} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/blob/master/mythtv/libs/libmyth/programinfo.cpp">programinfo.cpp: ProgramInfo::ToStringList(..)</a>
	 */
	public static enum Props {
		/** 
		 * The recording title.
		 * <p>
		 * E.g. {@code Malcolm mittendrin}
		 * 
		 * @see IProgramInfo#getTitle() 
		 */
		@MythParameterType(String.class)
		TITLE,
		
		/** 
		 * The recording sub-title.
		 * <p>
		 * E.g. {@code Victors zweite Familie}
		 * <p>
		 * For a manually scheduled recording the subtitle
		 * is always set to the program-start-time by the MythTV scheduler.
		 * 
		 * @see IProgramInfo#getSubtitle() 
		 */
		@MythParameterType(String.class)
		SUBTITLE,
		
		/**
		 * A description about the recording.
		 * <p>
		 * E.g. {@code Als Lois erfährt, dass ihr Vater eine zweite Familie hatte, beschließt sie kurzerhand...}
		 * 
		 * @see IProgramInfo#getDescription() 
		 */
		@MythParameterType(String.class)
		DESCRIPTION,
		
		/**
		 * The season number of the recording.
		 * 
		 * @see IProgramInfo#getSeason()
		 * 
		 * @since {@mythProtoVersion 67}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_67)
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		SEASON,

		/**
		 * The episode number of the recording.
		 * 
		 * @see IProgramInfo#getEpisode()
		 * 
		 * @since {@mythProtoVersion 67}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_67)
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		EPISODE,

		/**
		 * @since{@mythProtoVersion 78}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_78)
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		TOTALEPISODES,
		
		/**
		 * The syndicated episode of the recording.
		 * 
		 * @see IProgramInfo#getSyndicatedEpisode()
		 * 
		 * @since {@mythProtoVersion 76}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_76)
		@MythParameterType(String.class)
		@MythParameterDefaultValue("0")
		SYNDICATED_EPISODE,
		
		/** 
		 * The category of the recording.
		 * <p>
		 * E.g. {@code comedy}
		 * 
		 * @see IProgramInfo#getCategory() 
		 */
		@MythParameterType(String.class)
		CATEGORY,
		
		/** 
		 * The channel ID.
		 * <p>
		 * The unique id of the channel: e.g. {@code 1001}.
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_ID}.
		 * 
		 * @see IProgramInfo#getChannelID()
		 */
		@MythParameterType(Integer.class)
		CHANNEL_ID,
		
		/** 
		 * The channel number.
		 * <p>
		 * The user defined number of the channel: e.g. {@code 2}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_NUMBER}.
		 * 
		 * @see IProgramInfo#getChannelNumber()
		 */
		CHANNEL_NUMBER,
		
		/** 
		 * The channel abbreviation.
		 * <p>
		 * E.g. {@code ORF1}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_SIGN}.
		 * 
		 * @see IProgramInfo#getChannelSign()
		 */
		CHANNEL_SIGN,
		
		/** 
		 * The channel name.
		 * <p>
		 * The full name of the channel: e.g. {@code ORF 1}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_NAME}.
		 * 
		 * @see IProgramInfo#getChannelName() 
		 */
		CHANNEL_NAME,
		
		/** 
		 * The recording file.
		 * <p>
		 * E.g. {@code myth://127.0.0.1:6543/1000_20100512085000.mpg} or
		 * E.g. {@code /var/lib/mythtv/livetv/1004_20120113085933.mpg}
		 * 
		 * @see IProgramInfo#getPathName() 
		 */
		PATH_NAME,
		
		/** 
		 * First part of the file size.
		 * <p>
		 * High 32bit of the file-size.
		 *  
		 * @see IProgramInfo#getFileSize()
		 * @deprecated {@mythProtoVersion 57}, replaced by {@link #FILESIZE}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
		FILESIZE_HIGH,
		
		/**
		 * Second part of the file size.
		 * <p> 
		 * Low 32bit of the file-size.
		 * 
		 * @see IProgramInfo#getFileSize()
		 * @deprecated {@mythProtoVersion 57}, replaced by {@link #FILESIZE}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
		FILESIZE_LOW,
		
		/**
		 * The recording file size.
		 * <p>
		 * This is a replacement for {@link #FILESIZE_LOW} and {@link #FILESIZE_HIGH}.
		 * 
		 * @see IProgramInfo#getFileSize()
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FILESIZE,
		
		/** 
		 * The scheduled start of the program.
		 * @see IProgramInfo#getStartDateTime() 
		 */
		@MythParameterType(Date.class)
		START_DATE_TIME,
		
		/** 
		 * The scheduled end of the program.
		 * @see IProgramInfo#getEndDateTime() 
		 */
		@MythParameterType(Date.class)
		END_DATE_TIME,
		
		/** 
		 * Marks a recording as duplicate.
		 * <p>
		 * <b>Note:</b><br>
		 * In protocol {@mythProtoVersion 04} this was the {@code conflicting} property?
		 * 
		 * @see IProgramInfo#isDuplicate()
		 * 
		 * @deprecated {@mythProtoVersion 57}, 
		 * 		replaced by {@link IProgramFlags.Flags#FL_DUPLICATE}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
		@MythParameterType(Boolean.class)
		DUPLICATE,
		
		/** 
		 * Shareable card inputs.
		 * <p>
		 * Signals that the used card input can be shared, e.g. on 
		 * DVB cards. Till protocol version {@mythProtoVersion 57} this information was
		 * used by the scheduler.
		 * <p>
		 * 
		 * <b>Note:</b><br>
		 * In protocol {@mythProtoVersion 04} this was the {@code recording}.
		 * 
		 * @see IProgramInfo#isShareable()
		 * @deprecated {@mythProtoVersion 57}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
		@MythParameterType(Boolean.class)
		SHAREABLE,
		
		/** 
		 * The find-id.
		 * <p>
		 * 
		 * <b>Note:</b> <br>
		 * According to the MythTV <a href="http://www.mythtv.org/wiki/Record_table">wiki</a>, 
		 * this is calculated like this:
	     * <code>(UNIX_TIMESTAMP(program.starttime)/60/60/24)+719528</code>
		 * <p>
		 * 
		 * <b>Note:</b><br>
		 * In protocol {@mythProtoVersion 04}, this was the {@code override} property.
		 * 
		 * @see IProgramInfo#getFindID()
		 */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("0")
		FIND_ID,
		
		/** 
		 * The recording host.
		 * <p> 
		 * @see IProgramInfo#getHostName()
		 */
		HOSTNAME,
		
		/** 
		 * The source ID. 
		 * <p>
		 * This is usually the first digit of the channel ID.
		 * 
		 * @see IProgramInfo#getSourceID()
		 */
		@MythParameterType(Integer.class)
		SOURCE_ID,
		
	    /** 
	     * The ID of a physical capture card.
	     * <p>
	     * This is the ID of the recorder that will record a scheduled program.
	     * <br>
	     * The card-is is {@code 0} for an already recorded program.
	     * 
	     * @see IProgramInfo#getCardID()
	     */
		@MythParameterType(Integer.class)
		CARD_ID,
		
	    /** 
	     * The ID of a specific input.
	     * <br>
	     * The card-is is {@code 0} for an already recorded program.
	     * 
	     * @see IProgramInfo#getInputID()
	     */
		@MythParameterType(Integer.class)
	    INPUT_ID,
	    
	    /** 
	     * The recording priority.
	     * <p>
	     * Values are between {@code -20} and {@code 20}.
	     * 
	     * @see IProgramInfo#getRecordingPriority()
	     */
	    @MythParameterType(Integer.class)
	    REC_PRIORITY,
	    
	    /** 
	     * The current recording status of the {@link IRemoteEncoder encoder}.
	     * <p>
	     * This is similar to {@link IRemoteEncoder#getRecordingStatus()}.
	     * <br>
	     * See {@link IProgramRecordingStatus.Status here} for all available status types.
	     * 
	     * @see IProgramRecordingStatus 
	     * @see IProgramInfo#getRecordingStatus()
	     * @see IRemoteEncoder#getRecordingStatus()
	     */
	    @MythParameterType(ProgramRecordingStatus.class)
	    REC_STATUS,
	    
	    /** 
	     * The recording ID.
	     * <p>
		 * <b>ATTENTION:</b> This is the ID of the mythtv scheduling rule, 
		 * stored in the database. Therefore multiple recordings may have the same
		 * recording id!<br>
		 * If you require a unique id for a recording, use {@link IProgramInfo#getUniqueRecordingId()} instead.
	     * 
	     * @see IProgramInfo#getRecordingId()
	     */
	    @MythParameterType(Integer.class)
	    REC_ID,
	    
	    /** 
	     * The recording type.
	     * <p>
	     * E.g. if the program is recorded a single time or using a weekly time-slot, etc.
	     * <br>
	     * See {@link IProgramRecordingType.Type here} for all available types.
	     * 
	     * @see IProgramRecordingType 
	     * @see IProgramInfo#getRecordingType()
	     */
	    @MythParameterType(ProgramRecordingType.class)
	    REC_TYPE,
	    
	    /**
	     * The duplication type and mode of the recording.
	     * 
	     * @deprecated {@mythProtoVersion 03}, 
	     * 		replaced by {@link #DUP_IN} and {@link #DUP_METHOD}
	     */
	    @MythParameterType(Integer.class)
	    @MythProtoVersionAnnotation(to=PROTO_VERSION_03)
	    REC_DUPS,
	    
	    /** 
	     * The duplication type of the recording.
	     * <p>
	     * Specifies how to handle duplicates.
	     * <br>
	     * See {@link IProgramRecordingDupInType.Flags here} for all available types.
	     * 
	     * @see IProgramRecordingDupInType
	     * @see IProgramInfo#getRecordingDuplicationType()
	     *  
	     * @since {@mythProtoVersion 03}
	     */
	    @MythParameterType(ProgramRecordingDupInType.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	    DUP_IN,
	    
	    /** 
	     * The duplication mode to use for the recording.
	     * <p>
	     * Specifies how a check for duplicates is done.
	     * <br>
	     * See {@link IProgramRecordingDupMethodType.Flags here} for all available types.
	     * 
	     * @see IProgramRecordingDupMethodType
	     * @see IProgramInfo#getRecordingDuplicationMethod()
	     * 
	     * @mythProtoVersionRange
	     * @since {@mythProtoVersion 03}
	     */
	    @MythParameterType(ProgramRecordingDupMethodType.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	    DUP_METHOD,
	    
	    /** 
	     * Beginning of actual recording. 
	     * 
	     * @see IProgramInfo#getRecordingStartTime()
	     */
	    @MythParameterType(Date.class)
	    REC_START_TIME,
	    
	    /** 
	     * End of actual recording.
	     * 
	     * @see IProgramInfo#getRecordingEndTime() 
	     */
	    @MythParameterType(Date.class)
	    REC_END_TIME,
	    
	    /**
	     * Repeated program.
	     * <p>
	     * Specifies if this is a duplicate to a previously recorded program.
	     * 
	     * @see IProgramInfo#isRepeat()
	     * 
	     * @deprecated {@mythProtoVersion 57},
	     * 		replaced by {@link IProgramFlags.Flags#FL_REPEAT}
	     * 
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_00, fromInfo={
	    	@MythProtoVersionMetadata(key=GIT_COMMIT,value="1d8f02ada387b1f58cf089eca3542a3948a8b8de")
	    }, to=PROTO_VERSION_57)
	    @MythParameterType(Boolean.class)
	    REPEAT,
	    
	    /** 
	     * Program flags.
	     * <p>
	     * Additional information about the current state of the recording.
	     * <br>
	     * See {@link IProgramFlags.Flags here} for all available flags}.
	     *  
	     * @see IProgramInfo#getProgramFlags()
	     * @see IProgramFlags
	     */
	    @MythParameterType(ProgramFlags.class)
	    PROGRAM_FLAGS,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 3
	     * ========================================== */	    
	    
	    /** 
	     * Recording Groups.
	     * <p>
	     * A scheduled recording can have a group assigned.
	     * 
	     * @see IProgramInfo#getRecordingGroup()
	     * 
	     * @since {@mythProtoVersion 03}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	    @MythParameterDefaultValue(RECORDING_GROUP_DEFAULT)
	    REC_GROUP,
	    
	    /** 
	     * Commercial free status.
	     * <p>
	     * Indicates if the channel is commercial free.
	     * 
	     * @see IProgramInfo#isChannelCommFree()
	     * 
	     * @since {@mythProtoVersion 03}
	     * @deprecated {@mythProtoVersion 57}, 
	     * 		replaced by {@link IProgramFlags.Flags#FL_CHANCOMMFREE} 
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_03,to=PROTO_VERSION_57)
	    @MythParameterType(Boolean.class)
	    CHAN_COMM_FREE,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 6
	     * ========================================== */
	    
	    /** 
	     * Output filters.
	     * <p>
	     * Allows to specify a set of filters to be used when playing back recordings 
	     * from a given channel. This is useful for those with hardware encoders and 
	     * more than one source, since filters are rarely a one-size-fits-all thing.
	     * 
	     * @see IProgramInfo#getChannelOuputFilters()
	     * @since {@mythProtoVersion 06}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_06)
	    CHANNEL_OUTPUT_FILTERS,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 8
	     * ========================================== */
	    
	    /** 
	     * Series ID.
	     * <p>
	     * Is used for dup checks.
	     * 
	     * @since {@mythProtoVersion 08}
	     * @see IProgramInfo#getSeriesID()
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_08)
	    SERIES_ID,
	    
	    /** 
	     * Program ID.
	     * <p>
	     * Is used for dup checks.
	     * 
	     * @since {@mythProtoVersion 08}
	     * @see IProgramInfo#getProgramID()
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_08)
	    PROGRAM_ID,
	    
	    /**
	     * An identification string used by metadata grabbers.
	     * 
	     * @since {@mythProtoVersion 67}
	     * @see IProgramInfo#getInetRef()
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_67)
	    INETREF,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 11
	     * ========================================== */
	    
	    /** 
	     * Last modification time.
	     * <p>
	     * Keeps track of the last time the entry was updated.
	     * 
	     * @see IProgramInfo#getLastModified()
	     * @since {@mythProtoVersion 11}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_11)
	    @MythParameterType(Date.class)
	    LAST_MODIFIED,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 12
	     * ========================================== */
	    
	    /** 
	     * Rating.
	     * <p>
	     * The "stars" rating of the program.
	     * 
	     * @see IProgramInfo#getStars()
	     * @since {@mythProtoVersion 12}
	     */
	    @MythParameterDefaultValue("0.0")
	    @MythParameterType(Float.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_12)
	    STARS,
	    
	    /** 
	     * The original airdate.
	     *  
	     * @see IProgramInfo#getOriginalAirdate()
	     * @since {@mythProtoVersion 12}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_12)
	    @MythParameterType(Date.class)
	    ORIGINAL_AIRDATE,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 15
	     * ========================================== */
	    
	    /** 
	     * Air date available.
	     * <p>
	     * Specifies if an original air date is available.
	     * 
	     * @see IProgramInfo#hasAirDate()
	     * @since {@mythProtoVersion 15}
	     * @deprecated {@mythProtoVersion 57}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
	    		@MythProtoVersionMetadata(key=SVN_COMMIT,value="5829"),
	    		@MythProtoVersionMetadata(key=GIT_COMMIT,value="99fdbbaa256b668c971c")
	    	},
	    	to=PROTO_VERSION_57
	    )
	    @MythParameterType(Boolean.class)
	    HAS_AIRDATE,
	    
	    /**
	     * Time stretch setting for recording.
	     * <p>
	     * 
	     * @see IProgramInfo#getTimeStretch()
	     * @since {@mythProtoVersion 18}
	     * @deprecated {@mythProtoVersion 23}, 
	     * 		replaced by {@link #PLAY_GROUP}.
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_18,to=PROTO_VERSION_23,toInfo={
	    	@MythProtoVersionMetadata(key=SVN_COMMIT,value="7965"),
	    	@MythProtoVersionMetadata(key=SVN_COMMIT,value="ba21ecda5f975e2ebb66")
	    })
	    @MythParameterType(Float.class)
	    TIMESTRETCH,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 23
	     * ========================================== */
	    
	    /** 
	     * The play group.
	     * <p>
	     * The play group of the recording.
	     * <p>
	     * Like recording groups (aka recgroups), each recording is put into a playgroup when it
	     * is recorded as specified in its recording rule.  When that recording
	     * is watched, selected playback options are set based on the playgroup
	     * instead of using system or host specific settings.
	     * 
	     * @since {@mythProtoVersion 23}
	     * @see IProgramInfo#getPlayGroup()
	     */ 
	    @MythParameterDefaultValue(PLAY_GROUP_DEFAULT)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_23,fromInfo={
	    	@MythProtoVersionMetadata(key=SVN_COMMIT,value="7965"),
	    	@MythProtoVersionMetadata(key=SVN_COMMIT,value="ba21ecda5f975e2ebb66")
	    })
	    PLAY_GROUP,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 25
	     * ========================================== */
	    
	    /** 
	     * Recording Priority 2.
	     * <p>
	     * An additional priority value.
	     * <p>
	     * TODO: This priority value seems to be specified via programinfo.h/WatchListStatus, if
	     *       the value is lower {@code -1}. Otherwise it is interpreted as "watchListScore".
	     *       
	     * @since {@mythProtoVersion 25}
	     * @see IProgramInfo#getRecordingPriority2()
	     */
	    @MythParameterType(Integer.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_25)
	    REC_PRIORITY2,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 31
	     * ========================================== */	    
	    
	    /** 
	     * Parent ID.
	     * <p>
	     * If this recording is a duplicate, the ID of the parent is specified here.
	     * <p>
	     * This is used in combination with {@link IProgramRecordingType.Type#OVERRIDE_RECORD}.
	     * 
	     * @since {@mythProtoVersion 31}
	     * @see IProgramInfo#getParentID()
	     * @see ISchedule.Props#PARENT_ID
	     */
	    @MythParameterType(Integer.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_31)
	    PARENT_ID,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 32
	     * ========================================== */	    
	    
	    /** 
	     * Storage group.
	     * <p>
	     * The storage group of the recording.
	     * 
	     * @since {@mythProtoVersion 32}
	     * @see IProgramInfo#getStorageGroup()
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	    @MythParameterDefaultValue(STORAGE_GROUP_DEFAULT)
	    STORAGE_GROUP,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 35
	     * ========================================== */
	    
	    /** 
	     * Audio properties.
	     * <p>
	     * Audio properties of the recording.
	     * <br>
	     * See {@link IProgramAudioProperties.Flags here} for all available properties.
	     * 
	     * @since {@mythProtoVersion 35}
	     * @see IProgramInfo#getAudioProperties()
	     * @see IProgramAudioProperties
	     */	    
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	    @MythParameterType(ProgramAudioProperties.class)
	    AUDIO_PROPERTIES,
	    
	    /** 
	     * Video Properties.
	     * <p>
	     * Video properties of the recording.
	     * <br>
	     * See {@link IProgramVideoProperties.Flags here} for all available properties.
	     * 
	     * @see IProgramInfo#getVideoProperties()
	     * @see IProgramVideoProperties
	     * 
	     * @since {@mythProtoVersion 35}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	    @MythParameterType(ProgramVideoProperties.class)
	    VIDEO_PROPERTIES,
	    
	    /** 
	     * Subtitle type.
	     * <p>
	     * The subtitle type of the recording.
	     * <br>
	     * See {@link IProgramSubtitleType.Flags here} for all available types.
	     * 
	     * @see IProgramInfo#getSubtitleType()
	     * @see IProgramSubtitleType
	     * 
	     * @since {@mythProtoVersion 35}
	     */
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	    @MythParameterType(ProgramSubtitleType.class)
	    SUBTITLE_TYPE,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 41
	     * ========================================== */	    
	    
	    /** 
	     * Production year.
	     * 
	     * @see IProgramInfo#getYear()
	     * 
	     * @since {@mythProtoVersion 41}
	     */
	    @MythParameterType(Integer.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_41)
	    YEAR,
	    
	    /* ==========================================
	     * MYTHTV PROTOCOL >= 76
	     * ========================================== */
	    /**
	     * Part Number.
	     * 
	     * @since {@mythProtoVersion 76}
	     */
	    @MythParameterType(Integer.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_76)
	    PART_NUMBER,
	    
	    /**
	     * Part Total.
	     * 
	     * @since {@mythProtoVersion 76}
	     */
	    @MythParameterType(Integer.class)
	    @MythProtoVersionAnnotation(from=PROTO_VERSION_76)
	    PART_TOTAL,

		/**
		 * Category Type.
		 *
		 * @since {@mythProtoVersion 79}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_79)
		@MythParameterType(ProgramCategoryType.class)
		CATEGORYTYPES,

		@MythProtoVersionAnnotation(from=PROTO_VERSION_82)
		@MythParameterType(Integer.class)
		RECORDEDID,

		@MythProtoVersionAnnotation(from=PROTO_VERSION_86)
		@MythParameterType(String.class)
		INPUTNAME,

		@MythProtoVersionAnnotation(from=PROTO_VERSION_86)
		@MythParameterType(Date.class)
		BOOKMARKUPDATE
	}

	public List<String> getPropertyValues();
	
	/**
	 * The unique recording id if this recording.
	 * <p>
	 * This id has the format: <code>[channel-id]_[recording-start-time]</code>
	 * <p>
	 * <b>Note:</b> This id is different to {@link #getUniqueProgramId()}
	 * 
	 * @return
	 * 		the unique recording id.
	 */
	public String getUniqueRecordingId();	
	
	/* ====================================================================
	 * IBasicProgramInfo methods.
	 * ==================================================================== */
	
	/**
	 * Gets the title of this program.
	 * 
	 * @see IProgramInfo.Props#TITLE
	 * @see IBasicProgramInfo#getTitle()
	 */
	public abstract String getTitle();

	/**
	 * Gets the subtitle of this program.
	 * 
	 * @see IProgramInfo.Props#SUBTITLE
	 * @see IBasicProgramInfo#getSubtitle()
	 */
	public abstract String getSubtitle();

	/**
	 * Gets the description of this program.
	 * 
	 * @see IProgramInfo.Props#DESCRIPTION
	 * @see IBasicProgramInfo#getDescription()
	 */
	public abstract String getDescription();
	
	/**
	 * Gets the category of this program.
	 * 
	 * @see IProgramInfo.Props#CATEGORY
	 * @see IBasicProgramInfo#getCategory()
	 */
	public abstract String getCategory();

	/**
	 * Gets the start-date and -time of this program.
	 * <p>
	 * <b>Note:</b> This value may be different to {@link #getRecordingStartTime()}.
	 * 
	 * @see IProgramInfo.Props#START_DATE_TIME
	 * @see IBasicProgramInfo#getStartDateTime()
	 */
	public abstract Date getStartDateTime();

	/**
	 * Gets the end-date and -time of this program.
	 * <p>
	 * <b>Note:</b> This value may be different to {@link #getRecordingEndTime()}.
	 * 
	 * @see IProgramInfo.Props#END_DATE_TIME
	 * @see IBasicProgramInfo#getEndDateTime()
	 */
	public abstract Date getEndDateTime();

	/**
	 * Gets the series id of this program.
	 * <p>
	 * This is used by MythTV for duplication checks.
	 * 
	 * @see IProgramInfo.Props#SERIES_ID
	 * @see IBasicProgramInfo#getSeriesID()
	 * 
	 * @since {@mythProtoVersion 08}
	 */
	public abstract String getSeriesID();

	/**
	 * Gets the program id of this program.
	 * <p>
	 * This is used by MythTV for duplication checks.
	 * 
	 * @see IProgramInfo.Props#PROGRAM_ID
	 * @see IBasicProgramInfo#getProgramID()
	 * 
	 * @since {@mythProtoVersion 08}
	 */
	public abstract String getProgramID();	

	/* ====================================================================
	 * IBasicChannelInfo methods.
	 * ==================================================================== */		
	
	/**
	 * Gets the channel id of this program.
	 * 
	 * @see IProgramInfo.Props#CHANNEL_ID
	 * @see IBasicChannelInfo#getChannelID()
	 */
	public abstract Integer getChannelID();

	/**
	 * Gets the channel station abbreviation of this program.
	 * 
	 * @see IProgramInfo.Props#CHANNEL_SIGN
	 * @see IBasicChannelInfo#getChannelSign()
	 */
	public abstract String getChannelSign();

	/**
	 * Gets the channel number of this program.
	 * 
	 * @return
	 *  	the channel number
	 *  
	 * @see IProgramInfo.Props#CHANNEL_NUMBER
	 * @see IBasicChannelInfo#getChannelNumber()
	 */
	public abstract String getChannelNumber();	

	/* ====================================================================
	 * IBasicInputInfo methods.
	 * ==================================================================== */
	
	/**
	 * Gets the id of the recorder, that will record this program.
	 * 
	 * @see IProgramInfo.Props#CARD_ID
	 * @see IBasicInputInfo#getCardID()
	 */
	public abstract Integer getCardID();

	/**
	 * Gets the id of the recorder input, that will be used for recording.
	 * 
	 * @see IProgramInfo.Props#INPUT_ID
	 * @see IBasicInputInfo#getInputID()
	 */
	public abstract Integer getInputID();

	/**
	 * Gets the source id of this program.
	 * 
	 * @see IProgramInfo.Props#SOURCE_ID
	 * @see IBasicInputInfo#getSourceID()
	 */
	public abstract Integer getSourceID();
	
	/* ====================================================================
	 * Program Flags
	 * ==================================================================== */		
	
	/**
	 * Checks if the recording is a duplicate.
	 * <p>
	 * 
	 * @see IProgramInfo.Props#DUPLICATE
	 * @see IProgramFlags.Flags#FL_DUPLICATE
	 * 
	 * @deprecated {@link ProtocolVersion#PROTO_VERSION_57 57}, use {@link IProgramFlags#isSet(org.jmythapi.protocol.response.IProgramFlags.Flags)} instead.
	 */	
	@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
	public abstract Boolean isDuplicate();

	/**
	 * Checks if a shareable card input is used.
	 * <p>
	 * Signals that the used card input can be shared, e.g. on 
	 * DVB cards. Till protocol version {@mythProtoVersion 57} this information was
	 * used by the scheduler.
	 * 
	 * @see IProgramInfo.Props#SHAREABLE
	 * 
	 * @deprecated {@link ProtocolVersion#PROTO_VERSION_57 57}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
	public abstract Boolean isShareable();

	/**
	 * @see IProgramInfo.Props#REPEAT
	 * @see IProgramFlags.Flags#FL_REPEAT
	 * 
	 * @deprecated {@link ProtocolVersion#PROTO_VERSION_57 57}, use {@link IProgramFlags#isSet(org.jmythapi.protocol.response.IProgramFlags.Flags)} instead.
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_57)
	public abstract Boolean isRepeat();

	/**
	 * @see IProgramInfo.Props#CHAN_COMM_FREE
	 * @see IProgramFlags.Flags#FL_CHANCOMMFREE
	 * 
	 * @since {@mythProtoVersion 03}
	 * @deprecated {@link ProtocolVersion#PROTO_VERSION_57 57}, use {@link IProgramFlags#isSet(org.jmythapi.protocol.response.IProgramFlags.Flags)} instead.
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03,to=PROTO_VERSION_57)
	public abstract Boolean isChannelCommFree();

	/**
	 * Checks if an airdate is available for the program.
	 * 
	 * @see IProgramInfo.Props#HAS_AIRDATE
	 * @since {@mythProtoVersion 15}
	 * @deprecated {@link ProtocolVersion#PROTO_VERSION_57 57}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15,to=PROTO_VERSION_57)
	public abstract Boolean hasAirDate();	
	
	/* ====================================================================
	 * Additional methods.
	 * ==================================================================== */
	
	/**
	 * Gets the find-id of the recording.
	 * 
	 * <b>Note:</b> According to the MythTV <a href="http://www.mythtv.org/wiki/Record_table">wiki</a>, 
	 * this is calculated like this:
	 * <code>(UNIX_TIMESTAMP(program.starttime)/60/60/24)+719528</code>
	 * 
	 * @return
	 * 		the find id of the recording or {@code 0}
	 * 
	 * @see IProgramInfo.Props#FIND_ID
	 */
	public abstract Integer getFindID();	
	
	/**
	 * Parent ID.
	 * <p>
	 * If this recording is a duplicate, the ID of the parent is specified here.
	 * <p>
	 * This is used in combination with {@link IProgramRecordingType.Type#OVERRIDE_RECORD}.
	 * 
	 * @return
	 * 		the parent id of the recording
	 * 
	 * @since {@mythProtoVersion 31}
	 * @see IProgramInfo.Props#PARENT_ID
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_31)
	public abstract Integer getParentID();
	
	/**
	 * Gets the season of the program.
	 * 
	 * @see IProgramInfo.Props#SEASON
	 * 
	 * @since {@mythProtoVersion 67}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_67)
	public abstract Integer getSeason();
	
	/**
	 * Gets the episode number of the program.
	 * 
	 * @see IProgramInfo.Props#EPISODE
	 * 
	 * @since {@mythProtoVersion 67}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_67)
	public abstract Integer getEpisode();

	/**
	 * Gets the total episodes of the program.
	 *
	 * @see IProgramInfo.Props#TOTALEPISODES
	 *
	 * @since {@mythProtoVersion 78}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_78)
	public abstract Integer getTotalEpisodes();
	
	/**
	 * Gets the syndicated episode of the program.
	 * 
	 * @see IProgramInfo.Props#SYNDICATED_EPISODE
	 * 
	 * @since {@mythProtoVersion 76}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_76)
	public abstract String getSyndicatedEpisode();
		
	/**
	 * Gets an identification string to use for metadata grabbers.
	 * 
	 * @see IProgramInfo.Props#INETREF
	 * 
	 * @since {@mythProtoVersion 67}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_67)
	public abstract String getInetRef();	
	
	/**
	 * Gets the channel name.
	 * 
	 * @return
	 * 		the recording channel name
	 * 
	 * @see IProgramInfo.Props#CHANNEL_NAME
	 * @since {@mythProtoVersion 00}
	 */
	public abstract String getChannelName();
	
	/**
	 * Output filters.
	 * <p>
	 * Allows to specify a set of filters to be used when playing back recordings 
	 * from a given channel. This is useful for those with hardware encoders and 
	 * more than one source, since filters are rarely a one-size-fits-all thing.
	 * 
	 * @return
	 * 		the channel output filters
	 * 
	 * @see IProgramInfo.Props#CHANNEL_OUTPUT_FILTERS
	 * @since {@mythProtoVersion 06}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_06)
	public abstract String getChannelOuputFilters();

	/**
	 * Gets the recording file name or URI.
	 * 
	 * @return
	 * 		the recording file name and path or the URI to the file.
	 * @see IProgramInfo.Props#PATH_NAME
	 * @since {@mythProtoVersion 00}
	 */
	public abstract String getPathName();
	
	/**
	 * Gets the name of the default recordings preview image.
	 * <p>
	 * You can use the value returned by this function download the preview-image of the recording. 
	 * 
	 * @return
	 * 		the name of the preview image, e.g. {@code 1000_20110701123000.mpg.png}
	 * 
	 */
	public abstract String getPreviewImageName();
	
	/**
	 * Gets the recording file base name.
	 * @return
	 * 		the recording file base name, e.g. {@code 1000_20110701123000.mpg}
	 * 
	 * @see IProgramInfo.Props#PATH_NAME
	 * @since {@mythProtoVersion 00}
	 */
	public abstract String getBaseName();

	/**
	 * Gets the name of the host storing the recording file.
	 * 
	 * @return
	 * 		the host name
	 * @see IProgramInfo.Props#HOSTNAME
	 * @since {@mythProtoVersion 00}
	 */
	public String getHostName();
	
	/**
	 * Gets the size of the recording file.
	 * <p>
	 * <b>Hint:</b> To print a human readable string you can use {@link EncodingUtils#getFormattedFileSize}.
	 * 
	 * @return
	 * 		the recording file size in bytes.
	 * 
	 * @see IProgramInfo.Props#FILESIZE
	 * @see IProgramInfo.Props#FILESIZE_LOW
	 * @see IProgramInfo.Props#FILESIZE_HIGH
	 * @see EncodingUtils#getFormattedFileSize(long)
	 * @since {@mythProtoVersion 00}
	 */
	public abstract Long getFileSize();

	/**
	 * Updates the size of the recording.
	 * 
	 * @param fileSize
	 * 		the new file size
	 * 
	 * @see IProgramInfo.Props#FILESIZE
	 * @see IProgramInfo.Props#FILESIZE_LOW
	 * @see IProgramInfo.Props#FILESIZE_HIGH
	 * @see EncodingUtils#getFormattedFileSize(long)
	 * @since {@mythProtoVersion 00}
	 */
	public void setFileSize(Long fileSize);
	
	/**
	 * Gets the original airdate of the recording.
	 * @return
	 * 		the air date of the recording.
	 * 
	 * @see IProgramInfo.Props#ORIGINAL_AIRDATE
	 * 
	 * @since {@mythProtoVersion 12}
	 */
	public abstract Date getOriginalAirdate();

	/**
	 * Gets the recording start time.
	 * <p>
	 * <b>Note:</b> This may be different to {@link #getStartDateTime()}.
	 * 
	 * @return
	 * 		the recording start time.
	 * 
	 * @see IProgramInfo.Props#REC_START_TIME
	 * @since {@mythProtoVersion 00}
	 */
	public abstract Date getRecordingStartTime();
	
	/**
	 * Gets the recording end time.
	 * <p>
	 * <b>Note:</b> This may be different to {@link #getEndDateTime()}.
	 * 
	 * @return
	 * 		the recording end time.
	 * 
	 * @see IProgramInfo.Props#REC_END_TIME
	 * @since {@mythProtoVersion 00}
	 */
	public abstract Date getRecordingEndTime();

	/**
	 * Gets the recording start offset.
	 * <p>
	 * This value is the difference in minutes between {@link #getRecordingStartTime()} and {@link #getStartDateTime()}. 
	 * 
	 * @return
	 * 		the recording start offset in minutes.
	 * @see EncodingUtils#getMinutesDiff(Date, Date)
	 */
	public int getRecordingStartOffset();	
	
	/**
	 * Gets the recording end offset.
	 * <p>
	 * This value is the difference in minutes between {@link #getRecordingEndTime()} and {@link #getEndDateTime()}. 
	 * 
	 * @return
	 * 		the recording end offset in minutes.
	 * 
	 * @see EncodingUtils#getMinutesDiff(Date, Date)
	 */	
	public int getRecordingEndOffset();

	/**
	 * Gets the recording duration for the program in minutes.
	 * <p>
	 * <i>Note:</i> This value may be different to {@link #getDuration()}.
	 * 
	 * @return 
	 * 		the recording duration of the program in minutes or <code>-1</code> if unknown. 
	 */		
	public int getRecordingDuration();
	
	/**
	 * Gets the amount of minutes elapsed since recording of this program has started.
	 * <p>
	 * If current time is before recording-start-time, then {@code 0} is returned.<br>
	 * If current time is after recording-end-time, then {@link #getRecordingDuration()} is returned.<br>
	 * If current time is between recording-start- -and -end-time, {@code EncodingUtils.getMinutesDiff(recStartDate,now)}
	 * is used to calculate the amount of minutes elapsed.
	 * 
	 * @return
	 * 		the amount of minutes elapsed since recording started.
	 * 
	 * @see EncodingUtils#getMinutesDiff(Date, Date)
	 */
	public int getRecordingElapsedMinutes();
	
	/**
	 * Gets the amount of minutes elapsed in percent, since recording of this program has started.
	 * 
	 * @return
	 * 		the amount of minutes elapsed in percent, since recording has started.
	 */
	public int getRecordingElapsedPercent();
	
	/**
	 * Gets the recording priority.
	 * 
	 * @return
	 * 		the recording priority. This is typically a value between {@code -20} and {@code 20}
	 * 
	 * @see IProgramInfo.Props#REC_PRIORITY
	 */
	public abstract Integer getRecordingPriority();
	
	/**
	 * Gets the recording priority 2.
	 * 
	 * @return
	 * 		the recording priority-2
	 * 
	 * @see IProgramInfo.Props#REC_PRIORITY2
	 * @since {@mythProtoVersion 25}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_25)
	public abstract Integer getRecordingPriority2();
	
	/**
	 * Gets the recording group.
	 * 
	 * @return
	 * 		the name of the recording group, e.g. {@code Default}, {@code LiveTV} or {@code Deleted}
	 * 
	 * @see IProgramInfo.Props#REC_GROUP
	 * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public abstract String getRecordingGroup();
	
	/**
	 * Gets the play group assigned to the recording.
	 * 
	 * @return
	 * 		the play group name, e.g. {@code Default}
	 * 
	 * @see IProgramInfo.Props#PLAY_GROUP
	 * @since {@mythProtoVersion 23}
	 */
	public abstract String getPlayGroup();

	/**
	 * Gets the name of the storage group storing the recording.
	 * 
	 * @return
	 * 		the storage group name, e.g. {@code Default}
	 * 
	 * @see IProgramInfo.Props#STORAGE_GROUP
	 * @since {@mythProtoVersion 32}
	 */
	public abstract String getStorageGroup();

	/**
	 * Gets additional status flags of the recording.
	 * <p>
	 * See {@link IProgramFlags.Flags} for all possible values.
	 * 
	 * @return
	 * 		status flags
	 * 
	 * @see IProgramInfo.Props#PROGRAM_FLAGS
	 * @since {@mythProtoVersion 00}
	 */
	public abstract IProgramFlags getProgramFlags();

	/**
	 * Gets the recording audio properties.
	 * <p>
	 * See {@link IProgramAudioProperties.Flags} for all possible values.
	 * 
	 * @return
	 * 		the audio properties.
	 * 
	 * @see IProgramInfo.Props#AUDIO_PROPERTIES
	 * @since {@mythProtoVersion 35}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_35)
	public abstract IProgramAudioProperties getAudioProperties();

	/**
	 * Gets the recording video properties.
	 * <p>
	 * See {@link IProgramVideoProperties.Flags} for all possible values.
	 * 
	 * @return
	 *		the recording video properties. 		
	 * 
	 * @see IProgramInfo.Props#VIDEO_PROPERTIES
	 * @since {@mythProtoVersion 35}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_35)
	public abstract IProgramVideoProperties getVideoProperties();

	/**
	 * Gets the recording subtitle properties.
	 * <p>
	 * See {@link IProgramSubtitleType.Flags} for all possible values.
	 * 
	 * @return
	 * 		the subtitle properties.
	 * 
	 * @see IProgramInfo.Props#SUBTITLE_TYPE
	 * @since {@mythProtoVersion 35}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_35)
	public abstract IProgramSubtitleType getSubtitleType();

	/**
	 * Gets the id of the recording in the database.
	 * <p>
	 * <b>Note:</b> This is the ID of the mythtv scheduling rule, 
	 * stored in the database. Therefore multiple recordings may have the same
	 * recording id!<br>
	 * If you need an unique id for a recording, use {@link #getUniqueRecordingId()} instead.
	 * 
	 * @return
	 * 		the recording ID
	 * 
	 * @see IProgramInfo.Props#REC_ID
	 * @since {@mythProtoVersion 00}
	 */
	public Integer getRecordingId();
	
	/**
	 * Gets the current recording status.
	 * <p>
	 * The recording status is typically {@link IProgramRecordingStatus.Status#UNKNOWN unknown} 
	 * for a {@link IBackend#queryAllScheduled recording schedule}.<br>
	 * See {@link IProgramRecordingStatus.Status} for all possible status values.
	 * 
	 * @return
	 * 		the recording status.
	 * @see IProgramInfo.Props#REC_STATUS
	 * @since {@mythProtoVersion 00}
	 */
	public abstract IProgramRecordingStatus getRecordingStatus();

	/**
	 * Gets the recording type.
	 * <p>
	 * See {@link IProgramRecordingType.Type} for all possible types.
	 * 
	 * @return
	 * 		the recording type.
	 * 
	 * @see IProgramInfo.Props#REC_TYPE
	 * @since {@mythProtoVersion 00}
	 */
	public IProgramRecordingType getRecordingType();
	
	/**
	 * Gets the duplication type of the recording.
	 * <p>
	 * See {@link IProgramRecordingDupInType.Flags} for all possible flags.
	 * 
	 * @return
	 * 		the duplication type
	 * 
	 * @see IProgramInfo.Props#DUP_IN
	 * 
     * @mythProtoVersionRange
     * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public IProgramRecordingDupInType getRecordingDuplicationType();
	
	/**
	 * Gets the duplication mode to use for the recording.
	 * <p>
	 * See {@link IProgramRecordingDupMethodType.Flags} for all possible flags.
	 * 
	 * @return
	 * 		the duplication mode.
	 * 
	 * @see IProgramInfo.Props#DUP_METHOD
	 * 
     * @mythProtoVersionRange
     * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public IProgramRecordingDupMethodType getRecordingDuplicationMethod();
	
	/**
	 * Gets the production year of the program.
	 * 
	 * @return
	 * 		the production year of the program.
	 * 
	 * @see IProgramInfo.Props#YEAR
	 * @since {@mythProtoVersion 41}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_41)
	public Integer getYear();
	
	/**
	 * Part number.
	 * 
	 * @see IProgramInfo.Props#PART_NUMBER
	 * @since {@mythProtoVersion 76}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_76)
	public Integer getPartNumber();
	
	/**
	 * Part total.
	 * 
	 * @see IProgramInfo.Props#PART_TOTAL
	 * @since {@mythProtoVersion 76}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_76)
	public Integer getPartTotal();
	
	/**
	 * Rating.
	 * 
	 * @return
	 * 		the rating for this program.
	 * 
	 * @see IProgramInfo.Props#STARS
	 * @since {@mythProtoVersion 12}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_12)
	public Float getStars();
	
	/**
	 * Time stretch setting for recording.
	 * 
	 * @return
	 * 	the time stretch setting
	 * 
	 * @see IProgramInfo.Props#TIMESTRETCH
	 * @since {@mythProtoVersion 18}
	 * @deprecated {@mythProtoVersion 23}, replaced by {@link #getPlayGroup()}
	 */
    @MythProtoVersionAnnotation(from=PROTO_VERSION_18,to=PROTO_VERSION_23,toInfo={
    	@MythProtoVersionMetadata(key=SVN_COMMIT,value="7965"),
    	@MythProtoVersionMetadata(key=SVN_COMMIT,value="ba21ecda5f975e2ebb66")
    })
	public Float getTimeStretch();
    
    /**
     * Last modification time.
     * <p>
     * Keeps track of the last time the entry was updated.
     * 
     * @return
     * 		the last modified date of the recording
     * 
     * @see IProgramInfo.Props#LAST_MODIFIED
     * @since {@mythProtoVersion 11}
     */
    @MythProtoVersionAnnotation(from=PROTO_VERSION_11)
    public Date getLastModified();

    @MythProtoVersionAnnotation(from=PROTO_VERSION_79)
	public abstract IProgramCategoryType getCategoryType();

	@MythProtoVersionAnnotation(from=PROTO_VERSION_82)
	public int getRecordedId();

	@MythProtoVersionAnnotation(from=PROTO_VERSION_86)
	public String getInputName();

	@MythProtoVersionAnnotation(from=PROTO_VERSION_86)
	public Date getBookmarkUpdate();
}