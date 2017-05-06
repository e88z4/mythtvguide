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
package org.jmythapi.protocol.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * This annotation is used to define the data type of a response parameter.
 * <p>
 * The datatype specified with this annotation is used by {@link IPropertyAware} objects
 * to do the "string to object conversion" of their property values, when using the functions 
 * {@code IPropertyAware.getPropertyValueObject(Enum)} and {@code IPropertyAware.setPropertyValueObject(Enum, Object)}.
 * 
 * <h3>Usage example:</h3>
 * <h4>Specify the datatype of a propery:</h4>
 * {@mythCodeExample <pre>
 *   public interface IProgramInfo &#123;
 *     public static enum Props &#123;
 *       ...
 *       // REC_STATUS is of type ProgramRecordingStatus
 *       &#064;MythParameterType(ProgramRecordingStatus.class)
 *       REC_STATUS,
 *       
 *       // REC_ID is of type Integer
 *       &#064;MythParameterType(Integer.class)
 *       REC_ID,
 *       ...
 *     &#125;
 *   ...
 * &#125;
 * </pre>}
 * 
 * <h4>Query the datatype of a propery:</h4>
 * The datatype of a property can be queried using functions of the {@link EnumUtils} class.<br>
 * If a property has no datatype annotation, {@code String} is assumed as datatype of the property.
 * 
 * {@mythCodeExample <pre>
 *    // the following will return ProgramRecordingStatus.class
 *    Class clazz = EnumUtils.getEnumDataType(IProgramInfo.Props.REC_STATUS);
 * </pre>}
 * 
 * @see EnumUtils#getEnumDataType(Enum)
 * @see IPropertyAware#getPropertyValueObject(Enum)
 * @see IPropertyAware#setPropertyValueObject(Enum, Object)
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface MythParameterType {
	Class<?> value() default String.class;
	
	/**
	 * The type to use when the data type should be converted
	 * to a string.
	 * <p>
	 * E.g. if this is a {@code Boolean} type but the stringType is set to {@code Integer}
	 * then {@code true} will be converted to {@code 1}.
	 * 
	 * @see EncodingUtils#encodeObject
	 */
	Class<?> stringType() default Object.class;
}
