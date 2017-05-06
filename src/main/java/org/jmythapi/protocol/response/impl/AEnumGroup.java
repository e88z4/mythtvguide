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

import java.util.EnumSet;
import java.util.logging.Logger;

import org.jmythapi.IPositionalValue;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IEnumGroup;
import org.jmythapi.protocol.response.IVersionableValue;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * An implementation of {@link IEnumGroup}.
 */
public abstract class AEnumGroup <E extends Enum<E>> extends AGroup<E> implements IEnumGroup<E> {
	private static final long serialVersionUID = 1L;
	
	public AEnumGroup(Class<E> enumClass, ProtocolVersion protoVersion, long enumValue) {
		super(enumClass,protoVersion,enumValue);	
	}
	
	public EnumSet<E> getSupportedEnums(E prop) {
		if(prop == null) throw new NullPointerException("Value was null");
		
		// getting all supported properties
		final Class<E> enumClass = prop.getDeclaringClass();
		return EnumUtils.getEnums(enumClass, protoVersion);		
	}
	
	public E getEnum() {
		return EnumUtils.getEnum(this.groupClass,this.protoVersion,(int)this.longValue());
	}	
	
	public void setEnum(E prop) {		
		if(prop == null) throw new NullPointerException("Value was null");
		
		// getting all supported properties
		final EnumSet<E> supportedProps = this.getSupportedEnums(prop);
		
		// check if the given flag is supported
		if(!supportedProps.contains(prop)) {
			Logger.getLogger(AEnumGroup.class.getName()).warning(String.format(
				"The enumeration property '%s' is not supported in protocol version '%s'.",
				prop, protoVersion
			));					
			return;
		}		
		
		// getting the enumeration value
		final Long enumLong = getEnumValue(protoVersion,prop);
		if (enumLong == null) {
			Logger.getLogger(AEnumFlagGroup.class.getName()).warning(String.format(
				"Unable to determine the integer value for property %s",
				prop
			));
			return;
		}
		
		// setting the new value
		this.setLongValue(enumLong.longValue());
	};
	
	public boolean hasEnum(E prop) {
		final E eValue = this.getEnum();
		if(eValue == null) return false;
		return eValue.equals(prop);
	}
	
	public boolean hasEnum(E... props) {
		boolean hasProp = false;
		for(E prop : props) {
			hasProp |= this.hasEnum(prop);
		}
		return hasProp;
	}
	
	/* ============================================================================
	 * VALUEOF methods
	 * ============================================================================ */
	public static <E extends Enum<E>> Long getEnumValue(ProtocolVersion protoVersion, E prop) {
		if(prop == null) return null;

		Long enumLong = null;
		if(prop instanceof IVersionableValue) {
			enumLong = ((IVersionableValue)prop).getValue(protoVersion);
		} else if(prop instanceof IPositionalValue) {
			enumLong = Long.valueOf(((IPositionalValue)prop).getPosition());
		} else {
			enumLong = Long.valueOf(EnumUtils.getEnumPosition(prop, protoVersion));
		}
		return enumLong;
	}	
	
	public static <E extends Enum<E>, G extends AEnumGroup<E>> G valueOf(
		Class<G> groupClass, ProtocolVersion protoVersion, E prop
	) {	
		if(prop == null) return null;
		
		// getting all supported properties
		final Class<E> enumClass = prop.getDeclaringClass();
		final EnumSet<E> supportedProps = EnumUtils.getEnums(enumClass, protoVersion);
		
		// check if the given flag is supported
		if(!supportedProps.contains(prop)) {
			Logger.getLogger(AEnumFlagGroup.class.getName()).warning(String.format(
				"The enumeration property '%s' is not supported in protocol version '%s'.",
				prop, protoVersion
			));					
			return null;
		}
		
		// getting the enumeration value
		final Long enumLong = getEnumValue(protoVersion,prop);
		if (enumLong == null) {
			Logger.getLogger(AEnumFlagGroup.class.getName()).warning(String.format(
				"Unable to determine the integer value for property %s",
				prop
			));
			return null;
		}
		
		return valueOf(groupClass, protoVersion, enumLong);
	}
	
	/* ============================================================================
	 * TO_STRING methods
	 * ============================================================================ */	
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();		
		buff.append(this.longValue()).append("=> {");
		
		final E eValue = this.getEnum();
		if (eValue != null) {
			buff.append(eValue.toString());
		}
		
		buff.append("}");
		return buff.toString();
	}
}
