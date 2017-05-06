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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_57;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

/**
 * <p>This {@link Enum} can be used to define, which types of events a client is interested to receive via a playback- or monitor 
 * socket.</p>
 *  
 * @see IBackend#annotatePlayback(String, EPlaybackSockEventsMode)
 * @see <a href="http://svn.mythtv.org/trac/browser/trunk/mythtv/programs/mythbackend/playbacksock.h">playbacksock.h</a>
 * @see IBackend#annotatePlayback(String, EPlaybackSockEventsMode)
 */
public enum EPlaybackSockEventsMode {
	/**
	 * No events.
	 * <p>
	 * The client wants to receive no events.
	 */
	NONE,
	
	/**
	 * All events.
	 * <p>
	 * The client wants to receive all events.
	 */
	NORMAL,
	
	/**
	 * Non-system events.
	 * <p>
	 * The client wants to receive non-system events.
	 * 
	 * @since {@mythProtoVersion 57} 
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_57,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="bfa20e9cd66ce89929f10bdeeecf75d2b7fd1166"),
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="23636")
	})
	NON_SYSTEM,
	
	/**
	 * System-only events.
	 * <p>
	 * The client wants to receive only system events.
	 * 
	 * @since {@mythProtoVersion 57}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_57,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="bfa20e9cd66ce89929f10bdeeecf75d2b7fd1166"),
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="23636")
	})
	SYSTEM_ONLY
}
