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
package org.jmythapi.protocol.impl;

import static org.jmythapi.protocol.ProtocolConstants.STORAGE_GROUP_DEFAULT;
import static org.jmythapi.protocol.ProtocolVersion.*;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;
import static org.jmythapi.protocol.request.EPlaybackSockEventsMode.NON_SYSTEM;
import static org.jmythapi.protocol.request.EPlaybackSockEventsMode.NORMAL;
import static org.jmythapi.protocol.request.IMythCommand.ANN_MONITOR;
import static org.jmythapi.protocol.request.IMythCommand.ANN_PLAYBACK;
import static org.jmythapi.protocol.utils.ResponseUtils.STATUS_OK;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.impl.ResultList;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ProtocolException;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.UnknownCommandException;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.events.IMythEvent;
import org.jmythapi.protocol.events.IMythEventListener;
import org.jmythapi.protocol.events.IMythEventPacketListener;
import org.jmythapi.protocol.events.IPixmapGenerated;
import org.jmythapi.protocol.events.IScheduleChange;
import org.jmythapi.protocol.events.impl.ClientErrorEvent;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.AMythRequest;
import org.jmythapi.protocol.request.EPlaybackSockEventsMode;
import org.jmythapi.protocol.request.ERecordingsType;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.*;
import org.jmythapi.protocol.response.IProgramInfoList.MapKey;
import org.jmythapi.protocol.response.IProgramRecordingStatus.Status;
import org.jmythapi.protocol.response.impl.*;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.protocol.utils.EventUtils;
import org.jmythapi.protocol.utils.RequestUtils;
import org.jmythapi.protocol.utils.ResponseUtils;
import org.jmythapi.utils.EncodingUtils;

@SuppressWarnings("deprecation")
public class Backend implements IVersionable, Closeable, IBackend, IMythEventPacketListener {
	public boolean debugging = false;

	/**
	 * The MythTV-backend hostname to connect to
	 */
	private String hostName = null;
	
	/**
	 * The MythTV-backend port to connect to
	 */
	private int hostPort = IBackendConnection.DEFAULT_COMMAND_PORT;
	
	/**
	 * The MythTV-protocol version used to communicate
	 * with the backend
	 */
	protected ProtocolVersion protoVersion = ProtocolVersion.getMaxVersion();
	
	/**
	 * A connection to the backend. The connection is established using {@link #connect()}
	 */
	private IBackendConnection cmdConnection = null;
	
	/**
	 * For message logging
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());		
	
	/**
	 * The annotated events mode or {@code null} if we are not
	 * in playback or monitor mode.
	 */
	private EPlaybackSockEventsMode eventsListeningMode = null;
	
	private List<IMythEventPacketListener> eventPacketListeners = new CopyOnWriteArrayList<IMythEventPacketListener>();
	
	private Map<Class<?>,List<IMythEventListener>> eventListeners = new ConcurrentHashMap<Class<?>,List<IMythEventListener>>();
	
	private ExecutorService eventDispatcherThreads = null;
	
	/**
	 * @param hostname the MythTV-backend hostname to connect to
	 */
	public Backend(String hostname) {
		this(hostname,IBackendConnection.DEFAULT_COMMAND_PORT);
	}
	
	/**
	 * 
	 * @param hostname the MythTV-backend hostname to connect to
	 * @param hostPort the MythTV-backend port to connect to
	 */
	public Backend(String hostname, int hostPort) {
		this.hostName = hostname;
		this.hostPort = hostPort;
	}
	
	/**
	 * Constructs the backend object from an existing backend connection.
	 * 
	 * @param backendConnection
	 * 		the existing backend connection
	 */
	public Backend(IBackendConnection backendConnection) {
		if(backendConnection == null) throw new NullPointerException();
		this.cmdConnection = backendConnection;
		this.protoVersion = this.cmdConnection.getVersionNr();
		this.hostName = this.cmdConnection.getHostname();
		this.hostPort = this.cmdConnection.getPort();
	}
	
	/**
	 * @return the {@link BackendConnection} used by this backend, or {@code null} if
	 * {@link #connect()} was not called so far.
	 */
	public IBackendConnection getCommandConnection() {
		return this.cmdConnection;
	}
	
	protected IBackendConnection createCommandConnection(String hostname, int hostPort) {
		return new BackendConnection(hostname,hostPort);
	}
	
	public String getHostName() {
		return this.hostName;
	}
	
	public int getHostPort() {
		return this.hostPort;
	}
	
	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}
	
	public void setInitialVersionNr(ProtocolVersion protoVersion) {
		if(!debugging && this.isConnected()) throw new IllegalStateException("Connection already opened");
		this.protoVersion = protoVersion;
	}
	
	public boolean isConnected() {
		if(this.cmdConnection == null) return false;
		return !this.cmdConnection.isClosed();
	}
	
	public void connect() throws IOException {
		this.connect(null);
	}
	
	public void connect(Integer connectionTimeout) throws IOException {
		this.cmdConnection = this.createCommandConnection(this.hostName, this.hostPort);
		this.cmdConnection.setInitialVersionNr(this.protoVersion);
		if(connectionTimeout != null) {
			this.cmdConnection.setConnectTimeout(connectionTimeout.intValue());
		}
		this.cmdConnection.open();
		this.protoVersion = this.cmdConnection.getVersionNr();
	}
	
	public void disconnect() {
		if (this.cmdConnection != null) {
			this.cmdConnection.close();
			this.cmdConnection = null;
		}
		if(this.eventDispatcherThreads != null) {
			this.eventDispatcherThreads.shutdown();
		}
	}
	
	public void close() throws IOException {
		this.disconnect();
	}
	
	public void addEventPacketListener(IMythEventPacketListener listener) {
		this.eventPacketListeners.add(listener);
	}	
	
	public void removeEventPacketListener(IMythEventPacketListener listener) {
		this.eventPacketListeners.remove(listener);
	}	
	
	public <Event extends IMythEvent<?>>  void addEventListener(
		Class<Event> eventClass, IMythEventListener<Event> listener
	) {
		List<IMythEventListener> listeners = null;
		if(this.eventListeners.containsKey(eventClass)) {
			listeners = this.eventListeners.get(eventClass);
		} else {
			listeners = new CopyOnWriteArrayList<IMythEventListener>();
			this.eventListeners.put(eventClass,listeners);
		}
		
		listeners.add(listener);
	}
	
	public <Event extends IMythEvent<?>> void removeEventListener(
		Class<Event> eventClass, IMythEventListener<Event> listener
	) {
		if(!this.eventListeners.containsKey(eventClass)) return;
		
		final List<IMythEventListener> listeners = this.eventListeners.get(eventClass);
		listeners.remove(listener);
	}
	
	public void fireEvent(final IMythPacket eventPacket) {
		if(this.eventListeners.isEmpty() && this.eventPacketListeners.isEmpty()) return;
				
		// passing event packet to packet listeners
		for(final IMythEventPacketListener packetListener : this.eventPacketListeners) {
			this.eventDispatcherThreads.submit(new Runnable() {				
				public void run() {
					try {
						packetListener.fireEvent(eventPacket);
					} catch (Throwable e) {
						logger.log(Level.WARNING,String.format(
							"Unexpected error while passing event-packet to listener %s.",
							packetListener.getClass().getName()
						),e);
					}	
				}
			});		
		}
		
		// pass event object to object listeners
		try {
			// converting the packet into an event object
			final IMythEvent<?> event = (eventPacket instanceof ClientErrorPacket) 
				? EventUtils.readFrom(ClientErrorEvent.class,eventPacket)
				: EventUtils.readFrom(eventPacket);
			
			if(event == null) {
				logger.warning(String.format(
					"Unable to read unknown event type: %s",
					eventPacket
				));
				return;
			}
			
			// determine if we have any listeners for the given event packet
			for(Entry<Class<?>,List<IMythEventListener>> entry : this.eventListeners.entrySet()) {
				final Class<?> eventClass = entry.getKey();
				
				if(eventClass.isAssignableFrom(event.getClass())) {
					final List<IMythEventListener> listeners = entry.getValue();
					if(listeners == null || listeners.isEmpty()) continue;
					
					for(final IMythEventListener listener : listeners) {
						if(listener == null) continue;
						
						this.eventDispatcherThreads.submit(new Runnable() {
							public void run() {
								try {
									listener.fireEvent(event);
								} catch (Throwable e) {
									logger.log(Level.WARNING,String.format(
										"Unexpected error while passing event to listener %s.",
										listener.getClass().getName()
									),e);
								}
							}
						});
					}
				}
			}
			
		} catch (UnknownCommandException e) {
			logger.log(Level.WARNING,"Unsupported event packet.",e);
		} catch (IllegalArgumentException e) {
			logger.log(Level.WARNING,"Unexpected event packet format.",e);
		}

	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_85)
	public boolean annotateFrontend() throws IOException {
		return this.annotateFrontend(RequestUtils.getHostname());
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_85)
	public boolean annotateFrontend(String clientName) throws IOException {
		return this.annotateFrontend(clientName, EPlaybackSockEventsMode.NONE);
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_85)
	public boolean annotateFrontend(String clientName, EPlaybackSockEventsMode eventsMode) throws IOException {
		return this.annotatePlaybackOrMonitor(IMythCommand.ANN_FRONTEND, clientName, eventsMode);
	}

	public boolean annotatePlayback() throws IOException {
		return this.annotatePlayback(RequestUtils.getHostname());
	}

	public boolean annotatePlayback(String clientName) throws IOException {
		return this.annotatePlayback(clientName, EPlaybackSockEventsMode.NONE);
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean annotatePlayback(String clientName, EPlaybackSockEventsMode eventsMode) throws IOException {
		return this.annotatePlaybackOrMonitor(IMythCommand.ANN_PLAYBACK, clientName, eventsMode);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_22)
	public boolean annotateMonitor() throws IOException {
		return this.annotateMonitor(RequestUtils.getHostname());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_22)
	public boolean annotateMonitor(String clientName) throws IOException {
		return this.annotateMonitor(clientName, EPlaybackSockEventsMode.NONE);
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_22)
	public boolean annotateMonitor(String clientName, EPlaybackSockEventsMode eventsMode) throws IOException {
		return this.annotatePlaybackOrMonitor(IMythCommand.ANN_MONITOR, clientName, eventsMode);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	private boolean annotatePlaybackOrMonitor(String connectionType, String clientName, EPlaybackSockEventsMode eventsMode) throws IOException {
		if(connectionType == null) {
			throw new NullPointerException("No connection type specified");
		} else if(!connectionType.equals(ANN_PLAYBACK) && !connectionType.equals(ANN_MONITOR)) {
			throw new ProtocolException("Unknown connection type " + connectionType); 
		}
		
		// monitor connections are supported starting with version 22
		if(connectionType.equalsIgnoreCase(ANN_MONITOR) && this.protoVersion.compareTo(PROTO_VERSION_22)<0) {
			this.logger.warning(String.format(
				"'ANN %s' is not supported in protocol version '%s'. Fallback to 'ANN %s'.",
				ANN_MONITOR, this.protoVersion, ANN_PLAYBACK
			));
			connectionType = ANN_PLAYBACK;
		}
		
		// setting the client name if required
		if (clientName == null || clientName.length() == 0) {
			clientName = RequestUtils.getHostname();
		}
		
		// setting the events mode iv required
		if (eventsMode == null) {
			eventsMode = EPlaybackSockEventsMode.NONE;
		} else if (eventsMode.ordinal() > 1 && this.protoVersion.compareTo(PROTO_VERSION_57)<0) {
			this.logger.warning(String.format(
				"'The event mode %s' is not supported in protocol version '%s'. Fallback to '%s'.",
				eventsMode, this.protoVersion, EPlaybackSockEventsMode.NORMAL
			));			
			
			// other events mode are only supported with protocol version >= 57
			eventsMode = EPlaybackSockEventsMode.NORMAL;
		}
		this.eventsListeningMode = eventsMode;
		
		// send the ANNOTATION request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.ANN,
				connectionType,
				clientName,
				Integer.toString(eventsMode.ordinal())
			)
		));
		
		// starting the packet receiver thread
		if(!eventsMode.equals(EPlaybackSockEventsMode.NONE)) {
			this.eventDispatcherThreads = Executors.newCachedThreadPool();
			this.cmdConnection.addEventListener(this);
			this.cmdConnection.enableEventListening();
		}
		
		// reading the response
		final IMythPacket resp = this.cmdConnection.readPacket();
		boolean success = (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
		if(!success) {
			logger.severe(String.format(
				"Unable to annotate %s. Backend returned: %s",
				eventsMode, resp
			));
		}
		
		return success;
	}
	
	@MythProtoVersionAnnotation(from = PROTO_VERSION_00)
	public IFileTransfer annotateFileTransfer(IProgramInfo programInfo) throws IOException {
		return this.annotateFileTransfer(programInfo,null,null,null);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public FileTransfer annotateFileTransfer(
			IProgramInfo programInfo,
			@MythProtoVersionAnnotation(from=PROTO_VERSION_29)
			Boolean useReadAhead,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_29,to=PROTO_VERSION_60)
			Integer retries,
			@MythProtoVersionAnnotation(from = PROTO_VERSION_60)
			Integer timeoutMs
	) throws IOException {
		if (programInfo == null) throw new NullPointerException("No programinfo specified.");
		
		final String fileName = programInfo.getPathName();
		final String storageGroup = programInfo.getStorageGroup();
		
		final FileTransfer fileTransfer = this.annotateFileTransfer(fileName, useReadAhead, retries, timeoutMs, storageGroup);
		fileTransfer.setProgramInfo(programInfo);
		return fileTransfer;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public FileTransfer annotateFileTransfer(URI fileUrl) throws IOException {
		final String storageGroup = fileUrl.getUserInfo();
		final String fileName = fileUrl.getPath();
		return this.annotateFileTransfer(fileName,null,null,null,storageGroup);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public FileTransfer annotateFileTransfer(
		String fileName,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
		String storageGroup
		
	) throws IOException {
		return this.annotateFileTransfer(fileName,null,null,null,storageGroup);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public FileTransfer annotateFileTransfer(
		String fileName,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_29)
		Boolean useReadAhead,
		@MythProtoVersionAnnotation(from = PROTO_VERSION_29,to=PROTO_VERSION_60)
		Integer retries,
		@MythProtoVersionAnnotation(from = PROTO_VERSION_60)
		Integer timeoutMs,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
		String storageGroup
		
	) throws IOException {
		if (fileName == null || fileName.length() == 0) throw new IllegalArgumentException("No file name specified");
		
		// open a new data connection
		final BackendConnection dataConnection = new BackendConnection(this.hostName);
		dataConnection.setInitialVersionNr(this.protoVersion);
		dataConnection.open(128*1024,true);
		
		Boolean writeMode = Boolean.FALSE; // TODO: (added version 46)		
		if (useReadAhead==null) {
			useReadAhead = Boolean.TRUE;
		}
		if (timeoutMs == null) {
			if(retries != null && retries.intValue() > 0) {
				timeoutMs = Integer.valueOf(retries.intValue() * 500);
			} else {
				timeoutMs = Integer.valueOf(2000);
			}			
		}
		if (retries == null) {
			if(timeoutMs != null && timeoutMs.intValue() > 500) {
				retries = Integer.valueOf(timeoutMs.intValue() / 500);
			} else {
				retries = Integer.valueOf(-1);
			}
		}
		if (storageGroup == null) {
		}
		final String clientName = RequestUtils.getHostname();
		
		// 0 <= protocol < 29
		if (this.protoVersion.compareTo(PROTO_VERSION_29) < 0) {
			dataConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.ANN,
					IMythCommand.ANN_FILE_TRANSFER,
					clientName
				),
				fileName
			));
		} 
		
		// 29 <= protocol < 44
		else if (this.protoVersion.compareTo(PROTO_VERSION_44) < 0) {
			dataConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.ANN,
					IMythCommand.ANN_FILE_TRANSFER,
					clientName,
					useReadAhead.booleanValue()?"1":"0", // [3]
					retries.toString()					 // [4]
				),
				fileName
			));
		} 
		
		// 44 <= protocol < 46
		else if (this.protoVersion.compareTo(PROTO_VERSION_46) < 0) {
			dataConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.ANN,
					IMythCommand.ANN_FILE_TRANSFER,
					clientName,
					useReadAhead.booleanValue()?"1":"0", // [3] 
					retries.toString()                   // [4]
				),
				fileName,
				storageGroup	// added in version 44
			));			
		} 
		
		// 46 <= protocol < 60
		else if (this.protoVersion.compareTo(PROTO_VERSION_60) < 0) {
			dataConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.ANN,
					IMythCommand.ANN_FILE_TRANSFER,
					clientName,
					writeMode.booleanValue()?"1":"0",	 // [3] 
					useReadAhead.booleanValue()?"1":"0", // [4] 
					retries.toString()                   // [5]
				),
				fileName,
				storageGroup
			));
		}
		
		// 60 <= protocol
		else  {
			dataConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.ANN,
					IMythCommand.ANN_FILE_TRANSFER,
					clientName,
					writeMode.booleanValue()?"1":"0",	 // [3] 
					useReadAhead.booleanValue()?"1":"0", // [4] 
					timeoutMs.toString()                 // [5]
				),
				fileName,
				storageGroup
			));			
		}
		
		// read response
		final IMythPacket resp = dataConnection.readPacket();
		if (!resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) {
			dataConnection.close();
			return null;
		} else if (resp.getPacketArgs().size() != 4 && resp.getVersionNr().compareTo(PROTO_VERSION_66)< 0) {
			dataConnection.close();
			throw new IllegalStateException("Something went wrong");
		} else if (resp.getPacketArgs().size() != 3 && resp.getVersionNr().compareTo(PROTO_VERSION_66)>= 0) {
			dataConnection.close();
			throw new IllegalStateException("Something went wrong");
		}
		
		final FileTransfer fileTransfer = new FileTransfer(this.cmdConnection, dataConnection, resp);
		fileTransfer.setFileInfo(fileName, storageGroup);
		return fileTransfer;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Recorder getRecorder(IRecorderInfo recorderInfo) throws IOException {
		if (recorderInfo == null) return null;

		// TODO: connect to the recorder if necessary
		// TODO: the recorder my be located at a different host		
		return new Recorder(this.cmdConnection, recorderInfo.getRecorderID());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public IRecorderInfo getNextFreeRecorder() throws IOException {
		return this.getNextFreeRecorder(Integer.valueOf(-1));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public IRecorderInfo getNextFreeRecorder(IRecorderInfo currentRecorder) throws IOException {
		return this.getNextFreeRecorder(Integer.valueOf(currentRecorder==null?-1:currentRecorder.getRecorderID()));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public IRecorderInfo getNextFreeRecorder(Integer currentRecorderID) throws IOException {
		if (currentRecorderID == null) currentRecorderID = Integer.valueOf(-1);
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GET_NEXT_FREE_RECORDER
			),
			currentRecorderID.toString()
		));
		
		// read response		
		final IMythPacket resp = this.cmdConnection.readPacket();
		final RecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
		return recorderInfo;
	}			
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00, to=PROTO_VERSION_87)
	public IRecorderInfo getFreeRecorder() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GET_FREE_RECORDER
			)
		));
		
		// read response		
		final IMythPacket resp = this.cmdConnection.readPacket();
		final IRecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
		return recorderInfo;
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public IRecorderInfo getFreeRecorder(String channelNumber) throws IOException {
		// getting all free recorders
		final List<IRecorderInfo> freeRecorders = this.getFreeRecorders();
		if(freeRecorders.isEmpty()) return null;
		
		for(IRecorderInfo recorderInfo : freeRecorders) {
			// connect to the recorder
			final IRecorder recorder = this.getRecorder(recorderInfo);
			
			// check if the channel is known by the recorder
			final boolean found = recorder.checkChannel(channelNumber);
			
			// close recorder connection
			recorder.close();
			
			// if found return
			if(found) return recorderInfo;
		}
		
		return null;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public List<IRecorderInfo> getRecorders() throws IOException {
		final List<IRecorderInfo> recorders = new ResultList<IRecorderInfo>();
		
		int recorderIdx = 1, recorderCount = 0, failCount = 0;
		IRecorderInfo recorderInfo = null;
		do {
			recorderInfo = this.getRecorderForNum(recorderIdx);
			if(recorderInfo != null) {
				recorders.add(recorderInfo);
				recorderCount++;
			} else {
				failCount++;
			}
			recorderIdx++;			
		} while (recorderInfo != null || (failCount < 10));
		
		return recorders;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public int[] getRecorderIDs() throws IOException {
		final List<IRecorderInfo> recorders = this.getRecorders();
		
		final int[] recordeIDs = new int[recorders.size()];
		for(int i=0; i<recorders.size(); i++) {
			recordeIDs[i] = recorders.get(i).getRecorderID();
		}
		return recordeIDs;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecorderInfo getRecorderForNum(int recorderId) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GET_RECORDER_FROM_NUM
			),
			Integer.toString(recorderId)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		
		// change the response array
		final List<String> responseArgs = resp.getPacketArgs();
		responseArgs.add(0,Integer.toString(recorderId));
		if(!RecorderInfo.recorderFound(responseArgs)) return null;
		
		final IRecorderInfo recorderInfo = new RecorderInfo(this.protoVersion, responseArgs);
		return recorderInfo;
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecorderInfo getRecorderNum(IProgramInfo programInfo) throws IOException {
		return getRecorderNum(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecorderInfo getRecorderNum(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GET_RECORDER_NUM
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final IRecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
		return recorderInfo;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_09, fromFallback=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public int getFreeRecorderCount() throws IOException {
		// ============== PROTO_VERSION >= 03 ==============
		if(this.protoVersion.compareTo(PROTO_VERSION_09)<0) {
			final int[] freeRecIds = this.getFreeRecorderIDs();
			return freeRecIds.length;
		}		
		
		// ============== PROTO_VERSION >= 09 ==============
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GET_FREE_RECORDER_COUNT
			)
		));		
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return Integer.valueOf(resp.getPacketArg(0)).intValue();
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromFallback=PROTO_VERSION_03, to=PROTO_VERSION_87)
	public int[] getFreeRecorderIDs() throws IOException {
		// ============== PROTO_VERSION >= 3 AND PROTO_VERSION < 17 ============== 
		if(this.protoVersion.compareTo(PROTO_VERSION_03)>=0 && this.protoVersion.compareTo(PROTO_VERSION_17)<0) {
			final SortedSet<Integer> freeRecorderIdList = new TreeSet<Integer>();
			Integer recorderId = Integer.valueOf(-1);
			
			// collect all free recorder IDs
			IRecorderInfo recorderInfo = null;
			do {
				recorderInfo = this.getNextFreeRecorder(recorderId);
				if(recorderInfo != null) {
					recorderId = Integer.valueOf(recorderInfo.getRecorderID());
					if(freeRecorderIdList.contains(recorderId)) break;					
					freeRecorderIdList.add(recorderInfo.getRecorderID());
				}
			} while (recorderInfo != null);
			
			// convert to an array
			int[] ids = new int[freeRecorderIdList.size()];
			int idx = 0;
			for(Integer freeRecorderId : freeRecorderIdList) {
				ids[idx] = freeRecorderId.intValue();
				idx++;
			}
			return ids;
		} 
		
		// ============== PROTO_VERSION >= 17 ==============

		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GET_FREE_RECORDER_LIST
			)
		));		
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final List<String> args = resp.getPacketArgs();
		if (args.get(0).equals("0")) return new int[0];
		
		final int[] ids = new int[resp.getPacketArgs().size()];		
		for (int i=0; i < args.size(); i++) {
			ids[i] = Integer.valueOf(args.get(i)).intValue();
		}
		return ids;		
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public List<IRecorderInfo> getFreeRecorders() throws IOException {
		// get the ID of all free recorders
		int[] recIds = this.getFreeRecorderIDs();
		if(recIds == null || recIds.length == 0) return Collections.emptyList();
		
		// fetch the recorder-info object for all IDs
		final List<IRecorderInfo> freeRecorders = new ResultList<IRecorderInfo>();
		for(int recId : recIds) {
			final IRecorderInfo recInfo = this.getRecorderForNum(recId);
			if(recInfo != null) freeRecorders.add(recInfo);
		}
		return freeRecorders;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_03)
	public boolean hasFreeRecorders() throws IOException {
		final int freeRecorderCount = this.getFreeRecorderCount();
		return freeRecorderCount > 0;
	}
	
	public void recordingListChange() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.BACKEND_MESSAGE
			),
			IMythCommand.BACKEND_MESSAGE_RECORDING_LIST_CHANGE,
			"empty"
		));
//		IMythPacket resp = this.connection.readMessage();	
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_20)
	public void liveTvChainUpdate(String chainId) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.BACKEND_MESSAGE
			),
			IMythCommand.BACKEND_MESSAGE_LIVETV_CHAIN + " UPDATE " + chainId,
			"empty"
		));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IProgramInfoList queryRecordings() throws IOException {
		return this.queryRecordings(ERecordingsType.Play);
	}
		
	/**
	 * TODO: sorting for versions >= 65
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IProgramInfoList queryRecordings(ERecordingsType eRecordingsType) throws IOException {
		// set default value
		if (eRecordingsType == null) {
			if(this.protoVersion.compareTo(PROTO_VERSION_65)<0) {
				eRecordingsType = ERecordingsType.Play;
			} else {
				eRecordingsType = ERecordingsType.Ascending;
			}
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDINGS,
				eRecordingsType.name()
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final ProgramInfoList recordings = ResponseUtils.readFrom(ProgramInfoList.class, resp);
		if(recordings.isEmpty()) return recordings;
		
		/*
		 * Prior to protocol version 19 the recording-status was not set properly
		 * in mainserver.cpp:
		 * 
		 * ----- 
		 * void MainServer::HandleQueryRecordings(QString type, PlaybackSock *pbs)
		 * -----
		 * 
		 * We need to correct the UNKNOWN recording status here.
		 */
		if(this.protoVersion.compareTo(PROTO_VERSION_19)<=0) {
			Map<String,IProgramInfo> inProgressRecordings = null;
			
			/*
			 * If mythtv is currently recording we need to determine all currently running recordings
			 */
			final IRecordingStatus recordingStatus = this.queryIsRecording();
			if(recordingStatus.isRecording()) {
				final IRecordingsPending pendingRecordings = this.queryAllPending();
				inProgressRecordings = pendingRecordings.getProgramInfoList(ProgramInfoFilters.status(Status.RECORDING)).asMap(MapKey.UNIQUE_RECORDING_ID);
			} else {
				inProgressRecordings = Collections.emptyMap();
			}
			
			// loop through all recordings and change the status
			for(IProgramInfo recording : recordings) {				
				final String uniqueRecId = recording.getUniqueRecordingId();
				Status newStatus = null;				
				
				// determine the new status to use
				if(inProgressRecordings.containsKey(uniqueRecId)) {
					newStatus = Status.RECORDING;
				} else {
					newStatus = Status.RECORDED;
				}
				
				// change the status
				recording.setPropertyValueObject(
					IProgramInfo.Props.REC_STATUS,
					ProgramRecordingStatus.valueOf(this.protoVersion,newStatus)
				);
			}
		}
		
		return recordings;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_61,toFallback=PROTO_VERSION_LATEST)
	public boolean queryGenPixmap(IProgramInfo program) throws IOException {
		return this.queryGenPixmap(program,null,null,null,null,null);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_61,toFallback=PROTO_VERSION_LATEST)
	public boolean queryGenPixmap(IProgramInfo program, Boolean inSeconds, Long time, String fileName, Integer width, Integer height) throws IOException {
		return this.queryGenExtraPixmap(program.getPropertyValues(),inSeconds,time,fileName,width,height);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_61,toFallback=PROTO_VERSION_LATEST)
	private boolean queryGenExtraPixmap(
		final List<String> programInfo, Boolean inSeconds, Long time, String fileName, Integer width, Integer height
	) throws IOException {
		// Fallback for PROTO_VERSION > 60
		if(this.protoVersion.compareTo(PROTO_VERSION_61)>=0) {
			IMythEventListener<IPixmapGenerated> eventListener = null;
			try {
				// generate a unique token
				final String token = String.format("PIXMAP_%d",System.currentTimeMillis());
				
				// register an event listener to wait for the pixmap file
				final Semaphore pixmapGenerated = new Semaphore(0);
				eventListener = new IMythEventListener<IPixmapGenerated>() {
					public void fireEvent(IPixmapGenerated event) {
						if(token.equals(event.getToken())) {
							// PIXMAP GENERATED
							pixmapGenerated.release();						
						}						
					}
				};
				this.addEventListener(IPixmapGenerated.class,eventListener);					
				
				// trigger pixmap generation 
				boolean success = this.queryGenPixmap2(token,programInfo,inSeconds,time,fileName,width,height);
				if(success) {
					// wait for max 30 seconds
					success = pixmapGenerated.tryAcquire(30,TimeUnit.SECONDS);
				}
				return success;
			} catch (InterruptedException e) {
				// nothing todo here
				return false;
			} finally {
				if(eventListener != null) this.removeEventListener(IPixmapGenerated.class,eventListener);
			}				
		}
		
		// create arguments
		final ArrayList<String> args = new ArrayList<String>();
		args.addAll(programInfo);
		if(inSeconds != null && this.protoVersion.compareTo(PROTO_VERSION_37)>=0) {
			// time in seconds or frame
			args.add(inSeconds.booleanValue()?"s":"f");
			
			// time or frame count
			if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) { // TODO: is this the correct version number?
				final String[] timeParts = EncodingUtils.encodeLong(time);
				args.add(timeParts[0]);
				args.add(timeParts[1]);
			} else {
				args.add(time.toString());
			}
			
			// file name
			args.add(fileName);
			
			// width and height
			args.add(width==null?"-1":width.toString());
			args.add(height==null?"-1":height.toString());
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GENPIXMAP
			),
			args
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_61,fromFallback=PROTO_VERSION_00)
	public boolean queryGenPixmap2(String token, IProgramInfo program) throws IOException {
		return this.queryGenPixmap2(token, program.getPropertyValues(),null,null,null,null,null);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_61,fromFallback=PROTO_VERSION_00)
	public boolean queryGenPixmap2(String token, IProgramInfo program, Boolean inSeconds, Long time, String fileName, Integer width, Integer height) throws IOException {
		return this.queryGenPixmap2(token, program.getPropertyValues(),inSeconds,time,fileName,width,height);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_61,fromFallback=PROTO_VERSION_00)
	private boolean queryGenPixmap2(
		String token, List<String> programInfo,Boolean inSeconds, Long time, String fileName, Integer width, Integer height
	) throws IOException {
		// Fallback for PROTO_VERSION < 61
		if(this.protoVersion.compareTo(PROTO_VERSION_61)<0) {
			return this.queryGenExtraPixmap(programInfo,inSeconds,time,fileName,width,height);
		}		
		
		if(token == null || token.length() == 0) token = "do_not_care";
		final ArrayList<String> args = new ArrayList<String>();
		args.add(token);
		args.addAll(programInfo);
		if(inSeconds != null) {
			// time in seconds or frame
			args.add(inSeconds.booleanValue()?"s":"f");
			
			// time or frame count
			if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) { // TODO: is this the correct version number?
				final String[] timeParts = EncodingUtils.encodeLong(time);
				args.add(timeParts[0]);
				args.add(timeParts[1]);
			} else {
				args.add(time.toString());
			}
			
			// file name
			args.add(fileName);
			
			// width and height
			args.add(width==null?"-1":width.toString());
			args.add(height==null?"-1":height.toString());
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GENPIXMAP2
			),
			args
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);		
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IPixmap queryPixmap(String previewImageName, String storageGroup) throws IOException {
		// init a file transfer
		final IFileTransfer fileTransfer = this.annotateFileTransfer(previewImageName,storageGroup);
		if(!fileTransfer.isOpen()) return null;
		return Pixmap.valueOf(fileTransfer,null); 
	}	
	
	
	public IPixmap queryPixmap(IProgramInfo program) throws IOException {
		return this.queryPixmapIfModified(program,null,null);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Date queryPixmapLastModified(IProgramInfo program) throws IOException {
		return this.queryPixmapLastModified(program.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	private Date queryPixmapLastModified(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_PIXMAP_LASTMODIFIED
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final String respStr = resp.getPacketArg(0);
		if(respStr.length() == 0 || respStr.equalsIgnoreCase("BAD")) return null;
		return EncodingUtils.decodeString(Date.class,this.protoVersion,respStr);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49, fromFallback=PROTO_VERSION_17)
	public IPixmap queryPixmapIfModified(IProgramInfo program, Date lastModifiedDate, Long maxFileSize) throws IOException {
		if(maxFileSize == null) maxFileSize = Long.valueOf(10*1240*1024);
		
		// FALLBACK for protoVersion < PROTO_VERSION_49
		if(this.protoVersion.compareTo(PROTO_VERSION_49) < 0) {
			// extracting the required data from the program
			final String previewImageName = program.getPreviewImageName();
			final String storageGroup = program.getStorageGroup();
			
			// check the last modified date
			Date cacheModified = null;
			if(this.protoVersion.compareTo(PROTO_VERSION_17)>=0) {
				cacheModified = this.queryPixmapLastModified(program);
					
				// pixmap not found
				if(cacheModified == null) return null;
				
				// pixmap not modified or file size == 0
				else if(maxFileSize.longValue()<=0 || (lastModifiedDate != null && lastModifiedDate.before(cacheModified))) {
					// just return the last-modified-timestamp
					return Pixmap.valueOf(this.protoVersion, lastModifiedDate);
				}
			}
			
			// init a file transfer
			final IFileTransfer fileTransfer = this.annotateFileTransfer(previewImageName,storageGroup);
			
			// pixmap modified
			if(fileTransfer.getFileSize() < maxFileSize.longValue()) {
				// return the full data
				return Pixmap.valueOf(fileTransfer,cacheModified);
			} 
			
			// pixmap is to big
			else {
				// return null
				return null;
			}
		}
		
		return this.queryPixmapIfModified(program.getPropertyValues(), lastModifiedDate, maxFileSize);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49)
	private IPixmap queryPixmapIfModified(List<String> program, Date lastModifiedDate, Long maxFileSize) throws IOException {
		// request arguments
		final ArrayList<String> args = new ArrayList<String>();
		args.add(Long.toString(lastModifiedDate==null?-1l:lastModifiedDate.getTime() / 1000));
		args.add(Long.toString(maxFileSize==null?0:maxFileSize.longValue()));
		args.addAll(program);
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_PIXMAP_GET_IF_MODIFIED
			),
			args
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final String respStr = resp.getPacketArg(0);
		if(respStr.equalsIgnoreCase("ERROR")) {
			logger.warning(String.format(
				"Unable to download preview image: %s",
				resp.getPacketArgsLength()>1?resp.getPacketArg(1):"Unknown"
			));
			return null;
		} else if (respStr.equalsIgnoreCase("WARNING")) {
			logger.info(String.format(
				"Preview image not downloaded: %s",
				resp.getPacketArgsLength()>1?resp.getPacketArg(1):"Unknown"
			));
			return null;
		} 
		
		return ResponseUtils.readFrom(Pixmap.class,resp);		
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_41,fromFallback=PROTO_VERSION_00)
	public boolean deleteRecording(Integer channelID, Date recordingStartTime) throws IOException {
		// protoVersion < PROTO_VERSION_41
		if(this.protoVersion.compareTo(PROTO_VERSION_41)<0) {
			// query the recording
			final IProgramInfo programInfo = this.queryRecording(channelID, recordingStartTime);
			if(programInfo == null) return false;
			
			// delete the recording
			return this.deleteRecording(programInfo);
		}
		
		// protoVersion >= PROTO_VERSION_41
		return this.deleteRecording(channelID, recordingStartTime, Boolean.FALSE, Boolean.FALSE);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_41,fromFallback=PROTO_VERSION_16)
	public boolean deleteRecording(
		IProgramInfo programInfo,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_56)
		Boolean forceMetadatDelete,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		Boolean forgetHistory
	) throws IOException {
		// setting parameters to default values
		if(forceMetadatDelete == null) forceMetadatDelete = Boolean.FALSE;
		if(forgetHistory == null) forgetHistory = Boolean.FALSE;
				
		// fallback for earlier version
		if(
			// fallback for protoVersion < PROTO_VERSION_41
			(this.protoVersion.compareTo(PROTO_VERSION_41)<0) ||
			
			// fallback for protoVersion < PROTO_VERSION_56 + forceMetadatDelete
			(this.protoVersion.compareTo(PROTO_VERSION_56)<0 && forceMetadatDelete.booleanValue()) ||
			
			// fallback for protoVersion < PROTO_VERSION_59 + forgetHistory
		    (this.protoVersion.compareTo(PROTO_VERSION_59)<0 && forgetHistory.booleanValue())
		) {
			// delete recording history
			if(forgetHistory.booleanValue()) {
				this.forgetRecording(programInfo);
			}
						
			if(forceMetadatDelete.booleanValue() && this.protoVersion.compareTo(PROTO_VERSION_16)>=0) {
				// delete recording and metadata
				return this.forceDeleteRecording(programInfo);
			} else {
				// delete recording
				return this.deleteRecording(programInfo);
			}
		}
		
		// delete recording + metadata + history
		return this.deleteRecording(programInfo.getChannelID(),programInfo.getRecordingStartTime(),forceMetadatDelete,forgetHistory);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_41,fromFallback=PROTO_VERSION_16)
	public boolean deleteRecording(
		Integer channelID, Date recordingStartTime, 
		@MythProtoVersionAnnotation(from=PROTO_VERSION_56)
		Boolean forceMetadatDelete,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		Boolean forgetHistory
	) throws IOException {
		// setting parameters to default values
		if(forceMetadatDelete == null) forceMetadatDelete = Boolean.FALSE;
		if(forgetHistory == null) forgetHistory = Boolean.FALSE;
				
		// fallback for earlier version
		if(
			// fallback for protoVersion < PROTO_VERSION_41
			(this.protoVersion.compareTo(PROTO_VERSION_41)<0) ||
			
			// fallback for protoVersion < PROTO_VERSION_56 + forceMetadatDelete
			(this.protoVersion.compareTo(PROTO_VERSION_56)<0 && forceMetadatDelete.booleanValue()) ||
			
			// fallback for protoVersion < PROTO_VERSION_59 + forgetHistory
		    (this.protoVersion.compareTo(PROTO_VERSION_59)<0 && forgetHistory.booleanValue())
		) {
			// query the recording
			final IProgramInfo programInfo = this.queryRecording(channelID, recordingStartTime);
			if(programInfo == null) return false;
			
			// delete recording history
			if(forgetHistory.booleanValue()) {
				this.forgetRecording(programInfo);
			}
						
			if(forceMetadatDelete.booleanValue() && this.protoVersion.compareTo(PROTO_VERSION_16)>=0) {
				// delete recording and metadata
				return this.forceDeleteRecording(programInfo);
			} else {
				// delete recording
				return this.deleteRecording(programInfo);
			}
		}
				
		// command arguments
		final ArrayList<String> args = new ArrayList<String>();
		args.add(channelID.toString());
		args.add(EncodingUtils.formatDateTime(recordingStartTime, false));	
		
		// delete metadat
		if(this.protoVersion.compareTo(PROTO_VERSION_56)>=0) {
			args.add(forceMetadatDelete.booleanValue()?"FORCE":"NO_FORCE");
		}
		
		// forget history
		if(this.protoVersion.compareTo(PROTO_VERSION_59)>=0) {
			args.add(forgetHistory.booleanValue()?"FORGET":"NO_FORGET");
		}
		
		// write request		
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.DELETE_RECORDING,
				args
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Integer statusCode = Integer.valueOf(resp.getPacketArg(0));
		if(statusCode.intValue() >= 0) {
			logger.info(String.format(
				"Encoder %d has stopped recording.",
				statusCode
			));
			
		} else if(statusCode.intValue() == -2) {
			logger.warning("Unable to delete recording-file. File doesn't exist.");
		}
		return statusCode.compareTo(-1) >= 0;
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean deleteRecording(IProgramInfo programInfo) throws IOException {
		return this.deleteRecording(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	private boolean deleteRecording(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.DELETE_RECORDING
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Integer statusCode = Integer.valueOf(resp.getPacketArg(0));
		if(statusCode.intValue() >= 0) {
			logger.info(String.format(
				"Encoder %d has stopped recording the file '%s'.",
				statusCode,
				programInfo.get(EnumUtils.getEnumPosition(IProgramInfo.Props.PATH_NAME,this.protoVersion))
			));
			
		} else if(statusCode.intValue() == -2) {
			logger.warning(String.format(
				"Unable to delete recording-file '%s'. File doesn't exist.",
				programInfo.get(EnumUtils.getEnumPosition(IProgramInfo.Props.PATH_NAME,this.protoVersion))
			));
		}
		return statusCode.compareTo(-1) >= 0;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_36)
	public boolean undeleteRecording(IProgramInfo programInfo) throws IOException {
		return this.undeleteRecording(programInfo.getPropertyValues());
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_36)
	private boolean undeleteRecording(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.UNDELETE_RECORDING
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equals("0");
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean forgetRecording(IProgramInfo programInfo) throws IOException {
		return this.forgetRecording(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean forgetRecording(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.FORGET_RECORDING
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equals("0");
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_16,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT, value="834fedd51d451da70f40"),
		@MythProtoVersionMetadata(key=SVN_COMMIT, value="6235")
	})
	public boolean forceDeleteRecording(IProgramInfo programInfo) throws IOException {
		return this.forceDeleteRecording(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_16,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT, value="834fedd51d451da70f40"),
		@MythProtoVersionMetadata(key=SVN_COMMIT, value="6235")
	})
	public boolean forceDeleteRecording(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.FORCE_DELETE_RECORDING
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Integer statusCode = Integer.valueOf(resp.getPacketArg(0));
		
		if(statusCode.intValue() > 0) {
			logger.info(String.format(
				"Encoder %d has stopped recording the file '%s'.",
				statusCode,
				programInfo.get(EnumUtils.getEnumPosition(IProgramInfo.Props.PATH_NAME,this.protoVersion))
			));
			
		} else if(statusCode.intValue() == -2) {
			logger.warning(String.format(
				"Unable to delete recording-file '%s'. File doesn't exist.",
				programInfo.get(EnumUtils.getEnumPosition(IProgramInfo.Props.PATH_NAME,this.protoVersion))
			));
		}
		return statusCode.compareTo(-1) >= 0;
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_19)
	public boolean reactivateRecording(IProgramInfo programInfo) throws IOException {
		return this.reactivateRecording(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_19)
	public boolean reactivateRecording(List<String> programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.REACTIVATE_RECORDING
			),
			programInfo
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return !resp.getPacketArg(0).equals("0");
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public Future<Boolean> rescheduleAllRecordings() throws IOException {
		return this.rescheduleRecordings(Integer.valueOf(-1));
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15, to=PROTO_VERSION_73)
	public Future<Boolean> rescheduleRecordings(Integer recordID) throws IOException {
		if (recordID == null) recordID = Integer.valueOf(-1);
		
		// fallback for protocol versions >= 73
		if(this.protoVersion.compareTo(PROTO_VERSION_73)>=0) {
			if(recordID.intValue() == 0) {
				// TODO: 
				/*
            		$this->sendCommand(array('RESCHEDULE_RECORDINGS ',
                                     'CHECK 0 0 0 PHP',
                                     '', '', '', '**any**'));
				 */
			} else {
				if (recordID.intValue() == -1) recordID = Integer.valueOf(0);
				return this.rescheduleRecordingsMatch(recordID,null,null,null,"Rescheduling of all recordings");
			}
		}
		
		final ScheduleChangedCallback eventListener = new ScheduleChangedCallback(this);
		if(this.eventsListeningMode != null && (this.eventsListeningMode.equals(NORMAL) || this.eventsListeningMode.equals(NON_SYSTEM))) {
			// registering an event listener			
			this.addEventListener(IScheduleChange.class,eventListener);
		}		
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.RESCHEDULE_RECORDINGS,
				Integer.toString(recordID)
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if(resp.getPacketArg(0).equalsIgnoreCase("0")) {
			eventListener.setFailed();
		}
		return eventListener;
	}
	
	public Future<Boolean> rescheduleRecordingsMatch(Integer recordID, Integer sourceID, Integer multiplexID, Date maxStartTime, String reason) throws IOException {
		if(recordID == null) recordID = Integer.valueOf(0);
		if(sourceID == null) sourceID = Integer.valueOf(0);
		if(multiplexID == null) multiplexID = Integer.valueOf(0);
		
		// fallback for protocol version < 73
		if(this.protoVersion.compareTo(PROTO_VERSION_73)<0) {
			if(recordID.intValue() == 0) recordID = Integer.valueOf(-1);
			return this.rescheduleRecordings(recordID);
		}
		
		final ScheduleChangedCallback eventListener = new ScheduleChangedCallback(this);
		if(this.eventsListeningMode != null && (this.eventsListeningMode.equals(NORMAL) || this.eventsListeningMode.equals(NON_SYSTEM))) {
			// registering an event listener			
			this.addEventListener(IScheduleChange.class,eventListener);
		}		
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.RESCHEDULE_RECORDINGS
			),
			String.format(
				"%s %d %d %d %s %s",
				"MATCH",
				recordID,
				sourceID,
				multiplexID,
				(maxStartTime==null)?"-":EncodingUtils.formatDateTime(maxStartTime, false),
				reason==null?"jMythAPI":reason
			)
		));		
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if(resp.getPacketArg(0).equalsIgnoreCase("0")) {
			eventListener.setFailed();
		}
		return eventListener;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IBasicFreeSpace getFreeSpaceOverview() throws IOException {
		if (this.protoVersion.compareTo(PROTO_VERSION_17)<0) {
			return this.queryFreeSpace();
		} else if (this.protoVersion.compareTo(PROTO_VERSION_17)>0 && this.protoVersion.compareTo(PROTO_VERSION_32)<0) {
			return this.queryFreeSpaceList(true);
		} else {
			return this.queryFreeSpaceSummary();
		}	
	}
	
	/**
	 * @deprecated {@mythProtoVersion 17}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_17,toFallback=PROTO_VERSION_LATEST)
	public FreeSpace queryFreeSpace() throws IOException {
		if (this.getVersionNr().compareTo(PROTO_VERSION_17) >= 0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_FREESPACE, this.protoVersion, IMythCommand.QUERY_FREE_SPACE_SUMMARY
			));
			
			final IBasicFreeSpace freeSpaceOverview = this.getFreeSpaceOverview();
			final FreeSpace freeSpace = new FreeSpace(
				this.getVersionNr(),
				freeSpaceOverview.getTotalSpace() / (1024*1024),
				freeSpaceOverview.getUsedSpace() / (1024*1024)
			);
			return freeSpace;
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_FREESPACE
			)
		));

		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(FreeSpace.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public FreeSpaceList queryFreeSpaceList(boolean allhosts) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				allhosts? IMythCommand.QUERY_FREE_SPACE_LIST : IMythCommand.QUERY_FREE_SPACE
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(FreeSpaceList.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	public FreeSpaceSummary queryFreeSpaceSummary() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_FREE_SPACE_SUMMARY
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(FreeSpaceSummary.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public ILoad queryLoad() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_LOAD
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(Load.class, resp);		
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public IUptime queryUptime() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_UPTIME
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final IUptime uptime = ResponseUtils.readFrom(Uptime.class, resp);
		
		return uptime;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public IMemStats queryMemStats() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_MEMSTATS
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if (resp.getPacketArg(0).equalsIgnoreCase("ERROR")) {
			throw new IOException(resp.getPacketArg(1));
		}
		
		final IMemStats memStats = ResponseUtils.readFrom(MemStats.class, resp);		
		return memStats;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public GuideDataThrough queryGuideDataThrough() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GUIDEDATATHROUGH
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final GuideDataThrough guideDataThrough = ResponseUtils.readFrom(GuideDataThrough.class, resp);
		
		return guideDataThrough;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecordingStatus queryIsRecording() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_ISRECORDING
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final IRecordingStatus recordingRecords = ResponseUtils.readFrom(RecordingStatus.class, resp);
		
		return recordingRecords;
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public RecordingsPending queryAllPending() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GETALLPENDING
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final RecordingsPending pendingRecords = ResponseUtils.readFrom(RecordingsPending.class, resp);
		
		return pendingRecords;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public RecordingsScheduled queryAllScheduled() throws IOException {
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GETALLSCHEDULED
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final RecordingsScheduled recordsScheduled = ResponseUtils.readFrom(RecordingsScheduled.class, resp);
		
		return recordsScheduled;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_23,fromFallback=PROTO_VERSION_00)
	public RecordingsExpiring queryExpiring() throws IOException {
		// fallback for PROTO_VERSION < 23
		if (this.protoVersion.compareTo(PROTO_VERSION_23)<0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_GETEXPIRING, this.protoVersion, IMythCommand.QUERY_RECORDINGS
			));
			
			// fetch all recordings
			final IProgramInfoList allRecordings = this.queryRecordings();
			
			// filter recordings by flag
			final IProgramInfoList filteredRecordingsList = allRecordings.filter(ProgramInfoFilters.flag(IProgramFlags.Flags.FL_AUTOEXP));
			
			// Create a new RecordingsExpiring object
			final RecordingsExpiring expiringRecords = RecordingsExpiring.valueOf(filteredRecordingsList);
			return expiringRecords;
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GETEXPIRING
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final RecordingsExpiring expiringRecords = ResponseUtils.readFrom(RecordingsExpiring.class, resp);
		
		return expiringRecords;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public RecordingsConflicting queryConflicting() throws IOException {
		// starting with PROTO_VERSION_57 it seens not to be possible anymore
		// to fetch a list of all conflicts.
		if(this.protoVersion.compareTo(PROTO_VERSION_57)>=0) {
			// getting all pending recordings
			final RecordingsPending pendingRecordings = this.queryAllPending();
			
			// filter out recordings with status conflict
			final IProgramInfoFilter conflictsFilter = ProgramInfoFilters.status(Status.CONFLICT);
			final IProgramInfoList conflictingRecordings = pendingRecordings.getProgramInfoList(conflictsFilter);
			
			// create a new conflicts list
			final RecordingsConflicting conflicts = RecordingsConflicting.valueOf(conflictingRecordings);
			return conflicts;
		}
		
		return this.queryConflicting(null);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public RecordingsConflicting queryConflicting(IProgramInfo programInfo) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_GETCONFLICTING
			),
			(programInfo==null)?null:programInfo.getPropertyValues()
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final RecordingsConflicting conflictingRecordings = ResponseUtils.readFrom(RecordingsConflicting.class, resp);		
		return conflictingRecordings;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean isActiveBackend() throws IOException {
		return this.isActiveBackend(null);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public boolean isActiveBackend(String hostname) throws IOException {
		// if the hostname is empty or null we need to determine the real hostname
		if(hostname == null || hostname.length() == 0) {
			hostname = this.queryHostname();			
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_IS_ACTIVE_BACKEND
			),
			hostname
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return Boolean.valueOf(resp.getPacketArg(0));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_72)
	public List<String> queryActiveBackends() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_ACTIVE_BACKENDS
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if(resp.getPacketArgsLength() == 0 || resp.getPacketArg(0).equals("0")) {
			return Collections.emptyList();
		} 
		
		return resp.getPacketArgs().subList(1, resp.getPacketArgsLength());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05)
	public boolean refreshBackend() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.REFRESH_BACKEND
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final String response = resp.getPacketArg(0);
		return response.equalsIgnoreCase(STATUS_OK);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public FileStatus queryCheckFile(
		IProgramInfo programInfo, 
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		Boolean checkSlaves
	) throws IOException {
		return this.queryCheckFile(programInfo.getPropertyValues(), checkSlaves);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	@SuppressWarnings("serial")
	private FileStatus queryCheckFile(
		final List<String> programInfoParams, 
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		final Boolean checkSlaves
	) throws IOException {		
		final boolean checkSlaveFlag = checkSlaves==null?true:checkSlaves.booleanValue();
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_CHECKFILE
			),
			new ArrayList<String>(){{
				if(protoVersion.compareTo(PROTO_VERSION_32)>=0) {
					// before PROTO_VERSION_32 this was always true
					this.add(checkSlaveFlag?"1":"0");
				}
				this.addAll(programInfoParams);
			}}
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(FileStatus.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49,fromFallback=PROTO_VERSION_00)
	public FileStatus queryFileExists(IProgramInfo programInfo) throws IOException {
		// 00 <= PROTO_VERSION < 49
		if(this.protoVersion.compareTo(PROTO_VERSION_49) < 0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_FILE_EXISTS, this.protoVersion, IMythCommand.QUERY_CHECKFILE
			));
			return this.queryCheckFile(programInfo,Boolean.TRUE);
		} 
		
		// 49 <= PROTO_VERSION
		else {		
			final String fileName = programInfo.getPathName();
			final String storageGroup = programInfo.getStorageGroup();
			return this.queryFileExists(fileName,storageGroup);
		}
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49,fromFallback=PROTO_VERSION_00)
	public FileStatus queryFileExists(URI fileUrl) throws IOException {
		final String storageGroup = fileUrl.getUserInfo();
		final String fileName = fileUrl.getPath();
		return this.queryFileExists(fileName, storageGroup);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49,fromFallback=PROTO_VERSION_00)
	public FileStatus queryFileExists(String fileName, String storageGroup) throws IOException {
		// fallback for protoVersion < PROTO_VERSION_49
		if(this.protoVersion.compareTo(PROTO_VERSION_49) < 0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s - %s instead ...",
				IMythCommand.QUERY_FILE_EXISTS, this.protoVersion, IMythCommand.QUERY_FILETRANSFER, IMythCommand.QUERY_FILETRANSFER_IS_OPEN
			));			
			
			// init a file transfer to check the file status
			final FileTransfer fileTransfer = this.annotateFileTransfer(fileName,storageGroup);
			final boolean fileExists = fileTransfer.isOpen();
			fileTransfer.close();
			
			return FileStatus.valueOf(this.protoVersion,fileExists,fileName);
		}
		
		// only use the file name here
		int idx = -1;
		if ((idx = fileName.lastIndexOf('/')) != -1) {
			fileName = fileName.substring(idx);
		}
		
		// using the default storage group if now group was specified
		if (storageGroup == null) {
			storageGroup = STORAGE_GROUP_DEFAULT;
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_FILE_EXISTS
			),
			fileName,
			storageGroup
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(FileStatus.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_51)
	public String queryFileHash(IProgramInfo programInfo) throws IOException {
		final String fileName = programInfo.getPathName();
		final String storageGroup = programInfo.getStorageGroup();
		final String hostName = programInfo.getHostName();
		return this.queryFileHash(fileName,storageGroup,hostName);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_51)
	public String queryFileHash(String fileName, String storageGroup) throws IOException {
		return this.queryFileHash(fileName,storageGroup,null);
	}
	
	@MythProtoVersionAnnotation(from = PROTO_VERSION_51)
	public String queryFileHash(URI fileUrl) throws IOException {
		final String storageGroup = fileUrl.getUserInfo();
		final String fileName = fileUrl.getPath();
		final String hostName = fileUrl.getHost();
		return this.queryFileHash(fileName, storageGroup, hostName);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_51)
	public String queryFileHash(
		String fileName, 
		String storageGroup, 
		@MythProtoVersionAnnotation(from=PROTO_VERSION_69) String hostName
	) throws IOException {	
		// only use the file name here (XXX: required for all protocol version?)
		int idx = -1;
		if ((idx = fileName.lastIndexOf('/')) != -1) {
			fileName = fileName.substring(idx);
		}
		
		// using the default storage group if now group was spezified
		if (storageGroup == null || storageGroup.length() == 0) {
			storageGroup = STORAGE_GROUP_DEFAULT;
		}
		
		// build the arguments list
		final List<String> args = new ArrayList<String>();
		args.add(fileName);
		args.add(storageGroup);
		if(hostName != null && hostName.length()>0 && this.protoVersion.compareTo(PROTO_VERSION_69)>=0) {
			args.add(hostName);
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_FILE_HASH
			),
			args
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public StorageGroupFile queryStorageGroupFile(String hostName, String storageGroup, String fileName) throws IOException {
		if(fileName == null) return null;
		
		// fallback for null hostname
		if(hostName == null) {
			// we can just us the current connection hostname
			if(this.protoVersion.compareTo(PROTO_VERSION_58)>= 0) {
				hostName = this.hostName;
			} else {
				// query the hostname of the current backend
				hostName = this.queryHostname();
			} 
		}
		
		// fallback for null storage-group
		if(storageGroup == null || storageGroup.length()==0) {
			storageGroup = STORAGE_GROUP_DEFAULT;
		}
		
		// for protoVersions < PROTO_VERSION_58 an absolute path is required
		if(this.protoVersion.compareTo(PROTO_VERSION_58)<0 && (fileName.startsWith("mythtv:") || !fileName.startsWith("/"))) {
			this.logger.warning(String.format(
				"In the current protocol version, the command %s required an absolut path to the file. " +
				"Using %s to determine the full file path ...",
				IMythCommand.QUERY_SG_FILEQUERY, IMythCommand.QUERY_FILE_EXISTS
			));			
			
			// determine the base name
			final int idx = fileName.lastIndexOf('/');
			if(idx != -1) fileName = fileName.substring(idx+1);
			
			// query the file status
			final IFileStatus status = this.queryFileExists(fileName, storageGroup);
			if(status != null && status.fileExists()) {			
				// now we have the full path
				fileName = status.getFilePath();
			}
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_SG_FILEQUERY
			),
			hostName,
			storageGroup,
			fileName
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(StorageGroupFile.class,resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public IStorageGroupFileList queryStorageGroupFileList(String hostName, String storageGroup, String path) throws IOException {
		return this.queryStorageGroupFileList(hostName, storageGroup, path,false);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public StorageGroupFileList queryStorageGroupFileList(
		String hostName, 
		String storageGroup, 
		String path,		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_49,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="22122"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="301a3816e687db3c76d6")
		})
		boolean fileNamesOnly
	) throws IOException {
		if(hostName == null) {
			// query the hostname of the current backend
			hostName = this.queryHostname();
		}
		if(storageGroup == null || storageGroup.length()==0) {
			storageGroup = STORAGE_GROUP_DEFAULT;
		}		
		
		// collect request params
		final ArrayList<String> requestParams = new ArrayList<String>();
		requestParams.add(hostName);
		requestParams.add(storageGroup);
		requestParams.add(path);
		if(protoVersion.compareTo(PROTO_VERSION_49)>=0) {
			requestParams.add(fileNamesOnly?"1":"0");
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_SG_GETFILELIST
			),
			requestParams
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if(resp.getPacketArg(0).startsWith("EMPTY LIST")) return null;
		else if(resp.getPacketArg(0).startsWith("SLAVE UNREACHABLE")) return null;
		return ResponseUtils.readFrom(StorageGroupFileList.class,resp);	
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_42)
	public ITimezone queryTimeZone() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_TIME_ZONE
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return ResponseUtils.readFrom(Timezone.class, resp);
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
	public boolean goToSleep() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.GO_TO_SLEEP
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_50,fromFallback=PROTO_VERSION_00)
	public String queryHostname() throws IOException {
		// 00 <= PROTO_VERSION < PROTO_VERSION_50 
		if(this.protoVersion.compareTo(PROTO_VERSION_50)<=0) {
			final List<IRecorderInfo> recorders = this.getRecorders();
			for(IRecorderInfo recorder : recorders) {
				final String recorderHost = recorder.getHostName();
				if(recorderHost.equals(this.getHostName())) {
					return recorderHost;
				}
				
				try {
					final InetAddress backendIP = InetAddress.getByName(this.getHostName());
					final InetAddress recorderID = InetAddress.getByName(recorderHost);
					if(backendIP.equals(recorderID)) {
						return recorderHost;
					}
				} catch (Throwable e) {
					// ignore this
				}
			}
			
			// fallback to the server hostname
			return this.getHostName();
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_HOSTNAME
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_19)
	public Boolean blockShutdown() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.BLOCK_SHUTDOWN
			)
		));
		
		// reading the response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_19)
	public Boolean allowShutdown() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.ALLOW_SHUTDOWN
			)
		));
		
		// reading the response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public void shutdownNow() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.SHUTDOWN_NOW
			)
		));
		
		// there seems to be no response
		// final IMythPacket resp = this.cmdConnection.readMessage();
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Setting querySetting(String hostName,String settingName) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_SETTING,
				hostName,
				settingName
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if(resp.getPacketArg(0).equals("-1")) return null;
		
		final Setting setting = ResponseUtils.readFrom(Setting.class,resp);
		setting.setHostname(hostName);
		setting.setName(settingName);
		return setting;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public boolean setSetting(String hostName,String settingName,String settingValue) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.SET_SETTING,
				hostName,
				settingName,
				settingValue
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final String response = resp.getPacketArg(0);
		return response.equalsIgnoreCase(STATUS_OK);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_23)
	public void clearSettingsCache() throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.BACKEND_MESSAGE
			),
			IMythCommand.BACKEND_MESSAGE_CLEAR_SETTINGS_CACHE,
			"empty"
		));
	}
	
	public ITunerInfo lockTuner() throws IOException {
		return this.lockTuner(null);
	}
	
	public ITunerInfo lockTuner(Integer recorderID) throws IOException {		
		// write request
		if (recorderID != null) {			
			this.cmdConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.LOCK_TUNER,
					Integer.toString(recorderID)
				)
			));
		} else {
			this.cmdConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.LOCK_TUNER
				)
			));
		}
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();		
		final String response = resp.getPacketArg(0);
		if (response.equals("-1")) return null;
		else if (response.equals("-2")) return null;
		return ResponseUtils.readFrom(TunerInfo.class, resp);
	}
	
	public boolean freeTuner(Integer recorderID) throws IOException {
		if (recorderID == null) throw new NullPointerException();
		
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.FREE_TUNER,
				Integer.toString(recorderID)
			)
		));
		
		final IMythPacket resp = this.cmdConnection.readPacket();
		final String response = resp.getPacketArg(0);
		return response.equalsIgnoreCase(STATUS_OK);
	}
	
	public IProgramInfo fillProgramInfo(IProgramInfo programInfo) throws IOException {
		return this.fillProgramInfo(programInfo,RequestUtils.getHostname());
	}
	
	public IProgramInfo fillProgramInfo(IProgramInfo programInfo, String playBackHostname) throws IOException {
		final List<String> args = this.fillProgramInfo(programInfo.getPropertyValues(), playBackHostname);
		return new ProgramInfo(this.protoVersion,args);
	}
	
	@SuppressWarnings("serial")
	private List<String> fillProgramInfo(final List<String> programInfoArgs, final String playBackHostname) throws IOException {
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.FILL_PROGRAM_INFO
			),
			new ArrayList<String>() {{
				this.add(playBackHostname);
				this.addAll(programInfoArgs);
			}}
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArgs();
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Integer checkRecording(IProgramInfo programInfo) throws IOException {
		if(programInfo == null) return null;
				
		final String hostName = programInfo.getHostName();
		final boolean emptyHostName = hostName == null || hostName.trim().length() == 0;
		if(emptyHostName) {
			logger.warning(String.format(
				"The hostname of the recording is null. Therefore the command %s does not work properly. " +
				"Using %s instead ...",
				IMythCommand.CHECK_RECORDING, IMythCommand.QUERY_REMOTEENCODER_MATCHES_RECORDING
			));
			
			// loop through all encoders to see if the given recording matches
			final List<IRecorderInfo> recorderInfos = this.getRecorders();
			for(IRecorderInfo recorderInfo : recorderInfos) {
				IRecorder recorder = null;
				try {
					// connect to recorder
					recorder = this.getRecorder(recorderInfo);
					
					// get encoder
					final IRemoteEncoder encoder = recorder.getRemoteEncoder();
					
					// check if encoder is recording the given program
					final boolean matches = encoder.matchesRecording(programInfo);
					if(matches) {
						return recorder.getRecorderID();
					}
				} finally {
					// disconnect from recorder
					if(recorder != null) recorder.close();
				}
			}
			
			// no matching recorder found
			return null;
		} 
		
		return this.checkRecording(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	private Integer checkRecording(List<String> programInfoArgs) throws IOException {
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.CHECK_RECORDING
			),
			programInfoArgs
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Integer recorderID = Integer.valueOf(resp.getPacketArg(0));
		if (recorderID.intValue() == 0) return null;
		return recorderID;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Integer stopRecording(IProgramInfo programInfo) throws IOException {
		return this.stopRecording(programInfo.getPropertyValues());
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	private Integer stopRecording(List<String> programInfoArgs) throws IOException {
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.STOP_RECORDING
			),
			programInfoArgs
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return Integer.valueOf(resp.getPacketArg(0));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRecorderInfo getRecorderForProgram(IProgramInfo programInfo) throws IOException {
		// determine the ID of the responsible recorder
		final Integer recorderId = this.checkRecording(programInfo);
		if(recorderId == null) return null;
		
		// get recorder-infos for the ID
		return this.getRecorderForNum(recorderId.intValue());
	}
	
	/**
	 * TODO: fallback using a program filter!!!!
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	public ProgramInfo queryRecording(String baseName) throws IOException {
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDING,
				IMythCommand.QUERY_RECORDING_BASENAME,
				baseName
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if (!resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) return null;
		
		// build program-info
		final List<String> programArgs = resp.getPacketArgs();
		programArgs.remove(0);
		return new ProgramInfo(this.protoVersion,programArgs);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32, fromFallback=PROTO_VERSION_00)
	public IProgramInfo queryRecording(IBasicChannelInfo channel, Date startTime) throws IOException {
		return this.queryRecording(channel.getChannelID(), startTime);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32, fromFallback=PROTO_VERSION_00)
	public IProgramInfo queryRecording(Integer channelID, Date recordingStartTime) throws IOException {
		// 00 <= PROTO_VERSION < PROTO_VERSION_32
		if(this.protoVersion.compareTo(PROTO_VERSION_32)<=0) {
			this.logger.warning(String.format(
				"The command %s %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_RECORDING, IMythCommand.QUERY_RECORDING_TIMESLOT, this.protoVersion, IMythCommand.QUERY_RECORDINGS
			));
			
			// query all recordings
			final IProgramInfoList allRecordings = this.queryRecordings();
			final IProgramInfoList foundRecordings = allRecordings.filter(ProgramInfoFilters.channelIdRecStartTime(channelID, recordingStartTime));
			if(foundRecordings.isEmpty()) return null;
			return foundRecordings.get(0);
		}
		
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDING,
				IMythCommand.QUERY_RECORDING_TIMESLOT,
				channelID.toString(),
				EncodingUtils.formatDateTime(recordingStartTime,false)
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if (!resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) return null;
		
		// build program-info
		final List<String> programArgs = resp.getPacketArgs();
		programArgs.remove(0);
		return new ProgramInfo(this.protoVersion,programArgs);
	}

	public <C extends IBasicChannelInfo> Map<Integer,C> getBasicChannelInfoMap() throws IOException {
		final HashMap<Integer,C> channelInfos = new HashMap<Integer, C>();
		
		// get a list of all available recorders
		final List<IRecorderInfo> recorderInfos = this.getRecorders();			
		if(recorderInfos != null) {
			
			// loop through all recorders to fetch the channel info
			for(IRecorderInfo recorderInfo : recorderInfos) {
				// connecting to the recorder
				final IRecorder recorder = this.getRecorder(recorderInfo);
				if(recorder == null) continue;
				
				// fetching the channel infos from the current recorder
				final List<C> recorderChannelInfos = recorder.getBasicChannelInfos();
				if(recorderChannelInfos != null) {
					// loop through all recorders to determine if we already know them
					for(C recorderChannelInfo : recorderChannelInfos) {
						final Integer channelID = recorderChannelInfo.getChannelID();
						if(channelInfos.containsKey(channelID)) continue;
						channelInfos.put(channelID,recorderChannelInfo);
					}
				}
				
				// disconnect from recorder
				recorder.close();
			}
		}
		
		return channelInfos;
	}	
	
	public <C extends IBasicChannelInfo> List<C> getBasicChannelInfos() throws IOException {
		final Map<Integer,C> channelMap = this.getBasicChannelInfoMap();
		return new ResultList<C>(channelMap.values());
	}
	
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public List<IRecorderChannelInfo> getChannelInfos() throws IOException {
		return this.getBasicChannelInfos();
	}
	
	@MythProtoVersionAnnotation(from = PROTO_VERSION_28)
	public boolean setChannelInfo(String oldChannelNumber, IRecorderChannelInfo channelInfo) throws IOException {		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.SET_CHANNEL_INFO
			),
			channelInfo.getPropertyValue(IRecorderChannelInfo.Props.CHANNEL_ID),
			channelInfo.getPropertyValue(IRecorderChannelInfo.Props.SOURCE_ID),
			oldChannelNumber, // old channel number
			channelInfo.getPropertyValue(IRecorderChannelInfo.Props.CHANNEL_SIGN),
			channelInfo.getPropertyValue(IRecorderChannelInfo.Props.CHANNEL_NUMBER),
			channelInfo.getPropertyValue(IRecorderChannelInfo.Props.CHANNEL_NAME),
			channelInfo.getPropertyValue(IRecorderChannelInfo.Props.XMLTV_ID)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return EncodingUtils.decodeBoolean(resp.getPacketArg(0));
	}	
	
	public List<IRecorderNextProgramInfo> getNextProgramInfos(Date date) throws IOException {
		if(date == null) date = new Date();
		
		final Map<Integer,IRecorderNextProgramInfo> allPrograms = new HashMap<Integer,IRecorderNextProgramInfo>();
		
		// get a list of all available recorders
		final List<IRecorderInfo> recorderInfos = this.getRecorders();			
		if(recorderInfos != null) {
			// loop through all recorders to fetch the channel info
			for(IRecorderInfo recorderInfo : recorderInfos) {
				// connecting to the recorder
				final IRecorder recorder = this.getRecorder(recorderInfo);
				if(recorder == null) continue;
				
				final List<IRecorderNextProgramInfo> pinfos = recorder.getNextProgramInfos(date);
				if(pinfos != null) {
					for(IRecorderNextProgramInfo pinfo : pinfos) {
						final Integer channelId = pinfo.getChannelID();
						if(allPrograms.containsKey(channelId)) continue;
						
						// check for already seen programs
						allPrograms.put(channelId,pinfo);
					}
				}
				
				// disconnect from recorder
				recorder.close();
			}
		}
		
		return new ResultList<IRecorderNextProgramInfo>(allPrograms.values());
	}

	public Map<String,String> getChannelLogoMap(IRecorderNextProgramInfo.Props keyProp) throws IOException {
		if(keyProp == null) keyProp = IRecorderNextProgramInfo.Props.CHANNEL_ID;
		final Map<String,String> logoMap = new HashMap<String,String>();
		
		final List<IRecorderNextProgramInfo> nextPrograms = this.getNextProgramInfos(null);
		for(IRecorderNextProgramInfo nextProgram : nextPrograms) {
			// getting the icon path
			final String channelIcon = nextProgram.getChannelIconPath();
			if(channelIcon == null || channelIcon.trim().length() == 0) continue;
			
			final String channelKey = nextProgram.getPropertyValue(keyProp);			
			logoMap.put(channelKey,channelIcon.trim());
		}
		
		return logoMap;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_64)
	public boolean scanVideos() throws IOException {
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.SCAN_VIDEOS
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final String response = resp.getPacketArg(0);
		return response.equalsIgnoreCase(STATUS_OK);
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})	
	public boolean queryTranscode(IProgramInfo programInfo) throws IOException {
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUEUE_TRANSCODE
			),
			programInfo.getPropertyValues()
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		// this seems to return 0 aleays
		return resp.getPacketArg(0).equals("0");
	}
	
	public URI downloadFile(URI url, String storageGroup, String fileName) throws IOException {
		return this.downloadFile(false, url, storageGroup, fileName);
	}
	
	public URI downloadFileNow(URI url, String storageGroup, String fileName) throws IOException {
		return this.downloadFile(true, url, storageGroup, fileName);
	}	
	
	private URI downloadFile(boolean synchronous, URI url, String storageGroup, String fileName) throws IOException {
		// fallback to the default storage group if required
		if(storageGroup == null || storageGroup.length() == 0) {
			storageGroup = STORAGE_GROUP_DEFAULT;
		}
		
		// write request	
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				synchronous ? IMythCommand.DOWNLOAD_FILE_NOW : IMythCommand.DOWNLOAD_FILE
			),
			url.toURL().toExternalForm(),
			storageGroup,
			fileName
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Boolean success = EncodingUtils.decodeBoolean(resp.getPacketArg(0));
		if(!success.booleanValue()) {
			logger.warning(String.format(
				"Unable to download remote file '%s' into storage-group '%s' with name '%s':\r\n%s",
				url, storageGroup, fileName, resp
			));			
			return null;
		}
				
		return URI.create(resp.getPacketArg(1));
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_46)	
	public boolean deleteFile(URI fileUrl) throws IOException {
		final String storageGroup = fileUrl.getUserInfo();
		final String fileName = fileUrl.getPath();
		return this.deleteFile(fileName, storageGroup);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_46)	
	public boolean deleteFile(String fileName, String storageGroup) throws IOException {
		if(storageGroup == null || storageGroup.length() == 0) {
			storageGroup = STORAGE_GROUP_DEFAULT;
		}
		
		// write request
		final ArrayList<String> args = new ArrayList<String>();
		args.add(fileName);
		if(this.protoVersion.compareTo(PROTO_VERSION_47)>=0) {
			args.add(storageGroup);
		}
		
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.DELETE_FILE
			),
			args
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Boolean success = EncodingUtils.decodeBoolean(resp.getPacketArg(0));
		if(!success.booleanValue()) {
			logger.warning(String.format(
				"Unable to delete file '%s' in storage-group '%s'.",
				fileName, storageGroup
			));					
		}
		return success.booleanValue();
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Long queryBookmark(IProgramInfo program) throws IOException {
		return this.queryBookmark(program.getChannelID(), program.getRecordingStartTime());
	}
	

	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public Long queryBookmark(Integer channelID, Date recordingStartTime) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_BOOKMARK,
				channelID.toString(),
				Long.toString(recordingStartTime.getTime() / 1000)
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			return EncodingUtils.decodeLong(resp.getPacketArg(0),resp.getPacketArg(1));
		} else {
			return Long.valueOf(resp.getPacketArg(0));
		}
	}
	
	public boolean setBookmark(IProgramInfo recording, Long bookmarkPosition) throws IOException {
		return this.setBookmark(recording.getChannelID(), recording.getRecordingStartTime(), bookmarkPosition);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public boolean setBookmark(Integer channelID, Date recordingStartTime, Long bookmarkPosition) throws IOException {
		if(bookmarkPosition == null) {
			bookmarkPosition = Long.valueOf(0);
		}
		
		// request args
		final ArrayList<String> args = new ArrayList<String>();
		args.add(channelID.toString());
		args.add(Long.toString(recordingStartTime.getTime() / 1000));
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			final String[] longlongStr = EncodingUtils.encodeLong(bookmarkPosition.longValue());
			args.add(longlongStr[0]);
			args.add(longlongStr[1]);
		} else {
			args.add(bookmarkPosition.toString());
		}
		
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.SET_BOOKMARK,
				args
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		final Boolean success = EncodingUtils.decodeBoolean(resp.getPacketArg(0));
		if(!success.booleanValue()) {
			logger.warning(String.format(
				"Unable to set bookmark to frame %d for channel %d and recording-start-time %s",
				bookmarkPosition, channelID, recordingStartTime
			));					
		}
		return success.booleanValue();
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})	
	public boolean queueTranscode(IProgramInfo recording) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUEUE_TRANSCODE,
				recording.getPropertyValues()
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArgsLength() > 0 && resp.getPacketArg(0).equals("0");
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})	
	public boolean queueTranscodeCutlist(IProgramInfo recording) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUEUE_TRANSCODE_CUTLIST,
				recording.getPropertyValues()
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArgsLength() > 0 && resp.getPacketArg(0).equals("0");		
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	})	
	public boolean queueTranscodeStop(IProgramInfo recording) throws IOException {
		// write request
		this.cmdConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUEUE_TRANSCODE_STOP,
				recording.getPropertyValues()
			)
		));
		
		// read response
		final IMythPacket resp = this.cmdConnection.readPacket();
		return resp.getPacketArgsLength() > 0 && resp.getPacketArg(0).equals("0");		
	}

	public IFreeInputInfoList getFreeInputInfo() throws IOException {
		return getFreeInputInfo(0);
	}

	public IFreeInputInfoList getFreeInputInfo(int excluded_input) throws IOException {
		this.cmdConnection.writeMessage(new AMythRequest(
				new AMythCommand(
						this.protoVersion,
						IMythCommand.GET_FREE_INPUT_INFO,
						Integer.toString(excluded_input)
				)
		));

		final IMythPacket resp = this.cmdConnection.readPacket();
		//final ProgramInfoList recordings = ResponseUtils.readFrom(ProgramInfoList.class, resp);
		//if(recordings.isEmpty()) return recordings;
		final FreeInputInfoList inputsList = ResponseUtils.readFrom(FreeInputInfoList.class,resp);
		return inputsList;
	}

	@Override
	public String toString() {
		final StringBuffer buff = new StringBuffer();
		buff.append(this.hostName).append(":").append(this.hostPort)
		    .append("@").append(this.protoVersion).append(" ");
		if(this.isConnected()) {
			buff.append(this.cmdConnection.toString());
		}
		
		return buff.toString();
	}
	
	public static void main(String[] args) {
		IBackend b = null;
		try {
//			EventListener el = new EventListener("mythtv");
//			el.start();
			
			// new backend
			b = new Backend("192.168.10.207");
			
			// connect to it
			b.connect();
			b.getCommandConnection().setMsgDebugOut(System.out);
			b.addEventListener(IMythEvent.class,new IMythEventListener() {
				public void fireEvent(IMythEvent event) {
					System.out.println(event);
				}
				
			});			
			
			
			b.annotatePlayback("mythtvClient",EPlaybackSockEventsMode.NORMAL);

			
			// check if there are any free recorders
			IRecorderInfo ri = b.getNextFreeRecorder();
			if (ri != null) {
				final IRecorder r = b.getRecorder(ri);
				b.getRecorderForNum(r.getRecorderID());				
				
				if (r.spawnLiveTV()) {
					IRingBuffer buf = null;
					
					// TODO: we should check the encoder flags????
					r.signalFrontendReady();
					
					b.recordingListChange();
					// b.liveTvChainUpdate(null);
					
					if (r.waitForIsRecording(5000l)) {
						System.out.println("Recorder is recording ...");
						@SuppressWarnings("unused")
						IProgramInfo recording = r.getRecording();							
						@SuppressWarnings("unused")
						IRecorderProgramInfo repProgInfo = r.getProgramInfo();				
						
						r.getFramesWritten();		
						r.getFrameRate();
						
						buf =  r.annotateRingBuffer();
						buf.seek(0,0,null);
						
						File tmp = File.createTempFile("ringbuf", ".nuv");
						FileOutputStream fout = new FileOutputStream(tmp);
						
						long count = 0;
						long size = 50*1024*1024;
						while (size > 0) {
							int blockLength = (int)Math.min(size, 65536l);
							byte[] data = new byte[blockLength];
							int len = buf.readBlock(data,blockLength);
							if (len == -1) break;
							
							fout.write(data,0, len);
							fout.flush();
							size -= len;
							System.out.println(len + " " + size);
							
							count++;
//							if (count % 100 == 0) r.changeChannel(EChannelChangeDirection.UP);
						}
						
						fout.close();
						tmp.delete();
					}
					r.stopLiveTv();
					buf.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (b != null) b.disconnect();
		}
	}
}
