package org.jmythapi.protocol.response.impl;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IFreeInputInfo;
import java.util.List;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_87;

@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
public class FreeInputInfo extends AMythResponse<IFreeInputInfo.Props> implements IFreeInputInfo {

    public FreeInputInfo(IMythPacket packet) {
        super(IFreeInputInfo.Props.class, packet);
    }

    public FreeInputInfo(ProtocolVersion protoVersion, List<String> responseArgs) {
        super(protoVersion, IFreeInputInfo.Props.class, responseArgs);
    }

    public String getInputName(){
        return this.getPropertyValueObject(Props.INPUTNAME);
    }

    public Integer getSourceId(){
        return this.getPropertyValueObject(Props.SOURCEID);
    }

    public Integer getInputId() {
        return this.getPropertyValueObject(Props.INPUTID);
    }

    public Integer getMultiplexId() {
        return this.getPropertyValueObject(Props.MULTIPLEXID);
    }

    public Integer getChannelId() {
        return this.getPropertyValueObject(Props.CHANID);
    }

    public Integer getLiveTvOrder(){
        return this.getPropertyValueObject(Props.LIVETVORDER);
    }

    public Integer getScheduleOrder(){
        return this.getPropertyValueObject(Props.SCHEDORDER);
    }

    public String getDisplayName(){
        return this.getPropertyValueObject(Props.DISPLAYNAME);
    }

    public Integer getRecordingPriority(){
        return this.getPropertyValueObject(Props.RECPRIORITY);
    }

    public boolean getQuickTune(){
        return this.getPropertyValueObject(Props.QUICKTUNE);
    }

    public Integer getRecordingCount(){
        return this.getPropertyValueObject(Props.RECCOUNT);
    }

    public Integer getRecordingLimit(){
        return this.getPropertyValueObject(Props.REC_LIMIT);
    }
}
