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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1029;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1037;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1042;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1047;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1057;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1061;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1062;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1082;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1085;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1088;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1108;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1143;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1158;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1171;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1193;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1257;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1277;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1278;
import static org.jmythapi.protocol.ProtocolConstants.PLAY_GROUP_DEFAULT;
import static org.jmythapi.protocol.ProtocolConstants.RECORDING_GROUP_DEFAULT;
import static org.jmythapi.protocol.ProtocolConstants.RECORDING_PROFILE_DEFAULT;
import static org.jmythapi.protocol.ProtocolConstants.STORAGE_GROUP_DEFAULT;

import java.sql.Time;
import java.util.Date;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IPropertyAware;
import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.annotation.MythParameterDefaultValue;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.response.IBasicProgramInfo;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingDupInType;
import org.jmythapi.protocol.response.IProgramRecordingDupMethodType;
import org.jmythapi.protocol.response.IProgramRecordingFilters;
import org.jmythapi.protocol.response.IProgramRecordingSearchType;
import org.jmythapi.protocol.response.IProgramRecordingType;
import org.jmythapi.protocol.response.impl.ProgramRecordingDupInType;
import org.jmythapi.protocol.response.impl.ProgramRecordingDupMethodType;
import org.jmythapi.protocol.response.impl.ProgramRecordingFilters;
import org.jmythapi.protocol.response.impl.ProgramRecordingSearchType;
import org.jmythapi.protocol.response.impl.ProgramRecordingType;

/**
 * An interface to create a new recording schedule.
 * <p>
 * This interface is used to insert a new recording schedule into the MythTV database.<br>
 * See {@link ISchedule.Props} for properties of this interface.
 * 
 * <h3>A response example:</h3>
 * {@mythResponseExample
 * 		A "find one record" example:
 * 		<pre><00>REC_ID: 1221 | <01>REC_TYPE: 6=> &#123;FIND_ONE_RECORD&#125; | <02>CHANNEL_ID: 1067 | <03>START_TIME: 22:25:00 | <04>START_DATE: 2011-07-27 | <05>END_TIME: 00:10:00 | <06>END_DATE: 2011-07-28 | <07>TITLE: Airforce Two | <08>SUBTITLE:  | <09>DESCRIPTION:  | <10>CATEGORY: spielfilm | <11>PROFILE: Default | <12>REC_PRIORITY: 0 | <13>AUTOEXPIRE: true | <14>MAX_EPISODES: 0 | <15>MAX_NEWEST: false | <16>START_OFFSET: 5 | <17>END_OFFSET: 15 | <18>REC_GROUP: Default | <19>DUP_METHOD: 6=> &#123;DUP_CHECK_SUB,DUP_CHECK_DESC,DUP_CHECK_SUB_DESC&#125; | <20>DUP_IN: 15=> &#123;DUPS_IN_RECORDED,DUPS_IN_OLD_RECORDED,DUPS_IN_ALL&#125; | <21>CHANNEL_SIGN: SAT.1 | <22>SERIES_ID: 202888895 | <23>PROGRAM_ID:  | <24>SEARCH: 0 | <25>AUTO_TRANSCODE: false | <26>AUTO_COMMFLAG: false | <27>AUTO_USERJOB_1: false | <28>AUTO_USERJOB_2: false | <29>AUTO_USERJOB_3: false | <30>AUTO_USERJOB_4: false | <31>FIND_DAY: 4 | <32>FIND_TIME: 22:25:00 | <33>FIND_ID: 0 | <34>INACTIVE: false | <35>PARENT_ID: 0 | <36>TRANSCODER: 0 | <37>TS_DEFAULT: 1.0 | <38>PLAY_GROUP: Default | <39>PREF_INPUT: 0 | <40>NEXT_RECORD: null | <41>LAST_RECORD: Sun Jul 31 00:20:02 CEST 2011 | <42>LAST_DELETE: null | <43>STORAGEGROUP: Default | <44>AVG_DELAY: 100</per>
 * 
 * 		A "Do not record" example:
 * 		<pre><00>REC_ID: 1280 | <01>REC_TYPE: 8=> &#123;DONT_RECORD&#125; | <02>CHANNEL_ID: 1063 | <03>START_TIME: 07:15:00 | <04>START_DATE: 2011-07-30 | <05>END_TIME: 07:45:00 | <06>END_DATE: 2011-07-30 | <07>TITLE: The Big Bang Theory | <08>SUBTITLE:  | <09>DESCRIPTION: Sheldon pro se Serie mit Kaley Cuoco, Johnny Galecki<10>CATEGORY: serie | <11>PROFILE: Default | <12>REC_PRIORITY: 0 | <13>AUTOEXPIRE: true | <14>MAX_EPISODES: 0 | <15>MAX_NEWEST: false | <16>START_OFFSET: 5 | <17>END_OFFSET: 15 | <18>REC_GROUP: Default | <19>DUP_METHOD: 6=> &#123;DUP_CHECK_SUB,DUP_CHECK_DESC,DUP_CHECK_SUB_DESC&#125; | <20>DUP_IN: 15=> &#123;DUPS_IN_RECORDED,DUPS_IN_OLD_RECORDED,DUPS_IN_ALL&#125; | <21>CHANNEL_SIGN: PRO7 | <22>SERIES_ID: 100797097 | <23>PROGRAM_ID:  | <24>SEARCH: 0 | <25>AUTO_TRANSCODE: false | <26>AUTO_COMMFLAG: false | <27>AUTO_USERJOB_1: false | <28>AUTO_USERJOB_2: false | <29>AUTO_USERJOB_3: false | <30>AUTO_USERJOB_4: false | <31>FIND_DAY: 6 | <32>FIND_TIME: 06:45:00 | <33>FIND_ID: 0 | <34>INACTIVE: false | <35>PARENT_ID: 0 | <36>TRANSCODER: 0 | <37>TS_DEFAULT: 1.0 | <38>PLAY_GROUP: Default | <39>PREF_INPUT: 0 | <40>NEXT_RECORD: null | <41>LAST_RECORD: Fri Jul 29 12:40:02 CEST 2011 | <42>LAST_DELETE: null | <43>STORAGEGROUP: Default | <44>AVG_DELAY: 100</pre>
 * }
 * <br>
 */
public interface ISchedule extends IBasicProgramInfo, IPropertyAware<ISchedule.Props> {
	public static final String CATEGORY_MANUAL_RECORDING = "Manual recording";
	public static final String DESCRIPTION_MANUALLY_SCHEDULED = "Manually scheduled";
	
	/**
	 * Properties of an {@link ISchedule} object.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
	    /** 
	     * The recording ID.
	     * <p>
	     * The id the recording in the MythTV database.
	     * <p>
	     * <pre>`recordid` int(10) unsigned NOT NULL AUTO_INCREMENT</pre>
	     * 
	     * @see ISchedule#getRecordingId()
	     */
	    @MythParameterType(Integer.class)
	    @MythDatabaseColumn(column="recordid")
	    REC_ID,
	    
	    /** 
	     * The recording type.
	     * <p>
	     * E.g. if the program is recorded a single time or using a weekly time-slot, etc.
	     * <p>
	     * <pre>`type` int(10) unsigned NOT NULL DEFAULT '0'</pre>
	     * 
	     * @see IProgramRecordingType 
	     * @see ISchedule#getRecordingType()
	     */
	    @MythDatabaseColumn(column="type")
	    @MythParameterType(ProgramRecordingType.class)
	    @MythParameterDefaultValue("0")
	    REC_TYPE,
	    
		/** 
		 * The channel ID.
		 * <p>
		 * The unique id of the channel: e.g. {@code 1001}.
		 * <p>
		 * <pre>`chanid` int(10) unsigned DEFAULT NULL</pre>
		 * 
		 * @see ISchedule#getChannelID()
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(column="chanid")
		CHANNEL_ID,
		
		/** 
		 * The scheduled start time of the program.
		 * <p>
		 * <pre>`starttime` time NOT NULL DEFAULT '00:00:00'</pre>
		 * 
		 * @see ISchedule#getStartTime()
		 */
		@MythParameterType(Time.class)
		@MythParameterDefaultValue("00:00:00")
		@MythDatabaseColumn(column="starttime")
		START_TIME,
		
		/**
		 * The scheduled start date of the program.
		 * <p>
		 * <pre>`startdate` date NOT NULL DEFAULT '0000-00-00'</pre>
		 * 
		 * @see ISchedule#getStartDate()
		 */
		@MythParameterType(java.sql.Date.class)
		@MythParameterDefaultValue("0000-00-00")
		@MythDatabaseColumn(column="startdate")		
		START_DATE,
		
		/** 
		 * The scheduled end time of the program.
		 * <p>
		 * <pre>`endtime` time NOT NULL DEFAULT '00:00:00'</pre>
		 * 
		 * @see ISchedule#getEndTime()
		 */
		@MythParameterType(Time.class)
		@MythParameterDefaultValue("00:00:00")
		@MythDatabaseColumn(column="endtime")
		END_TIME,
		
		/**
		 * The scheduled end date of the program.
		 * <p>
		 * <pre>`enddate` date NOT NULL DEFAULT '0000-00-00'</pre>
		 * 
		 * @see ISchedule#getEndDateTime()
		 */
		@MythParameterType(java.sql.Date.class)
		@MythParameterDefaultValue("0000-00-00")
		@MythDatabaseColumn(column="enddate")		
		END_DATE,
		
		/** 
		 * The recording title.
		 * <p>
		 * E.g. {@code Malcolm mittendrin}
		 * <p>
		 * <pre>`title` varchar(128) NOT NULL DEFAULT ''</pre>
		 * 
		 * @see ISchedule#getTitle()
		 */
		@MythParameterDefaultValue("")
		@MythDatabaseColumn(column="title",length=128)		
		TITLE,
		
		/** 
		 * The recording sub-title.
		 * <p>
		 * E.g. {@code Victors zweite Familie}
		 * <p>
		 * <pre>`subtitle` varchar(128) NOT NULL DEFAULT ''</pre>
		 * 
		 * @see ISchedule#getSubtitle()
		 */
		@MythParameterDefaultValue("")
		@MythDatabaseColumn(column="subtitle",length=128)
		SUBTITLE,
		
		/**
		 * A description about the recording.
		 * <p>
		 * E.g. {@code Als Lois erfährt, dass ihr Vater eine zweite Familie hatte, beschließt sie kurzerhand...}
		 * <p>
		 * <pre>`description` text NOT NULL</pre>
		 * 
		 * @see ISchedule#getDescription()
		 */
		@MythParameterDefaultValue("")
		@MythDatabaseColumn(column="description", nullable=false)
		DESCRIPTION,		
		
		/**
		 * The season.
		 * <p>
		 * 
		 * @since {@mythProtoVersion 67}, dbver == "1277"
		 * 
		 * @see ISchedule#getSeason()
		 */
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		@MythDatabaseColumn(column="season")
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1277)
		SEASON,

		/**
		 * The episode number.
		 * 
		 * <pre>episode SMALLINT(5) NOT NULL"</pre>
		 * @since {@mythProtoVersion 67}, dbver == "1277"
		 * 
		 * @see ISchedule#getEpisode()
		 */
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		@MythDatabaseColumn(column="episode")
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1277)
		EPISODE,		
		
		/** 
		 * The category of the recording.
		 * <p>
		 * E.g. {@code comedy}
		 * <p>
		 * <pre>`category` varchar(64) NOT NULL DEFAULT ''</pre>
		 * 
		 * @see ISchedule#getCategory()
		 */
		@MythDatabaseColumn(column="category")
		@MythParameterDefaultValue("")
		CATEGORY,		
		
		/**
		 * The recording profile to use.
		 * <p>
		 * <pre>`profile` varchar(128) NOT NULL DEFAULT 'Default'</pre>
		 * 
		 * @see ISchedule#getProfile()
		 */
		@MythDatabaseColumn(column="profile")
		@MythParameterDefaultValue(RECORDING_PROFILE_DEFAULT)
		PROFILE,
		
		/**
		 * The recording priority.
		 * 
		 * <pre>`recpriority` int(10) NOT NULL DEFAULT '0'</pre>
		 * 
		 * @see ISchedule#getRecordingPriority()
		 */
		@MythDatabaseColumn(column="recpriority")
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		REC_PRIORITY,
		
		/**
		 * Specifies if the recording will automatically expire.
		 * 
		 * <pre>`autoexpire` int(11) NOT NULL DEFAULT '0'</pre>
		 * 
		 * TODO: this seems to have multiple possible values:
		 * http://code.mythtv.org/doxygen/programtypes_8h.html#aa2dbdc45e75666cdb568f45660525c18
		 * 
		 * @see ISchedule#getAutoExpire()
		 */
		@MythDatabaseColumn(column="autoexpire")
		@MythParameterType(value=Boolean.class,stringType=Integer.class)
		@MythParameterDefaultValue("0")
		AUTOEXPIRE,
		
		/**
		 * <pre>`maxepisodes` int(11) NOT NULL DEFAULT '0'</pre>
		 * 
		 * @see ISchedule#getMaxEpisodes()
		 */
		@MythDatabaseColumn(column="maxepisodes")
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")		
		MAX_EPISODES,
		
		/**
		 * <pre>`maxnewest` int(11) NOT NULL DEFAULT '0'</pre>
		 * 
		 * @see ISchedule#getMaxNewest()
		 */
		@MythDatabaseColumn(column="maxnewest")
		@MythParameterType(Boolean.class)
		@MythParameterDefaultValue("0")				
		MAX_NEWEST,
		
		/**
		 * The recording start offset.
		 * 
		 * <pre>`startoffset` int(11) NOT NULL DEFAULT '0'</pre>
		 * 
		 * @see ISchedule#getStartOffset()
		 */
		@MythDatabaseColumn(column="startoffset")
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")		
		START_OFFSET,
		
		/**
		 * The recording end offset.
		 * 
		 * <pre>`endoffset` int(11) NOT NULL DEFAULT '0'</pre>
		 * 
		 * @see ISchedule#getEndOffset()
		 */
		@MythDatabaseColumn(column="endoffset")
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")		
		END_OFFSET,
		
		/**
		 * The recording group.
		 * 
		 * <pre>`recgroup` varchar(32) NOT NULL DEFAULT 'Default'</pre>
		 * @since dbver == "1029"
		 * 
		 * @see ISchedule#getRecordingGroup()
		 */
		@MythDatabaseColumn(column="recgroup")
		@MythParameterDefaultValue(RECORDING_GROUP_DEFAULT)
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1029) 
		REC_GROUP,
			
		/**
		 * The duplication mode and type.
		 * @deprecated dbver == "1029"
		 */
		@MythDatabaseVersionAnnotation(to=DB_VERSION_1029)  
		REC_DUPS,
		
	    /** 
	     * The duplication mode to use for the recording.
	     * <p>
	     * Specifies how a check for duplicates is done.
	     * <p>
	     * <pre>`dupmethod` int(11) NOT NULL DEFAULT '6'</pre>
	     * 
	     * @see IProgramRecordingDupMethodType
	     * 
	     * @mythProtoVersionRange
	     * @since {@mythProtoVersion 03}, dbver == "1029"
	     */
	    @MythDatabaseColumn(column="dupmethod")
	    @MythParameterType(ProgramRecordingDupMethodType.class)
	    @MythParameterDefaultValue("6")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1029)    
	    DUP_METHOD,			
		
	    /** 
	     * The duplication type of the recording.
	     * <p>
	     * Specifies how to handle duplicates.
	     * <p>
	     * <pre>`dupin` int(11) NOT NULL DEFAULT '15'</pre>
	     * 
	     * @see IProgramRecordingDupInType
	     *  
	     * @since {@mythProtoVersion 03}, dbver == "1029"
	     */
	    @MythDatabaseColumn(column="dupin")
	    @MythParameterType(ProgramRecordingDupInType.class)
	    @MythParameterDefaultValue("15")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1029)
	    DUP_IN,	
		
		/** 
		 * The channel abbreviation.
		 * <p>
		 * E.g. {@code ORF1}
		 * <p>
		 * <pre>`station` varchar(20) NOT NULL DEFAULT ''</pre>
		 * 
		 * @since dbver == "1037"
		 * 
		 * @see ISchedule#getChannelSign() 
		 * @see IProgramInfo#getChannelSign()
		 */
	    @MythParameterDefaultValue("")
		@MythDatabaseColumn(column="station")
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1037)
		CHANNEL_SIGN,		
		
	    /** 
	     * The series ID.
	     * 
	     * <pre>`seriesid` varchar(40) NOT NULL DEFAULT ''</pre>
	     * 
	     * @since {@mythProtoVersion 08}, dbver == "1042"
	     * 
	     * @see ISchedule#getSeriesID()
	     */
		@MythParameterDefaultValue("")
	    @MythDatabaseColumn(column="seriesid")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1042)
	    SERIES_ID,
	    
	    /**
	     * The program ID.
	     * 
	     * <pre>`programid` varchar(40) NOT NULL DEFAULT ''</pre>
	     * 
	     * @since {@mythProtoVersion 08}, dbver == "1042"
	     * 
	     * @see ISchedule#getProgramID()
	     */
	    @MythParameterDefaultValue("")
	    @MythDatabaseColumn(column="programid")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1042)
	    PROGRAM_ID,		
		
		/**
		 * The search type.
		 * 
		 * <pre>`search` int(10) unsigned NOT NULL DEFAULT '0'</pre>
		 * @since dbver == "1047"
		 */
	    @MythParameterType(ProgramRecordingSearchType.class)
	    @MythDatabaseColumn(column="search")
	    @MythParameterDefaultValue("0")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1047)
	    SEARCH,
	    
	    /**
	     * Autotranscode.
	     * 
	     * <pre>`autotranscode` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1057"
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="autotranscode")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    AUTO_TRANSCODE,
	    
	    /**
	     * Auto commflagging.
	     * 
	     * <pre>`autocommflag` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1057"
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue(value="0")
	    @MythDatabaseColumn(column="autocommflag")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    AUTO_COMMFLAG,
	    
	    /**
	     * Auto userjob 1.
	     * 
	     * <pre>`autouserjob1` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1057"
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="autouserjob1")	
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    AUTO_USERJOB_1,
	    
	    /**
	     * Auto userjob 2.
	     * <pre>`autouserjob2` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1057"
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="autouserjob2")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    AUTO_USERJOB_2,
	    
	    /**
	     * Auto userjob 3.
	     * 
	     * <pre>`autouserjob3` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1057"
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="autouserjob3")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    AUTO_USERJOB_3,
	    
	    /**
	     * Auto user job 4.
	     * <pre>`autouserjob4` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1057"	    
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="autouserjob4")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    AUTO_USERJOB_4,
	    
	    /**
	     * Auto metadata.
	     * 
	     * <pre>autometadata TINYINT(1) NOT NULL DEFAULT '0'</pre>
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="autometadata")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1278)
	    AUTO_METADATA,

	    /**
	     * The findday.
	     * 
	     * <pre>`findday` tinyint(4) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1061"
	     * 
	     * @see ISchedule#getFindDay()
	     */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="findday")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1061)	    
	    FIND_DAY,
	    
	    /**
	     * The find time.
	     * 
	     * <pre>`findtime` time NOT NULL DEFAULT '00:00:00'</pre>
	     * @since dbver == "1061"
	     * 
	     * @see ISchedule#getFindTime()
	     */
	    @MythParameterType(Time.class)
	    @MythParameterDefaultValue("00:00:00")
	    @MythDatabaseColumn(column="findtime")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1061)	    
	    FIND_TIME,
	    
	    /**
	     * Find-ID.
	     * <p>
	     * According to the MythTV <a href="http://www.mythtv.org/wiki/Record_table">wiki</a>, this is calculated like this:
	     * <code>(UNIX_TIMESTAMP(program.starttime)/60/60/24)+719528</code>
	     * 
	     * <pre>`findid` int(11) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1061"
	     * 
	     * @see ISchedule#getFindId()
	     */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="findid")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1061)	    
	    FIND_ID,
	    
	    /**
	     * Inactive.
	     * <p>
	     * Allow deactivating and reactivating a recording rule.  
	     * When a rule is inactive, programs matching it will not be recorded.
	     * <p>
	     * <pre>`inactive` tinyint(1) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1062"
	     */
	    @MythParameterType(value=Boolean.class,stringType=Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="inactive")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1062)	    
	    INACTIVE,
	    
	    /**
	     * The recorings parent ID.
	     * 
	     * <pre>`parentid` int(11) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1082"
	     */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="parentid")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1082)	    
	    PARENT_ID,
	    
	    /**
	     * Transcoder.
	     * 
	     * <pre>`transcoder` int(11) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1085"
	     */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="transcoder")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1085)		    
	    TRANSCODER,
	    
	    /**
	     * Time stretch setting.
	     * <p>
	     * 
	     * <pre>`tsdefault` float NOT NULL DEFAULT '1'</pre>
	     * @since dbver == "1088"
	     * @deprecated dbver == "1257"
	     */
	    @MythParameterType(Float.class)
	    @MythParameterDefaultValue("1.0")
	    @MythDatabaseColumn(column="tsdefault")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1088,to=DB_VERSION_1257)	    
	    TS_DEFAULT,
	    
	    /**
	     * The play group.
	     * 
	     * <pre>`playgroup` varchar(32) NOT NULL DEFAULT 'Default'</pre>
	     * <pre>dbver == "1108"</pre>
	     */
	    @MythParameterDefaultValue(PLAY_GROUP_DEFAULT)
	    @MythDatabaseColumn(column="playgroup")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1108)	    
	    PLAY_GROUP,
	    
	    /**
	     * The prefered input to use.
	     * <p>
	     * 
	     * <pre>`prefinput` int(10) NOT NULL DEFAULT '0'</pre>
	     * @since dbver == "1143"
	     */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="prefinput")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1143)	    
	    PREF_INPUT,
	    
	    /**
	     * Next record.
	     * 
	     * <pre>`next_record` datetime NOT NULL</pre>
	     * @since dbver == "1158"
	     */
	    @MythParameterType(Date.class)
	    @MythParameterDefaultValue("0000-00-00 00:00:00")
	    @MythDatabaseColumn(column="next_record")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1158)	    
	    NEXT_RECORD,
	    
	    /**
	     * Last record.
	     * 
	     * <pre>`last_record` datetime NOT NULL</pre>
	     * @since dbver == "1158"
	     */
	    @MythParameterType(Date.class)
	    @MythParameterDefaultValue("0000-00-00 00:00:00")
	    @MythDatabaseColumn(column="last_record")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1158)	    
	    LAST_RECORD,
	    
	    /**
	     * Last delete.
	     * 
	     * <pre>`last_delete` datetime NOT NULL</pre>
	     * @since dbver == "1158"
	     */
	    @MythParameterType(Date.class)
	    @MythParameterDefaultValue("0000-00-00 00:00:00")
	    @MythDatabaseColumn(column="last_delete")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1158)		    
	    LAST_DELETE,
	    
	    /**
	     * The storage group.
	     * 
	     * <pre>`storagegroup` varchar(32) NOT NULL DEFAULT 'Default'</pre>
	     * 
	     * @since dbver == "1171"
	     */
	    @MythParameterDefaultValue(STORAGE_GROUP_DEFAULT)
	    @MythDatabaseColumn(column="storagegroup")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1171)	    
	    STORAGEGROUP,
	    
	    /**
	     * Average delay.
	     * <p>
	     * 
	     * <pre>`avg_delay` int(11) NOT NULL DEFAULT '100'</pre>
	     * @since dbver == "1193"
	     */
	    @MythParameterType(Integer.class)
	    @MythParameterDefaultValue("100")
	    @MythDatabaseColumn(column="avg_delay")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1193)
	    AVG_DELAY,
	    
	    /**
	     * Recording filter.
	     * <p>
	     * 
	     * <pre>filter INT UNSIGNED NOT NULL DEFAULT 0</pre>
	     * @since dbver == "1277"
	     */
	    @MythParameterType(ProgramRecordingFilters.class)
	    @MythParameterDefaultValue("0")
	    @MythDatabaseColumn(column="filter")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1277)
	    FILTER,	    
	    
	    /**
	     * An identification string used by metadata grabbers.
	     * 
	     * <pre>inetref VARCHAR(40) NOT NULL</pre>
	     * 
		 * @since dbver == "1278"
	     */
	    @MythParameterDefaultValue("")
	    @MythDatabaseColumn(column="inetref")
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1278)	    
	    INETREF,
	};
	
	public Integer getRecordingId();
	
	public void setRecordingId(Integer recordingId);
	
	public IProgramRecordingType getRecordingType();
	
	public void setRecordingType(IProgramRecordingType recType);
	
	public Integer getChannelID();
	
	public void setChannelID(Integer channelId);
	
	public String getChannelSign();
	
	public void setChannelSign(String sign);
	
	/**
	 * @see #setChannelSign(String)
	 * @see #setChannelID(Integer)
	 */
	public void setChannel(IBasicChannelInfo channelInfo);
	
	public java.sql.Date getStartDate();
	
	public Time getStartTime();
	
	/**
	 * @see #getStartDate()
	 * @see #getStartTime()
	 */
	public Date getStartDateTime();
	
	public void setStartDate(Date startDate);
	
	public void setStartTime(Date startTime);
	
	/**
	 * @see #setStartDate(Date)
	 * @see #setStartTime(Date)
	 */
	public void setStartDateTime(Date startDateTime);
	
	public java.sql.Date getEndDate();
	
	public Time getEndTime();
	
	/**
	 * @see #getEndDate()
	 * @see #getEndTime()
	 */
	public Date getEndDateTime();
	
	public void setEndDate(Date endDate);
	
	public void setEndTime(Date endDate);
	
	/**
	 * @see #setEndDate(Date)
	 * @see #setEndTime(Date)
	 */
	public void setEndDateTime(Date endDateTime);
	
	public String getTitle();
	
	public void setTitle(String title);
	
	public String getFullTitle();
	
	public String getSubtitle();
	
	public void setSubtitle(String subTitle);
	
	public String getDescription();
	
	public void setDescription(String description);	
	
	public String getCategory();
	
	public void setCategory(String category);
	
	public String getProfile();
	
	public void setProfile(String profile);
	
	public Integer getSeason();
	
	public void setSeason(Integer season);
	
	public Integer getEpisode();
	
	public void setEpisode(Integer episode);
	
	public Integer getRecordingPriority();
		
	public void setRecordingPriority(Integer priority);
	
	public Integer getFindDay();
	
	public void setFindDay(Date dayDateTime);
	
	public void setFindDay(Integer day);
	
	public Time getFindTime();
	
	public void setFindTime(Date findTime);
	
	public Integer getFindId();
	
	public void setFindId(Date startDateTime);
	
	public void setFindId(Integer findId);
	
	public IProgramRecordingSearchType getSearch();
	
	public void setSearch(IProgramRecordingSearchType search);
	
	public String getSeriesID();
	
	public void setSeriesID(String seriesId);
	
	public String getProgramID();
	
	public void setProgramID(String programId);
	
	public Integer getStartOffset();
	
	public void setStartOffset(Integer startOffset);
	
	public Integer getEndOffset();
	
	public void setEndOffset(Integer endOffset);
		
	public Integer getAutoExpire();
	
	public void setAutoExpire(Integer autoExpire);
	
	public Integer getMaxEpisodes();
	
	public void setMaxEpisodes(Integer maxEpisodes);
	
	public Integer getMaxNewest();
	
	public void setMaxNewest(Integer maxNewest);
	
	public String getRecordingGroup();
	
	public void setRecordingGroup(String recordingGroup);
	
	public IProgramRecordingFilters getRecordingFilters();
		
	public void setRecordingFilters(IProgramRecordingFilters filters);
}
