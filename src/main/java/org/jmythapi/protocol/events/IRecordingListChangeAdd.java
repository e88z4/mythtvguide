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
package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_53;

import java.util.Date;

import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Recording-List Event - Recording Added.
 * <p>
 * {@mythProtoExample
 * 81      BACKEND_MESSAGE[]:[]RECORDING_LIST_CHANGE ADD 1000 2013-09-18T05:35:00Z[]:[]empty
 * }
 * <p>
 * <h2>Protocol Version Hint:</h2>
 * Prior to protocol {@mythProtoVersion 53}, it was possible to receive an {@link IMythCommand#BACKEND_MESSAGE_UPDATE_PROG_INFO UPDATE_PROG_INFO} message for new recordings.
 * <p>
 * <i>UTC Support:</i>Starting with {@mythProtoVersion 75} dates are delivered with timezone UTC, but are automatically
 * converted into the local timezone by this class.
 * 
 * 
 * @see IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE BACKEND_MESSAGE_RECORDING_LIST_CHANGE
 * 
 * @since {@mythProtoVersion 53}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_53)
public interface IRecordingListChangeAdd extends IRecordingListChangeSingle<IRecordingListChangeAdd.Props>{
	public static enum Props {
		@MythParameterType(Integer.class)
		CHANNEL_ID,
		@MythParameterType(Date.class)
		RECORDING_START_TIME
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
}
