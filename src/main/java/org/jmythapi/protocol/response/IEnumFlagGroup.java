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

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * A flag group.
 * <p>
 * This interface represents a flag group. The value of a flag group is a single integer value, 
 * which can be accessed using {@link #longValue()}. How this value can be interpreted, depends on the 
 * available flags of the flag group. Depending on the current protocol-version of the flag group,
 * different flags may be available with different flag values.
 * <p>
 * In a flag group multiple flags can be active at the same time.
 * <p>
 * See {@link #longValue()} for the current value of the group.<br>
 * See {@link #getGroupClass()} for the enum class defining all possible flags.<br>
 * See {@link #getSupportedFlags()} for all flags that are available for the current protocol version.<br>
 * See {@link #getActiveFlags()} for all currently active flags.<br>
 * <p>
 * To test if a specific flag is currently active, {@link #isSet(Enum)} can be used.
 * 
 * <h3>Usage Example:</h3>
 * {@mythCodeExample <pre>
 *    // a flag group
 *    IProgramFlags flags = recording.getProgramFlags();
 *    
 *    // testing if a flag is set
 *    boolean hasBookmark = flags.isSet(Flags.FL_BOOKMARK);
 *    if(hasBookmark) &#123;
 *       // do something ...
 *    &#125; 
 * </pre> }
 * 
 * @param <E>
 * 		the enumeration constant describing all possible flags.
 */
public interface IEnumFlagGroup <E extends Enum<E> & IFlag> extends IFlagGroup<E> {
	/**
	 * Gets all possible flags of this flag group.
	 * @return
	 * 		all possible flags
	 */
	public EnumSet<E> getSupportedFlags();
	
	/**
	 * Gets all currently active flags of this group.
	 * @return
	 * 		all active flags.
	 */
	public EnumSet<E> getActiveFlags();
	
	/**
	 * Gets all currently inactive flags of this group.
	 * @return
	 * 		all inactive flags.
	 */
	public EnumSet<E> getInactiveFlags();
	
	/**
	 * Gets a map with all flags and their isSet status.
	 * 
	 * @return
	 * 		a map with all flags and boolean value indicating if a given flag is set.
	 */
	public EnumMap<E,Boolean> getFlagMap();
	
	/**
	 * Checks if the given flag is actually set.
	 * @param flag
	 * 		the flag to check
	 * @return
	 * 		{@code true} if the flag is currently set.
	 */
	public boolean isSet(E flag);
}
