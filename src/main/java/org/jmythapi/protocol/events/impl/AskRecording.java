package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.events.IAskRecording.Props.CHANNEL_NUMBER;
import static org.jmythapi.protocol.events.IAskRecording.Props.CHANNEL_SIGN;
import static org.jmythapi.protocol.events.IAskRecording.Props.HAS_LATER_SHOWING;
import static org.jmythapi.protocol.events.IAskRecording.Props.HAS_RECORDING;
import static org.jmythapi.protocol.events.IAskRecording.Props.RECORDER_ID;
import static org.jmythapi.protocol.events.IAskRecording.Props.SECONDS_TILL_RECORDING;
import static org.jmythapi.protocol.events.IAskRecording.Props.TITLE;

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IAskRecording;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.impl.ProgramInfo;
import org.jmythapi.protocol.utils.EnumUtils;

public class AskRecording extends AMythEvent<IAskRecording.Props> implements IAskRecording {
	public AskRecording(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	@Override
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		final int eventPropsLength = this.getExpectedSize(responseArgs);
		if(responseArgs == null || responseArgs.size() < eventPropsLength) {
			throw new IllegalArgumentException(String.format(
				"At least %d args expected but %d args found.",
				eventPropsLength, responseArgs==null?0:responseArgs.size()
			));			
		}
		
		if(this.protoVersion.compareTo(PROTO_VERSION_37)>=0) {
			final int programInfoLength = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);
			if(responseArgs.size() != programInfoLength + eventPropsLength) {
				throw new IllegalArgumentException(String.format(
					"%d args expected but %d args found.",
					programInfoLength+eventPropsLength, responseArgs==null?0:responseArgs.size()
				));
			}
			
		}
	}

	public Integer getRecorderId() {
		return this.getPropertyValueObject(RECORDER_ID);
	}

	public Integer getSecondsTillRecording() {
		return this.getPropertyValueObject(SECONDS_TILL_RECORDING);
	}

	public Boolean hasLaterShowing() {
		return this.getPropertyValueObject(HAS_RECORDING);
	}

	public Boolean hasRecording() {
		return this.getPropertyValueObject(HAS_LATER_SHOWING);
	}

	public IProgramInfo getProgramInfo() {
		if(protoVersion.compareTo(PROTO_VERSION_37)<0) {
			return null;
		}
		
		final int programInfoLength = EnumUtils.getEnumLength(IProgramInfo.Props.class, this.protoVersion);
		final int programInfoPos = EnumUtils.getEnumPosition(HAS_LATER_SHOWING,this.protoVersion) + 1;
		final List<String> programInfoArgs = this.respArgs.subList(programInfoPos,programInfoPos+programInfoLength);
		return new ProgramInfo(this.protoVersion,programInfoArgs);
	}


	public String getChannelName() {
		if(protoVersion.compareTo(PROTO_VERSION_37)<0) {
			return this.getPropertyValueObject(Props.CHANNEL_NAME);
		} else {
			return this.getProgramInfo().getChannelName();
		}
	}

	public String getChannelNumber() {
		if(protoVersion.compareTo(PROTO_VERSION_37)<0) {
			return this.getPropertyValueObject(CHANNEL_NUMBER);
		} else {
			return this.getProgramInfo().getChannelNumber();
		}
	}

	public String getChannelSign() {
		if(protoVersion.compareTo(PROTO_VERSION_37)<0) {
			return this.getPropertyValueObject(CHANNEL_SIGN);
		} else {
			return this.getProgramInfo().getChannelSign();
		}
	}

	public String getRecordingTitle() {
		if(protoVersion.compareTo(PROTO_VERSION_37)<0) {
			return this.getPropertyValueObject(TITLE);
		} else {
			return this.getProgramInfo().getTitle();
		}
	}	
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		
		buff.append(super.toString());
		if(protoVersion.compareTo(PROTO_VERSION_37)>=0) {
			buff.append("\r\nPROGRAM_INFO: ");
			buff.append(this.getProgramInfo());
		}
		
		return buff.toString();
	}
}
