package org.jmythapi.protocol.events;

import java.util.Date;

import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * @see IMythCommand#BACKEND_MESSAGE_FILE_WRITTEN
 */
public interface IFileWritten extends IRecordingEvent<IFileWritten.Props> {
	public static enum Props {
		/**
		 * e.g. {@code /var/lib/mythtv/recordings/4008_20141021052500.mpg}
		 */
		@MythParameterType(String.class)
		FILE_PATH,
		@MythParameterType(Long.class)
		FILE_SIZE
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Integer getChannelID();
	
	/**
	 * {@inheritDoc}
	 */
	public Date getRecordingStartTime();
	
	/**
	 * {@inheritDoc}
	 */
	public String getUniqueRecordingID();
	
	/**
	 * The base name of the written file.
	 * 
	 * @return
	 * 		the base name, e.g. {@code 4008_20141021052500.mpg}
	 */
	public String getFileBaseName();
	
	/**
	 * The full path to the written file.
	 * 
	 * @return
	 * 		the full file-name, e.g. {@code /var/lib/mythtv/recordings/4008_20141021052500.mpg}
	 */
	public String getFilePath();
	
	/**
	 * The amount of data written to the file so far.
	 * 
	 * @return
	 * 		the amount of bytes written.
	 */
	public Long getFileSize();
}
