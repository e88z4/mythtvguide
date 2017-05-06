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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_47;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;

import java.util.List;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.impl.FreeSpaceList.Entry;

/**
 * One entry of an {@link IFreeSpaceList} response.
 * <p>
 * A response example:
 * <br>
 * {@mythResponseExample 
 * 		<pre><00>HOSTNAME: mythbox | <01>DIRECTORIES: mythbox:/mnt/data/mythpinky | <02>IS_LOCAL: false | <03>FILESYSTEM_ID: 1 | <04>STORAGE_GROUP_ID: 12 | <05>BLOCK_SIZE: 131072 | <06>TOTAL_SPACE1: 0 | <07>TOTAL_SPACE2: 1922845568 | <08>USED_SPACE1: 0 | <09>USED_SPACE2: 1814276224</pre>
 * }
 * <br>
 */
public interface IFreeSpaceListEntry extends IVersionable, IBasicFreeSpace, IPropertyAware<IFreeSpaceListEntry.EntryProps> {

	/**
	 * The properties of an {@link IFreeSpaceList} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum EntryProps {
		/**
		 * Hostname.
		 * <p>
		 * The name of the backend using this directory.
		 * 
		 * @see Entry#getHostName()
		 */
		HOSTNAME,
		
		/**
		 * Directories.
		 * <p>
		 * A list of directories contained in the storage group
		 * 
		 * @since {@mythProtoVersion 32}
		 * @see Entry#getDirectories()
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		DIRECTORIES,
		
		/**
		 * Local Filesystem.
		 * <p>
		 * Specifies if the file system is local to the backend.
		 * 
		 * @since {@mythProtoVersion 32}
		 * @see Entry#isLocal()
		 */
		@MythParameterType(Boolean.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		IS_LOCAL,
		
		/**
		 * File system ID.
		 * 
		 * @since {@mythProtoVersion 32}
		 * @see Entry#getFileSystemID()
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		FILESYSTEM_ID,
		
		/**
		 * Storage Group ID.
		 * <p>
		 * If a storage group has multiple directories, this is only the 
		 * id of the first directory listed in storage groups database table.
		 * 
		 * @since {@mythProtoVersion 37}
		 * @see Entry#getStorageGroupID()
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
		STORAGE_GROUP_ID,
		
		/**
		 * Filesystem block size.
		 * 
		 * @since {@mythProtoVersion 47}
		 * @see Entry#getBlockSize()
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_47)
		BLOCK_SIZE,
		
		/**
		 * Total disk space - part 1.
		 * 
		 * @see Entry#getTotalSpace()
		 * 
		 * @deprecated {@mythProtoVersion 66}, replaced by {@link #TOTAL_SPACE}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_66)
		TOTAL_SPACE1,
		
		/**
		 * Total disk space - part 2.
		 * 
		 * @see Entry#getTotalSpace()
		 * 
		 * @deprecated {@mythProtoVersion 66}, replaced by {@link #TOTAL_SPACE}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_66)
		TOTAL_SPACE2,
		
		/**
		 * Total disk space.
		 * 
		 * @see Entry#getTotalSpace()
		 * 
		 * @since {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_66)
		TOTAL_SPACE,
		
		/**
		 * Used disk space - part 1.
		 * 
		 * @see Entry#getUsedSpace()
		 * 
		 * @deprecated {@mythProtoVersion 66}, replaced by {@link #USED_SPACE}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_66)
		USED_SPACE1,
		
		/**
		 * Used disk space - part 2.
		 * 
		 * @see Entry#getUsedSpace()
		 * 
		 * @deprecated {@mythProtoVersion 66}, replaced by {@link #USED_SPACE}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(to=PROTO_VERSION_66)
		USED_SPACE2,
		
		/**
		 * Used disk space.
		 * 
		 * @see Entry#getUsedSpace()
		 * 
		 * @since {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_66)
		USED_SPACE
	}

	public abstract String getHostName();

	/**
	 * Gets all directory of the file system.
	 * 
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_32)
	public abstract List<String> getDirectories();
	
	/**
	 * Gets all directory of the file system.
	 * 
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_32)	
	public abstract List<String> getDirectories(boolean withHostnames);
	
	/**
	 * Checks if the given directory exists in the file system entry.
	 * 
	 * @param directory
	 * 		the desired directory
	 * @return
	 * 		{@code true} if the directory exists
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_32)
	public boolean hasDirectory(String directory);

	/**
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_32)
	public abstract Boolean isLocal();

	/**
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_32)
	public abstract String getFileSystemID();

	/**
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_37)
	public abstract Integer getStorageGroupID();

	/**
	 * @since {@mythProtoVersion 47}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_47)
	public abstract Integer getBlockSize();

	/**
	 * {@inheritDoc}
	 * @see IFreeSpaceListEntry.EntryProps#TOTAL_SPACE
	 */
	public abstract Long getTotalSpace();

	/**
	 * {@inheritDoc}
	 * @see IFreeSpaceListEntry.EntryProps#USED_SPACE
	 */	
	public abstract Long getUsedSpace();
	
	public void addUsedSpace(long spaceDiff);
	
	public void setUsedSpace(long usedSpace);
}