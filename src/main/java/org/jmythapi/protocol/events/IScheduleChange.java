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

import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Schedule Changed.
 * <p>
 * {@mythProtoExample
 * 45      BACKEND_MESSAGE[]:[]SCHEDULE_CHANGE[]:[]empty
 * }
 * 
 * @see IMythCommand#BACKEND_MESSAGE_SCHEDULE_CHANGE BACKEND_MESSAGE_SCHEDULE_CHANGE
 * @since {@mythProtoVersion 00}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
public interface IScheduleChange extends IMythEvent<IScheduleChange.Props>{
	public static enum Props {
		// no properties available
	}
}
