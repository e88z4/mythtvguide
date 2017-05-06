/*
 * Copyright (C) ${year} Martin Thelian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, please email thelian@users.sourceforge.net
 */
package org.jmythapi.protocol.response;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get all pending recordings of a MythTV backend.
 * <p>
 * This object represents the response to a {@link IBackend#queryAllPending()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecordingsPending.Props properties-list} for all properties of this interface.
 * <p>
 * Use {@link #getConflictsCount()} to get the amount of recordings with the status {@link IProgramRecordingStatus.Status#CONFLICT CONFLICT}.
 * 
 * @see IBackend#queryAllPending()
 * @see IMythCommand#QUERY_GETALLPENDING QUERY_GETALLPENDING
 */
public interface IRecordingsPending extends IRecordings, IVersionable, IPropertyAware<IRecordingsPending.Props> {

	/**
	 * The properties of an {@link IRecordingsPending} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * Amount of conflicts.
		 * <p>
		 * All recordings with the status {@link IProgramRecordingStatus.Status#CONFLICT CONFLICT}.
		 * 
		 * @see IRecordingsPending#getConflictsCount()
		 */
		@MythParameterType(Integer.class)
		CONFLICTS,
		
		/**
		 * List size.
		 * @see IRecordingsPending#size()
		 */
		@MythParameterType(Integer.class)
		SIZE
	}

	/**
	 * Gets the amount of conflicts between all pending recordings.
	 * 
	 * @return how many conflicts were detected between the pending recordings
	 */
	public abstract int getConflictsCount();
	
	/**
	 * Checks if there are any conflicts between all pending recordings.
	 * @return
	 * 		{@code true} if there are any conflicts
	 */
	public boolean hasConflicts();
	
	/**
	 * This function returns the earliest pending recording for each recorder.
	 * 
	 * @return 
	 * 		a map containing the recorder-id as key and 
	 * 		the earliest next recording as value.
	 */
	public Map<Integer, IProgramInfo> getNextPendingRecordings() throws IOException;
	
	/**
	 * This function returns the earliest recording-start time for each recorder.
	 * <p>
	 * Usage example:
	 * <br>
	 * {@mythCodeExample <pre>
	 * 
	 *       IBackend backend = ....;     // an already connected backend
	 *       
	 *       // get all pending recordings
	 *       IRecordingsPending pendingRecords = backend.queryAllPending();
	 *       
	 *       // get next recording start times
	 *       Map&lt;Integer,Date&gt; nextRecordingDates = pendingRecords.getNextPendingRecordingDates();
	 *       for(Entry&lt;Integer,Date&gt; nextRecordingDate : nextRecordingDates.entrySet()) &#123;
	 *          System.out.println(String.format(
	 *             "Recorder %02d will start a recording at %2$tF %2$tT.",
	 *             nextRecordingDate.getKey(),
	 *             nextRecordingDate.getValue()
	 *          ));
	 *       &#125;
	 * </pre> }
	 * <p>
	 *  
	 * @return 
	 * 		a map containing the recorder-id as key and 
	 * 		the start-time of the earliest next recording as value.
	 * 		If there is no pending recording an empty map is returned.
	 * 
	 * @see #getNextPendingRecordings()
	 */
	public Map<Integer,Date> getNextPendingRecordingsDates() throws IOException;
	
	/**
	 * Gets the date of the next pending recording.
	 * @return
	 * 		the date of the next pending recording 
	 * 		or {@code null} if there is no pending recording.
	 */
	public Date getNextPendingRecordingDate() throws IOException;
	
	/**
	 * Gets the minutes to the next pending recording.
	 *  
	 * @return
	 * 		the minutes to the next pending recording 
	 * 		or {@code -1} if there is no pending recording.
	 */
	public int getMinutesToNextRecording() throws IOException;
}