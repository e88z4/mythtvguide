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

import java.util.Date;

import org.jmythapi.protocol.impl.Recorder;

/**
 * Specifies the direction used for channel browsing.
 * 
 * @see Recorder#getNextProgramInfo(String, Integer, EChannelBrowseDirection, Date) 
 * @see <a href="https://github.com/MythTV/mythtv/tree/master/mythtv/libs/libmythtv/tv.h">tv.h</a>
 */
public enum EChannelBrowseDirection {
	/**
	 * Same channel and time.
	 * <p>
	 * Fetch browse information on current channel and time.
	 */
	SAME,
	
	/**
	 * Previous channel.
	 * <p>
	 * Fetch information on previous channel.
	 */
	UP,
	
	/**
	 * Next channel.
	 * <p>
	 * Fetch information on next channel.
	 */
	DOWN,
	
	/**
	 * Same channel in the past.
	 * <p>
	 * Fetch information on current channel in the past.
	 */
	LEFT,
	
	/**
	 * Same channel in the future.
	 * <p>
	 * Fetch information on current channel in the future.
	 */
	RIGHT,
	
	/**
	 * Next favorite channel.
	 * <p>
	 * Fetch information on the next favorite channel.
	 */
	FAVORITE
}
