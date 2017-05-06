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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_28;

import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.impl.RecorderChannelInfo;

/**
 * An interface to get informations about a MythTV recorder channel.
 * <p>
 * This interface represents the response to an {@link IRecorder#getChannelInfo(Integer)} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecorderChannelInfo.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * {@mythResponseExample
 * 		Example 1:
 * 		<pre><0>CHANNEL_ID: 11123 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: PULS 4 | <3>CHANNEL_NUMBER: 10123 | <4>CHANNEL_NAME: PULS 4 | <5>XMLTV_ID:</pre>
 * 		Example 2:
 * 		<pre><0>CHANNEL_ID: 1079 | <1>SOURCE_ID: 1 | <2>CHANNEL_SIGN: VOX | <3>CHANNEL_NUMBER: 8 | <4>CHANNEL_NAME: VOX | <5>XMLTV_ID: CNI0D8E</pre>  
 * }
 * 
 * @since {@mythProtoVersion 28}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
public interface IRecorderChannelInfo extends IBasicChannelInfo, IPropertyAware<IRecorderChannelInfo.Props>, IVersionable {
	/**
	 * Properties of an {@link IRecorderChannelInfo} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_28)
	public static enum Props {
		/**
		 * [01] The unique id of the channel ({@link String}): e.g. "1000".<br/>
		 * @see IRecorderChannelInfo#getChannelID()
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(table="channel",column="chanid")
		CHANNEL_ID,
		
		/**
		 * [02]
		 * @see IRecorderChannelInfo#getSourceID()
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(table="channel",column="sourceid")
		SOURCE_ID,
		
		/**
		 * [03] The sign of the channel ({@link String}): e.g. "ORF1"<br/>
		 * @see IRecorderChannelInfo#getChannelSign()
		 */
		@MythDatabaseColumn(table="channel",column="callsign")
		CHANNEL_SIGN,
		
		/**
		 * [04] The number of the channel ({@link String}): e.g. "2"<br/>
		 * @see IRecorderChannelInfo#getChannelNumber()
		 */
		@MythDatabaseColumn(table="channel",column="channum")
		CHANNEL_NUMBER,
		
		/**
		 * [05] The name of the channel ({@link String}): e.g. "ORF 1"<br/>
		 * @see IRecorderChannelInfo#getChannelName()
		 */
		@MythDatabaseColumn(table="channel",column="name")
		CHANNEL_NAME,
		
		/**
		 * [06]
		 * @see IRecorderChannelInfo#getXmlTvID()
		 */
		@MythDatabaseColumn(table="channel",column="xmltvid")
		XMLTV_ID
	}

	/* ======================================================================
	 * IBasicChannelInfo Methods
	 * ====================================================================== */	
	
	/**
	 * @see RecorderChannelInfo.Props#CHANNEL_ID
	 * @see IBasicChannelInfo#getChannelID()
	 */
	public abstract Integer getChannelID();

	/**
	 * @see IRecorderChannelInfo.Props#CHANNEL_SIGN
	 * @see IBasicChannelInfo#getChannelSign()
	 */
	public abstract String getChannelSign();

	/**
	 * Gets the user defined channel number.
	 * 
	 * @return
	 * 		the user defined number. 
	 * 		This could contain '{@code -}', '{@code _}' or '{@code .}'
	 *   
	 * @see IRecorderChannelInfo.Props#CHANNEL_NUMBER
	 */
	public abstract String getChannelNumber();	
	
	/* ======================================================================
	 * Additional Methods
	 * ====================================================================== */		
	
	/**
	 * Gets the channel name.
	 * @return
	 * 		the channel name
	 * 
	 * @see IRecorderChannelInfo.Props#CHANNEL_NAME
	 */
	public abstract String getChannelName();

	/**
	 * Gets the source id.
	 * @return
	 * 		the source id
	 * 
	 * @see IRecorderChannelInfo.Props#SOURCE_ID
	 */
	public abstract Integer getSourceID();

	/**
	 * Gets the XML-TV id.
	 * 
	 * @return
	 * 		the xml tv id
	 * @see IRecorderChannelInfo.Props#XMLTV_ID
	 */
	public abstract String getXmlTvID();

}