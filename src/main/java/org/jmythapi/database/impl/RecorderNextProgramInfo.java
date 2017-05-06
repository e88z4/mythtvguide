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

import java.util.Date;
import java.util.List;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;
import org.jmythapi.utils.EncodingUtils;

public class RecorderNextProgramInfo extends ADatabaseRow<IRecorderNextProgramInfo.Props> implements IRecorderNextProgramInfo {

	public RecorderNextProgramInfo(ProtocolVersion protoVersion, int dbVersion, List<String> data) {
		super(protoVersion, dbVersion, Props.class, data);
	}
	
	public String getCategory() {
		return this.getPropertyValueObject(Props.CATEGORY);
	}

	public Integer getChannelID() {
		return this.getPropertyValueObject(Props.CHANNEL_ID);
	}

	public String getChannelIconPath() {
		return this.getPropertyValueObject(Props.CHANNEL_ICON_PATH);
	}

	public String getChannelNumber() {
		return this.getPropertyValueObject(Props.CHANNEL_NUMBER);
	}

	public String getChannelSign() {
		return this.getPropertyValueObject(Props.CHANNEL_SIGN);
	}

	public String getDescription() {
		return this.getPropertyValueObject(Props.DESCRIPTION);
	}

	public Date getEndDateTime() {
		return this.getPropertyValueObject(Props.END_DATE_TIME);
	}

	public String getProgramID() {
		return this.getPropertyValueObject(Props.PROGRAM_ID);
	}

	public String getSeriesID() {
		return this.getPropertyValueObject(Props.SERIES_ID);
	}

	public Date getStartDateTime() {
		return this.getPropertyValueObject(Props.START_DATE_TIME);
	}

	public String getSubtitle() {
		return this.getPropertyValueObject(Props.SUBTITLE);
	}

	public String getTitle() {
		return this.getPropertyValueObject(Props.TITLE);
	}
	
	public String getFullTitle() {
		return EncodingUtils.getFormattedTitle(this.getTitle(),this.getSubtitle());
	}	

	public int getDuration() {
		return EncodingUtils.getMinutesDiff(this.getStartDateTime(),this.getEndDateTime());
	}

	public int getMinutesAfterMidnight() {
		return EncodingUtils.getMinutesAfterMidnight(this.getStartDateTime());
	}

	public String getUniqueProgramId() {
		if(!isValid()) return null;
		
		return String.format(
			"%d_%2$tY%2$tm%2$td%2$tH%2$tM%2$tS",
			this.getChannelID(),
			this.getStartDateTime()
		);
	}

	public boolean isValid() {
		return (this.getStartDateTime() != null 
			&& this.getEndDateTime() != null 
			&& this.getChannelID() != null
			&& this.getTitle() != null
		);
	}

}
