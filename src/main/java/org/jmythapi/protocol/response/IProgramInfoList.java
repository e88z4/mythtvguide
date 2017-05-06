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

import java.util.List;
import java.util.Map;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.impl.ProgramInfoList;

/**
 * An interface to get a list of MythTV recordings.
 * <p>
 * This object represents a list of finished, pending or scheduled MythTV recording.
 * 
 * <h3>Request example:</h3>
 * {@mythCodeExample <pre>
 *    // query all available recordings
 *    IProgramInfoList allRecordings = backend.queryRecordings();
 *    
 *    // print the channel, length, file-size and title of all recordings
 *    System.out.println("Channel | Length  | Size     | Title");
 *    for(IProgramInfo program : allRecordings) &#123;
 *       // print out the found recodings
 *       System.out.println(String.format(
 *          "%-5s | %3d min | %8s | %s",
 *          program.getChannelSign(),
 *          program.getDuration(),
 *          EncodingUtils.getFormattedFileSize(Locale.ENGLISH,program.getFileSize()),
 *          program.getFullTitle()
 *       ));			
 *    &#125;
 * </pre>}
 * <p>
 * The above example will output, e.g. 
 * <pre>
 * Channel | Length  | Size     | Title
 * PULS 4  | 137 min |  4,06 GB | Das Schweigen der L&auml;mmer
 * PULS 4  | 137 min |  3,71 GB | Ein Vogel auf dem Drahtseil
 * </pre>
 * 
 * <h3>Response example:</h3>
 * <br>
 * {@mythResponseExample 
 * <pre>
 * 		<00>TITLE: Das Schweigen der L&auml;mmer | <01>SUBTITLE: Thriller / USA / 1991 | <02>DESCRIPTION: Ein Serienkiller zieht seine blutige Spur durch den mittleren Westen der USA.. | <03>CATEGORY:  | <04>CHANNEL_ID: 11123 | <05>CHANNEL_NUMBER: 10123 | <06>CHANNEL_SIGN: PULS 4 | <07>CHANNEL_NAME: PULS 4 | <08>PATH_NAME: myth://192.168.10.201:6543/11123_20110326225800.mpg | <09>FILESIZE_HIGH: 1 | <10>FILESIZE_LOW: 65078508 | <11>START_DATE_TIME: Sat Mar 26 23:03:10 CET 2011 | <12>END_DATE_TIME: Sun Mar 27 01:20:30 CET 2011 | <13>DUPLICATE: false | <14>SHAREABLE: false | <15>FIND_ID: 734589 | <16>HOSTNAME: brain | <17>SOURCE_ID: 0 | <18>CARD_ID: 0 | <19>INPUT_ID: 0 | <20>REC_PRIORITY: 0 | <21>REC_STATUS: -3=> &#123;RECORDED&#125; | <22>REC_ID: 323 | <23>REC_TYPE: 0=> &#123;NOT_RECORDING&#125; | <24>DUP_IN: 0=> &#123;&#125; | <25>DUP_METHOD: 0=> &#123;&#125; | <26>REC_START_TIME: Sat Mar 26 22:58:00 CET 2011 | <27>REC_END_TIME: Sun Mar 27 01:36:00 CET 2011 | <28>REPEAT: false | <29>PROGRAM_FLAGS: 1=> &#123;FL_COMMFLAG&#125; | <30>REC_GROUP: Default | <31>CHAN_COMM_FREE: false | <32>CHANNEL_OUTPUT_FILTERS:  | <33>SERIES_ID:  | <34>PROGRAM_ID:  | <35>LAST_MODIFIED: Sat Apr 02 00:21:57 CEST 2011 | <36>STARS: 0.0 | <37>ORIGINAL_AIRDATE:  | <38>HAS_AIRDATE: false | <39>PLAY_GROUP: Default | <40>REC_PRIORITY2: 0 | <41>PARENT_ID: 0 | <42>STORAGE_GROUP: Default | <43>AUDIO_PROPERTIES: 1=> &#123;AUD_STEREO&#125; | <44>VIDEO_PROPERTIES: 0=> &#123;&#125; | <45>SUBTITLE_TYPE: 0=> &#123;&#125; | <46>YEAR: 0
 * 		<00>TITLE: Ein Vogel auf dem Drahtseil | <01>SUBTITLE: Komödie / USA / 1989 | <02>DESCRIPTION: Rick Jarmin hat es weiß Gott nicht leicht... | <03>CATEGORY:  | <04>CHANNEL_ID: 11123 | <05>CHANNEL_NUMBER: 10123 | <06>CHANNEL_SIGN: PULS 4 | <07>CHANNEL_NAME: PULS 4 | <08>PATH_NAME: myth://192.168.10.201:6543/11123_20110328200900.mpg | <09>FILESIZE_HIGH: 0 | <10>FILESIZE_LOW: -313977244 | <11>START_DATE_TIME: Mon Mar 28 20:14:25 CEST 2011 | <12>END_DATE_TIME: Mon Mar 28 22:31:20 CEST 2011 | <13>DUPLICATE: false | <14>SHAREABLE: false | <15>FIND_ID: 734590 | <16>HOSTNAME: brain | <17>SOURCE_ID: 0 | <18>CARD_ID: 0 | <19>INPUT_ID: 0 | <20>REC_PRIORITY: 0 | <21>REC_STATUS: -3=> &#123;RECORDED&#125; | <22>REC_ID: 324 | <23>REC_TYPE: 0=> &#123;NOT_RECORDING&#125; | <24>DUP_IN: 0=> &#123;&#125; | <25>DUP_METHOD: 0=> &#123;&#125; | <26>REC_START_TIME: Mon Mar 28 20:09:00 CEST 2011 | <27>REC_END_TIME: Mon Mar 28 22:46:00 CEST 2011 | <28>REPEAT: false | <29>PROGRAM_FLAGS: 0=> &#123;&#125; | <30>REC_GROUP: Default | <31>CHAN_COMM_FREE: false | <32>CHANNEL_OUTPUT_FILTERS:  | <33>SERIES_ID:  | <34>PROGRAM_ID:  | <35>LAST_MODIFIED: Sat Apr 02 00:21:55 CEST 2011 | <36>STARS: 0.0 | <37>ORIGINAL_AIRDATE:  | <38>HAS_AIRDATE: false | <39>PLAY_GROUP: Default | <40>REC_PRIORITY2: 0 | <41>PARENT_ID: 0 | <42>STORAGE_GROUP: Default | <43>AUDIO_PROPERTIES: 1=> &#123;AUD_STEREO&#125; | <44>VIDEO_PROPERTIES: 0=> &#123;&#125; | <45>SUBTITLE_TYPE: 0=> &#123;&#125; | <46>YEAR: 0
 * </pre> }
 * 
 * <h3><a name="usage">Usage examples</a></h3>
 * 
 * Examples how to get a list of recordings from the backend:
 * 
 * <ul>
 * <li>Get all recorded programs ({@link IBackend#queryRecordings() link})</li>
 * <li>Get all pending recordings ({@link IBackend#queryAllPending() link})</li>
 * <li>Get all scheduled recordings ({@link IBackend#queryAllScheduled() link})</li>
 * <li>Get all conflicting recordings ({@link IBackend#queryConflicting() link})</li>
 * <li>Get all expiring recordings ({@link IBackend#queryExpiring() link})</li>
 * </ul>
 * 
 * @see IBackend#queryRecordings()
 * @see IMythCommand#QUERY_RECORDINGS QUERY_RECORDINGS
 */
public interface IProgramInfoList extends Iterable<IProgramInfo>, IVersionable, IPropertyAware<IProgramInfoList.Props> {
	/**
	 * The properties of an {@link IProgramInfoList} response.
	 */
	public static enum Props {
		/**
		 * List isze.
		 * 
		 * @see ProgramInfoList#size()
		 */
		SIZE,
	}

	/**
	 * Specifies which key should be used for {@link IProgramInfoList#asMap(MapKey)}
	 */
	public static enum MapKey {
		UNIQUE_PROGRAM_ID,
		UNIQUE_RECORDING_ID
	};	
	
	/**
	 * Returns the number of programs contained in this list
	 * 
	 * @return
	 * 		the list size
	 */
	public abstract int size();

	/**
	 * Checks if the program list is empty.
	 * 
	 * @return
	 * 		{@code true} if the list is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns a list of programs.
	 * <p>
	 * Use {@link #asList(IProgramInfoFilter)} if you want to filter out unwanted programs.
	 * 
	 * @return
	 * 		a list of program-info objects
	 */
	public abstract List<IProgramInfo> asList();
	
	/**
	 * Returns a list of programs. Only programs accepted by the given filter are returned.
	 * 
	 * @param filter
	 * 		the filter to apply to the list of programs
	 * @return
	 * 		a list of program-info objects
	 */
	public abstract List<IProgramInfo> asList(IProgramInfoFilter filter);		
	
	/**
	 * Returns a map containing the unique-id of each program as key and the program as value.
	 * <p>
	 * The input parameter specifies if the unique {@link IProgramInfo#getUniqueProgramId() program-id} or
	 * {@link IProgramInfo#getUniqueRecordingId() recording-id} should be used as key.
	 * <br>
	 * Please note that multiple different recordings may have the same program-id but different recording-ids.
	 * 
	 * 
	 * @return
	 * 		a mapping between the program-ids and programs
	 */
	public Map<String,IProgramInfo> asMap(MapKey mapKey);	
	
	/**
	 * Returns a map containing the unique-id of each program as key and the program-index as value.
	 * <p>
	 * The result of this function can be used to determine the index of a program within the program-list. 
	 * <p>
	 * The input parameter specifies if the unique {@link IProgramInfo#getUniqueProgramId() program-id} or
	 * {@link IProgramInfo#getUniqueRecordingId() recording-id} should be used as key.
	 * <br>
	 * Please note that multiple different recordings may have the same program-id but different recording-ids.
	 * 
	 * @return
	 * 		an index list
	 */
	public Map<String,Integer> getIndexMap(MapKey mapKey);
	
	/**
	 * Returns a new program list containing only programs that are accepted by the given filter.
	 * <p>
	 * <h4>Usage example:</h4>
	 * <br>
	 * The following example shows how to filter a list of programs by the programs recording-status.
	 * <br>
	 * {@mythCodeExample <pre>
	 *    // our list of programs
	 *    IProgramInfoList programs = ...; 
	 * 
	 *    // creating the program filter
	 *    IProgramInfoFilter filter = ProgramInfoFilters.recordingStatus(IProgramRecordingStatus.Status.WILL_RECORD);
	 * 
	 *    // filter programs
	 *    IProgramInfoList filteredPrograms = programs.filter(filter);
	 * 
	 *    // loop through the filtered list 
	 *    for (IProgramInfo program : filteredPrograms) &#123;
	 *       System.out.println(String.format(
	 *          "%1$tF %1$tT- %2$s (%3$s)",
	 *          program.getStartTime(),
	 *          program.getFullTitle(),
	 *          program.getChannelSign()
	 *       ));
	 *    &#125;
	 * </pre>}
	 * <p>
	 * See {@link ProgramInfoFilters} for a list of predefined filters.
	 * 
	 * @param filter
	 * 		the program filter to use or {@code null} if no filtering should be done
	 * @return
	 * 		a list of filtered program-info objects
	 */
	public abstract IProgramInfoList filter(IProgramInfoFilter filter);
	
	/**
	 * Allows to filter the program list, using multiple filters.
	 * <p>
	 * For each filter a list of matching program-info objects is returned.
	 * 
	 * @param filters
	 * 		a list of filters
	 * @return
	 * 		an array with the same length as the amount of filters.
	 */
	public abstract IProgramInfoList[] multiFilter(IProgramInfoFilter... filters);
	
	/**
	 * Gets the program-info object at the given index.
	 * 
	 * @param idx
	 * 		the index
	 * @return
	 * 		the found program
	 */
	public abstract IProgramInfo get(int idx);	
	
	/**
	 * Groups the list of program-infos by the specified program-info property.
	 * 
	 * <h4>Usage example:</h4>
	 * The following example groups all recordings by their storage group.
	 * 
	 * {@mythCodeExample <pre>
	 *    // get a list of all recordings
	 *    IProgramInfoList allRecordings = backend.queryRecordings();
	 *    
	 *    // group recordings by storage group
	 *    Map&lt;Object,IProgramInfoList&gt; recordingsByStorageGroup = allRecordings.groupBy(IProgramInfo.Props.STORAGE_GROUP);
	 *    for(Entry<Object,IProgramInfoList> entry : recordingsByStorageGroup.entrySet()) &#123;
	 *       System.out.println(String.format(
	 *          "\r\nSTORAGE-GROUP '%s': %02d recordings",
	 *          entry.getKey(),
	 *          entry.getValue().size()
	 *       ));
	 *       
	 *       for(IProgramInfo program : entry.getValue()) &#123;
	 *          System.out.println(String.format(
	 *             "- %s",
	 *             program.getFullTitle()
	 *          ));
	 *       &#125;
	 *    &#125;
	 * </pre>}
	 * <p>
	 * 
	 * @param prop
	 * 		the program-info property that is used for grouping
	 * @return
	 * 		a map containing the found property values as keys and the grouped lists of programs as values
	 */
	public Map<Object, IProgramInfoList> groupBy(IProgramInfo.Props prop); 
	
	/**
	 * Groups the list of program-infos by the specified program-info property.
	 * Only programs matching the given program-filter are returned.
	 * 
	 * <h4>Usage example:</h4>
	 * The following example groups all recordings by their categories. 
	 * 
	 * {@mythCodeExample <pre>
	 *    // get a list of all recordings
	 *    IProgramInfoList allRecordings = backend.queryRecordings();
	 * 
	 *    // optionally define a filter
	 *    IProgramInfoFilter filter = null;
	 *    
	 *    // group recordings by category
	 *    Map&#60;Object,IProgramInfoList&#62; recordingsByCategory = allRecordings.groupBy(IProgramInfo.Props.CATEGORY, filter);
	 *    for(Entry&#60;Object,IProgramInfoList&#62; entry : recordingsByCategory.entrySet()) &#123;
	 *       System.out.println(String.format(
	 *          "\r\nCATEGORY '%s': ",
	 *          entry.getKey()
	 *       ));
	 *       			
	 *       for(IProgramInfo program : entry.getValue()) &#123;
	 *          System.out.println(String.format(
	 *             "- %s",
	 *             program.getFullTitle()
	 *          ));
	 *       &#125;
	 *    &#125;
	 * </pre>}
	 * <p>
	 * 
	 * @param prop
	 * 		the program-info property that is used for grouping
	 * @param filter
	 * 		the program filter to use or {@code null} if no filtering should be done
	 * @return
	 * 		a map containing the found property values as keys and the grouped lists of programs as values
	 */
	public Map<Object, IProgramInfoList> groupBy(IProgramInfo.Props prop,IProgramInfoFilter filter); 
	
	/**
	 * Gets the sum of all file sizes.
	 * 
	 * @return
	 * 		the total size of all recording files
	 * 
	 * @see IProgramInfo#getFileSize()
	 */
	public long getTotalFilesSize();
	
	/**
	 * Gets the sum of all recording durations.
	 * 
	 * @return
	 * 		the total duration of all recordings
	 * 
	 * @see IProgramInfo#getDuration()
	 */
	public long getTotalDuration();
	
	/**
	 * Clones this program info list.
	 * @return
	 * 		a copy of this list
	 */
	public Object clone() throws CloneNotSupportedException;
}