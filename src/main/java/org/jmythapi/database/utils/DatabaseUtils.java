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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.impl.ADatabaseRow;
import org.jmythapi.impl.ResultList;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.utils.EncodingUtils;

public class DatabaseUtils {
	/**
	 * For logging
	 */
	private static final Logger logger = Logger.getLogger(DatabaseUtils.class.getName());
	
	private static String getFullColumnName(ResultSetMetaData metaData, int columnIdx) throws SQLException {
    	final String tableName = metaData.getTableName(columnIdx);
    	final String columnName = metaData.getColumnName(columnIdx);
    	return getFullColumnName(tableName, columnName);
	}
	
	private static String getFullColumnName(MythDatabaseColumn column) {
		return getFullColumnName(column.table(),column.column());
	}
	
	private static String getFullColumnName(String tableName, String columnName) {
		return (tableName == null || tableName.length() == 0) ? columnName : tableName + "." + columnName;
	}
	
	public static Map<String,Integer> getColumnNameIndexMap(ResultSet resultSet) throws SQLException {
		// getting resultset metadata
		final ResultSetMetaData metaData = resultSet.getMetaData();
		return getColumnNameIndexMap(metaData);
	}

    public static Map<String,Integer> getColumnNameIndexMap(ResultSetMetaData metaData) throws SQLException {
        final int cols = metaData.getColumnCount();

        final HashMap<String,Integer> nameIndexMap = new HashMap<String, Integer>();
        for(int columnIdx = 1; columnIdx <= cols; columnIdx++) {
        	final String colName = metaData.getColumnName(columnIdx);
        	nameIndexMap.put(colName,Integer.valueOf(columnIdx));
        	
        	final String fullColName = getFullColumnName(metaData, columnIdx);
        	nameIndexMap.put(fullColName,Integer.valueOf(columnIdx));
        }
        return nameIndexMap;
	}
	
	public static <E extends Enum<E>> EnumMap<E,Integer> getEnumIndexMap(int dbVersion, ResultSet resultSet, Class<E> propsClass) throws SQLException {
		// getting an enum->colName map
		final EnumMap<E,MythDatabaseColumn> enumColMap = EnumUtils.getEnumColumnMap(dbVersion, propsClass);
		if(enumColMap == null) return null;
		
		final Map<String,Integer> colNameIdxMap = getColumnNameIndexMap(resultSet);
		if(colNameIdxMap == null) return null;
        
        // loop through all enums to find the corresponding column
        final EnumMap<E,Integer> enumIndexMap = new EnumMap<E,Integer>(propsClass);
        for(Entry<E,MythDatabaseColumn> entry : enumColMap.entrySet()) {   	
        	final String fullColumnName = getFullColumnName(entry.getValue());
        	final String colName = entry.getValue().column();
        	Integer colIdx = null;
        	
        	// try to find the column index via the column alias
        	try {
        		colIdx = Integer.valueOf(resultSet.findColumn(colName));
        	} catch (SQLException e) {
        		// col not found by alias
        	}

        	// try to find the column index via the real column names
        	if(colIdx == null) {
	        	if(colNameIdxMap.containsKey(fullColumnName)) {
	        		colIdx = colNameIdxMap.get(fullColumnName);
	        	} else if(colNameIdxMap.containsKey(colName)) {
	        		colIdx = colNameIdxMap.get(colName);
	        	}
        	}
        	
        	if(colIdx != null) {
        		enumIndexMap.put(entry.getKey(),colIdx);
        	} else {
        		logger.warning(String.format(
        			"Unable to determine the column index for property %s with annotation %s.",
        			entry.getKey(),
        			entry.getValue()
        		));
        	}
        }
		return enumIndexMap;
	}
	
	public static <E extends Enum<E>, R extends ADatabaseRow<E>> List<R> getDataRows(
		ProtocolVersion protoVersion, int dbVersion, ResultSet resultSet, Class<R> rowClass, Class<E> propsClass
	) throws SQLException {
		final List<R> rows = new ResultList<R>();

		// determine all possible properties
		final EnumSet<E> enumSet = EnumUtils.getEnums(propsClass,dbVersion);
		if(enumSet == null) return null;				
		
		// get the mapping between the enum and the column idx
		final EnumMap<E,Integer> propIdxMap = getEnumIndexMap(dbVersion, resultSet, propsClass);
		if(propIdxMap == null) return rows;
		
		while (resultSet.next())  {
			final List<String> valueList = new ArrayList<String>(enumSet.size());
			
			// collect values
			for(E prop : enumSet) {
				final Integer colIdx = propIdxMap.get(prop);
				if(colIdx == null) {
					valueList.add(null);
					continue;
				}
				
				// fetching the value
				String propValueStr = null;
				final Object propValueObj = resultSet.getObject(colIdx);				
				if(propValueObj != null) {
					final Class<?> propValueClass = propValueObj.getClass();
					// converting the value to a string
					
					if(propValueObj instanceof Time) {
						propValueStr = EncodingUtils.formatTime((Time)propValueObj,false /* no tz conversion required */);
					} else if (propValueObj instanceof java.sql.Date) {
						propValueStr = EncodingUtils.formatDay((java.sql.Date)propValueObj,false /* no tz conversion required */);
					} else if (propValueObj instanceof Date) {
						propValueStr = EncodingUtils.formatDateTime((Date)propValueObj,false /* no tz conversion required */);
					} else if (propValueObj instanceof byte[]) {
						propValueStr = new String((byte[])propValueObj);
					} else if (propValueObj instanceof Blob) {
						try {
							final InputStream input = ((Blob)propValueObj).getBinaryStream();
							final ByteArrayOutputStream bout = new ByteArrayOutputStream();
							int c = -1;
							while((c = input.read()) != -1) {
								bout.write(c);
							}
							propValueStr = bout.toString();
						} catch (IOException e) {
							logger.warning("Unable to read blob data: " + e.getMessage());
						}
					} else if(propValueObj instanceof Number || propValueObj instanceof String){
						propValueStr = propValueObj.toString();
					} else {
						propValueStr = propValueObj.toString();
						logger.warning(String.format(
							"Unexpected datatype '%s' of property '%s' at index %d. Using string representation of value: %s",
							propValueClass, prop, colIdx, propValueStr
						));			
					}
				}
				valueList.add(propValueStr);
			}			
			
			// build the result class
			final R dataObject = createDataObject(protoVersion, dbVersion, rowClass, valueList);
			rows.add(dataObject);
		}
		return rows;
	}
	
	public static <E extends Enum<E>, R extends ADatabaseRow<E>> R createDataObject(
		ProtocolVersion protoVersion, int dbVersion, Class<R> rowClass, List<String> valueList
	) {	
		// the constructor of the msg 
		try {
			final Constructor<R> msgClassConstructor = rowClass.getConstructor(new Class[] {
				ProtocolVersion.class,
				int.class,
				List.class
			});
	
			// creating a new message instance
			final R rowObject = msgClassConstructor.newInstance(new Object[] {
				protoVersion, dbVersion, valueList
			});        
			return rowObject;
		} catch (Exception e) {
			final String errorMsg = String.format(
				"Unable to create a new instance of class '%s'.",
				rowClass==null?"null":rowClass.getName()
			);
			logger.severe(errorMsg);
			throw new RuntimeException(errorMsg);
		}
	}
}
