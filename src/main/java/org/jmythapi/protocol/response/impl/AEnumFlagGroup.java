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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IEnumFlagGroup;
import org.jmythapi.protocol.response.IFlag;
import org.jmythapi.protocol.utils.EnumUtils;

public abstract class AEnumFlagGroup <E extends Enum<E> & IFlag> extends AFlagGroup<E> implements IVersionable, IEnumFlagGroup<E> {
	private static final long serialVersionUID = 1L;

	public AEnumFlagGroup(Class<E> groupClass, ProtocolVersion protoVersion, long enumValue) {
		super(groupClass, protoVersion, enumValue);
	}
	
	@Override
	public EnumSet<E> getSupportedFlags() {
		return getSupportedFlags(this.groupClass, this.protoVersion);
	}
	
	@Override
	public EnumSet<E> getActiveFlags() {
		return getFlagsOfType(Boolean.TRUE);
	}
	
	public EnumSet<E> getInactiveFlags() {
		return getFlagsOfType(Boolean.FALSE);
	}
	
	private EnumSet<E> getFlagsOfType(Boolean isActive) {
		final EnumSet<E> foundFlags = EnumSet.noneOf(this.groupClass);
		
		final EnumMap<E,Boolean> flagsStatus = this.getFlagMap();
		for(Entry<E,Boolean> flagStatus : flagsStatus.entrySet()) {
			if(isActive == null || flagStatus.getValue().equals(isActive)) {
				foundFlags.add(flagStatus.getKey());
			}
		}
		
		return foundFlags;		
	}
	
	@Override
	public boolean isSupportedFlag(E flag) {
		return this.getSupportedFlags().contains(flag);
	}
	
	public EnumMap<E,Boolean> getFlagMap() {
		final EnumMap<E,Boolean> enumMap = new EnumMap<E, Boolean>(this.groupClass);
		for(E flag : this.getSupportedFlags()) {
			enumMap.put(flag,this.isSet(flag));
		}
		return enumMap;
	}
	
	/* ============================================================================
	 * VALUEOF methods
	 * ============================================================================ */
	
	public static <E extends Enum<E> & IFlag> EnumSet<E> getSupportedFlags(Class<E> groupClass, ProtocolVersion protoVersion) {
		return EnumUtils.getEnums(groupClass,protoVersion);
	}	
	
	public static <E extends Enum<E> & IFlag, G extends AGroup<E>> G valueOf(
		Class<G> groupClass, ProtocolVersion protoVersion, E... flags
	) {
		long flagValues = 0;
		if(flags != null && flags.length > 0) {
			final Class<E> enumClass = flags[0].getDeclaringClass();
			final EnumSet<E> supportedFlags = getSupportedFlags(enumClass, protoVersion);
			
			for(E flag : flags) {
				// check if the given flag is supported
				if(!supportedFlags.contains(flag)) {
					Logger.getLogger(AEnumFlagGroup.class.getName()).warning(String.format(
						"The enumeration property '%s' is not supported in protocol version '%s'.",
						flag, protoVersion
					));					
					continue;
				}
				
				// getting the current flag value
				final Long flagLong = getFlagValue(protoVersion,flag);
				if (flagLong == null) {
					Logger.getLogger(AEnumFlagGroup.class.getName()).warning(String.format(
						"Unable to determine the integer value for flag %s",
						flag
					));
					continue;
				}
				
				flagValues |= flagLong.longValue();
			}
		}
		
		return valueOf(groupClass, protoVersion, flagValues);
	}
}
