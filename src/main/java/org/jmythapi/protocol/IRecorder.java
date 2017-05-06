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
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_18;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_19;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_20;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_21;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_23;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_26;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_27;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_30;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_34;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_77;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.request.EChannelBrowseDirection;
import org.jmythapi.protocol.request.EChannelChangeDirection;
import org.jmythapi.protocol.request.EPictureAdjustmentType;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IFreeInputList;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IRecorderChannelPrefixStatus;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;
import org.jmythapi.protocol.response.IRecorderProgramInfo;
import org.jmythapi.protocol.response.IRingBuffer;

/**
 * An interface to communicate with a MythTV recorder.
 * <p>
 * See the <a href='#usage'>usage examples</a> for a list of things you can do with a recorder.
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
 * The following steps are required to connect to a recorder:
 * <ol>
 * 	<li>Establish a connection to the {@link IBackend backend} and register as a client:<br>
 * 	{@code IBackend backend = BackendFactory.createBackend(...);}<br>
 *  {@code backend.connect();}<br>
 *  {@code backend.annotatePlayback(...);}
 *  </li> 
 *  <li>Getting a {@link IRecorderInfo reference} to a recorder:
 *  	<ul>
 *  		<li>Get the next free recorder:<br>
 *  			{@code IRecorderInfo recorderInfo = backend.getNextFreeRecorder();}</li>
 *  		<li>Get a busy recorder:<br>
 *  			{@code IRecorderInfo recorderInfo = backend.getRecorderForProgram(IProgramInfo);}</li>
 *  		<li>Get a recorder by id:<br>
 *  			{@code IRecorderInfo recorderInfo = backend.getRecorderForNum(Integer);}</li>
 *  	</ul>
 *  </li>
 *  <li>Connect to the recorder:<br>
 *  	{@code IRecorder recorder = backend.getRecorder(IRecorderInfo);}
 *  </li>
 *  <li>Connect to the encoder:<br>
 * 	   See <a href='#usage_encoder'>here</a>, if you need to work with {@link IRemoteEncoder encoders}.
 *  </li>
 * </ol>
 * 
 * <p>
 * <i>Use the recorder:</i><br>
 * Afterwards you can use any function provided by this interface to query and control your recorder.<br/>
 * See the <a href='#usage'>usage examples</a> for some examples.
 * </p>
 * 
 * <p>
 * <i>Disconnect from the recorder:</i><br>
 * To disconnect from the recorder, {@code recorder.close()} needs to be called.
 * </p>
 * 
 * <h3><a name='#usage'>Usage Examples:</a></h3>
 * The following section provides some examples how to use a recorder class.
 * 
 * <h4>Usage Example:</h4>
 * The following example shows how to list all channels available for a recorder.
 * <br>
 * {@mythCodeExample <pre>
 *    // the connected backend
 *    IBackend backend = ...;
 *
 *    // searching for the next free recorder
 *    IRecorderInfo recorderInfo = backend.getNextFreeRecorder();
 *    if(recorderInfo == null) &#123;
 *        System.out.println("No free recorder available");
 *    &#125; else &#123;
 *        // connect to the recorder
 *        IRecorder recorder = backend.getRecorder(recorderInfo);
 *        
 *        // get all channels available on this recorder
 *        List&lt;IRecorderChannelInfo&gt; channels = recorder.getChannelInfos();
 *        for(IRecorderChannelInfo channel : channels) &#123;
 *            System.out.println(String.format("Channel %d: %s",channel.getChannelID(),channel.getChannelName()));
 *        &#125;
 *        
 *        // close recorder connection
 *        recorder.close();
 *    &#125;
 * </pre>}
 * <br>
 * 
 * <h4><a name='usage'>More usage Examples:</a></h4>
 * 
 * See the javadoc for the methods provided by this interface.
 * <ul>    
 *    <li><b>Work with recordings:</b>
 *    	<ul>
 *    		<li>Determine the the recording is in prograss ({@link #isRecording() link})</li>
 *    		<li>Determine the current recording ({@link #getCurrentRecording() link})</li>
 *    		<li>Cancel next recording ({@link #cancelNextRecording(Boolean) link})</li>
 *    		<li>Finish a recording ({@link #finishRecording() link})</li>
 *    		<li>Get the number of bytes written ({@link #getFilePosition() link})</li>
 *    		<li>Get the number of frames written ({@link #getFramesWritten() link})</li>
 *    		<li>Get the frame rate ({@link #getFrameRate() link})</li>
 *    		<li>Get maximum bitrate ({@link #getMaxBitrate() link})</li>
 *   	</ul>
 *    </li>
 *    
 *    <li><b>Work with live-TV:</b>
 *    	<ul>
 *    		<li>Spawn live TV ({@link #spawnLiveTV() link})</li>
 *    		<li>Stop live TV ({@link #stopLiveTv() link})</li>
 *    		<li>Pause live TV ({@link #pause() link})</li>
 *    		<li>Keep live TV recording ({@link #setLiveTvRecording() link})</li>
 *     		<li>Get a ringbuffer connection ({@link #annotateRingBuffer() link} (deprecatec {@mythProtoVersion 20})</li> 
 *    	</ul>
 *    </li>
 *    
 *    <li><b>Work with picture settings:</b>
 *    	<ul>
 *    		<li>Query/Change brightness ({@link #getBrightness() link},{@link #changeBrightness(boolean) link})</li>
 *    		<li>Query/Change color ({@link #getColour() link},{@link #changeColour(boolean) link})</li>
 *    		<li>Query/Change contrast ({@link #getContrast() link},{@link #changeContrast(boolean) link})</li>
 *    		<li>Query/Change hue ({@link #getHue() link},{@link #changeHue(boolean) link})</li>
 *    	</ul>
 *    </li>
 *    
 *    <li><b>Work with channels:</b>
 *    	<ul>
 *    		<li>Check if a channel exists on current tuner ({@link #checkChannel(String) link})</li>
 *    		<li>Check channel-number prefix ({@link #checkChannelPrefix(String) link})</li>
 *    		<li>Check if the input needs to be switched for a channel ({@link #shouldSwitchCard(Integer) link})</li>
 *    		<li>Get all known channels ({@link #getBasicChannelInfos() link})</li>
 *     		<li>Change the channel ({@link #changeChannel(int) link})</li>
 *     		<li>Toggle channel favorite ({@link #toggleChannelFavorite() link})</li>
 *    		<li>Get the program for a channel ({@link #getNextProgramInfos(Date) link})</li>
 *    	</ul>
 *    </li>
 *        
 *    <li><b>Work with inputs:</b>
 *    	<ul>
 *    		<li>Query the current input ({@link #getInput() link})</li>
 *    		<li>List free inputs ({@link #getFreeInputs() link}}</li>
 *    		<li>Change the recorder input ({@link #setInput(String) link})</li>
 *    		<li>Switch to the next input ({@link #switchToNextInput() link})</li>
 *    	</ul>
 *    </li>
 *    
 *    <li><b><a name='usage_encoder'>Work with encoders:</a></b>
 *    	<ul>
 *    		<li>Connect to the encoder ({@link #getRemoteEncoder() link})</li>
 *    	</ul>
 *    </li>
 * </ul>
 * 
 * 
 * @see ProtocolVersion All supported protocol version
 * 
 * @see IBackend#getRecorder(org.jmythapi.IRecorderInfo) getRecorder
 * 
 * @see IBackend#getRecorders() getRecorders
 * @see IBackend#getFreeRecorders() getFreeRecorders
 * 
 * @see IBackend#getRecorderIDs() getRecorderIDs
 * @see IBackend#getFreeRecorderIDs() getFreeRecorderIDs
 * 
 * @see IBackend#getNextFreeRecorder() getNextFreeRecorder
 */
@SuppressWarnings("deprecation")
public interface IRecorder extends IVersionable, Closeable {
	/**
	 * Closes the current recorder connection.
	 * 
	 * @throws IOException
	 * 		on communication errors
	 */
	public void close() throws IOException;
	
	/**
	 * Gets the id of this recorder
	 * @return
	 * 		the recorder id
	 */
	public abstract int getRecorderID();

	/**
	 * Gets the remote-encoder object.
	 * @return
	 * 		the remote encoder object.
	 */
	public abstract IRemoteEncoder getRemoteEncoder();

	/**
	 * Tells the recorder that the frontend is up and ready.
	 * <p>
	 * This is required e.g. if the backend needs to transmit the {@code ASK_RECORDING} message to the frontend.
	 * 
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_FRONTEND_READY QUERY_RECORDER_FRONTEND_READY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean signalFrontendReady() throws IOException;
	
	/**
	 * Tells the recorder to stop streaming a recording to the frontend.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #stopLiveTv} to stop streaming live TV.
	 * 
	 * @return
	 * 		{@code true} on success. 
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_STOP_PLAYING QUERY_RECORDER_STOP_PLAYING
	 *  
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20)
	public abstract boolean stopPlaying() throws IOException;

	/**
	 * Tells the recorder to start a "Live TV" recording.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #stopLiveTv} to stop streaming live TV.
	 * 
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #spawnLiveTV(boolean, String)
	 * @see IMythCommand#QUERY_RECORDER_SPAWN_LIVETV QUERY_RECORDER_SPAWN_LIVETV
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean spawnLiveTV() throws IOException;

	/**
	 * Tells the recorder to start a "Live TV" recording.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #stopLiveTv} to stop streaming live TV.
	 * 
	 * @param pictureInPicture
	 * 		specifies if picture in picture should be used. (Since {@mythProtoVersion 20}).
	 * @param recorderChannelInfo
	 * 		the channel to swith the recorder to. (Since {@mythProtoVersion 34}).
	 * @return
	 * 		{@code true} if the backend returned {@code OK}.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see #spawnLiveTV(boolean, String)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean spawnLiveTV(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_20)
		final boolean pictureInPicture,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_34) 
		final IRecorderChannelInfo recorderChannelInfo
	)throws IOException;

	/**
	 * Tells the recorder to start a "Live TV" recording.
	 * <p>
	 *  
	 * <h4>Protocol Version Hint:</h4>
	 * For protocols prior to {@mythProtoVersion 20}, this function also initializes a ringbuffer, required for LiveTV streaming.<br>
	 * See {@link #annotateRingBuffer()} to get access to this ringbuffer. 
	 * <p>
	 * If this function is called prior to {@mythProtoVersion 34}, {@link #setChannel(String)} is used to switch to the desired channel. 
	 * 
	 * @param pictureInPicture
	 * 		specifies if picture in picture should be used. (Since {@mythProtoVersion 20}).
	 * @param channelNumber
	 * 		the channel number to switch to. (Since {@mythProtoVersion 34}).
	 * @return
	 * 		{@code true} if the backend returned {@code OK}.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SETUP_RING_BUFFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean spawnLiveTV(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_20)
		final boolean pictureInPicture,
		@MythProtoVersionAnnotation(from = PROTO_VERSION_34) 
		final String channelNumber
	)throws IOException;

	/**
	 * Tells the recorder to stop a "Live TV" recording.
	 * <p>
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #spawnLiveTV} to start playing Live TV.
	 * 
	 * @return
	 * 		{@code true} if the recorder returned {@code OK}.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean stopLiveTv() throws IOException;

	/**
	 * Checks if the recorder is actually recording.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #waitForIsRecording(long)} if you need to wait till the recorder is recording.
	 * 
	 * @return
	 * 		{@code true} if the recorder is currently recording.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_IS_RECORDING QUERY_RECORDER_IS_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean isRecording() throws IOException;

	/**
	 * Wait for recording.
	 * <p>
	 * Checks if the recorder is actually recording until recording has been started or the timeout has reached.
	 * 
	 * @param timeoutMs
	 * 		the timeout in ms
	 * @return
	 * 		{@code true} if the recorder is currently recording
	 * @throws IOException
	 * 		on communication errors
	 * @throws InterruptedException
	 * 		if the thread was interrupted
	 */
	public boolean waitForIsRecording(long timeoutMs) throws IOException, InterruptedException;
	
	/**
	 * Tells the recorder to stop recording, but only after "overrecord" seconds.
	 * <p>
	 * 
	 * @return 
	 * 		{@code true} if the recorder returned {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_FINISH_RECORDING QUERY_RECORDER_FINISH_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean finishRecording() throws IOException;
	
	/**
	 * Tells the recorder to keep a LiveTV recording.
	 * 
	 * @return 
	 * 		{@code true} if the backend returned {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SET_LIVE_RECORDING QUERY_RECORDER_SET_LIVE_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 26}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_26)
	public abstract boolean setLiveTvRecording() throws IOException;

	/**
	 * Signals the recorder that we want to use a ring-buffer.
	 * <p>
	 * 
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}
	 * @throws IOException 
	 * 		on communication errors
	 *
	 * @see IMythCommand#ANN_RING_BUFFER ANN_RING_BUFFER
	 *
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_20)
	public abstract IRingBuffer annotateRingBuffer() throws IOException;

	/**
	 * Returns the recorders current recording.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Prior to protocol version {@mythProtoVersion 20} this function returns {@code null} on live-tv recordings.
	 * 
	 * @return
	 * 		the current recording, or {@code null} if the 
	 * 		recorder is currently not recording.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_RECORDING QUERY_RECORDER_GET_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract IProgramInfo getRecording() throws IOException;

	/**
	 * Returns the recorders current recording.
	 * <p>
	 * 
	 * <h4>Usage Note:</h4>
	 * According to the <a href="http://www.sudu.dk/mythtvplayer/index.php?page=mythtv-protocol">MythTV Player</a>
	 * homepage, the backend may crash if this function is called on an idle recorder. Therefore this function first checks
	 * via a call to {@link #isRecording()} if this recorder is busy and if it is not, it just returns {@code null}.
	 * <p>
	 * 
	 * @return
	 * 		the current recording, or {@code null} if the recorder
	 * 		is currently not recording.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_CURRENT_RECORDING QUERY_RECORDER_GET_CURRENT_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 19 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_19, fromFallback=PROTO_VERSION_00)
	public abstract IProgramInfo getCurrentRecording() throws IOException;

	/**
	 * Gets the current program on the current recorder channel.
	 * <p>
	 * This returns information about the program that could be seen on the current
	 * channel of the recorder.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If you call this function with a protocol version greater than {@mythProtoVersion 20},
	 * then {@link #getNextProgramInfo getNextProgramInfo} is used to fetch the information.
	 * 
	 * @return
	 * 		information about the current program on the current channel of the recorder.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_PROGRAM_INFO QUERY_RECORDER_GET_PROGRAM_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 21 toFallback=-1}, 
	 * 		use {@link #getNextProgramInfo getNextProgramInfo} instead.
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_21,toFallback=PROTO_VERSION_LATEST)
	public abstract IRecorderProgramInfo getProgramInfo() throws IOException;

	/**
	 * Gets the next program on the specified channel.
	 * <p>
	 * This returns information about the program that would be seen, if we changed the channel using 
	 * the given direction.
	 * <p>
	 * 
	 * <h4>Usage Note:</h4>
	 * Use {@link EChannelChangeDirection#SAME SAME} to query the program on the given channel.
	 * But if {@link EChannelChangeDirection#SAME SAME} is used, the channel-info must not be {@code null},
	 * otherwise the backend will fallback to the first available channel.
	 * 
	 * @param channelInfo
	 * 		the channel to start browsing.
	 * @param direction
	 * 		the direction to browse. 
	 * @param startTime
	 * 		the starting time that should be used for the query
	 * @return
	 * 		the found program information.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_NEXT_PROGRAM_INFO QUERY_RECORDER_GET_NEXT_PROGRAM_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public abstract IRecorderNextProgramInfo getNextProgramInfo(
		IBasicChannelInfo channelInfo,
		EChannelBrowseDirection direction, 
		Date startTime
	) throws IOException;
	
	/**
	 * Gets the next program on the specified channel.
	 * <p>
	 * This returns information about the program that would be seen, if we changed the channel using 
	 * the given direction.
	 * <p>
	 * 
	 * <h4>Usage Note:</h4>
	 * Use {@link EChannelChangeDirection#SAME SAME} to query the program on the given channel.
	 * But if {@link EChannelChangeDirection#SAME SAME} is used, the channel-info must not be {@code null},
	 * otherwise the backend will fallback to the first available channel.
	 * 
	 * @param channelnumber
	 * 		the number of the channel to start browsing
	 * @param chanID
	 * 		the id of the channel
	 * @param direction
	 * 		the direction to browse. 
	 * @param startTime
	 * 		the starting time that should be used for the query
	 * @return
	 * 		the found program information.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_NEXT_PROGRAM_INFO QUERY_RECORDER_GET_NEXT_PROGRAM_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public abstract IRecorderNextProgramInfo getNextProgramInfo(
			String channelnumber, Integer chanID,
			EChannelBrowseDirection direction, Date startTime
	) throws IOException;

	/**
	 * Gets a list containing the next programs of all channels.
	 * <p>
	 * This function internally uses {@link #getChannelsNextProgramInfoMap(Date)}.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    IRecorder recorder = ...; // an already connected recorder
	 *    
	 *    // query all currently distributed programs
	 *    List&lt;IRecorderNextProgramInfo&gt; nextPrograms = recorder.getNextProgramInfos(null);
	 *    for(IRecorderNextProgramInfo nextProgram : nextPrograms) &#123;
	 *       // skipping invalid programs
	 *       if(!nextProgram.isValid()) continue;
	 *       
	 *       // print program info
	 *       System.out.println(String.format(
	 *          "%1$tF %1$tT | %2$tF %2$tT | %3$3d min | %4$5s | %5$s",
	 *          nextProgram.getStartDateTime(), nextProgram.getEndDateTime(), nextProgram.getDuration(), 
	 *          nextProgram.getChannelSign(), nextProgram.getFullTitle()
	 *       ));
	 *    &#125;
	 * </pre>}
	 * <br>
	 * The above example will output, e.g.:
	 * <pre>
	 * 2012-01-01 14:55:01 | 2012-01-01 16:30:01 |  95 min |  PRO7 | Sind wir endlich fertig?
	 * 2012-01-01 15:10:01 | 2012-01-01 16:40:01 |  90 min |   ARD | Sterne über dem Eis
	 * 2012-01-01 13:45:01 | 2012-01-01 17:05:01 | 200 min |   ZDF | ZDF SPORTextra
	 * 2012-01-01 15:10:01 | 2012-01-01 18:45:01 | 215 min |   RTL | Die ultimative Chart Show - Die erfolgreichsten Hits des Jahres 2011
	 * 2012-01-01 14:10:01 | 2012-01-01 15:45:01 |  95 min | SAT.1 | Die Schöne und das Biest
	 * 2012-01-01 14:45:01 | 2012-01-01 15:30:01 |  45 min |   BR3 | Als die Fremden kamen - Vom Winterurlaub in Tirol
	 * 2012-01-01 14:45:01 | 2012-01-01 15:30:01 |  45 min |  SWBW | Neues aus dem Glottertal
	 * 2012-01-01 13:55:01 | 2012-01-01 15:50:01 | 115 min |  ORF1 | FIS Weltcup Skispringen - HS 140 - Das Springen
	 * 2012-01-01 15:15:01 | 2012-01-01 17:00:01 | 105 min |  ORF2 | Die Z&uuml;rcher Verlobung
	 * 2012-01-01 14:45:01 | 2012-01-01 16:00:01 |  75 min |  3SAT | Willi und die Wunder dieser Welt
	 * 2012-01-01 14:30:01 | 2012-01-01 16:05:01 |  95 min | KABE1 | Legende
	 * 2012-01-01 15:15:01 | 2012-01-01 16:30:01 |  75 min | SUPRT | Cosmo &amp; Wanda - Wenn Elfen helfen: Alltagshelden - Die Alltagshelden
	 * 2012-01-01 14:35:01 | 2012-01-01 16:15:01 | 100 min |   VOX | Nine Months
	 * 2012-01-01 15:15:01 | 2012-01-01 16:55:01 | 100 min |  RTL2 | Der Todes-Twister
	 * 2012-01-01 14:55:01 | 2012-01-01 15:40:01 |  45 min |  ARTE | Naturparadiese Afrikas - &Auml;thiopien - Land der Extreme
	 * 2012-01-01 13:40:01 | 2012-01-01 15:20:01 | 100 min |  ATV+ | Nach Ansage
	 * 2012-01-01 14:45:00 | 2012-01-01 16:30:00 | 105 min | PULS4 | Louis' unheimliche Begegnung mit den Außerirdischen
	 * </pre>
	 * 
	 * @param startTime
	 * 		the start time. if this is {@code null} the current date is used.
	 * @return
	 * 		the next programs of all channels.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_NEXT_PROGRAM_INFO QUERY_RECORDER_GET_NEXT_PROGRAM_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public List<IRecorderNextProgramInfo> getNextProgramInfos(Date startTime) throws IOException;
	
	/**
	 * Gets a map containing the next programs of all channels.
	 * <p>
	 * This returns a map containing the channel-id as key and recorder-next-program info as value.
	 * <p>
	 * This method uses {@link #getNextProgramInfo(String, Integer, EChannelBrowseDirection, Date)} to loop
	 * through all channels and to fetch the current channel program.
	 * 
	 * @return
	 * 		the channel-program map
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public Map<Integer,IRecorderNextProgramInfo> getChannelsNextProgramInfoMap(Date startTime) throws IOException;
	
	/**
	 * Gets informations about the given channel.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link IBackend#setChannelInfo(String, IRecorderChannelInfo)} to change the properties of a channel.
	 * 
	 * @param chanID
	 * 		the id of the desired channel.
	 * @return
	 * 		informations about the given channel
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 28}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public abstract IRecorderChannelInfo getChannelInfo(Integer chanID) throws IOException;

	/**
	 * Gets detailed informations about the given channel.
	 * <p>
	 * This uses the channel-id of the basic-channel-info object to fetch additional informations 
	 * about the channel.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link IBackend#setChannelInfo(String, IRecorderChannelInfo)} to change the properties of a channel.
	 * 
	 * @param channelInfo
	 * 		the desired channel
	 * @return
	 * 		informations about the given channel	 * 
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see #getChannelInfo(Integer)
	 * @see IMythCommand#QUERY_RECORDER_GET_CHANNEL_INFO QUERY_RECORDER_GET_CHANNEL_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 28}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public abstract IRecorderChannelInfo getChannelInfo(IBasicChannelInfo channelInfo) throws IOException;
	
	/**
	 * Gets the currently active channel of the recorder.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the current protocol-version is less than {@mythProtoVersion 28}
	 * an {@link IBasicChannelInfo} object is returned, otherwise an {@link IRecorderChannelInfo} object is returned.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // getting the current input of the recorder
	 *    String recorderInput = recorder.getInput();
	 *    
	 *    // getting the current recorder channel
	 *    IBasicChannelInfo recorderChannel = recorder.getCurrentChannel();
	 *    
	 *    // print infos
	 *    System.out.println(String.format(
	 *       "Recorder %d is using input: %s (channel: %s).",
	 *       recorderId, recorderInput, recorderChannel==null?"-":recorderChannel.getChannelSign()
	 *    ));
	 * </pre>}
	 * 
	 * @param <C>
	 * 		the type of the channel information. This is either a {@link IRecorderChannelInfo} or a {@link IBasicChannelInfo}.
	 * @return
	 * 		the information about the current channel or {@code null}.
	 * @throws IOException
	 * 		on communication errors.
	 */
	public <C extends IBasicChannelInfo> C getCurrentChannel() throws IOException;
	
	/**
	 * Gets a list of all known channels.
	 * <p>
	 * 
	 * This function uses {@link #getChannelsNextProgramInfoMap(Date)} to determine the channel-IDs of all known channels
	 * and {@link #getChannelInfo(Integer)} to fetch the info object for each channel.
	 * <p>
	 * 
	 * <h4>Response example:</h4>
	 * {@mythResponseExample <pre>
	 * 		<0>CHANNEL_ID: 11123 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: PULS 4 | <3>CHANNEL_NUMBER: 10123 | <4>CHANNEL_NAME: PULS 4 | <5>XMLTV_ID:  
	 * 		<0>CHANNEL_ID: 12104 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: ServusTV | <3>CHANNEL_NUMBER: 11104 | <4>CHANNEL_NAME: ServusTV | <5>XMLTV_ID:  
	 * 		<0>CHANNEL_ID: 11120 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: ATV | <3>CHANNEL_NUMBER: 10120 | <4>CHANNEL_NAME: ATV | <5>XMLTV_ID:  
	 * 		<0>CHANNEL_ID: 11101 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: ORF1 | <3>CHANNEL_NUMBER: 10101 | <4>CHANNEL_NAME: ORF1 | <5>XMLTV_ID:  
	 * 		<0>CHANNEL_ID: 12102 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: 3SAT | <3>CHANNEL_NUMBER: 11102 | <4>CHANNEL_NAME: 3SAT | <5>XMLTV_ID:  
	 * 		<0>CHANNEL_ID: 11102 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: ORF2 W | <3>CHANNEL_NUMBER: 10102 | <4>CHANNEL_NAME: ORF2 W | <5>XMLTV_ID:  
	 * 		<0>CHANNEL_ID: 12103 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: ORF Sport Plus | <3>CHANNEL_NUMBER: 11103 | <4>CHANNEL_NAME: ORF Sport Plus | <5>XMLTV_ID: 
	 * </pre> }
	 * 
	 * @return
	 * 		a list of all available channels.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 28}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public List<IRecorderChannelInfo> getChannelInfos() throws IOException;
	
	/**
	 * Gets a list of all known channels.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the current protocol-version is less than {@mythProtoVersion 28}
	 * an {@link IBasicChannelInfo} object is returned, otherwise an {@link IRecorderChannelInfo} object is returned.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    IRecorder recorder = ...; // an already connected recorder
	 *    
	 *    // getting all available channel 
	 *    List&lt;IBasicChannelInfo&gt; channels = recorder.getBasicChannelInfos();
	 *    System.out.println(String.format(
	 *       "Id   |  Nr | Sign "				
	 *    ));
	 *    for(IBasicChannelInfo channel : channels) &#123;
	 *       System.out.println(String.format(
	 *          "%04d | %3s | %s",
	 *          channel.getChannelID(),channel.getChannelNumber(), channel.getChannelSign()
	 *       ));
	 *    &#125;
	 * </pre>}
	 * <br>
	 * The above example will output, e.g.
	 * <pre>
	 * Id   |  Nr | Sign 
	 * 1063 |   1 | PRO7
	 * 1064 |   5 | ARD
	 * 1065 |   6 | ZDF
	 * 1066 |   3 | RTL
	 * 1067 |   2 | SAT.1
	 * 1068 |  14 | BR3
	 * 1071 |  16 | SWBW
	 * 1072 |  10 | ORF1
	 * 1073 |  11 | ORF2
	 * 1074 |  18 | A9
	 * 1075 |  12 | 3SAT
	 * 1077 |   7 | KABE1
	 * 1078 |  15 | SUPRT
	 * 1079 |   8 | VOX
	 * 1080 |   4 | RTL2
	 * 1085 |   9 | ARTE
	 * 1091 |  13 | ATV+
	 * 1095 |  17 | PULS4
	 * </pre>
	 * @return
	 * 		a list of all known channels.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public <C extends IBasicChannelInfo> List<C> getBasicChannelInfos() throws IOException;
	
	/**
	 * Gets the name of the current recorder input.
	 *
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called with a protocol-version greater to or equal with {@mythProtoVersion 27},
	 * then the function {@link #getInput()} is called instead.
	 * 
	 * @return
	 * 		the name of the current input, e.g. {@code DVBInput}
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_INPUT_NAME QUERY_RECORDER_GET_INPUT_NAME
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 21}, use {@link #getInput()} instead.
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_21)
	public abstract String getInputName() throws IOException;

	/**
	 * Gets the name of the current recorder input.
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called prior to {@mythProtoVersionHint 27}, then {@link #getInputName()} is called instead.
	 * 
	 * @return
	 * 		the name of the current recorder input, e.g. {@code DVBInput} or {@code Tuner 1}.
	 * 		If the recorder is currently not active {@code null} is returned. 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 27}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_INPUT QUERY_RECORDER_GET_INPUT
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_27)
	public abstract String getInput() throws IOException;

	/**
	 * Tells the recorder to change to the specified input.
	 * 
	 * @param inputName
	 * 		the name of the input to change to.
	 * @return 
	 * 		{@code true} if the input was switched successfully.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 27}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SET_INPUT QUERY_RECORDER_SET_INPUT
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27)
	public boolean setInput(String inputName)throws IOException;
	
	/**
	 * Tells the recorder to change to the next input.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called with a protocol-version starting with {@mythProtoVersion 27},
	 * then the function {@link #switchToNextInput()} is called instead.
	 * 
	 * @return
	 * 		{@code true} if the backend respond with {@code ok}.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 27 toFallback=-1}, replaced by {@link #setInput(String)} and {@link #switchToNextInput()}.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_TOGGLE_INPUTS QUERY_RECORDER_TOGGLE_INPUTS
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_27,toFallback=PROTO_VERSION_LATEST)
	public boolean toggleInputs() throws IOException;
	
	/**
	 * Tells the recorder to switch to the next input.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called for protocols prior to {@mythProtoVersion 27}, then {@link #toggleInputs()} is called.
	 * 
	 * @return 
	 * 		the name of the new input
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 27 fromFallback=0}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SET_INPUT QUERY_RECORDER_SET_INPUT
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27,fromFallback=PROTO_VERSION_00)
	public String switchToNextInput()throws IOException;
	
	/**
	 * Checks if named channel exists on current tuner.
	 * <p>
	 * 
	 * @param channelNumber
	 * 		the channel number
	 * @return
	 * 		{@code true} if it succeeds, {@code false} otherwise.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHECK_CHANNEL QUERY_RECORDER_CHECK_CHANNEL
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean checkChannel(String channelNumber) throws IOException;
	
	/**
	 * Checks a channel-number prefix against the channels in the MythTV-DB.
	 * 
	 * @param channelNumberPrefix
	 * 		the channel number prefix
	 * @throws IOException
	 * 		on communication errors.
	 * @return
	 * 		the matching status
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHECK_CHANNEL_PREFIX QUERY_RECORDER_CHECK_CHANNEL_PREFIX
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVerson 00}
	 */
	public abstract IRecorderChannelPrefixStatus checkChannelPrefix(String channelNumberPrefix) throws IOException;

	/**
	 * Changes to the next or previous channel.
	 * 
	 * <h4>Usage Note:</h4>
	 * You must pause the recorder before doing this.
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see #changeChannel(int)
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_CHANNEL QUERY_RECORDER_CHANGE_CHANNEL
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean changeChannel(EChannelChangeDirection direction) throws IOException;

	/**
	 * Changes to the next or previous channel.
	 * 
	 * <h4>Usage Note:</h4>
	 * You must pause the recorder before doing this.
	 * 
	 * @param direction 
	 * 		{@code 0}=up, {@code 1}=down, {@code 2}=favorite, {@code 3}=same
	 * @return 
	 * 		{@code true} if the channel was changed successfully.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_CHANNEL QUERY_RECORDER_CHANGE_CHANNEL
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean changeChannel(int direction) throws IOException;

	/**
	 * Changes to a named channel on the current tuner.
	 * 
	 * <h4>Usage Note:</h4>
	 * You must pause the recorder before doing this.
	 * 
	 * @param channelNumber
	 * 		the channel number
	 * @return
	 * 		{@code true} if the backend returned {@code OK}.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SET_CHANNEL QUERY_RECORDER_SET_CHANNEL
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract boolean setChannel(String channelNumber) throws IOException;

	/**
	 * Checks if named channel exists on current tuner, or another tuner.
	 * <p>
	 * 
	 * @param channelID
	 * 		the id of the channel
	 * @return
	 * 		{@code true} rue if the channel on another tuner and not current tuner,
	 * 	    {@code false} otherwise. 
	 *      This also returns {@code false} if the channel id is unknown.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_SHOULD_SWITCH_CARD QUERY_RECORDER_SHOULD_SWITCH_CARD
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public boolean shouldSwitchCard(Integer channelID) throws IOException;
	
	/**
	 * Adds or removes the current channel from the favorite channel list.
	 * 
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean toggleChannelFavorite() throws IOException;	
	
	/**
	 * Adds or removes the current channel to or from the given channel group.
	 * 
	 * @param channelGroup
	 * 		the name of the channel group (since {@mythProtoVersion 45}).
	 * 		If this is {@code null}, {@link ProtocolConstants#CHANNEL_GROUP_DEFAULT} is used as 
	 * 		group.
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean toggleChannelFavorite(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		String channelGroup
	) throws IOException;
	
	/**
	 * Returns the number of frames written to disk.
	 * <p>
	 * <h4>Usage Hint:</h4>
	 * Use {@link #waitForFramesWritten(long)} if you need to wait for a given amount of frames.
	 * 
	 * @return
	 * 		the number of frames written to disk or {@code -1} if
	 * 		the recorder is not recording.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_FRAMES_WRITTEN QUERY_RECORDER_GET_FRAMES_WRITTEN
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract long getFramesWritten() throws IOException;
	
	/**
	 * Wait for number of frames written.
	 * <p>
	 * This function uses {@link #getFramesWritten()} to determine the current amount of frames written
	 * and waits till the requested amount of frames was writte.
	 * 
	 * @param desiredFrames
	 * 		the amount of frames that should be written
	 * @return
	 * 		the actual amount of frames written
	 * @throws IOException
	 * 		on communication errors
	 * @throws InterruptedException
	 * 		if the thread was interrupted
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_FRAMES_WRITTEN QUERY_RECORDER_GET_FRAMES_WRITTEN
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract long waitForFramesWritten(long desiredFrames) throws IOException, InterruptedException;

	/**
	 * Returns the recording frame rate.
	 * <p>
	 * 
	 * @return
	 * 		the recording frame rate
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_FRAMERATE QUERY_RECORDER_GET_FRAMERATE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract float getFrameRate() throws IOException;

	/**
	 * Returns the total number of bytes written by the recorder.
	 * <p>
	 * 
	 * @return
	 * 		the total number of bytes written.
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_FILE_POSITION QUERY_RECORDER_GET_FILE_POSITION
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public abstract long getFilePosition() throws IOException;


	/**
	 * Gets all free inputs of the recorder.
	 * <p> 
	 * 
	 * @return 
	 * 		a list of free inputs or {@code null} if there is no free input.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_FREE_INPUTS QUERY_RECORDER_GET_FREE_INPUTS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_37)
	public abstract IFreeInputList getFreeInputs() throws IOException;

	/**
	 * Get all inputs configured for the recorder.
	 * <p>
	 * <h3>Protocol Version Hint:</h3>
	 * If this function is called with protocol versions greater than {@mythProtoVersion 37}, {@link #getFreeInputs} and 
	 * {@link #getInput} are used to determine the input names.
	 * 
	 * @return
	 * 		the names of all known inputs
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_CONNECTED_INPUTS QUERY_RECORDER_GET_CONNECTED_INPUTS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 27}
	 * @deprecated {@mythProtoVersion 37 toFallback=-1}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27,to=PROTO_VERSION_37,toFallback=PROTO_VERSION_LATEST)
	public abstract List<String> getConnectedInputs() throws IOException;
	
	/**
	 * <p>Gets the colour of a recording.</p>
	 * 
	 * @returns
	 * 		the current colour value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_COLOUR QUERY_RECORDER_GET_COLOUR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 30}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_30)
	public abstract Integer getColour() throws IOException;

	/**
	 * <p>Gets the contrast of a recording.</p>
	 * 
	 * @returns
	 * 		the current contrast value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 30}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_CONTRAST QUERY_RECORDER_GET_CONTRAST
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_30)
	public abstract Integer getContrast() throws IOException;

	/**
	 * <p>Gets the brightness of a recording.</p>
	 * 
	 * @returns
	 * 		the current brightness value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 30}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_BRIGHTNESS QUERY_RECORDER_GET_BRIGHTNESS
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_30)
	public abstract Integer getBrightness() throws IOException;

	/**
	 * <p>Gets the hue of a recording.</p>
	 * 
	 * @returns
	 * 		the current hue value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 30}
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_HUE QUERY_RECORDER_GET_HUE
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_30)
	public abstract Integer getHue() throws IOException;

	/**
	 * Changes the colour of a recording.
	 * 
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_COLOUR QUERY_RECORDER_CHANGE_COLOUR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */	
	public Integer changeColour(boolean up) throws IOException;
	
	/**
	 * Changes the colour value.
	 * 
	 * @param adjustmentType
	 * 		the adjustment type (Since {@mythProtoVersion 30})
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_COLOUR QUERY_RECORDER_CHANGE_COLOUR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public Integer changeColour(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException;
	
	/**
	 * Changes the contrast of a recording.
	 * 
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_CONTRAST QUERY_RECORDER_CHANGE_CONTRAST
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */		
	public Integer changeContrast(boolean up) throws IOException;	
	
	/**
	 * Changes the contrast value.
	 * 
	 * @param adjustmentType
	 * 		the adjustment type (Since {@mythProtoVersion 30})
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_CONTRAST QUERY_RECORDER_CHANGE_CONTRAST
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */	
	public Integer changeContrast(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException;
	
	/**
	 * Changes the brightness of a recording.
	 * 
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_BRIGHTNESS QUERY_RECORDER_CHANGE_BRIGHTNESS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */		
	public Integer changeBrightness(boolean up) throws IOException;	
	
	/**
	 * Changes the brightness value.
	 * 
	 * @param adjustmentType
	 * 		the adjustment type (Since {@mythProtoVersion 30})
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_BRIGHTNESS QUERY_RECORDER_CHANGE_BRIGHTNESS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */		
	public Integer changeBrightness(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException;
	
	/**
	 * Changes the hue value of a recording.
	 * 
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_HUE QUERY_RECORDER_CHANGE_HUE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */		
	public Integer changeHue(boolean up) throws IOException;	
	
	/**
	 * Changes the hue value.
	 * 
	 * @param adjustmentType
	 * 		the adjustment type (Since {@mythProtoVersion 30})
	 * @param up
	 * 		the direction, {@code true} means increase.
	 * @return
	 * 		the new value
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CHANGE_HUE QUERY_RECORDER_CHANGE_HUE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */		
	public Integer changeHue(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException;
	
	/**
	 * Gets the maximum bitrate a recorder can output.
	 * <p>
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_MAX_BITRATE QUERY_RECORDER_GET_MAX_BITRATE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public long getMaxBitrate() throws IOException;
	
	/**
	 * Sets the signal monitoring rate.
	 * <p>
	 * 
	 * <h4>Usage Note:</h4>
	 * <b>ATTENTION:</b> this can only be set in LiveTV Mode. You also need to {@link #pause()} the 
	 * recorder before calling this function. 
	 * 
	 * @param rate 
	 * 		Milliseconds between each signal check, 0 to disable, -1 to preserve old value.
	 * @param notifyFrontend 
	 * 		If 1 SIGNAL messages are sent to the frontend, if 0 SIGNAL messages will not be sent, and if -1 the old value is preserved.
	 * @return 
	 * 		Old rate if it succeeds, -1 if it fails.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 18}
	 *        
	 * @see IMythCommand#QUERY_RECORDER_SET_SIGNAL_MONITORING_RATE QUERY_RECORDER_SET_SIGNAL_MONITORING_RATE
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_18,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6847")
	})
	public int setSignalMonitoringRate(int rate, boolean notifyFrontend) throws IOException;
	
	/**
	 * Tells the recorder to pause a recorder.
	 * <p>
	 * 
	 * @return
	 * 		{@code true} if the recorder respond with {@code OK}.
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_PAUSE QUERY_RECORDER_PAUSE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public boolean pause() throws IOException;
	
	/**
	 * Tells the recorder to cancel or continue the next recording.
	 * <p>
	 * This is used when the user is watching "Live TV" and does not 
	 * want to allow the recorder to be taken for a pending recording.
	 * 
	 * @param cancel
	 * 		if the next recording should be canceled or continued (Since {@mythProtoVersion 23}).
	 * 
	 * @throws IOException 
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_CANCEL_NEXT_RECORDING QUERY_RECORDER_CANCEL_NEXT_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public boolean cancelNextRecording(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_23)
		Boolean cancel
	) throws IOException;

	/**
	 * Changes LiveTV recording directory.
	 * 
	 * @param path
	 * 		the new path
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SET_NEXT_LIVETV_DIR SET_NEXT_LIVETV_DIR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	public boolean setNextLiveTvDirectory(String path) throws IOException;
	
	/**
	 * Returs the byte position for the given frame number.
	 * 
	 * @param frameNumber
	 * 		the desired frame number.
	 * @return
	 * 		the frame pisition in bytes or {@code -1}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_GET_KEYFRAME_POS QUERY_RECORDER_GET_KEYFRAME_POS
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	public long getKeyframePosition(long frameNumber) throws IOException;
	
	/**
	 * Returns the keyframe position map.
	 * <p>
	 * <h3>Protocol Version Hint:</h3>
	 * For protocol version prior to {@mythProtoVersion 43}, only 32 bit integer values are supported as frame-numbers.<br>
	 * If this fuction is valled with larger frame numbers {@link #getKeyframePosition(long)} is used instead, if no more
	 * than 1000 frames should be tested, otherwise this function call returns an empty list. 
	 * 
	 * 
	 * @param frameNumberStart
	 * 		the starting frame number
	 * @param frameNumberEnd
	 * 		the ending frame number
	 * @return
	 * 		a map containing the keyframe number as key and the byte position of the key frame as value
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDER_FILL_POSITION_MAP QUERY_RECORDER_FILL_POSITION_MAP
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	public Map<Long,Long> fillPositionMap(long frameNumberStart, long frameNumberEnd) throws IOException;

	/**
	 * @see IMythCommand#QUERY_RECORDER_FILL_DURATION_MAP QUERY_RECORDER_FILL_DURATION_MAP
	 * 
	 * @since {@mythProtoVersion 77}
	 * @mythProtoVersionRange
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_77)
	public Map<Long,Long> fillDurationMap(long start, long end) throws IOException;
}