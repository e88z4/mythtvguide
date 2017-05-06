package org.jmythapi.protocol.response;

import java.util.Set;

public interface IFlagGroup <E extends IFlag> extends IGroup<E> {
	/**
	 * Gets all currently active flags of this group.
	 * @return
	 * 		all active flags.
	 */
	public Set<E> getActiveFlags();
	
	/**
	 * Checks if the given flag is actually set.
	 * @param flag
	 * 		the flag to check
	 * @return
	 * 		{@code true} if the flag is currently set.
	 */
	public boolean isSet(E flag);	
	
	/**
	 * Sets the given flag.
	 * 
	 * @param flag
	 * 		the flag to set
	 * @return
	 * 		{@code true} if the flag was set.
	 */
	public boolean set(E flag);
	
	/**
	 * Clears the given flag
	 * @param flag
	 * 		the flag to clear
	 * @return
	 * 		{@code true} if the flag was cleared.
	 */
	public boolean clear(E flag);
}
