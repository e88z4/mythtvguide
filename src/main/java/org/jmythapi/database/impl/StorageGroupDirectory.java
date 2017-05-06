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

import static org.jmythapi.database.IStorageGroupDirectory.Props.DIRNAME;
import static org.jmythapi.database.IStorageGroupDirectory.Props.GROUPNAME;
import static org.jmythapi.database.IStorageGroupDirectory.Props.HOSTNAME;
import static org.jmythapi.database.IStorageGroupDirectory.Props.DIRECTORY_ID;

import java.util.List;

import org.jmythapi.database.IStorageGroupDirectory;
import org.jmythapi.protocol.ProtocolVersion;

public class StorageGroupDirectory extends ADatabaseRow<IStorageGroupDirectory.Props> implements IStorageGroupDirectory {

	public StorageGroupDirectory(ProtocolVersion protoVersion, int dbVersion, List<String> data) {
		super(protoVersion, dbVersion, IStorageGroupDirectory.Props.class, data);
	}

	public Integer getDirectoryId() {
		return this.getPropertyValueObject(DIRECTORY_ID);
	}

	public String getGroupName() {
		return this.getPropertyValueObject(GROUPNAME);
	}

	public String getHostName() {
		return this.getPropertyValueObject(HOSTNAME);
	}	
	
	public String getDirName() {
		return this.getPropertyValueObject(DIRNAME);
	}
}
