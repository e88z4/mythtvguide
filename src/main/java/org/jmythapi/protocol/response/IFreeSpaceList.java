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

import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * The response to a {@link IBackend#queryFreeSpaceList(boolean)} request.
 * <p>
 * 
 * <h3>Request example:</h3>
 * {@mythCodeExample <pre>
 *    // query freespace for all hosts
 *    IFreeSpaceList freeSpaceList = backend.queryFreeSpaceList(true);
 *    
 *    // print the summary
 *    System.out.println(String.format(
 *       "MythTV has used %s space out of %s used on %d storage groups.",
 *       EncodingUtils.getFormattedFileSize(freeSpaceList.getUsedSpace()),
 *       EncodingUtils.getFormattedFileSize(freeSpaceList.getTotalSpace()),
 *       freeSpaceList.size()
 *    ));
 *    
 *    // print details for each storage group
 *    for (IFreeSpaceListEntry entry : freeSpaceList) &#123;
 *       System.out.println(String.format(
 *          "- %s : %s space out of %s used",
 *          entry.getDirectories(),
 *          EncodingUtils.getFormattedFileSize(entry.getUsedSpace()),
 *          EncodingUtils.getFormattedFileSize(entry.getTotalSpace())
 *       ));
 *    &#125;
 * </pre>}
 * 
 * The above example will output, e.g.
 * <pre>
 * MythTV has used 3,17 GB space out of 3,72 GB used on 2 storage groups.
 * - [mythbox:/mnt/data/mythpinky] : 3,14 GB space out of 3,58 GB used
 * - [mythbox:/var/lib/mythtv/livetv, mythbox:/var/lib/mythtv/recordings] : 28,00 MB space out of 137,93 MB used
 * </pre>
 * 
 * <h3>Response Example:</h3>
 * {@mythResponseExample <pre>
 *    <00>HOSTNAME: mythbox | <01>DIRECTORIES: mythbox:/mnt/data/mythpinky | <02>IS_LOCAL: false | <03>FILESYSTEM_ID: 1 | <04>STORAGE_GROUP_ID: 12 | <05>BLOCK_SIZE: 131072 | <06>TOTAL_SPACE1: 0 | <07>TOTAL_SPACE2: 1922845568 | <08>USED_SPACE1: 0 | <09>USED_SPACE2: 1814276224
 *    <00>HOSTNAME: mythbox | <01>DIRECTORIES: mythbox:/var/lib/mythtv/livetv,mythbox:/var/lib/mythtv/recordings | <02>IS_LOCAL: true | <03>FILESYSTEM_ID: 2 | <04>STORAGE_GROUP_ID: 11 | <05>BLOCK_SIZE: 4096 | <06>TOTAL_SPACE1: 0 | <07>TOTAL_SPACE2: 144629480 | <08>USED_SPACE1: 0 | <09>USED_SPACE2: 43545660
 * </pre>}
 * 
 * @see IBackend#queryFreeSpaceList(boolean)
 * @see IMythCommand#QUERY_FREE_SPACE_LIST QUERY_FREE_SPACE_LIST
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_17)
public interface IFreeSpaceList extends Iterable<IFreeSpaceListEntry>, IBasicFreeSpace,IVersionable {
	/**
	 * {@inheritDoc}
	 */
	public Long getTotalSpace();

	/**
	 * {@inheritDoc}
	 */
	public Long getUsedSpace();
	
	/**
	 * {@inheritDoc}
	 */
	public Long getFreeSpace();

	/**
	 * Returns a list, containing one entry for each backend storage-group.
	 * 
	 * @return
	 * 		a list of entries.
	 */
	public List<IFreeSpaceListEntry> asList();
	
	/**
	 * Gets the entry at the given index.
	 *  
	 * @param idx
	 * 		the index of the entry
	 * @return
	 * 		the entry
	 */
	public IFreeSpaceListEntry get(int idx); 

	/**
	 * Returns the number of entries contained in this list
	 * 
	 * @return
	 * 		the list size
	 */	
	public int size();

}