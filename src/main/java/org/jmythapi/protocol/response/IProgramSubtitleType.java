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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_35;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

/**
 * The subtitle type of a recording.
 * <p>
 * This interface represents the result of the function call {@link IProgramInfo#getSubtitleType()}.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IProgramSubtitleType.Flags} for a list of all available flags.
 * 
 * @since {@mythProtoVersion 35}
 * 
 * @see IProgramInfo.Props#SUBTITLE_TYPE
 * @see IProgramInfo#getSubtitleType()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
public interface IProgramSubtitleType extends IVersionable, IEnumFlagGroup<IProgramSubtitleType.Flags> {

	/**
	 * The flags of the {@link IProgramInfo.Props#SUBTITLE_TYPE} property.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythtv/programinfo.h">libmythtv/programinfo.h</a>
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/programinfo.h">libmyth/programinfo.h</a>
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/programtypes.h">libmyth/programtypes.h</a>
	 * 
	 * @since {@mythProtoVersion 35}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	public static enum Flags implements IFlag {
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		SUB_UNKNOWN(0x00),
		
		/**
		 * Closed Captioned
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		SUB_HARDHEAR(0x01),
		
		/**
		 * Subtitles Available
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		SUB_NORMAL(0x02),
		
		/**
		 * Subtitled
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		SUB_ONSCREEN(0x04),
		
		/**
		 * Deaf Signing
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="14789"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="f84369e041384c37593d")
		})
		SUB_SIGNED(0x08);
		
		private final long flag;
		Flags(long flag) {
			this.flag = flag;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(this.flag);
		}
	}
}