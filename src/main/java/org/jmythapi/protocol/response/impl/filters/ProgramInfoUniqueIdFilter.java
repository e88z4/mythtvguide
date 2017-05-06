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
package org.jmythapi.protocol.response.impl.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramInfoFilter;
import org.jmythapi.protocol.response.IProgramInfoList;

public class ProgramInfoUniqueIdFilter implements IProgramInfoFilter {

	private final IProgramInfoList.MapKey idType;
	
	private final Set<String> ids = new HashSet<String>();

	public ProgramInfoUniqueIdFilter(IProgramInfoList.MapKey idType, String... ids) {
		this.idType = idType;
		this.ids.addAll(Arrays.asList(ids));
	}	
	
	public boolean accept(IProgramInfo program) {
		if(program == null) return false;
		else if(idType == null) return false;
		
		// getting the storage group
		String id = null;
		switch (this.idType) {
			case UNIQUE_PROGRAM_ID:
				id = program.getUniqueProgramId();
				break;
			case UNIQUE_RECORDING_ID:
				id = program.getUniqueRecordingId();
				break;
			default:
				break;
		}
		
		if(id == null) return false;
		return ids.contains(id);
	}

}
