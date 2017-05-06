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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to geht the busy status of a MythTV encoder.
 * <p>
 * This interface represents the response to a {@link IRemoteEncoder#getBusyStatus()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRemoteEncoderBusyStatus.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 *      An idle encoder (Protocol Version 56):
 * 		<pre><0>IS_BUSY: false | <0>INPUT_NAME: <EMPTY> | <1>SOURCE_ID: 0 | <2>INPUT_ID: 0 | <3>CARD_ID: 0 | <4>MULTIPLEX_ID: 0 | <5>CHANNEL_ID: 0</pre>
 *      A busy encoder  (Protocol Version 56):
 *      <pre><0>IS_BUSY: true | <0>INPUT_NAME: Tuner 1 | <1>SOURCE_ID: 1 | <2>INPUT_ID: 1 | <3>CARD_ID: 1 | <4>MULTIPLEX_ID: 0 | <5>CHANNEL_ID: 1063</pre>
 *      An idle encoder (Protocol Version 15):
 *      <0>IS_BUSY: false
 * }
 * 
 * @see IRemoteEncoder#getBusyStatus()
 * @see IMythCommand#QUERY_REMOTEENCODER_IS_BUSY QUERY_REMOTEENCODER_IS_BUSY
 */
public interface IRemoteEncoderBusyStatus extends IVersionable, IPropertyAware<IRemoteEncoderBusyStatus.Props> {

	/**
	 * The properties of an {@link IRemoteEncoderBusyStatus} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		@MythParameterType(Boolean.class)
		IS_BUSY
	}

	public abstract boolean isBusy();

	/**
	 * Gets the input that is currently used by the encoder.
	 * 
	 * @return
	 * 		the busy input or {@code null}
	 * 
	 * @since {@mythProtoVersion 37}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_37)
	public abstract IInputInfoTuned getInputInfo();

}