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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramInfoFilter;
import org.jmythapi.protocol.response.IProgramInfoList;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.protocol.utils.PropertyAwareUtils;
import org.jmythapi.utils.EncodingUtils;

public class ProgramInfoList extends AMythResponse<IProgramInfoList.Props> implements Iterable<IProgramInfo>, IProgramInfoList {
	public ProgramInfoList(IMythPacket packet) {
		super(IProgramInfoList.Props.class, packet);
	}
	
	public ProgramInfoList(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion, IProgramInfoList.Props.class, responseArgs);
	}
	
	@Override
	protected int getExpectedSize(List<String> responseArgs) {
		// getting the value of the size field
		final int programInfoListSize = Integer.valueOf(responseArgs.get(Props.SIZE.ordinal()));
		
		// getting the length of the program-info properties
		final int programInfoSize = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);
		
		// getting the length of recording properties 
		final int recordingInfoSize = EnumUtils.getEnumLength(this.propsClass, this.protoVersion);
		
		// the expected size is
		return recordingInfoSize + (programInfoListSize*programInfoSize);
	}
	
	public int size() {
		return Integer.valueOf(this.getPropertyValue(IProgramInfoList.Props.SIZE));
	}
	
	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	public List<IProgramInfo> asList() {
		return this.asList(null);
	}
	
	public List<IProgramInfo> asList(IProgramInfoFilter filter) {
		final int size = this.size();
		if (size == 0) return Collections.emptyList();
		
		final ArrayList<IProgramInfo> progInfoList = new ArrayList<IProgramInfo>(size);
		
		// getting the expected length of the data
		final int progInfoArgsLengthExpected = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);
		final int progInfoArgsLengthCurrent = (this.respArgs.size() - 1) / size;
		if (progInfoArgsLengthExpected != progInfoArgsLengthCurrent) {
			logger.warning(String.format(
				"%d args per program-info expected but %d args found.",
				progInfoArgsLengthExpected,
				progInfoArgsLengthCurrent
			));
		}
		
		int fromIdx = 1;
		for (int i=0; i<size; i++) {
			int toIdx = fromIdx + progInfoArgsLengthExpected;
			final List<String> progInfoArgs = this.respArgs.subList(fromIdx, toIdx);
			final ProgramInfo progInfo = new ProgramInfo(this.protoVersion, progInfoArgs);
			if(filter==null || filter.accept(progInfo)) {
				progInfoList.add(progInfo);
			}
			fromIdx = toIdx;
		}
		
		return progInfoList;
	}
	
	public IProgramInfoList filter(IProgramInfoFilter filter) {
		if(filter == null) return this;
		
		final List<IProgramInfo> programs = this.asList(filter);
		return ProgramInfoList.valueOf(this.protoVersion,programs);
	}
	
	public IProgramInfoList[] multiFilter(IProgramInfoFilter... filters) {
		if(filters == null) return null;
		
		final Map<IProgramInfoFilter,List<IProgramInfo>> programGroups = new LinkedHashMap<IProgramInfoFilter, List<IProgramInfo>>();
		
		final List<IProgramInfo> allPrograms = this.asList();
		for(IProgramInfo program : allPrograms) {
			for(IProgramInfoFilter filter : filters) {
				if(filter == null || filter.accept(program)) {
					List<IProgramInfo> programs = null;
					if(programGroups.containsKey(filter)) {
						programs = programGroups.get(filter);
					} else {
						programs = new ArrayList<IProgramInfo>();
						programGroups.put(filter,programs);
					}
					programs.add(program);
				}
			}
		}
		
		final IProgramInfoList[] result = new IProgramInfoList[filters.length];
		for(int i=0; i<filters.length;i++) {
			final IProgramInfoFilter filter = filters[i];
			final List<IProgramInfo> programs = programGroups.get(filter);
			if(programs == null) {
				result[i] = ProgramInfoList.emptyList(this.protoVersion);
			} else {
				result[i] = ProgramInfoList.valueOf(this.protoVersion, programs);
			}
		}
		
		return result;
	}
	
	
	private int[] getProgramInfoIdx(int idx) throws IndexOutOfBoundsException {
		// the length of a single program-info object
		final int progInfoArgsLengthExpected = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);
		
		// calculate from and to
		final int fromIdx = 1 + (idx * progInfoArgsLengthExpected);
		final int toIdx = fromIdx + progInfoArgsLengthExpected;
		
		return new int[]{fromIdx,toIdx};
	}	
	
//	public IProgramInfo remove(int idx) throws IndexOutOfBoundsException {
//		// determine the index
//		final int[] programInfoIdx = this.getProgramInfoIdx(idx);
//		
//		// determine the arguments to remove
//		final List<String> argsToRemove = respArgs.subList(programInfoIdx[0],programInfoIdx[1]);
//		
//		// copy arguments
//		final List<String> progInfoArgs = new ArrayList<String>(argsToRemove);
//		
//		// delete old arguments and change list size
//		argsToRemove.clear();
//		setPropertyValue(Props.SIZE,Integer.toString(this.size()-1));
//		
//		// create a new program info object
//		return new ProgramInfo(this.protoVersion, progInfoArgs);
//	}
	
	public IProgramInfo get(int idx) {
		// determine the index
		final int[] programInfoIdx = this.getProgramInfoIdx(idx);
		
		// determine the arguments
		final List<String> progInfoArgs = this.respArgs.subList(programInfoIdx[0],programInfoIdx[1]);
		
		// create a new program info object
		return new ProgramInfo(this.protoVersion, progInfoArgs);
	}
	
	/**
	 * @see Iterable#iterator()
	 */
	public Iterator<IProgramInfo> iterator() {
		return this.iterator(null);
	}
	
	public Iterator<IProgramInfo> iterator(IProgramInfoFilter filter) {
		final List<IProgramInfo> programInfoList = this.asList(filter);
		return programInfoList.iterator();
	}	
	
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		
		if (this.size() > 0) {
			for (IProgramInfo progInfo : this.asList()) {
				buf.append(progInfo.toString()).append("\r\n");
			}
		}
		
		return buf.toString();
	}
	
	public Map<String,IProgramInfo> asMap(MapKey mapKey) {
		return this.generateMap(mapKey, IProgramInfo.class);
	}
	
	public Map<String,Integer> getIndexMap(MapKey mapKey) {
		return this.generateMap(mapKey, Integer.class);
	}
	
	@SuppressWarnings("unchecked")
	private <R> Map<String,R> generateMap(MapKey mapKey,Class<R> valueType) {
		if(mapKey == null) mapKey = MapKey.UNIQUE_RECORDING_ID;
		
		boolean returnIndex = false;
		if(IProgramInfo.class.isAssignableFrom(valueType)) {
			returnIndex = false;
		} else if(Integer.class.isAssignableFrom(valueType)) {
			returnIndex = true;
		} else {
			throw new IllegalArgumentException();
		}		
		
		final LinkedHashMap<String,R> programMap = new LinkedHashMap<String,R>();
		
		final List<IProgramInfo> programs = asList();
		int programIndex = -1;
		for(IProgramInfo program : programs) {
			programIndex++;
			if(!program.isValid()) continue;
			
			String id = null;
			switch (mapKey) {
				case UNIQUE_RECORDING_ID:
					id = program.getUniqueRecordingId();
					break;
				
				case UNIQUE_PROGRAM_ID:
					id = program.getUniqueProgramId();
					break;
					
				default:
					id = null;
					break;
			}
			
			if(id != null) {
				if(returnIndex) {
					programMap.put(id,(R)Integer.valueOf(programIndex));
				} else {
					programMap.put(id,(R)program);
				}
			}
		}
		
		return (Map<String, R>) programMap;
	}
	
	public Map<Object, IProgramInfoList> groupBy(IProgramInfo.Props prop) {
		return this.groupBy(prop, null);
	}
	
	
	public Map<Object, IProgramInfoList> groupBy(IProgramInfo.Props prop, IProgramInfoFilter filter) {
		if(prop == null) return Collections.emptyMap();

		final Map<Object,IProgramInfoList> result = new HashMap<Object, IProgramInfoList>();
		
		// group by programs
		final Map<Object,List<IProgramInfo>> entries = PropertyAwareUtils.groupListByProperty(this,prop, filter);
		if(entries != null) {
			for(Entry<Object,List<IProgramInfo>> entry : entries.entrySet()) {
				result.put(
					entry.getKey(),
					ProgramInfoList.valueOf(this.getVersionNr(),entry.getValue())
				);
			}
		}
		
		return result;
	}
	
	public long getTotalFilesSize() {
		return getTotalFileSize(this);
	}
	
	public long getTotalDuration() {
		return getTotalDuration(this,false);
	}	
	
	public static final long getTotalFileSize(Iterable<IProgramInfo> programs) {
		if(programs == null) return 0;
		
		long totalSize = 0;
		for(IProgramInfo program : programs) {
			final long fileSize = program.getFileSize();
			if(fileSize <= 0) continue;
			totalSize += fileSize;
		}
		return totalSize;		
	}
	
	public static final long getTotalDuration(Iterable<IProgramInfo> programs, boolean checkCurrentTime) {
		if(programs == null) return 0;
		
		final Date now = new Date();
		long totalDuration = 0;
		for(IProgramInfo program : programs) {
			long duration = 0;
			
			final Date start = program.getStartDateTime();
			if(start.after(now)) continue;
			
			final Date end = program.getEndDateTime();
			if(end.after(now)) {
				duration = EncodingUtils.getMinutesDiff(start, now);
			} else {
				duration = EncodingUtils.getMinutesDiff(start, end);
			}			
			totalDuration += duration;
		}
		return totalDuration;		
	}
	
	/**
	 * Converts a collection of program-info objects into a program-info list.
	 * 
	 * @param programs
	 * 		the collection of programs.
	 * @return
	 * 		the created program-info list
	 */
	public static final ProgramInfoList valueOf(ProtocolVersion protoVersion, Collection<IProgramInfo> programs) {
		final int progListSize = programs==null?0:programs.size();
		final int argsListSize = EnumUtils.getEnumLength(IProgramInfo.Props.class,protoVersion);
		
		final ArrayList<String> args = new ArrayList<String>((argsListSize * progListSize) + 1);
		// IProgramInfoList.Props.SIZE 
		args.add(Integer.toString(progListSize)); 
		
		// a list of programs
		if(programs != null) {
			int programCounter = 0;
			for(IProgramInfo program : programs) {
				if(!protoVersion.equals(program.getVersionNr())) {
					throw new IllegalArgumentException(String.format(
						"Programs at index %d has a different version number.",
						Integer.valueOf(programCounter)
					));
				}
				args.addAll(program.getPropertyValues());
				programCounter++;
			}
		}
		
		return new ProgramInfoList(protoVersion,args);
	}
	
	/**
	 * Creates an empty program-info list
	 * 
	 * @param protoVersion
	 * 		the protocol version 
	 * @return
	 * 		the empty list
	 */
	public static final ProgramInfoList emptyList(ProtocolVersion protoVersion) {
		return new ProgramInfoList(protoVersion,Arrays.asList(new String[]{"0"}));
	}
}
