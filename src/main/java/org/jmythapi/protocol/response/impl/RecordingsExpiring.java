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

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramInfoList;
import org.jmythapi.protocol.response.IRecordingsExpiring;

public class RecordingsExpiring extends ARecordings<IRecordingsExpiring.Props> implements IRecordingsExpiring {
	
	public RecordingsExpiring(IMythPacket packet) {
		super(IRecordingsExpiring.Props.class, packet);
	}	
	
	public RecordingsExpiring(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion, IRecordingsExpiring.Props.class, responseArgs);
	}
	
	@Override
	protected int getSizePropertyIndex() {
		return IRecordingsExpiring.Props.SIZE.ordinal();
	}
	
	/**
	 * Constructs a recordings expiring list from a list of programs.
	 * 
	 * @param programs
	 * 		the expiring programs
	 * @return
	 * 		the expiring list
	 */
	public static final RecordingsExpiring valueOf(IProgramInfoList programs) {
		if(programs == null) return null;
		return new RecordingsExpiring(
			programs.getVersionNr(),
			programs.getPropertyValues()
		);		
	}
}
