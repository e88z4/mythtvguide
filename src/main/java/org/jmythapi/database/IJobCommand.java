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
package org.jmythapi.database;

import org.jmythapi.IPositionalValue;
import org.jmythapi.protocol.response.IEnumGroup;

public interface IJobCommand extends IEnumGroup<IJobCommand.Commands> {
	public static enum Commands implements IPositionalValue {
		RUN(0x0000),
		PAUSE(0x0001),
		RESUME(0x0002),
		STOP(0x0004),
		RESTART(0x0008);

		private int value;
		
		private Commands(int value) {
			this.value = value;
		}
		
		public int getPosition() {
			return this.value;
		}		
	}

}
