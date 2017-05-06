package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_69;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Video List - No Change.
 * <p>
 * This message informs the client that no changes were detected during a video-storagegroup rescan.
 * <p>
 * 
 * @see IBackend#scanVideos()
 * @see IMythCommand#BACKEND_MESSAGE_VIDEO_LIST_NO_CHANGE BACKEND_MESSAGE_VIDEO_LIST_NO_CHANGE
 * @since {@mythProtoVersion 69}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_69,fromInfo={
	@MythProtoVersionMetadata(key=GIT_COMMIT,value="82c763ac10994866c74ffb0f0ba7b924bac0df93")			
})
public interface IVideoListNoChange extends IVideoList<IVideoListNoChange.Props> {
	public static enum Props {
		// this event has no properties
	}
}
