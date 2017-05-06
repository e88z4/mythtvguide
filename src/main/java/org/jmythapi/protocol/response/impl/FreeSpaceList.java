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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_47;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.BLOCK_SIZE;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.DIRECTORIES;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.FILESYSTEM_ID;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.HOSTNAME;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.IS_LOCAL;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.STORAGE_GROUP_ID;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.TOTAL_SPACE;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.TOTAL_SPACE1;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.TOTAL_SPACE2;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.USED_SPACE;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.USED_SPACE1;
import static org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps.USED_SPACE2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jmythapi.impl.ResultList;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IFreeSpaceList;
import org.jmythapi.protocol.response.IFreeSpaceListEntry;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * @since {@mythProtoVersion 17}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
public class FreeSpaceList extends AFreeSpace<FreeSpaceList.Props> implements Iterable<IFreeSpaceListEntry>, IFreeSpaceList {
	
	private static final String TOTAL_DISK_SPACE = "TotalDiskSpace";

	public static enum Props {
		// NO PROPERTIES AVAILABLE
	}
	
	public FreeSpaceList(IMythPacket packet) {
		super(Props.class, packet);
	}

	/**
	 * The size of the response array must be a multiple of {@code IFreeSpaceListEntry.EntryProps.values().size()}.
	 * There is no size value at the beginning of the response list (e.g. as available for program-lists).
	 */
	@Override
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		final int expectedSize = EnumUtils.getEnumLength(IFreeSpaceListEntry.EntryProps.class, this.protoVersion);
		final int currentSize = (responseArgs==null)?0:responseArgs.size();
		
		if (currentSize != 0 && currentSize % expectedSize != 0) {
			throw new IllegalArgumentException(String.format(
				"%d args expected but %d args found.",
				((currentSize / expectedSize)+1)*expectedSize,
				currentSize
			));
		}
	}
	
	public Long getTotalSpace() {
		final List<IFreeSpaceListEntry> entryList = this.asList(true);
		if(this.hasSummaryEntry()) {
			return entryList.get(entryList.size()-1).getTotalSpace();
		}

		long totalSpace = 0;
		for(IFreeSpaceListEntry entry : entryList) {
			final Long entryTotalSpace = entry.getTotalSpace();
			if(entryTotalSpace == null) continue;
			totalSpace += entryTotalSpace.longValue();
		}
		return Long.valueOf(totalSpace);
	}
	
	public Long getUsedSpace() {
		final List<IFreeSpaceListEntry> entryList = this.asList(true);
		if(this.hasSummaryEntry()) {
			return entryList.get(entryList.size()-1).getUsedSpace();
		}
		
		long totalUsedSpace = 0;			
		for(IFreeSpaceListEntry entry : entryList) {
			final Long entryUsedSpace = entry.getUsedSpace();
			if(entryUsedSpace == null) continue;
			totalUsedSpace += entryUsedSpace.longValue();
		}
		return Long.valueOf(totalUsedSpace);
	}
	
	public List<IFreeSpaceListEntry> asList() {
		return this.asList(false);
	}
	
	public IFreeSpaceListEntry get(int idx) {
		final List<IFreeSpaceListEntry> list = this.asList(false);
		return list.get(idx);
	}
	
	private List<IFreeSpaceListEntry> asList(boolean withSummaryEntry) {
		final List<IFreeSpaceListEntry> entryList = new ResultList<IFreeSpaceListEntry>();
		
		final int totalSize = this.getPropertyCount();
		final int entryArgsLength = EnumUtils.getEnumLength(IFreeSpaceListEntry.EntryProps.class, this.protoVersion);
		final int size = totalSize / entryArgsLength;
		
		int fromIdx = 0;
		int toIdx = entryArgsLength;
		for (int i=0; i<size; i++) {
			final List<String> entryArgs = this.respArgs.subList(fromIdx, toIdx);
			
			final Entry entry = new Entry(this.protoVersion, entryArgs);
			if (withSummaryEntry || !entry.getDirectories().get(0).equalsIgnoreCase(TOTAL_DISK_SPACE)) {
				entryList.add(entry);
			}
			
			fromIdx = toIdx;
			toIdx = toIdx + entryArgsLength;
		}		
		
		return entryList;
	}
	
	public int size() {
		final int totalSize = this.getPropertyCount();
		final int entryArgsLength = EnumUtils.getEnumLength(IFreeSpaceListEntry.EntryProps.class, this.protoVersion);
		final int entryCount = totalSize / entryArgsLength;
		return this.hasSummaryEntry() ? entryCount - 1 : entryCount;
	}
	
	private boolean hasSummaryEntry() {
		boolean hasSummaryEntry = false;
		
		// getting the amount of received arguments
		final int totalSize = this.getPropertyCount();
		
		// getting the args length of one entry
		final int entryArgsLength = EnumUtils.getEnumLength(IFreeSpaceListEntry.EntryProps.class, this.protoVersion);
	
		// getting the position of the "host" property
		final int entryDirectoryidx = EnumUtils.getEnumPosition(IFreeSpaceListEntry.EntryProps.DIRECTORIES,this.protoVersion);
		
		if(totalSize >= entryArgsLength) {
			final int hostIdx = (totalSize - entryArgsLength) + entryDirectoryidx;
			final String directory = this.getPropertyValue(hostIdx);
			if(directory.equals(TOTAL_DISK_SPACE)) {
				hasSummaryEntry = true;
			}
		}
		
		return hasSummaryEntry;
	}
	
	public Iterator<IFreeSpaceListEntry> iterator() {
		final List<IFreeSpaceListEntry> entries = this.asList();
		if (entries == null) return null;
		return entries.iterator();
	}	
		
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		
		final List<IFreeSpaceListEntry> entries = this.asList();
		if (entries != null) {
			for (IFreeSpaceListEntry entry : entries) {
				buff.append(entry).append("\r\n");
			}
		}
		
		return buff.toString();
	}	
	
	public class Entry extends AMythResponse<IFreeSpaceListEntry.EntryProps> implements IFreeSpaceListEntry {
		public Entry(IMythPacket packet) {
			super(EntryProps.class, packet);
		}
		
		public Entry(ProtocolVersion protoVersion, List<String> responseArgs) {
			super(protoVersion, EntryProps.class, responseArgs);
		}
		
		public String getHostName() {
			return this.getPropertyValue(HOSTNAME);
		}
		
		/**
		 * @since {@mythProtoVersion 32}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		public List<String> getDirectories() {
			return this.getDirectories(true);
		}
		
		public List<String> getDirectories(boolean withHostnames) {
			final String dirListStr = this.getPropertyValue(DIRECTORIES);
			if(dirListStr == null) return Collections.emptyList();
			
			final List<String> directories = new ArrayList<String>();
			for(String directory : dirListStr.split(",")) {
				if(!withHostnames) {
					int idx = directory.indexOf(':');
					if(idx != -1) directory = directory.substring(idx+1);
				}
				if(directory.endsWith("/")) directory = directory.substring(0,directory.length()-1);				
				directories.add(directory.trim());
			}
			return directories;
		}
		
		public boolean hasDirectory(String directory) {
			if(directory == null || directory.length() == 0) return false;
			if(directory.endsWith("/")) directory = directory.substring(0,directory.length()-1);
			
			for(String entryDirectory : this.getDirectories(false)) {
				if(entryDirectory.endsWith("/")) entryDirectory = entryDirectory.substring(0,entryDirectory.length()-1);
				
				if(entryDirectory.equals(directory)) {
					return true;
				}
			}
			
			return false;
		}
		
		/**
		 * @since {@mythProtoVersion 32}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		public Boolean isLocal() {
			final String temp = this.getPropertyValue(IS_LOCAL);
			if (temp == null) return null;
			return Boolean.valueOf(temp.equals("1"));
		}
		
		/**
		 * @since {@mythProtoVersion 32}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		public String getFileSystemID() {
			return this.getPropertyValue(FILESYSTEM_ID);
		}
		
		/**
		 * @since {@mythProtoVersion 37}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
		public Integer getStorageGroupID() {
			return this.getPropertyValueObject(STORAGE_GROUP_ID);
		}

		/**
		 * @since {@mythProtoVersion 47}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_47)
		public Integer getBlockSize() {
			final String temp = this.getPropertyValue(BLOCK_SIZE);
			if (temp == null) return null;
			return Integer.valueOf(temp);
		}

		@SuppressWarnings("deprecation")
		public Long getTotalSpace() {
			long totalSpaceKB = 0;
			if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
				totalSpaceKB = EncodingUtils.decodeLong(
					this.getPropertyValue(TOTAL_SPACE1),
					this.getPropertyValue(TOTAL_SPACE2)
				);
			} else {
				totalSpaceKB = (Long) this.getPropertyValueObject(TOTAL_SPACE);
			}
			
			return totalSpaceKB * 1024;
		}
		
		@SuppressWarnings("deprecation")
		public Long getUsedSpace() {
			long usedSpaceKB = 0;
			if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
				usedSpaceKB = EncodingUtils.decodeLong(
					this.getPropertyValue(USED_SPACE1),
					this.getPropertyValue(USED_SPACE2)
				);
			} else {
				usedSpaceKB = (Long) this.getPropertyValueObject(USED_SPACE);
			}
			
			return usedSpaceKB * 1024;
		}
		
		public void addUsedSpace(long spaceDiff) {
			long usedSpace = this.getUsedSpace();
			usedSpace += spaceDiff;
			this.setUsedSpace(usedSpace);
		}
		
		@SuppressWarnings("deprecation")
		public void setUsedSpace(long usedSpace) {
			final long usedSpaceKB = usedSpace / 1024;
			
			if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
				final String[] usedSpaceParts = EncodingUtils.encodeLong(usedSpaceKB);
				this.setPropertyValue(USED_SPACE1,usedSpaceParts[0]);
				this.setPropertyValue(USED_SPACE2,usedSpaceParts[1]);
			} else {
				this.setPropertyValueObject(USED_SPACE, usedSpaceKB);
			}
		}
		
		public Long getFreeSpace() {
			final Long totalSpace = this.getTotalSpace();
			if(totalSpace == null) return null;
			final Long usedSpace = this.getUsedSpace();
			if(usedSpace == null) return null;			
			return Long.valueOf(totalSpace.longValue() - usedSpace.longValue());
		}
	}
}
