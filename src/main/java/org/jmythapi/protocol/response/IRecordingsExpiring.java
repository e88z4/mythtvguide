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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_23;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get all expiring recordings of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryExpiring()} request.<br>
 * Alternatively you fetch all available recordings and then determine the expiring recordings by the
 * {@link IProgramFlags.Flags#FL_AUTOEXP autoexpire} flag.<p>
 * This interface is {@link IPropertyAware property-aware}. See {@link IRecordingsExpiring.Props} for a list of properties of this interface.
 * 
 * @see IBackend#queryExpiring()
 * @see IMythCommand#QUERY_GETEXPIRING QUERY_GETEXPIRING
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_23)
public interface IRecordingsExpiring extends IRecordings, IVersionable, IPropertyAware<IRecordingsExpiring.Props> {

	/**
	 * The properties of an {@link IRecordingsExpiring} response.
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_23)
	public static enum Props {
		/**
		 * List size.
		 * @see IRecordingsExpiring#size()
		 */
		@MythParameterType(Integer.class)
		SIZE
	}

}