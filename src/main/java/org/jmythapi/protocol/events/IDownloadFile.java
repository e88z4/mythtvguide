package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_58;

import java.net.URI;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Download File.
 * <p>
 * This message informs the client about a currently running backend download.
 * <p>
 * The following sub-events of this event are possible.
 * <ul>
 * 	<li>{@link IDownloadFileUpdate} for a in-progress download.</li>
 *  <li>{@link IDownloadFileFinished} for a finished download.</li>
 * </ul>
 * 
 * @see IMythCommand#BACKEND_MESSAGE_DOWNLOAD_FILE BACKEND_MESSAGE_DOWNLOAD_FILE
 * @see IBackend#downloadFile(URI, String, String)
 * 
 * @since {@mythProtoVersion 58}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_58)
public interface IDownloadFile <E extends Enum<E>> extends IMythEvent<E> {
	/**
	 * Gets the URI to the remote file, which is downloaded by the backend.
	 * @return
	 * 		the URI of a remote file
	 */
	public URI getRemoteURI();
	
	/**
	 * Gets the URI to the local copy of the remote file, stored into a backend storage-group
	 * @return
	 * 		the URI of the local file stored in a storage group
	 */
	public URI getLocalURI();
	
	/**
	 * Gets the total size of the file in bytes.
	 * @return
	 * 		the total size of the file in bytes
	 */
	public Long getBytesTotal();
	
	/**
	 * Returns how many percent of the remote file were downloaded so far.
	 * @return
	 * 		how many percent of the file were downloaded so far.
	 */
	public Float getReceivedPercent();
}
