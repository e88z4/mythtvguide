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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_24;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to check if a channel-number prefix matches.
 * <p>
 * This interface represents the response of an {@link IRecorder#checkChannelPrefix(String) IRecorder.checkChannelPrefix} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecorderChannelPrefixStatus.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>IS_PREFIX_MATCH: true | <1>IS_COMPLETE_MATCH: true | <2>IS_EXTRA_CHAR_USEFUL: false | <3>NEEDED_SPACER: X</pre>
 * }
 * 
 * @see IRecorder#checkChannelPrefix(String)
 * @see IMythCommand#QUERY_RECORDER_CHECK_CHANNEL_PREFIX QUERY_RECORDER_CHECK_CHANNEL_PREFIX
 */
public interface IRecorderChannelPrefixStatus extends IVersionable, IPropertyAware<IRecorderChannelPrefixStatus.Props> {
	/**
	 * The properties of an {@link IRecorderChannelPrefixStatus} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * If the prefix matches at least one channel on the recorder,
		 * this is set to {@code true}.
		 * 
		 * @see IRecorderChannelPrefixStatus#isPrefixMatch()
		 */
		@MythParameterType(Boolean.class)
		IS_PREFIX_MATCH,
		
		/**
		 * If the prefix matches any channel entirely (i.e. prefix == channum),
		 * then this is set to {@code true}.
		 * 
		 * @see IRecorderChannelPrefixStatus#isCompleteMatch()
		 */
		@MythParameterType(Boolean.class)
		IS_COMPLETE_MATCH,
		
		/**
		 * If adding another character could reduce the number of channels the
		 * prefix matches then this is set to {@code true}.
		 * 
		 * @see IRecorderChannelPrefixStatus#isExtraCharUseful()
		 * 
		 * @since {@mythProtoVersion 24}
		 */
		@MythParameterType(Boolean.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_24)
		IS_EXTRA_CHAR_USEFUL,
		
		/**
		 * If in order for the prefix to match a channel, a spacer needs to be added, 
		 * the first matching spacer is returned.
		 *  
		 * @see IRecorderChannelPrefixStatus#getNeededSpacer()
		 *  
		 * @since {@mythProtoVersion 24}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_24)
		NEEDED_SPACER
	}
	
	/**
	 * Checks if the prefix matches at least one channel number.
	 * @return
	 * 		{@code true} if there are channels with channel numbers starting with the prefix.
	 */
	public Boolean isPrefixMatch();
	
	/**
	 * Checks if the prefix fully matches a channel number.
	 * @return
	 * 		{@code true} if a complete channel number is matched by the prefix.
	 */
	public Boolean isCompleteMatch();
	
	/**
	 * Checks if an additional prefix char chould reduce the number of matching channels.
	 * @return
	 * 		{@code true} if an additional prefix would reduce the number of matching channels.
	 */
	public Boolean isExtraCharUseful();
	
	/**
	 * Gets the first matching spacer, if a spacer whould be needed for the prefix to match a channel.
	 * @return
	 * 		the first matching spacer.
	 */
	public String getNeededSpacer();
}
