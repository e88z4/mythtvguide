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
package org.jmythapi.database.utils;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jmythapi.IPositionalValue;
import org.jmythapi.database.DatabaseVersionRange;
import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.utils.GenericEnumUtils;

public class EnumUtils extends GenericEnumUtils {
	static <E extends Enum<E>> DatabaseVersionRange getEnumVersionRange(Class<E> enumClass, Enum<?> enumProp) {
		DatabaseVersionRange range = null;
		try {
			final Field enumField = enumClass.getField(enumProp.name());
			final MythDatabaseVersionAnnotation versionRange = enumField.getAnnotation(MythDatabaseVersionAnnotation.class);
			if(versionRange != null) {
				range = new DatabaseVersionRange(versionRange);
			} else {
				range = DatabaseVersionRange.DEFAULT_RANGE;
			}
		} catch (Exception e) {
			// this should not happens
			logger.log(Level.SEVERE,"Unexpected error",e);
		}
		return range;
	}	
	
	public static <E extends Enum<E>> EnumMap<E,DatabaseVersionRange> getEnumVersionMap(Class<E> propsClass) {
		// getting all enum-constants for the given enum
		final E[] propsArray = propsClass.getEnumConstants();
		if (propsArray == null) {
			logger.warning(String.format(
				"Class %s has no enum constants.",
				propsClass
			));
			return null;
		}
		
		// determine the version range for each enum-constant
		final EnumMap<E, DatabaseVersionRange> enumVersions = new EnumMap<E, DatabaseVersionRange>(propsClass);
		for (int i=0; i < propsArray.length; i++) {
			final E enumItem = propsArray[i];
			final DatabaseVersionRange versionRange = EnumUtils.getEnumVersionRange(propsClass, enumItem);
			if(versionRange == null) continue;
			enumVersions.put(enumItem, versionRange);
		}
		
		// return result
		return enumVersions;		
	}
	
	public static <E extends Enum<E>> EnumSet<E> getEnums(Class<E> propsClass, int dbVersion) {
		final EnumMap<E,DatabaseVersionRange> versionMap = getEnumVersionMap(propsClass);
		if (versionMap == null) {
			logger.warning(String.format(
				"Unable to get enum-version-map for class '%s' and version '%d'.",
				propsClass, dbVersion
			));
			return null;
		}
		
		final EnumSet<E> props = EnumSet.noneOf(propsClass);
		for (Entry<E,DatabaseVersionRange> entry : versionMap.entrySet()) {
			final E enumItem = entry.getKey();
			final DatabaseVersionRange versionRange = entry.getValue();
			
			if (!versionRange.isInRange(dbVersion)) {
				// property not supported in this version. skipping property
				continue;
			}

			props.add(enumItem);
		}
		return props;
	}
	
	public static <E extends Enum<E>> int getEnumPosition(Enum<E> enumProp, int dbVersion) {
		// getting all enums for the given version
		final EnumSet<E> enumSet = getEnums(enumProp.getDeclaringClass(), dbVersion);
		
		// searching for the requested enum and return it's position
		int pos = -1;
		for(E nextProp : enumSet) {
			pos++;
			if(nextProp.equals(enumProp)) {
				return pos;
			}
		}
		
		return -1;		
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E getEnum(Class<E> propsClass, int dbVersion, int position) {				
		// get the enum-set for the current protocol version
		final EnumSet<E> enumSet = getEnums(propsClass, dbVersion);
			
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
	
	public static <E extends Enum<E>> EnumMap<E,MythDatabaseColumn> getEnumColumnMap(int dbVersion, Class<E> propsClass) {
		// getting all available enums
		final EnumSet<E> enumSet = getEnums(propsClass, dbVersion);
		if(enumSet == null) return null;
		
		// loop through all properties to determine if they are db columns
		final EnumMap<E,MythDatabaseColumn> enumColumns = new EnumMap<E,MythDatabaseColumn>(propsClass);
		for (E enumItem : enumSet) {
			final MythDatabaseColumn dbColumn = EnumUtils.getEnumColumn(dbVersion, propsClass, enumItem);
			if(dbColumn != null) {
				enumColumns.put(enumItem, dbColumn);
			}
		}
		return enumColumns;
	}
	
	public static <E extends Enum<E>> MythDatabaseColumn getEnumColumn(int dbVersion, Enum<E> enumProp) {
		final Class<E> enumClass = enumProp.getDeclaringClass();
		return getEnumColumn(dbVersion, enumClass, enumProp);
	}
	
	public static <E extends Enum<E>> MythDatabaseColumn getEnumColumn(int dbVersion, Class<E> enumClass, Enum<E> enumProp) {
		try {
			final Field enumField = enumClass.getField(enumProp.name());
			
			// getting the column annotation
			final MythDatabaseColumn dbColumn = enumField.getAnnotation(MythDatabaseColumn.class);
			return dbColumn;
		} catch (Exception e) {
			logger.log(Level.SEVERE,e.getMessage(),e);
			return null;
		}
	}
}
