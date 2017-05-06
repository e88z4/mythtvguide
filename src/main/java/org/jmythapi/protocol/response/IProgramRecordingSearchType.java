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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1072;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_09;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_15;

import org.jmythapi.IVersionable;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.ProtocolVersionInfo;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

/**
 * @since {@mythProtoVersion 09}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_09,fromInfo={
	@MythProtoVersionMetadata(key=ProtocolVersionInfo.DATE,value="2004-05-29"),
	@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="35c8b08a72dd27d59f7316f9d8e181dbdd578e40"),
	@MythProtoVersionMetadata(key=ProtocolVersionInfo.SVN_COMMIT,value="3765")
})
public interface IProgramRecordingSearchType extends IEnumGroup<IProgramRecordingSearchType.Type>, IVersionable {
	
	/**
	 * The types of the {@link IProgramRecordingSearchType} object.
	 * <p>
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/blob/master/mythtv/libs/libmythtv/recordingtypes.h">libmythtv/recordingtypes.h</a>
	 * @see <a href="https://github.com/MythTV/mythtv/blob/2907eea4d2c76fb7bc68f1b09e6c3c41b3b84655/mythtv/libs/libmythtv/scheduledrecording.h">libmythtv/scheduledrecording.h (till 2004-05-29)</a>
	 */
	public static enum Type {
		NO_SEARCH,
		POWER_SEARCH,
		TITLE_SEARCH,
		KEYWORD_SEARCH,
		PEOPLE_SEARCH,
		
		@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.DATE,value="2005-02-19"),
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="e73b621fa1fce924519e18c7002fa4e7921557dd"),
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.SVN_COMMIT,value="5656")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1072)
		MANUAL_SEARCH
	}
}
