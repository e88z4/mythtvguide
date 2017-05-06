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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_55;

import java.util.Date;
import java.util.EnumMap;

import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IProgramInfo;

/**
 * Backend Event - Recording - Properties Updated.
 * <p>
 * This event replaces {@link IUpdateProgInfo}.
 * <p>
 * {@mythProtoExample
 * 447     BACKEND_MESSAGE[]:[]RECORDING_LIST_CHANGE UPDATE[]:[]TEST[]:[]2013-09-18 07:35:06[]:[][]:[]0[]:[]0[]:[][]:[][]:[]1000[]:[]1[]:[]ORF1[]:[]ORF1[]:[]1000_20130918053500.mpg[]:[]0[]:[]1379482506[]:[]1379482806[]:[]0[]:[]myth77[]:[]0[]:[]0[]:[]0[]:[]-99[]:[]-3[]:[]36[]:[]0[]:[]15[]:[]6[]:[]1379482500[]:[]1379483406[]:[]0[]:[]Default[]:[][]:[][]:[][]:[][]:[]1379482507[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0
 * }
 * <p>
 * <h2>Protocol Version Hint:</h2>
 * <i>UTC Support:</i>Starting with {@mythProtoVersion 75} dates are delivered with timezone UTC, but are automatically
 * converted into the local timezone by this class.
 * 
 * @see IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE BACKEND_MESSAGE_RECORDING_LIST_CHANGE
 * 
 * @since {@mythProtoVersion 55}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_55)
public interface IRecordingListChangeUpdate extends IRecordingUpdateEvent<IRecordingListChangeUpdate.Props>, IRecordingListChangeSingle<IRecordingListChangeUpdate.Props>{
	public static enum Props {
		// no properties available
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
	 * Gets the info object of the changed recording.
	 * 
	 * @return
	 * 		the changed recording
	 */
	public IProgramInfo getProgramInfo();
	
	/**
	 * {@inheritDoc}
	 */
	public EnumMap<IProgramInfo.Props,Object[]> getUpdatedProperties(IProgramInfo oldProgramInfo);
}
