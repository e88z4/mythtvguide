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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_04;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_07;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_12;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_15;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_19;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_31;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_33;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_63;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_65;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.utils.EnumUtils;

public interface IProgramRecordingStatus extends IEnumGroup<IProgramRecordingStatus.Status>, IVersionable {

	/**
	 * The values of the {@link IProgramInfo.Props#REC_STATUS} property.
	 * <p>
	 * A response example:
	 * <br>
	 * {@mythResponseExample 
	 * 		For Live-TV recording:
	 * 		<pre>-2=> &#123;RECORDING&#125;</pre>
	 * }
	 * <p>
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/programtypes.h">libmyth/programtypes.h</a> 
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/mythcontext.h">libmyth/mythcontext.h (as of 2010-05-16)</a>
	 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/programinfo.h">libmyth/programinfo.h (till 2010-05-16)</a>
	 */
	public static enum Status implements IVersionableValue {
		/**
		 * Other Recording.
		 * <p>
		 * This showing is being recorded on a different channel.
		 * <p>
		 * These status is analogous to the existing {@link #RECORDING} status and indicates 
		 * that a recording of the program is active on another related channel.
		 * 
		 * @since {@mythProtoVersion 73}
		 * @see #RECORDING
		 */
		@MythProtoVersionAnnotation(from=ProtocolVersion.PROTO_VERSION_73,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="090f847f1549e28cd92d6273f0b492293a9e421c")
		})			
		OTHER_RECORDING(-13),
		
		/**
		 * Other Tuning.
		 * <p>
		 * The showing is being tuned on a different channel.
		 * <p>
		 * These status is analogous to the existing {@link #TUNING} status and indicates 
		 * that a recording of the program is active on another related channel.
		 * 
		 * @since {@mythProtoVersion 73}
		 * @see #TUNING
		 */
		@MythProtoVersionAnnotation(from=ProtocolVersion.PROTO_VERSION_73,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="090f847f1549e28cd92d6273f0b492293a9e421c")
		})		
		OTHER_TUNING(-12),
		
		/**
		 * Missed Future.
		 * <p>
		 * This showing was not recorded because the master backend was hung or not running.
		 * 
		 * @since {@mythProtoVersion 65}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_65,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="543d7836c99adc4817fb")
		})
		MISSED_FUTURE(-11),
		
		/**
		 * Tuning.
		 * <p>
		 * The channel is being tuned.
		 * 
		 * @since {@mythProtoVersion 63}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_63)
		TUNING(-10),
		
		/**
		 * Recorder Failed.
		 * <p>
		 * E.g. if the recorder fails to tune in the channel.
		 * 
		 * @since {@mythProtoVersion 31}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_31,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="11211"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="ff50f067a1b403779ac4")
		})
		FAILED(-9),
		
		/**
		 * Tuner Busy.
		 * <p>
		 * The tuner card was already being used.
		 */
		TUNER_BUSY(
			// 00 <= protocol < 19
			VersionablePair.valueOf(PROTO_VERSION_00, 12),
			// 19 <= protocol (SVN 6896, Git 8542a707a7f3e0393db9)
			VersionablePair.valueOf(PROTO_VERSION_19, -8) 
		),
		
		/**
		 * Low Disk Space.
		 * <p>
		 * There wasn't enough disk space available.
		 */
		LOW_DISKSPACE(
			// 19 <= protocol
			VersionablePair.valueOf(PROTO_VERSION_00, +11),
			// 19 <= protocol (SVN 6896, Git 8542a707a7f3e0393db9)
			VersionablePair.valueOf(PROTO_VERSION_19, -07)
		),
		
		/**
		 * Manual Cancel.
		 * <p>
		 * This showing was not recorded because it was manually cancelled.
		 */
		CANCELLED(
			// 19 <= protocol
			VersionablePair.valueOf(PROTO_VERSION_00, +6),
			// 19 <= protocol (SVN 6896, Git 8542a707a7f3e0393db9)
			VersionablePair.valueOf(PROTO_VERSION_19, -6)
		),
		
		/**
		 * Deleted.
		 * <p>
		 * This showing was recorded but was deleted.
		 * 
		 * @deprecated in {@mythProtoVersion 19}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_19,toInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="7191"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="42bf4a6e0b961a12ab88")
		})
		DELETED(-5),
		
		/**
		 * Missed.
		 * <p>
		 * This showing was not recorded because it was scheduled after it would have ended.
		 * 
		 * @since {@mythProtoVersion 19}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_19,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="bbc9f2ed33ca6340c82b"),
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="7300")
		})
		MISSED(-5),
		
		/**
		 * Stopped.
		 * <p>
		 * This showing was recorded but was stopped before recording was completed.
		 * 
		 * @deprecated in {@mythProtoVersion 19}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_19,toInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="7191"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="42bf4a6e0b961a12ab88")
		})
		STOPPED(-4),
		
		/**
		 * Aborted.
		 * <p>
		 * This showing was recorded but was aborted before recording was completed.
		 * 
		 * @since {@mythProtoVersion 19}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_19,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="7191"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="42bf4a6e0b961a12ab88")
		})
		ABORTED(-4),
		
		/**
		 * Recorded.
		 * <p>
		 * This showing was recorded.
		 */
		RECORDED(-3),
		
		/**
		 * Recording.
		 * <p>
		 * This showing is being recorded.
		 */
		RECORDING(-2),
		
		/**
		 * Will Record.
		 * <p>
		 * This showing will be recorded.
		 */
		WILL_RECORD(-1),
		
		/**
		 * Unknown.
		 * <p>
		 * The status of this showing is unknown.
		 */
		UNKNOWN(0),
	
		/**
		 * Manual Override.
		 * <p>
		 * It was manually set to not record.
		 * 
		 * @deprecated as of {@mythProtoVersion 07}, 
		 * 		use {@link #DONT_RECORD} instead.
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_07)
		MANUAL_OVERRIDE(1),
	
		/**
		 * Don't Record.
		 * <p>
		 * It was manually set to not record.
		 * @since {@mythProtoVersion 07}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_07)
		DONT_RECORD(1),
		
		/**
		 * Previously Recorded.
		 * <p>
		 * This episode was previously recorded according to the
		 * duplicate policy chosen for this title.
		 */
		PREVIOUS_RECORDING(2),
		
		/**
		 * Currently Recorded.
		 * <p>
		 * This episode was previously recorded and is still available 
		 * in the list of recordings.
		 */
		CURRENT_RECORDING(3),
		
		/**
		 * Earlier Showing.
		 * <p>
		 * This episode will be recorded at an earlier time instead.
		 * <p>
		 * This was previously (before protocol {@link ProtocolVersion#PROTO_VERSION_04 04}) named
		 * {@code OTHER_SHOWING}.
		 * 
		 * TODO: when was this introduced?
		 */
		EARLIER_SHOWING(4),
		
		/**
		 * Max Recordings.
		 * <p>
		 * Too many recordings of this program have already been recorded.
		 */
		TOO_MANY_RECORDINGS(5),
		
		/**
		 * Not Listed.
		 * <p>
		 * This rule does not match any showing in the current program listings.<br>
		 * This seem also to be true for an inactive manually scheduled recording.
		 * 
		 * @since {@mythProtoVersion 17}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="6301"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="b999a5114b43c0cc12e2")
		})
		NOT_LISTED(
			// 19 <= protocol
			VersionablePair.valueOf(PROTO_VERSION_17, 13),
			// 19 <= protocol (SVN 6896, Git 8542a707a7f3e0393db9)
			VersionablePair.valueOf(PROTO_VERSION_19,  6)
		),
		
		/**
		 * Low Priority.
		 * <p>
		 * Another program with a higher recording priority wil be recorded.
		 * 
		 * @deprecated as of {@mythProtoVersion 04},
		 * 		replaced by {@link #CONFLICT}.
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_04)
		LOWER_REC_PRIORITY(7),
		
		/**
		 * Conflicting.
		 * <p>
		 * Another program with a higher priority will be recorded.
		 * 
		 * @since {@mythProtoVersion 04}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_04)
		CONFLICT(7),
		
		/**
		 * Manual Conflict.
		 * <p>
		 * Another program was manually chosen to be recorded instead.
		 * 
		 * @deprecated as of {@mythProtoVersion 04}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_04)
		MANUAL_CONFLICT(8),
		
		/**
		 * Later Showing.
		 * <p>
		 * This episode will be recorded at an later time.
		 * 
		 * @since {@mythProtoVersion 04}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_04)
		LATER_SHOWING(8),
		
		/**
		 * Auto Conflict.
		 * <p>
		 * Another program was automatically chosen to be recorded instead.
		 * 
		 * @deprecated as of {@mythProtoVersion 04}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_04)
		AUTO_CONFLICT(9),
		
		/**
		 * Repeat.
		 * <p>
		 * This episode is a repeat.
		 * 
		 * @since {@mythProtoVersion 12}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_12)
		REPEAT(9),
		
		/**
		 * Overlap.
		 * <p>
		 * It is covered by another scheduled recording for the same program.
		 * 
		 * @deprecated as of {@mythProtoVersion 07}
		 */
		@MythProtoVersionAnnotation(to=PROTO_VERSION_07)
		OVERLAP(10),
		
		/**
		 * Inactive.
		 * <p>
		 * This recording rule is inactive.
		 * 
		 * @since {@mythProtoVersion 15}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="5133"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="29decbddb635b05a8b25")
		})
		INACTIVE(10),
		
		/**
		 * Never Record.
		 * <p>
		 * It was marked to never be recorded.
		 * 
		 * @since {@mythProtoVersion 19}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_19,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="6896"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="8542a707a7f3e0393db9")
		})
		NEVER_RECORD(11),
		
		/**
		 * Recorder Off-Line.
		 * <p>
		 * The backend recorder is off-line.
		 * 
		 * @since {@mythProtoVersion 28}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_28,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="9064"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="e6b408694f171ae7897a")
		})
		OFFLINE(12),
		
		/**
		 * Other Showing.
		 * <p>
		 * If an episode is shown on two or more channels at the same time, the showing on the
		 * channel(s) not recording are neither Earlier nor Later. A show marked rsOtherShowing 
		 * means "this episode will be recorded on a different channel at this time."
		 * <p>
		 * 
		 * <h3>Protocol Version Hint</h3>
		 * Till protocol version {@mythProtoVersion 74} this status is similar to {@link #WILL_RECORD}, {@link #TUNING}
		 * or {@link #RECORDING}.
		 * <br>
		 * As of {@mythProtoVersion 75} new status values are introduced for {@link #OTHER_TUNING} and {@link #OTHER_RECORDING}.
		 * Therefore this status is then only similar to {@link #WILL_RECORD}.
		 * 
		 * @see #WILL_RECORD
		 * @see #TUNING
		 * @see #RECORDING
		 * @see #OTHER_RECORDING
		 * @see #OTHER_TUNING
		 * @since {@mythProtoVersion 33}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_33,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="12463"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="ca23023b68101eff00c4")
		})
		OTHER_SHOWING(13);
		
		private VersionablePair[] values;
		
		private Status(int value) {
			this(VersionablePair.valueOf(value));
		}
		
		private Status(VersionablePair... values) {
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

	public int getStatusValue();
	
	public Status getStatus();
	
	public boolean hasStatus(Status status);
	
	/**
	 * Checks if the recording is in one of the given statuses.
	 * 
	 * @param statuses
	 * 		the status values to check
	 * @return
	 * 		{@code true} if the current status is in the list of given statuses.
	 */
	public boolean hasStatus(Status... statuses);
}
