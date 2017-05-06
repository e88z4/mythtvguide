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

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get all scheduled recordings of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryAllScheduled()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecordingsScheduled.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample
 * <pre>
 * 		<0>SIZE: 2 
 * 		<00>TITLE: James Bond (Title Suche) | <01>SUBTITLE:  | <02>DESCRIPTION: James Bond | <03>CATEGORY: Custom recording | <04>CHANNEL_ID: 11101 | <05>CHANNEL_NUMBER: 10101 | <06>CHANNEL_SIGN: ORF1 | <07>CHANNEL_NAME: ORF1 | <08>PATH_NAME:  | <09>FILESIZE_HIGH: 0 | <10>FILESIZE_LOW: 0 | <11>START_DATE_TIME: Sat Apr 02 00:00:00 CEST 2011 | <12>END_DATE_TIME: Sat Apr 02 00:00:00 CEST 2011 | <13>DUPLICATE: false | <14>SHAREABLE: false | <15>FIND_ID: 734501 | <16>HOSTNAME:  | <17>SOURCE_ID: 0 | <18>CARD_ID: 0 | <19>INPUT_ID: 0 | <20>REC_PRIORITY: 0 | <21>REC_STATUS: 0=> &#123;UNKNOWN&#125; | <22>REC_ID: 174 | <23>REC_TYPE: 4=> &#123;ALL_RECORD&#125; | <24>DUP_IN: 15=> &#123;DUPS_IN_RECORDED,DUPS_IN_OLD_RECORDED,DUPS_IN_ALL&#125; | <25>DUP_METHOD: 6=> &#123;DUP_CHECK_SUB,DUP_CHECK_DESC,DUP_CHECK_SUB_DESC&#125; | <26>REC_START_TIME: Sat Apr 02 00:00:00 CEST 2011 | <27>REC_END_TIME: Sat Apr 02 00:00:00 CEST 2011 | <28>REPEAT: false | <29>PROGRAM_FLAGS: 0=> &#123;&#125; | <30>REC_GROUP: Default | <31>CHAN_COMM_FREE: false | <32>CHANNEL_OUTPUT_FILTERS:  | <33>SERIES_ID:  | <34>PROGRAM_ID:  | <35>LAST_MODIFIED: Sat Apr 02 19:56:40 CEST 2011 | <36>STARS: 0.0 | <37>ORIGINAL_AIRDATE:  | <38>HAS_AIRDATE: false | <39>PLAY_GROUP: Default | <40>REC_PRIORITY2: 0 | <41>PARENT_ID: 0 | <42>STORAGE_GROUP: Default | <43>AUDIO_PROPERTIES: 0=> &#123;&#125; | <44>VIDEO_PROPERTIES: 0=> &#123;&#125; | <45>SUBTITLE_TYPE: 0=> &#123;&#125; | <46>YEAR: 
 * 		<00>TITLE: Peter Alexander (Keyword Suche) | <01>SUBTITLE:  | <02>DESCRIPTION: Peter Alexander | <03>CATEGORY: Custom recording | <04>CHANNEL_ID: 11101 | <05>CHANNEL_NUMBER: 10101 | <06>CHANNEL_SIGN: ORF1 | <07>CHANNEL_NAME: ORF1 | <08>PATH_NAME:  | <09>FILESIZE_HIGH: 0 | <10>FILESIZE_LOW: 0 | <11>START_DATE_TIME: Sat Apr 02 00:00:00 CEST 2011 | <12>END_DATE_TIME: Sat Apr 02 00:00:00 CEST 2011 | <13>DUPLICATE: false | <14>SHAREABLE: false | <15>FIND_ID: 734549 | <16>HOSTNAME:  | <17>SOURCE_ID: 0 | <18>CARD_ID: 0 | <19>INPUT_ID: 0 | <20>REC_PRIORITY: 0 | <21>REC_STATUS: 0=> &#123;UNKNOWN&#125; | <22>REC_ID: 280 | <23>REC_TYPE: 4=> &#123;ALL_RECORD&#125; | <24>DUP_IN: 15=> &#123;DUPS_IN_RECORDED,DUPS_IN_OLD_RECORDED,DUPS_IN_ALL&#125; | <25>DUP_METHOD: 6=> &#123;DUP_CHECK_SUB,DUP_CHECK_DESC,DUP_CHECK_SUB_DESC&#125; | <26>REC_START_TIME: Sat Apr 02 00:00:00 CEST 2011 | <27>REC_END_TIME: Sat Apr 02 00:00:00 CEST 2011 | <28>REPEAT: false | <29>PROGRAM_FLAGS: 0=> &#123;&#125; | <30>REC_GROUP: Default | <31>CHAN_COMM_FREE: false | <32>CHANNEL_OUTPUT_FILTERS:  | <33>SERIES_ID:  | <34>PROGRAM_ID:  | <35>LAST_MODIFIED: Sat Apr 02 19:56:40 CEST 2011 | <36>STARS: 0.0 | <37>ORIGINAL_AIRDATE:  | <38>HAS_AIRDATE: false | <39>PLAY_GROUP: Default | <40>REC_PRIORITY2: 0 | <41>PARENT_ID: 0 | <42>STORAGE_GROUP: Default | <43>AUDIO_PROPERTIES: 0=> &#123;&#125; | <44>VIDEO_PROPERTIES: 0=> &#123;&#125; | <45>SUBTITLE_TYPE: 0=> &#123;&#125; | <46>YEAR: 
 * </per> }
 * 
 * @see IBackend#queryAllScheduled()
 * @see IMythCommand#QUERY_GETALLSCHEDULED QUERY_GETALLSCHEDULED
 */
public interface IRecordingsScheduled extends IRecordings, IVersionable, IPropertyAware<IRecordingsScheduled.Props> {

	/**
	 * The properties of an {@link IRecordingsScheduled} response.
	 */
	public static enum Props {
		/**
		 * List size.
		 * @see IRecordingsScheduled#size()
		 */
		@MythParameterType(Integer.class)
		SIZE
	}

}