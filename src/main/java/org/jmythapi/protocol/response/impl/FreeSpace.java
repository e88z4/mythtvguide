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
import static org.jmythapi.protocol.response.IFreeSpace.Props.TOTAL_SPACE;
import static org.jmythapi.protocol.response.IFreeSpace.Props.USED_SPACE;

import java.util.ArrayList;
import java.util.Arrays;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IFreeSpace;

/**
 * @deprecated {@mythProtoVersion 17}
 */
@MythProtoVersionAnnotation(to=PROTO_VERSION_17)
public class FreeSpace extends AFreeSpace<IFreeSpace.Props> implements IFreeSpace {
	
	
	public FreeSpace(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	public FreeSpace(ProtocolVersion protoVersion, long totalSizeMB, long usedSizeMB) {
		super(protoVersion,Props.class,new ArrayList<String>(Arrays.asList(new String[]{
			Long.toString(totalSizeMB),
			Long.toString(usedSizeMB)
		})));
	}
	
	public Long getTotalSpace() {
		final Long totalSizeMB = this.getPropertyValueObject(TOTAL_SPACE);
		if(totalSizeMB == null) return null;
		return Long.valueOf(totalSizeMB.longValue() * 1024l * 1024l);
	}
	
	public Long getUsedSpace() {
		final Long usedSizeMB = this.getPropertyValueObject(USED_SPACE);
		if(usedSizeMB == null) return null;
		return Long.valueOf(usedSizeMB.longValue() * 1024l * 1024l);
	}
	
	public void addUsedSpace(long spaceDiff) {
		long usedSpace = this.getUsedSpace();
		usedSpace += spaceDiff;
		this.setUsedSpace(usedSpace);
	}
	
	public void setUsedSpace(long usedSpace) {
		final long usedSpaceMB = (usedSpace / 1024l) / 1024l; 
		this.setPropertyValueObject(USED_SPACE, Long.valueOf(usedSpaceMB));
	}
}
