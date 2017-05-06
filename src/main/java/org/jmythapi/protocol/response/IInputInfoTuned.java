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
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

import static org.jmythapi.protocol.ProtocolVersion.*;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_87;

/**
 * An interface to get a currently in use recorder input.
 * <p>
 * This interface represents the response to a {@link IRemoteEncoder#getBusyStatus() IRemoteEncoder.getBusyStatus}.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IInputInfoTuned.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample 
 * 		<pre><0>INPUT_NAME: DVBInput | <1>SOURCE_ID: 1 | <2>INPUT_ID: 1 | <3>CARD_ID: 1 | <4>MULTIPLEX_ID: 0 | <5>CHANNEL_ID: 1002</pre>
 * }
 * 
 * @since {@mythProtoVersion 37}
 * 
 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
 * @see IRemoteEncoder#getBusyInput
 * @see IRemoteEncoder#getBusyStatus
 * @see <a href="https://github.com/MythTV/mythtv/blob/master/mythtv/libs/libmythtv/inputinfo.cpp">inputinfo.cpp</a>
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
public interface IInputInfoTuned extends IInputInfo, IVersionable, IPropertyAware<IInputInfoTuned.Props> {
	/**
	 * The properties of an {@link IInputInfoTuned} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
	public static enum Props {
		/**
		 * Input name.
		 * <p>
		 * The name of the input.
		 *
		 * @see IInputInfo#getInputName()
		 */
		INPUT_NAME,

		/**
		 * Source ID.
		 * <p>
		 * The ID of the program source.
		 * This is usually the first digit of the channel ID.
		 *
		 * @see IInputInfo#getSourceID()
		 */
		@MythParameterType(Integer.class)
		SOURCE_ID,

		/**
		 * Input ID.
		 * <p>
		 * The ID of a specific input.
		 *
		 * @see IInputInfo#getInputID()
		 */
		@MythParameterType(Integer.class)
		INPUT_ID,

		/**
		 * Card ID.
		 * <p>
		 * The ID of a physical capture card.
		 *
		 * @see IInputInfo#getCardID()
		 */
		@MythParameterType(Integer.class)
		CARD_ID,

		/**
		 * Multiplex ID.
		 *
		 * @see IInputInfo#getMultiplexID()
		 */
		@MythParameterType(Integer.class)
		MULTIPLEX_ID,

		/**
		 * Channel ID
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_81)
		CHANNEL_ID,

		@MythParameterType(String.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
		DISPLAY_NAME,

		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
		REC_PRIORITY,

		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
		SCHEDULE_ORDER,
		/**
		 * Live TV order.
		 * <p>
		 * When entering Live TV, the available, local input with the lowest, non-zero value will
		 * be used.  If no local inputs are available, the available, remote input with the lowest,
		 * non-zero value will be used.
		 * Setting this value to zero will make the input unavailable to live TV.
		 *
		 * @since {@mythProtoVersion 71}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_71)
		LIVETV_ORDER,

		@MythParameterType(Boolean.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
		QUICK_TUNE
	}
}
