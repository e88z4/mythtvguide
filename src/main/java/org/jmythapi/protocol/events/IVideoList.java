package org.jmythapi.protocol.events;

import org.jmythapi.protocol.IBackend;

/**
 * Backend Event - Video List.
 * <p>
 * This message informs the client about the result of a video-storagegroup rescan.
 * <p>
 * The following sub-events of this event are possible.
 * <ul>
 * 	<li>{@link IVideoListChange} if changes were detected.</li>
 *  <li>{@link IVideoListNoChange} if no changes were detected.</li>
 * </ul>
 * @param <E>
 * 		the properties of the video-list event
 * 
 * @see IVideoListChange
 * @see IVideoListNoChange
 * @see IBackend#scanVideos()
 */
public interface IVideoList <E extends Enum<E>> extends IMythEvent<E> {
	/**
	 * Gets the amount of detected changes.
	 * 
	 * @return
	 * 		the amount of changes.
	 */
	public int getChangeCount();
	
	/**
	 * Checks if changes were detected.
	 * @return
	 * 		{@code true} if changes were detected.
	 */
	public boolean hasChanges();
}
