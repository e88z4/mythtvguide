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
import org.jmythapi.utils.GenericEnumUtils;

/**
 * This annotation is used to define the default value of a response parameter.
 * <p>
 * The default value defined by this annotation is always of type {@code String},
 * even if the {@link MythParameterType} annotation is used to specified a different data-type. 
 * Therefore to set the value of a property to its default value, function {@link IPropertyAware#setPropertyValue(int, String)}
 * must be used.
 * 
 * <h3>Usage example:</h3>
 * <h4>Specify the default value of a property:</h4>
 * An example hot to specify the default value of an enum property.
 * {@mythCodeExample <pre>
 *    public interface IProgramInfo extends IPropertyAware<IProgramInfo.Props> &#123;
 *       public static enum Props &#123;
 *          ...
 *          // SEASON has the default value 0
 *          &#064;MythParameterType(Integer.class)
 *          &#064;MythParameterDefaultValue("0")
 *          SEASON,
 *          ...
 *          // STORAGE_GROUP has the default value "Default"
 *          &#064;MythParameterDefaultValue("Default")
 *          STORAGE_GROUP,
 *          ...
 *       &#125;
 *    &#125;
 * </pre>}
 * 
 * <h4>Query the default value of a propery:</h4>
 * An example how to query the default value of an enum property at runtime.
 * {@mythCodeExample <pre>
 *    // The following will return "Default"
 *    String defaultValue = GenericEnumUtils.getEnumDefaultValue(IProgramInfo.Props.STORAGE_GROUP);
 *    System.out.println(defaultValue);
 * </pre>}
 * 
 * @see GenericEnumUtils#getEnumDefaultValue
 * @see GenericEnumUtils#getDefaultValuesList(Class, java.util.EnumSet)
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface MythParameterDefaultValue {
	String value() default "";
}
