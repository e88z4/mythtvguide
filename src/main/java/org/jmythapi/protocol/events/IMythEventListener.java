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

import java.util.EventListener;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IMythPacket;

/**
 * Backend Event Listener-Interface.
 * <p>
 * This is a listener interface to receive MythTV {@link IMythEvent backend events}.
 * <p>
 * 
 * <h3>Register/Unregister event-listeners</h3>
 * The following {@link IBackend} methods can be used to (un)register event-listeners.
 * <ul>
 *    <li>{@link IBackend#addEventListener(Class, IMythEventListener) addEventListener} to register a listener.</li>
 *    <li>{@link IBackend#removeEventListener(Class, IMythEventListener) removeEventListener} to unregister a listener.</li>
 * </ul>
 * 
 * <h3>Register/Unregister event-packet listeners</h3>
 * Alternatively it is possible to receive the plain Myth-protocol event {@link IMythPacket packets}.<br>
 * See {@link IMythEventPacketListener} on how to receive event-packets.
 * 
 * @param <E>
 * 		the type of the event a listener should be registered for.
 * 
 * @see IBackend#addEventListener(Class, IMythEventListener)
 * @see IBackend#removeEventListener(Class, IMythEventListener)
 * @see IMythEventPacketListener
 */
public interface IMythEventListener <E extends IMythEvent<?>> extends EventListener {
	public void fireEvent(E event);
}
