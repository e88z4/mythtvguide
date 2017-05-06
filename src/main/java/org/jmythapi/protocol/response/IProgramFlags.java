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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_21;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_23;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_27;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_31;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_35;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_53;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_57;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.response.impl.ProgramInfo;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * The flags of a recording.
 * <p>
 * This interface represents the result of the function call {@link IProgramInfo#getProgramFlags()}.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IProgramFlags.Flags} for a list of all available flags.
 * 
 * <h4>Usage example:</h4>
 * 
 * This example shows how a list of recordings can be filtered by program flags.
 * 
 * {@mythCodeExample <pre>
 *    // fetch all recordings
 *    IProgramInfoList allRecordings = backend.queryRecordings();
 *    
 *    // filter recordings by flag
 *    IProgramInfoFilter recordingsFilter = ProgramInfoFilters.flag(IProgramFlags.Flags.FL_AUTOEXP);
 *    IProgramInfoList filteredRecordingsList = allRecordings.filter(recordingsFilter);
 *    
 *    long spaceUsed = 0;
 *    System.out.println("Expiring records:");
 *    for(IProgramInfo program : expiringRecords) &#123;
 *    	 spaceUsed += program.getFileSize().longValue();
 *       System.out.println("- " + program.getFullTitle());
 *    &#125;
 *    System.out.println(String.format(
 *       "%s used by %d expiring recordings.",
 *       EncodingUtils.getFormattedFileSize(spaceUsed),
 *       expiringRecords.size()
 *    ));
 * </pre>}
 * 
 * @see IProgramInfo.Props#PROGRAM_FLAGS
 * @see IProgramInfo#getProgramFlags()
 */
public interface IProgramFlags extends IVersionable, IEnumFlagGroup<IProgramFlags.Flags> {

	/**
	 *The flags of the {@link IProgramInfo.Props#PROGRAM_FLAGS PROGRAM_FLAGS} property.
	 * <p>
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see ProgramInfoFilters#flag(Flags)
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythtv/programinfo.h">libmythtv/programinfo.h (till 21.08.2009)</a>
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/programinfo.h">libmyth/programinfo.h (till 19.04.2010)</a>
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/programtypes.h">libmyth/programtypes.h (since 16.05.2010)</a>
	 */
	public static enum Flags implements IFlag, IVersionableValue {
		/**
		 * @since {@mythProtoVersion 57}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_NONE(0x00000000),
		
		/**
		 * Commercial flags.
		 * <p>
		 * Commercials are flagged.
		 * 
		 * @since {@mythProtoVersion 00}
		 */
		FL_COMMFLAG(0x00000001),
		
		/**
		 * Cutlist.
		 * <p>
		 * An editing cutlist is present.
		 * 
		 * @since {@mythProtoVersion 00}
		 */
		FL_CUTLIST(0x00000002),
		
		/**
		 * Autoexpire.
		 * <p>
		 * The program is able to auto-expire
		 * @since {@mythProtoVersion 00}
		 */
		FL_AUTOEXP(0x00000004),
		
		/**
		 * Editing.
		 * <p>
		 * If the user has started editing a recording or 
		 * if commercials are being flagged.
		 * 
		 * @since {@mythProtoVersion 00}
		 */
		FL_EDITING(0x00000008),
		
		/**
		 * Bookmark.
		 * <p>
		 * A bookmark is set.
		 * 
		 * @since {@mythProtoVersion 00}
		 */
		FL_BOOKMARK(0x00000010),
		
		/**
		 * In Use Recording.
		 * <p>
		 * This recording is currently in use by another Myth program.
		 * <p>
		 * This was renamed from In-Use to In-Use-Recording in SVN Rev 7928.
		 * 
		 * @since {@mythProtoVersion 21}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_21,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="7812"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="1de52a52e7476ed7d7e4")
		})
		FL_INUSERECORDING(			
			VersionablePair.valueOf(PROTO_VERSION_21, 0x00000020), // 21 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00100000)  // 57 <= protocol
		),
		
		/**
		 * In Use Playing.
		 * <p>
		 * @since {@mythProtoVersion 23}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_23,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="7928"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="c8df32dc3eab6832bdd1")
		})
		FL_INUSEPLAYING(			
			VersionablePair.valueOf(PROTO_VERSION_23, 0x00000040), // 23 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00200000)  // 57 <= protocol
		),
		
		/**
		 * Stereo.
		 * <p>
		 * @since {@mythProtoVersion 27}
		 * @deprecated {@mythProtoVersion 35}, 
		 * 		moved to {@link IProgramAudioProperties.Flags#AUD_STEREO}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_27,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="8949"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="f365f48b45dc73057ab6")
		},to=PROTO_VERSION_35)
		FL_STEREO(0x00000080),
		
		/**
		 * Editing in progress.
		 * <p>
		 * If the user has started but not finished editing.
		 * 
		 * @since {@mythProtoVersion 53}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_53)
		FL_REALLYEDITING(			
			VersionablePair.valueOf(PROTO_VERSION_53, 0x00000080), // 53 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00000020)  // 57 <= protocol
		),
		
		/**
		 * Closed captioned Flag.
		 * <p>
		 * Recording is Closed Captioned.
		 * 
		 * @since {@mythProtoVersion 27}
		 * @deprecated {@mythProtoVersion 35}, 
		 * 		moved to {@link IProgramSubtitleType}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_27,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="8949"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="f365f48b45dc73057ab6")
		},to=PROTO_VERSION_35)
		FL_CC(0x00000100),
		
		/**
		 * Commercial flagging in progress.
		 * <p>
		 * If commercials are being flagged.
		 * @since {@mythProtoVersion 53}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_53)
		FL_COMMPROCESSING(			
			VersionablePair.valueOf(PROTO_VERSION_53, 0x00000100), // 53 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00000040)  // 57 <= protocol
		),
		
		/**
		 * High Definition.
		 * <p>
		 * Recording is in High Definition
		 * 
		 * @since {@mythProtoVersion 27}
		 * @deprecated {@mythProtoVersion 35}, 
		 * 		moved into {@link IProgramVideoProperties.Flags#VID_HDTV}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_27,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="8949"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="f365f48b45dc73057ab6")
		},to=PROTO_VERSION_35)
		FL_HDTV(0x00000200),
		
		/**
		 * Delete Pending.
		 * <p>
		 * Deleting of the recording is pending.
		 * 
		 * @since {@mythProtoVersion 53}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_53)
		FL_DELETEPENDING(			
			VersionablePair.valueOf(PROTO_VERSION_53, 0x00000200), // 53 <= protocol < 57
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00000080)  // 57 <= protocol
		),
		
		/**
		 * Transcoded.
		 * <p>
		 * Recording has been transcoded.
		 * 
		 * @since {@mythProtoVersion 28}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_28,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="9029"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="bdb1d2e5ebbc706e5ffe")
		})
		FL_TRANSCODED( 			
			VersionablePair.valueOf(PROTO_VERSION_28, 0x00000400), // 28 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00000100)  // 57 <= protocol
		),
		
		/**
		 * Watched.
		 * <p>
		 * Marks a recording as watched.
		 * 
		 * @since {@mythProtoVersion 31}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_31,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="11138"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="c3f0c9860f96a979964f")
		})
		FL_WATCHED(			
			VersionablePair.valueOf(PROTO_VERSION_31, 0x00000800), // 31 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00000200)  // 57 <= protocol
		),
		
		/**
		 * Preserved.
		 * <p>
		 * Marks an episode as preserved.
		 * 
		 * @since {@mythProtoVersion 32}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="11455"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="a88b47c172eda224a6f1")
		})
		FL_PRESERVED(			
			VersionablePair.valueOf(PROTO_VERSION_32, 0x00001000), // 32 <= protocol < 57			
			VersionablePair.valueOf(PROTO_VERSION_57, 0x00000400)  // 57 <= protocol
		),
		
		/**
		 * Channel Commercial free.
		 * <p>
		 * Marks a channel to be commercial free.
		 * <p>
		 * This flag replaces {@link ProgramInfo.Props#CHAN_COMM_FREE}.
		 * @since {@mythProtoVersion 57}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_CHANCOMMFREE(0x00000800),
		
		/**
		 * This flag replaces {@link ProgramInfo.Props#REPEAT}.
		 * @since {@mythProtoVersion 57}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_REPEAT(0x00001000),
		
		/**
		 * This flag replaces {@link ProgramInfo.Props#DUPLICATE}.
		 * @since {@mythProtoVersion 57}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_DUPLICATE(0x00002000),
		
		/**
		 * @since {@mythProtoVersion 57}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_REACTIVATE(0x00004000),		
		
		/**
		 * @since {@mythProtoVersion 57}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_IGNOREBOOKMARK(0x00008000),	
		
		/**
		 * @since {@mythProtoVersion 57}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_TYPEMASK(0x000F0000),
		
		/**
		 * @since {@mythProtoVersion 57}
		 */		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_57)
		FL_INUSEOTHER(0x00400000);
		
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