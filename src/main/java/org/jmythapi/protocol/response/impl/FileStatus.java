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

import static org.jmythapi.protocol.response.IFileStatus.Props.FILE_EXISTS;
import static org.jmythapi.protocol.response.IFileStatus.Props.FILE_PATH;
import static org.jmythapi.protocol.response.IFileStatus.Props.MTIME;
import static org.jmythapi.protocol.response.IFileStatus.Props.SIZE;

import java.util.Arrays;
import java.util.Date;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IFileStatus;
import org.jmythapi.protocol.utils.EnumUtils;

public class FileStatus extends AMythResponse<IFileStatus.Props> implements IFileStatus {
	/**
	 * Constructs a status object from a protocol packet.
	 * @param packet
	 * 		the protocol packet
	 */
	public FileStatus(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	/**
	 * Constructs a status object for the given protocol version and response arguments.
	 * 
	 * @param protoVersion
	 * 		the protocol version
	 * @param responseArgs
	 * 		the response arguments
	 */
	public FileStatus(ProtocolVersion protoVersion, String... responseArgs) {
		super(protoVersion,Props.class,responseArgs);
	}

	public boolean fileExists() {
		return ((Boolean)this.getPropertyValueObject(FILE_EXISTS)).booleanValue();
	}

	public String getFilePath() {
		if(this.fileExists()) {
			return this.getPropertyValueObject(FILE_PATH);
		} else {
			return null;
		}
	}
	
	public Long getFileSize() {
		return this.getPropertyValueObject(SIZE);
	}
	
	public Date getLastModified() {
		return this.getPropertyValueObject(MTIME);
	}
		
	/**
	 * Constructs a new status object from a protocol packet and does some data correction.
	 * 
	 * @param packet
	 * 		the protocol packet
	 * @return
	 * 		the created status object
	 */
	public static FileStatus valueOf(IMythPacket packet) {
		return valueOf(
			packet.getVersionNr(),
			packet.getPacketArgs().toArray(new String[packet.getPacketArgsLength()])
		);
	}
	
	public static FileStatus valueOf(ProtocolVersion protoVersion, String[] args) {
		// determine the expected size of the response array
		final int expectedSize = EnumUtils.getEnumLength(Props.class,protoVersion);		
		final int actualSize = args.length;
		
		/*
		 * If the file was not found or the file-stat info could not be generated
		 * the response argument size is wrong. We need to fix this here.
		 */
		if(actualSize != expectedSize) {
			// create a new empty array
			final String[] newResponseArgs = new String[expectedSize];
			Arrays.fill(newResponseArgs,null);

			// copy arguments
			final String[] oldArgs = args;
			System.arraycopy(oldArgs, 0, newResponseArgs, 0, Math.min(expectedSize, oldArgs.length));
			
			// create a nfo object now
			return new FileStatus(protoVersion,newResponseArgs);
		} 
		return new FileStatus(protoVersion,args);
	}
	
	public static FileStatus valueOf(ProtocolVersion protoVersion, boolean fileExists, String fileName) {
		final String[] args = new String[]{Boolean.toString(fileExists),fileName};
		return valueOf(protoVersion, args);
	}	
}
