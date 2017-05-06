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
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_04;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_09;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_12;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_14;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_33;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

/**
 * The duplication mode of a recording.
 * <p>
 * This interface represents the result to the function call {@link IProgramInfo#getRecordingDuplicationMethod()}.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IProgramRecordingDupMethodType.Flags} for a list of all available flags.
 * 
 * @see IProgramInfo.Props#DUP_METHOD
 * @see IProgramInfo#getRecordingDuplicationMethod()
 * 
 * @since {@mythProtoVersion 03}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
public interface IProgramRecordingDupMethodType extends IVersionable, IEnumFlagGroup<IProgramRecordingDupMethodType.Flags> {
	
	/**
	 * The flags of the {@link IProgramInfo.Props#DUP_METHOD} property.
	 * 
     * {@mythProtoVersionMatrix}
     * 
     * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythtv/recordingtypes.h">libmythtv/recordingtypes.h</a>
     * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythtv/scheduledrecording.h">libmythtv/scheduledrecording.h</a>
     * 
     * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public static enum Flags implements IFlag {
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUP_CHECK_UNKNOWN(0x00),
		
		/**
		 * No duplicates check.
		 * <p>
		 * Don't match duplicates.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUP_CHECK_NONE(0x01),
		
		/**
		 * Subtitle.
		 * <p>
		 * Match duplicates using subtitle.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUP_CHECK_SUB(0x02),
		
		/**
		 * Description.
		 * <p>
		 * Match duplicates using description.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUP_CHECK_DESC(0x04),
		
		/**
		 * Subtitle and description.
		 * <p>
		 * Match duplicates using subtitle & description
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
		DUP_CHECK_SUB_DESC(0x06),

		/**
		 * Matches duplicates even if empty.
		 * 
		 * @deprecated {@mythProtoVersion 04}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03,to=PROTO_VERSION_04,
			toInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="3170"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="58d2f6ee7ee9455e97a9")
			}
		)
		DUP_ALLOW_EMPTY(0x10),

		/**
		 * @deprecated {@mythProtoVersion 04}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_03,to=PROTO_VERSION_04,
			toInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="3170"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="58d2f6ee7ee9455e97a9")
			}
		)
		DUP_EMPTY_SUB_DESC(0x16),		
		
		/**
		 * Program ID.
		 * <p>
		 * Using the programid to check for duplicated programs.
		 * 
		 * @since {@mythProtoVersion 09}
		 * @deprecated {@mythProtoVersion 09}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_09,to=PROTO_VERSION_09,
			fromInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="3623"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="1fb379b36c98736aa93e")
			},
			toInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="3630"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="a78f200d95a39a8a3a72")
			}
		)
		DUP_CHECK_ID_ONLY(0x08),
		
		
		/**
		 * New episodes only.
		 * <p>
		 * 
		 * @since {@mythProtoVersion 12}
		 * @deprecated {@mythProtoVersion 14}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_12 ,to=PROTO_VERSION_14,
			toInfo={
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="4450"),
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="39683670afae9ab2ca4c")
			}
		)
		DUP_CHECK_NEW_EPI(0x08),
		
		/**
		 * Subtitle then description.
		 * <p>
		 * Match duplicates using subtitle then description.
		 * <p>
		 * This uses the subtitle for the dup match if there 
		 * is a subtitle. If the subtitle is empty then the
		 * description is used to do dup matching.
		 * 
		 * @since {@mythProtoVersion 33}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_33,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="12808"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="b37cf5283c3ab4770815")
		})
		DUP_CHECK_SUB_THEN_DESC(0x08);
		
		private final long flag;
		Flags(long flag) {
			this.flag = flag;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(this.flag);
		}
	}
}
