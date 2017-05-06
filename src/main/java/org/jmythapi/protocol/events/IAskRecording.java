package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_05;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_23;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;

import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IProgramInfo;

/**
 * Backend Event - Ask Recording.
 * <p>
 * This event is sent by the backend to ask for the permission to record a scheduled program.<br>
 * The client can cancel the recording unsing {@link IRemoteEncoder#cancelNextRecording(Boolean)}.
 * <p>
 * {@mythProtoExample
 * 440     BACKEND_MESSAGE[]:[]ASK_RECORDING 1 0 0 0[]:[]TEST[]:[]2013-09-18 07:35:06[]:[][]:[]0[]:[]0[]:[][]:[][]:[]1000[]:[]1[]:[]ORF1[]:[]ORF1[]:[]/home/mythtv/recordings[]:[]0[]:[]1379482506[]:[]1379482806[]:[]0[]:[]myth77[]:[]1[]:[]1[]:[]1[]:[]-99[]:[]-1[]:[]36[]:[]1[]:[]15[]:[]6[]:[]1379482506[]:[]1379483406[]:[]0[]:[]Default[]:[][]:[][]:[][]:[][]:[]1379482507[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0
 * }
 * <p>
 * <h2>Protocol Version Hint:</h2>
 * <i>UTC Support:</i>Starting with {@mythProtoVersion 75} dates are delivered with timezone UTC, but are automatically
 * converted into the local timezone by this class.
 * 
 * @see IMythCommand#BACKEND_MESSAGE_ASK_RECORDING BACKEND_MESSAGE_ASK_RECORDING 
 */
public interface IAskRecording extends IMythEvent<IAskRecording.Props> {
	public static enum Props {
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		@MythParameterType(Integer.class)
		RECORDER_ID,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		@MythParameterType(Integer.class)
		SECONDS_TILL_RECORDING,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_23)
		@MythParameterType(Boolean.class)
		HAS_RECORDING,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
		@MythParameterType(Boolean.class)
		HAS_LATER_SHOWING,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
		TITLE,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
		CHANNEL_NUMBER,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
		CHANNEL_SIGN,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
		CHANNEL_NAME
	}
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Integer getRecorderId();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
	public Integer getSecondsTillRecording();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_23)
	public Boolean hasRecording();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public Boolean hasLaterShowing();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
	public String getRecordingTitle();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
	public String getChannelNumber();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
	public String getChannelSign();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_37)
	public String getChannelName();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public IProgramInfo getProgramInfo();
	
}
