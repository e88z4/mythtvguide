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

import static org.jmythapi.protocol.response.IStorageGroupFile.Props.FILE_PATH;
import static org.jmythapi.protocol.response.IStorageGroupFile.Props.LAST_MOD;
import static org.jmythapi.protocol.response.IStorageGroupFile.Props.FILE_SIZE;

import java.util.Date;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IStorageGroupFile;
import org.jmythapi.protocol.utils.EnumUtils;

public class StorageGroupFile extends AMythResponse<IStorageGroupFile.Props> implements IStorageGroupFile {
	public StorageGroupFile(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	public StorageGroupFile(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion,Props.class,responseArgs);
	}

	public boolean isDirectory() {
		final String type = this.getFileType();
		if(type == null) return false;
		return !type.equalsIgnoreCase("file");
	}	
	
	public String getFileType() {
		return this.getPropertyValueObject(Props.FILE_TYPE);
	}
	
	public String getFilePath() {
		return this.getPropertyValueObject(FILE_PATH);
	}

	public Date getLastModified() {
		return this.getPropertyValueObject(LAST_MOD);
	}

	public Long getFileSize() {
		return this.getPropertyValueObject(FILE_SIZE);
	}
	
	public static StorageGroupFile valueOf(IMythPacket packet) {
		final List<String> args = packet.getPacketArgs();
		if(args.get(0).startsWith("EMPTY LIST")) return null;
		else if(args.get(0).startsWith("SLAVE UNREACHABLE")) return null;

		// getting the expected args length
		final ProtocolVersion protoVersion = packet.getVersionNr();
		final int expectedLength = EnumUtils.getEnumLength(IStorageGroupFile.Props.class, protoVersion);
		
		// add the type file if required
		if(args.size() == expectedLength -1) {
			args.add(0,"file");	
		}
		
		return new StorageGroupFile(protoVersion,args);
	}
}
