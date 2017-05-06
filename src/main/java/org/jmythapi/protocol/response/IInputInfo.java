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

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.impl.InputInfoTuned;

/**
 * An interface to get informations about a recorder input.
 * <p>
 * This interface represents the response to a {@link IRemoteEncoder#getInputs()} request.<br>
 * This interface is the parent interface of {@link IInputInfoFree} and {@link InputInfoTuned}.
 * 
 * <h3>Usage Example:</h3>
 * 
 * {@mythCodeExample <pre>
 *    IRemoteEncoder encoder = ...; // an already connected encoder
 *  
 *    // get all encoder inputs (busy and free)
 *    List&lt;IInputInfo&gt; allInputs = encoder.getInputs();
 *    for(IInputInfo input : allInputs) &#123;
 *       System.out.println(String.format(
 *          "Input %d (name: %s, source: %d, card: %d, multiplex: %d) is %s.",
 *          input.getInputID(),
 *          input.getInputName(),
 *          input.getSourceID(),
 *          input.getCardID(),
 *          input.getMultiplexID(),
 *          input.isBusy()?"busy":"idle"
 *       ));
 *     &#125;
 *  
 *  <pre>}
 *  
 *  @since {@mythProtoVersion 37}
 *  
 *  @see IRemoteEncoder#getInputs() get all inputs
 *  @see IRemoteEncoder#getFreeInputs() get free inputs
 *  @see IRemoteEncoder#getBusyInput() bet gusy inputs
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_37)
public interface IInputInfo extends IBasicInputInfo, IVersionable {
	/**
	 * Constant used as input name if the encoder is idle.
	 */
	public static final String EMPTY_INPUT = "<EMPTY>";
	
	/* ====================================================================
	 * IBasicInputInfo methods
	 * ==================================================================== */
	/**
	 * {@inheritDoc}
	 */
	public Integer getSourceID();
	
	/**
	 * {@inheritDoc}
	 */
	public Integer getInputID();
	
	/**
	 * {@inheritDoc}
	 */
	public Integer getCardID();
	
	/* ====================================================================
	 * Additional methods
	 * ==================================================================== */
	
	/**
	 * Gets the name of the input.
	 * @return
	 * 		the input name
	 */
	public String getInputName();
	
	/**
	 * Gets the multiplex ID of the input.
	 * 
	 * @return
	 * 		the multiplex id.
	 */
	public Integer getMultiplexID();
	
	/**
	 * Gets the live-TV order of the input.
	 * 
	 * @return
	 * 		the order for live-TV
	 */
	public Integer getLiveTvOrder();
	
	/**
	 * Gets the busy status of the encoder input.
	 * 
	 * @return
	 * 		{@code true} if this input is busy.
	 */
	public boolean isBusy();

	public Integer getChannelId();

	public String getDisplayName();

	public Integer getRecordingPriority();

	public Integer getScheduleOrder();

	public boolean getQuickTune();
}
