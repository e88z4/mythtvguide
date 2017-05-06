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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramInfoList;
import org.jmythapi.protocol.response.IRecordingsPending;
import org.jmythapi.protocol.response.ProgramInfoFilters;
import org.jmythapi.protocol.response.IProgramRecordingStatus.Status;
import org.jmythapi.utils.EncodingUtils;

public class RecordingsPending extends ARecordings<IRecordingsPending.Props> implements IRecordingsPending {
	
	public RecordingsPending(IMythPacket packet) {
		super(IRecordingsPending.Props.class, packet);
	}
	
	public RecordingsPending(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion, Props.class, responseArgs);
	}	
	
	public boolean hasConflicts() {
		return this.getConflictsCount() > 0;
	}
	
	public int getConflictsCount() {
		return Integer.valueOf(this.getPropertyValue(IRecordingsPending.Props.CONFLICTS));
	}
	
	@Override
	protected int getSizePropertyIndex() {
		return IRecordingsPending.Props.SIZE.ordinal();
	}
	
	public Map<Integer, IProgramInfo> getNextPendingRecordings() {
		final Map<Integer,IProgramInfo> nextRecordings = new HashMap<Integer, IProgramInfo>();
		
		if(!this.isEmpty()) {
			for(IProgramInfo pendingRecording : this.getProgramInfoList(ProgramInfoFilters.status(Status.WILL_RECORD))) {
				// getting the recorder-ID
				final Integer recorderId = pendingRecording.getCardID();
				if(recorderId == null) continue;
				
				// getting the recording start time
				final Date recordingStartDate = pendingRecording.getRecordingStartTime();
				if(recordingStartDate == null) continue;
				
				if(nextRecordings.containsKey(recorderId)) {
					final IProgramInfo nextRecording = nextRecordings.get(recorderId);
					final Date nextRecordingDate = nextRecording.getRecordingStartTime();
					
					if(nextRecordingDate.compareTo(recordingStartDate) > 0) {
						nextRecordings.put(recorderId,pendingRecording);
					}
				} else {
					nextRecordings.put(recorderId,pendingRecording);
				}
			}
		}
		
		return nextRecordings;
	}	
	
	public Map<Integer, Date> getNextPendingRecordingsDates() {
		final Map<Integer,Date> dates = new HashMap<Integer, Date>();
		final Map<Integer,IProgramInfo> nextRecordings = this.getNextPendingRecordings();
		
		for(Entry<Integer,IProgramInfo> nextRecording : nextRecordings.entrySet()) {
			dates.put(nextRecording.getKey(),nextRecording.getValue().getRecordingStartTime());
		}
		
		return dates;
	}	
	
	public Date getNextPendingRecordingDate() {
		final Map<Integer, Date> nextPendingRecordingsDates = this.getNextPendingRecordingsDates();
		if(nextPendingRecordingsDates == null || nextPendingRecordingsDates.isEmpty()) return null;
		
		Date date = null;
		for(Date nextRecordingDate : nextPendingRecordingsDates.values()) {
			if(date == null || date.compareTo(nextRecordingDate)>0) {
				date = nextRecordingDate;
			}
		}
		return date;
	}
	
	public int getMinutesToNextRecording() {
		final Date now = new Date();
		final Date nextRecordingDate = this.getNextPendingRecordingDate();
		if(nextRecordingDate == null) return -1;
		return EncodingUtils.getMinutesDiff(now,nextRecordingDate);
	}
	
	/**
	 * Creates a new pending recordings list from a program list.
	 * 
	 * @param programs
	 * 		the program list
	 * @return
	 * 		the pending recordings list
	 */
	public static final RecordingsPending valueOf(IProgramInfoList programs) {
		if(programs == null) return null;
		
		// count conflicts
		int conflicts = 0;
		for(IProgramInfo program : programs) {
			if(program.getRecordingStatus().hasStatus(Status.CONFLICT)) {
				conflicts++;
			}
		}
		
		// build argument list
		final ArrayList<String> args = new ArrayList<String>();
		args.add(Integer.toString(conflicts));
		args.addAll(programs.getPropertyValues());
		
		// create a new object
		return new RecordingsPending(
			programs.getVersionNr(),
			args
		);		
	}
	
	/**
	 * Creates a new empty list
	 * 
	 * @param protoVersion
	 * 		the protocol version to use
	 * @return
	 * 		the empty list
	 */
	public static final RecordingsPending emptyList(ProtocolVersion protoVersion) {
		return new RecordingsPending(protoVersion,Arrays.asList(new String[]{"0","0"}));
	}
}
