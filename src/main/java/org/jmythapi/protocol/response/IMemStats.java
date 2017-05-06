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
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the memory uage of a MythTV backend.
 * <p>
 * This interface represents the response to an {@link IBackend#queryMemStats() IBackend.queryMemStats} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IMemStats.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>TOTAL_RAM_MB: 1000 | <1>FREE_RAM_MB: 13 | <2>TOTAL_VM_MB: 3820 | <3>FREE_VM_MB: 3704</pre>
 * }
 * 
 * @see IBackend#queryMemStats()
 * @see IMythCommand#QUERY_MEMSTATS QUERY_MEMSTATS
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
public interface IMemStats extends IVersionable, IPropertyAware<IMemStats.Props> {
	/**
	 * The properties of an {@link IMemStats} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public static enum Props {
		/**
		 * The available physical and swap memory in MB
		 * @see IMemStats#getTotalRamMB()
		 */
		@MythParameterType(Integer.class)
		TOTAL_RAM_MB,
		
		/**
		 * The free physical and swap memory in MB 
		 * @see IMemStats#getFreeRamMB()
		 */
		@MythParameterType(Integer.class)
		FREE_RAM_MB,
		
		/**
		 * The available swap space in MB.
		 * @see IMemStats#getTotalVmMB()
		 */
		@MythParameterType(Integer.class)
		TOTAL_VM_MB,
		
		/**
		 * The free swap space in MB.
		 * @see IMemStats#getFreeVmMB()
		 */
		@MythParameterType(Integer.class)
		FREE_VM_MB
	}

	/**
	 * Gets the total amount of available physical and swap memory in MB.
	 * 
	 * @return 
	 * 		the available physical and swap memory in MB
	 */
	public abstract int getTotalRamMB();

	/**
	 * Gets the total amount of free physical and swap memory in MB.
	 * 
	 * @return 
	 * 		the free physical and swap memory in MB 
	 */
	public abstract int getFreeRamMB();

	/**
	 * Gets the total amount of available swap space in MB.
	 * 
	 * @return 
	 * 		the available swap space in MB
	 */
	public abstract int getTotalVmMB();

	/**
	 * Gets the total amount of free swap space in MB.
	 * 
	 * @return 
	 * 		the free swap space in MB
	 */
	public abstract int getFreeVmMB();

}