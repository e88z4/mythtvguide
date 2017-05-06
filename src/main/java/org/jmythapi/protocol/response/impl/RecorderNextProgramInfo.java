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

import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.CATEGORY;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.CHANNEL_ICON_PATH;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.CHANNEL_ID;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.CHANNEL_NUMBER;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.CHANNEL_SIGN;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.DESCRIPTION;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.END_DATE_TIME;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.PROGRAM_ID;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.SERIES_ID;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.START_DATE_TIME;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.SUBTITLE;
import static org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props.TITLE;

import java.util.Date;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.response.IBasicProgramInfo;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;

public class RecorderNextProgramInfo extends ARecorderProgramInfo<IRecorderNextProgramInfo.Props> implements IBasicChannelInfo, IBasicProgramInfo, IRecorderNextProgramInfo {
	
	public RecorderNextProgramInfo(IMythPacket packet) {
		super(IRecorderNextProgramInfo.Props.class, packet);
	}
	
	/* ======================================================================
	 * IBasicProgramInfo Methods
	 * ====================================================================== */	
	
	@Override
	public String getTitle() {
		return this.getPropertyValue(TITLE);
	}
	
	@Override
	public String getSubtitle() {
		return this.getPropertyValue(SUBTITLE);
	}
	
	@Override
	public String getDescription() {
		return this.getPropertyValue(DESCRIPTION);
	}
	
	@Override
	public String getCategory() {
		return this.getPropertyValue(CATEGORY);
	}
	
	@Override
	public Date getStartDateTime() {
		return this.getPropertyValueObject(START_DATE_TIME);
	}
	
	@Override
	public Date getEndDateTime() {
		return this.getPropertyValueObject(END_DATE_TIME);
	}
	
	@Override
	public String getSeriesID() {
		return this.getPropertyValueObject(SERIES_ID);
	}
	
	@Override
	public String getProgramID() {
		return this.getPropertyValueObject(PROGRAM_ID);
	}
	
	/* ======================================================================
	 * IBasicChannelInfo Methods
	 * ====================================================================== */
	
	@Override
	public Integer getChannelID() {
		return this.getPropertyValueObject(CHANNEL_ID);
	}		
	
	@Override
	public String getChannelSign() {
		return this.getPropertyValue(CHANNEL_SIGN);
	}
	
	@Override
	public String getChannelNumber() {
		return this.getPropertyValue(CHANNEL_NUMBER);
	}
	
	/* ======================================================================
	 * Additional Methods
	 * ====================================================================== */	
	@Override
	public String getChannelIconPath() {
		final String iconPath = this.getPropertyValue(CHANNEL_ICON_PATH);
		if("none".equals(iconPath)) return null;
		return iconPath;
	}
}
