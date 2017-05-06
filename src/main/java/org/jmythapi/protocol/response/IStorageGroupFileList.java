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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_44;

import java.util.List;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * The response to a {@link IBackend#queryStorageGroupFileList} request.
 * 
 * @see IBackend#queryStorageGroupFileList
 * @see IMythCommand#QUERY_SG_GETFILELIST QUERY_SG_GETFILELIST
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
public interface IStorageGroupFileList extends Iterable<IStorageGroupFile> {
	/**
	 * The deliminator to split the file-properties into separate values.
	 */
	public static final String FILE_PROPS_DELIM = "::";
	
	/**
	 * Gets a list of files stored in the storage group.
	 * 
	 * @return
	 * 		a list of files
	 */
	public List<IStorageGroupFile> asList();
	
	/**
	 * Gets the amount of files stored in the storage group.
	 * @return
	 * 		the amount of files of the storage group
	 */
	public abstract int size();
}
