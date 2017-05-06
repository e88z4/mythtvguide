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


/**
 * An enum group.
 * <p>
 * This interface represents a enum group. The value of a enum group is a single integer value, 
 * which can be accessed using {@link #longValue()}. How this value can be interpreted, depends on the 
 * available constants of the enumeration group. Depending on the current protocol-version of the group,
 * different constants may be available with different types.
 * <p>
 * In an enum group only a single constant can be active at the same time.
 * <p>
 * See {@link #longValue()} for the current value of the group.<br>
 * See {@link #getGroupClass()} for the enum class defining all possible constants.<br>
 * <p>
 * To test if a specific constant is currently active, {@link #hasEnum(Enum)} can be used.
 * 
 * <h3>Usage Example:</h3>
 * {@mythCodeExample <pre>
 *    // getting the enum group
 *    IProgramRecordingStatus recStatus = recording.getRecordingStatus();
 *    
 *    // testing for an active enum
 *    if(recStatus.hasEnum(Status.RECORDING)) &#123;
 *       // do something
 *    &#125;
 * </pre> }
 */
public interface IEnumGroup <E extends Enum<E>> extends IGroup<E> {
	/**
	 * Gets the current value as enum.
	 * @return
	 * 		the current enum value
	 */
	public E getEnum();
	
	/**
	 * Sets the current value as enum.
	 */
	public void setEnum(E value);
	
	public boolean hasEnum(E prop);
	
	public boolean hasEnum(E... props);
}
