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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_66;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the free disk-space of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryFreeSpaceSummary() backend.queryFreeSpaceSummary} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IFreeSpaceSummary.Props properties-list} for all properties of this interface.
 * 
 * <h3>Request example:</h3>
 * 
 * {@mythCodeExample <pre>
 *    IFreeSpaceSummary freeSpaceSummary = backend.queryFreeSpaceSummary();
 *    System.out.println(String.format(
 *       "MythTV has used %d space out of %d.",
 *       freeSpaceSummary.getUsedSpace(),
 *       freeSpaceSummary.getTotalSpace()
 *    ));
 * </pre>}
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>TOTAL_SPACE1: 0 | <1>TOTAL_SPACE2: 2067475048 | <2>USED_SPACE1: 0 | <3>USED_SPACE2: 1856202500</pre>
 * }
 * 
 * @see IBackend#queryFreeSpaceSummary()
 * @see IMythCommand#QUERY_FREE_SPACE_SUMMARY QUERY_FREE_SPACE_SUMMARY
 * 
 * @since {@mythProtoVersion 32}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
public interface IFreeSpaceSummary extends IBasicFreeSpace, IVersionable, IPropertyAware<IFreeSpaceSummary.Props> {

	/**
	 * The properties of an {@link IFreeSpaceSummary} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
	public static enum Props {
		/**
		 * @see IFreeSpaceSummary#getTotalSpace()
		 * 
		 * @since {@mythProtoVersion 32}
		 * @deprecated {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32,to=PROTO_VERSION_66)
		TOTAL_SPACE1,
		
		/**
		 * @see IFreeSpaceSummary#getTotalSpace()
		 * 
		 * @since {@mythProtoVersion 32}
		 * @deprecated {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32,to=PROTO_VERSION_66)
		TOTAL_SPACE2,
		
		/**
		 * @see IFreeSpaceSummary#getTotalSpace()
		 * @since {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_66)
		TOTAL_SPACE,
		
		/**
		 * @see IFreeSpaceSummary#getUsedSpace()
		 * 
		 * @deprecated {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32,to=PROTO_VERSION_66)
		USED_SPACE1,
		
		/**
		 * @see IFreeSpaceSummary#getUsedSpace()
		 * 
		 * @deprecated {@mythProtoVersion 66}
		 */
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32,to=PROTO_VERSION_66)
		USED_SPACE2,
		
		/**
		 * @see IFreeSpaceSummary#getUsedSpace()
		 * @since {@mythProtoVersion 32}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_66)
		USED_SPACE
	}

	/**
	 * {@inheritDoc}
	 * @see IFreeSpaceSummary.Props#TOTAL_SPACE
	 */
	public abstract Long getTotalSpace();

	/**
	 * {@inheritDoc}
	 * @see IFreeSpaceSummary.Props#USED_SPACE
	 */	
	public abstract Long getUsedSpace();

}