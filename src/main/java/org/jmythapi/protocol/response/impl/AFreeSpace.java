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

import java.util.ArrayList;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IBasicFreeSpace;

public abstract class AFreeSpace <E extends Enum<E>> extends AMythResponse<E> implements IBasicFreeSpace {

	public AFreeSpace(Class<E> propsClass, IMythPacket packet) {
		super(propsClass, packet);
	}
	
	public AFreeSpace(ProtocolVersion protoVersion, Class<E> propsClass, String... responseArgs) {
		super(protoVersion, propsClass, responseArgs);
	}
	
	public AFreeSpace(ProtocolVersion protoVersion, Class<E> propsClass, ArrayList<String> responseArgs) {
		super(protoVersion, propsClass, responseArgs);
	}
	
	public abstract Long getTotalSpace();
	public abstract Long getUsedSpace();
	
	public Long getFreeSpace() {
		final Long totalSpace = this.getTotalSpace();
		if(totalSpace == null) return null;
		final Long usedSpace = this.getUsedSpace();
		if(usedSpace == null) return null;
		return Long.valueOf(totalSpace.longValue() - usedSpace.longValue());
	}
}
