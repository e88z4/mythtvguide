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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * The flags of a remote encoder.
 * <p>
 * This interface represents the result to a {@link IRemoteEncoder#getFlags()} request.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link IRemoteEncoderFlags.Flags} for a list of all available flags.
 * <p>
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		Example - EIT running: 
 * 		<pre>83886082=> &#123;RUN_MAIN_LOOP,SIGNAL_MONITOR_RUNNING,EIT_SCANNER_RUNNING,ANY_RUNNING&#125;</pre>
 * 		Example - idle encoder:
 * 		<pre>2=> &#123;RUN_MAIN_LOOP&#125;</pre>
 * 		Example - Live-TV recording:
 *      <pre>1610612771=> &#123;FRONTEND_READY,RUN_MAIN_LOOP,CANCEL_NEXT_RECORDING,RECORDER_RUNNING,ANY_REC_RUNNING,ANY_RUNNING,RINGBUFFER_READY&#125;</pre>
 * }
 * <br>
 * 
 * @since {@mythProtoVersion 37}
 * 
 * @see IRemoteEncoder#getFlags()
 * @see IMythCommand#QUERY_REMOTEENCODER_GET_FLAGS QUERY_REMOTEENCODER_GET_FLAGS
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
public interface IRemoteEncoderFlags extends IVersionable, IEnumFlagGroup<IRemoteEncoderFlags.Flags> {

	/**
	 * The flags of an {@link IRemoteEncoderFlags} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/blob/master/mythtv/libs/libmythtv/tv_rec.h">tv_rec.h</a>
	 */
	public static enum Flags implements IFlag {
		/**
		 * None.
		 */
		NONE(0x00000000),	
		
	    /* ========================================================================
	     *  General State flags
	     * ======================================================================== */
		/**
		 * General State Flag: Frontend Ready.
		 * <p>
		 * If a frontend has sent {@link IMythCommand#QUERY_RECORDER_FRONTEND_READY} to the recorder.
		 */
		FRONTEND_READY(0x00000001),
		
		/**
		 * General State Flag: Run main loop.
		 * <p>
		 * If the recorder main thread is up and running.
		 */
		RUN_MAIN_LOOP(0x00000002),
		
		/**
		 * General State Flag: Exit player.
		 * <p>
		 * If LiveTV needs to be aborted due to a pending recording.
		 */
		EXIT_PLAYER(0x00000004),
		
		/**
		 * General State Flag: Finish recording.
		 */
		FINISH_RECORDING(0x00000008),
		
		/**
		 * General State Flag: Errored.
		 * <p>
		 * If the recorder is in an error state.
		 */
		ERRORED(0x00000010),
		
		/**
		 * General State Flag: Cancel next recording.
		 * <p>
		 * If the next pending recording should be canceled due to a running LiveTV recording.
		 */
		CANCEL_NEXT_RECORDING(0x00000020),
	
	    /* ========================================================================
	     * Tuning flags
	     * ======================================================================== */
		/**
		 * Tuning Flag: Live TV.
		 * <p>
		 * Final result desired is LiveTV recording.
		 */
		LIVE_TV(0x00000100),
	
		/**
		 * Tuning Flag: Recording.
		 * <p>
		 * final result desired is a timed recording
		 */
		RECORDING(0x00000200),
		
		/**
		 * Tuning Flag: Antenna adjust.
		 * <p>
		 * antenna adjusting mode (LiveTV without recording).
		 */
		ANTENNA_ADJUST(0x00000400),
		
		/**
		 * Tuning Flag: Rec.
		 * <p>
		 */
		REC(0x00000F00),
	
	    /* ========================================================================
	     *  Non-recording Commands
	     * ======================================================================== */
		/**
		 * Non-recording Command: EIT Scan.
		 * <p>
		 * final result desired is an EIT Scan
		 */
		EIT_SCAN(0x00001000),
		
		/**
		 * Non-recording Command: Close Recorder.
		 * <p>
		 * close recorder, keep recording
		 */
		CLOSED_REC(0x00002000),

		/**
		 * Non-recording Command: Kill Recording.
		 * <p>
		 * close recorder, discard recording
		 */
		KILL_REC(0x00004000),
		
		/**
		 * Non-recording Command: No Rec.
		 * <p>
		 */
		NO_REC(0x0000F000),
		
		/**
		 * Non-recording Command: Kill Ringbuffer.
		 */
		KILL_RINGBUFFER(0x00010000),
	
	    /* ========================================================================
	     *  Waiting stuff
	     * ======================================================================== */
		/**
		 * Waiting State: Waiting for rec. pause.
		 */
		WAITING_FOR_REC_PAUSE(0x00100000),
		
		/**
		 * Waiting State: Waiting for signal.
		 */
		WAITING_FOR_SIGNAL(0x00100000),
		
		/**
		 * Waiting State: Need to start recorder.
		 */
		NEED_TO_START_RECORDER(0x00800000),
		
		/**
		 * Waiting State: Pending actions.
		 */
		PENDING_ACTIONS(0x00F00000),
	
	    /* ========================================================================
	     *  Running stuff
	     * ======================================================================== */
		/**
		 * Running State: Signal monitor running.
		 */
		SIGNAL_MONITOR_RUNNING(0x01000000),
		
		/**
		 * Running State: EIT scanner running.
		 */
		EIT_SCANNER_RUNNING(0x04000000),
	
		/**
		 * Running State: Dummy recorder running.
		 */
		DUMMY_RECORDER_RUNNING(0x10000000),
		
		/**
		 * Running State: Recorder running.
		 */
		RECORDER_RUNNING(0x20000000),
		
		/**
		 * Running State: Any rec. running.
		 */
		ANY_REC_RUNNING(0x30000000),
		
		/**
		 * Running State: Any running.
		 */
		ANY_RUNNING(0x3F000000),
	
	    /* ========================================================================
	     *  Tuning state
	     * ======================================================================== */
		/**
		 * Tuning state: Ringbuffer ready.
		 */
		RINGBUFFER_READY(0x40000000),
		
		/**
		 * Tuning state: Detect.
		 */
		DETECT(0x80000000);
		
		private final long flag;
		Flags(long flag) {
			this.flag = flag;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(this.flag);
		}
	}
}