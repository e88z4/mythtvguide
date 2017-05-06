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

import java.util.Date;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IVersionable;

/**
 * This interface is used to access basic program informations.
 * <p>
 * The following interfaces extend from this interface. The table below shows which
 * program-related informations are provided by which interfaces.
 * 
 * <table class="infoTable">
 * 	<thead>
 * 	  <tr class="infoTitle">
 * 		<th colspan="16">Program Informations</th>
 *    </tr>
 *    <tr class="subHead">
 *    	<th class="head" rowspan="2">Interface</th>
 *      <th class="head" rowspan="2">Range</th>
 *      <th class="head" colspan="8" style="text-align: center;">IBasicProgramInfo</th>
 *      <th class="head" colspan="6" style="text-align: center;">Additional Informations</th>
 *    </tr>
 *    <tr class="subHead">
 *    	<th class="head">Title</th>
 *      <th class="head">Sub<wbr>title</th>
 *      <th class="head">Descr.</th>
 *      <th class="head">Category</th>
 *      <th class="head">Start Time</th>
 *      <th class="head">End Time</th>
 *      <th class="head">Series ID</th>
 *      <th class="head">Program ID</th>
 *      <th class="head">Channel Infos</th>
 *      <th class="head">Output Filters</th>
 *      <th class="head">Repeat</th>
 *      <th class="head">Air<wbr>date</th>
 *      <th class="head">Stars</th>
 *      <th class="head">Recording Infos</th>
 *    </tr>
 *  </thead>
 *  <tbody>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IRecorderNextProgramInfo}</th>
 *      <td class="propRange">[00,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#TITLE X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#SUBTITLE X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#DESCRIPTION X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#CATEGORY X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#START_DATE_TIME X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#END_DATE_TIME X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#SERIES_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#PROGRAM_ID X}</td>
 *      <td class="propExists">{@link IBasicChannelInfo X}</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *      <td class="propExists">-</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IRecorderProgramInfo}</th>
 *      <td class="propRange">[00,21)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#TITLE X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#SUBTITLE X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#DESCRIPTION X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CATEGORY X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#START_DATE_TIME X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#END_DATE_TIME X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#SERIES_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#PROGRAM_ID X}</td>
 *      <td class="propExists">{@link IBasicChannelInfo X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CHANNEL_OUTPUT_FILTERS X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#REPEAT X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#ORIGINAL_AIRDATE X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#STARS X}</td>
 *      <td class="propExists">-</td>
 *    </tr>
 *    <tr class="propInfo">
 *      <th class="propName">{@link IProgramInfo}</th>
 *      <td class="propRange">[00,-1)</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#TITLE X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#SUBTITLE X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#DESCRIPTION X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CATEGORY X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#START_DATE_TIME X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#END_DATE_TIME X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#SERIES_ID X}</td>
 *      <td class="propExists propertyInherited">{@link org.jmythapi.protocol.response.IProgramInfo.Props#PROGRAM_ID X}</td>
 *      <td class="propExists">{@link IBasicChannelInfo X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_OUTPUT_FILTERS X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#REPEAT X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#ORIGINAL_AIRDATE X}</td>
 *      <td class="propExists">{@link org.jmythapi.protocol.response.IProgramInfo.Props#STARS X}</td>
 *      <td class="propExists">{@link IProgramInfo X}</td>
 *    </tr>
 */
public interface IBasicProgramInfo extends IVersionable {
	/**
	 * Gets the program title.
	 * @return 
	 * 		the title of the program
	 */
	public String getTitle();
	
	/**
	 * Gets the program subtitle.
	 * @return 
	 * 		the subtitle of the program
	 */
	public String getSubtitle();
	
	/**
	 * Gets the full title of the program.
	 * <p>
	 * If a program has no subtitle, than this function is the same as {@code getTitle()}, 
	 * otherwise the returned full title is in the format "{@code title - subtitle}"
	 *  
	 * @return
	 * 		the full title of the program
	 */
	public String getFullTitle();
	
	/**
	 * Gets the program description.
	 * @return 
	 * 		the program description
	 */
	public String getDescription();
	
	/**
	 * Gets the category of the program.
	 * @return 
	 * 		the program category, e.g. <code>Comedy</code>
	 */
	public String getCategory();
	
	/**
	 * Gets the scheduled start of the program.
	 * @return
	 * 		the scheduled start time.
	 */
	public Date getStartDateTime();
	
	/**
	 * Gets the scheduled end of the program.
	 * @return
	 * 		the scheduled end time.
	 */
	public Date getEndDateTime();
	
	/**
	 * Gets the duration for the program in minutes.
	 * 
	 * @return 
	 * 		the duration of the program in minutes or <code>-1</code> if unknown. 
	 */	
	public int getDuration();
	
	/**
	 * Gets the the start time of the program in minutes after midnight.
	 * @return
	 * 		the start time in minutes after midnight
	 */
	public int getMinutesAfterMidnight();
	
	/**
	 * Gets the series ID of the program.
	 * @return
	 * 		the series ID.
	 */
	public String getSeriesID();
	
	/**
	 * Get the program ID.
	 * @return
	 * 		the program ID.
	 */
	public String getProgramID();
	
	/**
	 * Checks if the program object contains valid data.
	 * 
	 * @return
	 * 		{@code true} if the program contains valid data.
	 */
	public boolean isValid();
	
	/**
	 * Generates a unique id for this program-info.
	 * <p>
	 * This id has the format: <code>[channel-id]_[program-start-time]</code>
	 * 
	 * @return
	 * 		a unique id, e.g. {@code 1000_20110719162900}
	 */
	public String getUniqueProgramId();
}
