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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_03;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_14;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_33;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_34;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * The episode filter of a recording.
 * <p>
 * This interface represents the result of the function call {@link IProgramInfo#getRecordingDuplicationType()}.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IProgramRecordingDupInType.Flags} for a list of all available flags.
 * <p>
 * 
 * @since {@mythProtoVersion 03}
 * 
 * @see IProgramInfo.Props#DUP_IN
 * @see IProgramInfo#getRecordingDuplicationType()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
public interface IProgramRecordingDupInType extends IVersionable, IEnumFlagGroup<IProgramRecordingDupInType.Flags> {
	/**
	 * The flags of the {@link IProgramInfo.Props#DUP_IN} property.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythtv/recordingtypes.h">libmythtv/recordingtypes.h</a>
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythtv/scheduledrecording.h">libmythtv/scheduledrecording.h</a>
	 * 
	 * @see IProgramInfo.Props#DUP_IN
	 * 
	 * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public static enum Flags implements IFlag, IVersionableValue {
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUPS_IN_UNKNOWN(0x00),
		
		/**
		 * Look for duplicates in current recordings only.
		 * 
		 * @since {@mythProtoVersion 03}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUPS_IN_RECORDED(0x01),
		
		/**
		 * Look for duplicates in previous recordings only
		 * 
		 * @since {@mythProtoVersion 03}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUPS_IN_OLD_RECORDED(0x02),
		
		/**
		 * @since {@mythProtoVersion 14}
		 * @deprecated {@mythProtoVersion 33}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_14,to=PROTO_VERSION_33,
			fromInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="4450"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="39683670afae9ab2ca4c")
			}, toInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="12433"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="ee3e82e0bfd6baf162cc")
			}
		)
		DUPS_IN_BOTH(0x03),
		
		/**
		 * Look for duplicates in current and previous recordings.
		 * 
		 * @since {@mythProtoVersion 03}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUPS_IN_ALL(0x0F),
		
		/**
		 * Record new episodes only
		 * @since {@mythProtoVersion 14} 
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_14,
			fromInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="4450"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="39683670afae9ab2ca4c")
			}
		)
		DUPS_NEW_EPI(
			// 14 <= protocol < 33
			VersionablePair.valueOf(PROTO_VERSION_14, 0x04),
			// 33 <= protocol (SVN 12433, GIT ee3e82e0bfd6baf162cc)
			VersionablePair.valueOf(PROTO_VERSION_33, 0x10)
		),
		
		/**
		 * Exclude old episodes.
		 * 
		 * @since {@mythProtoVersion 33}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_33,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="12433"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="ee3e82e0bfd6baf162cc")
		})
		DUPS_EX_REPEATS(0x20),
		
		/**
		 * Exclude unidentified episodes.
		 * 
		 * @since {@mythProtoVersion 33}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_33,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="12433"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="ee3e82e0bfd6baf162cc")
		})
		DUPS_EX_GENERIC(0x40),
		
		/**
		 * Record new episode first showings.
		 * 
		 * @since {@mythProtoVersion 34}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_34,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="13005"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="1fd6892925f1ddbd09f2")
		})
		DUPS_FIRST_NEW(0x80);
		
		private VersionablePair[] values;
		
		private Flags(int value) {
			this(VersionablePair.valueOf(value));
		}
		
		private Flags(VersionablePair... values) {
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
}
