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

import org.jmythapi.protocol.request.IMythCommand;

/**
 * Backend Event - Recordings-List Changed.
 * <p>
 * This type of event represents changes to the backends recording list.
 * <p>
 * This is a parent interface for the following sub-interfaces:
 * <ul>
 * 	<li>{@link IRecordingListChangeList} (protocol versions &lt; {@mythProtoVersion 55})</li>
 *  <li>{@link IRecordingListChangeSingle}<br/>
 *  	<ul>
 *  		<li>{@link IRecordingListChangeAdd}</li>
 *  		<li>{@link IRecordingListChangeUpdate}</li>
 *  		<li>{@link IRecordingListChangeDelete}</li>
 *  	</ul>
 *  </li>
 * </ul>
 * 
 * @see IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE BACKEND_MESSAGE_RECORDING_LIST_CHANGE
 */
public interface IRecordingListChange <E extends Enum<E>> extends IMythEvent<E> {

}
