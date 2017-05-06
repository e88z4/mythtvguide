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

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramRecordingType;

public class ProgramRecordingType extends AVersionableEnumGroup<IProgramRecordingType.Type> implements IProgramRecordingType {
	private static final long serialVersionUID = 1L;

	public ProgramRecordingType(ProtocolVersion protoVersion, long value) {		
		super(Type.class, protoVersion, value);
	}

	public Type getType() {
		return this.getEnum();
	}

	public int getTypeValue() {
		return (int)this.longValue();
	}
	
	public boolean hasType(Type type) {
		return this.hasType(type);
	}
	
	public boolean hasType(Type... types) {
		return this.hasEnum(types);
	}

	/* ============================================================================
	 * TO_STRING methods
	 * ============================================================================ */	
	
	public static String toString(ProgramRecordingType group) {
		return Long.toString(group.longValue());
	}	
	
	/* ============================================================================
	 * VALUE_OF methods
	 * ============================================================================ */
	
	public static ProgramRecordingType valueOf(ProtocolVersion protoVersion, IProgramRecordingType.Type type) {
		return AEnumGroup.valueOf(ProgramRecordingType.class,protoVersion,type);
	}
	
	public static ProgramRecordingType valueOf(ProtocolVersion protoVersion, Integer value) {
		return AEnumGroup.valueOf(ProgramRecordingType.class,protoVersion,value.longValue());
	}
	
	public static ProgramRecordingType valueOf(ProtocolVersion protoVersion, String value) {
		return AEnumGroup.valueOf(ProgramRecordingType.class,protoVersion,value);
	}
}
