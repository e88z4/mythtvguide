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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_15;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;

/**
 * An interface to get the load of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryLoad()} request. <br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link ILoad.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample 
 * 		<pre><0>LOAD_AVERAGE_NOW: 0.04 | <1>LOAD_AVERAGE_LAST_5MINS: 0.06 | <2>LOAD_AVERAGE_LAST_15MINS: 0.06</pre>
 * }
 * 
 * @since {@mythProtoVersion 15}
 * @see IBackend#queryLoad()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
public interface ILoad extends IVersionable, IPropertyAware<ILoad.Props> {

	/**
	 * The properties of an {@link ILoad} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public static enum Props {
		/**
		 * The current backend load.
		 * @see ILoad#getCurrentLoad()
		 */
		@MythParameterType(Double.class)
		LOAD_AVERAGE_NOW,
		
		/**
		 * Load average of the last 5 minutes.
		 * @see ILoad#getLast5MinsLoad()
		 */
		@MythParameterType(Double.class)
		LOAD_AVERAGE_LAST_5MINS,
		
		/**
		 * Load average of the last 15 minutes.
		 * @see ILoad#getLast15MinsLoad()
		 */
		@MythParameterType(Double.class)
		LOAD_AVERAGE_LAST_15MINS
	}

	/**
	 * Gets the current backend load.
	 * 
	 * @return
	 * 		the current backend load
	 */
	public double getCurrentLoad();

	/**
	 * Get the backend load average of the last 5 minutes.
	 * 
	 * @return
	 * 		the load average of the last 5 minutes
	 */
	public double getLast5MinsLoad();

	/**
	 * Gets the backend load average of the last 15 minutes.
	 * 
	 * @return
	 * 		the load average of the last 15 minutes
	 */
	public double getLast15MinsLoad();

}