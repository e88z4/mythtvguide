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

import java.util.Date;
import java.util.List;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IBasicProgramInfo;
import org.jmythapi.utils.EncodingUtils;

public abstract class AProgramInfo <E extends Enum<E>> extends AMythResponse<E> implements IBasicChannelInfo, IBasicProgramInfo {

	public AProgramInfo(Class<E> propsClass, IMythPacket packet) {
		super(propsClass, packet);
	}
	
	public AProgramInfo(ProtocolVersion protoVersion, Class<E> propsClass) {
		super(protoVersion,propsClass);
	}
	
	public AProgramInfo(ProtocolVersion protoVersion, Class<E> propsClass, List<String> responseArgs) {
		super(protoVersion, propsClass, responseArgs);
	}
	
	/* ======================================================================
	 * IBasicProgramInfo Methods
	 * ====================================================================== */
	public abstract String getTitle();	
	public abstract String getSubtitle();
	public abstract String getDescription();
	public abstract String getCategory();
	public abstract Date getStartDateTime();
	public abstract Date getEndDateTime();	
	public abstract String getSeriesID();
	public abstract String getProgramID();	
	
	/* ======================================================================
	 * IBasicChannelInfo Methods
	 * ====================================================================== */	
	public abstract String getChannelSign();
	public abstract String getChannelNumber();
	public abstract Integer getChannelID();

	/* ======================================================================
	 * Convenience Methods
	 * ====================================================================== */	
	public String getFullTitle() {
		return EncodingUtils.getFormattedTitle(this.getTitle(),this.getSubtitle());
	}

	public String getUniqueProgramId() {
		if(!isValid()) return null;
		
		return EncodingUtils.generateId(
			this.getChannelID(),
			this.getStartDateTime()
		);
	}

	public int getDuration() {
		final Date start = this.getStartDateTime();
		final Date end = this.getEndDateTime();
		return EncodingUtils.getMinutesDiff(start, end);
	}
	
	public int getMinutesAfterMidnight() {
		final Date start = this.getStartDateTime();		
		return EncodingUtils.getMinutesAfterMidnight(start);
	}
	
	public boolean isValid() {
		return (this.getStartDateTime() != null 
			&& this.getEndDateTime() != null 
			&& this.getChannelID() != null
			&& this.getTitle() != null
		);
	}
}
