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

import java.util.Date;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the uptime of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryUptime()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IUptime.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample <pre>
 * 		<0>UPTIME_SECONDS: 1163950
 * </pre>}
 * 
 * @see IBackend#queryUptime()
 * @see IMythCommand#QUERY_UPTIME QUERY_UPTIME
 * 
 * @since {@mythProtoVersion 15}
 * @mythProtoVersionRange
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
public interface IUptime extends IVersionable, IPropertyAware<IUptime.Props> {

	/**
	 * Properties of the {@link IUptime} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
	public static enum Props {
		/**
		 * The Uptime in seconds or {@code -1} if unknown
		 * 
		 * @see IUptime#getUptimeSeconds()
		 */
		@MythParameterType(Integer.class)
		UPTIME_SECONDS
	}

	/**
	 * Gets the uptime in seconds
	 * 
	 * @return 
	 * 		the uptime in seconds or {@code -1} if unknown
	 * 
	 * @see Props#UPTIME_SECONDS
	 */
	public Integer getUptimeSeconds();
	
	public Integer getUptimeSeconds(Date uptimeLoadedDate);
	
	/**
	 * Gts the startup time of the backend.
	 * <p>
	 * This date/time is calculated using the uptime in seconds. 
	 * 
	 * @return
	 * 		the backend startup time
	 */
	public Date getStartupTime();
	
	public Date getStartupTime(Date uptimeLoadedDate);
}