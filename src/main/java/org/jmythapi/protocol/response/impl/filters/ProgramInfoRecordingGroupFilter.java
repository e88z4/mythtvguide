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

public class ProgramInfoRecordingGroupFilter implements IProgramInfoFilter {

	private final Set<String> recordingGroups = new HashSet<String>();

	public ProgramInfoRecordingGroupFilter(String... recordingGroups) {
		this.recordingGroups.addAll(Arrays.asList(recordingGroups));
	}	
	
	public boolean accept(IProgramInfo program) {
		if(program == null) return false;
		
		// getting the storage group
		final String recordingGroup = program.getRecordingGroup();
		if(recordingGroup == null || recordingGroup.length() == 0) return false;
		return recordingGroups.contains(recordingGroup);
	}

}
