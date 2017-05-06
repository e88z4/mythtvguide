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

import java.lang.reflect.Method;
import java.net.URI;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IVersionableValue;
import org.jmythapi.protocol.response.impl.AGroup;
import org.jmythapi.protocol.utils.EnumUtils;

public class EncodingUtils {
	public static final String TIMEZONE_UTC = "UTC";
	
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String TIME_PATTERN = "HH:mm:ss";
	
	public static final String NULL_DAY = "0000-00-00";
	public static final String NULL_TIME = "00:00:00";
	public static final String NULL_DATE_TIME = NULL_DAY + " " + NULL_TIME;
	
	/**
	 * Date pattern used to parse dates.
	 * 
	 * @see EncodingUtils#parseDate
	 */
	public static enum DateTimePattern {
		PROTOCOL_DATE_FORMAT("yyyy-MM-dd'T'HH:mm:ss"),
		SHORT("yyyy-MM-dd HH:mm"),
		MEDIUM("yyyy-MM-dd HH:mm:ss"),
		LONG("yyyy-MM-dd HH:mm:ss.S");
		
		private String pattern;
		DateTimePattern(String pattern) {
			this.pattern = pattern;
		}
		
		public String getPattern() {
			return this.pattern;
		}
	}
	
	/**
	 * For logging
	 */
	private static Logger logger = Logger.getLogger(EncodingUtils.class.getName());
	
	/**
	 * <code>valueOf</code> methods that are supported by {@link #decodeString}
	 */
	public static final Class<?>[][] VALUE_OF_ARGS = new Class[][] {
		{ProtocolVersion.class, String.class},
		{ProtocolVersion.class, int.class, String.class},
		{String.class},
		{Object.class}
	};
	
	/**
	 * @see #parseDay
	 */
	public static String formatDay(Date day, boolean isUTC) {
		if(day==null) return NULL_DAY;
		
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
		if(isUTC) {
			 formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
		}
		return formatter.format(day);		
	}
	
	/**
	 * @see #parseDay
	 */
	public static java.sql.Date parseDay(String dayString, boolean isUTC) {
		if(dayString == null || dayString.equals(NULL_DAY)) return null;
		
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
			if(isUTC) {
				 formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
			}
			
			return new java.sql.Date(formatter.parse(dayString).getTime());
		} catch(Throwable e) {
			logger.log(Level.WARNING, String.format(
				"Unable to parse the day string '%s' using pattern '%s'.",
				dayString, DATE_PATTERN
			),e);
			return null;
		}
	}
	
	/**
	 * @see #parseTime
	 */
	public static String formatTime(Date time, boolean isUTC) {
		if(time==null) return NULL_TIME;
		
		final SimpleDateFormat formatter = new SimpleDateFormat(TIME_PATTERN);
		if(isUTC) {
			 formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
		}		
		return formatter.format(time);		
	}
	
	/**
	 * @see #formatTime
	 */
	public static Time parseTime(String timeString, boolean isUTC) {
		if(timeString == null || timeString.equals(NULL_TIME)) return null;
		
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat(TIME_PATTERN);			
			if(isUTC) {
				formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
			}			
			return new Time(formatter.parse(timeString).getTime());
		} catch(Throwable e) {
			logger.log(Level.WARNING, String.format(
				"Unable to parse the time string '%s' using pattern '%s'.",
				timeString, TIME_PATTERN
			),e);
			return null;
		}
	}
	
	public static Date aggregateDateTime(java.sql.Date day, Time time, boolean isUTC) {
		if(day == null && time == null) return null;
		else if(day == null && time != null) return time;
		else if(day != null && time == null) return day;
		
		return parseDate(formatDay(day,isUTC) + " " + formatTime(time,isUTC), isUTC);		
	}
	
	/**
	 * Formats the given date as string.
	 * <p>
	 * The returned string is in the format {@link DateTimePattern#PROTOCOL_DATE_FORMAT}
	 * 
	 * @param date
	 * 		the date to format
	 * @param isUTC 
	 * 		if the date is in UTC format
	 * @return 
	 * 		the output string.
	 */
	public static String formatDateTime(Date date, boolean isUTC) {
		final DateFormat formatter = new SimpleDateFormat(DateTimePattern.PROTOCOL_DATE_FORMAT.getPattern());
		if(isUTC) {
			formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
		}
		return formatter.format(date);
	}	
	
	public static String formatDateTimeToUnixTimestamp(Date date) {
		if(date == null) return "0";
		return Long.toString(date.getTime() / 1000);
	}
	
	/**
	 * Parses the given string into a date object.
	 * <p>
	 * The date string can be in one of the following formats
	 * <ul>
	 * 	<li>{@code 0000-00-00}: will be converted to {@code null}</li>
	 *  <li>{@code \\d+}: will be interpreted as unix timestamp (seconds since January 1, 1970)</li>
	 *  <li>In the format {@link DateTimePattern#PROTOCOL_DATE_FORMAT}</li>
	 * </ul>
	 * 
	 * @param value 
	 * 		the date string. 
	 * @param isUTC
	 * 		if the date is in UTC format
	 * @return 
	 * 		the parsed date or {@code null} if the date is invalid or can not be parsed
	 */
	public static Date parseDate(String value, boolean isUTC) {
		if(value == null || value.length() == 0 || value.startsWith(NULL_DAY)) {
			return null;
		} else if(!value.matches("\\d+")) {	
			String pattern = null;
			try {
				// determine the pattern to use
				if(value.contains("T")) {
					pattern = DateTimePattern.PROTOCOL_DATE_FORMAT.getPattern();
				} else {
					for(DateTimePattern dateTimePattern : DateTimePattern.values()) {
						if(dateTimePattern.getPattern().length() == value.length()) {
							pattern = dateTimePattern.getPattern();
							break;
						}
					}
				}
				
				// parse the date
				final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				if(isUTC) {
					 sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
				}
				return sdf.parse(value);
			} catch (ParseException e) {
				logger.log(Level.WARNING, String.format(
					"Unable to parse the date string '%s' using pattern '%s'.",
					value, pattern
				),e);
				return null;
			}
		} else {
			final GregorianCalendar cal = new GregorianCalendar();
			if(isUTC) {
				cal.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
			}
			cal.setTimeInMillis(Long.valueOf(value).longValue() * 1000);
			
			return cal.getTime();
		}
	}
	
	/**
	 * Converts a 64 bit long value into two signed 32 bit integer values.
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/blob/1b1d0a9cd02b2be79e0d91a1b26c9b4daaaf13c2/mythtv/libs/libmythbase/decodeencode.cpp">decodeencode.cpp</a>
	 * @deprecated {@mythProtoVersion 66}
	 */
	public static String[] encodeLong(long value) {
		final String value1 = Integer.toString((int) (value >>> 32),10);
	    final String value2 = Integer.toString((int) (value & 0xffffffffL),10);
	    return new String[]{value1,value2};
	}
	
	/**
	 * Converts two signed 32 bit integer values into a 64 bit long value.
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/blob/1b1d0a9cd02b2be79e0d91a1b26c9b4daaaf13c2/mythtv/libs/libmythbase/decodeencode.cpp">decodeencode.cpp</a>
	 * @deprecated {@mythProtoVersion 66}
	 */
	public static long decodeLong(String value1, String value2) {
		final int int1 = Long.decode(value1).intValue();
		final int int2 = Long.decode(value2).intValue();
		return decodeLong(new int[]{int1,int2});
	}
	
	/**
	 * Converts two sigend 32 bit integer values into a 64 bit long value.
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/blob/1b1d0a9cd02b2be79e0d91a1b26c9b4daaaf13c2/mythtv/libs/libmythbase/decodeencode.cpp">decodeencode.cpp</a>
	 * @deprecated {@mythProtoVersion 66}
	 */	
	public static long decodeLong(int[] values) {
	    int l1 = values[0];
	    int l2 = values[1];		
		
		return (((long)(l2)) & 0xffffffffL) | (((long)(l1)) << 32);
	}
	
	/**
	 * Converts the given value-string into the specified class. 
	 * <p>
	 * The conversion is done by calling one of the following static functions (see: {@link #VALUE_OF_ARGS}):
	 * <ul>
	 * 	<li>{@code Class.valueOf(int protoVersion, String value)}</li>
	 *  <li>{@code Class.valueOf(String value)}</li>
	 *  <li>{@code Class.valueOf(Object value)}</li>
	 * </ul>
	 * <p>
	 * Usage example:
	 * <pre>
	 *  ProgramInfo programInfo = ....;
	 *  ProgramFlags programFlags = EncodingUtils.decodeString(
	 *      // the target class to convert the string to 
	 *      ProgramFlags.class,
	 *      // the protocol version
	 *      this.protoVersion,
	 *      // the string value of the given argument
	 *      programInfo.getResponseArgument(ProgramInfo.Props.PROGRAM_FLAGS)
	 *  );
	 * </pre>
	 * 
	 * @param <E> 
	 * 		the target type
	 * @param clazz 
	 * 		the target class
	 * @param protoVersion 
	 * 		the protocol- or database-version
	 * @param value 
	 * 		the value as string
	 * @return 
	 * 		the converted value. If the given value is {@code null} then {@code null} is returned
	 */
	public static <E> E decodeString(Class<E> clazz, ProtocolVersion protoVersion, String value) {
		return decodeString(clazz, protoVersion, -1, false, value, null);
	}
	
	public static <E> E decodeString(Class<E> clazz, ProtocolVersion protoVersion, int dbVersion, boolean isUTC, String value) {	
		return decodeString(clazz, protoVersion, dbVersion, isUTC, value, null);
	}
	
	/**
	 * Converts the given value-string into the specified class. 
	 * <p>
	 * The conversion is done by calling one of the following static functions (see: {@link #VALUE_OF_ARGS}):
	 * <ul>
	 * 	<li>{@code Class.valueOf(int protoVersion, String value)}</li>
	 *  <li>{@code Class.valueOf(String value)}</li>
	 *  <li>{@code Class.valueOf(Object value)}</li>
	 * </ul>
	 * Additionally the following simple types are supported:
	 * <ul>
	 * 	<li>{@code String}</li>
	 *  <li>{@code Boolean}</li>
	 *  <li>{@code Date}</li>
	 *  <li>{@code Time}</li>
	 *  <li>{@code Enum}</li>
	 * </ul>
	 * 
	 * <p>
	 * Usage example:
	 * <pre>
	 *  ProgramInfo programInfo = ....;
	 *  ProgramFlags programFlags = EncodingUtils.decodeString(
	 *      // the target class to convert the string to 
	 *      ProgramFlags.class,
	 *      // the protocol version
	 *      this.protoVersion,
	 *      // the string value of the given argument
	 *      programInfo.getResponseArgument(ProgramInfo.Props.PROGRAM_FLAGS)
	 *  );
	 * </pre>
	 * 
	 * @param <E> 
	 * 		the target type
	 * @param clazz 
	 * 		the target class
	 * @param protoVersion 
	 * 		the protocol- or database-version
	 * @param value 
	 * 		the value as string. 
	 * @return 
	 * 		If the given value is {@code null} then the value of {@code onEmptyValue} is returned 
	 */		
	@SuppressWarnings("unchecked")
	public static <E> E decodeString(Class<E> clazz, ProtocolVersion protoVersion, int dbVersion, boolean isUTC, String value, E onEmptyValue) {
		if (value == null) return null;
		else if (value.trim().length() == 0) return onEmptyValue;
		
		// STRING
		if(String.class.isAssignableFrom(clazz)) {
			return (E) value;
		} 
		
		// SQL-TIME
		else if(Time.class.isAssignableFrom(clazz)) {
			return (E) parseTime(value,isUTC);
		}
		
		// SQL-DATE
		else if(java.sql.Date.class.isAssignableFrom(clazz)) {
			return (E) parseDay(value,isUTC);
		}		
		
		// DATE
		else if(Date.class.isAssignableFrom(clazz)) {
			return (E) parseDate(value,isUTC);
		} 
		
		// BOOLEAN (as number)
		else if (Boolean.class.isAssignableFrom(clazz) && value.matches("^\\d+$")) {
			final Integer intValue = Integer.valueOf(value);
			if(intValue > 1) {
				logger.warning(String.format(
					"Unexpected value %d while decoding a String to a Boolean.",
					intValue
				));
			}
			return (E) Boolean.valueOf(intValue.intValue() > 0);
		} 
		
		// BOOLEAN (as string ok or OK)
		else if (Boolean.class.isAssignableFrom(clazz) && value.equalsIgnoreCase("ok")) {
			return (E) Boolean.TRUE;
		}
		
		// ENUM
		else if (Enum.class.isAssignableFrom(clazz) && value.matches("^\\d+$")) {
			final Long valueLong = Long.valueOf(value);
			if (IVersionableValue.class.isAssignableFrom(clazz)) {
				return (E) EnumUtils.getVersionableValueEnum((Class)clazz,protoVersion, valueLong);
			} else {
				return (E) EnumUtils.getEnum((Class)clazz,protoVersion, valueLong.intValue());
			}		
		} 
		
		// URI
		if(URI.class.isAssignableFrom(clazz)) {
			return (E) URI.create(value);
		}
		
		// OTHER TYPE
		else {
			for(Class<?>[] parameterTypes : VALUE_OF_ARGS) {
				try {
					// get parser-method
					Method valueOf = clazz.getMethod("valueOf", parameterTypes);
					
					// get concrete value
					final Object[] args = new Object[parameterTypes.length];
					if (parameterTypes.length == 2 && parameterTypes[0].equals(ProtocolVersion.class)) {
						args[0] = protoVersion;
						args[1] = value;
					} else if (parameterTypes.length == 3 && parameterTypes[0].equals(ProtocolVersion.class) && parameterTypes[1].equals(int.class)) {
						args[0] = protoVersion;
						args[1] = Integer.valueOf(dbVersion);
						args[2] = value;
					} else {
						args[0] = value;
					}
						
					return (E) valueOf.invoke(null, args);
				} catch (NoSuchMethodException e) {
					// ignore this
				} catch (Exception e) {
					logger.log(Level.WARNING,String.format(
						"Unexpected %s while decoding the string '%s' using type '%s'.",
						e.getClass().getSimpleName(), value, clazz.getName()						
					),e);
					return null;
				}
			}
		}
				
		logger.warning(String.format(
			"Unable to the string '%s' using type '%s'. No proper conversion method found.",
			value, clazz.getName()						
		));		
		return null;
	}
	
	public static Boolean decodeBoolean(String value) {
		return decodeBoolean(value, null);
	}
	
	public static Boolean decodeBoolean(String value, Boolean onEmptyValue) {
		if (value == null) return null;
		else if (value.trim().length() == 0) return onEmptyValue;
		
		if(value.matches("^\\d+$")) {
			final Integer intValue = Integer.valueOf(value);
			if(intValue > 1) {
				logger.warning(String.format(
					"Unexpected value %d while decoding a String to a Boolean.",
					intValue
				));
			}
			return Boolean.valueOf(intValue.intValue() > 0);
		} 
		
		// BOOLEAN (as string ok or OK)
		else if (value.equalsIgnoreCase("ok")) {
			return Boolean.TRUE;
		}		
		
		return Boolean.FALSE;
	}
	
	public static <S,T> String encodeObject(
		Class<S> sourceType, ProtocolVersion versionNr, S value, Class<T> targetType, String onNullValue
	) {
		return encodeObject(sourceType, versionNr, -1, false, value, targetType, onNullValue);
	}
		
	public static <S,T> String encodeObject(
		Class<S> sourceType, ProtocolVersion versionNr, int dbVersion, boolean isUTC, S value, Class<T> targetType, String onNullValue
	) {
		if(value == null) return onNullValue;
		
		// STRING
		if(String.class.isAssignableFrom(sourceType)) {
			return (String) value;
		}
		
		// DATE
		else if(Date.class.isAssignableFrom(sourceType)) {			
			// SQL TIME
			if (Time.class.isAssignableFrom(sourceType)) {
				return formatTime((Date)value,isUTC);
			} 
			
			// SQL DATE
			else if(java.sql.Date.class.isAssignableFrom(sourceType)) {
				return formatDay((Date)value,isUTC);			
			} 
			
			// DATE + TIME
			else {
				return formatDateTime((Date)value,isUTC);
			}
		} 
		
		// BOOLEAN
		else if (Boolean.class.isAssignableFrom(sourceType)) {
			// INTEGER
			if(targetType != null && Integer.class.isAssignableFrom(targetType)) {
				if(value == null) return "0";
				return ((Boolean)value).booleanValue()?"1":"0";
			} 
			
			// BOOLEAN
			else {
				if(value == null) return "false";
				return ((Boolean)value).booleanValue()?"true":"false";
			}
		} 
		
		// AGROUP
		else if (AGroup.class.isAssignableFrom(sourceType)) {
			return Integer.toString(((AGroup<?>)value).intValue());
		}

//		// ENUM
//		else if (Enum.class.isAssignableFrom(clazz) && value.matches("^\\d+$")) {
//			final Integer valueInt = Integer.valueOf(value);
//			if (IVersionableValue.class.isAssignableFrom(clazz)) {
//				return (E) EnumUtils.getVersionableValueEnum((Class)clazz,versionNr, valueInt);
//			} else {
//				return (E) EnumUtils.getEnum((Class)clazz,versionNr, valueInt);
//			}		
//		}
		
		// OTHER TYPE
		else {
			try {
				// get the toString method
				Method toStrMethod = sourceType.getMethod("toString", sourceType);
				return (String) toStrMethod.invoke(null,value);
			} catch (NoSuchMethodException e) {
				// ignore this				
			} catch (Exception e) {
				logger.log(Level.WARNING,String.format(
					"Unexpected %s while encoding an object of type '%s' to a string.",
					e.getClass().getSimpleName(), sourceType.getName()						
				),e);
				return null;
			}
			
			// fallback to the normal toString method
			return value.toString();
		}		
	}
	
	public static int getMinutesAfterMidnight(Date start) {
		if(start == null) return -1;
		
		final Calendar midnight = Calendar.getInstance();
		midnight.setTime(start);
		midnight.set(Calendar.HOUR_OF_DAY, midnight.getActualMinimum(Calendar.HOUR_OF_DAY));
		midnight.set(Calendar.MINUTE, midnight.getActualMinimum(Calendar.MINUTE));
		midnight.set(Calendar.SECOND, midnight.getActualMinimum(Calendar.SECOND));
		midnight.set(Calendar.MILLISECOND, midnight.getActualMinimum(Calendar.MILLISECOND));
		
		return EncodingUtils.getMinutesDiff(midnight.getTime(),start);
	}
	
	public static int getSecondsDiff(long start, long end) {
		return (int) ((end - start) / 1000);
	}
	
	public static int getSecondsDiff(Date start, Date end) {
		if(start==null || end==null) return -1;
		return getSecondsDiff(start.getTime(),end.getTime());
	}	
	
	public static int getMinutesDiff(long start, long end) {
		return getSecondsDiff(start, end) / 60;
	}	
	
	public static int getMinutesDiff(Date start, Date end) {
		if(start==null || end==null) return -1;
		return getMinutesDiff(start.getTime(),end.getTime());
	}
	
	public static int getHoursDiff(Date start, Date end) {
		final int minDiff = getMinutesDiff(start, end);
		if(minDiff == -1) return -1;
		return minDiff / 60;
	}
	
	public static int getDaysDiff(Date start, Date end) {
		final int hoursDiff = getHoursDiff(start, end);
		if(hoursDiff == -1) return -1;
		return hoursDiff / 24;
	}
	
	public static int[] getHourMinutesLength(int duration) {
		int hours = duration / 60;
		int minutes = duration % 60;
		return new int[]{hours,minutes};
	}
	
	public static String getFormattedFileSize(long bytes) {
		return getFormattedFileSize(Locale.getDefault(), bytes);
	}
	
	public static String getFormattedFileSize(Locale locale, long bytes) {
    	final long gb = 1024 * 1024 * 1024;
    	final long mb = 1024 * 1024;
    	final long kb = 1024;
    	
    	String value;
    	if (bytes / gb > 0) {
    		value = String.format(locale,"%.2f GB", (float)bytes/(float)gb);
    	} else if (bytes / mb > 0) {
    		value = String.format(locale,"%.2f MB", (float)bytes/(float)mb);
    	} else if (bytes / kb > 0) {
    		value = String.format(locale,"%.2f KB", (float)bytes/(float)kb);
    	} else {
    		value = bytes + " bytes";
    	}
    	
    	return value;
	}
	
	public static String getFormattedTitle(String title, String subTitle) {
		final StringBuilder fullTitle = new StringBuilder();
		fullTitle.append(title);
		
		if(subTitle != null && subTitle.trim().length() > 0) {
			fullTitle.append(" - ");
			fullTitle.append(subTitle);
		}
		return fullTitle.toString();		
	}
	
	/**
	 * Returns e.g. 
	 * <code>4008_20141021052500</code>
	 * 
	 * @param id
	 * 		the channel id
	 * @param date
	 * 		the recording start date
	 * @return
	 * 		the generated id
	 */
	public static String generateId(Integer id, Date date) {
		return String.format(
			"%d_%2$tY%2$tm%2$td%2$tH%2$tM%2$tS",
			id,
			date
		);		
	}
	
	/**
	 * Splits a id-string into the channel-id and start-date part.
	 * 
	 * @param idString
	 * 		the unique- program- or -recording-id
	 * @return
	 * 		an array containing the channel-id and start-date.
	 */
	public static Object[] splitId(String idString,boolean isUTC) {
		if(idString == null || idString.length() == 0) return null;
		
		final int idx = idString.indexOf('_');
		final Integer id = Integer.valueOf(idString.substring(0,idx));
		final Date date = EncodingUtils.parseDate(idString.substring(idx+1),isUTC);
		
		return new Object[]{id,date};
	}

	/**
	 * This class should not be instantiated.
	 */
	private EncodingUtils() {
	}
}
