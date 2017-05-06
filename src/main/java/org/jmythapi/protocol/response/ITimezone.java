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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_42;

import java.util.Date;
import java.util.TimeZone;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Am interface to get the timezone of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryTimeZone()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link ITimezone.Props properties-list} for all properties of this interface.
 * 
 * <h3>Rresponse example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>TIMEZONE_ID: Europe/Vienna | <1>UTC_OFFSET: 7200 | <2>CURRENT_DATE: Sat Apr 02 20:07:22 CEST 2011</pre>
 * }
 * 
 * @see IBackend#queryTimeZone()
 * @see IMythCommand#QUERY_TIME_ZONE QUERY_TIME_ZONE
 * 
 * @since {@mythProtoVersion 42}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_42)
public interface ITimezone extends IVersionable, IPropertyAware<ITimezone.Props> {
	/**
	 * Properties of the {@link ITimezone} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_42)
	public static enum Props {
		/**
		 * [01]
		 * @see ITimezone#getTimeZoneID()
		 */
		TIMEZONE_ID,
		
		/**
		 * [02]
		 * @see ITimezone#getUTCOffset()
		 */
		@MythParameterType(Integer.class)
		UTC_OFFSET,
		
		/**
		 * [03]
		 * @see ITimezone#getCurrentDate()
		 */
		@MythParameterType(Date.class)
		CURRENT_DATE
	}

	public abstract String getTimeZoneID();

	public abstract Integer getUTCOffset();

	public abstract TimeZone getTimeZone();

	public abstract Date getCurrentDate();

}