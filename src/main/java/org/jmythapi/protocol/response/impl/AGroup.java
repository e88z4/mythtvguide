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

import static java.util.FormattableFlags.ALTERNATE;

import java.lang.reflect.Constructor;
import java.util.Formattable;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IGroup;
import org.jmythapi.protocol.response.IGroupValueChangedCallback;

public abstract class AGroup<E> extends Number implements IGroup<E>, Formattable, Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * For logging
	 */
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * A {@link Enum} class defining all available values.
	 */
	protected final Class<E> groupClass;
	
	/**
	 * The current protocol version
	 */
	protected final ProtocolVersion protoVersion;
	
	/**
	 * The value of this flagGroup as {@link Integer}
	 */
	protected long longValue;
	
	/**
	 * A callback interface that is informed about changed values.
	 */
	protected IGroupValueChangedCallback valueChangedCallback;
	
	public AGroup(Class<E> groupClass, ProtocolVersion protoVersion, int enumValue) {
		this(groupClass, protoVersion, (long)enumValue);
	}
	
	public AGroup(Class<E> groupClass, ProtocolVersion protoVersion, long enumValue) {
		if(groupClass == null) throw new NullPointerException("No enum class specified.");
		else if(protoVersion == null) throw new NullPointerException("No protocol version specified.");
		
		this.groupClass = groupClass;
		this.protoVersion = protoVersion;
		this.longValue = (long) enumValue;		
	}	
	
	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}	
	
	public Class<E> getGroupClass() {
		return this.groupClass;
	}
		
	/* ==============================================================
	 * Generic methods
	 * ============================================================== */	
	
	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		else if(!(other instanceof AGroup<?>)) return false;
		return this.groupClass.equals(((AGroup<?>)other).groupClass) && 
			   this.longValue() == ((AGroup<?>)other).longValue();
	}	
	
	@Override
	public int hashCode() {
		return this.groupClass.hashCode() *10000 + (int) this.longValue();
	}
	
	/* ============================================================================
	 * CLONEABLE methods
	 * ============================================================================ */
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			// get the class to clone
			final Class<?> cloneClass = this.getClass();
			
			// create the object
			return createObject(cloneClass, this.protoVersion, this.longValue());
		} catch (Throwable e) {
			final CloneNotSupportedException c = new CloneNotSupportedException("Unable to clone object");
			c.initCause(e);
			throw c;
		}		
	}
	
	/* ============================================================================
	 * VALUEOF methods
	 * ============================================================================ */
	
	public static <G> G valueOf(Class<G> groupImplClass, ProtocolVersion protoVersion, String value) {
		if(value == null || value.length() == 0) return null;
		final Long longValue = Long.valueOf(value);
		return valueOf(groupImplClass,protoVersion,longValue);
	}
	
	public static <G> G valueOf(Class<G> groupImplClass, ProtocolVersion protoVersion, Number value) {
		if(value == null) return null;
		return valueOf(groupImplClass, protoVersion, value.longValue());
	}
	
	public static <G> G valueOf(Class<G> groupImplClass, ProtocolVersion protoVersion, long value) {
		try {
			return createObject(groupImplClass, protoVersion, value);
		} catch (Throwable e) {
			throw new RuntimeException("Unable to construct object",e);
		}
	}
	
	public static <G> G createObject(Class<G> groupImplClass, ProtocolVersion protoVersion, long groupValue) throws Exception {
		// determine the constructor to use
		final Constructor<G> constructor = groupImplClass.getConstructor(ProtocolVersion.class, long.class);
		
		// create a new instance
		final G createdObject = constructor.newInstance(
			protoVersion, groupValue
		);
		return createdObject;		
	}
	
	/* ==============================================================
	 * NUMBER methods
	 * ============================================================== */
	
	public int intValue() {
		return (int) this.longValue();
	}	
	
	public double doubleValue() {
		return (double) this.longValue();
	}

	public float floatValue() {
		return (float) this.longValue();
	}
	
	public long longValue()	{
		return this.longValue;
	}
	
	public void setLongValue(long newLongValue) {
		// change value
		this.longValue = newLongValue;
		
		// inform callback
		if(this.valueChangedCallback != null) try {
			this.valueChangedCallback.valueChanged(Long.toString(newLongValue));
		} catch(Throwable e) {
			logger.log(Level.WARNING,String.format(
				"Unexpected error while calling value-changed callback."
			),e);
		}
	}
	
	/* ============================================================================
	 * FORMATABLE methods
	 * ============================================================================ */
	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		boolean alternate = (flags & ALTERNATE) == ALTERNATE;
		if(alternate) {
			formatter.format("%d",this.longValue());
		} else {
			formatter.format(this.toString());
		}
	}
	
	/* ============================================================================
	 * CALLBACK methods
	 * ============================================================================ */
	/**
	 * Registers a callback interface that is informed about a changed value
	 * of this group.
	 * 
	 * @param valueChangedCallback
	 * 		the callback object.
	 */
	public void setValueChangedCallback(IGroupValueChangedCallback valueChangedCallback) {
		this.valueChangedCallback = valueChangedCallback;
	}
}
