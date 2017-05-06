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
package org.jmythapi.protocol.events.impl;

import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IUpdateProgInfo;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.impl.ProgramInfo;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.protocol.utils.PropertyAwareUtils;

@SuppressWarnings("deprecation")
public class UpdateProgInfo extends AMythEvent<IUpdateProgInfo.Props> implements IUpdateProgInfo {

	public UpdateProgInfo(IMythPacket packet) {
		super(Props.class, packet);
	}

	@Override
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		final int expectedSize = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);	
		if (responseArgs == null || responseArgs.size() != expectedSize) {
			throw new IllegalArgumentException(String.format(
				"%d args expected but %d args found.",
				expectedSize, responseArgs==null?0:responseArgs.size()
			));
		}
	}
	
	public IProgramInfo getProgramInfo() {
		return new ProgramInfo(this.protoVersion,this.getPropertyValues());
	}

	public Integer getChannelID() {
		final IProgramInfo programInfo = this.getProgramInfo();
		if(programInfo == null) return null;
		return programInfo.getChannelID();
	}

	public Date getRecordingStartTime() {
		final IProgramInfo programInfo = this.getProgramInfo();
		if(programInfo == null) return null;
		return programInfo.getRecordingStartTime();
	}	
	
	public String getUniqueRecordingID() {
		final IProgramInfo programInfo = this.getProgramInfo();
		if(programInfo == null) return null;
		return programInfo.getUniqueRecordingId();
	}
	
	public EnumMap<org.jmythapi.protocol.response.IProgramInfo.Props, Object[]> getUpdatedProperties(IProgramInfo oldProgramInfo) {
		if(oldProgramInfo == null) return null;
		
		return PropertyAwareUtils.compare(oldProgramInfo,this.getProgramInfo());
	}		
	
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		
		buff.append(super.toString());
		buff.append(this.getProgramInfo());
		
		return buff.toString();
	}
}
