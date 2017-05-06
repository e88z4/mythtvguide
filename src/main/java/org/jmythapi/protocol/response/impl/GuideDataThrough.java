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

import static org.jmythapi.IGuideDataThrough.Props.DATE_TIME;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_15;

import java.util.Date;

import org.jmythapi.IGuideDataThrough;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.utils.EncodingUtils;

@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
public class GuideDataThrough extends AMythResponse<IGuideDataThrough.Props> implements IGuideDataThrough {
	public GuideDataThrough(IMythPacket packet) {
		super(IGuideDataThrough.Props.class, packet);
	}

	public Date getDate() {
		return this.getPropertyValueObject(DATE_TIME);
	}
	
	public int getHours() {
		return EncodingUtils.getHoursDiff(new Date(),getDate());
	}
}
