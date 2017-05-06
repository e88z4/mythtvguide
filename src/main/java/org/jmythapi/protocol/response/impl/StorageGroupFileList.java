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
import java.util.Iterator;
import java.util.List;

import org.jmythapi.impl.ResultList;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.response.IStorageGroupFile;
import org.jmythapi.protocol.response.IStorageGroupFileList;
import org.jmythapi.protocol.utils.EnumUtils;

public class StorageGroupFileList extends AMythResponse<StorageGroupFileList.Props> implements IStorageGroupFileList {
	public static enum Props {
		// NO PROPERTIES AVAILABLE
	}
	
	public StorageGroupFileList(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	@Override
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		// no check size required. one list entry is one file
	}
	
	public List<IStorageGroupFile> asList() {
		final List<IStorageGroupFile> files = new ResultList<IStorageGroupFile>();
		if(this.respArgs != null) {
			for(String respArg : respArgs) {
				if(respArg == null) continue;
				
				// split into props
				final String[] entryArgs = respArg.split(FILE_PROPS_DELIM);
				
				// copy some properties to the target props
				final int requiredLength = EnumUtils.getEnumLength(IStorageGroupFile.Props.class,this.protoVersion);
				final String[] fileArgs = new String[requiredLength];
				
				// file-type
				fileArgs[IStorageGroupFile.Props.FILE_TYPE.ordinal()] = entryArgs.length==1?"file":entryArgs[0];
				
				// file-name
				fileArgs[IStorageGroupFile.Props.FILE_PATH.ordinal()] = entryArgs[entryArgs.length==1?0:1];
				
				
				if(entryArgs.length>2) {
					fileArgs[IStorageGroupFile.Props.FILE_SIZE.ordinal()] = entryArgs[2];
				}
				
				// create new file entry
				final IStorageGroupFile file = new StorageGroupFile(this.protoVersion,new ArrayList<String>(Arrays.asList(fileArgs)));
				files.add(file);
			}
		}
		return files;
	}

	public Iterator<IStorageGroupFile> iterator() {
		final List<IStorageGroupFile> files = this.asList();
		if(files == null) return null;
		return files.iterator();
	}
	
	public int size() {
		final int totalSize = this.getPropertyCount();
		final int entryArgsLength = EnumUtils.getEnumLength(IStorageGroupFile.Props.class, this.protoVersion);
		return totalSize / entryArgsLength;
	}	
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		
		final List<IStorageGroupFile> entries = this.asList();
		if (entries != null) {
			for (IStorageGroupFile entry : entries) {
				buff.append(entry).append("\r\n");
			}
		}
		
		return buff.toString();
	}	
}
