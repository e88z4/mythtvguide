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
package org.jmythapi.utils;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythParameterDefaultValue;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * This class provides enumeration related utility function.
 * <p>
 * This class provides methods to:
 * <ul>
 *  <li>Determine the default value of a property ({@link #getEnumDefaultValue link}).</li>
 *  <li>Determine the data-type of a property ({@link #getEnumDataType link}).</li>
 *  <li>Determine the string-type of a property ({@link #getEnumStringType link}).</li>
 *  <li>Copy property values between {@link IPropertyAware property-aware} objects ({@link #copyEnumValues link}).</li>
 * </ul>
 * <p>
 * See the subclasses of this class for additional methods regarding database- or protocol-related enumeration properties.<br>
 * See the javadoc of the provided methods for some usage examples of this class.
 */
public class GenericEnumUtils {
	/**
	 * For logging
	 */
	protected static final Logger logger = Logger.getLogger(EnumUtils.class.getName());	

//	public static <E extends Enum<E>, VRA extends Annotation, V extends Enum<V> & IVersion, VR extends AVersionRange<V,VRA>, S extends Annotation> 
//	VR getEnumVersionRange(
//		Class<E> enumClass, Enum<E> enumProp,		
//		Class<V> versionClass,
//		Class<VR> rangeClass,
//		Class<VRA> rangeAnnotation,
//		Class<S> skipAnnotation,
//		VR defaultRange
//	) {
//		VR range = null;
//		try {
//			final Field enumField = enumClass.getField(enumProp.name());
//			final VRA versionRange = enumField.getAnnotation(rangeAnnotation);
//			final boolean skipProp = skipAnnotation==null?false:enumField.getAnnotation(skipAnnotation) != null;
//			if(skipProp) {
//				range = null;
//			} else if(versionRange != null) {
//				// otherwise we try to use the proper constructor
//				final Constructor<VR> rangeConstructor = rangeClass.getConstructor(new Class[] { 
//					rangeAnnotation
//				});
//		
//				// creating a new message instance
//				range = rangeConstructor.newInstance(new Object[]{versionRange});
//			} else {
//				range = defaultRange;
//			}
//		} catch (Exception e) {
//			// this should not happens
//			logger.log(Level.SEVERE,"Unexpected error",e);
//		}
//		return range;
//	}	
	
	/* ==============================================================================
	 * MythParameterType related functions
	 * ============================================================================== */	
	
	/**
	 * Gets the datatype of a enumeration property.
	 * <p>
	 * This function determines the datatype, the value of a given property should be converted to, whereas 
	 * the datatype of a property is defined with the {@link MythParameterType} annotation.
	 * <p>
	 * The result of this function is required for the string to object conversion of property values. 
	 * (See: {@link EncodingUtils#decodeString(Class, ProtocolVersion, int, String)}).
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *   public interface IProgramInfo &#123;
	 *     public static enum Props &#123;
	 *       ...
	 *       &#064;MythParameterType(ProgramRecordingStatus.class)
	 *       REC_STATUS,
	 *       ...
	 *     &#125;
	 *   ...
	 *   // the following will return ProgramRecordingStatus.class
	 *   Class clazz = EnumUtils.getEnumDataType(IProgramInfo.Props.REC_STATUS);
	 * &#125;
	 * </pre>}
	 * 
	 * @param <T>
	 * 		the property value data type
	 * @param <E>
	 * 		the property type
	 * @param enumProp
	 * 		the Enum property.
	 * @return
	 * 		the class of the type or {@code null} if unknown
	 */
	public static <T,E extends Enum<E>> Class<T> getEnumDataType(E enumProp) {
		return getEnumDataType(enumProp.getDeclaringClass(), enumProp);
	}
	
	/**
	 * Returns the datatype the value of a given property should be converted to.
	 * <p>
	 * The datatype to use can be specified by annotating a property with the {@link MythParameterType} annotation.
	 * 
	 * @param <E>
	 * 		the Enum type
	 * @param enumClass
	 * 		the Enum class
	 * @param enumProp
	 * 		the Enum property
	 * @return
	 * 		the class of the type or {@code null} if unknown
	 * 
	 * @see #getEnumDataType(Enum)
	 */
	@SuppressWarnings("unchecked")
	static <T, E extends Enum<E>> Class<T> getEnumDataType(Class<E> enumClass, Enum<E> enumProp) {
		Class<T> paramTypeClass = null;
		try {
			final Field enumField = enumClass.getField(enumProp.name());
			final MythParameterType parameterType = enumField.getAnnotation(MythParameterType.class);
			if (parameterType == null) paramTypeClass = (Class<T>) String.class;
			else paramTypeClass = (Class<T>) parameterType.value();
		} catch (Exception e) {
			logger.log(Level.SEVERE,String.format("Unable to determine datatype of property %s",enumProp),e);
		}
		return paramTypeClass;
	}
	
	/**
	 * Gets the string-data-type for a given enumeration-constant.
	 * <p>
	 * This function determines the string data-type for an enumeration-constant. This "string-type" is e.g.
	 * used if a {@code Boolean} value should be converted into a {@code String}. In this case the "string-type" 
	 * specifies if the Boolean value should be converted into {@code 0} or {@code 1}, or into {@code true} or {@code false}.
	 * <p>
	 * The string data-type for an enumeration constant is defined via {@link MythParameterType#stringType()}.
	 * 
	 * @param <T> 
	 * 		the string data-type
	 * @param <E>
	 * 		the enumeration type
	 * @param enumProp
	 * 		the enumeration constant
	 * @return
	 * 		the string data-type  or {@code null}
	 */
	public static <T,E extends Enum<E>> Class<T> getEnumStringType(E enumProp) {
		return getEnumStringType(enumProp.getDeclaringClass(), enumProp);
	}		
	
	/**
	 * Gets the string-data-type for a given enumeration-constant.
	 * <p>
	 * This function determines the string data-type for an enumeration-constant. This "string-type" is e.g.
	 * used if a {@code Boolean} value should be converted into a {@code String}. In this case the "string-type" 
	 * specifies if the Boolean value should be converted into {@code 0} or {@code 1}, or into {@code true} or {@code false}.
	 * <p>
	 * The string data-type for an enumeration constant is defined via {@link MythParameterType#stringType()}.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // the following will return ProgramRecordingStatus.class
	 *    Class clazz = EnumUtils.getEnumDataType(IProgramInfo.Props.REC_STATUS);
	 * </pre>}
	 * 
	 * @param <T> 
	 * 		the string data-type
	 * @param <E>
	 * 		the enumeration type
	 * @param enumClass
	 * 		the enumeration class
	 * @param enumProp
	 * 		the enumeration constant
	 * @return
	 * 		the string data-type  or {@code null}
	 */	
	@SuppressWarnings("unchecked")
	public static <T, E extends Enum<E>> Class<T> getEnumStringType(Class<E> enumClass, Enum<E> enumProp) {
		Class<T> stringTypeClass = null;
		try {
			final Field enumField = enumClass.getField(enumProp.name());
			final MythParameterType parameterType = enumField.getAnnotation(MythParameterType.class);
			if (parameterType != null) {
				stringTypeClass = (Class<T>) parameterType.stringType();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE,String.format("Unable to determine datatype of property %s",enumProp),e);
		}
		return stringTypeClass;
	}

	/* ==============================================================================
	 * MythParameterDefaultValue related functions
	 * ============================================================================== */
	
	/**
	 * Gets the default values for all constants of the given enumeration-constants.
	 * <p>
	 * This function uses the {@link MythParameterDefaultValue} annotation to determine
	 * the default value for the enumeration-constant. If no annotation can be found, {@code null} is returned.
	 * 
	 * @param <E>
	 * 		the type of the enumeration constant
	 * @param enumClass
	 * 		the enum class
	 * @param enumProps
	 * 		a set of enumeration constants
	 * @return
	 * 		a list containing the default values for all enumeration-constants.
	 */	
	public static <E extends Enum<E>> List<String> getDefaultValuesList(Class<E> enumClass, EnumSet<E> enumProps) {
		final List<String> defaultValues = new ArrayList<String>(enumProps.size());
		for(E enumProp : enumProps) {
			final String defaultValue = getEnumDefaultValue(enumClass,enumProp);
			defaultValues.add(defaultValue);
		}
		return defaultValues;
	}	
	
	/**
	 * Gets the default value for a given enumeration-constant.
	 * <p>
	 * This function uses the {@link MythParameterDefaultValue} annotation to determine
	 * the default value for a given enumeration-constant. If no annotation can be found, {@code null} is returned.
	 * 
	 * <h4>Usage Example:</h4>
	 * {@mythCodeExample <pre>
	 *    String defaultValue = GenericEnumUtils.getEnumDefaultValue(IProgramInfo.Props.STORAGE_GROUP);
	 *    System.out.println(defaultValue);
	 * </pre>}
	 * 
	 * @param <E>
	 * 		the type of the enumeration constant
	 * @param enumProp
	 * 		the enumeration constant to inspect
	 * @return
	 * 		the default value or {@code null}.
	 */	
	public static <E extends Enum<E>> String getEnumDefaultValue(Enum<E> enumProp) {
		return getEnumDefaultValue(enumProp.getDeclaringClass(),enumProp);
	}
	
	/**
	 * Gets the default value for a given enumeration-constant.
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
	 * @param enumProp
	 * 		the enumeration constant to inspect
	 * @return
	 * 		the default value or {@code null}.
	 */
	public static <E extends Enum<E>> String getEnumDefaultValue(Class<E> enumClass, Enum<E> enumProp) {		
		String defaultValueString = null;
		try {
			final Field enumField = enumClass.getField(enumProp.name());			
			final MythParameterDefaultValue defaultValue = enumField.getAnnotation(MythParameterDefaultValue.class);
			if(defaultValue != null) {
				defaultValueString = defaultValue.value();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE,String.format("Unable to determine default-value of property %s",enumProp),e);
		}	
		return defaultValueString;
	}
	
	/**
	 * Creates a map containing the names and enumeration-constants for all properties of the property-aware object. 
	 * 
	 * @param <E>
	 * 		the type of the enumeration constants
	 * @param props
	 * 		the properties to process
	 * @return
	 * 		a map containing the names of all properties as key and the
	 * 		enumeration-constants itself as values.
	 */	
	public static <E extends Enum<E>> Map<String,E> getEnumNameMap(IPropertyAware<E> props) {
		return getEnumNameMap(props.getProperties());
	}
	
	/**
	 * Creates a map containing the names and enumeration-constants for a given enumeration-set. 
	 * 
	 * @param <E>
	 * 		the type of the enumeration constants
	 * @param props
	 * 		the properties to process
	 * @return
	 * 		a map containing the names of all properties as key and the
	 * 		enumeration-constants itself as values.
	 */
	public static <E extends Enum<E>> Map<String,E> getEnumNameMap(EnumSet<E> props) {	
		final Map<String,E> propsNames = new LinkedHashMap<String,E>();
		for (E prop : props) {
			propsNames.put(prop.name(),prop);
		}
		return propsNames;
	}
	
	/**
	 * A function to determine equivalent parameters of a source and target object.
	 * <p>
	 * This function determines the properties of a source- and target-object and compares them by name.
	 * Properties with the same name are handled as equivalent. Optionally the specified {@link MythParameterType parameter-types} 
	 * can be taken into account.
	 * <br>
	 * If the timeFieldSupport is set, source-properties with the name {@code XXX_DATE_TIME} are handled as equivalent
	 * to target-properties with the names {@code XXX_DATE} and {@code XXX_TIME}.
	 */
	public static <S,T,E extends Enum<E>, F extends Enum<F>> Map<E,Set<F>> getEnumMapping(
		EnumSet<E> sourceProps, EnumSet<F> targetProps, boolean checkType, boolean timeFieldSupport
	) {
		// determine all available source- and target-property enums
		final Map<String,E> sourcePropsMap = EnumUtils.getEnumNameMap(sourceProps);
		final Map<String,F> targetPropsMap = EnumUtils.getEnumNameMap(targetProps);
		
		// determine the properties to copy
		final HashSet<String> equalNames = new LinkedHashSet<String>(targetPropsMap.keySet());
		equalNames.retainAll(sourcePropsMap.keySet());
		
		// copy data
		final Map<E,Set<F>> mapping = new LinkedHashMap<E,Set<F>>();
		for(String copyPropName : equalNames) {
			// determine the source- and target-property by name
			final E sourceProp = sourcePropsMap.get(copyPropName);
			final F targetProp = targetPropsMap.get(copyPropName);
			
			// determine the source and target datatype
			boolean equal = true;
			if(sourceProp != targetProp && checkType) {
				final Class<S> sourceType = getEnumDataType(sourceProp);
				final Class<S> targetType = getEnumDataType(targetProp);
				equal = (sourceType == null && targetType == null || sourceType.equals(targetType));
			}
			
			if(equal) {
				Set<F> targetPropList = null;
				if(mapping.containsKey(sourceProp)) {
					targetPropList = mapping.get(sourceProp);
				} else {
					targetPropList = new HashSet<F>();
					mapping.put(sourceProp,targetPropList);
				}
				targetPropList.add(targetProp);
			}
		}
		
		// a special mapping for DATE_TIME to DATE and TIME
		if(timeFieldSupport) {
			final Pattern p = Pattern.compile("(\\w+)_DATE_TIME");		
			for(Entry<String,E> sourceProp : sourcePropsMap.entrySet()) {
				final String sourcePropName = sourceProp.getKey();
				final Matcher m = p.matcher(sourcePropName);
				if(m.matches()) {
					final Class<S> sourceType = getEnumDataType(sourceProp.getValue());
					if(!java.util.Date.class.isAssignableFrom(sourceType)) continue;
					
					Set<F> targetPropList = null;
					if(mapping.containsKey(sourceProp)) {
						targetPropList = mapping.get(sourceProp);
					} else {
						targetPropList = new HashSet<F>();
						mapping.put(sourceProp.getValue(),targetPropList);
					}				
					
					final String propNamePrefix = m.group(1);
					String targetPropName = null;
					
					targetPropName = propNamePrefix + "_DATE";
					if(targetPropsMap.containsKey(targetPropName)) {
						final F targetProp = targetPropsMap.get(targetPropName);
						final Class<S> targetType = getEnumDataType(targetProp);
						if(java.sql.Date.class.isAssignableFrom(targetType)) {
							targetPropList.add(targetProp);
						}
					}
					
					targetPropName = propNamePrefix + "_TIME";
					if(targetPropsMap.containsKey(propNamePrefix + "_TIME")) {
						final F targetProp = targetPropsMap.get(targetPropName);
						final Class<S> targetType = getEnumDataType(targetProp);
						if(Time.class.isAssignableFrom(targetType)) {
							targetPropList.add(targetProp);
						}
					}
				}
			}
		}
		
		return mapping;
	}
	
	/**
	 * A function to copy properties from a source- to a target-object.
	 * <p>
	 * This function determines the properties of the source- and target-object and copies
	 * the values of properties with the same name from source to target.<br>
	 * The source- and target-object must be {@link IPropertyAware}.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *   // the source object
	 *   RecorderNextProgramInfo nextProgram = ....;
	 * 	
	 *   // the target object
	 *   ProgramInfo programInfo = new ProgramInfo(nextProgram.getVersionNr());
	 * 	
	 *   // copy properties
	 *   GenericEnumUtils.copyEnumValues(nextProgram,programInfo);
	 * </pre>}
	 * 
	 * 
	 * @param <E>
	 * 		the type of the source properties
	 * @param <S>
	 * 		the type of the source object
	 * @param <F>
	 * 		the type of the target properties
	 * @param <T>
	 * 		the type of the target object
	 * @param source
	 * 		the source object
	 * @param target
	 * 		the target object
	 */	
	public static final <E extends Enum<E>, S extends IPropertyAware<E>, F extends Enum<F>, T extends IPropertyAware<F>> Map<E,Set<F>> copyEnumValues(
			S source, T target
	) {
		return copyEnumValues(source,target, true, true, true);
	}
	
	/**
	 * A function to copy properties from a source- to a target-object.
	 * <p>
	 * This function determines the properties of the source- and target-object and copies
	 * the values of properties with the same name from source to target.<br>
	 * The source- and target-object must be {@link IPropertyAware}.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *   // the source object
	 *   RecorderNextProgramInfo nextProgram = ....;
	 * 	
	 *   // the target object
	 *   ProgramInfo programInfo = new ProgramInfo(nextProgram.getVersionNr());
	 * 	
	 *   // copy properties
	 *   GenericEnumUtils.copyEnumValues(nextProgram,programInfo, true, true, true);
	 * </pre>}
	 * 
	 * 
	 * @param <E>
	 * 		the type of the source properties
	 * @param <S>
	 * 		the type of the source object
	 * @param <F>
	 * 		the type of the target properties
	 * @param <T>
	 * 		the type of the target object
	 * @param source
	 * 		the source object
	 * @param target
	 * 		the target object
	 * @param checkType
	 * 		if the source- and target- property must have the same {@link MythParameterType datatype}.
	 * @param replaceNullWithDefault
	 * 		if {@code null} values of source-properties should be replaced with the {@link MythParameterDefaultValue default-values} 
	 *      specified for target-properties.
	 * @param timeFieldSupport
	 * 		if source-properties with the name {@code XXX_DATE_TIME} should be used to set the value of target-properties with the name
	 * 	    {@code XXX_DATE} and {@code XXX_TIME}
	 */		
	public static final <E extends Enum<E>, S extends IPropertyAware<E>, F extends Enum<F>, T extends IPropertyAware<F>> Map<E,Set<F>> copyEnumValues(
		S source, T target, boolean checkType, boolean replaceNullWithDefault, boolean timeFieldSupport
	) {
		// determine the properties to copy
		final Map<E,Set<F>> propsMapping = getEnumMapping(source.getProperties(),target.getProperties(),checkType,timeFieldSupport);
		
		// copy data
		for(Entry<E,Set<F>> props : propsMapping.entrySet()) {
			// determine the source- and target-properties
			final E sourceProp = props.getKey();
			final Set<F> targetProps = props.getValue();
			
			// copy value
			final String sourceValue = source.getPropertyValue(sourceProp);
			for(F targetProp : targetProps) {
				String targetValue = sourceValue;
				
				// replace null values with default values
				if(sourceValue == null && replaceNullWithDefault) {
					targetValue = EnumUtils.getEnumDefaultValue(targetProp);
				}
				
				// special handling for date/time only values
				final Class<S> targetPropType = EnumUtils.getEnumDataType(targetProp);
				if(Time.class.isAssignableFrom(targetPropType)) {
					String[] valueParts = sourceValue.split("[\\sT]");
					if(valueParts.length==2) targetValue = valueParts[1];
				} else if (Date.class.isAssignableFrom(targetPropType)) {
					String[] valueParts = sourceValue.split("[\\sT]");
					if(valueParts.length==2) targetValue = valueParts[0];
				} else {
					
				}
				target.setPropertyValue(targetProp, targetValue);
			}
		}
		
		return propsMapping;
	}
}
