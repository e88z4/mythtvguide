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
package org.jmythapi.protocol.annotation;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * This annotation describes a protocol command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MythProtocolCmd {
	/**
	 * @return the MythTV-protocol version where the command was introduced.
	 */
	MythProtoVersionAnnotation protoVersion() default @MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_LATEST);
	
	/**
	 * @return the parent-command if any or <code>""</code> if no parent exists.
	 */
	String parentCommand() default "";
	
	/**
	 * @return the position of sub-commands, if any. e.g.:<p/>
	 * "1" means in the second {@link IMythPacket} argument.<br/>
	 * "0.1" means in the second command-argument of the {@link IMythCommand}
	 */
	String subCommandPos() default "";
	
	/**
	 * TODO: currently only used for documentation purposes
	 * @return
	 * 		the class used to parse the response
	 */
	Class<?> responseClass() default Object.class;
}
