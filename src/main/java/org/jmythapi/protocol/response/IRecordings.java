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

import java.util.Iterator;

import org.jmythapi.IVersionable;

/**
 * A list of recordings.
 * <p>
 * This interface represents a list of recordings
 */
public interface IRecordings extends IVersionable, Iterable<IProgramInfo> {

	/**
	 * Gets a list of contained recordings.
	 * 
	 * @return 
	 * 		the list of contained recordings
	 * 		
	 */
	public abstract IProgramInfoList getProgramInfoList();

	/**
	 * Gets the list of contained recordings.
	 * Only recordings matching the given filter will be returned.
	 * <p>
	 * 
	 * <h4>Usage example:</h4>
	 * <br>
	 * In the example listed below we fetch all pending recordings, but only list recordings having the status <code>WILL_RECORD</code>.
	 * 
	 * {@mythCodeExample <pre>
	 * // query pending recordings
	 * IRecordingsPending pendingRecords = backend.queryAllPending();
	 * 
	 * // creating the program filter
	 * IProgramInfoFilter filter = ProgramInfoFilters.recordingStatus(IProgramRecordingStatus.Status.WILL_RECORD);
	 * 
	 * // loop through the filtered list
	 * for (IProgramInfo program : pendingRecords.getProgramInfoList(filter)) &#123;
	 *    System.out.println(String.format(
	 *       "%1$tF %1$tT- %2$s (%3$s)",
	 *       program.getStartTime(),
	 *       program.getTitle(),
	 *       program.getChannelSign()
	 *    ));
	 * &#125;
	 * </pre> }
	 * <p>
	 * This function internally uses {@link IProgramInfoList#filter(IProgramInfoFilter)} to filter the programs.
	 * 
	 * @param filter
	 * 		the filter that should be applied, or {@code null} if no filtering should be done.
	 * @return
	 * 		a new list containing the filtered programs
	 */
	public abstract IProgramInfoList getProgramInfoList(IProgramInfoFilter filter);	
	
	/**
	 * Gets an iterator to loop through all available recordings.
	 * 
	 * @return
	 * 		a recordings iterator
	 * 
	 * @see IProgramInfoList#iterator()		
	 */
	public Iterator<IProgramInfo> iterator();
	
	/**
	 * Gets an iterator to loop through all recordings matching
	 * the given filter.
	 * 
	 * @param filter
	 * 		the filter that should be applied or {@code null} if no filtering should be done.
	 * @return
	 * 		all recordings matching the given filter
	 */
	public Iterator<IProgramInfo> iterator(IProgramInfoFilter filter);
	
	/**
	 * Gets the amount of recordings contained in the list.
	 * @return 
	 * 		the recording-list size
	 */
	public abstract int size();

	/**
	 * Checks if the recordings-list contains a recording.
	 * @return 
	 * 		{@code false} if there is at least one recording
	 */
	public boolean isEmpty();	
}