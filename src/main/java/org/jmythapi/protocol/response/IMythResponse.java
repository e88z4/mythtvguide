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
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ISendable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.protocol.utils.ResponseUtils;

/**
 * This interface represents a MythTV response object.
 * <p>
 * A response object consists of a list of response arguments. This response arguments are simple 
 * strings that were received over network in the form of a {@link IMythPacket packet}.
 * 
 * <h3>Using Response Objects:</h3>
 * 
 * To use a response object, a previously received response packet needs to be converted into a more easily to read response-object, 
 * via the utility class {@link ResponseUtils}:
 * 
 * {@mythCodeExample <pre>
 *    // read the response packet from network
 *    IMythPacket resp = connection.readPacket();
 *    
 *    // convert the response packet into a specific response object
 *    IRecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
 * </pre>}
 * 
 * <h4>Response Object Properties:</h4>
 * All response objects are {@link IPropertyAware property-aware}. Therefore the advantage of a response-object 
 * in comparison to a the raw packet is the enumeration property class, that belongs to each type of response 
 * object. 
 * 
 * <h5>Property Position and Version Range</h5>
 * The enumeration properties of a response-object are used to describe, at which position in the response-arguments list, the 
 * value for a specific property can be found. Additionally for each property a {@link MythProtoVersionAnnotation version-range} 
 * is specified, describing in which protocol version a specific property is part of a response. 
 * 
 * <h5>Usage example:</h5>
 * 
 * For example if we have received the arguments of a recorder-info object over network, we can convert
 * the received packet to a {@code IRecorderInfo} object (as show in the following example) and then we can use the 
 * enumeration property {@code HOSTNAME} to get the hostname of the recorder.
 *  
 * {@mythCodeExample <pre>
 *    IMythPacket resp = ...; // a previously received myth packet
 * 
 *    // convert the response packet into a specific response object
 *    RecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
 *    
 *    // read a property value
 *   String hostName = recorderInfo.getPropertyValue(IRecorderInfo.Props.HOSTNAME);
 *   
 *   // the above call is similar to
 *   hostName = recorderInfo.getHostName();
 * </pre>}
 * 
 * The following protocol packets are send and received via network:
 * 
 * {@mythProtoExample notitle
 * 29      GET_NEXT_FREE_RECORDER[]:[]-1
 * 26      1[]:[]192.168.0.2[]:[]6543
 * }
 * 
 * As shown above, the various arguments of response object can be fetched using the enumeration constants
 * of the response object.
 * <br>
 * The advantage of this solution is, that you do not need to know, that the response argument <code>hostname</code>
 * is located at position {@code 1} in the response array. Furthermore the position of an argument may change 
 * depending of the protocol-version, the backend is speaking. When using the enumeration constants to fetch an argument, 
 * we do not need to take care about this.
 * Moreover, an enumeration property may not be part of the response in the current protocol version. In this case the  
 * the property-aware object will just return {@code null} as value.
 *  
 * <h4>Which properties are valid for a given Protocol Version</h4>
 * The method {@link #getPropertyValue(Enum)} internally uses helper function of the utility
 * class {@link EnumUtils} to determine, which enumeration properties are available in which protocol-version and
 * at which position in the response arguments array, the value for a given property can be found.
 * 
 * <h5>Usage example:</h5>
 * To iterate over all available properties of a response object the following can be done:
 * 
 * {@mythCodeExample <pre>
 *    EnumMap&lt;IRecorderInfo.Props,String&gt; props = recorderInfo.getPropertyMap();
 *    for(Entry&lt;IRecorderInfo.Props,String&gt; prop : props.entrySet()) &#123;
 *       System.out.println(String.format(
 *          "Property %s=%s", prop.getKey(),prop.getValue()
 *       ));
 *    &#125;
 * </pre>}
 * 
 * There are more useful functions to get and set properties. See {@link IPropertyAware} for details. 
 * 
 * @param <E>
 * 		the type of the response object properties
 * 
 * @see ResponseUtils#readFrom
 * @see EnumUtils
 */
public interface IMythResponse <E extends Enum<E>> extends IPropertyAware<E>, IVersionable, ISendable {
}
