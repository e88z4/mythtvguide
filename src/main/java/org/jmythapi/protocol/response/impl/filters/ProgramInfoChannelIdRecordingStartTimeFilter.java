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
package org.jmythapi.protocol.response.impl.filters;

import java.util.Date;

import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramInfoFilter;

public class ProgramInfoChannelIdRecordingStartTimeFilter implements IProgramInfoFilter {

	private final Integer channelId;
	private final Date recStartTime;

	public ProgramInfoChannelIdRecordingStartTimeFilter(Integer channelId, Date recStartTime) {
		this.channelId = channelId;
		this.recStartTime = recStartTime;
	}	
	
	public boolean accept(IProgramInfo program) {
		if(program == null) return false;
		
		final Integer pChannelId = program.getChannelID();
		if(pChannelId == null) return false;
		else if(!this.channelId.equals(pChannelId)) return false;
		
		final Date pChannelRecStartTime = program.getRecordingStartTime();
		if(pChannelRecStartTime == null) return false;
		else if(!this.recStartTime.equals(pChannelRecStartTime)) return false;
		
		return true;
	}

}
