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
package org.jmythapi.protocol;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_19;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_63;

import java.io.IOException;
import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IFreeInputList;
import org.jmythapi.protocol.response.IInputInfo;
import org.jmythapi.protocol.response.IInputInfoTuned;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingStatus;
import org.jmythapi.protocol.response.IRemoteEncoderBusyStatus;
import org.jmythapi.protocol.response.IRemoteEncoderFlags;
import org.jmythapi.protocol.response.IRemoteEncoderState;
import org.jmythapi.protocol.response.ISleepStatus;

/**
 * This interface represents a special view to a MythTV-recorder. 
 * It is mainly used to query and control the recording state of a recorder.
 * 
 * <h3>Supported Protocol Versions:</h3>
 * 
 * <i>Protocol Version Ranges:</i><br>
 * All listed functions are supported by at least one of the supported protocol-versions. If a given function 
 * is not supported by all known protocol-versions, it is marked with the {@link MythProtoVersionAnnotation MythProtoVersion} 
 * annotation. Additionally the lower bound of the version range is documented with the {@code @since} javadoc
 * tag, whereas the upper bound is documented with the {@code @deprecated} tag.
 * <p>
 * <i>Extended Version Ranges:</i><br>
 * Please note that many functions are usable (maybe with some restrictions) beyond the given protocol range, 
 * if an alternative set of functions can be used to achieve the same result. If such an "extended" version-range is 
 * available for a function, this is mentioned as "fallback-from"- and "fallback-to"-version in the functions javadoc.  
 * <p>
 * <i>Supported Versions:</i><br>
 * Which protocol versions are in general supported by the jMythAPI can be seen in the enumeration {@link ProtocolVersion}.
 * 
 * <h3>How to start:</h3>
 * <i>Connect to the recorder:</i><br>
 * The following steps are required to connect to an encoder:
 * <ol>
 * 	<li>Establish a connection to the {@link IBackend backend} and register as a client:<br>
 * 	{@code IBackend backend = BackendFactory.createBackend(...);}<br>
 *  {@code backend.connect();}<br>
 *  {@code backend.annotatePlayback(...);}
 *  </li> 
 *  <li>Connect to a {@link IRecorder recorder}:<br>
 *  {@code IRecorderInfo recorderInfo = backend.getNextFreeRecorder();} or<br>
 *  {@code IRecorderInfo recorderInfo = backend.getRecorderForNum(Integer);} or<br>
 *  {@code IRecorderInfo recorderInfo = backend.getRecorderForProgram(IProgramInfo);}
 *  </li>
 *  <li>Get the encoder:<br>
 *  {@code IRemoteEncoder encoder = recorder.getRemoteEncoder();}
 *  </li>
 * </ol>
 * 
 * <p>
 * <i>Use the encoder:</i><br>
 * Afterwards you can use any function provided by this interface to query and control your encoder.<br/>
 * See the <a href='#usage'>usage examples</a> for some examples.
 * </p>
 * 
 * <p>
 * <i>Disconnect from the encoder:</i><br>
 * This is not required, because an encoder shares a connection with its recorder.
 * </p>
 * 
 * <h3>Usage Examples:</h3>
 * 
 * {@mythCodeExample <pre>
 *    // the connected backend
 *    IBackend backend = ...;
 *
 *    // get a specific recorder
 *    IRecorderInfo recorderInfo = backend.getRecorderForNum(1);
 *    if(recorderInfo != null) &#123;
 *        // connect to the recorder
 *        IRecorder recorder = backend.getRecorder(recorderInfo);
 *        
 *        // query the current recording state
 *        if(recorder.isRecording()) &#123;
 *            // getting a reference to the encoder
 *            IRemoteEncoder encoder = recorder.getRemoteEncoder();
 *            
 *            // check the encoder state
 *            IRemoteEncoderState encoderState = encoder.getState();
 *            System.out.println(String.format("The encoder is in state '%s'.",encoderState.getState()));
 *        &#125;
 *        
 *        // close recorder connection
 *        recorder.close();
 *    &#125;
 * </pre>}
 * 
 * <h4><a name='usage'>More usage Examples:</a></h4>
 * 
 * See the javadoc for the methods provided by this interface.
 * <ul>    
 *    <li><b>Work with recordings:</b>
 *    	<ul>
 *    		<li>Start a recording ({@link #startRecording link})</li>
 *    		<li>Stop a recording ({@link #stopRecording link})</li>
 *    		<li>Cancel next recording ({@link #cancelNextRecording link})</li>
 *    		<li>Check if recording is in progress ({@link #matchesRecording link})</li>
 *    		<li>Get current recording ({@link #getCurrentRecording link})</li> 
 *    	</ul>
 *    </li>
 *    <li><b>Work with inputs:</b>
 *    	<ul>
 *    		<li>Get currently used input ({@link #getBusyInput() link})</li>
 *    		<li>Get free inputs ({@link #getFreeInputs() link})</li>
 *    		<li>Get all inputs ({@link #getInputs() link})</li>
 *    	</ul>
 *    </li>
 *    <li><b>Query Encoder status:</b>
 *    	<ul>
 *    		<li>Query busy status ({@link #getBusyStatus() link})</li>
 *    		<li>Query sleep status ({@link #getSleepStatus() link})</li>
 *    		<li>Query recording status ({@link #getRecordingStatus() link})</li>
 *    		<li>Query current state ({@link #getState() link})</li>
 *    		<li>Query encoder flags ({@link #getFlags() link})</li>
 *    		<li>Query encoder bitrate ({@link #getMaxBitrate() link})</li>
 *    	</ul>
 *    </li>
 * </ul>
 * 
 * @see IRecorder#getRemoteEncoder() getRemoteEncoder
 */
public interface IRemoteEncoder extends IVersionable {

	/**
	 * Gets the ID of this remote encoder.
	 * @return
	 * 		the remote encoder ID.
	 */
	public abstract int getRemoteEncoderID();

	/**
	 * Gets the maximum bitrate the encoder can output.
	 * <p>
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_MAX_BITRATE QUERY_REMOTEENCODER_GET_MAX_BITRATE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public long getMaxBitrate() throws IOException;	
	
	/**
	 * Gets the current sleep status of the encoder.
	 * 
	 * @return
	 * 		the current sleep status of the encoder.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_SLEEPSTATUS QUERY_REMOTEENCODER_GET_SLEEPSTATUS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 45}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_45)
	public abstract ISleepStatus getSleepStatus() throws IOException;

	/**
	 * Returns the current encoder state (TvState).
	 * <p>
	 * For example an idle encoder is in the state {@link org.jmythapi.protocol.response.IRemoteEncoderState.State#NONE NONE},
	 * whereas a encoder recording a scheduled program is in the state {@link org.jmythapi.protocol.response.IRemoteEncoderState.State#RECORDING_ONLY RECORDING_ONLY}.
	 * <p>
	 * See {@link org.jmythapi.protocol.response.IRemoteEncoderState.State State} for all available states.
	 * 
	 * @return 
	 * 		the remote-encoder state
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00} 
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_STATE QUERY_REMOTEENCODER_GET_STATE
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public abstract IRemoteEncoderState getState() throws IOException;

	/**
	 * Gets additional information about the current encoder state.
	 * <p>
	 * In addition to the {@link #getState() encoder-state}, various other flags 
	 * can be checked to get further information.
	 * <br>
	 * For example an idle encoder may currently fetch EIT informations 
	 * (See {@link org.jmythapi.protocol.response.IRemoteEncoderFlags.Flags#EIT_SCANNER_RUNNING EIT_SCANNER_RUNNING}).
	 * <p>
	 * See {@link org.jmythapi.protocol.response.IRemoteEncoderFlags.Flags Flags} for all available flags.
	 *  
	 * @return
	 * 		additional information about the current encoder state.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_FLAGS QUERY_REMOTEENCODER_GET_FLAGS
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public abstract IRemoteEncoderFlags getFlags() throws IOException;

	/**
	 * Query whether the recorder is currently busy.
	 * <p>
	 * 
	 * @return
	 * 		{@code true} if the encoder is currently busy.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see #isBusy(Integer)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean isBusy() throws IOException;

	/**
	 * Query whether the recorder is busy, or will be within the next time_buffer seconds.
	 * <p>
	 * 
	 * @return
	 * 		{@code true} if the encoder is currently busy or will be busy in the given amount of seconds.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see #getBusyStatus(Integer)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean isBusy(
		@MythProtoVersionAnnotation(from = PROTO_VERSION_37) 
		Integer timeBuffer
	) throws IOException;

	/**
	 * Query the current busy status of the recorder.
	 * <p>
	 * If the encoder is busy the response includes informations about the
	 * busy encoder input device (since {@mythProtoVersion 37}).
	 * 
	 * @return
	 * 		the current busy status, including informations about the busy input device.
	 * 
	 * @see #getBusyStatus(Integer)
	 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract IRemoteEncoderBusyStatus getBusyStatus() throws IOException;

	/**
	 * <p>Query the current busy status of the recorder.</p>
	 * 
	 * @param timeBuffer 
	 * 		in seconds (Since {@mythProtoVersion 37})
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public abstract IRemoteEncoderBusyStatus getBusyStatus(
		@MythProtoVersionAnnotation(from = PROTO_VERSION_37) 
		Integer timeBuffer
	) throws IOException;

	/**
	 * Queries the currently used input of a busy encoder.
	 * <p>
	 * This function internally uses {@link #getBusyStatus()} to determine the current
	 * encoder status.
	 * 
	 * @return
	 * 		the busy input or {@code null} if the encoder is not busy
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IInputInfoTuned getBusyInput() throws IOException;
	
	/**
	 * Queries the input that will be used by the encoder in the given amount of seconds.
	 * <p>
	 * This function internally uses {@link #getBusyStatus(Integer)} to determine the
	 * encoder status.
	 * 	 
	 * @param timeBuffer 
	 * 		in seconds (Since {@mythProtoVersion 37})
	 * @return
	 * 		the busy input or {@code null} if the encoder is not busy
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IInputInfoTuned getBusyInput(Integer timeBuffer) throws IOException;	
	
	/**
	 * Gets the free inputs of the encoder.
	 * 
	 * @return 
	 * 		a list of free inputs or {@code null} if there is no free input.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_FREE_INPUTS QUERY_REMOTEENCODER_GET_FREE_INPUTS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_37)
	public abstract IFreeInputList getFreeInputs() throws IOException;
	
	/**
	 * Gets a list of all inputs of the encoder.
	 * <p>
	 * This method internally uses {@link #getFreeInputs()} to determine all free inputs
	 * and {@link #getBusyInput()} to get the currently used input. 
	 * <p>
	 * <i>Usage Example:</i>
	 * <br>
	 * {@mythCodeExample <pre>
	 * IRemoteEncoder encoder = ...; // an already connected encoder
	 *  
	 * List&lt;IInputInfo&gt; allInputs = encoder.getInputs();
	 * for(IInputInfo input : allInputs) &#123;
	 *    System.out.println(String.format(
	 *       "Input %d (name: %s, source: %d, card: %d, multiplex: %d) is %s.",
	 *       input.getInputID(),
	 *       input.getInputName(),
	 *       input.getSourceID(),
	 *       input.getCardID(),
	 *       input.getMultiplexID(),
	 *       input.isBusy()?"busy":"idle"
	 *    ));
	 *  &#125;
	 *  
	 *  <pre>}
	 * 
	 * @return
	 * 		a list of all inputs of this encoder.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see #getFreeInputs()
	 * @see #getBusyInput()
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_FREE_INPUTS QUERY_REMOTEENCODER_GET_FREE_INPUTS
	 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public List<IInputInfo> getInputs() throws IOException;
	
	/**
	 * Checks if this encoder is currently recording the given recording.
	 * <p>
	 * 
	 * @param programInfo
	 * 		the recording to check.
	 * @return
	 * 		{@code true} if the encoder is currently recording the given program.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_MATCHES_RECORDING QUERY_REMOTEENCODER_MATCHES_RECORDING
	 * @see #matchesRecording(IProgramInfo)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean matchesRecording(IProgramInfo programInfo) throws IOException;
	
	/**
	 * TODO: is it possible to pass in a Nextrecorderproiinfo here?
	 * 
	 * @param programInfo
	 * 		the recording to start
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_START_RECORDING QUERY_REMOTEENCODER_START_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public boolean startRecording(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Stop a currently active recording on the given recorder.
	 * <p>
	 * 
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_STOP_RECORDING QUERY_REMOTEENCODER_STOP_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public boolean stopRecording() throws IOException;
	
	/**
	 * Returns the encoders current recording.
	 * <p>
	 * 
	 * <h4>Usage Note:</h4>
	 * According to the <a href="http://www.sudu.dk/mythtvplayer/index.php?page=mythtv-protocol">MythTV Player</a>
	 * homepage the backend may crash if this function is called on an idle encoder. Therefore this function first checks
	 * via a call to {@link #isBusy()} if this encoder is busy and if it is not, it just returns {@code null}.
	 * <p>
	 * 
	 * @return
	 * 		the current recording, or {@code null} if the encoder
	 * 		is currently not recording.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_CURRENT_RECORDING QUERY_REMOTEENCODER_GET_CURRENT_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 19}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_19)
	public abstract IProgramInfo getCurrentRecording() throws IOException;	
	
	/**
	 * Gets the current recording status of the tuner.
	 * <p>
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to protocol-version {@mythProtoVersion 63},
	 * this function uses {@link #getCurrentRecording()} to get the current recording
	 * and calls {@link IProgramInfo#getRecordingStatus()} afterwards, to determine the 
	 * current recording status. If there is no current recording, {@code null} is returned.
	 * 
	 * @return
	 * 		the recording status or {@code null} if unknown.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_GET_RECORDING_STATUS QUERY_REMOTEENCODER_GET_RECORDING_STATUS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 63}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_63)
	public IProgramRecordingStatus getRecordingStatus() throws IOException;
	
	/**
	 * Tells the encoder to cancel the next recording.
	 * <p>
	 * This is used when the user is watching "Live TV" and does not 
	 * want to allow the recorder to be taken for a pending recording.
	 * 
	 * @param cancel
	 * 		if the next recording should be canceled or continued.
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */
	public boolean cancelNextRecording(Boolean cancel) throws IOException;
}