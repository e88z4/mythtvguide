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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_58;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ProtocolVersionInfo;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to geht the state of a MythTV encoder.
 * <p>
 * This interface represents the response to a {@link IRemoteEncoder#getState()} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRemoteEncoderState.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * {@mythResponseExample
 * 		If the encoder is recording:
 * 		<pre><0>STATE: RECORDING_ONLY</pre>
 * 		If watching Live TV:
 * 		<pre><0>STATE: WATCHING_LIVE_TV</pre>
 * 		If the encoder is idle:
 * 		<pre><0>STATE: NONE</pre>
 * }
 * 
 * @see IRemoteEncoder#getState()
 * @see IMythCommand#QUERY_REMOTEENCODER_GET_STATE QUERY_REMOTEENCODER_GET_STATE
 */
public interface IRemoteEncoderState extends IVersionable, IPropertyAware<IRemoteEncoderState.Props> {

	/**
	 * The properties of an {@link IRemoteEncoderState} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * The current state of a remote encoder.
		 * @see IRemoteEncoderState#getState()
		 */
		@MythParameterType(State.class)
		STATE
	}

	/**
	 * The states of an {@link IRemoteEncoderState} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/tree/master/mythtv/libs/libmythtv/tv.h">tv.h</a>
	 */
	public static enum State {
	    /** 
	     * None.
	     * <p>
	     * None State, this is the initial state in both TV and TVRec, it
	     * indicates that we are ready to change to some other state.
	     * 
	     * @since {@mythProtoVersion 00}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		NONE,
		
	    /** 
	     * Watching LiveTV.
	     * <p>
	     * Watching LiveTV is the state for when we are watching a
	     * recording and the user has control over the channel and
	     * the recorder to use.
	     * 
	     * @since {@mythProtoVersion 00}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		WATCHING_LIVE_TV,
	    
	    /** 
	     * Watching Pre-recorded.
	     * <p>
	     * Watching Pre-recorded is a TV only state for when we are
	     * watching a pre-existing recording.
	     * 
	     * @since {@mythProtoVersion 00}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		WATCHING_PRERECORDED,
		
	    /** 
	     * Watching Video.
	     * <p>
	     * Watching Video is the state when we are watching a video and is not a dvd
	     * 
	     * @since {@mythProtoVersion 45}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.SVN_COMMIT,value="20037"),
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="3ba6d8ebaeba6eb19947")
		})
		WATCHING_VIDEO,	
	    
	    /** 
	     * Watching DVD.
	     * <p>
	     * Watching DVD is the state when we are watching a DVD 
	     * 
	     * @since {@mythProtoVersion 45}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.SVN_COMMIT,value="20037"),
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="3ba6d8ebaeba6eb19947")
		})
		WATCHING_DVD,
	    
		/**
		 * Watching BD.
		 * <p>
		 *  Watching BD is the state when we are watching a BD.
		 *  
		 *  @since {@mythProtoVersion 58}
		 *  
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_58,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.SVN_COMMIT,value="25058"),
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="5d632571796218fa2780")
		})		
		WATCHING_BD,
		
	    /** 
	     * Watching Recording.
	     * <p>
	     * Watching Recording is the state for when we are watching
	     * an in progress recording, but the user does not have control
	     * over the channel and recorder to use.
	     * 
	     * @since {@mythProtoVersion 00}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		WATCHING_RECORDING,
		    
	    /** 
	     * Recording Only.
	     * <p>
	     * Recording Only is a TVRec only state for when we are recording
	     * a program, but there is no one currently watching it.
	     * 
	     * @since {@mythProtoVersion 00}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		RECORDING_ONLY,
	    
	    /** 
	     * Changing State.
	     * <p>
	     * This is a placeholder state which we never actually enter,
	     * but is returned by GetState() when we are in the process
	     * of changing the state.
	     * 
	     * @since {@mythProtoVersion 00}
	     */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_00)
		CHANGING_STATE
	}

	/**
	 * Gets the current state of a remote encoder as integer value.
	 * <p>
	 * This is the ordinal number of the {@link Props}.
	 *  
	 * @return
	 * 		the current state as integer value.
	 */
	public abstract int getStateValue();

	/**
	 * Gets the current state of a remote encoder.
	 * 
	 * @return 
	 * 		the encoder state or {@code null} on errors.
	 */
	public abstract State getState();
	
	public boolean hasState(State state);
	
	public boolean hasState(State... states);
}