package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_58;

import java.net.URI;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Download File Update.
 * <p>
 * This event is sent by a backend to inform the client about a in-progress download.
 * <p>
 * 
 * @see IMythCommand#BACKEND_MESSAGE_DOWNLOAD_FILE BACKEND_MESSAGE_DOWNLOAD_FILE
 * @see IBackend#downloadFile(URI, String, String)
 * 
 * @since {@mythProtoVersion 58}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_58)
public interface IDownloadFileUpdate extends IDownloadFile<IDownloadFileUpdate.Props> {
	/**
	 * The properties of an {@link IDownloadFileUpdate} event.
	 * <p> 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * The URI to the remote file.
		 * 
		 * @see IDownloadFileUpdate#getRemoteURI()
		 */
		@MythParameterType(URI.class)
		REMOTE_URI,
		
		/**
		 * The URI to the local copy of the remote file.
		 * 
		 * @see IDownloadFileUpdate#getLocalURI()
		 */
		@MythParameterType(URI.class)
		LOCAL_URI,
		
		/**
		 * The currently downloaded amount of bytes.
		 * 
		 * @see IDownloadFileUpdate#getBytesReceived()
		 */
		@MythParameterType(Long.class)
		BYTES_RECEIVED,
		
		/**
		 * The total size of the remote file.
		 * 
		 * @see IDownloadFileUpdate#getBytesTotal()
		 */
		@MythParameterType(Long.class)
		BYTES_TOTAL
	}
	
	/**
	 * Gets the currently downloaded amount of bytes.
	 * @return
	 * 		the downloaded amount of bytes
	 * 
	 * @see Props#BYTES_RECEIVED
	 */
	public Long getBytesReceived();	
}
