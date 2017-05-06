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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.impl.RemoteEncoder;
import org.jmythapi.protocol.response.ISleepStatus;

/**
 * @see RemoteEncoder#getSleepStatus()
 * @since {@mythProtoVersion 45}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
public class SleepStatus extends AEnumFlagGroup<ISleepStatus.Flags> implements ISleepStatus {
	private static final long serialVersionUID = 1L;

	public SleepStatus(ProtocolVersion protoVersion, long sleepStatusFlags) {
		super(ISleepStatus.Flags.class, protoVersion, sleepStatusFlags);
	}
}
