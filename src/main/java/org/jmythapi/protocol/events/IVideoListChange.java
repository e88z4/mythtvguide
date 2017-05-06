package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_63;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_68;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;

import java.util.List;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Video List - Change.
 * <p>
 * This message informs the client about the detected changes during a video-storagegroup rescan.
 * <p>
 * 
 * @see IBackend#scanVideos()
 * @see IMythCommand#BACKEND_MESSAGE_VIDEO_LIST_CHANGE BACKEND_MESSAGE_VIDEO_LIST_CHANGE
 * 
 * @since {@mythProtoVersion 63}.
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_63,fromInfo={
	@MythProtoVersionMetadata(key=GIT_COMMIT,value="20097268b71d947908636e09f38ead4f875fc12e")			
})
public interface IVideoListChange extends IVideoList<IVideoListChange.Props> {
	public static enum Props {
		// this event has no properties
	};
	
	/**
	 * Video List Change Type.
	 * <p>
	 * This enumeration defines all possible change types.
	 * 
	 * @since {@mythProtoVersion 68}
	 */
	public static enum ChangeType {
		added,
		moved,
		deleted
	};
	
	/**
	 * Video List Change.
	 * <p>
	 * This class represents a single change determined by the backend during video rescanning.<br>
	 * For each change the {@link ChangeType type} of the change and the id of the video are returned.
	 * 
	 * @since {@mythProtoVersion 68}
	 */
	public static interface IVideoChange {
		/**
		 * Gets the type of change.
		 * @return
		 * 		the type of change.
		 */
		public ChangeType getType();
		
		/**
		 * Gets the video id.
		 * @return
		 * 		the video id.
		 */
		public Integer getId();
	}
	
	/**
	 * Gets a list of all detected changes.
	 * 
	 * @return
	 * 		the list of detected changes.
	 * 
	 * @since {@mythProtoVersion 68}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_68)
	public List<IVideoChange> getChanges();
}
