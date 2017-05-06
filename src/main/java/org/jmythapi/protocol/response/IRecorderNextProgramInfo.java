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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1037;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_08;

import java.util.Date;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IPropertyAware;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the next program of a MythTV recorder channel.
 * <p>
 * This interface represents the response to a {@link IRecorder#getNextProgramInfo IRecorder.getNextProgramInfo(...)} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecorderNextProgramInfo.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><00>TITLE: Formel 1 | <01>SUBTITLE:   | <02>DESCRIPTION: Countdown Grosser Preis von Malaysia. mit Christian Danner, Heiko Wasser | <03>CATEGORY:   | <04>START_DATE_TIME: Sun Apr 10 08:45:00 CEST 2011 | <05>END_DATE_TIME: Sun Apr 10 10:00:00 CEST 2011 | <06>CHANNEL_SIGN: RTL | <07>CHANNEL_ICON_PATH: /home/theli/.mythtv/channels/rtl.jpg | <08>CHANNEL_NUMBER: 3 | <09>CHANNEL_ID: 1066 | <10>SERIES_ID: 110344929 | <11>PROGRAM_ID: </pre>  
 * }
 *
 * @see IBackend#getNextProgramInfos(Date)
 * @see IRecorder#getNextProgramInfos(Date)
 * @see IMythCommand#QUERY_RECORDER_GET_NEXT_PROGRAM_INFO QUERY_RECORDER_GET_NEXT_PROGRAM_INFO
 */
public interface IRecorderNextProgramInfo extends IBasicChannelInfo, IBasicProgramInfo, IVersionable, IPropertyAware<IRecorderNextProgramInfo.Props> {

	/**
	 * The properties of an {@link IRecorderNextProgramInfo} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * The program title.
		 * 
		 * @see IRecorderNextProgramInfo#getTitle()
		 */
		@MythDatabaseColumn(column="title")
		TITLE,
		
		/**
		 * The program subtitle.
		 * 
		 * @see IRecorderNextProgramInfo#getSubtitle()
		 */
		@MythDatabaseColumn(column="subtitle")		
		SUBTITLE,
		
		/**
		 * A description about the program.
		 * 
		 * @see IRecorderNextProgramInfo#getDescription()
		 */
		@MythDatabaseColumn(column="description")		
		DESCRIPTION,
		
		/**
		 * The program category.
		 * <p>
		 * E.g. {@code comedy}
		 * 
		 * @see IRecorderNextProgramInfo#getCategory()
		 */
		@MythDatabaseColumn(column="category")		
		CATEGORY,
		
		/**
		 * The scheduled start of the program.
		 * 
		 * @see IRecorderNextProgramInfo#getStartDateTime()
		 */
		@MythParameterType(Date.class)
		@MythDatabaseColumn(column="starttime")
		START_DATE_TIME,
		
		/**
		 * The scheduled end of the program.
		 * 
		 * @see IRecorderNextProgramInfo#getEndDateTime()
		 */
		@MythParameterType(Date.class)
		@MythDatabaseColumn(column="endtime")
		END_DATE_TIME,
		
		/**
		 * The channel abbreviation.
		 * <p>
		 * E.g. {@code ORF1}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_SIGN}.
		 * 
		 * @see IRecorderNextProgramInfo#getChannelSign()
		 */
		@MythDatabaseColumn(column="callsign")		
		CHANNEL_SIGN,
		
		/**
		 * The channel icon path.
		 * <p>
		 * 
		 * @see IRecorderNextProgramInfo#getChannelIconPath()
		 */
		@MythDatabaseColumn(column="icon")
		CHANNEL_ICON_PATH,
		
		/**
		 * The channel number.
		 * <p>
		 * The user defined number of the channel: e.g. {@code 2}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_NUMBER}.
		 * 
		 * @see IRecorderNextProgramInfo#getChannelNumber()
		 */
		@MythDatabaseColumn(column="channum")		
		CHANNEL_NUMBER,
		
		/**
		 * The channel ID.
		 * <p>
		 * The unique id of the channel: e.g. {@code 1001}.
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_ID}.
		 * 
		 * @see IRecorderNextProgramInfo#getChannelID()
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(column="chanid")		
		CHANNEL_ID,
		
		/**
	     * Series ID.
	     * <p>
	     * Is used for dup checks.
		 * 
		 * @since {@mythProtoVersion 08}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_08)
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1037)
	    @MythDatabaseColumn(column="seriesid")		
		SERIES_ID,
		
		/**
		 * Program ID.
	     * <p>
	     * Is used for dup checks.
		 * 
		 * @since {@mythProtoVersion 08}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_08)
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1037)
	    @MythDatabaseColumn(column="programid")		
		PROGRAM_ID
	}

	/* ======================================================================
	 * IBasicProgramInfo Methods
	 * ====================================================================== */		
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#TITLE
	 */
	public abstract String getTitle();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#SUBTITLE
	 */
	public abstract String getSubtitle();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#DESCRIPTION
	 */
	public abstract String getDescription();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#CATEGORY
	 */
	public abstract String getCategory();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#START_DATE_TIME
	 */
	public abstract Date getStartDateTime();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#END_DATE_TIME
	 */
	public abstract Date getEndDateTime();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#SERIES_ID
	 */
	public abstract String getSeriesID();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#PROGRAM_ID
	 */
	public abstract String getProgramID();

	/* ======================================================================
	 * IBasicChannelInfo Methods
	 * ====================================================================== */	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#CHANNEL_ID
	 */
	public abstract Integer getChannelID();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#CHANNEL_SIGN
	 */
	public abstract String getChannelSign();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderNextProgramInfo.Props#CHANNEL_NUMBER
	 */
	public abstract String getChannelNumber();

	/* ======================================================================
	 * Additional Methods
	 * ====================================================================== */
	/**
	 * Gets the path to the channel icon.
	 * 
	 * @see IRecorderNextProgramInfo.Props#CHANNEL_ICON_PATH
	 */
	public abstract String getChannelIconPath();

}