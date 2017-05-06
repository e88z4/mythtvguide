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
 * The audio properties of a recording.
 * <p>
 * This interface represents the result of the function call {@link IProgramInfo#getAudioProperties()}.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IProgramAudioProperties.Flags} for a list of available flags.
 * 
 * @since {@mythProtoVersion 35}
 * 
 * @see IProgramInfo.Props#AUDIO_PROPERTIES
 * @see IProgramInfo#getAudioProperties()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
public interface IProgramAudioProperties extends IVersionable, IEnumFlagGroup<IProgramAudioProperties.Flags> {

	/**
	 * The flags of the {@link IProgramInfo.Props#AUDIO_PROPERTIES} property.
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
		AUD_UNKNOWN(0x00),
		
		/**
		 * Stereo
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		AUD_STEREO(0x01),
		
		/**
		 * Mono
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		AUD_MONO(0x02),
		
		/**
		 * Surround Sound
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		AUD_SURROUND(0x04),
		
		/**
		 * Dolby Sound
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_35)
		AUD_DOLBY(0x08),
		
		/**
		 * Audio for Hearing Impaired
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15361"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="0ee58223eb09e74c4572")
		})
		AUD_HARDHEAR(0x10),
		
		/**
		 * Audio for Visually Impaired
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15361"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="0ee58223eb09e74c4572")
		})
		AUD_VISUALIMPAIR(0x20);
		
		private final long flag;
		Flags(long flag) {
			this.flag = flag;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(this.flag);
		}
	}
}