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
package org.jmythapi.protocol.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jmythapi.IPositionalValue;
import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;
import org.jmythapi.protocol.annotation.MythParameterDefaultValue;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtocolSkipProperty;
import org.jmythapi.protocol.response.IVersionableValue;
import org.jmythapi.protocol.response.IVersionableValue.VersionablePair;
import org.jmythapi.utils.GenericEnumUtils;

/**
 * This class provides enumeration related utility function.
 * <p>
 * This class provides methods to filter or find enumeration properties for a given protocol version.<br>
 * The methods of this utility class are used by {@link IPropertyAware} objects to get and set their
 * property values. Furthermore {@link IVersionableValue} flags or enumeration constants using this class
 * to determine the current value of a flag or property.
 * <p>
 * See the parent class of this class for additional methods regarding enumeration constants.<br>
 * See the javadoc of the provided methods for some usage examples of this class.
 * 
 * @see IPropertyAware
 * @see IVersionableValue
 */
public class EnumUtils extends GenericEnumUtils {
	/**
	 * A map to cache the result of {@link #getEnumVersionMap(Class)}
	 */
	private static final Map<Class<?>,EnumMap<?,ProtocolVersionRange>> versionMapCache = new HashMap<Class<?>,EnumMap<?,ProtocolVersionRange>>();
	
	/**
	 * Returns the amount of enum properties that are supported in the given protocol version.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 * int length = 0;
	 * 
	 * // will return 30
	 * length = EnumUtils.getEnumLength(IProgramInfo.Props.class, PROTO_VERSION_00);
	 * 
	 * // will return 32
	 * length = EnumUtils.getEnumLength(IProgramInfo.Props.class, PROTO_VERSION_03);
	 * 
	 * // will return 47
	 * length = EnumUtils.getEnumLength(IProgramInfo.Props.class, PROTO_VERSION_56);
	 * 
	 * // will return 41
	 * length = EnumUtils.getEnumLength(IProgramInfo.Props.class, PROTO_VERSION_57);
	 * </pre>}
	 * </br>
	 *  
	 * @param <E>
	 * 		the Enum type
	 * @param propsClass
	 * 		the Enum class
	 * @param protoVersion
	 * 		the protocol version
	 * @return
	 * 		the amount of properties supported in the given protocol version
	 */
	public static <E extends Enum<E>> int getEnumLength(Class<E> propsClass, ProtocolVersion protoVersion) {
		final EnumMap<E,ProtocolVersionRange> versionMap = getEnumVersionMap(propsClass);
		if (versionMap == null) return 0;
		
		int counter = 0;
		for (Entry<E,ProtocolVersionRange> entry : versionMap.entrySet()) {
			final ProtocolVersionRange versionRange = entry.getValue();
			if (!versionRange.isInRange(protoVersion)) {
				continue;
			}
			counter++;
		}
		return counter;
	}
	
	/**
	 * Returns the version-range information for a given enum property.
	 * <p>
	 * If a property is not annotated with the {@link MythProtoVersionAnnotation}, then the property
	 * is seen to be valid in the version-range {@code [0,-1)}.<br>
	 * If a enum property is annotated with {@link MythProtocolSkipProperty}, then the property 
	 * is ignored and {@code null} is returned.
	 * 
	 * @param enumProp
	 * 		the Enum property
	 * @return
	 * 		the version-range of the property or {@code null} if the property should be skipped.
	 */
	public static <E extends Enum<E>> ProtocolVersionRange getEnumVersionRange(Enum<E> enumProp) {
		return getEnumVersionRange(enumProp.getDeclaringClass(), enumProp);
	}
	
	/**
	 * Returns the version-range information for a given enum property.
	 * <p>
	 * If a property is not annotated with the {@link MythProtoVersionAnnotation}, then the property
	 * is seen to be valid in the version-range {@code [0,-1)}.<br>
	 * If a enum property is annotated with {@link MythProtocolSkipProperty}, then the property 
	 * is ignored and {@code null} is returned.
	 * 
	 * @param <E>
	 * 		the Enum type
	 * @param enumClass
	 * 		the Enum class
	 * @param enumProp
	 * 		the Enum property
	 * @return
	 * 		the version-range of the property or {@code null} if the property should be skipped.
	 */
	static <E extends Enum<E>> ProtocolVersionRange getEnumVersionRange(Class<E> enumClass, Enum<E> enumProp) {
		ProtocolVersionRange range = null;
		try {
			final Field enumField = enumClass.getField(enumProp.name());
			final MythProtoVersionAnnotation versionRange = enumField.getAnnotation(MythProtoVersionAnnotation.class);
			final boolean skipProp = enumField.getAnnotation(MythProtocolSkipProperty.class) != null;
			if(skipProp) {
				range = null;
			} else if(versionRange != null) {
				range = new ProtocolVersionRange(versionRange);
			} else {
				range = ProtocolVersionRange.DEFAULT_RANGE;
			}
		} catch (Exception e) {
			// this should not happens
			logger.log(Level.SEVERE,"Unexpected error",e);
		}
		return range;
	}
	
	/**
	 * Returns a map of enumeration properties. For each property the version-range is returned in which 
	 * the enumeration property is supported.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    EnumMap<IProgramInfo.Props, ProtocolVersionRange> enumVersions = EnumUtils.getEnumVersionMap(IProgramInfo.Props.class);
	 *    System.out.println("Property               | Version Range");
	 *    for(Entry<IProgramInfo.Props, ProtocolVersionRange> enumVersion : enumVersions.entrySet()) &#123;
	 *       System.out.println(String.format(
	 *          "%-22s | %s",
	 *          enumVersion.getKey(), enumVersion.getValue()
	 *       ));
	 *    &#125;
	 * </pre>}
	 * The above example will print out the following table:
	 * <pre>
	 * TITLE                  | [00,-1)
	 * SUBTITLE               | [00,-1)
	 * DESCRIPTION            | [00,-1)
	 * SEASON                 | [67,-1)
	 * EPISODE                | [67,-1)
	 * CATEGORY               | [00,-1)
	 * CHANNEL_ID             | [00,-1)
	 * CHANNEL_NUMBER         | [00,-1)
	 * CHANNEL_SIGN           | [00,-1)
	 * CHANNEL_NAME           | [00,-1)
	 * PATH_NAME              | [00,-1)
	 * FILESIZE_HIGH          | [00,57)
	 * FILESIZE_LOW           | [00,57)
	 * FILESIZE               | [57,-1)
	 * START_DATE_TIME        | [00,-1)
	 * END_DATE_TIME          | [00,-1)
	 * DUPLICATE              | [00,57)
	 * SHAREABLE              | [00,57)
	 * FIND_ID                | [00,-1)
	 * HOSTNAME               | [00,-1)
	 * SOURCE_ID              | [00,-1)
	 * CARD_ID                | [00,-1)
	 * INPUT_ID               | [00,-1)
	 * REC_PRIORITY           | [00,-1)
	 * REC_STATUS             | [00,-1)
	 * REC_ID                 | [00,-1)
	 * REC_TYPE               | [00,-1)
	 * REC_DUPS               | [00,03)
	 * DUP_IN                 | [03,-1)
	 * DUP_METHOD             | [03,-1)
	 * REC_START_TIME         | [00,-1)
	 * REC_END_TIME           | [00,-1)
	 * REPEAT                 | [00,57)
	 * PROGRAM_FLAGS          | [00,-1)
	 * REC_GROUP              | [03,-1)
	 * CHAN_COMM_FREE         | [03,57)
	 * CHANNEL_OUTPUT_FILTERS | [06,-1)
	 * SERIES_ID              | [08,-1)
	 * PROGRAM_ID             | [08,-1)
	 * INETREF                | [67,-1)
	 * LAST_MODIFIED          | [11,-1)
	 * STARS                  | [12,-1)
	 * ORIGINAL_AIRDATE       | [12,-1)
	 * HAS_AIRDATE            | [15,57)
	 * TIMESTRETCH            | [18,23)
	 * PLAY_GROUP             | [23,-1)
	 * REC_PRIORITY2          | [25,-1)
	 * PARENT_ID              | [31,-1)
	 * STORAGE_GROUP          | [32,-1)
	 * AUDIO_PROPERTIES       | [35,-1)
	 * VIDEO_PROPERTIES       | [35,-1)
	 * SUBTITLE_TYPE          | [35,-1)
	 * YEAR                   | [41,-1)
	 * </pre>
	 * 
	 * @param <E>
	 * 		the Enum Type
	 * @param propsClass
	 * 		the Enum class
	 * @return
	 * 		a map with the version-range information for each property
	 */	
	public static <E extends Enum<E>> EnumMap<E,ProtocolVersionRange> getEnumVersionMap(Class<E> propsClass) {
		// cache lookup
		if(versionMapCache.containsKey(propsClass)) {
			@SuppressWarnings("unchecked")
			final EnumMap<E,ProtocolVersionRange> enumVersions = (EnumMap<E,ProtocolVersionRange>) versionMapCache.get(propsClass);
			return enumVersions;
		}
		
		// getting all enum-constants for the given enum
		final E[] propsArray = propsClass.getEnumConstants();
		if (propsArray == null) return null;
		
		// determine the version range for each enum-constant
		final EnumMap<E, ProtocolVersionRange> enumVersions = new EnumMap<E, ProtocolVersionRange>(propsClass);
		for (int i=0; i < propsArray.length; i++) {
			final E enumItem = propsArray[i];
			final ProtocolVersionRange versionRange = EnumUtils.getEnumVersionRange(propsClass, enumItem);
			if(versionRange == null) continue;
			enumVersions.put(enumItem, versionRange);
		}
		
		// insert into cache
		versionMapCache.put(propsClass,enumVersions);
		
		// return result
		return enumVersions;
	}
	
	/**
	 * Gets a set of Enum properties from a given Enum type which are supported in the given protocol versions.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    EnumSet<IProgramInfo.Props> props = EnumUtils.getEnums(IProgramInfo.Props.class, PROTO_VERSION_00);
	 * </pre>}
	 * 
	 * @param <E>
	 * 		the Enum Type
	 * @param propsClass
	 * 		the Enum class
	 * @param protoVersion
	 * 		the protocol version
	 * @return
	 * 		all enum properties that are supported in the given protocol version
	 */
	public static <E extends Enum<E>> EnumSet<E> getEnums(Class<E> propsClass, ProtocolVersion protoVersion) {
		final EnumMap<E,ProtocolVersionRange> versionMap = getEnumVersionMap(propsClass);
		if (versionMap == null) return null;
		
		final EnumSet<E> props = EnumSet.noneOf(propsClass);
		for (Entry<E,ProtocolVersionRange> entry : versionMap.entrySet()) {
			final E enumItem = entry.getKey();
			final ProtocolVersionRange versionRange = entry.getValue();
			
			if (!versionRange.isInRange(protoVersion)) {
				// property not supported in this version. skipping property
				continue;
			}

			props.add(enumItem);
		}
		return props;
	}
	
	/**
	 * Returns the position of the Enum property in the backend response-array, depending 
	 * on the given current version.
	 * 
	 * <h4>Usage example:</h4>
	 * In the following example the second call will return {@code -1} because the property {@code WATCHING_RECORDING} 
	 * is unknown in protocol version 0.
	 * {@mythCodeExample <pre>
	 *    // the following will return 3
	 *    int pos1 = EnumUtils.getEnumPosition(IRemoteEncoderState.State.WATCHING_RECORDING, PROTO_VERSION_57)
	 *    
	 *    // the following will return -1
	 *    int pos2 = EnumUtils.getEnumPosition(IRemoteEncoderState.State.WATCHING_RECORDING, PROTO_VERSION_00)
	 * </pre>}
	 * 
	 * @param <E>
	 * 		the Enum type
	 * @param enumProp
	 * 		the Enum property
	 * @param protoVersion
	 * 		the protocol version
	 * @return
	 * 		the position of the property in the response array.
	 */
	public static <E extends Enum<E>> int getEnumPosition(Enum<E> enumProp, ProtocolVersion protoVersion) {
		
		// getting all enums for the given enum class and protocol version
		final EnumSet<E> enumSet = getEnums(enumProp.getDeclaringClass(), protoVersion);
		
		// searching for the requested enum and return it's position
		int pos = -1;
		for(E nextProp : enumSet) {
			pos++;
			if(nextProp.equals(enumProp)) {
				return pos;
			}
		}
		
		logger.warning(String.format(
			"The enumeration property '%s' is not supported in protocol version '%s'.",
			enumProp, protoVersion
		));
		return -1;
	}
	
	/**
	 * Returns the Enum property for the given name and the given protocol version.
	 * <p>
	 * If the specified property-name is not available for the given protocol version, {@code null} is returned.
	 * 
	 * <h4>Usage example:</h4>
	 * In the following example the first call returns {@code null}, because the property was introduced 
	 * first in version <@code 45}.
	 * {@mythCodeExample <pre>
	 *    // the following will return null
	 *    IRemoteEncoderState.State state1 = EnumUtils.getEnum(IRemoteEncoderState.State.class,PROTO_VERSION_44,"WATCHING_VIDEO");
	 * 
	 *    // the following will return State.WATCHING_VIDEO
	 *    IRemoteEncoderState.State state2 = EnumUtils.getEnum(IRemoteEncoderState.State.class,PROTO_VERSION_45,"WATCHING_VIDEO");
	 * </pre>}
	 * 
	 * @param <E>  
	 * 		the Enum type
	 * @param propsClass 
	 * 		the Enum class
	 * @param protoVersion 
	 * 		the protocol version
	 * @param propName 
	 * 		the property name
	 * @return 
	 * 		the found Enum property or {@code null}
	 */
	public static <E extends Enum<E>> E getEnum(Class<E> propsClass, ProtocolVersion protoVersion, String propName) {
		final EnumSet<E> enumSet = getEnums(propsClass, protoVersion);
		if(enumSet == null) return null;
		
		for (E enumItem : enumSet) {
			if(enumItem.name().equals(propName)) {
				return enumItem;
			}
		}
		
		logger.warning(String.format(
			"The enumeration property '%s' is not supported in protocol version '%s'.",
			propName, protoVersion
		));		
		return null;
	}
	
	/**
	 * Returns a single Enum-property at the given position (in the backend response array) 
	 * depending on the given protocol-version.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    // the following will return State.WATCHING_RECORDING
	 *    IRemoteEncoderState.State state1 = EnumUtils.getEnum(IRemoteEncoderState.State.class, PROTO_VERSION_30, 3);
	 * 
	 *    // the following will return State.WATCHING_VIDEO
	 *    IRemoteEncoderState.State state2 = EnumUtils.getEnum(IRemoteEncoderState.State.class, PROTO_VERSION_57, 3);
	 * </pre>}
	 * 
	 * 
	 * @param <E> 
	 * 		the Enum Type
	 * @param propsClass 
	 * 		the class of the Enum
	 * @param protoVersion 
	 * 		the protocol version
	 * @param position 
	 * 		the position of the enum
	 * @return
	 * 		the enum property at the given position
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E getEnum(Class<E> propsClass, ProtocolVersion protoVersion, int position) {				
		// get the enum-set for the current protocol version
		final EnumSet<E> enumSet = getEnums(propsClass, protoVersion);
			
		if(IPositionalValue.class.isAssignableFrom(propsClass)) {
			for(E enumEntry : enumSet) {
				final int entryPos = ((IPositionalValue)enumEntry).getPosition();
				if(entryPos == position) return enumEntry;				
			}
			return null;
		} else {
			if (position < 0) throw new IndexOutOfBoundsException();
			else if (enumSet.size() <= position) throw new IndexOutOfBoundsException();			
			
			// getting the item at the proper position
			return (E) enumSet.toArray()[position];			
		}
	}
	
	/**
	 * Returns the names of all Enum properties that are available for the given protocol version.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    // getting the names of all properties for protocol version 00
	 *    Set<String> programProps = EnumUtils.getEnumNames(IProgramInfo.Props.class, PROTO_VERSION_00);
	 * </pre>}
	 * 
	 *  
	 * @param <E> 
	 * 		the Enum Type
	 * @param propsClass 
	 * 		the class of the Enum
	 * @param protoVersion 
	 * 		the protocol version
	 * @return
	 * 		a set of enum-properties that are supported in the given protocol version
	 */
	public static <E extends Enum<E>> Set<String> getEnumNames(Class<E> propsClass, ProtocolVersion protoVersion) {
		final EnumSet<E> props = getEnums(propsClass, protoVersion);
		if (props == null) return null;
		return getEnumNameMap(props).keySet();
	}
	
	/**
	 * Determines the Enum property which has the given value when using the given protocol-version.
	 * 
	 * <h4>Usage example:</h4>
	 * The following example the property {@code NOT_LISTED} has a value of {@code 6} in 
	 * protocol version {@code 19}, but a value of {@code 13} in protocol version {@code 17}. In protocol
	 * version {@code 33} the value {@code 13} belongs to the property {@code OTHER_SHOWING}. 
	 * {@mythCodeExample <pre>
	 *   IProgramRecordingStatus.Status status = null;
	 * 
	 *   // will return Status.NOT_LISTED
	 *   status = EnumUtils.getVersionableValueEnum(IProgramRecordingStatus.Status.class,PROTO_VERSION_19,6);
	 * 
	 *   // will return Status.NOT_LISTED
	 *   status = EnumUtils.getVersionableValueEnum(IProgramRecordingStatus.Status.class,PROTO_VERSION_17,13);
	 * 
	 *   // will return Status.OTHER_SHOWING
	 *   status = EnumUtils.getVersionableValueEnum(IProgramRecordingStatus.Status.class, PROTO_VERSION_33,13);
	 * </pre>}
	 * 
	 * @param <E>
	 * 		the Enum Type
	 * @param propsClass
	 * 		the class of the enum property
	 * @param protoVersion
	 * 		the given protocol version
	 * @param value
	 * 		the value of the property
	 * @return
	 * 		the found Enum property or {@code null} if no matching property could be found.
	 */
	public static <E extends Enum<E> & IVersionableValue> E getVersionableValueEnum(Class<E> propsClass, ProtocolVersion protoVersion, Long value) {
		if (value == null) return null;
		
		// getting the enum-set for the given protocol-version
		final EnumSet<E> enumSet = EnumUtils.getEnums(propsClass, protoVersion);
		
		// loop through all enums for the given value
		for(E item : enumSet) {
			final Long nextStatusValue = item.getValue(protoVersion);
			if (nextStatusValue != null && nextStatusValue.equals(value)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Returns the actual property value for the given protocol version.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *   // the following will return 12 
	 *   Integer value1 = EnumUtils.getVersionableValue(5, IProgramRecordingStatus.Status.TUNER_BUSY);
	 *   
	 *   // the following will return -8
	 *   Integer value2 = EnumUtils.getVersionableValue(20, IProgramRecordingStatus.Status.TUNER_BUSY);
	 * </pre>}
	 *  
	 * 
	 * @param protoVersion
	 * 		the protocol version
	 * @return
	 * 		the property value for the given protocol version
	 */
	public static Long getVersionableValue(ProtocolVersion protoVersion, IVersionableValue versionableValue) {
		if (versionableValue == null) throw new NullPointerException("No versionable value.");
		else if (protoVersion == null) throw new NullPointerException("No protocol version specified.");
		
		final VersionablePair[] values = versionableValue.getValues();
		if (values.length == 1) return Long.valueOf(values[0].getValue());
		
		Long flagValue = null;
		if (protoVersion.getVersion() == -1) {
			// just return the last entry
			flagValue = values[values.length-1].getValue();
		} else {
			for(int i=values.length-1; i>=0;i--) {
				if(values[i].getProtoVersion().compareTo(protoVersion)<=0) {
					flagValue = Long.valueOf(values[i].getValue());
					break;
				}
			}			
		}
		
		return flagValue; 	
	}
	
	/**
	 * Gets the default value of all constants of a given enumeration class.
	 * <p>
	 * This function uses the {@link MythParameterDefaultValue} annotation to determine
	 * the default value for a given enumeration-constant. If no annotation can be found, {@code null} is returned.
	 * <p>
	 * This function is mainly used when creating a new {@link IPropertyAware} object, 
	 * to init the object with default parameter values.
	 * 
	 * @param <E>
	 * 		the type of the enumeration constant
	 * @param enumClass
	 * 		the enumeration class
	 * @return
	 * 		the default value or {@code null}.
	 */	
	public static <E extends Enum<E>> List<String> getDefaultValuesList(Class<E> enumClass, ProtocolVersion protoVersion) {
		final EnumSet<E> enumProps = getEnums(enumClass, protoVersion);
		if(enumProps == null) return null;
		
		final List<String> defaultValues = new ArrayList<String>(enumProps.size());
		for(E enumProp : enumProps) {
			final String defaultValue = getEnumDefaultValue(enumClass,enumProp);
			defaultValues.add(defaultValue);
		}
		return defaultValues;
	}	
	
}
