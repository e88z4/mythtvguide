package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_58;

import java.net.URI;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Download File Finished.
 * <p>
 * This event is sent by a backend to inform the client about a finished download.
 * <p>
 * 
 * @see IMythCommand#BACKEND_MESSAGE_DOWNLOAD_FILE BACKEND_MESSAGE_DOWNLOAD_FILE
 * @see IBackend#downloadFile(URI, String, String)
 * 
 * @since {@mythProtoVersion 58}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_58)
public interface IDownloadFileFinished extends IDownloadFile<IDownloadFileFinished.Props> {
	/**
	 * The properties of an {@link IDownloadFileFinished} event.
	 * <p> 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * The URI to the remote file.
		 * 
		 * @see IDownloadFileFinished#getRemoteURI()
		 */
		@MythParameterType(URI.class)
		REMOTE_URI,
		
		/**
		 * The URI to the local copy of the remote file.
		 * 
		 * @see IDownloadFileFinished#getLocalURI()
		 */
		@MythParameterType(URI.class)
		LOCAL_URI,
		
		/**
		 * The total size of the remote file.
		 * 
		 * @see IDownloadFileFinished#getBytesTotal()
		 */
		@MythParameterType(Long.class)
		BYTES_TOTAL,
		
		/**
		 * The error text of a download error.
		 * 
		 * @see IDownloadFileFinished#getErrorText()
		 */
		@MythParameterType(String.class)
		ERROR_TEXT,
		
		/**
		 * The error code of a download error.
		 * @see <a href="http://www.riverbankcomputing.co.uk/static/Docs/PyQt4/html/qnetworkreply.html#NetworkError-enum">QNetworkReply.NetworkError</a>
		 * @see IDownloadFileFinished#getErrorCode()
		 */
		@MythParameterType(Integer.class)
		ERROR_CODE
	}
	
	/**
	 * Gets a text to describe a download error.
	 * @return
	 * 		an error text
	 */
	public String getErrorText();
	
	/**
	 * Gets a error code to describe a download error.
	 * @return
	 * 		an error code
	 */
	public Integer getErrorCode();
}
