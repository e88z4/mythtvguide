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
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_19;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_63;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;
import static org.jmythapi.protocol.utils.ResponseUtils.STATUS_OK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jmythapi.IVersionable;
import org.jmythapi.impl.ResultList;
import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.AMythRequest;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IFreeInputList;
import org.jmythapi.protocol.response.IInputInfo;
import org.jmythapi.protocol.response.IInputInfoTuned;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingStatus;
import org.jmythapi.protocol.response.IRemoteEncoderBusyStatus;
import org.jmythapi.protocol.response.IRemoteEncoderState;
import org.jmythapi.protocol.response.ISleepStatus;
import org.jmythapi.protocol.response.impl.FreeInputsList;
import org.jmythapi.protocol.response.impl.ProgramInfo;
import org.jmythapi.protocol.response.impl.ProgramRecordingStatus;
import org.jmythapi.protocol.response.impl.RemoteEncoderBusyStatus;
import org.jmythapi.protocol.response.impl.RemoteEncoderFlags;
import org.jmythapi.protocol.response.impl.RemoteEncoderState;
import org.jmythapi.protocol.response.impl.SleepStatus;
import org.jmythapi.protocol.utils.ResponseUtils;
import org.jmythapi.utils.EncodingUtils;

/**
 * TODO: what exactly is the difference between a {@link Recorder} and a {@link RemoteEncoder}?
 */
public class RemoteEncoder implements IVersionable, IRemoteEncoder {
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
	 * The ID of this remote-encoder
	 */
	private int encoderID = -1;
	
	public RemoteEncoder(IBackendConnection mythtvConnection, int encoderID) {		
		this.encoderID = encoderID;
		this.commandConnection = mythtvConnection;
		this.protoVersion = this.commandConnection.getVersionNr();
	}
	
	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}
	
	public int getRemoteEncoderID() {
		return this.encoderID;
	}
	
	@SuppressWarnings("deprecation")
	@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
	public long getMaxBitrate() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_GET_MAX_BITRATE
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
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
	public ISleepStatus getSleepStatus() throws IOException {
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_GET_SLEEPSTATUS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final Integer sleepStatusInt = Integer.valueOf(resp.getPacketArg(0));
		return new SleepStatus(this.protoVersion,sleepStatusInt);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRemoteEncoderState getState() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_GET_STATE
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return ResponseUtils.readFrom(RemoteEncoderState.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public RemoteEncoderFlags getFlags() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_GET_FLAGS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final int flagValue = Integer.valueOf(resp.getPacketArg(0)).intValue();
		return new RemoteEncoderFlags(this.protoVersion,flagValue);
	}
	
	public boolean isBusy() throws IOException {
		return this.isBusy(null);
	}
	
	public boolean isBusy(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
		Integer timeBuffer
	) throws IOException {
		final IRemoteEncoderBusyStatus busyStatus = this.getBusyStatus(timeBuffer);
		return busyStatus.isBusy();
	}
	
	public IRemoteEncoderBusyStatus getBusyStatus() throws IOException {
		return this.getBusyStatus(null);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public IRemoteEncoderBusyStatus getBusyStatus(
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
		Integer timeBuffer
	) throws IOException {
		// write request
		if (timeBuffer == null || this.protoVersion.compareTo(PROTO_VERSION_37)<0) {
			this.commandConnection.writeMessage(new AMythRequest(
					new AMythCommand(
						this.protoVersion,
						IMythCommand.QUERY_REMOTEENCODER,
						Integer.toString(this.encoderID)
					),
					IMythCommand.QUERY_REMOTEENCODER_IS_BUSY
			));
		} else {
			this.commandConnection.writeMessage(new AMythRequest(
					new AMythCommand(
						this.protoVersion,
						IMythCommand.QUERY_REMOTEENCODER,
						Integer.toString(this.encoderID)
					),
					IMythCommand.QUERY_REMOTEENCODER_IS_BUSY,
					timeBuffer.toString()
			));
		}
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return ResponseUtils.readFrom(RemoteEncoderBusyStatus.class, resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IInputInfoTuned getBusyInput() throws IOException {
		return this.getBusyInput(null);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IInputInfoTuned getBusyInput(
		Integer timeBuffer
	) throws IOException {
		final IRemoteEncoderBusyStatus busyStatus = this.getBusyStatus();
		if(busyStatus == null) return null;
		else if(!busyStatus.isBusy()) return null;
		return busyStatus.getInputInfo();
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public List<IInputInfo> getInputs() throws IOException {
		final ResultList<IInputInfo> inputs = new ResultList<IInputInfo>();

		// getting the busy input
		final IInputInfoTuned busyInput = this.getBusyInput();
		if(busyInput != null) {
			inputs.add(busyInput);
		}		
		
		// getting all free inputs
		final IFreeInputList freeInputs = this.getFreeInputs();
		if(!freeInputs.isEmpty()) {
			inputs.addAll(freeInputs.asList());
		}
		
		return inputs;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IFreeInputList getFreeInputs() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_GET_FREE_INPUTS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();	
		return ResponseUtils.readFrom(FreeInputsList.class, resp);
	}
	
	public boolean matchesRecording(IProgramInfo programInfo) throws IOException {
		return this.matchesRecording(programInfo.getPropertyValues());
	}
	
	@SuppressWarnings("serial")
	public boolean matchesRecording(final List<String> programInfoArgs) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				new ArrayList<String>() {{
					this.add(IMythCommand.QUERY_REMOTEENCODER_MATCHES_RECORDING);
					this.addAll(programInfoArgs);
				}}
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return !resp.getPacketArg(0).equals("0");
	}
	
	public boolean startRecording(IProgramInfo programInfo) throws IOException {
		return this.startRecording(programInfo.getPropertyValues());
	}
	
	@SuppressWarnings("serial")
	public boolean startRecording(final List<String> programInfoArgs) throws IOException {	
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				new ArrayList<String>() {{
					this.add(IMythCommand.QUERY_REMOTEENCODER_START_RECORDING);
					this.addAll(programInfoArgs);
				}}
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return !resp.getPacketArg(0).equals("0");		
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public boolean stopRecording() throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_REMOTEENCODER,
				Integer.toString(this.encoderID)
			),
			IMythCommand.QUERY_REMOTEENCODER_STOP_RECORDING
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return !resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);	
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_19)
	public IProgramInfo getCurrentRecording() throws IOException {
		/*
		 * Due to [1] GET_CURRENT_RECORDING should not be send to
		 * idle recorders.
		 * 
		 * [1] http://www.sudu.dk/mythtvplayer/index.php?page=mythtv-protocol
		 */
		if (!this.isBusy()) {
			logger.warning(String.format("The encoder %s is not recording.",this.encoderID));
			return null;
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.QUERY_REMOTEENCODER,
				Integer.toString(this.encoderID)
			),
			IMythCommand.QUERY_REMOTEENCODER_GET_CURRENT_RECORDING
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		final IProgramInfo programInfo = ResponseUtils.readFrom(ProgramInfo.class, resp);
		
		return programInfo;
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_63)
	public IProgramRecordingStatus getRecordingStatus() throws IOException {
		// fallback for old protocol versions
		if(this.protoVersion.compareTo(PROTO_VERSION_63)<0 && this.protoVersion.compareTo(PROTO_VERSION_19)>=0) {
			final IProgramInfo currentRecording = this.getCurrentRecording();
			if(currentRecording == null) {
				return null;
			} else {
				return currentRecording.getRecordingStatus();
			}
		}
		
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_GET_RECORDING_STATUS
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return ProgramRecordingStatus.valueOf(resp);
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public boolean cancelNextRecording(final Boolean cancel) throws IOException {
		// write request
		this.commandConnection.writeMessage(new AMythRequest(
				new AMythCommand(
					this.protoVersion,
					IMythCommand.QUERY_REMOTEENCODER,
					Integer.toString(this.encoderID)
				),
				IMythCommand.QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING,
				cancel==null||cancel.booleanValue()?"1":"0"
		));
		
		// read response
		final IMythPacket resp = this.commandConnection.readPacket();
		return resp.getPacketArg(0).equalsIgnoreCase(STATUS_OK);
	}	
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();		
		buf.append("ID=").append(this.encoderID).append(" ").append(this.commandConnection.toString());		
		return buf.toString();	
	}	
}
