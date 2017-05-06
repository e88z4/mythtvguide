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

import java.util.Date;
import java.util.EnumMap;

import org.jmythapi.IPropertyAware;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.utils.PropertyAwareUtils;

/**
 * Backend Event - Recording - Properties Updated.
 * <p>
 * 
 * @param <E>
 * 		the properties of the event.
 * 
 * @see IRecordingListChangeUpdate
 * @see IUpdateProgInfo
 */
public interface IRecordingUpdateEvent <E extends Enum<E>> extends IRecordingEvent<E> {
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
	 * Compares a previous version of the recording with the changed version.
	 * <p>
	 * This function compares each property of the recordings and returns a map containing all
	 * changed properties
	 * <p>
	 * 
	 * <h3>Usage Example:</h3>
	 * {@mythCodeExample <pre>
	 *    EnumMap<IProgramInfo.Props,Object[]> diffMap = ((IRecordingListChangeUpdate)event).getUpdatedProperties(oldRecording);
	 *    
	 *    // pring changes
	 *    System.out.println(String.format(
	 *       "%d properties of recording changed:",
	 *       Integer.valueOf(diffMap.size()), recording.getBaseName()
	 *    ));							
	 *    for(Entry<IProgramInfo.Props,Object[]> entry : diffMap.entrySet()) &#123;
	 *       IProgramInfo.Props property = entry.getKey();
	 *       Object[] values = entry.getValue();
	 *       System.out.println(String.format(
	 *          "\r\n   %s: %s -> %s",
	 *          property, values[0],values[1]
	 *       ));
	 *    &#125;						 	
	 * </pre>}
	 * 
	 * 
	 * @param oldProgramInfo
	 * 		a previous version of the recording
	 * @return
	 * 		a map containing all found differences
	 * 
	 * @see PropertyAwareUtils#compare(IPropertyAware, IPropertyAware)
	 */	
	public EnumMap<IProgramInfo.Props,Object[]> getUpdatedProperties(IProgramInfo oldProgramInfo);
}
