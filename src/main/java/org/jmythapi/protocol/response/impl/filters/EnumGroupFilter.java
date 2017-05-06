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
import org.jmythapi.protocol.response.IEnumGroup;
import org.jmythapi.protocol.response.IFilter;

public class EnumGroupFilter 
	<E extends Enum<E>,P extends Enum<P>, O extends IPropertyAware<P>> 
	implements IFilter<P, O> {

	private final P property;
	
	private final E[] enums;
	
	public EnumGroupFilter(P property, E... filterStatus) {
		this.property = property;
		this.enums = filterStatus;
	}
	
	public boolean accept(O object) {
		if(object == null) return false;
		else if(property == null) return false;
		
		// getting the enum group
		final IEnumGroup<E> enumGroup = object.getPropertyValueObject(this.property);
		if(enumGroup == null) return false;
		
		// check for the requested status
		return enumGroup.hasEnum(this.enums);
	}
	
}