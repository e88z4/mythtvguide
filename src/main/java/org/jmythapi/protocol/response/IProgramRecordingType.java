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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1061;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1302;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1309;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1310;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_02;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_07;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_15;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_74;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_77;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.utils.EnumUtils;

public interface IProgramRecordingType extends IEnumGroup<IProgramRecordingType.Type>, IVersionable {

	/**
	 * The types of the {@link IProgramInfo.Props#REC_TYPE} property.
	 * <p>
	 * A response example:
	 * <br>
	 * {@mythResponseExample
	 * 		For Live-TV recording:
	 * 		<pre>0=> &#123;NOT_RECORDING&#125;</pre>
	 * }
	 * <p>
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Type implements IVersionableValue {
		/**
		 * Not Recording.
		 * <p>
		 * Don't record this program.
		 * <p>
		 * <i>Note:</i> This is also the status of a currently running Live-TV recording.
		 */
		NOT_RECORDING(0),
		
		/**
		 * Single Record.
		 * <p> 
		 * Record only this showing.
		 */
		SINGLE_RECORD(1),
		
		/**
		 * Record Daily.
		 * 
		 * @since {@mythProtoVersion 77}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_77,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="9497ba1b63a5a6a91b06dbb85beea37444ef3ccc")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1309)
		DAILY_RECORD(2),
		
		/**
		 * Record Timeslot Daily.
		 * <p>
		 * Record this program in this timeslot every day.
		 * 
		 * @deprecated {@mythProtoVersion 77}, use {@link #ALL_RECORD} with the 
		 * 		{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_CHANNEL channel} and 
		 * 		{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_TIME time} filter.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_77,toInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="9497ba1b63a5a6a91b06dbb85beea37444ef3ccc")
		})
		@MythDatabaseVersionAnnotation(to=DB_VERSION_1309)
		TIMESLOT_RECORD(2),
		
		/**
		 * Channel Record.
		 * <p>
		 * Record at any time one the current channel.
		 * 
		 * @since {@mythProtoVersion 00}
		 * @deprecated {@mythProtoVersion 77}, use {@link #ALL_RECORD} instead with the 
		 * 		{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_CHANNEL channel} filter.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_77,toInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="9497ba1b63a5a6a91b06dbb85beea37444ef3ccc")
		})
		@MythDatabaseVersionAnnotation(to=DB_VERSION_1310)
		CHANNEL_RECORD(3),
		
		/**
		 * Record All.
		 * <p>
		 * Record at any time on any channel.
		 */
		ALL_RECORD(4),
		
		/**
		 * Record Timeslot Weekly.
		 * <p>
		 * Record this program in this timeslot every week.
		 * 
		 * @deprecated {@mythProtoVersion 77}, use {@value #ALL_RECORD} with the 
		 * 		{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_CHANNEL channel} and 
		 * 		{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_DAY_AND_TIME day-and-time} filter.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_77,toInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="9497ba1b63a5a6a91b06dbb85beea37444ef3ccc")
		})
		@MythDatabaseVersionAnnotation(to=DB_VERSION_1309)
		WEEKSLOT_RECORD(5),
		
		/**
		 * Record Weekly.
		 * <p>
		 * 
		 * @since {@mythProtoVersion 77}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_77,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="9497ba1b63a5a6a91b06dbb85beea37444ef3ccc")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1309)
		WEEKLY_RECORD(5),
		
		/**
		 * Find One.
		 * <p>
		 * Find and record one showing of this title.
		 * 
		 * @since {@mythProtoVersion 02}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_02)
		FIND_ONE_RECORD(6),
		
		
		/**
		 * Override Recording.
		 * <p>
		 * Recording this showing with override options.
		 * 
		 * @since {@mythProtoVersion 07}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_07)
		OVERRIDE_RECORD(7),
		
		/**
		 * Not Recording.
		 * <p>
		 * Not recording this showing.
		 * 
		 * @since {@mythProtoVersion 07}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_07)
		DONT_RECORD(8),
		
		/**
		 * Find Daily.
		 * <p>
		 * Find and record one showing of this title every day.
		 * 
		 * @since {@mythProtoVersion 15}
		 * @deprecated {@mythProtoVersion 77}, use  {@link #TIMESLOT_RECORD} instead
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="5090"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="d2a0c908019409d8721a")
		},to=PROTO_VERSION_77,toInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="a1f979393d4897d91b338581a14a2e245d76fa16")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1061,to=DB_VERSION_1309)
		FIND_DAILY_RECORD(9),
		
		/**
		 * Find Weekly.
		 * <p>
		 * Find and record one showing of this title each week.
		 * 
		 * @since {@mythProtoVersion 15}
		 * @deprecated {@mythProtoVersion 77}, use {@link #WEEKSLOT_RECORD} instead
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="5090"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="d2a0c908019409d8721a")
		},to=PROTO_VERSION_77,toInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="a1f979393d4897d91b338581a14a2e245d76fa16")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1061,to=DB_VERSION_1309)
		FIND_WEEKLY_RECORD(10),
		
		/**
		 * Template Recording.
		 * <p>
		 * Recording rule templates are used to initialize new recording rules.<br>
		 * The "Default" template replaces several, individual settings previously available in the Setup wizard.
		 * 
		 * @since {@mythProtoVersion 74}
		 */
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1302)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_74,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="71c65ba67614c751ea149a1960e0db60437591af")
		})
		TEMPLATE_RECORD(11);
		
		private VersionablePair[] values;
		
		private Type(int value) {
			this(VersionablePair.valueOf(value));
		}
		
		private Type(VersionablePair... values) {
			this.values = values;
		}
		
		public VersionablePair[] getValues() {
			return this.values;
		}
		
		public Long getFlagValue() {
			return this.getValue(PROTO_VERSION_LATEST);
		}		
		
		public Long getValue(ProtocolVersion protoVersion) {
			return EnumUtils.getVersionableValue(protoVersion, this); 
		}
	}

	public int getTypeValue();
	
	public Type getType();
}
