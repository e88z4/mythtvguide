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
package org.jmythapi.protocol.response;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.utils.EnumUtils;


/**
 * An interface for flags and enumeration constants whose values are protocol-version dependent.
 * <p>
 * This interface is a marker interface for {@link IFlag}- or {@link Enum}-properties, whose values 
 * have changed between different MythTv-protocol versions.<br>A flag or properties marked with this
 * interface is capable to return different property values for different protocol versions.
 * <p>
 * 
 * <h3>Usage example:</h3>
 * <h4>Defining protocol-version dependent values:</h4>
 * In the following example the enumeration property {@code TUNER_BUSY} had a value of {@code 12} for 
 * the protocol range {@code [00,19)} and a value of {@code -8} for the protocol range {@code [19,-1)}.
 * 
 * {@mythCodeExample <pre>
 * public interface IProgramRecordingStatus extends IVersionable &#123;
 *    public static enum Status implements IVersionableValue &#123;
 *      ...
 *      TUNER_BUSY(
 *         // 00 <= protocol < 19
 *         VersionablePair.valueOf(PROTO_VERSION_00, 12),
 *         // 19 <= protocol
 *         VersionablePair.valueOf(PROTO_VERSION_19, -8) 
 *      ),
 *      ...
 *    &#125;
 * &#125;
 * </pre>}
 * In the next example the value of the flag {@code FL_WATCHED} has changed in protocol version {@code 57}. 
 * {@mythCodeExample <pre>
 * public interface IProgramFlags extends IVersionable, IEnumFlagGroup<IProgramFlags.Flags> &#123;
 *    public static enum Flags implements IFlag, IVersionableValue &#123;
 *       ...
 *       FL_WATCHED(			
 *          // 31 <= protocol < 57
 *          VersionablePair.valueOf(PROTO_VERSION_31, 0x00000800),	
 *          // 57 <= protocol		
 *          VersionablePair.valueOf(PROTO_VERSION_57, 0x00000200)  
 *       ),
 *       ...
 *    &#125;
 * &#125;
 * </pre>}
 * 
 * <h4>Accessing protocol-version dependent values:</h4>
 * The following example shows how a protocol-version dependent value can be queried at runtime: 
 * {@mythCodeExample <pre>
 *   // the following will return 12 
 *   Integer value1 = IProgramRecordingStatus.Status.TUNER_BUSY.getValue(PROTO_VERSION_05);
 *   
 *   // the following will return -8
 *   Integer value2 = IProgramRecordingStatus.Status.TUNER_BUSY.getValue(PROTO_VERSION_20);
 * </pre>}
 * 
 * <h4>Using the EnumUtils class:</h4>
 * The {@link EnumUtils} class provides additional methods to work with versionable values.<br>
 * An example to query the value of an enumeration constant:
 * 
 * {@mythCodeExample <pre>
 *   // the following will return 12 
 *   Integer value1 = EnumUtils.getVersionableValue(PROTO_VERSION_05, IProgramRecordingStatus.Status.TUNER_BUSY);
 *   
 *   // the following will return -8
 *   Integer value2 = EnumUtils.getVersionableValue(PROTO_VERSION_20, IProgramRecordingStatus.Status.TUNER_BUSY);
 * </pre>}
 * 
 * An example how a value can be converted back to the proper enumeration constant:
 * 
 * {@mythCodeExample <pre>
 *    // the following returns TUNER_BUSY
 *    IProgramRecordingStatus.Status status = EnumUtils.getVersionableValueEnum(IProgramRecordingStatus.Status.class,PROTO_VERSION_19,-8);
 * </pre>}
 * 
 * @see EnumUtils#getVersionableValueEnum(Class, ProtocolVersion, Long)
 * @see EnumUtils#getVersionableValue(ProtocolVersion, IVersionableValue)
 */
public interface IVersionableValue {
	/**
	 * Gets all possible values for the given property.
	 * <p>
	 * 
	 * There are some restrictions to the content of the array:
	 * <ul>
	 * <li>If a property has only one value over time, the array only contains a single value, 
	 * e.g. {@code FL_BOOKMARK(0x00000010)}.</li>
	 * <li>If a property has multiple values over time, the array contains multiple pairs. 
	 * The first part of a value pair is the protocol version when the new value was introduced, 
	 * the second number is the property value itself. <br>
	 * E.g. {@code FL_INUSERECORDING(VersionablePair.valueOf(PROTO_VERSION_21,0x00000020),VersionablePair.valueOf(PROTO_VERSION_57,0x00100000))}.</li>
	 * </ul>
	 * 
	 * @return 
	 * 		all possible values for the given property.
	 */
	public VersionablePair[] getValues();
	
	/**
	 * Returns the actual property value for the given protocol version.
	 * 
	 * @param protoVersion
	 * 		the protocol version
	 * @return
	 * 		the property value for the given protocol version
	 */
	public Long getValue(ProtocolVersion protoVersion);
	
	public class VersionablePair {
		private ProtocolVersion protoVersion;
		private long value;
		
		private VersionablePair(ProtocolVersion protoVersion, long value) {
			this.protoVersion = protoVersion;
			this.value = value;
		}

		public ProtocolVersion getProtoVersion() {
			return protoVersion;
		}

		public long getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			final StringBuilder buff = new StringBuilder();
			buff.append(protoVersion).append(": ").append(this.value);
			return buff.toString();
		}
		
		public static VersionablePair valueOf(long value) {
			return valueOf(null,value);
		}
		
		public static VersionablePair valueOf(ProtocolVersion version, long value) {
			return new VersionablePair(version,value);
		}
	}
}
