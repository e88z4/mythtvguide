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
package org.jmythapi.impl;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IGroupValueChangedCallback;
import org.jmythapi.protocol.response.impl.AGroup;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * A generic data object.
 * <p>
 * This object represents is a generic data object containing the following mandatory fields:
 * <ol>
 * 	<li><b>Protocol Version:</b><br> An constant describing the protocol-version the object was created for.</li>
 * 	<li><b>Property Class:</b><br> An enumeration class describing all properties of the data object.</li>
 * 	<li><b>Property Values:</b><br> A list of values for all properties.</li>
 * </ol>
 * <p>
 * This object is {@link IPropertyAware property-aware}. {@link #getProperties()} can be used to determine the
 * properties, which are accessible by this object.
 *
 * @param <E>
 * 		the enumeration class describing the object properties.
 */
public abstract class AData <E extends Enum<E>> implements Cloneable, IPropertyAware<E>, IVersionable {
	/**
	 * For logging.
	 */
	protected Logger logger = Logger.getLogger(this.getClass().getName());	
	
	/**
	 * The MythTV-protocol-version of this data object.
	 */
	protected ProtocolVersion protoVersion;	
	
	/**
	 * An enumeration class defining all data properties.
	 */
	protected Class<E> propsClass;
	
	/**
	 * The values of all data properties.
	 * <p>
	 * This list has a fixed size.
	 */	
	protected List<String> respArgs;
	
	/**
	 * Initializes this object with all mandatory values.
	 * 
	 * @param protoVersion
	 * 		the protocol version of this object
	 * @param propsClass
	 * 		the enumeration class, containing all properties of this object
	 * @param data
	 * 		the property values of this object
	 */
	protected void init(ProtocolVersion protoVersion, Class<E> propsClass, List<String> data) {
		if(protoVersion == null) throw new NullPointerException("No protocol version specified");
		if (propsClass == null) throw new NullPointerException("The property class must not be null");
		
		this.protoVersion = protoVersion;
		this.propsClass = propsClass;
		
		// create the result structure (if needed)
		if(data == null) {
			 this.respArgs = EnumUtils.getDefaultValuesList(propsClass,this.getProperties());
		} else {
			this.respArgs = data;
		}
		
		// post process arguments after init
		this.postProcessArguments();
	}
	
	protected void postProcessArguments() {
		// nothing todo here
	}
	

	/* ====================================================================
	 * IVersionable Method
	 * ==================================================================== */
	/**
	 * {@inheritDoc}
	 */
	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}	
	
	/* ====================================================================
	 * Methods for ARGUMENTS handling
	 * ==================================================================== */
	
	public Class<E> getPropertyClass() {
		return this.propsClass;
	}
	
	public int getPropertyCount() {
		return this.respArgs==null?0:this.respArgs.size();
	}	
	
	public List<String> getPropertyValues() {
		return this.respArgs;
	}		
	
	public String getPropertyValue(int propIdx) {
		if (propIdx < 0) throw new IndexOutOfBoundsException();
		
		if (this.respArgs == null || propIdx > this.respArgs.size()) {
			throw new IndexOutOfBoundsException();
		}
		return this.respArgs.get(propIdx);
	}
	
	public String getPropertyValue(E prop) {
		if (prop == null) throw new NullPointerException("No property specified");	
		
		// determine the position of the property
		final int pos = this.getPropertyIndex(prop);
		if (pos == -1) {
			logger.warning(String.format(
				"Unable to determine the position of property: %s. Returning a null value",
				prop
			));
			return null;
		}
		
		return this.getPropertyValue(pos);
	}
	
	public <T> T getPropertyValueObject(E prop) {
		// getting the property type
		final Class<T> dataType = EnumUtils.getEnumDataType(prop);
		
		// get the property value
		return this.getPropertyValueObject(dataType, prop);
	}
	
	public <T> T getPropertyValueObject(Class<T> dataType, final E prop) {
		// getting the property value string
		final String dataValue = this.getPropertyValue(prop);
		
		// convert the string value into an object
		final T response = decodeProperty(
			dataType,
			dataValue
		);
		
		// callback to write changed values back
		if(response != null && response instanceof AGroup<?>) {
			((AGroup<?>) response).setValueChangedCallback(new IGroupValueChangedCallback() {				
				public void valueChanged(String newValue) {
					setPropertyValue(prop, newValue);
				}
			});
		}
		
		return response;
	}	
	
	protected abstract <T> T decodeProperty(Class<T> dataType, String dataValue);
	
	public String setPropertyValue(int propIdx, String value) {
		if (propIdx < 0) throw new IndexOutOfBoundsException();		
		else if (this.respArgs == null || propIdx > this.respArgs.size()) {
			throw new IndexOutOfBoundsException();
		}
		
		return this.respArgs.set(propIdx, value);
	}
	
	/**
	 * 
	 * @param prop the argument, whose value should be set
	 * @param value the new value of the argument
	 * @return the previous value of the argument
	 */
	public String setPropertyValue(E prop, String value) {
		if (prop == null) throw new NullPointerException();
		
		// determine the position of the property
		final int pos = this.getPropertyIndex(prop);
		if (pos == -1) {
			logger.warning(String.format(
				"Unable to determine the position of property: %s. No value is set",
				prop
			));
			return null;
		}
		
		// determine the target data type
		return this.setPropertyValue(pos,value);
	}
	
	
	public <S,T> void setPropertyValueObject(E prop, S propValue) {
		// getting the property type
		final Class<T> propStringType = EnumUtils.getEnumStringType(prop);
		final Class<S> propObjectType = EnumUtils.getEnumDataType(prop);
		
		// getting the default value (if any)
		final String propDefaultValue = EnumUtils.getEnumDefaultValue(prop);
		
		final String valueString = this.encodeProperty(
			propObjectType, 
			propValue,
			propStringType,
			propDefaultValue
		);
		this.setPropertyValue(prop, valueString);
	}	
	
	protected abstract <S,T> String encodeProperty(Class<S> propObjectType, S propValue, Class<T> propStringType, String propDefaultValue);	
	
	/**
	 * TODO: callback to write changed data back?
	 */
	public EnumMap<E, String> getPropertyMap() {
		final EnumMap<E, String> valueMap = new EnumMap<E, String>(this.propsClass);
		
		// getting all properties for the given protocol-version
		final EnumSet<E> props = this.getProperties();
		if (props == null || props.size() == 0) return valueMap;
		
		// building the map using the property and it's value.		
		final Iterator<E> propsIter = props.iterator();
		for (int i=0; i < Math.min(props.size(),this.respArgs.size()); i++) {
			final E prop = propsIter.next();
			final String value = this.getPropertyValue(i);
			valueMap.put(prop, value);
		}
		
		return valueMap;
	}
	
	public abstract E getProperty(int idx);
	public abstract int getPropertyIndex(E prop);
	public abstract EnumSet<E> getProperties();
	
	/* ============================================================================
	 * CLONEABLE methods
	 * ============================================================================ */
	
	/**
	 * Creates a copy of this object.
	 * @return
	 * 		a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			// get the class to clone
			final Class<?> cloneClass = this.getClass();
			
			// determine the constructor to use
			final Constructor<?> constructor = cloneClass.getConstructor(ProtocolVersion.class, List.class);
			
			// create a new instance
			final Object clonedObject = constructor.newInstance(
				this.protoVersion, new ArrayList<String>(this.respArgs)
			);
			return clonedObject;
		} catch (Throwable e) {
			final CloneNotSupportedException c = new CloneNotSupportedException("Unable to clone object");
			c.initCause(e);
			throw c;
		}
	}
	
	/* ====================================================================
	 * UTILTIY methods
	 * ==================================================================== */
	
	/**
	 * Tests objects for equality.
	 * <p>
	 * Data objects are seen to be equal if the response to the following function calls are equal:
	 * <ol>
	 * 	<li>{@link #getClass()}</li>
	 * 	<li>{@link #getVersionNr()}</li>
	 * 	<li>{@link #getPropertyClass()}</li>
	 * 	<li>{@link #getPropertyValues()}</li>
	 * </ol>
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		else if(!obj.getClass().equals(this.getClass())) return false;
		
		final AData<?> otherData = (AData<?>) obj;
		
		// check for same version
		if(otherData.protoVersion == null && this.protoVersion != null) return false;
		else if(otherData.protoVersion != null && this.protoVersion == null) return false;
		else if(!otherData.protoVersion.equals(this.protoVersion)) return false;
		
		// check for same property class
		if(otherData.propsClass == null && this.propsClass != null) return false;
		else if(otherData.propsClass != null && this.propsClass == null) return false;
		else if(!otherData.propsClass.equals(this.propsClass)) return false;
		
		// check for same property values
		final List<String> otherArgs = otherData.respArgs;
		if(otherArgs == null && this.respArgs != null) return false;
		else if (otherArgs != null && this.respArgs == null) return false;		
		return otherArgs.equals(this.respArgs);
	}
	
	/**
	 * Generates a hashCode for this object.
	 * <p>
	 * This is required when using this object as a key in a {@link Map map}.
	 * 
	 * @return
	 * 		the hash code
	 */
	@Override
	public int hashCode() {
		int hashCode = 0;
		if(this.protoVersion != null) hashCode += this.protoVersion.hashCode();
		if(this.propsClass != null) hashCode += this.propsClass.hashCode();
		if(this.respArgs != null) hashCode += this.respArgs.hashCode();
		return hashCode;
	}
	
	/**
	 * <p>Returns the content of this data object as a formatted string.</p>
	 * E.g.
	 * <pre>
	 * <0>TOTAL_RAM_MB: 1002 | <1>FREE_RAM_MB: 474 | <2>TOTAL_VM_MB: 853 | <3>FREE_VM_MB: 853
	 * </pre>
	 */
	public String toString() {
		try {
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			final EnumMap<E,String> propsMap = this.getPropertyMap();
			if (propsMap != null) {
				final int propsSize = propsMap.size();
				final int idxLength = Integer.toString(propsSize).length();
				final String formatStr = "<%0" + idxLength + "d>%s: %s";
				
				int idx = 0;
				for (E prop : propsMap.keySet()) {
					final String propName = prop.name();
					final String propValueString = propsMap.get(prop);
					final Class<?> propType = EnumUtils.getEnumDataType(prop);
					String propValueFormatted = null;
					if (propType == null) {
						// just print out the string
						propValueFormatted = propValueString;
					} else {
						// using the specified data-type to format the string-output
						try {
							final Object propObject = this.decodeProperty(propType,propValueString);
							if(propObject instanceof Time) {
								propValueFormatted = String.format("%1$tT",propObject);
							} else if(propObject instanceof java.sql.Date) {
								propValueFormatted = String.format("%1$tF",propObject);
							} else if(propObject instanceof Date) {
								propValueFormatted = String.format("%1$tF %1$tT",propObject);
							} else {
								propValueFormatted = propObject.toString();
							}
						} catch (Throwable e) {
							propValueFormatted = propValueString;
						}
					}
					
					final String propsString = String.format(
						formatStr,
						Integer.valueOf(idx),
						propName,						
						propValueFormatted
					);
					bout.write(propsString.getBytes("UTF-8"));
					
					idx++;
					if (idx < propsSize) {
						bout.write(" | ".getBytes("UTF-8"));
					}
				}
			} else {
				bout.write("No data available?".getBytes("UTF-8"));
			}
			return bout.toString("UTF-8");
		} catch (Exception e) {
			assert(false) : "this should never occure";
			return null;
		}
	}	
}
