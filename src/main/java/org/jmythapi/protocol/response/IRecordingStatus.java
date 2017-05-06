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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_40;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the recording status of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryIsRecording()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecordingStatus.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		Protocol Version 15 example:
 * 		<pre><0>RECORDING_IN_PROGRESS: 0</pre>
 * 
 * 		Protocol Version 56 example:
 * 		<pre><0>RECORDING_IN_PROGRESS: 0 | <1>LIVETV_IN_PROGRESS: 0</pre>
 * }
 * 
 * @see IBackend#queryIsRecording()
 * @see IMythCommand#QUERY_ISRECORDING QUERY_ISRECORDING
 */
public interface IRecordingStatus extends IVersionable, IPropertyAware<IRecordingStatus.Props> {

	/**
	 * The properties of {@link IRecordingStatus}.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * Recordings in progress.
		 * <p>
		 * The total amount of active recordings
		 * 
		 * @see IRecordingStatus#getActiveRecordings()
		 */
		@MythParameterType(Integer.class)
		RECORDING_IN_PROGRESS,
		
		/**
		 * LiveTV in progress.
		 * <p>
		 * Active recordings that are LiveTV recordings.
		 * 
		 * @see IRecordingStatus#getLiveTVRecordings()
		 * @since {@mythProtoVersion 40}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_40,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="15794"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="e1dfb99db1e9a46d7699")
		})
		LIVETV_IN_PROGRESS
	}

	/**
	 * Get the total amount of active recordings.
	 * 
	 * @return
	 * 		the amount of active recordins
	 */
	public abstract int getActiveRecordings();

	/**
	 * Get the total amount of recordings due to live-tv sessions
	 * 
	 * @return
	 * 		the amount of live-tv recordings

	 * @since 
	 * 		{@mythProtoVersion 40}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_40)
	public abstract int getLiveTVRecordings();	
	
	/**
	 * Gets the current recording status.
	 * 
	 * @return
	 * 		{@code true} if there is at least one active recording.
	 */
	public boolean isRecording();
}