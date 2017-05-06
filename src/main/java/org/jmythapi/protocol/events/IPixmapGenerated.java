package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_61;

import java.util.Date;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IPixmap;

/**
 * Backend Event - Pixmap Generated.
 * <p>
 * 
 * @see IMythCommand#BACKEND_MESSAGE_GENERATED_PIXMAP BACKEND_MESSAGE_GENERATED_PIXMAP
 * @see IBackend#queryGenPixmap2(String, org.jmythapi.protocol.response.IProgramInfo)
 * 
 * @since {@mythProtoVersion 61}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_61)
public interface IPixmapGenerated extends IRecordingEvent<IPixmapGenerated.Props> {
	public static enum Props {
		/**
		 * The status code.
		 * 
		 * @see IPixmapGenerated#getStatusCode()
		 */
		STATUS_CODE,
		
		/**
		 * The unique recording id.
		 * 
		 * @see IPixmapGenerated#getUniqueRecordingID()
		 * @see IPixmapGenerated#getChannelID()
		 * @see IPixmapGenerated#getRecordingStartTime()
		 */
		UNIQUE_RECORDING_ID,
		
		/**
		 * A message text.
		 * 
		 * @see IPixmapGenerated#getStatusMessage()
		 */
		STATUS_MESSAGE,
		
		/**
		 * Pixmap last modified timestamp.
		 * <p>
		 * This is null on {@code ERROR}.
		 * 
		 * @see IPixmapGenerated#getPixmap()
		 * @see IPixmap.Props#LAST_MODIFIED
		 */
		@MythParameterType(Date.class)
		PIXMAP_LAST_MODIFIED,
		
		/**
		 * Pixmap data size.
		 * <p>
		 * This is null on {@code ERROR}.
		 * 
		 * @see IPixmapGenerated#getPixmap()
		 * @see IPixmap.Props#DATA_SIZE
		 */
		@MythParameterType(Long.class)
		PIXMAP_DATA_SIZE,
		
		/**
		 * Pixmap checksum.
		 * <p>
		 * This is null on {@code ERROR}.
		 * 
		 * @see IPixmapGenerated#getPixmap()
		 * @see IPixmap.Props#DATA_CHECKSUM
		 */
		PIXMAP_CHECKSUM,
		
		/**
		 * Pixmap base64 encoded data.
		 * <p>
		 * This is null on {@code ERROR}.
		 * 
		 * @see IPixmapGenerated#getPixmap()
		 * @see IPixmap.Props#DATA_BASE64
		 */
		PIXMAP_DATA_BASE64,
		
		/**
		 * The token that was specified during pixmap generation. 
		 * 
		 * @see IPixmapGenerated#getToken()
		 */
		TOKEN
	};

	public Integer getChannelID();

	public Date getRecordingStartTime();
	
	public String getUniqueRecordingID();	
	
	public String getStatusCode();
	
	public String getStatusMessage();
	
	public IPixmap getPixmap();
	
	public String getToken();
	
	public String getPreviewImageName();
}
