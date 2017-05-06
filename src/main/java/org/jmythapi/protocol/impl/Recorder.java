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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_16;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_19;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_20;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_21;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_26;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_27;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_30;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_34;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_43;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_77;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.utils.ResponseUtils.STATUS_OK;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.impl.ResultList;
import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ProtocolConstants;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.AMythRequest;
import org.jmythapi.protocol.request.EChannelBrowseDirection;
import org.jmythapi.protocol.request.EChannelChangeDirection;
import org.jmythapi.protocol.request.EPictureAdjustmentType;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IFreeInputList;
import org.jmythapi.protocol.response.IInputInfoFree;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingStatus;
import org.jmythapi.protocol.response.IRecorderChannelPrefixStatus;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;
import org.jmythapi.protocol.response.IRecorderProgramInfo;
import org.jmythapi.protocol.response.IRingBuffer;
import org.jmythapi.protocol.response.impl.FreeInputsList;
import org.jmythapi.protocol.response.impl.ProgramInfo;
import org.jmythapi.protocol.response.impl.ProgramRecordingStatus;
import org.jmythapi.protocol.response.impl.RecorderChannelInfo;
import org.jmythapi.protocol.response.impl.RecorderChannelPrefixStatus;
import org.jmythapi.protocol.response.impl.RecorderNextProgramInfo;
import org.jmythapi.protocol.response.impl.RecorderProgramInfo;
import org.jmythapi.protocol.response.impl.RingBuffer;
import org.jmythapi.protocol.utils.RequestUtils;
import org.jmythapi.protocol.utils.ResponseUtils;
import org.jmythapi.utils.EncodingUtils;
import org.jmythapi.utils.GenericEnumUtils;

/**
 * An implementation of {@link IRecorder}.
 */
@SuppressWarnings("deprecation")
public class Recorder implements IVersionable, IRecorder {
	/** 
	 * For logging.
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * A MythTV-connection used to send {@link IMythCommand commands} to the 
	 * MythTV backend.
	 */
	private IBackendConnection commandConnection = null;
	
	/**
	 * The MythTV-protocol version that is used for communication
	 * @see BackendConnection#getVersionNr()
	 */
	private ProtocolVersion protoVersion;
	
	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
	private RingBuffer ringBuffer = null;
	
	/**
	 * The ID of this recorder
	 */
	private int recorderId = -1;
	
	private boolean shouldClose = false;
	
	public Recorder(IBackendConnection mythtvConnection, int recorderId) {
		this.shouldClose = false;
		this.recorderId = recorderId;
		this.commandConnection = mythtvConnection;
		this.protoVersion = this.commandConnection.getVersionNr();
	}
	
	/**
	 * Creates a copy of this recorder.
	 * Currently the same connection is used as the old recorder.
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		final IRecorder copy = new Recorder(this.commandConnection,this.recorderId);
		return copy;
	}
	
	public void close() throws IOException {	
		if(!shouldClose) return;
		
		if (this.commandConnection != null) {
			this.commandConnection.close();
			this.commandConnection = null;
		}		
	}
	
	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}
	
	public int getRecorderID() {
		return this.recorderId;
	}
	
	public IRemoteEncoder getRemoteEncoder() {
		// TODO: should we clone the connection here?
		return new RemoteEncoder(this.commandConnection, this.recorderId);
	}
	
	public boolean signalFrontendReady() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_FRONTEND_READY
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();		
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}
	
	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20)
	public boolean stopPlaying() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_STOP_PLAYING
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();		
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}	
	
	public boolean spawnLiveTV() throws IOException {
		return this.spawnLiveTV(false, "");
	}
	
	public boolean spawnLiveTV(
			@MythProtoVersionAnnotation(from=PROTO_VERSION_20)
			final boolean pictureInPicture,
			@MythProtoVersionAnnotation(from=PROTO_VERSION_34)
			final IRecorderChannelInfo recorderChannelInfo
	) throws IOException {
		// getting the channel number to use
		final String channelNumber = recorderChannelInfo==null?"":recorderChannelInfo.getChannelNumber();
		
		// spawn live-tv
		return this.spawnLiveTV(pictureInPicture, channelNumber);
	}
	
	@SuppressWarnings("serial")
	public boolean spawnLiveTV(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_20)
		final boolean pictureInPicture,
		@MythProtoVersionAnnotation(from=PROTO_VERSION_34)
		final String channelNumber
	) throws IOException {
		// FALLBACK for protoVersion < PROTO_VERSION_20
		if (this.protoVersion.compareTo(PROTO_VERSION_20) < 0 && this.ringBuffer == null) {
			logger.warning(String.format(
				"No ringfuffer was set up. Using %s to init a new ringbuffer",
				IMythCommand.QUERY_RECORDER_SETUP_RING_BUFFER
			));
			
			// setup the ring buffer
			final boolean ringBufferCreated = this.setupRingBuffer(pictureInPicture);
			if(!ringBufferCreated) return false;
		}
		
		// FALLBACK for protoVersion < PROTO_VERSION_34
		if(this.protoVersion.compareTo(PROTO_VERSION_34)<0 && channelNumber != null && channelNumber.length() > 0) {
			// XXX: pause the recorder first?
			// this.pause();
			
			// we need to change the channel first
			this.setChannel(channelNumber);
		}

		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				new ArrayList<String>() {{
					// Sub-command
					add(IMythCommand.QUERY_RECORDER_SPAWN_LIVETV);
					
					if (protoVersion.compareTo(PROTO_VERSION_20)>=0) {
						// Chain-ID
						add(RequestUtils.getChainID());
						
						// Picture in picture
						add(pictureInPicture?"1":"0");
					}
					
					// starting channel number
					if (protoVersion.compareTo(PROTO_VERSION_34)>=0) {
						add(channelNumber);
					}
				}}
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}
	
	public boolean stopLiveTv() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_STOP_LIVETV
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();		
		this.ringBuffer = null;
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}
	
	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_20)
	public boolean setupRingBuffer(boolean pictureInPicture) throws IOException {		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_SETUP_RING_BUFFER,
			pictureInPicture?"1":"0"
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if(this.protoVersion.compareTo(PROTO_VERSION_16)>=0) {
			if(!resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) {
				logger.warning("Unable to setup ring-buffer");
				return false;
			} else {
				resp.getPacketArgs().remove(0);
			}
		}

		this.ringBuffer = new RingBuffer(this.commandConnection, this.recorderId, resp);
		return true;
	}
	
	
	/**
	 * @deprecated {@mythProtoVersion 20}
	 */
	@MythProtoVersionAnnotation(to=ProtocolVersion.PROTO_VERSION_20)
	public IRingBuffer annotateRingBuffer() throws IOException {
		if(this.ringBuffer == null) {
			logger.warning("No ringbuffer created. SpawnLiveTV must be called first");
			return null;
		}
		
		// open a new data connection
		final BackendConnection dataConnection = new BackendConnection(this.commandConnection.getHostname(),this.commandConnection.getPort());
		dataConnection.setInitialVersionNr(this.protoVersion);
		dataConnection.open();
		
		// write request
		dataConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.ANN,
				IMythCommand.ANN_RING_BUFFER,
				RequestUtils.getHostname(), // TODO: change this
				Integer.toString(this.recorderId)
			)));
		
		// read response
		final IMythPacket resp = dataConnection.readPacket();
		if (resp.getPacketArgs().size() != 1 || !resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK)) {
			dataConnection.close();
			logger.warning("Unable to annouce a ringbuffer connection");
			return null;
		}
		
		this.ringBuffer.setDataConnectin(dataConnection);
		return this.ringBuffer;
	}	
	
	public boolean waitForIsRecording(long timeoutMs) throws IOException, InterruptedException {		
		final long start = System.currentTimeMillis();
		
		boolean isRecording = false;
		while (!isRecording && (System.currentTimeMillis()-start)<timeoutMs) {
			isRecording = this.isRecording();
			Thread.sleep(50);
		}
		
		return isRecording;
	}
	
	public boolean isRecording() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_IS_RECORDING
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return (resp.getPacketArg(0).equalsIgnoreCase("1"));
	}
	
	public boolean finishRecording() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_FINISH_RECORDING
		));	
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_26)
	public boolean setLiveTvRecording() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_SET_LIVE_RECORDING,
			"-1" // currently only -1 is supported
		));	
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}
	
	public ProgramInfo getRecording() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_RECORDING
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final ProgramInfo programInfo = ResponseUtils.readFrom(ProgramInfo.class, resp);
		if(programInfo.getChannelID() == null) return null;
		
		if(this.protoVersion.compareTo(PROTO_VERSION_19)<0) {
			/*
			 * prior to protocol version 19 the recording status was not set properly in tv_rec.cpp:
			 * 
			 * ------------------------------------------------------------------------------------
			 * ProgramInfo *TVRec::GetRecording(void) {
			 * 	  ...
			 *    ProgramInfo *tmppginfo = NULL;
			 *    if (curRecording && !changeState)
			 *        tmppginfo = new ProgramInfo(*curRecording);
			 *    else
			 *        tmppginfo = new ProgramInfo();
			 *    return tmppginfo;
			 * ------------------------------------------------------------------------------------
			 * 
			 * We need to correct this here.
			 */			
			programInfo.setPropertyValueObject(
				IProgramInfo.Props.REC_STATUS, 
				ProgramRecordingStatus.valueOf(this.protoVersion,IProgramRecordingStatus.Status.RECORDING)
			);
		}		
		return programInfo;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_19, fromFallback=PROTO_VERSION_00)
	public IProgramInfo getCurrentRecording() throws IOException {
		// 00 <= PROTO_VERSION < 19
		if(this.protoVersion.compareTo(PROTO_VERSION_19)<0) {			
			this.logger.warning(String.format(
				"The command %s' is not supported in protocol version '%s'. Using %s instead ...",
				IMythCommand.QUERY_RECORDER_GET_CURRENT_RECORDING, this.protoVersion, IMythCommand.QUERY_RECORDER_GET_RECORDING
			));			
			return this.getRecording();
		}
		
		/*
		 * Due to [1] GET_CURRENT_RECORDING should not be send to
		 * idle recorders.
		 * 
		 * [1] http://www.sudu.dk/mythtvplayer/index.php?page=mythtv-protocol
		 */
		if (!this.isRecording()) {
			logger.warning(String.format("The recorder %s is not recording.",this.recorderId));
			return null;
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_CURRENT_RECORDING
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final IProgramInfo programInfo = ResponseUtils.readFrom(ProgramInfo.class, resp);
		
		return programInfo;
	}
	
	/**
	 * @deprecated {@mythProtoVersion 21}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_21,toFallback=PROTO_VERSION_LATEST)
	public IRecorderProgramInfo getProgramInfo() throws IOException {
		if(this.protoVersion.compareTo(PROTO_VERSION_21)>=0) {
			// getting the program info
			final RecorderNextProgramInfo npi = this.getNextProgramInfo(null,null,null,null);
			if(npi == null) return null;
			
			// creating an empty recorder-program-info object
			final RecorderProgramInfo rpi = new RecorderProgramInfo(this.protoVersion);
			
			//copy properties
			GenericEnumUtils.copyEnumValues(npi,rpi);
			return rpi;			
		}		

		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_PROGRAM_INFO
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final IRecorderProgramInfo programInfo = ResponseUtils.readFrom(RecorderProgramInfo.class, resp);
		return programInfo;
	}
	
	public IRecorderNextProgramInfo getNextProgramInfo(
			IBasicChannelInfo channelInfo,
			EChannelBrowseDirection direction, Date startTime
	) throws IOException {
		final String channelNumber = channelInfo==null?null:channelInfo.getChannelNumber();
		final Integer channelId  = channelInfo==null?null:channelInfo.getChannelID();
		return this.getNextProgramInfo(channelNumber, channelId, direction, startTime);
	}
	
	public RecorderNextProgramInfo getNextProgramInfo(String channelNumber, Integer channelID, EChannelBrowseDirection direction, Date startTime) throws IOException {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		final String dateStr = sdf.format(startTime==null?new Date():startTime);
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			// the command + recorder-id
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			// the sub-command
			IMythCommand.QUERY_RECORDER_GET_NEXT_PROGRAM_INFO,
			// the channel name
			channelNumber==null?"":channelNumber,
			// the channel ID
			channelID==null?"":channelID.toString(),
			// the direction to move
			Integer.toString(((direction==null)?EChannelBrowseDirection.SAME:direction).ordinal()),
			// the starting-date
			dateStr
		));

		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final RecorderNextProgramInfo nextProgramInfo = ResponseUtils.readFrom(RecorderNextProgramInfo.class, resp);
		
		return nextProgramInfo;
	}
	
	public List<IRecorderNextProgramInfo> getNextProgramInfos(Date startTime) throws IOException {
		final Map<Integer,IRecorderNextProgramInfo> channelProgramInfos = this.getChannelsNextProgramInfoMap(startTime);
		if(channelProgramInfos == null) return Collections.emptyList();
		return new ResultList<IRecorderNextProgramInfo>(channelProgramInfos.values());
	}
	
	public Map<Integer,IRecorderNextProgramInfo> getChannelsNextProgramInfoMap(Date startTime) throws IOException {
		final TreeMap<Integer, IRecorderNextProgramInfo> channelProgramInfos = new TreeMap<Integer, IRecorderNextProgramInfo>();
		
		String channelNumber = null;
		int channelId = 0;
		while (true) {
			// getting the next program info
			final IRecorderNextProgramInfo nextProgramInfo = this.getNextProgramInfo(channelNumber, channelId, EChannelBrowseDirection.UP, startTime);
			channelNumber = nextProgramInfo.getChannelNumber();
			channelId = nextProgramInfo.getChannelID();			
			if (channelProgramInfos.containsKey(channelId)) break;
			
			// store next program info
			channelProgramInfos.put(channelId, nextProgramInfo);
		}
		
		return channelProgramInfos;
	}	
	
	@SuppressWarnings("unchecked")
	public <C extends IBasicChannelInfo> List<C> getBasicChannelInfos() throws IOException {
		if(this.getVersionNr().compareTo(PROTO_VERSION_28)>=0) {
			return (List<C>) this.getChannelInfos();
		} else {		
			// get the next programs of all known channels
			final Map<Integer,? extends IBasicChannelInfo> channelProgramInfos = this.getChannelsNextProgramInfoMap(null);
			return (List<C>) new ArrayList<IBasicChannelInfo>(channelProgramInfos.values());
		}
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
	public List<IRecorderChannelInfo> getChannelInfos() throws IOException {
		final List<IRecorderChannelInfo> recorderChannelInfos = new ResultList<IRecorderChannelInfo>();
		
		// get the next programs of all known channels
		final Map<Integer,IRecorderNextProgramInfo> channelProgramInfos = this.getChannelsNextProgramInfoMap(null);
		
		// loop through all channel-IDs and ignore the rest
		for(Integer chanId : channelProgramInfos.keySet()) {
			// getting the info to the corresponding channel
			final IRecorderChannelInfo recorderChannelInfo = this.getChannelInfo(chanId);
			recorderChannelInfos.add(recorderChannelInfo);
		}
		
		return recorderChannelInfos;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
	public RecorderChannelInfo getChannelInfo(Integer chanID) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_CHANNEL_INFO,
			chanID==null?"":chanID.toString()
		));

		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final RecorderChannelInfo nextProgramInfo = ResponseUtils.readFrom(RecorderChannelInfo.class, resp);		
		return nextProgramInfo;		
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
	public RecorderChannelInfo getChannelInfo(IBasicChannelInfo channelInfo) throws IOException {
		if (channelInfo == null) return null;
		if (this.protoVersion.compareTo(PROTO_VERSION_28)<0) return null;
		
		// getting the channel id
		final Integer chanId = channelInfo.getChannelID();
		if (chanId == null) return null;
		
		// getting the channel Info
		return this.getChannelInfo(chanId);
	}
	
	@SuppressWarnings("unchecked")
	public <C extends IBasicChannelInfo> C getCurrentChannel() throws IOException {
		IBasicChannelInfo channelInfo = null;
		if(this.protoVersion.compareTo(PROTO_VERSION_19)<0) {
			channelInfo = this.getProgramInfo();
		} else {
			channelInfo = this.getCurrentRecording();
		}
		if(channelInfo == null) return null;
		
		if(this.getVersionNr().compareTo(PROTO_VERSION_28)<0) {
			return (C) channelInfo;
		} else {
			return (C) this.getChannelInfo(channelInfo.getChannelID());
		}
	}
	
	/**
	 * @deprecated {@mythProtoVersion 21} 
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_21)
	public String getInputName() throws IOException {
		if(this.protoVersion.compareTo(PROTO_VERSION_21)<0) {
			// write request
			this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_GET_INPUT_NAME
			));
			
			// read response
			final IMythPacket resp = this.commandConnection.readPacket();
			return resp.getPacketArg(0);
		} else if(this.protoVersion.compareTo(PROTO_VERSION_27)>=0) {
			// as of PROTO_VERSION_27 getInput should be used.
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_RECORDER_GET_INPUT_NAME, this.protoVersion, IMythCommand.QUERY_RECORDER_GET_INPUT
			));			
			return this.getInput();
		} else {
			// between version 21 and 27 we have nothing to query
			return null;
		}
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27)
	public String getInput() throws IOException {
		// 
		if(this.protoVersion.compareTo(PROTO_VERSION_27)<0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_RECORDER_GET_INPUT, this.protoVersion, IMythCommand.QUERY_RECORDER_GET_INPUT_NAME
			));
			return this.getInputName();
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_INPUT
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final String inputName = resp.getPacketArg(0);
		if(inputName == null || inputName.equals("UNKNOWN")) return null;
		return inputName;
	}	
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27)
	private String setNewInput(String inputName) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_SET_INPUT,
			inputName
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27)
	public boolean setInput(String inputName) throws IOException {
		final String newInput = this.setNewInput(inputName);
		return newInput != null && newInput.equalsIgnoreCase(inputName);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27,fromFallback=PROTO_VERSION_00)
	public String switchToNextInput() throws IOException {
		if(this.protoVersion.compareTo(PROTO_VERSION_27)<0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_RECORDER_SET_INPUT, this.protoVersion, IMythCommand.QUERY_RECORDER_TOGGLE_INPUTS
			));
			this.toggleInputs();
			return this.getInputName();
		}		
		
		return this.setNewInput("SwitchToNextInput");
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_27,toFallback=PROTO_VERSION_LATEST)
	public boolean toggleInputs() throws IOException {
		if(this.protoVersion.compareTo(PROTO_VERSION_27)>=0) {
			this.logger.warning(String.format(
				"The command %s is not supported in protocol version %s. Using %s instead ...",
				IMythCommand.QUERY_RECORDER_TOGGLE_INPUTS, this.protoVersion, IMythCommand.QUERY_RECORDER_SET_INPUT
			));						
			return this.setInput("SwitchToNextInput");
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_TOGGLE_INPUTS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return EncodingUtils.decodeBoolean(resp.getPacketArg(0));
	}
	
	public boolean checkChannel(String channelNumber) throws IOException {
		if (channelNumber == null || channelNumber.length() == 0) throw new IllegalArgumentException("The channel-number must not be null or empty.");
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_CHECK_CHANNEL,
			channelNumber
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return EncodingUtils.decodeBoolean(resp.getPacketArg(0));
	}
	
	public IRecorderChannelPrefixStatus checkChannelPrefix(String channelNumberPrefix) throws IOException {
		if (channelNumberPrefix == null || channelNumberPrefix.length() == 0) throw new IllegalArgumentException("No channel number prefix specified");
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_CHECK_CHANNEL_PREFIX,
			channelNumberPrefix
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return ResponseUtils.readFrom(RecorderChannelPrefixStatus.class,resp);
	}
	
	public boolean changeChannel(EChannelChangeDirection direction) throws IOException {
		if (direction == null) throw new IllegalArgumentException("The direction must not be null");
		return this.changeChannel(direction.ordinal());
	}
	
	public boolean changeChannel(int direction) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_CHANGE_CHANNEL,
			Integer.toString(direction)
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}

	public boolean setChannel(String channelNumber) throws IOException {
		if (channelNumber == null || channelNumber.length() == 0) 
			throw new IllegalArgumentException("The channel-number must not be null or empty.");
		
		/* 
		 * TODO: 
		 * 
		 * SET_CHANNEL seems to return "ok" even if the channel name is not known
		 * (this is true at least for protocol version 15), therefore we need to 
		 * check if the channel-name is valid first
		 */
		if (!this.checkChannel(channelNumber)) {
			System.err.println(String.format("Channel-number '%s' is unkown", channelNumber));
			return false;
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_SET_CHANNEL,
			channelNumber
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return (resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK));
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public long getFramesWritten() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_FRAMES_WRITTEN
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if (resp.getPacketArg(0).equalsIgnoreCase("-1")) return -1;
		
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			return EncodingUtils.decodeLong(
				resp.getPacketArg(0),
				resp.getPacketArg(1)
			);
		} else {
			return EncodingUtils.decodeString(
				Long.class,
				this.protoVersion,
				resp.getPacketArg(0)
			);
		}
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public long waitForFramesWritten(long desiredFrames) throws IOException, InterruptedException {
		long totalFramesWritten = 0;
		do {
			long nextFramesWritten = this.getFramesWritten();
			if(nextFramesWritten == -1) break;
			totalFramesWritten += nextFramesWritten;
			
			if(totalFramesWritten<desiredFrames) {
				Thread.sleep(500);
			}			
		} while (totalFramesWritten < desiredFrames);
		
		return totalFramesWritten;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public float getFrameRate() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_FRAMERATE
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return Float.valueOf(resp.getPacketArg(0));
	}
	
	public long getFilePosition() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_FILE_POSITION
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if (resp.getPacketArg(0).equals("-1")) return -1;
		
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			return EncodingUtils.decodeLong(
				resp.getPacketArg(0),
				resp.getPacketArg(1)
			);
		} else {
			return EncodingUtils.decodeString(
				Long.class,
				this.protoVersion, 
				resp.getPacketArg(0)
			);
		}
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IFreeInputList getFreeInputs() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_FREE_INPUTS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		return ResponseUtils.readFrom(FreeInputsList.class, resp);
	}
	
	/**
	 * @since {@mythProtoVersion 27}
	 * @deprecated {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_27,to=PROTO_VERSION_37,toFallback=PROTO_VERSION_LATEST)
	public List<String> getConnectedInputs() throws IOException {
		if(this.protoVersion.compareTo(PROTO_VERSION_37)>0) {
			final ArrayList<String> inputNames = new ArrayList<String>();
			
			// adding free inputs
			final IFreeInputList freeInputs = this.getFreeInputs();
			if(freeInputs != null) {
				for(IInputInfoFree freeInput : freeInputs) {
					inputNames.add(freeInput.getInputName());
				}
			}
			
			// adding current Input
			final String currentInput = this.getInput();
			if(currentInput != null && currentInput.length() > 0) {
				inputNames.add(currentInput);
			}
			
			return inputNames;
			
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_GET_CONNECTED_INPUTS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if(resp.getPacketArgsLength() == 0 | resp.getPacketArg(0).equals("EMPTY_LIST")) {
			return Collections.emptyList();
		}
		return resp.getPacketArgs();
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
	public Integer getColour() throws IOException {
		return this.getPictureAttribute(IMythCommand.QUERY_RECORDER_GET_COLOUR);
	}

	@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
	public Integer getContrast() throws IOException {
		return this.getPictureAttribute(IMythCommand.QUERY_RECORDER_GET_CONTRAST);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
	public Integer getBrightness() throws IOException {
		return this.getPictureAttribute(IMythCommand.QUERY_RECORDER_GET_BRIGHTNESS);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
	public Integer getHue() throws IOException {
		return this.getPictureAttribute(IMythCommand.QUERY_RECORDER_GET_HUE);
	}
	
	private Integer getPictureAttribute(String subCommand) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				subCommand
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		return Integer.valueOf(resp.getPacketArg(0));		
	}
	
	public Integer changeColour(boolean up) throws IOException {
		return this.changeColour(EPictureAdjustmentType.RECORDING, up);
	}
	
	public Integer changeColour(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException {
		return this.setPictureAttribute(IMythCommand.QUERY_RECORDER_CHANGE_COLOUR, adjustmentType, up);
	}
	
	public Integer changeContrast(boolean up) throws IOException {
		return this.changeContrast(EPictureAdjustmentType.RECORDING, up);
	}
	
	public Integer changeContrast(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException {
		return this.setPictureAttribute(IMythCommand.QUERY_RECORDER_CHANGE_CONTRAST, adjustmentType, up);
	}	
	
	public Integer changeBrightness(boolean up) throws IOException {
		return this.changeBrightness(EPictureAdjustmentType.RECORDING, up);
	}
	
	public Integer changeBrightness(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException {
		return this.setPictureAttribute(IMythCommand.QUERY_RECORDER_CHANGE_BRIGHTNESS, adjustmentType, up);
	}	
	
	public Integer changeHue(boolean up) throws IOException {
		return this.changeHue(EPictureAdjustmentType.RECORDING, up);
	}
	
	public Integer changeHue(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_30)
		EPictureAdjustmentType adjustmentType, 
		boolean up
	) throws IOException {
		return this.setPictureAttribute(IMythCommand.QUERY_RECORDER_CHANGE_HUE, adjustmentType, up);
	}
	
	@SuppressWarnings("serial")
	private Integer setPictureAttribute(final String subCommand, final EPictureAdjustmentType adjustmentType, final boolean up) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				new ArrayList<String>(){{
					add(subCommand);
					if(protoVersion.compareTo(PROTO_VERSION_30)>=0) {
						add(Integer.toString((adjustmentType==null?EPictureAdjustmentType.RECORDING:adjustmentType).ordinal()));
					}
					add(up?"1":"0");
				}}
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		return Integer.valueOf(resp.getPacketArg(0));		
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public long getMaxBitrate() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_GET_MAX_BITRATE
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		if (resp.getPacketArg(0).equals("-1")) return -1;
		
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			return EncodingUtils.decodeLong(
				resp.getPacketArg(0),
				resp.getPacketArg(1)
			);
		} else {
			return EncodingUtils.decodeString(
				Long.class,
				this.protoVersion,
				resp.getPacketArg(0)
			);
		}
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public boolean shouldSwitchCard(Integer channelID) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_SHOULD_SWITCH_CARD,
				channelID.toString()
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final String status = resp.getPacketArg(0);
		return EncodingUtils.decodeBoolean(status);
	}
	
	public boolean toggleChannelFavorite() throws IOException {	
		return this.toggleChannelFavorite(ProtocolConstants.CHANNEL_GROUP_DEFAULT);
	}
	
	@SuppressWarnings("serial")
	public boolean toggleChannelFavorite(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		final String channelGroup
	) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				new ArrayList<String>(){{
					add(IMythCommand.QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE);
					
					// as of version 45 a channelgroup parameter is required
					if(protoVersion.compareTo(PROTO_VERSION_45)>=0) {
						add(channelGroup==null?ProtocolConstants.CHANNEL_GROUP_DEFAULT:channelGroup);
					}
				}}
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final String status = resp.getPacketArg(0);
		return EncodingUtils.decodeBoolean(status);
	}
	
	public int setSignalMonitoringRate(int rate, boolean notifyFrontend) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_SET_SIGNAL_MONITORING_RATE,
				Integer.toString(rate),
				notifyFrontend?"1":"0"
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final String oldRate = resp.getPacketArg(0);
		return Integer.valueOf(oldRate).intValue();
	}
	
	public boolean pause() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				IMythCommand.QUERY_RECORDER_PAUSE
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}
	
	@SuppressWarnings("serial")
	public boolean cancelNextRecording(final Boolean cancel) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_RECORDER,
					Integer.toString(this.recorderId)
				),
				new ArrayList<String>(){{
					add(IMythCommand.QUERY_RECORDER_CANCEL_NEXT_RECORDING);
					if(protoVersion.compareTo(ProtocolVersion.PROTO_VERSION_23)>=0) {
						add(cancel==null||cancel.booleanValue()?"1":"0");
					};
				}}
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	public boolean setNextLiveTvDirectory(String path) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.SET_NEXT_LIVETV_DIR,
				Integer.toString(this.recorderId),
				path
			)				
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);		
	}
	
	public long getKeyframePosition(long frameNumber) throws IOException {
		// request arguments
		final ArrayList<String> args = new ArrayList<String>();
		args.add(IMythCommand.QUERY_RECORDER_GET_KEYFRAME_POS);
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {		
			final String[] frameNrParts = EncodingUtils.encodeLong(frameNumber);
			args.add(frameNrParts[0]);
			args.add(frameNrParts[1]);
		} else {
			args.add(Long.toString(frameNumber));
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			args
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		
		// read the byte position
		long bytePos = -1;
		if(this.protoVersion.compareTo(PROTO_VERSION_66)<0) {
			bytePos = EncodingUtils.decodeLong(resp.getPacketArg(0),resp.getPacketArg(1));
		} else {
			bytePos = EncodingUtils.decodeString(Long.class,this.protoVersion,resp.getPacketArg(0));
		}
		
		if(bytePos < 0) {
			logger.info(String.format(
				"Unable to determine the byte position for frame '%d'.",
				Long.valueOf(frameNumber)
			));
		}
		return bytePos;
	}
	
	public Map<Long,Long> fillPositionMap(long startFrameNr, long endFrameNr) throws IOException {
		// till PROTO_VERSION_43 only integer values are supported
		if(this.protoVersion.compareTo(PROTO_VERSION_43)<0 && (startFrameNr > 2147483647l || endFrameNr > 2147483647l)) {
			
			// too many values. aborting
			if(endFrameNr-startFrameNr > 1000) {
				logger.warning(String.format(
					"For protocol versions prior to %s, only 32 bit integer values are supported as frame numbers. " +
					"endFrameNr - startFrameNr > 1000. Function aborted.",
					PROTO_VERSION_43
				));
				return Collections.emptyMap();
			} 
			
			// fallback to GET_KEYFRAME_POS
			else {
				logger.warning(String.format(
					"For protocol versions prior to %s, only 32 bit integer values are supported as frame numbers. Using command %s instead ...",
					PROTO_VERSION_43,IMythCommand.QUERY_RECORDER_GET_KEYFRAME_POS
				));
			}
			
			// loop through all keyframes
			final Map<Long,Long> keyFrameMap = new LinkedHashMap<Long, Long>();
			for(long frameNumber=startFrameNr; frameNumber<=endFrameNr; frameNumber++) {
				final long frameBytePos = this.getKeyframePosition(frameNumber);
				if(frameBytePos == -1) continue;
				keyFrameMap.put(frameNumber,frameBytePos);
			}
			return keyFrameMap;
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_FILL_POSITION_MAP,
			Long.toString(startFrameNr),
			Long.toString(endFrameNr)
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if(resp.getPacketArgsLength() == 1) {
			if(resp.getPacketArg(0).equalsIgnoreCase("error")) {
				logger.warning("Unable to get the keyframe position map.");				
			} else {
				logger.warning("The key frame position map is empty.");
			}
			
			return Collections.emptyMap();
		}
		
		final Map<Long,Long> keyFrameMap = new LinkedHashMap<Long, Long>();
		if(this.protoVersion.compareTo(PROTO_VERSION_43)<0) {
			for(int i=0; i<resp.getPacketArgsLength(); i = i+4) {
				final long frameNr = EncodingUtils.decodeLong(resp.getPacketArg(i), resp.getPacketArg(i+1));
				final long frameBytePos = EncodingUtils.decodeLong(resp.getPacketArg(i+2), resp.getPacketArg(i+3));
				keyFrameMap.put(frameNr,frameBytePos);
			}
		} else {
			for(int i=0; i<resp.getPacketArgsLength(); i = i+2) {
				final long frameNr = Long.valueOf(resp.getPacketArg(i));
				final long frameBytePos = Long.valueOf(resp.getPacketArg(i+1));
				keyFrameMap.put(frameNr,frameBytePos);
			}
		}
		
		return keyFrameMap;
	}
	
	public Map<Long,Long> fillDurationMap(long start, long end) throws IOException {
		// TODO: is a fallback to the frame based function possible?
		if(this.protoVersion.compareTo(PROTO_VERSION_77)<0) {
			logger.warning(String.format(
				"For protocol versions prior to %s, use function fillPositionMap instead.",
				PROTO_VERSION_77
			));
			return Collections.emptyMap();
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_RECORDER,
				Integer.toString(this.recorderId)
			),
			IMythCommand.QUERY_RECORDER_FILL_DURATION_MAP,
			Long.toString(start),
			Long.toString(end)
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		if(resp.getPacketArgsLength() == 1) {
			if(resp.getPacketArg(0).equalsIgnoreCase("error")) {
				logger.warning("Unable to get the duration map.");				
			} else {
				logger.warning("The duration map is empty.");
			}
			
			return Collections.emptyMap();
		}
		
		final Map<Long,Long> durationMap = new LinkedHashMap<Long, Long>();
		for(int i=0; i<resp.getPacketArgsLength(); i = i+2) {
			final long frameNr = Long.valueOf(resp.getPacketArg(i));
			final long frameBytePos = Long.valueOf(resp.getPacketArg(i+1));
			durationMap.put(frameNr,frameBytePos);
		}
		
		return durationMap;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();		
		buf.append("ID=").append(this.recorderId).append("@" + this.protoVersion + " ").append(this.commandConnection.toString());		
		return buf.toString();	
	}
}
