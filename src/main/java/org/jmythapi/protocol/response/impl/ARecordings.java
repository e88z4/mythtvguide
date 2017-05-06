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
import java.util.Iterator;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramInfoFilter;
import org.jmythapi.protocol.response.IProgramInfoList;
import org.jmythapi.protocol.response.IRecordings;
import org.jmythapi.protocol.utils.EnumUtils;

public abstract class ARecordings <E extends Enum<E>> extends AMythResponse<E> implements IRecordings {
	
	public ARecordings(Class<E> propsClass, IMythPacket packet) {
		super(propsClass, packet);
	}
	
	public ARecordings(ProtocolVersion protoVersion, Class<E> propsClass,  List<String> responseArgs) {
		super(protoVersion, propsClass, responseArgs);
	}
	
	@Override
	protected int getExpectedSize(List<String> responseArgs) {
		// getting the value of the size field
		final int programInfoListSize = Integer.valueOf(responseArgs.get(this.getSizePropertyIndex()));
		
		// getting the length of the program-info properties
		final int programInfoSize = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);
		
		// getting the length of recording properties 
		final int recordingInfoSize = EnumUtils.getEnumLength(this.propsClass, this.protoVersion);
		
		// the expected size is
		return recordingInfoSize + (programInfoListSize*programInfoSize);
	}
	
	/**
	 * @return the position within the response-arguments where the
	 *  size property is located
	 */
	protected abstract int getSizePropertyIndex();
	
	public IProgramInfoList getProgramInfoList() {
		return this.getProgramInfoList(null);
	}
	
	public IProgramInfoList getProgramInfoList(IProgramInfoFilter filter) {
		// getting the program list
		List<String> args = this.getPropertyValues();
		args = args.subList(this.getSizePropertyIndex(), args.size());
		
		final IProgramInfoList programList = new ProgramInfoList(this.protoVersion, args);		
		if(filter == null) return programList;
		
		// filter programs
		final IProgramInfoList filteredPrograms = programList.filter(filter);
		return filteredPrograms;
	}
	
	public Iterator<IProgramInfo> iterator() {
		return this.iterator(null);
	}
	
	public Iterator<IProgramInfo> iterator(IProgramInfoFilter filter) {
		if(this.isEmpty()) return new ArrayList<IProgramInfo>(0).iterator();
		return this.getProgramInfoList(filter).iterator();
	}
	
	/**
	 * @return the size of the {@link ProgramInfoList}
	 */
	public int size() {
		return Integer.valueOf(this.getPropertyValue(this.getSizePropertyIndex()));
	}
	
	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString())
		   .append("\r\n")
		   .append(this.getProgramInfoList());
		return buf.toString();
	}
}
