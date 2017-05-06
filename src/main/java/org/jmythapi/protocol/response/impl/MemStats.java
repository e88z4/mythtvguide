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

import static org.jmythapi.protocol.response.IMemStats.Props.FREE_RAM_MB;
import static org.jmythapi.protocol.response.IMemStats.Props.FREE_VM_MB;
import static org.jmythapi.protocol.response.IMemStats.Props.TOTAL_RAM_MB;
import static org.jmythapi.protocol.response.IMemStats.Props.TOTAL_VM_MB;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.response.IMemStats;

public class MemStats extends AMythResponse<IMemStats.Props> implements IMemStats {

	public MemStats(IMythPacket packet) {
		super(IMemStats.Props.class, packet);
	}
	
	public int getTotalRamMB() {
		return Integer.valueOf(this.getPropertyValue(TOTAL_RAM_MB));
	}
	
	public int getFreeRamMB() {
		return Integer.valueOf(this.getPropertyValue(FREE_RAM_MB));
	}
	
	public int getTotalVmMB() {
		return Integer.valueOf(this.getPropertyValue(TOTAL_VM_MB));
	}
	
	public int getFreeVmMB() {
		return Integer.valueOf(this.getPropertyValue(FREE_VM_MB));
	}
}
