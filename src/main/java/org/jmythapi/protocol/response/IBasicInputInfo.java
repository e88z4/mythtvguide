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

import org.jmythapi.IVersionable;

/**
 * This interface is used to access basic input informations.
 * <p>
 * The following interfaces extend from this interface. The table below shows which
 * input-related informations are provided by which interfaces.
 * 
 * <table class="infoTable">
 * 	<thead>
 * 	  <tr class="infoTitle">
 * 		<th colspan="9">Input Informations</th>
 *    </tr>
 *    <tr class="subHead">
 *    	<th class="head" rowspan="2">Interface</th>
 *      <th class="head" rowspan="2">Range</th>
 *      <th class="head" colspan="3" style="text-align: center;">{@link org.jmythapi.protocol.response.IBasicInputInfo}</th>
 *      <th class="head" colspan="2" style="text-align: center;">{@link org.jmythapi.protocol.response.IInputInfo}</th>
 *      <th class="head" colspan="1" style="text-align: center;">Additional</th>
 *    </tr>
 *    <tr class="subHead">
 *    	<th class="head">Card ID</th>
 *      <th class="head">Input ID</th>
 *      <th class="head">Source ID</th>
 *      <th class="head">Multiplex ID</th>
 *      <th class="head">Input Name</th>
 *      <th class="head">Channel ID</th>
 *    </tr>
 *  </thead>
 *  <tbody>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IInputInfoFree}</th>
 *      <td class="propRange">[37,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IInputInfoFree.Props#CARD_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IInputInfoFree.Props#INPUT_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IInputInfoFree.Props#SOURCE_ID X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IInputInfoFree.Props#MULTIPLEX_ID X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IInputInfoFree.Props#INPUT_NAME X}</td>
 *      <td class="propExists">-</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IInputInfoTuned}</th>
 *      <td class="propRange">[37,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#CARD_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#INPUT_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#SOURCE_ID X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#MULTIPLEX_ID X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#INPUT_NAME X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#CHANNEL_ID X}</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IProgramInfo}</th>
 *      <td class="propRange">[00,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CARD_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#INPUT_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#SOURCE_ID X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_ID X}</td>
 *    </tr>
 *  </tbody>
 * </table>
 */
public interface IBasicInputInfo extends IVersionable {
	/**
	 * Gets the source ID.
	 * 
	 * @return
	 * 		the source ID. This is usually the first digit of the channel ID.
	 */
	public Integer getSourceID();
	
	/**
	 * Gets the ID of a specific input.
	 * 
	 * @return
	 * 		the input ID
	 */
	public Integer getInputID();
	
	/**
	 * Gets the ID of the physical capture card.
	 * 
	 * @return
	 * 		the capture card ID
	 */
	public Integer getCardID();
}

