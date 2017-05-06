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

import org.jmythapi.protocol.response.IFilter;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramInfoFilter;

/**
 * This class is just a wrapper around an {@link IFilter} for {@link IProgramInfo} objects.
 */
public class ProgramInfoFilter implements IProgramInfoFilter {

	private final IFilter<IProgramInfo.Props, IProgramInfo> filter;
	
	public ProgramInfoFilter(IFilter<IProgramInfo.Props, IProgramInfo> filter) {
		this.filter = filter;
	}
	
	public boolean accept(IProgramInfo program) {
		return this.filter.accept(program);
	}

}
