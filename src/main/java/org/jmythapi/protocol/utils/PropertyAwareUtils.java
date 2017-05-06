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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.response.IFilter;

public class PropertyAwareUtils {
	/**
	 * <p>
	 * Usage example:
	 * <br>
	 * {@mythCodeExample <pre>
	 *    // query recorded programs
	 *    IProgramInfoList allRecordings = backend.queryRecordings();
	 *    
	 *    // Group programs by category
	 *    Map&lt;Object,List&lt;IProgramInfo&gt;&gt; recordingsByCategory = PropertyAwareUtils.groupListByProperty(allRecordings,IProgramInfo.Props.CATEGORY);
	 *    for(Entry&lt;Object,List&lt;IProgramInfo&gt;&gt; entry : recordingsByCategory.entrySet()) &#123;
	 *       System.out.println(String.format(
	 *          "\r\nCATEGORY '%s': %02d recordings",
	 *          entry.getKey(),
	 *          entry.getValue().size()
	 *       ));
	 *       
	 *       for(IProgramInfo program : entry.getValue()) &#123;
	 *          System.out.println(String.format(
	 *             "- %s",
	 *             program.getFullTitle()
	 *          ));
	 *       &#125;
	 *    &#125;
	 * </pre>}
	 * <br>
	 * 
	 * @param <P>
	 * 		The class of the enumeration property used for grouping
	 * @param <E>
	 * 		The class of the property-aware object
	 * @param list
	 * 		A list of property-aware objects
	 * @param prop
	 * 		The property to use for grouping
	 * @return
	 * 		the grouped lists
	 */
	public static <P extends Enum<P>, E extends IPropertyAware<P>> Map<Object, List<E>> groupListByProperty(
		Iterable<E> list, P prop
	) {
		return groupListByProperty(list, prop, null);
	}
		
	public static <P extends Enum<P>, E extends IPropertyAware<P>> Map<Object, List<E>> groupListByProperty(
		Iterable<E> list, P prop, IFilter<P,E> filter
	) {
		if(prop == null) return Collections.emptyMap();
		else if(list == null) return Collections.emptyMap();
		
		final HashMap<Object,List<E>> entityMap = new HashMap<Object, List<E>>();
		for(E entity : list) {
			// skipping objects not matching the filter
			if(filter != null && !filter.accept(entity)) {
				continue;
			}
			
			// getting the property value
			final Object propObject = entity.getPropertyValueObject(prop);
			
			// getting all currently found entities for this value
			List<E> entityList = null;
			if(entityMap.containsKey(propObject)) {
				entityList = entityMap.get(propObject);
			} else {
				entityList = new ArrayList<E>();
				entityMap.put(propObject,entityList);
			}
			
			// add the newly found entity
			entityList.add(entity);
		}
		
		// return the result
		return entityMap;
	}
	
	/**
	 * Compares two property-aware objects.
	 * <p>
	 * This function compares two property-aware objects and determines those properties, 
	 * whose values are not equal in the compared objects. As a result a enumeration map is
	 * returned containing the constants and values of all different properties.
	 * <p>
	 * <h4>Usage Example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    // two property aware objects to compare
	 *    IProgramInfo recording1 = ...;
	 *    IProgramInfo recording2 = ...;
	 * 
	 *    // compare the objects
	 *    EnumMap&lt;IProgramInfo.Props,Object[]&gt; diffMap = PropertyAwareUtils.compare(recording1,recording2);
	 *    
	 *    // print differences
	 *    for(Entry&lt;IProgramInfo.Props,Object[]&gt; entry : diffMap.entrySet())&#123;
	 *       IProgramInfo.Props property = entry.getKey();
	 *       Object[] values = entry.getValue();
	 *       System.out.println(String.format(
	 *          "Property %s is different: %s != %s",
	 *          property, values[0],values[1]
	 *       ));
	 *    &#125;
	 * </pre> }
	 * 
	 * 
	 * @param <P>
	 * 		the type of the property
	 * @param <E>
	 * 		the type of the objects
	 * @param source
	 * 		the source object
	 * @param target
	 * 		the target object
	 * @return
	 * 		a map containing all properties that are different
	 */
	public static <P extends Enum<P>, E extends IPropertyAware<P>> EnumMap<P,Object[]> compare(
		IPropertyAware<P> source, IPropertyAware<P> target
	) {
		if(source == null || target == null) return null;
		final Class<P> propClass = source.getPropertyClass();
		
		final EnumMap<P,Object[]> diffMap = new EnumMap<P,Object[]>(propClass);
		
		for(P prop : source.getProperties()) {
			final Object sourceValue = source.getPropertyValueObject(prop);
			final Object targetValue = target.getPropertyValueObject(prop);
			final Object[] values = new Object[]{sourceValue,targetValue};
			
			if(sourceValue == null && targetValue == null) continue;
			else if(sourceValue != null && targetValue != null && sourceValue.equals(targetValue)) continue;
			
			diffMap.put(prop,values);
		}
		
		return diffMap;
	}
}
