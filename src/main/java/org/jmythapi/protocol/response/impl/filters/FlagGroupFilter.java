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

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.response.IFilter;
import org.jmythapi.protocol.response.IFlag;
import org.jmythapi.protocol.response.IEnumFlagGroup;

public class FlagGroupFilter 
	<F extends Enum<F> & IFlag,P extends Enum<P>, O extends IPropertyAware<P>> 
	implements IFilter<P, O> 
{

	private final P property;
	
	private final boolean allFlags;
	
	private final F[] flags;
	
	public FlagGroupFilter(P property, F... flags) {
		this(property, true, flags);
	}
	
	public FlagGroupFilter(P property, boolean allFlags,F... flags) {
		this.property = property;
		this.allFlags = allFlags;
		this.flags = flags;
	}
	
	public boolean accept(O object) {
		if(object == null) return false;
		else if(flags == null) return false;
		else if(this.property == null) return false;
		
		// getting all flags
		final IEnumFlagGroup<F> flags = object.getPropertyValueObject(this.property);
		if(flags == null) return false;
		
		// check for the requested status
		boolean accepted = allFlags ? true : false;
		for(F flag : this.flags) {
			final boolean isSet = flags.isSet(flag);
			if(allFlags) {
				accepted &= isSet;
				if(!accepted) break;
			} else {
				accepted |= isSet;
				if(accepted) break;
			}			
		}
		return accepted;
	}
	
}