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
package org.jmythapi.protocol.request;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_33;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_65;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

/**
 * Recording types used by the {@link IBackend#queryRecordings(ERecordingsType)} request.
 */
public enum ERecordingsType {
	/**
	 * All available recordings
	 * @deprecated {@mythProtoVersion 65}
	 */	
	@MythProtoVersionAnnotation(to=PROTO_VERSION_65)
	Play,
	
	/**
	 * The same as {@link ERecordingsType#Play}, except that the recordings are listed in descending order based on start time.
	 * 
	 * @deprecated {@mythProtoVersion 65}
	 */	
	@MythProtoVersionAnnotation(to=PROTO_VERSION_65)
	Delete,	

	/**
	 * Recordings currently being recorded only. 
	 * <p>
	 * <pre>"AND recorded.endtime >= NOW() AND recorded.starttime <= NOW()"</pre>
	 * 
	 * @since {@mythProtoVersion 33}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_33,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="12704"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="6f9109591d4a558f1b78")
	})
	Recording,
	
	/**
	 * Retrieves an unsorted list.
	 * 
	 * @since {@mythProtoVersion 65}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_65)
	Unsorted,
	
	/**
	 * Retrieves a sorted list in ascending order based on the starttime.
	 * 
	 * @since {@mythProtoVersion 65}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_65)
	Ascending,
	
	/**
	 * Retrieves a sorted list in descending order based on the starttime.
	 * 
	 * @since {@mythProtoVersion 65}
	 */	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_65)
	Descending
}
