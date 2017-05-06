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
package org.jmythapi;

import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.impl.Backend;

/**
 * An interface to get connection informations of a MythTV recorder.
 * <p>
 * This interface represents the response the a {@link IBackend#getNextFreeRecorder()} and similar request.<br>
 * This interface is {@link IPropertyAware property-aware}. See {@link IRecorderInfo.Props} for all available object properties.
 * 
 * <h3>How to start:</h3>
 * The following {@link IBackend backend} functions can be used to get recorder-info object:
 * <ul>
 *    <li>Get the next free recorder:<br>
 *   	 {@code IRecorderInfo recorderInfo = backend.getNextFreeRecorder();}</li>
 *   <li>Get a busy recorder:<br>
 *  	 {@code IRecorderInfo recorderInfo = backend.getRecorderForProgram(IProgramInfo);}</li>
 *   <li>Get a recorder by id:<br>
 *  	 {@code IRecorderInfo recorderInfo = backend.getRecorderForNum(Integer);}</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * {@mythCodeExample <pre>
 *    // an already connected backend
 *    IBackend backend = ...;
 *
 *    // getting the next free recorder
 *    IRecorderInfo recorderInfo = backend.getNextFreeRecorder();
 *    if(recorderInfo == null) &#123;
 *        System.out.println("No free recorder available");
 *    &#125; else &#123;
 *        // connect to the recorder
 *        IRecorder recorder = backend.getRecorder(recorderInfo);
 *
 *        // do something with the recorder ...
 *        
 *        // close recorder connection
 *        recorder.close();
 *    &#125;
 * </pre>}
 * 
 * <h3>Response Example:</h3>
 * <br>
 * {@mythResponseExample 
 * 		<pre><0>RECORDER_ID: 2 | <1>HOSTNAME: 192.168.10.201 | <2>HOSTPORT: 6543</pre>
 * }
 * 
 * @see Backend#getFreeRecorder
 * @see Backend#getNextFreeRecorder
 * @see Backend#getRecorderForNum
 * @see Backend#getRecorderNum
 */
public interface IRecorderInfo extends IVersionable, IPropertyAware<IRecorderInfo.Props> {
	/**
	 * The {@link Props#HOSTNAME} that is returned on error.
	 */
	public static final String ERROR_HOSTNAME = "nohost";

	/**
	 * Properties of an {@link IRecorderInfo} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * [01] {@link Integer}
		 * @see IRecorderInfo#getRecorderID()
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(table="capturecard", column="cardid")
		RECORDER_ID,
		/**
		 * [02] {@link String}
		 * @see IRecorderInfo#getHostName()
		 */		
		@MythParameterType(String.class)
		@MythDatabaseColumn(column="backendIp")
		HOSTNAME,
		/**
		 * [03] {@link Integer}
		 * @see IRecorderInfo#getHostPort()
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(column="backendPort")
		HOSTPORT
	}

	public abstract int getRecorderID();

	public abstract String getHostName();

	public abstract int getHostPort();

}