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
package org.jmythapi;

import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;
import org.jmythapi.protocol.response.IRecorderProgramInfo;

/**
 * This interface is used to access some basic channels informations.
 * <p>
 * The following interfaces extend from this interface. The table below shows which 
 * channel-related informations are provided by which interfaces.
 * 
 * <table class="infoTable">
 * 	<thead>
 * 	  <tr class="infoTitle">
 * 		<th colspan="9">Channel Informations</th>
 *    </tr>
 *    <tr class="subHead">
 *    	<th class="head" rowspan="2">Interface</th>
 *      <th class="head" rowspan="2">Range</th>
 *      <th class="head" colspan="3" style="text-align: center;">IBasicChannelInfo</th>
 *      <th class="head" colspan="4" style="text-align: center;">Additional Informations</th>
 *    </tr>
 *    <tr class="subHead">
 *    	<th class="head">Channel ID</th>
 *      <th class="head">Channel Number</th>
 *      <th class="head">Channel Sign</th>
 *      
 *      <th class="head">Channel Name</th>
 *      <th class="head">Channel Icon</th>
 *      <th class="head">Source ID</th>
 *      <th class="head">XMLTV ID</th>
 *    </tr>
 *  </thead>
 *  <tbody>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IProgramInfo}</th>
 *      <td class="propRange">[00,-1)</td>
 *      
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_NUMBER X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_SIGN X}</td>     
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_NAME X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#SOURCE_ID X}</td>
 *      <td class="propExists">-</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IRecorderProgramInfo}</th>
 *      <td class="propRange">[00,21)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CHANNEL_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CHANNEL_NUMBER X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CHANNEL_SIGN X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CHANNEL_ICON_PATH X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IRecorderNextProgramInfo}</th>
 *      <td class="propRange">[00,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#CHANNEL_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#CHANNEL_NUMBER X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#CHANNEL_SIGN X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#CHANNEL_ICON_PATH X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IRecorderChannelInfo}</th>
 *      <td class="propRange">[28,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.IRecorderChannelInfo.Props#CHANNEL_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.IRecorderChannelInfo.Props#CHANNEL_NUMBER X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.IRecorderChannelInfo.Props#CHANNEL_SIGN X}</td>
 *      <td class="propExists">{@link org.jmythapi.IRecorderChannelInfo.Props#CHANNEL_NAME X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">{@link org.jmythapi.IRecorderChannelInfo.Props#SOURCE_ID X}</td>
 *      <td class="propExists">{@link org.jmythapi.IRecorderChannelInfo.Props#XMLTV_ID X}</td>
 *    </tr>
 *  </tbody>
 * </table>
 */
@SuppressWarnings("deprecation")
public interface IBasicChannelInfo extends IVersionable {
	/**
	 * Gets the MythTV internal channel ID.
	 *  
	 * @return 
	 * 		the ID of the channel, e.g. <code>11123</code>
	 */
	public Integer getChannelID();
	
	/**
	 * Gets the user defined channel number.
	 * 
	 * @return
	 * 		the user defined number. 
	 * 		This could contain '{@code -}', '{@code _}' or '{@code .}'
	 *   
	 * @see IRecorderChannelInfo.Props#CHANNEL_NUMBER
	 */
	public abstract String getChannelNumber();
	
	/**
	 * Gets the station abbreviation.
	 * @return 
	 * 		the channel abbreviation, e.g. <code>PULS 4</code>
	 */
	public String getChannelSign();	
	
	/**
	 * @return the source ID this channel belongs to.
	 */
	// TODO: getSourceID();
}
