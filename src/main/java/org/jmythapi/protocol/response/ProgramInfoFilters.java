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

import static org.jmythapi.protocol.response.IProgramInfo.Props.PROGRAM_FLAGS;
import static org.jmythapi.protocol.response.IProgramInfo.Props.REC_STATUS;
import static org.jmythapi.protocol.response.IProgramInfoList.MapKey.UNIQUE_PROGRAM_ID;
import static org.jmythapi.protocol.response.IProgramInfoList.MapKey.UNIQUE_RECORDING_ID;

import java.util.Date;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.protocol.response.IProgramFlags.Flags;
import org.jmythapi.protocol.response.IProgramRecordingStatus.Status;
import org.jmythapi.protocol.response.impl.filters.EnumGroupFilter;
import org.jmythapi.protocol.response.impl.filters.FlagGroupFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoChannelFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoChannelIdRecordingStartTimeFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoConjunctionFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoDisjunctionFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoNegotiationFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoRecorderFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoRecordingGroupFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoRecordingIdFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoStorageGroupFilter;
import org.jmythapi.protocol.response.impl.filters.ProgramInfoUniqueIdFilter;

public class ProgramInfoFilters {
	
	/**
	 * Creates a filter to filter recordings by {@link IProgramFlags flags}.
	 * 
	 * @see IProgramInfo#getProgramFlags()
	 */	
	public static IProgramInfoFilter flag(Flags flag) {
		return new ProgramInfoFilter(new FlagGroupFilter<IProgramFlags.Flags,IProgramInfo.Props, IProgramInfo>(PROGRAM_FLAGS,flag));
	}
	
	/**
	 * Creates a filter to filter recordings by {@link IProgramFlags flags}.
	 * <p>
	 * A program must have set <u>all</u> given flags to be accepted.
	 * 
	 * @see IProgramInfo#getProgramFlags()
	 */	
	public static IProgramInfoFilter allFlags(Flags... flags) {
		return new ProgramInfoFilter(new FlagGroupFilter<IProgramFlags.Flags,IProgramInfo.Props, IProgramInfo>(PROGRAM_FLAGS,true,flags));
	}	
	
	/**
	 * Creates a filter to filter recordings by {@link IProgramFlags flags}.
	 * <p>
	 * A program must have set <u>at least one</u> of the given flags to be accepted.
	 * 
	 * @see IProgramInfo#getProgramFlags()
	 */
	public static IProgramInfoFilter anyFlags(Flags... flags) {
		return new ProgramInfoFilter(new FlagGroupFilter<IProgramFlags.Flags,IProgramInfo.Props, IProgramInfo>(PROGRAM_FLAGS,false,flags));
	}		
	
	/**
	 * Creates a filter to filter recordings by {@link IProgramRecordingStatus recording-status}.
	 * 
	 * @see IProgramInfo#getRecordingStatus()
	 */
	public static IProgramInfoFilter status(Status... recordingStatus) {
		return new ProgramInfoFilter(new EnumGroupFilter<IProgramRecordingStatus.Status,IProgramInfo.Props, IProgramInfo>(REC_STATUS,recordingStatus));
	}
	
	/**
	 * Creates a filter to filter recordings by recorder-id.
	 * @see IProgramInfo#getCardID()
	 */
	public static IProgramInfoFilter recorder(Integer... recorderIds) {
		return new ProgramInfoRecorderFilter(recorderIds);
	}
	
	/**
	 * Creates a filter to filter recordings by recorder.
	 * @see IProgramInfo#getCardID()
	 */	
	public static IProgramInfoFilter recorder(IRecorderInfo... recorderInfos) {
		return new ProgramInfoRecorderFilter(recorderInfos);
	}

	/**
	 * Creates a filter to filter recordings by channel ID.
	 * @see IProgramInfo#getChannelID()
	 */		
	public static IProgramInfoFilter channel(Integer... channelIds) {
		return new ProgramInfoChannelFilter(channelIds);
	}
	
	/**
	 * Creates a filter to filter recordings by channel.
	 * @see IProgramInfo#getChannelID()
	 */	
	public static IProgramInfoFilter channel(IBasicChannelInfo... channelInfos) {
		return new ProgramInfoChannelFilter(channelInfos);
	}
	
	/**
	 * Creates a filter to filter recordings by storage-group.
	 * @see IProgramInfo#getStorageGroup()
	 */		
	public static IProgramInfoFilter storageGroup(String... storageGroups) {
		return new ProgramInfoStorageGroupFilter(storageGroups);
	}
	
	/**
	 * Creates a filter to filter recordings by recording-group.
	 * @see IProgramInfo#getRecordingGroup()
	 */
	public static IProgramInfoFilter recordingGroup(String... recordingGroups) {
		return new ProgramInfoRecordingGroupFilter(recordingGroups);
	}
	
	/**
	 * Creates a filter to filter recordings by unique program id.
	 * @see IProgramInfo#getUniqueProgramId()
	 */	
	public static IProgramInfoFilter uniqueProgramId(String... uniqueIds) {
		return new ProgramInfoUniqueIdFilter(UNIQUE_PROGRAM_ID,uniqueIds);
	}
	
	/**
	 * Creates a filter to filter recordings by unique recording id.
	 * @see IProgramInfo#getUniqueRecordingId()
	 */	
	public static IProgramInfoFilter uniqueRecordingId(String... uniqueIds) {
		return new ProgramInfoUniqueIdFilter(UNIQUE_RECORDING_ID,uniqueIds);
	}
	
	
	/**
	 * Creates a filter to filter recordings by recording id.
	 * @see IProgramInfo#getRecordingId()
	 */		
	public static IProgramInfoFilter recordingId(Integer... recordingIds) {
		return new ProgramInfoRecordingIdFilter(recordingIds);
	}	
	
	/**
	 * Creates a filter to filter recordings by channel-id and recording-start-time.
	 * @see IProgramInfo#getChannelID()
	 * @see IProgramInfo#getRecordingStartTime()
	 */		
	public static IProgramInfoFilter channelIdRecStartTime(Integer channelId, Date recordingStartTime) {
		return new ProgramInfoChannelIdRecordingStartTimeFilter(channelId, recordingStartTime);
	}
	
	public static IProgramInfoFilter and(IProgramInfoFilter... filters) {
		return new ProgramInfoConjunctionFilter(filters);
	}
	
	public static IProgramInfoFilter or(IProgramInfoFilter... filters) {
		return new ProgramInfoDisjunctionFilter(filters);
	}
	
	public static IProgramInfoFilter not(IProgramInfoFilter filter) {
		return new ProgramInfoNegotiationFilter(filter);
	}
}
