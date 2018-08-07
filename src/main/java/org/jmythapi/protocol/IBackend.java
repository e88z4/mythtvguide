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

import static org.jmythapi.protocol.ProtocolVersion.*;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IGuideDataThrough;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.ISetting;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.events.IDownloadFileFinished;
import org.jmythapi.protocol.events.IDownloadFileUpdate;
import org.jmythapi.protocol.events.IMythEvent;
import org.jmythapi.protocol.events.IMythEventListener;
import org.jmythapi.protocol.events.IMythEventPacketListener;
import org.jmythapi.protocol.events.IPixmapGenerated;
import org.jmythapi.protocol.events.IVideoList;
import org.jmythapi.protocol.events.IVideoListChange;
import org.jmythapi.protocol.events.IVideoListNoChange;
import org.jmythapi.protocol.request.EPlaybackSockEventsMode;
import org.jmythapi.protocol.request.ERecordingsType;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.*;
import org.jmythapi.protocol.response.impl.FileStatus;
import org.jmythapi.protocol.response.impl.FileTransfer;
import org.jmythapi.utils.OpenSubtitlesHasher;

/**
 * This interface is the main interface when communicating with a MythTV-backend.
 * It allows to connect and to send commands to a MythTV-backend.
 * <p>
 * See the <a href='#usage'>usage examples</a> for a list of things you can do with a backend.
 * 
 * <h3>Supported Commands:</h3>
 * 
 * The following page contains a list of commands supported by a MythTV-backend: {@link IMythCommand commands}.<br>
 * You can either send commands directly to a backend using a {@link IBackendConnection}, 
 * or you can use the more convenient functions provided by this backend class.
 * Depending on the protocol version, different commands with different request arguments and response values are supported. 
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
 * <h3><a name='start'>How to start:</a></h3>
 * <i>Connect to the backend:</i><br>
 * There are a few mandatory steps to connect to a MythTV backend:
 * <ol>
 * 	<li>Create a new backend object:<br>
 *  	{@code IBackend backend = BackendFactory.createBackend(...)}
 *  </li>
 * 	<li>Establish a connection to the backend:<br>
 * 		{@code backend.connect()}
 * </li>
 * 	<li>Register the client to the backend:
 * 		<ul>
 * 			<li>Register as playback client:<br>
 * 				{@code backend.annotatePlayback(...)}
 *			</li>
 * 			<li>Register as monitoring client:<br>
 * 				{@code backend.annotateMonitor(...)}
 *			</li>
 * 		</ul>
 * 		A playback connection blocks the shutdown of the backend whereas a monitoring does not. 
 * </li>
 * <li>Use recorders:<br>
 * 	   See <a href='#usage_recorder'>here</a>, if you need to work with {@link IRecorder recorders}.
 *  </li>
 * </ol>
 * <p>
 * <i>Use the backend:</i><br>
 * Afterwards you can use any function provided by this interface to query and control your backend.<br/>
 * See the <a href='#usage'>usage examples</a> for some examples.
 * </p>
 * 
 * <p>
 * <i>Disconnect from the backend:</i><br>
 * To disconnect from the backend, {@code backend.close()} needs to be called.
 * </p>
 * 
 * <h3><a name='usage'>Usage Examples:</a></h3>
 * The following section provides some examples how to use the backend class to send commands and get response.
 * 
 * <h4>Usage Example 1:</h4>
 * <p>
 * The following example shows how a connection can be established to a MythTV-backend and how a list 
 * of all pending recordings can be queried:
 * </p>
 * <!-- TEST COMMENT -->
 * {@mythCodeExample <pre>
 *   // create a backend object
 *   IBackend backend = BackendFactory.createBackend("mythbox");
 *   
 *   // connect to the backend
 *   backend.connect();
 *   System.out.println("The backend is speeking protocol version: " + backend.getVersionNr());
 *   
 *   // register as a playback client
 *   backend.annotatePlayback();
 *   
 *   // query pending recordings
 *   IRecordingsPending pendingRecordings = backend.queryAllPending();
 *   System.out.println(String.format("MythTV has %d pending records", pendingRecordings.size()));
 *   for (IProgramInfo recording : pendingRecordings) &#123;
 *   	System.out.println(recording);
 *   &#125;
 *   
 *   // close backend connection
 *   backend.disconnect();
 * </pre>}
 * 
 * <!-- TEST COMMENT -->
 * 
 * <h4>Usage Example 2:</h4>
 * <p>
 * An example how to connect to the next free {@link IRecorder recorder} and to fetch all channels
 * available on this recorder.
 * </p>
 * {@mythCodeExample <pre>
 *    // create a backend object
 *    IBackend backend = BackendFactory.createBackend("mythbox");
 *    backend.connect();
 *    backend.annotatePlayback();
 *
 *    // searching for the next free recorder
 *    IRecorderInfo recorderInfo = backend.getNextFreeRecorder();
 *    if(recorderInfo == null) &#123;
 *        System.out.println("No free recorder available");
 *    &#125; else &#123;
 *        // connect to the recorder
 *        IRecorder recorder = backend.getRecorder(recorderInfo);
 *        System.out.println("Connected to recorder " + recorder);
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
 *    
 *   // close backend connection
 *   backend.disconnect();
 * </pre>}
 * 
 * <h4><a name='more_usage_examples'>More usage Examples:</a></h4>
 * 
 * See the javadoc for the methods provided by this interface, e.g.
 * <ul>
 * 	 <li><b>Query recordings:</b>
 * 		<ul>
 * 			<li>Get all available recordings ({@link #queryRecordings() link})</li>
 *			<li>Get all pending recordings ({@link #queryAllPending() link})</li>
 *			<li>Get all scheduled recordings ({@link #queryAllScheduled() link})</li>
 *			<li>Get all conflicting recordings ({@link #queryConflicting() link})</li>
 *		</ul>
 *	 </li>
 *
 *   <li><b>Work with a recording:</b>
 *   	<ul>
 *   		<li>Download a recording ({@link #annotateFileTransfer(IProgramInfo) link})</li>
 *   		<li>Delete/Undelete a recording ({@link #deleteRecording link})</li>
 *   		<li>Stop a recording ({@link #stopRecording(IProgramInfo) link})</li>
 *   		<li>Reactivate a recording ({@link #reactivateRecording(IProgramInfo) link})</li>
 *   		<li>Update file size of a recording ({@link #fillProgramInfo(IProgramInfo) link})</li>
 *   		<li>Generate recording preview image ({@link #queryGenPixmap(IProgramInfo) link})</li>
 *   		<li>Download recording preview images ({@link #queryPixmap link})</li>
 *   		<li>Check the last modification time of a preview image ({@link #queryPixmapLastModified link})</li>
 *   		<li>Check if a recording file exists ({@link #queryFileExists(IProgramInfo) link})</li>
 *   		<li>Check if a recording is in progress ({@link #checkRecording(IProgramInfo) link})</li>
 *   		<li>Query the bookmark of a recording ({@link #queryBookmark(IProgramInfo) link})</li>
 *   		<li>Set the bookmark of a recording ({@link #setBookmark(IProgramInfo, Long) link})</li>
 *   	</ul>
 *   </li>
 *   
 *   <li><b>Work with files:</b>
 *   	<ul>
 *   		<li>Query free/used disk space ({@link #getFreeSpaceOverview() link})</li>
 *   		<li>List all files in a remote storage group ({@link #queryStorageGroupFileList link})</li>
 *   		<li>Check if a file exists ({@link #queryFileExists(String, String) link})</li>
 *   		<li>Generate a hash for a file ({@link #queryFileHash(String, String) link})</li>
 *   		<li>Download a file ({@link #downloadFile(URI, String, String) link})</li>
 *   		<li>Delete a file ({@link #deleteFile(String, String) link})</li>
 *   	</ul>
 *   </li>
 *   
 *   <li>
 *   	<b>Work with backends:</b>
 *   	<ul>
 *   		<li>Query/Write backend settings ({@link #querySetting link},{@link #setSetting link})</li>
 *   		<li>Force a backend to reload settings ({@link #refreshBackend() link})</li>
 *   		<li>Force a slave backend to go to sleep ({@link #goToSleep() link})</li>
 *   		<li>Force a slave backend to shutdown ({@link #shutdownNow() link})</li>
 *   		<li>Query the hostname of a backend ({@link #queryHostname() link})</li>
 *   		<li>Query the guide-date status of the backend ({@link #queryGuideDataThrough() link})</li>
 *   		<li>Query if a backend is active ({@link #isActiveBackend(String) link})</li>
 *   		<li>Query the load of a backend ({@link #queryLoad() link})</li>
 *   		<li>Query the uptime of a backend ({@link #queryUptime() link})</li>
 *   		<li>Query memory usage of a backend ({@link #queryMemStats() link})</li>
 *   		<li>Query timezone of a backend ({@link #queryTimeZone() link})</li>
 *   	</ul>
 *   </li>
 *   
 *   <li><b>Work with channels:</b>
 *   	<ul>
 *   		<li>Get a list of available channels ({@link #getChannelInfos() link})</li>
 *   		<li>Get the programs for all channels ({@link #getNextProgramInfos link})</li>
 *   	</ul>
 *   </li>
 *   
 *   <li><b><a name='usage_recorder'>Work with {@link IRecorder recorders}:</a></b>
 *   	<ul>
 *   		<li>Get all available recorders ({@link #getRecorders link})</li>
 *   		<li>Get all free recorders ({@link #getFreeRecorders link})</li>
 *   		<li>Get the next free recorder ({@link #getNextFreeRecorder link})</li>
 *   		<li>Get the amount of free recorders ({@link #getFreeRecorderCount() link})</li>
 *   		<li>Get a recorder by ID ({@link #getRecorderForNum link})</li>
 *   		<li>Get the recorder recording a program ({@link #getRecorderForProgram link})</li>
 *   		<li>Connect to a recorder ({@link #getRecorder(IRecorderInfo) link})</li>
 *   		<li>Lock/Free a tuner ({@link #lockTuner() link},{@link #freeTuner(Integer) link})</li>
 *   	</ul>
 *   </li>
 * </ul>
 * 
 * @see ProtocolVersion All supported protocol version
 */
@SuppressWarnings("deprecation")
public interface IBackend extends Closeable {

	/**
	 * Get the connection that was established to the backend.
	 * @return 
	 * 		the established connection or {@code null} if no connection was established so far
	 */
	public IBackendConnection getCommandConnection();

	/**
	 * Get the host name of the backend the client is connected to.
	 * 
	 * @return 
	 * 		the host name of the backend
	 */
	public String getHostName();
	
	/**
	 * Get the connection port.
	 * 
	 * @return 
	 * 		the port of the backend
	 */
	public int getHostPort();

	/**
	 * Get the MythTV protocol-version the backend is speaking.
	 * 
	 * @return 
	 * 		the protocol version the backend is speaking.
	 */
	public ProtocolVersion getVersionNr();

	/**
	 * Sets the initial protocol-version that should be used for 
	 * handshake.
	 * 
	 * @param protoVersion
	 * 		the initial protocol version
	 */
	public void setInitialVersionNr(ProtocolVersion protoVersion);
	
	/**
	 * Establishes a new connection to the backend.
	 * <p>
	 * Once the connection is established, the protocol-version is negotiated.
	 * 
	 * @throws IOException
	 * 		on communication errors
	 */
	public void connect() throws IOException;
	
	public void connect(Integer connectionTimeout) throws IOException;

	/**
	 * Disconnects from the backend.
	 */
	public void disconnect();
	
	/**
	 * Queries the current connection status.
	 * 
	 * @return
	 * 		{@code true} if a connection was established.
	 */
	public boolean isConnected();

	/**
	 * Announce a new playback connection to the backend.
	 * <p>
	 * This signals that backend that a new client will start playback and prevents it from
	 * shutting down the socket.
	 *  
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_PLAYBACK ANN_PLAYBACK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public boolean annotatePlayback() throws IOException;

	@MythProtoVersionAnnotation(from = PROTO_VERSION_85)
	public boolean annotateFrontend() throws IOException;

	/**
	 * Announce a new playback connection to the backend.
	 * <p>
	 * This signals that backend that a new client will start playback and prevents it from
	 * shutting down the socket.
	 * <p>
	 * @param clientHostName
	 * 		the name of the client
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_PLAYBACK ANN_PLAYBACK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public boolean annotatePlayback(String clientHostName) throws IOException;

	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public boolean annotateFrontend(String clientHostName) throws IOException;

	/**
	 * Announce a new playback connection to the backend.
	 * <p>
	 * This signals that backend that a new client will start playback and prevents it from
	 * shutting down the socket.
	 * 
	 * @param clientHostName 
	 * 		the name of this client
	 * @param eventsMode 
	 * 		the type of events the client is interested in.
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_PLAYBACK ANN_PLAYBACK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public boolean annotatePlayback(String clientHostName, EPlaybackSockEventsMode eventsMode) throws IOException;

	@MythProtoVersionAnnotation(from = PROTO_VERSION_85)
	public boolean annotateFrontend(String clientHostName, EPlaybackSockEventsMode eventsMode) throws IOException;

	/**
	 * Announces a new monitor connection to the backend.
	 * <p>
	 * This signals that backend that a new client will start querying the backend but does not prevents it from
	 * shutting down the socket.
	 * 
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_MONITOR ANN_MONITOR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 22}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_22)
	public boolean annotateMonitor() throws IOException;
	
	/**
	 * Announces a new monitor connection to the backend.
	 * <p>
	 * This signals that backend that a new client will start querying the backend but does not prevents it from
	 * shutting down the socket.
	 * 
	 * @param clientHostName
	 * 		the host name of the client
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_MONITOR ANN_MONITOR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 22}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_22)
	public boolean annotateMonitor(String clientHostName) throws IOException;

	/**
	 * Announces a new monitor connection to the backend.
	 * <p>
	 * This signals that backend that a new client will start querying the backend but does not prevents it from
	 * shutting down the socket.
	 * 
	 * @param clientHostName 
	 * 		the name of this client
	 * @param eventsMode 
	 * 		the type of events the client is interested in.
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_MONITOR ANN_MONITOR
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 22}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_22)
	public boolean annotateMonitor(String clientHostName, EPlaybackSockEventsMode eventsMode) throws IOException;
	
	/**
	 * Announce a new file-transfer connection to the backend.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    // getting previously recorded programs
	 *    IProgramInfoList programs = backend.queryRecordings();
	 *    
	 *    // getting the recording to transfer (we just use the first recording)
	 *    IProgramInfo program = programs.get(0);
	 *    		
	 *    // annotate a new file transfer
	 *    IFileTransfer transfer = backend.annotateFileTransfer(program);	
	 *    if (transfer.isOpen()) &#123;
	 *       // set fast timeout
	 *       transfer.setTimeout(true);
	 *       
	 *       // copy data
	 *       File tempFile = File.createTempFile("fileTransfer", ".mpg");
	 *       transfer.transferTo(tempFile);
	 *    &#125;
	 *    		
	 *    // close file transfer
	 *    transfer.close();
	 * </pre>}
	 * 
	 * @param programInfo 
	 * 		the program, whose file should be transfered
	 * @return
	 * 		a file-transfer object that can be used to fetch the data.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #annotateFileTransfer(String, Boolean, Integer, Integer, String)
	 * @see IMythCommand#ANN_FILE_TRANSFER ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IFileTransfer annotateFileTransfer(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Announce a new file-transfer connection to the backend.
	 * <p>
	 * It reads the file-name and storage-group from the given program-info object and calls 
	 * {@link #annotateFileTransfer(String, Boolean, Integer, Integer, String)}.
	 * 
	 * @param programInfo 
	 * 		the program, whose file should be transfered
	 * @param useReadAhead 
	 * 		if the backend should read ahead the requested location via a separate thread (since {@mythProtoVersion 29})
	 * @param retries 
	 * 		how many retries should be done before giving up (since {@mythProtoVersion 29}, deprecated {@mythProtoVersion 60})
	 * @param  timeoutMs
	 * 		the timeout to wait for data in milliseconds (since {@mythProtoVersion 60})
	 * @return
	 * 		a file-transfer object that can be used to fetch the data.
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #annotateFileTransfer(String, Boolean, Integer, Integer, String)
	 * @see IMythCommand#ANN_FILE_TRANSFER ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IFileTransfer annotateFileTransfer(
			@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
			IProgramInfo programInfo,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_29) 
			Boolean useReadAhead,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_29,to=PROTO_VERSION_60) 
			Integer retries,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_60) 
			Integer timeoutMs
	) throws IOException;

	/**
	 * Announce a new file-transfer connection to the backend.
	 * <p>
	 * This method calls {@link URI#getUserInfo()} and {@link URI#getPath()} to determine the storage-group and file name
	 * and calls {@link #annotateFileTransfer(String, Boolean, Integer, Integer, String)} afterwards. 
	 * 
	 * @param fileUrl 
	 * 		the URI to the file to transfer
	 * @return
	 * 		a file-transfer object that can be used to fetch the data.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_FILE_TRANSFER ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public FileTransfer annotateFileTransfer(URI fileUrl) throws IOException;	
	
	/**
	 * Announce a new file-transfer connection to the backend.
	 * <p>
	 * This method internally calls {@link #annotateFileTransfer(String, Boolean, Integer, Integer, String)}. 
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *     IBackend backend = ...;      // an already connected backend
	 *     IProgramInfo program = ...;  // a previously recorded program
	 *     
	 *     // the storage group and name of the recording preview image
	 *     String storagGroup = program.getStorageGroup();
	 *     String previewFileName = program.getPreviewImageName();
	 *     
	 *     // transfering the recording preview image to the client
	 *     File targetFile = File.createTempFile("preview", ".png");
	 *     IFileTransfer transfer = backend.annotateFileTransfer(previewFileName,storagGroup);
	 *     transfer.transferTo(targetFile);
	 *     transfer.close();
	 * </pre>}
	 * 
	 * @param fileName 
	 * 		the name of the file to be transfered
	 * @param storageGroup 
	 * 		the name of the storage-group the file is located in (since {@mythProtoVersion 44})
	 * @return
	 * 		a file-transfer object that can be used to fetch the data.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_FILE_TRANSFER ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)	
	public FileTransfer annotateFileTransfer(
		String fileName,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
		String storageGroup
		
	) throws IOException;
	
	/**
	 * Announce a new file-transfer connection to the backend.
	 * <p>
	 * This command must be sent as first command after calling {@link #connect()}.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *     IBackend backend = ...;      // an already connected backend
	 *     IProgramInfo program = ...;  // a previously recorded program
	 *     
	 *     // the storage group and name of the recording preview image
	 *     String storagGroup = program.getStorageGroup();
	 *     String previewFileName = program.getPreviewImageName();
	 *     
	 *     // transfering the recording preview image to the client
	 *     File targetFile = File.createTempFile("preview", ".png");
	 *     IFileTransfer transfer = backend.annotateFileTransfer(previewFileName, Boolean.TRUE, -1, 2000, storagGroup);
	 *     transfer.transferTo(targetFile);
	 *     transfer.close();
	 * </pre>}
	 * 
	 * @param fileName 
	 * 		the name of the file to be transfered
	 * @param storageGroup 
	 * 		the name of the storage-group the file is located in (since {@mythProtoVersion 44})
	 * @param useReadAhead 
	 * 		if the backend should read ahead the requested location via a separate thread. If {@code null} then {@code true} is used. (since {@mythProtoVersion 29})
	 * @param retries 
	 * 		how many retries should be done before giving up. If {@code null} then {@code -1} is used. (since {@mythProtoVersion 29}, deprecated {@mythProtoVersion 60})
	 * @param  timeoutMs
	 * 		the timeout to wait for data in milliseconds. If {@code null} then {@code 2000} is used. (since {@mythProtoVersion 60})
	 * @return
	 * 		a file-transfer object that can be used to fetch the data.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_FILE_TRANSFER ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IFileTransfer annotateFileTransfer(
			@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
			String fileName,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_29) 
			Boolean useReadAhead,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_29,to=PROTO_VERSION_60) 
			Integer retries,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_60) 
			Integer timeoutMs,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_44) 
			String storageGroup
	) throws IOException;

	/**
	 * Connects to the specified recorder.
	 * <p>
	 * This method and returns a recorder object which allows to send command to the recorder.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ....; // an already connected backend
	 * 
	 *    // getting the next free recorder
	 *    IRecorderInfo recorderInfo = backend.getNextFreeRecorder();
	 *    if(recorderInfo != null) &#123;
	 *       // connect to the recorder
	 *       IRecorder recorder = backend.getRecorder(recorderInfo);
	 *       
	 *       // send commands to the recorder ...
	 *       
	 *       // e.g., getting all available channel 
	 *       List&lt;IBasicChannelInfo&gt; channels = recorder.getBasicChannelInfos();
	 *       for(IBasicChannelInfo channel : channels) &#123;
	 *          System.out.println(String.format("Channel %d: %s",channel.getChannelID(),channel.getChannelSign()));
	 *       &#125;
	 *    &#125; else &#123;
	 *       System.out.println("No free recorder available");
	 *    &#125;
	 * </pre> }
	 * 
	 * @param recorderInfo
	 * 		informations about the recorder
	 * @return
	 * 		the connected recorder
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IRecorder getRecorder(IRecorderInfo recorderInfo) throws IOException;

	/**
	 * A function to get an info about the next free recorder.
	 * <p>
	 * 
	 * @see IMythCommand#GET_NEXT_FREE_RECORDER GET_NEXT_FREE_RECORDER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 03}
	 * @deprecated {@mythProtoVersion 87}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_03, to=PROTO_VERSION_87)
	public IRecorderInfo getNextFreeRecorder() throws IOException;

	/**
	 * A function to get the next free recorder.
	 * Searching is started with the given recorder-ID.
	 * 
	 * @param currentRecorderID 
	 * 		the current recorder id
	 * @return 
	 * 		info about the next free recorder or {@code null} if no recorder is available.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_NEXT_FREE_RECORDER GET_NEXT_FREE_RECORDER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 03}
	 * @deprecated {@mythProtoVersion 87}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_03, to = PROTO_VERSION_87)
	public IRecorderInfo getNextFreeRecorder(Integer currentRecorderID) throws IOException;

	/**
	 * A function to get the next free recorder.
	 * Searching is started with the given recorder.
	 * 
	 * @param currentRecorder 
	 * 		the current recorder
	 * @return 
	 * 		info about the next free recorder or {@code null} if no recorder is available.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_NEXT_FREE_RECORDER GET_NEXT_FREE_RECORDER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_03)	
	public IRecorderInfo getNextFreeRecorder(IRecorderInfo currentRecorder) throws IOException;	
	
	/**
	 * A function to get the next free {@link IRecorderInfo recorder}.
	 * <p>
	 * This is similar to {@link #getNextFreeRecorder()}, except that it tries to get a recorder on the local machine if possible.
	 * 
	 *  
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_FREE_RECORDER GET_FREE_RECORDER
	 *
	 * @deprecated {@mthprotoversion 87}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00, to = PROTO_VERSION_87)
	public IRecorderInfo getFreeRecorder() throws IOException;

	/**
	 * Gets the next free recorder that is capable to display the requested channel.
	 * 
	 * @param channelNumber
	 * 		the desired channel
	 * @return
	 * 		the next free recorder or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * @since {@mythProtoVersion 03}
	 * 
	 * @see IRecorder#checkChannel(String)
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public IRecorderInfo getFreeRecorder(String channelNumber) throws IOException;
	
	/**
	 * A function to get all available recorders, independently of their busy status.
	 * <p>
	 * This function uses {@link IBackend#getRecorderForNum(int) getRecorderForNum}, starting with recorder-index {@code 1}, 
	 * to determine all available recorders.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #getFreeRecorders() getFreeRecorders} to only get free recorders.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // determine all available recorders
	 *    List&lt;IRecorderInfo&gt; recorderInfos = backend.getRecorders();
	 *    
	 *    // print out the recorder connection infos
	 *    System.out.println(String.format("%d recorders found:",recorderInfos.size()));
	 *    for(IRecorderInfo recorderInfo : recorderInfos) &#123;
	 *       System.out.println(String.format(
	 *          "%02d: %s:%d",
	 *          recorderInfo.getRecorderID(),
	 *          recorderInfo.getHostName(),
	 *          recorderInfo.getHostPort()
	 *       ));
	 *    &#125;
	 * </pre>}
	 * <br>
	 * The above example will output, e.g. 
	 * <pre>
	 * 3 recorders found:
	 * 01: 192.168.10.202:6543
	 * 02: 192.168.10.202:6543
	 * 03: 192.168.10.202:6543
	 * </pre>
	 * 
	 * @return
	 * 		a list of all available (but not necessarily free) recorders.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_RECORDER_FROM_NUM GET_RECORDER_FROM_NUM
	 * @see IBackend#getRecorderForNum(int)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public List<IRecorderInfo> getRecorders() throws IOException;
	
	/**
	 * Returns the IDs of all available recorders. 
	 * <p>
	 * This method returns all recorders, independently of their busy status.<br>
	 * The returned IDs can be used as argument for function {@link #getRecorderForNum(int) getRecorderForNum} to 
	 * fetch further information about the recorders.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #getFreeRecorderIDs() getFreeRecorderIDs} to only get the IDs of all free recorders.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // get the IDs of all available (busy or idle) recorders
	 *    int[] allRecorderIDs = backend.getRecorderIDs();
	 *    System.out.println(String.format(
	 *       "%d recorders are available:", allRecorderIDs.length
	 *    ));
	 *    
	 *    for(int recorderId : allRecorderIDs) &#123;
	 *       // get the recorder info
	 *       IRecorderInfo recorderInfo = backend.getRecorderForNum(recorderId);
	 *       
	 *       // connect to the recorder
	 *       IRecorder recorder = backend.getRecorder(recorderInfo);
	 *       
	 *       // get the recorder status
	 *       boolean isRecording = recorder.isRecording();
	 *       System.out.println(String.format(
	 *          "Recorder %d is currently %s",
	 *          recorderId,
	 *          isRecording?"busy":"idle"
	 *       ));
	 *    &#125;
	 * </pre>}
	 * <br>
	 * The above example will output, e.g.:
	 * <pre>
	 * 3 recorders are available:
	 * Recorder 1 is currently busy
	 * Recorder 2 is currently idle
	 * Recorder 3 is currently idle
	 * </pre>
	 * 
	 * @return 
	 * 		the IDs of all available recorders.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_RECORDER_FROM_NUM GET_RECORDER_FROM_NUM
	 * @see #getRecorders()
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public int[] getRecorderIDs() throws IOException;
	
	/**
	 * Returns the recorder-info for the recorder, specified by id.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // getting recorder info by recorder-id
	 *    IRecorderInfo recorderInfo = backend.getRecorderForNum(recorderId);
	 *    
	 *    // connect to the recorder
	 *    IRecorder recorder = backend.getRecorder(recorderInfo);
	 *    
	 *    // getting the currently active recorder input and channel			
	 *    String recorderInput = recorder.getInput();
	 *    IBasicChannelInfo recorderChannel = recorder.getCurrentChannel();
	 *    System.out.println(String.format(
	 *       "Recorder %d is using input: %s (channel: %s).",
	 *       recorderId, recorderInput, recorderChannel.getChannelSign()
	 *    ));
	 * </pre>}
	 * <br>
	 * The above example will output, e.g.:
	 * <pre>
	 * Recorder 1 is using input: Tuner 1 (channel: PRO7).
	 * </pre>
	 * 
	 * @param recorderId
	 * 		the recorder id
	 * @return 
	 * 		info about the recorder or {@code null} if no recorder was found.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_RECORDER_FROM_NUM GET_RECORDER_FROM_NUM
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IRecorderInfo getRecorderForNum(int recorderId) throws IOException;

	/**
	 * Determines the recorder, which is currently recording the given program.
	 * <p>
	 * 
	 * @param programInfo
	 * 		the recording
	 * @return 
	 * 		the recorder-info object about the recorder, that is currently recording the given program,
	 * 		or {@code null} if no recorder was found.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_RECORDER_NUM GET_RECORDER_NUM
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IRecorderInfo getRecorderNum(IProgramInfo programInfo) throws IOException;

	/**
	 * Function to determine the amount of free recorders.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to {@mythProtoVersion 09}, then {@link #getFreeRecorderIDs()} is called to determine
	 * the amount of free recorders.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // get the amount of free recorders
	 *    int freeRecorderCount = backend.getFreeRecorderCount();
	 *    System.out.println(String.format(
	 *       "%d free recorders found.",
	 *       freeRecorderCount
	 *    ));
	 * </pre>}
	 * <br>
	 * 
	 * @return the current amount of free recorders.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_FREE_RECORDER_COUNT GET_FREE_RECORDER_COUNT
	 * 
	 * @deprecated {@mythprotoversion 87}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_09, fromFallback=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public int getFreeRecorderCount() throws IOException;

	/**
	 * Gets the IDs of all free recorders.
	 * <p>
	 * This function returns a list of free recorders IDs, or an empty list if none. The returned IDs can be used as argument for function {@link #getRecorderForNum(int)}
	 * to fetch further information about the recorders.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #getRecorderIDs() getRecorderIDs} to get the id of all (busy or idle) recorders.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to protocol-version {@mythProtoVersion 17},
	 * then it uses {@link #getNextFreeRecorder(Integer)} to determine the IDs of all free recorders.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // query the IDs of all free recorders
	 *    int[] freeRecorderIDs = backend.getFreeRecorderIDs();
	 *    System.out.println(String.format(
	 *       "The following recorders are ready to use: %s",
	 *       Arrays.toString(freeRecorderIDs)
	 *    ));
	 * </pre>}
	 * <br>
	 * 
	 * @return the IDs of all currently free recorders.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_FREE_RECORDER_LIST GET_FREE_RECORDER_LIST
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 87}
	 * @deprecated {@mythProtoVersion 87}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_17, fromFallback = PROTO_VERSION_03, to = PROTO_VERSION_87)
	public int[] getFreeRecorderIDs() throws IOException;

	/**
	 * Determines if there are any free recorders.
	 * <p>
	 * This function used {@link #getFreeRecorderCount() getFreeRecorderCount} to determine if there is 
	 * at lease one free recorder available.
	 * 
	 * @return <code>true</code> if there is at least one free recorder available
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_FREE_RECORDER_COUNT GET_FREE_RECORDER_COUNT
	 * 
	 * @deprecated
	 * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_03, to = PROTO_VERSION_87)
	public boolean hasFreeRecorders() throws IOException;

	/**
	 * Gets the recorder-info objects of all free recorders.
	 * <p>
	 * This method internally uses {@link #getFreeRecorderIDs()} to determine
	 * all free recorders and {@link #getRecorderForNum(int)} to fetch the information
	 * for a recorder.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #getRecorders() getRecorders} to get all (busy or idel) recorders.
	 * 
	 * @return
	 * 		a list of free recorders
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GET_FREE_RECORDER_LIST GET_FREE_RECORDER_LIST
	 * @see IMythCommand#GET_RECORDER_FROM_NUM GET_RECORDER_FROM_NUM
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 03}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public List<IRecorderInfo> getFreeRecorders() throws IOException;	
	
	/**
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE BACKEND_MESSAGE_RECORDING_LIST_CHANGE
	 */
	public void recordingListChange() throws IOException;

	/**
	 * XXX: is this event really required for a client?
	 *  
	 * @param chainId TODO
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#BACKEND_MESSAGE_LIVETV_CHAIN BACKEND_MESSAGE_LIVETV_CHAIN
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_20)
	public void liveTvChainUpdate(String chainId) throws IOException;

	/**
	 * Queries for all available recordings.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryAllScheduled() queryAllScheduled} to get all scheduled recordings.<br>
	 * Use {@link #queryAllPending() queryAllPending} to get all pending recordings.<br>
	 * Use {@link #queryConflicting() queryConflicting} to get all conflicting recordings.<br>
	 * Use {@link #queryExpiring() queryExpiring} to get all expiring recordings.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Prior to protocol version {@mythProtoVersion 19} the {@link IProgramInfo#getRecordingStatus() recording-status} 
	 * was not set properly by the MythTV-backend. For this protocol versions, this function additionally uses 
	 * {@link #queryAllPending()} to determine, which of the returned recordings are still in progress. 
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // query all available recordings
	 *    IProgramInfoList allRecordings = backend.queryRecordings();
	 *    
	 *    // print the channel, length, file-size and title of all recordings
	 *    System.out.println("Channel | Length  | Size     | Title");
	 *    for(IProgramInfo program : allRecordings) &#123;
	 *       // print out the found recodings
	 *       System.out.println(String.format(
	 *          "%-5s | %3d min | %8s | %s",
	 *          program.getChannelSign(),
	 *          program.getDuration(),
	 *          EncodingUtils.getFormattedFileSize(Locale.ENGLISH,program.getFileSize()),
	 *          program.getFullTitle()
	 *       ));			
	 *    &#125;
	 * </pre>}
	 * <p>
	 * The above example will output, e.g. 
	 * <pre>
	 * Channel | Length  | Size     | Title
	 * ATV+    |  55 min |  3.51 GB | Dr. Quinn - &Auml;rztin aus Leidenschaft - F&uuml;r das Leben der Cheyenne
	 * RTL2    |  30 min |  2.34 GB | King of Queens - Wer sch&ouml;n sein will ...
	 * RTL2    |  30 min |  2.34 GB | King of Queens - Das Haus am See
	 * </pre>
	 * 
	 * @return 
	 * 		all found recordings
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDINGS QUERY_RECORDINGS
	 * @see IMythCommand#QUERY_ISRECORDING QUERY_ISRECORDING
	 * @see IMythCommand#QUERY_GETALLPENDING QUERY_GETALLPENDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IProgramInfoList queryRecordings() throws IOException;

	/**
	 * Queries for recordings with the given recording-type.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryAllScheduled() queryAllScheduled} to get all scheduled recordings.<br>
	 * Use {@link #queryAllPending() queryAllPending} to get all pending recordings.<br>
	 * Use {@link #queryConflicting() queryConflicting} to get all conflicting recordings.<br>
	 * Use {@link #queryExpiring() queryExpiring} to get all expiring recordings.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Prior to protocol version {@mythProtoVersion 19} the {@link IProgramInfo#getRecordingStatus() recording-status} 
	 * was not set properly by the MythTV-backend. For this protocol versions, this function additionally uses 
	 * {@link #queryAllPending()} to determine, which of the returned recordings are still in progress. 
	 * 
	 * 
	 * @param eRecordingsType 
	 * 		the type of the requested recordings. Use {@code null} or {@link ERecordingsType#Play} to query all available recordings.
	 * @return 
	 * 		all found recordings for the given type
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDINGS QUERY_RECORDINGS
	 * @see IMythCommand#QUERY_ISRECORDING QUERY_ISRECORDING
	 * @see IMythCommand#QUERY_GETALLPENDING QUERY_GETALLPENDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IProgramInfoList queryRecordings(ERecordingsType eRecordingsType) throws IOException;

	/**
	 * Generates a preview image of the requested show.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryPixmapLastModified(IProgramInfo) queryPixmapLastModified} to query the last modified date of the pixmap.<br>
	 * Use {@link #queryPixmap(IProgramInfo) queryPixmap} to download the pixmap file.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called with a protocol version greater than {@mythProtoVersion 60}, 
	 * then this method calls {@link #queryGenPixmap2 queryGenPixmap2(...)}.
	 *  
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *      IBackend backend = ....;    // an already connected backend
	 *      IProgramInfo program = ...; // a previously recorded program
	 * 
	 *      // generate the preview image
	 *      if (backend.queryGenPixmap(program) &#123;
	 *          // getting the preview image name
	 *          String imageName = program.getPreviewImageName();
	 *          
	 *          // create a temp file to store the preview image
	 *          File targetFile = File.createTempFile("preview", ".png");
	 *          
	 *          // copy the png to the client
	 *          IFileTransfer transfer = backend.annotateFileTransfer(imageName, null, null, null);
	 *          transfer.transferTo(targetFile);
	 *          transfer.close();
	 *          
	 *          // load the image
	 *          BufferedImage image = ImageIO.read(targetFile);
	 *          System.out.println(String.format(
	 *          	"Image %s has a size of %dx%d",
	 *          	targetFile.getName(),
	 *          	image.getWidth(), image.getHeight()
	 *          ));
	 *      &#125; else &#123;
	 *      	Sytem.error.println("Preview image generation failed.");
	 *      &#125;
	 * </pre>}
	 * 
	 * @param program
	 * 		the recording, for which a preview image should be generated
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GENPIXMAP QUERY_GENPIXMAP
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 61 toFallback=-1}, replaced by {@link IBackend#queryGenPixmap2 queryGenPixmap2}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_61,toFallback=PROTO_VERSION_LATEST)
	public boolean queryGenPixmap(IProgramInfo program) throws IOException;
	
	/**
	 * Generates a preview image of the requested show.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryPixmapLastModified(IProgramInfo) queryPixmapLastModified} to query the last modified date of the pixmap.<br>
	 * Use {@link #queryPixmap(String, String) queryPixmap} to download the pixmap file.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called with a protocol version greater or equal to {@mythProtoVersion 61}, 
	 * then this method calls {@link #queryGenPixmap2 queryGenPixmap2(...)}.
	 * 
	 * @param program
	 * 		the recording, to grab a preview image from
	 * @param inSeconds
	 * 		if true time is in seconds, otherwise it is in frames (since {@mythProtoVersion 37})
	 * @param time
	 * 		the seconds or frames into the video to seek before capturing a frame (since {@mythProtoVersion 37})
	 * @param fileName
	 * 		the filename to use (since {@mythProtoVersion 37})
	 * @param width
	 * 		the preview image width (since {@mythProtoVersion 37})
	 * @param height
	 * 		the preview image height (since {@mythProtoVersion 37})
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GENPIXMAP QUERY_GENPIXMAP
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 61 toFallback=-1}, replaced by {@link IBackend#queryGenPixmap2 queryGenPixmap2}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_61,toFallback=PROTO_VERSION_LATEST)
	public boolean queryGenPixmap(IProgramInfo program,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="bcce4282b98408e822d6cae2944dc42e3d29e926"),
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15029")
		})
		Boolean inSeconds,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="bcce4282b98408e822d6cae2944dc42e3d29e926"),
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15029")
		})
		Long time,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="bcce4282b98408e822d6cae2944dc42e3d29e926"),
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15029")
		})		
		String fileName,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="bcce4282b98408e822d6cae2944dc42e3d29e926"),
				@MythProtoVersionMetadata(key=SVN_COMMIT,value="15029")
			})
		Integer width,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37,fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="bcce4282b98408e822d6cae2944dc42e3d29e926"),
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15029")
		})
		Integer height
	) throws IOException;
	
	/**
	 * Generates a preview image of the requested show.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryPixmapLastModified queryPixmapLastModified} to query the last modified date of the pixmap.<br>
	 * Use {@link #queryPixmap queryPixmap} to download the pixmap.<br>
	 * Use {@link #queryGenPixmap2 queryGenPixmap2} to generate an alternative pixmap.
	 * <p>
	 * Listen to the event {@link IPixmapGenerated} to receive a notification about the finished generation of the pixmap.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called with a protocol version less than {@mythProtoVersion 61}, 
	 * then {@link #queryGenPixmap queryGenPixmap} is called instead.
	 * 
	 * @param token
	 * 		a unique token that is assigned to the generation job.
	 * @param program
	 * 		the recording, for which a preview image should be generated
	 * @return
	 * 		{@code true} on success
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GENPIXMAP2 QUERY_GENPIXMAP2
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 61 fromFallback=0}, replaces {@link #queryGenPixmap(IProgramInfo) queryGenPixmap}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_61,fromFallback=PROTO_VERSION_00)
	public boolean queryGenPixmap2(String token, IProgramInfo program) throws IOException;

	/**
	 * Generates a preview image of the requested show.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryPixmapLastModified queryPixmapLastModified} to query the last modified date of the pixmap.<br>
	 * Use {@link #queryPixmap queryPixmap} to download the pixmap file.
	 * <p>
	 * Listen to the event {@link IPixmapGenerated} to receive a notification about the finished generation of the pixmap.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called with a protocol version less than {@mythProtoVersion 61}, 
	 * then {@link #queryGenPixmap queryGenPixmap} is called instead.
	 * 
	 * @param token
	 * 		a unique token that is assigned to the generation job.
	 * @param program
	 * 		the recording, for which a preview image should be generated
	 * @param inSeconds
	 * 		if true time is in seconds, otherwise it is in frames
	 * @param time
	 * 		the seconds or frames into the video to seek before capturing a frame
	 * @param fileName
	 * 		the filename to use
	 * @param width
	 * 		the preview image width
	 * @param height
	 * 		the preview image height
	 * @return
	 * 		{@code true} on success
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GENPIXMAP2 QUERY_GENPIXMAP2
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 61 fromFallback=0}, replaces {@link #queryGenPixmap(IProgramInfo) queryGenPixmap}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_61,fromFallback=PROTO_VERSION_00)
	public boolean queryGenPixmap2(String token, IProgramInfo program, Boolean inSeconds, Long time, String fileName, Integer width, Integer height) throws IOException;
	
	/**
	 * Queries the last-modified date of the pixmap of a given recording.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryGenPixmap2 queryGenPixmap2} to generate the pixmap.<br>
	 * Use {@link #queryPixmap queryPixmap} to download the pixmap.<br>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...;      // an already connected backend
	 *    IProgramInfo program = ...;  // a previously recorded program
	 *    
	 *    // get the pixmal last modified date
	 *    Date lastMod = backend.queryPixmapLastModified(program);
	 *    System.out.println(String.format(
	 *       "Pixmap of program '%s' was last modified at '%tc'.",
	 *       program.getTitle(),
	 *       lastMod
	 *    ));
	 * </pre>}
	 * <br>
	 * 
	 * @param program
	 * 		the recording whose pixmap should be checked.
	 * @return
	 * 		the last-modified date of the pixmap
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_PIXMAP_LASTMODIFIED QUERY_PIXMAP_LASTMODIFIED
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Date queryPixmapLastModified(IProgramInfo program) throws IOException;
	
	/**
	 * Queries a preview image.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryPixmapLastModified queryPixmapLastModified} to determine the last-modified date of the pixmap.<br>
	 * Use {@link #queryGenPixmap2 queryGenPixmap2} to generate the pixmap.<br>
	 * Use {@link #queryPixmap queryPixmap} do download a pixmap with a different name.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * For version prior to {@mythProtoVersion 49}, this method uses {@link #annotateFileTransfer(String, String)} to download the preview image.
	 * For later versions {@link #queryPixmapIfModified(IProgramInfo, Date, Long)} is used.
	 * 
	 * @param program
	 * 		the recording whose pixmap should be returned
	 * @return
	 * 		the downloaded preview image or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_PIXMAP_GET_IF_MODIFIED
	 * @see IMythCommand#ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IPixmap queryPixmap(IProgramInfo program) throws IOException;
	
	/**
	 * Queries a preview image.
	 * <p>
	 * This function uses {@link #annotateFileTransfer(String, String)} to download the preview image file with the given name.
	 * <p>
	 * See {@link #queryPixmapLastModified(IProgramInfo) queryPixmapLastModified} if you just want to determine the last-modified date of the pixmap.<br>
	 * See {@link #queryGenPixmap2(String, IProgramInfo) queryGenPixmap2} on how to generate the pixmap.<br>
	 * 
	 * @param fileName
	 * 		the name of the pixmap file
	 * @param storageGroup
	 * 		the storage group of the pixmap. If {@code null} then {@code Default} is used.
	 * 	
	 * @return
	 * 		the downloaded preview image or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ANN_FILE_TRANSFER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IPixmap queryPixmap(String fileName, String storageGroup) throws IOException;
	
	/**
	 * Queries a preview image.
	 * <p>
	 * This command can be used to download a previously generated preview image.<br>
	 * Optionally it can be specified that the preview image should only be returned:
	 * <ul>
	 * 	<li>if the pixmap was recently modified</li>
	 * 	<li>if the pixmal size is lower than the specified maximum</li>
	 * </ul>
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryPixmapLastModified queryPixmapLastModified} if you just want to determine the last-modified date of the pixmap.<br>
	 * Use {@link #queryGenPixmap2 queryGenPixmap2} to generate the pixmap.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this funtion is called prior to proto version {@mythProtoVersion 49}, than it uses {@link #queryPixmapLastModified(IProgramInfo) queryPixmapLastModified} to determine
	 * the last modified timestamp of the image, and calls {@link #annotateFileTransfer(String, String) annotateFileTransfer} afterwards to download the image.
	 * 
	 * @param program
	 * 		the recording whose pixmap should be returned
	 * @param lastModifiedDate
	 * 		the last modified date or {@code null} if the preview image should be downloaded 
	 * @param fileSize
	 * 		the maximum file size. If this is {@code 0} only the last modified timestamp is returned.<br>
	 * 		Use {@code null} if you do not care about the size.
	 * @return
	 * 		the downloaded preview image or {@code null} if the size was exceeded or if an error has occured.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_PIXMAP_GET_IF_MODIFIED QUERY_PIXMAP_GET_IF_MODIFIED
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 49 fromFallback=17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49, fromFallback=PROTO_VERSION_17)
	public IPixmap queryPixmapIfModified(IProgramInfo program, Date lastModifiedDate, Long fileSize) throws IOException;
	
	/**
	 * Deletes a recording file.
	 * <p>
	 * This function marks a recording for deletion, following the existing deletion rules, but does not delete the recording-metadata and -history.
	 * 
	 * <h4>Usage Hint</h4>
	 * Use {@link #forceDeleteRecording forceDeleteRecording} for also deleting the recording metadata.<br>
	 * Use {@link #forgetRecording forgetRecording} for also deleting the recording history.<br>
	 * 
	 * <h4>Usage example:</h4>
	 * <p>
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...;      // an already connected backend
	 *    IProgramInfo program = ...;  // a previously recorded program
	 *    
	 *    // delete the recording
	 *    if(backend.deleteRecording(program)) &#123;
	 *       System.out.println(String.format(
	 *          "Recording '%s' successfully deleted.",
	 *          program.getTitle(),
	 *       ));
	 *    &#125; 
	 * </pre>}
	 * <br>
	 * 
	 * @param programInfo
	 * 		the recording to be deleted.
	 * @return	
	 * 		{@code true} if the backend respond with {@code -1}.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DELETE_RECORDING DELETE_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean deleteRecording(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Delets a recording file, recording metadata and recording history.
	 * <p>
	 * This function marks a recording for deletion, following the existing deletion rules.
	 * Optionally this function can delete the metadata of a recording and the recording history.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called under the following conditions ... 
	 * <ul>
	 * 		<li>prior to {@mythProtoVersion 41}</li>
	 * 		<li>prior to {@mythProtoVersion 56} and {@code forceMetadataDelete=true}</li>
	 * 		<li>prior to {@mythProtoVersion 59} and {@code forgetHistory=true}</li>
	 * </ul>
	 * <br>
	 * ... then the following functions are called:
	 * <ul>
	 * 		<li>to delete recording-history: {@link #forgetRecording(IProgramInfo) forgetRecording}</li>
	 * 		<li>to delete recording-metadata and recording: {@link #forceDeleteRecording(IProgramInfo) forceDeleteRecording} (since {@mythProtoVersion 16})</li>
	 * 		<li>to delete recording: {@link #deleteRecording(IProgramInfo) deleteRecording}</li>
	 * </ul>
	 * <a name='protoRangeExample'></a>
	 * 
	 * @param programInfo
	 * 		the recording
	 * @param forceMetadatDelete
	 * 		forces deletion of recording metadata (since {@mythProtoVersion 56})
	 * @param forgetHistory
	 * 		forces deletion of recording history (since {@mythProtoVersion 59})
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DELETE_RECORDING DELETE_RECORDING
	 * 
	 * @since {@mythProtoVersion 41 fromFallback=16}
	 * @mythProtoVersionRange
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_41,fromFallback=PROTO_VERSION_16)
	public boolean deleteRecording(
		IProgramInfo programInfo,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_56)
		Boolean forceMetadatDelete,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		Boolean forgetHistory
	) throws IOException;
		
	/**
	 * Deletes a recording-file.
	 * <p>
	 * This function marks a recording for deletion, following the existing deletion rules, but does not delete the recording metadata and history.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #forceDeleteRecording forceDeleteRecording} for also deleting the recording metadata.<br>
	 * Use {@link #forgetRecording forgetRecording} for also deleting the recording history.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to {@mythProtoVersion 41}, than the recording is queried uses {@link #queryRecording(Integer, Date)},
	 * and {@link #deleteRecording(IProgramInfo)} is called afterwards.
	 * 
	 * @param channelID
	 * 		the channel id of the recording
	 * @param recordingStartTime
	 * 		the recording-start-time
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DELETE_RECORDING DELETE_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 41 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_41,fromFallback=PROTO_VERSION_00)
	public boolean deleteRecording(Integer channelID, Date recordingStartTime) throws IOException;
	
	/**
	 * Delets a recording file, recording metadata and recording history.
	 * <p>
	 * This function marks a recording-file for deletion, following the existing deletion rules.
	 * Optionally this function can delete the metadata of a recording and the recording history.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called under the following conditions ... 
	 * <ul>
	 * 		<li>prior to {@mythProtoVersion 41}</li>
	 * 		<li>prior to {@mythProtoVersion 56} and {@code forceMetadataDelete=true}</li>
	 * 		<li>prior to {@mythProtoVersion 59} and {@code forgetHistory=true}</li>
	 * </ul>
	 * <br>
	 * ... then the following functions are called:
	 * <ul>
	 * 		<li>to query the recording: {@link #queryRecording(Integer, Date) queryRecording}</li>
	 * 		<li>to delete recording-history: {@link #forgetRecording(IProgramInfo) forgetRecording}</li>
	 * 		<li>to delete recording-metadata and recording: {@link #forceDeleteRecording(IProgramInfo) forceDeleteRecording} (since {@mythProtoVersion 16})</li>
	 * 		<li>to delete recording: {@link #deleteRecording(IProgramInfo) deleteRecording}</li>
	 * </ul>
	 * 
	 * @param channelID
	 * 		the channel id of the recording
	 * @param recordingStartTime
	 * 		the recording start time
	 * @param forceMetadatDelete
	 * 		forces deletion of recording metadata (since {@mythProtoVersion 56})
	 * @param forgetHistory
	 * 		forces deletion of recording history (since {@mythProtoVersion 59})
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DELETE_RECORDING DELETE_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 41 fromFallback=16}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_41,fromFallback=PROTO_VERSION_16)
	public boolean deleteRecording(
		Integer channelID, Date recordingStartTime, 
		@MythProtoVersionAnnotation(from=PROTO_VERSION_56)
		Boolean forceMetadatDelete,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		Boolean forgetHistory
	) throws IOException;
	
	/**
	 * Undelets a recording-file.
	 * <p>
	 * This function undeletes a previously deleted recording.
	 * 
	 * <h4>Usage Note:</h4>
	 * Undeleting a recording only works if the MythTV property {@code AutoExpireInsteadOfDelete}
	 * is set to {@code 1} in the MythTV settings table.
	 * 
	 * @param programInfo
	 * 		the recording to undelete
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#UNDELETE_RECORDING UNDELETE_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 36}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_36)
	public boolean undeleteRecording(IProgramInfo programInfo) throws IOException;

	/**
	 * Delets recording history.
	 * <p>
	 * This function deletes the history of a recording but not the recording itself.<br>
	 * The recording is marked as never duplicates, so it can be re-recorded.
	 * <p>
	 * This is similar to the option "Forget Old" on MythWeb.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #forceDeleteRecording forceDeleteRecording} to also delete the recording-file and recording-metadata.<br>
	 * Use {@link #deleteRecording deleteRecording} to only delete the recording.<br>
	 * 
	 * @param programInfo
	 * 		the recording to forget
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#FORGET_RECORDING FORGET_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00} 
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean forgetRecording(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Deletes a recording-file and recording metadata.
	 * <p>
	 * This command forces the backend to delete a recording and its metadata.<br>
	 * See {@link #forgetRecording(IProgramInfo)} to also delete the recording-history.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #forgetRecording forgetRecording} to just delete the recording history.<br>
	 * Use {@link #deleteRecording deleteRecording} to just delete the recording but not the recording metadata.<br>
	 * 
	 * @param programInfo
	 * 		the recording to delete
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#FORCE_DELETE_RECORDING FORCE_DELETE_RECORDING
	 * 
	 * @since {@mythProtoVersion 16}
	 * @mythProtoVersionRange
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_16,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT, value="834fedd51d451da70f40"),
		@MythProtoVersionMetadata(key=SVN_COMMIT, value="6235")
	})
	public boolean forceDeleteRecording(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Reactivates a inactive recording.
	 * <p>
	 * This function allows inactive recordings to be reactivated.
	 * <p>
	 * This can be used fro things like restarting a program that was stopped or 
	 * deleted in progress or starting a program after correcting a low disk space
	 * or tuner busy condition.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Starting with protocol version {@mythProtoVersion 19}, a recording can be only reactivated by
	 * setting the {@code reactivate} property in database table {@code oldrecorded}.
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#REACTIVATE_RECORDING REACTIVATE_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 05}
	 * @deprecated {@mythProtoVersion 19}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_05, to = PROTO_VERSION_19, toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="7300")	
	})
	public boolean reactivateRecording(IProgramInfo programInfo) throws IOException;

	/**
	 * Force MythTV to reschedule all recordings.
	 * <p>
	 * This should be called if you have manually inserted a recording into the
	 * record table.
	 * 
	 * @return 
	 * 		{@code true} if rescheduling command was received successfully.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#RESCHEDULE_RECORDINGS RESCHEDULE_RECORDINGS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 15}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15,
		fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="5156"),
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="e6c87e28e6e54e046882")
		}
	)	
	public Future<Boolean> rescheduleAllRecordings() throws IOException;
	
	/**
	 * Force MythTV to reschedule the given recording.
	 * <p>
	 * This should be called if you have manually inserted a recording into the
	 * record table.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...;      // an already connected backend
	 *    Integer recordingId = ...;   // the id of a modfied scheduling rule
	 *    
	 *    // signal the backend to reschedule the recording and wait for
	 *    // the rescheduling process to finish.
	 *    long timeoutSeconds = 5;
	 *    Future&lt;Boolean&gt; rescheduled = backend.rescheduleRecordings(recordingId);
	 *    if(!rescheduled.get(timeoutSeconds,TimeUnit.SECONDS)) &#123;
	 *       System.err.println(String.format(
	 *          "The backend did not finish rescheduling for recording-id %d within %d %s",
	 *          recordingId, Long.valueOf(timeoutSeconds),TimeUnit.SECONDS
	 *       ));
	 *    &#125; else &#123;
	 *       System.err.println(String.format(
	 *          "Rescheduling for recording-id %d was done successfully.",
	 *          recordingId
	 *       ));
	 *    &#125; 
	 * </pre>}
	 * <br>
	 * @param recordingId 
	 * 		the ID of the recording to reschedule (see {@link IProgramInfo#getRecordingId()}.<br> 
	 *      {@code null} or {@code -1} forces the backend to reschedule all recordings.<br>
	 *      Use {@code 0} if the change isn't specific to a single record entry 
	 *      (e.g. channel or record type priorities)
	 * @return 
	 * 		{@code true} if rescheduling command was transmitted successfully.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#RESCHEDULE_RECORDINGS RESCHEDULE_RECORDINGS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 15}
	 * @deprecated {@mythProtoVersion 73 toFallback=-1}, use {@link #rescheduleRecordingsMatch(Integer, Integer, Integer, Date, String)} instead.
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15,
		fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="5156"),
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="e6c87e28e6e54e046882")
		},
		to=PROTO_VERSION_73, toFallback=PROTO_VERSION_LATEST
	)
	public Future<Boolean> rescheduleRecordings(Integer recordingId) throws IOException;
	
	/**
	 * 
	 * @param recordID
	 * @param sourceID
	 * @param multiplexID
	 * @param maxStartTime
	 * @param reason
	 * @return
	 * @throws IOException
	 * 
	 * @see IMythCommand#RESCHEDULE_RECORDINGS RESCHEDULE_RECORDINGS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 73 fallbackFrom=15} 
	 */
	@MythProtoVersionAnnotation(fromFallback=PROTO_VERSION_15,from=PROTO_VERSION_73,to=PROTO_VERSION_LATEST)	
	public Future<Boolean> rescheduleRecordingsMatch(Integer recordID, Integer sourceID, Integer multiplexID, Date maxStartTime, String reason) throws IOException;	
	
	/**
	 * Gets the free space on the backend.
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Depending on the used MythTV-protocol, this function uses a different myth-protocol command.
	 * <table border="1">
	 * <tr>
	 * 		<th colspan="2">Protocol [min,max)</th>
	 * 		<th rowspan="2">Used function</th>
	 * </tr>
	 * <tr>
	 * 		<th>Min</th>
	 * 		<th>Max</th>
	 * </tr>
	 * <tr>
	 * 		<td>{@mythProtoVersion 00}</td>
	 * 		<td>{@mythProtoVersion 17}</td>
	 * 		<td>{@link #queryFreeSpace()}</td>
	 * </tr>
	 * <tr>
	 * 		<td>{@mythProtoVersion 17}</td>
	 * 		<td>{@mythProtoVersion 32}</td>
	 * 		<td>{@link #queryFreeSpaceList(boolean)}</td>
	 * </tr>
	 * <tr>
	 * 		<td>{@mythProtoVersion 32}</td>
	 * 		<td>-</td>
	 * 		<td>{@link #queryFreeSpaceSummary()}</td>
	 * </tr>
	 * </table>
	 * <p>
	 * 
	 * <h4>Usage Example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    // the connected backend
	 *    IBackend backend = ...;
	 *    
	 *    // query the used space
	 *    IBasicFreeSpace freeSpaceOverview = backend.getFreeSpaceOverview();
	 *    System.out.println(String.format(
	 *       "MythTV has used %d space out of %d.", 
	 *       freeSpaceOverview.getTotalSpace(),
	 *       freeSpaceOverview.getUsedSpace()
	 *    ));
	 * </pre>}
	 * <br>
	 * 
	 * @return
	 * 		the free space overview
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #queryFreeSpace()
	 * @see #queryFreeSpaceList(boolean)
	 * @see #queryFreeSpaceSummary()
	 * @see IMythCommand#QUERY_FREESPACE QUERY_FREESPACE
	 * @see IMythCommand#QUERY_FREE_SPACE_LIST QUERY_FREE_SPACE_LIST
	 * @see IMythCommand#QUERY_FREE_SPACE_SUMMARY QUERY_FREE_SPACE_SUMMARY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IBasicFreeSpace getFreeSpaceOverview() throws IOException;

	/**
	 * Geturns the free space on the backend.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is called for versions greater or equal to {@mythProtoVersion 17}, then {@link #queryFreeSpaceSummary()} is used.
	 * 
	 * @return
	 * 		the free space
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FREESPACE QUERY_FREESPACE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 17 toFallback=-1}, replaced by {@link #queryFreeSpaceList queryFreeSpaceList} and {@link #queryFreeSpaceSummary queryFreeSpaceSummary}
	 */
	@MythProtoVersionAnnotation(to = PROTO_VERSION_17,toFallback=PROTO_VERSION_LATEST)
	public IFreeSpace queryFreeSpace() throws IOException;

	/**
	 * Returns the free disk space on the connected or on all MythTV backends.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // query freespace for all hosts
	 *    IFreeSpaceList freeSpaceList = backend.queryFreeSpaceList(true);
	 *    
	 *    // print the summary
	 *    System.out.println(String.format(
	 *       "MythTV has used %s space out of %s used on %d storage groups.",
	 *       EncodingUtils.getFormattedFileSize(freeSpaceList.getUsedSpace()),
	 *       EncodingUtils.getFormattedFileSize(freeSpaceList.getTotalSpace()),
	 *       freeSpaceList.size()
	 *    ));
	 *    
	 *    // print details for each storage group
	 *    for (IFreeSpaceListEntry entry : freeSpaceList) &#123;
	 *       System.out.println(String.format(
	 *          "- %s : %s space out of %s used",
	 *          entry.getDirectories(),
	 *          EncodingUtils.getFormattedFileSize(entry.getUsedSpace()),
	 *          EncodingUtils.getFormattedFileSize(entry.getTotalSpace())
	 *       ));
	 *    &#125;
	 * </pre>}
	 * The above example will output, e.g.
	 * <pre>
	 * MythTV has used 3,17 GB space out of 3,72 GB used on 2 storage groups.
	 * - [mythbox:/mnt/data/mythpinky] : 3,14 GB space out of 3,58 GB used
	 * - [mythbox:/var/lib/mythtv/livetv, mythbox:/var/lib/mythtv/recordings] : 28,00 MB space out of 137,93 MB used
	 * </pre>
	 * 
	 * @param allhosts 
	 * 		if the disk space status of all backends should be returned. if this is {@code false}
	 * 		only the current connected backend is returned. 
	 * 
	 * @return
	 * 		the disk space for all backends and a additional summary.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FREE_SPACE_LIST QUERY_FREE_SPACE_LIST
	 * @see IMythCommand#QUERY_FREE_SPACE QUERY_FREE_SPACE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_17)
	public IFreeSpaceList queryFreeSpaceList(boolean allhosts) throws IOException;

	/**
	 * Returns the free space on the connected backend.
	 * 
	 * @return
	 * 		the free space summary
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FREE_SPACE_SUMMARY QUERY_FREE_SPACE_SUMMARY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_32)
	public IFreeSpaceSummary queryFreeSpaceSummary() throws IOException;

	/**
	 * Queries the current load of the backend.
	 * 
	 * @return
	 * 		the load information
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_LOAD QUERY_LOAD
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 15}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_15)
	public ILoad queryLoad() throws IOException;

	/**
	 * Queries the uptime of the backend.
	 * 
	 * @return
	 * 		the uptime-info of the backend
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_UPTIME QUERY_UPTIME
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 15}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_15)
	public IUptime queryUptime() throws IOException;

	/**
	 * Queries the current memory state of the backend.
	 * 
	 * @return
	 * 		the current memory state of the backend
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_MEMSTATS QUERY_MEMSTATS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 15}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_15)
	public IMemStats queryMemStats() throws IOException;

	/**
	 * Queries the guide-date status of the backend.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IGuideDataThrough data = backend.queryGuideDataThrough();
	 *    
	 *    System.out.println(String.format(
	 *       "There's guide data until %1$tF %1$tT (%2$d days).",
	 *       data.getDate(),
	 *       EncodingUtils.getMinutesDiff(new Date(), data.getDate()) / 60 / 24
	 *    ));
	 * </pre> }
	 * <p>
	 * 
	 * @return
	 * 		the guide-data status.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GUIDEDATATHROUGH QUERY_GUIDEDATATHROUGH
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 15}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_15, fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="5218")
	})
	public IGuideDataThrough queryGuideDataThrough() throws IOException;

	/**
	 * Queries the current recording status of the backend. The status object contains
	 * the amount of current-recordings and live-tv-session.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // query recording status
	 *    IRecordingStatus recordingStatus = backend.queryIsRecording();
	 *    System.out.println(String.format(
	 *       "MythTV has %d active recordings. %d are Live-TV recordings.", 
	 *       recordingStatus.getActiveRecordings(),
	 *       recordingStatus.getLiveTVRecordings()
	 *    ));
	 * </pre> }
	 * <p>
	 * 
	 * @return
	 * 		the recording status containing the amount of current-recordings and live-tv-session
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_ISRECORDING QUERY_ISRECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IRecordingStatus queryIsRecording() throws IOException;

	/**
	 * Gets all programs that will be recorded soon.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryAllScheduled queryAllScheduled} to get all scheduled recordings.<br>
	 * Use {@link #queryConflicting queryConflicting} to get all conflicting recordings.<br>
	 * Use {@link #queryRecordings queryRecordings} to get all finished recordings.<br>
	 * Use {@link #queryExpiring queryExpiring} to get all expiring recordings.<br>
	 * 
	 * <h4>Usage Example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *       IBackend backend = ....;     // an already connected backend
	 *       
	 *       // get all pending recordings
	 *       IRecordingsPending pendingRecords = backend.queryAllPending();
	 *       for (IProgramInfo program : pendingRecords) &#123;
	 *          // we are only interrested in recordings with "WILL_RECORD" status
	 *          IProgramRecordingStatus recStatus = program.getRecordingStatus();
	 *          if(recStatus != null && !recStatus.hasStatus(Status.WILL_RECORD)) continue;
	 *          
	 *          // print out the found recodings
	 *          System.out.println(String.format(
	 *             "%1$tF %1$tT - %2$s (%3$s)",
	 *             program.getStartDateTime(),
	 *             program.getTitle(),
	 *             program.getChannelSign()
	 *          ));
	 *       &#125;
	 * </pre> }
	 * <p>
	 * The above example will output, e.g. 
	 * <pre>
	 * MythTV has 4 pending records
	 * 2011-04-17 18:10:00 - Die Simpsons (PRO7)
	 * 2011-04-17 20:15:00 - 10.000 BC (PRO7)
	 * 2011-04-18 22:15:00 - Roter Drache (ZDF)
	 * 2011-04-21 20:15:00 - Dr. House (ORF1)
	 * </pre>
	 * 
	 * @return
	 * 		a list of scheduled programs. If {@link IRecordingsPending#size()} returns {@code 0}, 
	 * 		no scheduled programs were found.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GETALLPENDING QUERY_GETALLPENDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IRecordingsPending queryAllPending() throws IOException;

	/**
	 * Gets all scheduled recordings.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryAllPending queryAllPending} to get all pending recordings.<br>
	 * Use {@link #queryConflicting queryConflicting} to get all conflicting recordings.<br>
	 * Use {@link #queryRecordings queryRecordings} to get all finished recordings.<br>
	 * Use {@link #queryExpiring queryExpiring} to get all expiring recordings.<br>
	 * 
	 * <h4>Usage example</h4>
	 * 
	 * {@mythCodeExample <pre>
	 * 
	 *       IBackend backend = ....;     // an already connected backend
	 *       
	 *       // getting all recording schedules
	 *       IRecordingsScheduled scheduledRecords = backend.queryAllScheduled();
	 *       System.out.println(String.format("MythTV has %d recording schedules:", scheduledRecords.size()));
	 *       System.out.println(String.format(
	 *          "%-35s | %-4s | %-7s | %-15s | %-22s | %-15s",
	 *          "Title","Prio","Channel","Recording Group","Type","Storage Group"
	 *       ));
	 *       
	 *       // print all scheduled recordings
	 *       for (IProgramInfo program : scheduledRecords) &#123;
	 *          System.out.println(String.format(
	 *             "%-35s | %4d | %-7s | %-15s | %-22s | %-15s",
	 *             program.getTitle(),
	 *             program.getRecordingPriority(),
	 *             program.getChannelSign(),
	 *             program.getRecordingGroup(),
	 *             program.getRecordingType(),
	 *             program.getStorageGroup()
	 *          ));		
	 *       &#125;
	 * </pre> }
	 * <p>
	 * The above example will output, e.g.
	 * <pre>
	 * MythTV has 6 recording schedules:
	 * Title                               | Prio | Channel | Recording Group | Type                   | Storage Group  
	 * 10.000 BC                           |    0 | PRO7    | Default         | 6=> {FIND_ONE_RECORD}  | Default       
	 * Die Simpsons                        |    0 | PRO7    | Default         | 3=> {CHANNEL_RECORD}   | Default        
	 * Dr. House                           |    0 | ORF1    | Default         | 5=> {WEEKSLOT_RECORD}  | Default        
	 * Fantastic Four: Rise of the Silver  |    0 | ORF1    | Default         | 6=> {FIND_ONE_RECORD}  | Default        
	 * Geisterhaus (Title Suche)           |    0 | PRO7    | Default         | 6=> {FIND_ONE_RECORD}  | Default              
	 * Stieg Larsson (Title Suche)         |    5 | PRO7    | Default         | 4=> {ALL_RECORD}       | Default 
	 * </pre>
	 * 
	 * @return
	 * 		a list of scheduled recordings. If {@link IRecordingsScheduled#size()} returns {@code 0}, 
	 * 		no scheduled programs were found.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GETALLSCHEDULED QUERY_GETALLSCHEDULED
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IRecordingsScheduled queryAllScheduled() throws IOException;

	/**
	 * Gets all programs that will expiring soon.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryAllScheduled queryAllScheduled} to get all scheduled recordings.<br>
	 * Use {@link #queryAllPending queryAllPending} to get all pending recordings.<br>
	 * Use {@link #queryConflicting queryConflicting} to get all conflicting recordings.<br>
	 * Use {@link #queryRecordings queryRecordings} to get all finished recordings.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this method is used prior to {@mythProtoVersion 23}, then {@link #queryRecordings()} is used and the expiring recordings
	 * are filtered using the {@link ProgramInfoFilters#flag(org.jmythapi.protocol.response.IProgramFlags.Flags) program-flag-filter}.
	 * 
	 * <h4>Usage example:</h4>
	 * <br>
	 * In the example shown below we fetch the list of expiring recordings, print their names and the sum of disk space used
	 * by all expiring recordings.
	 * <br> 
	 * {@mythCodeExample <pre>
	 *    // fetch all expiring recordings
	 *    IRecordingsExpiring expiringRecords = backend.queryExpiring();
	 *    long spaceUsed = 0;
	 *    
	 *    // print names of expiring recordings
	 *    System.out.println("Expiring records:");
	 *    for(IProgramInfo program : expiringRecords) &#123;
	 *       spaceUsed += program.getFileSize().longValue();
	 *       System.out.println("- " + program.getFullTitle());
	 *    &#125;
	 *    
	 *    // print disk space used by expiring recordings
	 *    System.out.println(String.format(
	 *       "%s used by %d expiring recordings.",
	 *       EncodingUtils.getFormattedFileSize(spaceUsed),
	 *       expiringRecords.size()
	 *    ));
	 * </pre> }
	 * <p>
	 * The above example will output, e.g. 
	 * <pre>
	 * Expiring records:
	 * - King of Queens - E-Mail f&uuml;r dich
	 * - King of Queens - Wilde Bullen
	 * - WALL-E - Der Letzte r&auml;umt die Erde auf
	 * 9,82 GB used by 3 expiring recordings.
	 * </pre>
	 * 
	 * @return
	 * 		a list of expiring programs. If {@link IRecordingsExpiring#size()} returns {@code 0}, 
	 * 		no expiring programs were found.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GETEXPIRING QUERY_GETEXPIRING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 23 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_23,fromFallback=PROTO_VERSION_00)
	public IRecordingsExpiring queryExpiring() throws IOException;

	/**
	 * Gets all programs that are in conflict which each other.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryAllScheduled queryAllScheduled} to get all scheduled recordings.<br>
	 * Use {@link #queryAllPending queryAllPending} to get all pending recordings.<br>
	 * Use {@link #queryRecordings queryRecordings} to get all finished recordings.<br>
	 * Use {@link #queryExpiring queryExpiring} to get all expiring recordings.<br>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Starting with protocol version {@mythProtoVersion 57}, the backend seens not to return a conflicting recording anymore, without
	 * specifying a recording as input parameter. Therefore starting with version {@mythProtoVersion 57} we all
	 * {@link #queryAllPending pending recordings} and use a {@link ProgramInfoFilters#status status-filter} to get all conflicting recordings.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    // query all conflicting recordings
	 *    IRecordingsConflicting conflictingRecordings = backend.queryConflicting();
	 *    if(!conflictingRecordings.isEmpty()) &#123;
	 *       System.out.println(String.format(
	 *          "%d conflicting records:", 
	 *          conflictingRecordings.size()
	 *       ));
	 *       
	 *       // print all conflicting recordings
	 *       for(IProgramInfo program : conflictingRecordings) &#123;
	 *          System.out.println(String.format(
	 *             "+-- %1$tF %1$tT - %2$s (%3$s)",
	 *             program.getStartDateTime(),
	 *             program.getFullTitle(),
	 *             program.getChannelSign()
	 *          ));
	 *          
	 *          // fetch all recordings the current recording is in conflict with
	 *          IRecordingsConflicting others = backend.queryConflicting(program);
	 *          for(IProgramInfo other : others) &#123;
	 *             System.out.println(String.format(
	 *                "  | %1$tF %1$tT - %2$s (%3$s)",
	 *                other.getStartDateTime(),
	 *                other.getFullTitle(),
	 *                other.getChannelSign()
	 *             ));
	 *          &#125;
	 *       &#125;
	 *    &#125;
	 * </pre> }
	 * 
	 * The above example will output, e.g.
	 * <pre>
	 * 1 conflicting records:
	 * +-- 2012-01-10 09:30:00 - Scrubs - Meine Sitcom (PRO7)
	 *   | 2012-01-10 09:30:00 - Mitten im Leben! (RTL)
	 *   | 2012-01-10 09:30:00 - Das Traumhotel (ARD)
	 * </pre>
	 * 
	 * @return
	 * 		a list of conflicting programs. If {@link IRecordingsConflicting#size()} returns {@code 0}, 
	 * 		no conflicts were found
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GETCONFLICTING QUERY_GETCONFLICTING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecordingsConflicting queryConflicting() throws IOException;
	
	/**
	 * Gets all programs that are in conflict with the given program.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryConflicting() queryConflicting} if you need to check all pending recordings for conflicts.
	 * 
	 * @param programInfo
	 * 		the program to check
	 * @return
	 * 		a list of conflicting programs. If {@link IRecordingsConflicting#size()} returns {@code 0}, 
	 * 		no conflicts were found
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_GETCONFLICTING QUERY_GETCONFLICTING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecordingsConflicting queryConflicting(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Checks if the current backend is active.
	 * <p>
	 * This function internally uses {@link #queryHostname()} to determine the hostname of the
	 * connected backend and thereafter calls {@link #isActiveBackend(String)} with this hostname.
	 * 
	 * @return 
	 * 		{@code true} if the given backend is active.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_IS_ACTIVE_BACKEND QUERY_IS_ACTIVE_BACKEND
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)	
	public boolean isActiveBackend() throws IOException;
	
	/**
	 * Checks if the given backend is active.
	 * <p>
	 * This function can be used on master backends to checks the activation status of 
	 * slave backends, which has been connected to the master using {@link IMythCommand#ANN_SLAVEBACKEND ANN SlaveBackend}.
	 * 
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // the hostname of a backend to check
	 *    String slaveBackendName = "mythbox2";
	 *    
	 *    // checking the is-active status
	 *    boolean isActiveBackend = backend.isActiveBackend(slaveBackendName);
	 *    System.out.println(String.format(
	 *       "The backend '%s' is currently ",
	 *       slaveBackendName,
	 *       (isActiveBackend?"active":"idle")
	 *    ));
	 * </pre>}
	 * <br>
	 * 
	 * @param hostname 
	 * 		the hostname of the backend. This must be the hostname of the mythtv box, 
	 * 		using the IP-address seems not to work
	 * @return 
	 * 		{@code true} if the given backend is active.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_IS_ACTIVE_BACKEND QUERY_IS_ACTIVE_BACKEND
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public boolean isActiveBackend(String hostname) throws IOException;
	
	/**
	 * Queries the names of all active backends.
	 * <p>
	 * This function returns the names of all currently active backends.
	 * 
	 * @return 
	 * 		a list containing the names of all active backends
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_ACTIVE_BACKENDS QUERY_ACTIVE_BACKENDS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 72}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_72)
	public List<String> queryActiveBackends() throws IOException; 

	/**
	 * Forces the backend to reload the backend settings.
	 * 
	 * @return 
	 * 		{@code true} if the backend has reload the backend-settings successfully.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 05}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_05, fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="3412")
	})
	public boolean refreshBackend() throws IOException;

	/**
	 * Checks if a recording-file exists for the given program.
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to protocol version {@mythProtoVersion 49}, 
	 * then function {@link #queryCheckFile queryCheckFile} is used instead.
	 * 
	 * @param programInfo
	 * 		the program for which the file should be searched
	 * @return 
	 * 		detailed information about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #queryFileExists(String, String)
	 * @see IMythCommand#QUERY_FILE_EXISTS QUERY_FILE_EXISTS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 49 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_49,fromFallback=PROTO_VERSION_00)
	public IFileStatus queryFileExists(IProgramInfo programInfo) throws IOException;

	/**
	 * Checks if given file exists on the backend.
	 * <p>
	 * This method calls {@link URI#getUserInfo()} and {@link URI#getPath()} to determine the storage-group and file-name
	 * and calls {@link #queryFileExists(String, String)} afterwards.
	 * 
	 * @param fileUrl
	 * 		the remote file
	 * @return 
	 * 		detailed information about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILE_EXISTS QUERY_FILE_EXISTS
	 * @see #queryFileExists(String, String)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 49 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_49,fromFallback=PROTO_VERSION_00)	
	public FileStatus queryFileExists(URI fileUrl) throws IOException;
	
	/**
	 * Checks if a the named file exists in the named storage group.
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to protocol version {@mythProtoVersion 49}, 
	 * then function {@link IFileTransfer#isOpen()} is used to determine the file exists status.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *      IBackend backend = ....;     // an already connected backend
	 *      IProgramInfo program = ....; // a previously recorded program
	 *      
	 *      // query if the preview image exists
	 *      String previewImageName = program.getPreviewImageName();
	 *      IFileStatus fileStatus = backend.queryFileExists(previewImageName,"Default");	
	 *      if (fileStatus.fileExists()) &#123;
	 *         System.out.println("Preview image found: " + fileStatus.getFilePath());
	 *      &#125; else &#123;
	 *         System.out.println("Preview image not found.");
	 *      &#125;
	 * </pre>}
	 * 
	 * @param fileName 
	 * 		the remote file name
	 * @param storageGroup 
	 * 		the storage group name, or {@code null} to search in the default group
	 * @return 
	 * 		detailed information about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILE_EXISTS QUERY_FILE_EXISTS
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 49 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_49,fromFallback=PROTO_VERSION_00)
	public IFileStatus queryFileExists(String fileName, String storageGroup) throws IOException;

	/**
	 * Checks if a recording file exists that belongs to the given program.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryFileExists queryFileExists} to check the existance of a non recording file, 
	 * e.g. a preview-image or a channel-logo.
	 * 
	 * <h4>Usage Example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *      IBackend backend = ....;       // an already connected backend
	 *      IProgramInfo recording = ....; // a previously recorded program
	 *      
	 *      // check if the file exists
	 *      IFileStatus fileStatus = backend.queryCheckFile(recording);
	 *      if(fileStatus.fileExists()) &#123;
	 *         System.out.println("Recording file found.");
	 *      &#125; else &#123;
	 *         System.out.println("Recording file not found.");
	 *      &#125;
	 * </pre> }
	 * 
	 * @param programInfo 
	 * 		the given program
	 * @param checkSlaves
	 * 		specifies if all slaves should be checked too. (Since {@mythProtoVersion 32})
	 * @return
	 * 		detailed information about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_CHECKFILE QUERY_CHECKFILE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IFileStatus queryCheckFile(
		IProgramInfo programInfo, 
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		Boolean checkSlaves
	) throws IOException;		
	
	/**
	 * Queries a unique hash-value for the recording-file of the given program.
	 * 
	 * @param programInfo 
	 * 		the program referencing a file, for which the file hash should be generated.
	 * @return 
	 * 		the generated hash-value, e.g. <code>75aa72141cd08662</code>
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #queryFileHash(String, String, String)
	 * @see IMythCommand#QUERY_FILE_HASH QUERY_FILE_HASH
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 51}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_51)
	public String queryFileHash(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Queries a unique hash-value for the given remote file.
	 * <p>
	 * This method calls {@link URI#getUserInfo()}, {@link URI#getHost()} and {@link URI#getPath()} to determine the storage-group, host-name and file-name
	 * and calls {@link #queryFileHash(String, String, String)} afterwards.
	 * 
	 * <h4>Usage Hint:</h4>
	 * To compate the remotely calculated has with a locally generated one,
	 * the class {@link OpenSubtitlesHasher} could be used.
	 * 
	 * @param fileUrl
	 * 		the remote file
	 * @return 
	 * 		the generated hash-value, e.g. {@code 75aa72141cd08662}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILE_HASH QUERY_FILE_HASH
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 51}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_51)	
	public String queryFileHash(URI fileUrl) throws IOException;
	
	/**
	 * Queries a unique hash-value for the given file stored in the given storage group.
	 * 
	 * <h4>Usage Hint:</h4>
	 * To compate the remotely calculated has with a locally generated one,
	 * the class {@link OpenSubtitlesHasher} could be used.
	 * 
	 * @param fileName 
	 * 		the remote file name
	 * @param storageGroup 
	 * 		the storage group name, or {@code null} if the default group should be used
	 * @return 
	 * 		the generated hash-value, e.g. {@code 75aa72141cd08662}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILE_HASH QUERY_FILE_HASH
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 51}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_51)
	public String queryFileHash(String fileName,String storageGroup) throws IOException;

	/**
	 * Queries a unique hash-value for the given file stored in the given storage group.
	 * 
	 * <h4>Usage Hint:</h4>
	 * To compate the remotely calculated has with a locally generated one,
	 * the class {@link OpenSubtitlesHasher} could be used.
	 * 
	 * @param fileName 
	 * 		the remote file name
	 * @param storageGroup 
	 * 		the storage group name, or {@code null} if the default group should be used
	 * @param hostName
	 * 		the name of the backend, storing the file. (since {@mythProtoVersion 69}). 
	 * 		If this is {@code null} the current backend will be queried.
	 * @return 
	 * 		the generated hash-value, e.g. {@code 75aa72141cd08662}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_FILE_HASH QUERY_FILE_HASH
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 51}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_51)
	public String queryFileHash(
		String fileName,
		String storageGroup,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_69) 
		String hostName
	) throws IOException;	
	
	/**
	 * Queries the a single file from the remote storage group. 
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * For protocol versions prior to {@mythProtoVersion 58} the given file-path must be an absolute path. 
	 * If the path is not absolute, {@link #queryFileExists(String, String)} is used to determine the proper absolute path.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *     IBackend backend = ...;     // an already connected backend
	 *     IProgramInfo program = ...; // a previously recorded program
	 *     
	 *     // getting all required parameters
	 *     String hostName = program.getHostName();
	 *     String storageGroup = program.getStorageGroup();
	 *     String previewFileName = program.getPreviewImageName();
	 *     
	 *     // query infos about the recording preview image
	 *     IStorageGroupFile previewFile = backend.queryStorageGroupFile(hostName,storageGroup,previewFileName);
	 *     if(previewFile != null) &#123;
	 *        System.out.println(String.format(
	 *           "Preview image path: '%s', last-modified date: '%tF', file-size: '%d'.",
	 *           previewFile.getFilePath(),
	 *           previewFile.getLastModified(),
	 *           previewFile.getSize()
	 *        ));	
	 *     &#125; else &#123;
	 *        System.err.println("Preview image not found");
	 *     &#125;
	 * </pre>}
	 * <br>
	 * 
	 * 
	 * @param hostName
	 * 		the backend host to query. if {@code null} the result of {@link #queryHostname()} is used to determine the hostname.
	 * @param storageGroup
	 * 		the storage group name. If {@code null} {@code Default} is used.
	 * @param fileName
	 * 		the file name to search for. Till {@mythProtoVersion 58} this path must be an absolute path.
	 * @return
	 * 		information about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_SG_FILEQUERY QUERY_SG_FILEQUERY
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 44}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public IStorageGroupFile queryStorageGroupFile(String hostName, String storageGroup, String fileName) throws IOException;
	
	/**
	 * Queries the a list of files from the remote storage group.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *     IBackend backend = ....; // an already connected backend
	 *     
	 *     // query all files stored in the storage-group "Default"
	 *     String hostName = backend.queryHostname();
	 *     IStorageGroupFileList fileList = backend.queryStorageGroupFileList(hostName, "Default", "/", true);
	 *     if(fileList != null) &#123;
	 *         // loop through all found files
	 *         for(IStorageGroupFile file : fileList) &#123;
	 *             // fetch the program info for all found recordings
	 *             final String fileName = file.getFilePath(); // e.g. 1064_20110403184000.mpg			
	 *             if(fileName.matches("\\d+_\\d+\\.mpg")) &#123;
	 *                 // fetch the program info for the recording file
	 *                 IProgramInfo program = backend.queryRecording(fileName);
	 *                 if(program != null) &#123;
	 *                    System.out.println(program);
	 *                 &#125;
	 *             &#125;
	 *         &#125;
	 *     &#125;
	 * </pre> }
	 * <p>
	 * 
	 * @param hostName
	 * 		the backend host to query. 
	 *      If this is {@code null} {@link #queryHostname()} is used to determine the hostname.
	 * @param storageGroup
	 * 		the storage group name. If this is {@code null} {@code Default} is used.
	 * @param path
	 * 		the path to query
	 * @return
	 * 		informations about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 44}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public IStorageGroupFileList queryStorageGroupFileList(String hostName, String storageGroup, String path) throws IOException;
	
	/**
	 * Queries the a list of files from the remote storage group.
	 * 
	 * @param hostName
	 * 		the backend host to query. 
	 *      If this is {@code null} {@link #queryHostname()} is used to determine the hostname.
	 * @param storageGroup
	 * 		the storage group name. If this is {@code null} {@code Default} is used.
	 * @param path
	 * 		the path to query
	 * @param fileNamesOnly
	 * 		if only the file names should be returned. (since {@mythProtoVersion 49})
	 * @return
	 * 		informations about the found file or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 44}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public IStorageGroupFileList queryStorageGroupFileList(
		String hostName, 
		String storageGroup, 
		String path,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_49,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="22122"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="301a3816e687db3c76d6")
		})
		boolean fileNamesOnly
	) throws IOException;
	
	/**
	 * Queries the timezone of the backend.
	 * 
	 * @return 
	 * 		the timezone of the backend
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_TIME_ZONE QUERY_TIME_ZONE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 42}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_42)
	public ITimezone queryTimeZone() throws IOException;

	/**
	 * Signals a slave backend to go to sleep.
	 * 
	 * @return 
	 * 		{@code true} if the backend went to sleep or {@code false} otherwise
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#GO_TO_SLEEP GO_TO_SLEEP
	 *  
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 45}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_45)
	public boolean goToSleep() throws IOException;

	/**
	 * Gets the hostname of the backend. 
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is called prior to version {@mythProtoVersion 50}, and the current backend connection was established using an IP-address,
	 * this function compares the backend IP adress with the addresses of all recorders, to determine the proper host-name.<br>
	 * If the current backend connection was established using a hostname, the result of {@link #getHostName()} is returned. 
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // query the backends host name
	 *    String backendHostName = backend.queryHostname();
	 *    System.out.println("We are connected to backend: " + backendHostName);
	 * </pre>}
	 * <br>
	 * 
	 * @return
	 * 		the hostname of the backend.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_HOSTNAME QUERY_HOSTNAME
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 50 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_50,fromFallback=PROTO_VERSION_00)
	public String queryHostname() throws IOException;

	/**
	 * Prevents a backend from shutting down.
	 * <p>
	 * This function prevents a backend from shutting down until a the next call of {@link #allowShutdown()}.
	 * 
	 * @return
	 * 		{@code true} if the backed has accepted the request
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#BLOCK_SHUTDOWN BLOCK_SHUTDOWN
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 19}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_19)
	public Boolean blockShutdown() throws IOException;

	/**
	 * Allows a backend to shutdown.
	 * <p>
	 * This function allows a backend to shut down again after a previous call of {@link #blockShutdown()}.
	 * 
	 * @return
	 * 		{@code true} if the backed has accepted the request
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#ALLOW_SHUTDOWN ALLOW_SHUTDOWN
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 19}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_19)
	public Boolean allowShutdown() throws IOException;

	/**
	 * Forces a slave backend to shutdown now.
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SHUTDOWN_NOW SHUTDOWN_NOW
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public void shutdownNow() throws IOException;

	/**
	 * Queries the remote host for a specific setting.
	 * <p>
	 * The backend will look in the MySQL database table 'settings', and 
	 * attempt to return the value for the given setting. It seems only settings 
	 * with the hostname set can be retrieved by this call.
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #setSetting setSetting} to change backend settings.
	 * 
	 * @param hostName 
	 * 		the name of the host, the setting belongs to. This can not be {@code null}.
	 * @param settingName 
	 * 		the name of the setting to query
	 * @return 
	 * 		the found settings-value or {@code null} if the setting was not found.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_17)
	public ISetting querySetting(String hostName, String settingName) throws IOException;

	/**
	 * Sets the given setting on the remote host.
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #querySetting querySetting} to read backend settings.<br>
	 * Use {@link #clearSettingsCache clearSettingsCache} to notify the backend about changed settings.<br>
	 * 
	 * @param hostName 
	 * 		the name of the host, the setting belongs to
	 * @param settingName 
	 * 		the name of the setting to change
	 * @param settingValue 
	 * 		the new setting value
	 * @return 
	 * 		{@code true} if the setting-value was changed successfully.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SET_SETTING SET_SETTING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_17)
	public boolean setSetting(String hostName, String settingName, String settingValue) throws IOException;

	/**
	 * Forces the backend to clear its settings cache.
	 * <p>
	 * This command should be used after changing any backend settings and forces the backend 
	 * to reload settings from the database (for example, using MythWeb or Mythtv-setup).
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #setSetting setSetting} to change backend settings.<br>
	 * Use {@link #querySetting querySetting} to read backend settings.<br>
	 * 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#BACKEND_MESSAGE_CLEAR_SETTINGS_CACHE BACKEND_MESSAGE_CLEAR_SETTINGS_CACHE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 23}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_23)
	public void clearSettingsCache() throws IOException;
	
	/**
	 * Asks the backend to lock the next free tuner.
	 * <p> 
	 * <b>ATTENTION:</b> This only works if your frontend is on the same machine as one
	 * of the available tuners.
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #freeTuner freeTuner} to unlock a tuner.
	 * 
	 * @return
	 * 		informations about the locked tuner
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #lockTuner(Integer)
	 * @see IMythCommand#LOCK_TUNER LOCK_TUNER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public ITunerInfo lockTuner() throws IOException;
	
	/**
	 * Asks the backend to lock the given tuner.
	 * <p> 
	 * <b>ATTENTION:</b> This only works if your frontend is on the same machine as the 
	 * tuner you are trying to lock.
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #freeTuner freeTuner} to unlock a tuner.
	 * 
	 * @param recorderID
	 * 		the ID of the recorder to lock
	 * @return
	 * 		informations about the locked tuner
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#LOCK_TUNER LOCK_TUNER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public ITunerInfo lockTuner(Integer recorderID) throws IOException;
	
	/**
	 * Frees a previously locked tuner.
	 * 
	 * <h4>Usage Hints:</h4>
	 * Use {@link #lockTuner lockTuner} to lock a tuner.
	 * 
	 * @param recorderID
	 * 		the ID of the recorder to free
	 * @return
	 * 		{@code true} if the recorder was freed successfully or {@code false} otherwise
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#FREE_TUNER FREE_TUNER
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean freeTuner(Integer recorderID) throws IOException;
	
	/**
	 * Fills in the pathname and file size fields of the given recording.
	 * 
	 * @param programInfo 
	 * 		the recording, whose information should be updated.
	 * @return
	 * 		the given program with the path-name and file-size filled in 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#FILL_PROGRAM_INFO FILL_PROGRAM_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)	
	public IProgramInfo fillProgramInfo(IProgramInfo programInfo) throws IOException;
	
	/**
	 *  Fills in the pathname and file size fields of the given recording.
	 *  <p>
	 *  Calling this function converts the {@link IProgramInfo#getPathName path} property from
	 *  e.g. {@code /var/lib/mythtv/livetv/1004_20120113085933.mpg} to {@code myth://192.168.10.207:6543/1004_20120113085933.mpg}
	 *  and additionally updates the {@link IProgramInfo#getFileSize file size}. 
	 * 
	 * @param programInfo 
	 * 		the recording, whose information should be updated.
	 * @param playBackHostname 
	 * 		the name of the playback host
	 * @return
	 * 		the given program with the path-name and file-size filled in 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#FILL_PROGRAM_INFO FILL_PROGRAM_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IProgramInfo fillProgramInfo(IProgramInfo programInfo, String playBackHostname) throws IOException;
	
	/**
	 * Checks if the given program is currently being recorded.
	 * <p>
	 * This function return the number of the recorder or {@code null}, if the program is not being recorded.
	 * <p>
	 * 
	 * <h4>Usage Note:</h4>
	 * 
	 * If the backend is not configured properly and therefore returns an empty string in {@link IProgramInfo#getHostName()},
	 * this function uses {@link IMythCommand#QUERY_REMOTEENCODER_MATCHES_RECORDING QUERY_REMOTEENCODER_MATCHES_RECORDING} 
	 * to determine the recorder that is recording this program.
	 * 
	 * <h4>Usage example:</h4>
	 * <p>
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 * 
	 *    // get all currently running recordings
	 *    IProgramInfoList recordings = backend.queryRecordings(ERecordingsType.Recording);
	 *    for(IProgramInfo recording : recordings) &#123;
	 *      // getting the ID of the recorder recording the program
	 *      Integer recorderId = backend.checkRecording(recording);
	 *      System.out.println(String.format(
	 *         "Recording '%s' (%s) is currently recorded by recorder-ID: %d",
	 *         recording.getTitle(),recording.getChannelSign(),
	 *         recorderId
	 *      ));
	 *    &#125;	
	 * </pre>}
	 * <br>
	 * 
	 * @param programInfo
	 * 		the given program
	 * @return 
	 * 		the recorder-ID or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#CHECK_RECORDING CHECK_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Integer checkRecording(IProgramInfo programInfo) throws IOException;	

	/**
	 * Stop a currently active recording.
	 * <p>
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * Prior to protocol version {@mythProtoVersion 19} the file-name of a recording is renamed,
	 * if the recording is stopping prior to the originally intended end time! So if you are plaining
	 * to delete the recording-file in a next step you need to update the file information.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // get all currently running recordings
	 *    IProgramInfoList recordings = backend.queryRecordings(ERecordingsType.Recording);
	 *    for(IProgramInfo recording : recordings) &#123;
	 *      // search for a recording called "Wetter-Panorama"
	 *      if(recording.getTitle().equals("Wetter-Panorama")) &#123;
	 *         // stop the recording
	 *         Integer recorderId = backend.stopRecording(recording);
	 *         System.out.println(String.format(
	 *            "Recording of program '%s' (%s) stopped on recorder %d.",
	 *            recording.getTitle(), recording.getChannelSign(),
	 *            recorderId
	 *         ));
	 *      &#125;
	 *    &#125;
	 * </pre>}
	 * <br>
	 * 
	 * @param programInfo
	 * 		the recording to be stopped.
	 * @return 
	 * 		the recorder-ID where the recording was stopped, or {@code -1} if the recording is not found.
	 * @throws IOException
	 * 		on communication errors.
	 * 
	 * @see IMythCommand#STOP_RECORDING STOP_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public Integer stopRecording(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Checks if the given program is currently being recorded and returns the recorder-info 
	 * for the recorder or {@code null}, if it is not being recorded.
	 * <p>
	 * This method combines {@link #checkRecording(IProgramInfo)} and {@link #getRecorderForNum(int)}.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    // get all currently running recordings
	 *    IProgramInfoList recordings = backend.queryRecordings(ERecordingsType.Recording);
	 *    for(IProgramInfo recording : recordings) &#123;
	 *       // determine the used recorder
	 *       IRecorderInfo recorder = backend.getRecorderForProgram(recording);
	 *       System.out.println(String.format(
	 *          "Recording '%s' (%s) is currently recorded by: %s",
	 *          recording.getTitle(),recording.getChannelSign(),
	 *          recorder.toString()
	 *       ));
	 *    &#125;
	 * </pre> }
	 * <br>
	 * 
	 * @param programInfo
	 * 		the given program
	 * @return
	 * 		the recorder-info object or {@code null}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #checkRecording(IProgramInfo)
	 * @see #getRecorderForNum(int)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	public IRecorderInfo getRecorderForProgram(IProgramInfo programInfo) throws IOException;
	
	/**
	 * Get the ProgramInfo for a single recording specified by the recordings base-name.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryRecording(Integer, Date)} to query a recording by channel id and rec-start-time.
	 * 
	 * @param baseName 
	 * 		the base-name of the recorded program-file, e.g. {@code 1057_20100811133747.mpg}.
	 * @return
	 * 		the found recording or {@code null} if no matching recording was found
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_RECORDING QUERY_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	public IProgramInfo queryRecording(String baseName) throws IOException;
	
	/**
	 * Get a single recording by its channel and recording start-time.
	 * <p>
	 * This function internally uses {@link IBasicChannelInfo#getChannelID()} to get the channel-id of the recording
	 * and calls {@link #queryRecording(Integer, Date)} afterwards.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryRecording(String)} to query a recording by base name.
	 * 
	 * @param channel 
	 * 		the channel of the recording
	 * @param recordingStartTime 
	 * 		the recording starting-time, e.g. {@code 2010-08-11T13:37:47}.
	 * @return
	 * 		the found recording or {@code null} if no matching recording was found
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #queryRecording(Integer, Date)
	 * @see IMythCommand#QUERY_RECORDING QUERY_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 32 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32, fromFallback=PROTO_VERSION_00)
	public IProgramInfo queryRecording(IBasicChannelInfo channel, Date recordingStartTime) throws IOException;
	
	/**
	 * Get a single recording by its channel-id and start-time.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryRecording(String)} to query a recording by base name.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If this function is used for versions prior to {@mythProtoVersion 32}, then {@link #queryRecordings()} is called to fetch all recordings,
	 * and a {@link ProgramInfoFilters#channelIdRecStartTime program-filter} is used afterwards to determine the desired recording. 
	 * 
	 * @param channelID 
	 * 		the channel-ID, e.g. {@code 1057}
	 * @param recordingStartTime 
	 * 		the recording starting-time, e.g. {@code 2010-08-11T13:37:47}
	 * @return
	 * 		the found recording or {@code null} if no matching recording was found
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #queryRecording(IBasicChannelInfo, Date)
	 * @see IMythCommand#QUERY_RECORDING QUERY_RECORDING
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 32 fromFallback=0}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32, fromFallback=PROTO_VERSION_00)
	public IProgramInfo queryRecording(Integer channelID, Date recordingStartTime) throws IOException;

	/**
	 * Get a list of all available channels on all available recorders.
	 * 
	 * <h4>Protocol Version Hint:</h4>
	 * If the current protocol-version is less than {@link ProtocolVersion#PROTO_VERSION_28 28}
	 * an {@link IBasicChannelInfo} object is returned, otherwise an {@link IRecorderChannelInfo} 
	 * object is returned.
	 * 
	 * @param <C>
	 * 		the return type. is is either a {@link IBasicChannelInfo} or an {@link IRecorderChannelInfo}
	 * @return
	 * 		the list of all available channels
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #getChannelInfos()
	 * @see #getRecorders()
	 * @see IRecorder#getBasicChannelInfos()
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public <C extends IBasicChannelInfo> List<C> getBasicChannelInfos() throws IOException;
	
	/**
	 * Get a list of all available channels on all available recorders.
	 * <p>
	 * This method internally uses {@link #getRecorders()} to get a list of all known recorders and
	 * calls {@link IRecorder#getChannelInfos()} for all recorders.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link IRecorder#getChannelInfo IRecorder.getChannelInfo} to get the info for a single channel<br>
	 * Use {@link #setChannelInfo setChannelInfo} to change the info for a single channel.<br>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 * 
	 *    List&lt;IRecorderChannelInfo&gt; channels = backend.getChannelInfos();
	 *    for(IRecorderChannelInfo channel : channels) &#123;
	 *       System.out.println(String.format(
	 *          "%2s | %-15s | %s",
	 *          channel.getChannelNumber(), channel.getChannelSign(),
	 *          channel.getChannelName()
	 *       ));
	 *    &#125;
	 * </pre> }
	 * <p>
	 * The above example will output, e.g. 
	 * <pre>
	 *  1 | PRO7            | Pro7
	 *  2 | SAT.1           | SAT.1
	 *  3 | RTL             | RTL
	 *  4 | RTL2            | RTL 2
	 *  5 | ARD             | ARD
	 *  6 | ZDF             | ZDF
	 *  7 | KABE1           | Kabel 1
	 *  ...
	 * </pre>
	 * 
	 * @return
	 * 		a list of all available channels
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see #getRecorders()
	 * @see IRecorder#getBasicChannelInfos()
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 28}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public List<IRecorderChannelInfo> getChannelInfos() throws IOException;
	
	/**
	 * Changes detailed infos about the given channel.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #getChannelInfos getChannelInfos} to get infos about all known channels.<br>
	 * Use {@link IRecorder#getChannelInfo IRecorder.getChannelInfo} to get the info for a single channel<br>
	 * 
	 * @param oldChannelNumber
	 * 		the old channel number
	 * @param channelInfo
	 * 		the new channel info
	 * @return
	 * 		{@code true} if channel data was changed successfully.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SET_CHANNEL_INFO SET_CHANNEL_INFO
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 28}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public boolean setChannelInfo(String oldChannelNumber, IRecorderChannelInfo channelInfo) throws IOException;	
	
	/**
	 * Gets the next programs of all known channels.
	 * <p>
	 * This method internally uses {@link #getRecorders()} to get a list of all known recorders and
	 * calls {@link IRecorder#getNextProgramInfos(Date)} for all recorders.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    IBackend backend = ...; // an already connected backend
	 *    
	 *    Calendar cal = Calendar.getInstance();
	 *    cal.set(Calendar.HOUR_OF_DAY,20);
	 *    cal.set(Calendar.MINUTE,15);
	 *    
	 *    // fetch the programs of all channels today at 8:15 PM
	 *    List&lt;IRecorderNextProgramInfo&gt; nextPrograms = backend.getNextProgramInfos(cal.getTime());
	 *    for(IRecorderNextProgramInfo nextProgram : nextPrograms) &#123;
	 *       if(!nextProgram.isValid()) continue; // if there is no valid program info for a channel
	 *       
	 *       System.out.println(String.format(
	 *          "%tT | %tT | %-15s | %s",
	 *          nextProgram.getStartDateTime(), nextProgram.getEndDateTime(),
	 *          nextProgram.getChannelSign(), nextProgram.getTitle()
	 *       ));
	 *    &#125;
	 * </pre> }
	 * <p>
	 * The above example will output, e.g. 
	 * <pre>
	 * 20:15:01 | 23:05:01 | RTL             | Harry Potter und der Stein der Weisen
	 * 20:15:01 | 21:15:01 | SAT.1           | Navy CIS
	 * 20:15:01 | 21:45:01 | ARD             | Tatort
	 * 20:15:01 | 21:45:01 | ZDF             | Rosamunde Pilcher: Englischer Wein
	 * 20:15:01 | 21:45:01 | SWBW            | SonntagAbend
	 * 19:45:01 | 21:15:01 | BR3             | Kreuzwege
	 * 18:25:00 | 20:15:00 | PULS4           | American Pie Presents Band Camp
	 * 20:15:01 | 22:20:01 | PRO7            | Wall-E - Der Letzte räumt die Erde auf
	 * 20:15:01 | 22:55:01 | ATV+            | The Missing
	 * 20:15:01 | 22:10:01 | RTL2            | Ungeklärte Morde - Dem Täter auf der Spur
	 * 20:15:01 | 22:45:01 | ARTE            | Fitzcarraldo
	 * 20:15:01 | 21:00:01 | 3SAT            | Neues aus der Anstalt
	 * 20:15:01 | 21:50:01 | ORF2            | Tatort
	 * 20:15:01 | 21:40:01 | ORF1            | WALL-E - Der Letzte räumt die Erde auf
	 * 20:15:01 | 22:45:01 | VOX             | Das perfekte Promi Dinner
	 * 20:15:01 | 21:15:01 | SUPRT           | Abgefahren - Der lange Weg zum Führerschein
	 * 20:15:01 | 20:45:01 | KABE1           | Two and a Half Men
	 * </pre>
	 * 
	 * @param date
	 * 		the start time
	 * @return
	 * 		the next programs
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IBackend#getRecorders()
	 * @see IRecorder#getNextProgramInfos(Date)
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public List<IRecorderNextProgramInfo> getNextProgramInfos(Date date) throws IOException;
	
	/**
	 * Gets a map containing the path to all channel logos.
	 * <p>
	 * This method interally uses {@link #getNextProgramInfos(Date)} to determine the path to
	 * all channel logos.
	 * 
	 * @param keyProp
	 * 		the property of the program-info object that should be used as key for the map.<br>
	 * 		If this is null {@link IRecorderNextProgramInfo.Props#CHANNEL_ID} is used.
	 * @return
	 * 		a map containing all channel logos
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 00}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Map<String,String> getChannelLogoMap(IRecorderNextProgramInfo.Props keyProp) throws IOException;
	
	
	/**
	 * Trigger a scan of all video folders.
	 * 
	 * @return
	 * 		{@code true} if the backend respond with {@code OK}
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SCAN_VIDEOS SCAN_VIDEOS
	 * @see IVideoList
	 * @see IVideoListNoChange
	 * @see IVideoListChange
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 64}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_64)
	public boolean scanVideos() throws IOException;
	
	/**
	 * Downloads a file to the backend.
	 * <p>
	 * The file is downloaded asynchronously. The backend sends {@link IDownloadFileUpdate} and {@link IDownloadFileFinished}-events
	 * to inform the client about the downloading status.
	 * <p>
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 * 
	 *    // allow the client to receive events
	 *    backend.annotateMonitor("mythClient",EPlaybackSockEventsMode.NORMAL);
	 *    Semaphore downloadFinished = new Semaphore(0);
	 *    
	 *    // register as event listeners
	 *    backend.addEventListener(IDownloadFileUpdate.class,new IMythEventListener&lt;IDownloadFileUpdate&gt;() &#123;
	 *       public void fireEvent(IDownloadFileUpdate event) &#123;			
	 *          System.out.println(String.format(
	 *             "%s of %s (%f%%) downloaded from '%s'.",
	 *             EncodingUtils.getFormattedFileSize(event.getBytesReceived()),
	 *             EncodingUtils.getFormattedFileSize(event.getBytesTotal()),
	 *             event.getReceivedPercent(),
	 *             event.getRemoteURI()
	 *          ));
	 *       &#125;
	 *    &#125;);
	 *    backend.addEventListener(IDownloadFileFinished.class,new IMythEventListener&lt;IDownloadFileFinished&gt;() &#123;
	 *       public void fireEvent(IDownloadFileFinished event) &#123;				
	 *          System.out.println(String.format(
	 *             "Download of '%s' (%s) finished",					
	 *             event.getRemoteURI(),
	 *             EncodingUtils.getFormattedFileSize(event.getBytesTotal())					
	 *          ));
	 *          
	 *          // signal that the download has finished
	 *          downloadFinished.release();
	 *       &#125;
	 *    &#125;);
	 *    
	 *    // force the backend to download the file
	 *    URI remoteFileUri = URI.create("http://upload.wikimedia.org/wikipedia/de/7/7c/ORF-Logo.svg");
	 *    String localFileName = "ORF-Logo.svg";
	 *    
	 *    URI localFileUri = backend.downloadFile(
	 *       remoteFileUri, 
	 *       null, 
	 *       localFileName
	 *    );
	 *    		
	 *    // waiting for the download to finish
	 *    downloadFinished.acquire();
	 *    
	 *    // transfer the file to the client
	 *    IFileTransfer transfer = backend.annotateFileTransfer(localFileUri);		
	 *    File localFile = new File(localFileName);
	 *    transfer.transferTo(localFile);
	 *    transfer.close();
	 * </pre> }
	 * 
	 * @param url
	 * 		the remote resource to download
	 * @param storageGroup
	 * 		the storage group the remote resource should be stored into
	 * @param fileName
	 * 		the name of the file to store the remote file into
	 * @return
	 * 		the URI to the downloaded resource 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DOWNLOAD_FILE DOWNLOAD_FILE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 58}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_58)
	public URI downloadFile(URI url, String storageGroup, String fileName) throws IOException;
	
	/**
	 * Downloads a file to the backend.
	 * <p>
	 * The remote file is downloaded synchronously by the backend.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *    backend.annotateMonitor();
	 *    
	 *    // force the backend to download the file
	 *    URI remoteFileUri = URI.create("http://upload.wikimedia.org/wikipedia/de/7/7c/ORF-Logo.svg");
	 *    String localFileName = "ORF-Logo.svg";
	 *    
	 *    URI localFileUri = backend.downloadFileNow(
	 *       remoteFileUri, 
	 *       null, 
	 *       localFileName
	 *    );
	 *    
	 *    // transfer the file to the client
	 *    IFileTransfer transfer = backend.annotateFileTransfer(localFileUri);		
	 *    File localFile = new File(localFileName);
	 *    transfer.transferTo(localFile);
	 *    transfer.close();
	 * </pre> }
	 * 
	 * @param url
	 * 		the remote resource to download
	 * @param storageGroup
	 * 		the storage group the remote resource should be stored into
	 * @param fileName
	 * 		the name of the file to store the remote file into
	 * @return
	 * 		the URI to the downloaded resource 
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DOWNLOAD_FILE_NOW DOWNLOAD_FILE_NOW
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 58}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_58)	
	public URI downloadFileNow(URI url, String storageGroup, String fileName) throws IOException;	
	
	/**
	 * Delete a remote file.
	 * <p>
	 * This method calls {@link URI#getUserInfo()} and {@link URI#getPath()} to determine the storage-group and file-name
	 * and calls {@link #deleteFile(String, String)} afterwards. 
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #deleteRecording(IProgramInfo)} if a recording should be deleted. 
	 * 
	 * @param fileUrl
	 * 		the url to the remote file
	 * @return
	 * 		{@code true} if the file was deleted successfully, or {@code false} otherwise
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DELETE_FILE DELETE_FILE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 46}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_46)	
	public boolean deleteFile(URI fileUrl) throws IOException;
	
	/**
	 * Delete a remote file.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #deleteRecording deleteRecording} if a recording should be deleted. 
	 * 
	 * @param fileName
	 * 		the name of the remote file
	 * @param storageGroup
	 * 		the storage-group the remote file is stored in.
	 * @return
	 * 		{@code true} if the file was deleted successfully, or {@code false} otherwise
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#DELETE_FILE DELETE_FILE
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 46}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_46)	
	public boolean deleteFile(String fileName, String storageGroup) throws IOException;
	
	/**
	 * Queries the bookmark position for a given recording.
	 * <p>
	 * This function uses {@link IProgramInfo#getChannelID()} and {@link IProgramInfo#getRecordingStartTime()} to determine
	 * the channel-id and recording-start-time and calls {@link #queryBookmark(Integer, Date)} afterwards.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #setBookmark setBookmark} to set the bookmark of a recording.
	 * 
	 * <h4>Usage example:</h4>
	 * 
	 * {@mythCodeExample <pre>
	 *   // getting a list of recordings
	 *   IProgramInfoList recordings = backend.queryRecordings();
	 *   
	 *   // print the bookmark position for all recordings with bookmarks
	 *   System.out.println("Rercordings with bookmarks: ");
	 *   for(IProgramInfo recording : recordings) &#123;
	 *      // determine if the recording has a bookmark set
	 *      boolean hasBookmark = recording.getProgramFlags().isSet(Flags.FL_BOOKMARK);
	 *      if(hasBookmark) &#123;
	 *         // query the bookmark position
	 *         Long bookmark = backend.queryBookmark(recording);
	 *         System.out.println(String.format(
	 *            "- %s (bookmark: frame %d)",
	 *            recording.getFullTitle(),
	 *            bookmark
	 *         ));
	 *      &#125;
	 *   &#125;
	 * </pre>}
	 * 
	 * @param program
	 * 		the recording
	 * @return
	 * 		the bookmark position in frames
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_BOOKMARK QUERY_BOOKMARK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Long queryBookmark(IProgramInfo program) throws IOException;
	
	/**
	 * Queries the bookmark position for a given recording.
	 * <p>
	 * <h4>Usage Hint:</h4>
	 * Use {@link #setBookmark setBookmark} to set the bookmark of a recording.
	 * 
	 * @param channelID
	 * 		the channel-ID of the recording
	 * @param recordingStartTime
	 * 		the recording-start-time of the recording
	 * @return
	 * 		the bookmark position in frames
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUERY_BOOKMARK QUERY_BOOKMARK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Long queryBookmark(Integer channelID, Date recordingStartTime) throws IOException;
	
	/**
	 * Sets the bookmark position for the given recording.
	 * <p>
	 * This function uses {@link IProgramInfo#getChannelID()} and {@link IProgramInfo#getRecordingStartTime()} to determine
	 * the channel-id and recording-start-time of the recording and calls {@link #setBookmark(Integer, Date, Long)} afterwards.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryBookmark queryBookmark} to get the bookmark of a recording.
	 * 
	 * @param recording
	 * 		the recording for which the bookmark should be set
	 * @param bookmarkPosition
	 * 		the bookmark position in frames
	 * @return
	 * 		{@code true} if the bookmark was set or {@code false} otherwise
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SET_BOOKMARK SET_BOOKMARK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public boolean setBookmark(IProgramInfo recording, Long bookmarkPosition) throws IOException;
	
	/**
	 * Sets the bookmark position for the given recording.
	 * <p>
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queryBookmark queryBookmark} to get the bookmark of a recording.
	 * 
	 * @param channelID
	 * 		the channel ID of the recording
	 * @param recordingStartTime
	 * 		the recording-start-time
	 * @param bookmarkPosition
	 * 		the bookmark position in frames
	 * @return
	 * 		{@code true} if the bookmark was set or {@code false} otherwise
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#SET_BOOKMARK SET_BOOKMARK
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 17}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public boolean setBookmark(Integer channelID, Date recordingStartTime, Long bookmarkPosition) throws IOException;
	
	/**
	 * Enqueues the recording for transcoding.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queueTranscodeStop queueTranscodeStop} to stop transcoding of a recording.
	 * 
	 * @param recording
	 * 		the recording to transcode
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUEUE_TRANSCODE QUEUE_TRANSCODE
	 * 
	 * @mythProtoVersionRange
	 * @deprecated {@mythProtoVersion 23}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})		
	public boolean queueTranscode(IProgramInfo recording) throws IOException;
	
	/**
	 * Enqueues the recording for transcoding and cutlist processing.
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queueTranscodeStop queueTranscodeStop} to stop transcoding of a recording.
	 * 
	 * @param recording
	 * 		the recording to transcode
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUEUE_TRANSCODE_CUTLIST QUEUE_TRANSCODE_CUTLIST
	 * 
	 * @mythProtoVersionRange
	 * @deprecated {@mythProtoVersion 23}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})	
	public boolean queueTranscodeCutlist(IProgramInfo recording) throws IOException;
	
	/**
	 * Stops transcoding of a recording
	 * 
	 * <h4>Usage Hint:</h4>
	 * Use {@link #queueTranscode queueTranscode} to start transcoding of a recording.<br>
	 * Use {@link #queueTranscodeCutlist queueTranscodeCutlist} to start transcoding and cutlist processing of a recording.<br>
	 * 
	 * @param recording
	 * 		the recording for which transcoding should be stopped
	 * @return
	 * 		{@code true} on success
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see IMythCommand#QUEUE_TRANSCODE_STOP QUEUE_TRANSCODE_STOP
	 * 
	 * @mythProtoVersionRange
	 * @deprecated {@mythProtoVersion 23}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})	
	public boolean queueTranscodeStop(IProgramInfo recording) throws IOException;

	@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
	public IFreeInputInfoList getFreeInputInfo() throws IOException;

	@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
	public IFreeInputInfoList getFreeInputInfo(int excluded_input) throws IOException;
	
	/**
	 * Registers a new event listener.
	 * 
	 * @param listener
	 * 		the event listener
	 */
	public void addEventPacketListener(IMythEventPacketListener listener);
	
	/**
	 * Unregisters a event listener.
	 * 
	 * @param listener
	 * 		the event listener
	 */
	public void removeEventPacketListener(IMythEventPacketListener listener);
	
	public <Event extends IMythEvent<?>> void addEventListener(Class<Event> eventClass, IMythEventListener<Event> listener);
	
	public <Event extends IMythEvent<?>> void removeEventListener(Class<Event> eventClass, IMythEventListener<Event> listener);
}