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

import static org.jmythapi.protocol.response.IProgramRecordingStatus.Status.UNKNOWN;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramRecordingStatus;
import org.jmythapi.protocol.response.IProgramRecordingStatus.Status;

public class ProgramRecordingStatus extends AVersionableEnumGroup<Status> implements IProgramRecordingStatus {
	private static final long serialVersionUID = 1L;

	public ProgramRecordingStatus(ProtocolVersion protoVersion, long value) {
		super(Status.class, protoVersion, value);
	}

	public Status getStatus() {
		return this.getEnum();
	}
	
	public boolean hasStatus(Status status) {
		return this.hasEnum(status);
	}
	
	public boolean hasStatus(Status... statuses) {		
		return this.hasEnum(statuses);
	}

	public int getStatusValue() {
		return (int)this.longValue();
	}
	
	/* ============================================================================
	 * TO_STRING methods
	 * ============================================================================ */	
	/**
	 * @deprecated this should not be needed anymore
	 */
	public static String toString(ProgramRecordingStatus status) {
		return Long.toString(status.longValue());
	}	
	
	/* ============================================================================
	 * VALUE_OF methods
	 * ============================================================================ */	
	
	public static ProgramRecordingStatus valueOf(IMythPacket packet) {
		return valueOf(packet.getVersionNr(),packet.getPacketArg(0));
	}
	
	public static ProgramRecordingStatus valueOf(ProtocolVersion protoVersion, String valueStr) {
		if(valueStr == null) {
			return valueOf(ProgramRecordingStatus.class,protoVersion,UNKNOWN);
		} else {
			return valueOf(ProgramRecordingStatus.class,protoVersion,valueStr);
		}
	}
	
	public static ProgramRecordingStatus valueOf(ProtocolVersion protoVersion, IProgramRecordingStatus.Status status) {
		return valueOf(ProgramRecordingStatus.class,protoVersion,status);
	}	
}
