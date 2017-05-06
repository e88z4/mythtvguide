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

import static org.jmythapi.protocol.response.ILoad.Props.LOAD_AVERAGE_LAST_15MINS;
import static org.jmythapi.protocol.response.ILoad.Props.LOAD_AVERAGE_LAST_5MINS;
import static org.jmythapi.protocol.response.ILoad.Props.LOAD_AVERAGE_NOW;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.ILoad;

public class Load extends AMythResponse<ILoad.Props> implements ILoad {
	
	public Load(IMythPacket packet) {
		super(ILoad.Props.class, packet);
	}
	
	public Load(ProtocolVersion protoVersion) {
		super(protoVersion, ILoad.Props.class);
	}
	
	public double getCurrentLoad() {
		return (Double)this.getPropertyValueObject(LOAD_AVERAGE_NOW);
	}
	
	public double getLast5MinsLoad() {
		return (Double)this.getPropertyValueObject(LOAD_AVERAGE_LAST_5MINS);
	}
	
	public double getLast15MinsLoad() {
		return (Double)this.getPropertyValueObject(LOAD_AVERAGE_LAST_15MINS);
	}
}
