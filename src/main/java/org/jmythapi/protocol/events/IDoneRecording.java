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
package org.jmythapi.protocol.events;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;

import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event- Recording Done.
 * <p>
 * {@mythProtoExample
 * 53      BACKEND_MESSAGE[]:[]DONE_RECORDING 1 436 -1[]:[]empty
 * }
 * <p>
 * 
 * @see IMythCommand#BACKEND_MESSAGE_DONE_RECORDING BACKEND_MESSAGE_DONE_RECORDING
 * @since {@mythProtoVersion 00}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
public interface IDoneRecording  extends IMythEvent<IDoneRecording.Props> {
	public static enum Props {
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)		
		RECORDER_ID,
		
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		RECORDED_SECONDS,
		
		@MythParameterType(Long.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		RECORDED_FRAMES
	}
	
	/**
	 * Gets the ID of the recorder, which has finished a recording.
	 * 
	 * @return
	 * 		the recorder ID.
	 */
	public Integer getRecorderID();
	
	/**
	 * Gets the amount of recorded seconds.
	 * @return
	 * 		the amount of recorded seconds.
	 */
	public Long getRecordedSeconds();
	
	/**
	 * Gets the amount of recorded frames.
	 * @return
	 * 		the amount of recorded frames
	 * 
	 * @since {@mythProtoVersion 45}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
	public Long getRecordedFrames();
}
