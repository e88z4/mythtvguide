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
package org.jmythapi.protocol;



/**
 * A request or response message that can be send over network.
 * <p>
 * Each object implementing this interface must implement the method {@link #getPacket()}.
 * This method is used to convert the payload of the sendable object into a MythTV-protocol packet,
 * which can be send over network.
 */
public interface ISendable {
	/**
	 * Converts the object into a packet that can be send over network.
	 * 
	 * @return 
	 * 		a packet object that can be send over network
	 */
	public IMythPacket getPacket();
}
