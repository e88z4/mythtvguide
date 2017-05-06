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
package org.jmythapi;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import org.jmythapi.protocol.response.IMythResponse;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * This interface represents a property aware object.
 * <p>
 * Property aware objects are typically {@link IMythResponse response messages}, received from a MythTV backend.
 * 
 * <h3>Property Position and Version Range</h3>
 * Each property-aware object has a set of enumeration properties, that can be used to get the value of a property, 
 * within the property value list.
 * <p>
 * Without the enum properties, you would need to know at which position in the value
 * array, a specific property value can be found. Moreover a property value may only be available for specific protocol
 * versions.
 * <p>
 * Then using the enum properties, you do not need to take care about the proper position or version number. 
 * If a property is not available for the current protocol-version, {@code null} is returned as value.  
 * 
 * <h3>Usage examples:</h3>
 * <h4>Read Property Values:</h4>
 * {@mythCodeExample <pre>
 *    // a property aware object
 *    IProgramInfo program = ...;
 *    
 *    // read the recording status as string (as returned by the backend)
 *    String recStatusString = program.getPropertyValue(IProgramInfo.Props.REC_STATUS);
 *    
 *    // read the recording status as object
 *    IProgramRecordingStatus recStatus = program.getPropertyValueObject(IProgramInfo.Props.REC_STATUS);
 *    
 *    // read the storage group (this will return null for proto version &lt; 32)
 *    String storageGroup = program.getPropertyValueObject(IProgramInfo.Props.STORAGE_GROUP);
 * </pre>}
 * 
 * <h4>Copy properties between objects:</h4>
 * {@mythCodeExample <pre>
 *    // the source and target object
 *    IProgramInfo source = ..., target = ...;
 *    
 *    // copy the string values of all properties
 *    for(E prop : source.getProperties()) &#123;
 *       String sourceValue = source.getPropertyValue(prop);
 *       target.setPropertyValue(prop, sourceValue);		
 *    &#125;		
 * </pre>}
 * 
 * <h4>Print the names of all available properties:</h4>
 * 
 * {@mythCodeExample <pre>
 *    for(int i=0; i < object.getPropertyCount(); i++) &#123;
 *       System.out.println(String.format(
 *          "Property %02d: %s",
 *          i, object.getProperty(i)
 *       ));
 *    &#125;
 * </pre>}

 * <h3>Utility Class:</h3>
 * See {@link EnumUtils} for additional methods that can be used to work with the properties of
 * a property-aware object.
 * 
 * @see EnumUtils
 * @see IMythResponse

 * @param <E>
 * 		the enumeration property type
 */
public interface IPropertyAware <E extends Enum<E>> extends Cloneable {
	/**
	 * Gets the enumeration class defining all available properties.
	 * 
	 * @return
	 * 		the enumeration class
	 */
	public Class<E> getPropertyClass();
	
	/**
	 * Gets the amount of available properties stored in this object.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the used protocol version the amount of available properties may be different.
	 * 
	 * @return 
	 * 		the amount of properties
	 */
	public int getPropertyCount();
	
	/**
	 * Gets the property for the given index.
	 * <p>
	 * This is the reverse operation of {@link #getPropertyIndex(Enum)}.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the used protocol version a specific property may be located at a different position.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    for(int i=0; i < object.getPropertyCount(); i++) &#123;
	 *       System.out.println(String.format(
	 *          "Property %02d: %s",
	 *          i, object.getProperty(i)
	 *       ));
	 *    &#125;
	 * </pre>}
	 * 
	 * @param idx
	 * 		the position within the property array
	 * @return
	 * 		the enumeration constant
	 * @throws IndexOutOfBoundsException
	 * 		if the specified index is out of range
	 */
	public E getProperty(int idx);	
	
	/**
	 * Gets the index for the given property.
	 * <p>
	 * This is the reverse operation of {@link #getProperty(int)} 
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the given property is not supported in the current protocol version, {@code -1} is returned.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    EnumSet<IProgramInfo.Props> props = program.getProperties();
	 *    for(IProgramInfo.Props prop : props) &#123;
	 *       System.out.println(String.format(
	 *          "Property %s is stored at position %d.",
	 *          prop.name(), program.getPropertyIndex(prop)
	 *       ));
	 *    &#125;
	 * </pre>}
	 * 
	 * @param prop
	 * 		the property for which the index should be returned
	 * @return
	 * 		the index of the property or {@code -1}, if a property is not supported in
	 * 		the currently used protocol version.
	 */
	public int getPropertyIndex(E prop);
	
	/**
	 * Gets the value for the given property.
	 * <p>
	 * Internally this method uses {@link #getPropertyIndex(Enum)}, 
	 * to determine the position of the value in the value list.
	 * 
	 * @param prop
	 * 		the desired property
	 * @return
	 * 		the property value or {@code null} if no value is available or
	 * 		the given property is not supported in the given protocol-version.
	 */
	public String getPropertyValue(E prop);	
	
	/**
	 * Gets the value for the given property index.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the current protocol version, a different property
	 * may be located on the same index. 
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // reads the recorder-info property "hostport" and 
	 *    String port = recorderInfo.getPropertyValue(IRecorderInfo.Props.HOSTPORT);
	 * </pre>}
	 * 
	 * @param idx
	 * 		the index of the property for which the value should be returned
	 * @return
	 * 		the property value
	 * @throws IndexOutOfBoundsException
	 * 		if the specified index is out of range
	 */
	public String getPropertyValue(int idx);
	
	/**
	 * Gets the property value for a given property and converts it 
	 * into the property data-type.
	 * <p>
	 * The data type conversion is done using methods of the utility
	 * class {@link EncodingUtils}.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the requested property is not supported in the current protocol version, {@code null}
	 * is returned as value.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // reads the recorder-info property "hostport" and 
	 *    // converts it into an object of type Integer.
	 *    Integer port = recorderInfo.getPropertyValueObject(IRecorderInfo.Props.HOSTPORT);
	 * </pre>}
	 * 
	 * @param prop
	 * 		the desired property
	 * 
	 * @param <T>
	 * 		the property data type
	 * @return
	 * 		the converted property value or {@code null} if no value is available or
	 * 		the given property is not supported in the given protocol-version.
	 * 
	 * @see EncodingUtils#decodeString
	 */
	public <T> T getPropertyValueObject(E prop);
	
	/**
	 * Returns all supported properties.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the protocol version, a different set of properties my be returned.
	 * 
	 * @return
	 * 		all properties supported in the current protocol version
	 */
	public EnumSet<E> getProperties();
	
	/**
	 * Returns all property values as strings.
	 * 
	 * @return 
	 * 		all properties as strings
	 */
	public List<String> getPropertyValues();
	
	/**
	 * Gets a map with all available properties and their values.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the protocol version a different set of properties may be returned.
	 * 
	 * @return
	 * 		a map containing all properties and their values.
	 * 
	 * @see EnumUtils#getEnums
	 */
	public EnumMap<E, String> getPropertyMap();	
	
	/**
	 * Sets the property value for the given property.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the given property is not supported for the current
	 * protocol version, than calling this method has no effect.
	 * 
	 * @param prop
	 * 		the property whose value should be set
	 * @param value
	 * 		the new value
	 * @return
	 * 		the old value.
	 */
	public String setPropertyValue(E prop, String value);
	
	/**
	 * Sets the property value for the given property.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the current protocol version, a different property
	 * may be located on the same index. 
	 * 
	 * @param propIdx 
	 * 		the index of the argument, whose value should be set.<br> 
	 * 		Depending on the current protocol version the same index
	 * 		may address different properties. 
	 * @param value
	 * 		the new value
	 * @return
	 * 		the old value
	 * @throws IndexOutOfBoundsException
	 * 		if the specified index is out of range
	 */
	public String setPropertyValue(int propIdx, String value);
	
	/**
	 * Sets the property value for the given property.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the given property is not supported for the current
	 * protocol version, than calling this method has no effect.
	 * 
	 * @param <S>
	 * 		the type of the property value
	 * @param <T>
	 * 		the to-string type of the property value
	 * @param prop
	 * 		the property
	 * @param propValue
	 * 		the property value as object
	 */
	public <S,T> void setPropertyValueObject(E prop, S propValue);	
}
