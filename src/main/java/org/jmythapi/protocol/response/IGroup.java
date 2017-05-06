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
package org.jmythapi.protocol.response;

import org.jmythapi.IVersionable;

/**
 * A value group.
 * <p>
 * This interface represents a value group. The value of this group is a single integer value, 
 * which can be accessed using {@link #longValue()}. How this value can be interpreted, depends 
 * on the used protocol version and on the type of the group.
 * <p>
 * A {@link IEnumFlagGroup flag group} may have multiple active flags at the same time.<br>
 * A {@link IEnumGroup} may have only one value set at the same time.<br>
 * <p>
 * See {@link #longValue()} for the current value of this group.<br>
 * See {@link #getGroupClass()} for an enumeration class defining all possible values of this group.
 *
 * @param <E>
 * 		the enum class defining all possible values of this group
 * 
 * @see IEnumFlagGroup
 * @see IEnumGroup
 */
public interface IGroup <E> extends IVersionable, Cloneable {
	/**
	 * Gets the current value.
	 * @return
	 * 		the value 
	 */
	public long longValue();
	
	/**
	 * Sets a new long value.
	 * <p>
	 * If a {@link IGroupValueChangedCallback callback} object was registered, it is informed
	 * about the changed value.
	 * 
	 * @param newLongValue
	 * 		the new value
	 */
	public void setLongValue(long newLongValue);
	
	/**
	 * Gets the enumeration class defining the possible values for this group.
	 * @return
	 * 		the enumeration class
	 */
	public Class<E> getGroupClass();
}
