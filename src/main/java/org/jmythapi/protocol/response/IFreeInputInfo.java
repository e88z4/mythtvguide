package org.jmythapi.protocol.response;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_87;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_89;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_90;

@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
public interface IFreeInputInfo extends IVersionable, IPropertyAware<IFreeInputInfo.Props> {

    @MythProtoVersionAnnotation(from=PROTO_VERSION_87)
    public static enum Props {
        INPUTNAME,
        @MythParameterType(Integer.class) SOURCEID,
        @MythParameterType(Integer.class) INPUTID,
        @MythParameterType(Integer.class) MULTIPLEXID,
        @MythParameterType(Integer.class) CHANID,
        DISPLAYNAME,
        @MythParameterType(Integer.class) RECPRIORITY,
        @MythParameterType(Integer.class) SCHEDORDER,
        @MythParameterType(Integer.class) LIVETVORDER,
        @MythParameterType(Boolean.class) QUICKTUNE,
        @MythParameterType(Integer.class)
        @MythProtoVersionAnnotation(from = PROTO_VERSION_90, to=PROTO_VERSION_90)
        RECCOUNT,
        @MythParameterType(Integer.class)
        @MythProtoVersionAnnotation(from = PROTO_VERSION_89, to = PROTO_VERSION_90)
        REC_LIMIT

    }

    public String getInputName();
    public Integer getSourceId();
    public Integer getInputId();
    public Integer getMultiplexId();
    public Integer getChannelId();
    public String getDisplayName();
    public Integer getRecordingPriority();
    public Integer getLiveTvOrder();
    public Integer getScheduleOrder();
    public boolean getQuickTune();
    public Integer getRecordingCount();
    public Integer getRecordingLimit();
}

