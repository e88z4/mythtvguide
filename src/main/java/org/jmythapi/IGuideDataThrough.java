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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_15;

import java.util.Date;

import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;

/**
 * This interface represents the response to an {@link IBackend#queryGuideDataThrough()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IGuideDataThrough.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample <pre>
 * 		<0>DATE_TIME: 2011-04-07 03:00
 * </pre>}
 * 
 * @since {@mythProtoVersion 15}
 * @see IBackend#queryGuideDataThrough()
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_15)
public interface IGuideDataThrough extends IVersionable, IPropertyAware<IGuideDataThrough.Props> {
	/**
	 * Properties of an {@link IGuideDataThrough} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_15)
	public static enum Props {
		/**
		 * The end date of the available guide-date
		 * @see IGuideDataThrough#getDate()
		 */
		@MythDatabaseColumn(column="maxDate")
		@MythParameterType(Date.class)
		DATE_TIME
	}

	/**
	 * Gets the end date of the available guide-date.
	 * 
	 * @return 
	 * 		the end date of the available guide-data or {@code null} if unknown.
	 */
	public Date getDate();

	/**
	 * Gets the amount of hours for which guide-data is available.
	 * 
	 * @return
	 * 		how many hours guide data is available
	 */
	public int getHours();
}