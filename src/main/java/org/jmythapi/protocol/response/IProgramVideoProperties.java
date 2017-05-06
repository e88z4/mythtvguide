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
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_70;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

/**
 * The video properties of a recording.
 * <p>
 * This interface represents the result of the function call {@link IProgramInfo#getVideoProperties()}.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IProgramVideoProperties.Flags} for a list of all available flags.
 * 
 * @since {@mythProtoVersion 35}
 * 
 * @see IProgramInfo.Props#VIDEO_PROPERTIES
 * @see IProgramInfo#getVideoProperties()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
public interface IProgramVideoProperties extends IVersionable, IEnumFlagGroup<IProgramVideoProperties.Flags> {

	/** 
	 * The flags of the {@link IProgramInfo.Props#VIDEO_PROPERTIES} property.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="http://svn.mythtv.org/trac/browser/trunk/mythtv/libs/libmythtv/programinfo.h">libmythtv/programinfo.h</a>
	 * @see <a href="http://svn.mythtv.org/trac/browser/trunk/mythtv/libs/libmyth/programinfo.h">libmyth/programinfo.h</a>
	 * @see <a href="http://svn.mythtv.org/trac/browser/trunk/mythtv/libs/libmyth/programtypes.h">libmyth/programtypes.h</a>
	 * 
	 * @since {@mythProtoVersion 35}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
	public static enum Flags implements IFlag {
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		VID_UNKNOWN(0x00),
		
		/**
		 * HDTV.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		VID_HDTV(0x01),
		
		/**
		 * Widescreen.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		VID_WIDESCREEN(0x02),
		
		/**
		 * AVC/H.264.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15361"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="0ee58223eb09e74c4572")
		})
		VID_AVC(0x04),
		
		/**
		 * 720p.
		 * 
		 * @since {@mythProtoVersion 45}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="20067"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="c1458fcb4b29747179f9")
		})
		VID_720(0x08),
		
		/**
		 * 1080i/p.
		 * 
		 * @since {@mythProtoVersion 45}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="20067"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="c1458fcb4b29747179f9")
		})
		VID_1080(0x10),
		
		/**
		 * Damaged video.
		 * <p>
		 * The recording is perhaps watchable, but this gives a warning
		 * that there may be a serious problem with the recording.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_70,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="72d437001e74c57f64217de14c383e22e081a857")
		})
		VID_DAMAGED(0x20);
		
		private final long flag;
		Flags(long flag) {
			this.flag = flag;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(this.flag);
		}
	}
}