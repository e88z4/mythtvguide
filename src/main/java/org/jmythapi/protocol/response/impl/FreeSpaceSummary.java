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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.response.IFreeSpaceSummary.Props.TOTAL_SPACE;
import static org.jmythapi.protocol.response.IFreeSpaceSummary.Props.TOTAL_SPACE1;
import static org.jmythapi.protocol.response.IFreeSpaceSummary.Props.TOTAL_SPACE2;
import static org.jmythapi.protocol.response.IFreeSpaceSummary.Props.USED_SPACE;
import static org.jmythapi.protocol.response.IFreeSpaceSummary.Props.USED_SPACE1;
import static org.jmythapi.protocol.response.IFreeSpaceSummary.Props.USED_SPACE2;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IFreeSpaceSummary;
import org.jmythapi.utils.EncodingUtils;

@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
public class FreeSpaceSummary extends AFreeSpace<IFreeSpaceSummary.Props> implements IFreeSpaceSummary {
	
	public FreeSpaceSummary(IMythPacket packet) {
		super(Props.class, packet);
	}

	@SuppressWarnings("deprecation")
	public Long getTotalSpace() {
		if(this.protoVersion.compareTo(ProtocolVersion.PROTO_VERSION_66)<0) {
			return EncodingUtils.decodeLong(
				this.getPropertyValue(TOTAL_SPACE1),
				this.getPropertyValue(TOTAL_SPACE2)
			);
		} else {
			return (Long)this.getPropertyValueObject(TOTAL_SPACE);
		}
	}
	
	@SuppressWarnings("deprecation")
	public Long getUsedSpace() {
		if(this.protoVersion.compareTo(ProtocolVersion.PROTO_VERSION_66)<0) {
			return EncodingUtils.decodeLong(
				this.getPropertyValue(USED_SPACE1),
				this.getPropertyValue(USED_SPACE2)
			);
		} else {
			return (Long) this.getPropertyValueObject(USED_SPACE);
		}
	}
}
