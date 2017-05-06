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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the free disk space of a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryFreeSpace() IBackend.queryFreeSpace} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IFreeSpace.Props properties-list} for all properties of this interface.
 * 
 * <h3>Request example:</h3>
 * {@mythCodeExample <pre>
 *    IFreeSpace freeSpace = backend.queryFreeSpace();
 *    System.out.println(String.format(
 *       "MythTV has used %d space out of %d.",
 *       freeSpace.getUsedSpace(),
 *       freeSpace.getTotalSpace()
 *    ));
 * </pre>}
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>TOTAL_SPACE: 13817 | <1>USED_SPACE: 50</pre>
 * }
 * 
 * @see IBackend#queryFreeSpace()
 * @see IMythCommand#QUERY_FREESPACE QUERY_FREESPACE
 * 
 * @since {@mythProtoVersion 00}
 * @deprecated {@mythProtoVersion 17}
 */
@MythProtoVersionAnnotation(to=PROTO_VERSION_17)
public interface IFreeSpace extends IBasicFreeSpace, IVersionable, IPropertyAware<IFreeSpace.Props> {

	/**
	 * The properties of an {@link IFreeSpace} response.
	 * 
	 * {@mythProtoVersionMatrix}.
	 */
	@MythProtoVersionAnnotation(to=PROTO_VERSION_17)
	public static enum Props {
		/**
		 * Total space in megabytes on file system.
		 * 
		 * @see IFreeSpace#getTotalSpace()
		 * @see IBasicFreeSpace#getTotalSpace()
		 */
		@MythParameterType(Long.class)
		TOTAL_SPACE,
		
		/**
		 * Used space in megabytes on file system.
		 * 
		 * @see IFreeSpace#getUsedSpace()
		 * @see IBasicFreeSpace#getUsedSpace()
		 */
		@MythParameterType(Long.class)
		USED_SPACE
	}

	/**
	 * {@inheritDoc}
	 * @see IFreeSpace.Props#TOTAL_SPACE
	 */
	public abstract Long getTotalSpace();

	/**
	 * {@inheritDoc}
	 * @see IFreeSpace.Props#USED_SPACE
	 */
	public abstract Long getUsedSpace();	
	
	public void addUsedSpace(long spaceDiff);
	
	public void setUsedSpace(long usedSpace);

}