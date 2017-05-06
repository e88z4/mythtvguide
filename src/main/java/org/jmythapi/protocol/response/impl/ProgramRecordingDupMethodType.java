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

import static org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags.DUP_CHECK_UNKNOWN;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramRecordingDupMethodType;
import org.jmythapi.protocol.utils.EnumUtils;

public class ProgramRecordingDupMethodType extends AEnumFlagGroup<IProgramRecordingDupMethodType.Flags> implements IProgramRecordingDupMethodType {
	private static final long serialVersionUID = 1L;

	public ProgramRecordingDupMethodType(ProtocolVersion protoVersion, long flagValues) {
		super(Flags.class, protoVersion, flagValues);
	} 
	
	/* ============================================================================
	 * VALUE_OF methods
	 * ============================================================================ */
	
	public static ProgramRecordingDupMethodType valueOf(ProtocolVersion protoVersion, Flags type) {
		final int pos = EnumUtils.getEnumPosition(type, protoVersion); // TODO: this is wrong
		if(pos == -1) return null;
		return new ProgramRecordingDupMethodType(protoVersion, pos);
	}
	
	public static ProgramRecordingDupMethodType valueOf(ProtocolVersion protoVersion, Integer value) {
		return new ProgramRecordingDupMethodType(
			protoVersion, 
			value==null?DUP_CHECK_UNKNOWN.getFlagValue().longValue():value.longValue()
		);
	}	
	
	public static ProgramRecordingDupMethodType valueOf(ProtocolVersion protoVersion, String value) {
		return new ProgramRecordingDupMethodType(
			protoVersion,
			value==null?DUP_CHECK_UNKNOWN.getFlagValue().longValue():Long.valueOf(value).longValue()
		);
	}
}
