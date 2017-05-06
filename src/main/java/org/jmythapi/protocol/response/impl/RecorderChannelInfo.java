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

import static org.jmythapi.IRecorderChannelInfo.Props.CHANNEL_ID;
import static org.jmythapi.IRecorderChannelInfo.Props.CHANNEL_NAME;
import static org.jmythapi.IRecorderChannelInfo.Props.CHANNEL_NUMBER;
import static org.jmythapi.IRecorderChannelInfo.Props.CHANNEL_SIGN;
import static org.jmythapi.IRecorderChannelInfo.Props.SOURCE_ID;
import static org.jmythapi.IRecorderChannelInfo.Props.XMLTV_ID;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;

@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
public class RecorderChannelInfo extends AMythResponse<IRecorderChannelInfo.Props> implements IBasicChannelInfo, IRecorderChannelInfo {
	
	public RecorderChannelInfo(IMythPacket packet) {
		super(Props.class, packet);
	}

	public RecorderChannelInfo(ProtocolVersion protoVersion,String... responseArgs) {
		super(protoVersion,Props.class,responseArgs);
	}
	
	/* ======================================================================
	 * IBasicChannelInfo Methods
	 * ====================================================================== */
	
	public Integer getChannelID() {
		return Integer.valueOf(this.getPropertyValue(CHANNEL_ID));
	}
	
	public String getChannelSign() {
		return this.getPropertyValue(CHANNEL_SIGN);
	}	
	
	public String getChannelNumber() {
		return this.getPropertyValue(CHANNEL_NUMBER);
	}		
	
	/* ======================================================================
	 * Additional Methods
	 * ====================================================================== */	
	public String getChannelName() {
		return this.getPropertyValue(CHANNEL_NAME);
	}		
	
	public Integer getSourceID() {
		return Integer.valueOf(this.getPropertyValue(SOURCE_ID));
	}	
	
	public String getXmlTvID() {
		return this.getPropertyValue(XMLTV_ID);
	}
}
