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
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_08;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_10;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_13;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_21;

import java.util.Date;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IPropertyAware;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the program of the current channel of a MythTV recorder.
 * <p>
 * This interface represents the response to a {@link IRecorder#getProgramInfo() IRecorder.getProgramInfo} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IRecorderProgramInfo.Props properties-list} for all properties of this interface.
 * 
 * @see IRecorder#getProgramInfo()
 * @see IMythCommand#QUERY_RECORDER_GET_PROGRAM_INFO QUERY_RECORDER_GET_PROGRAM_INFO
 * 
 * @deprecated {@mythProtoVersion 21}, replaced by {@link IProgramInfo} and {@link IRecorderChannelInfo}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_21)
public interface IRecorderProgramInfo extends IBasicProgramInfo, IBasicChannelInfo, IVersionable, IPropertyAware<IRecorderProgramInfo.Props> {

	/**
	 * The properties of an {@link IRecorderProgramInfo} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_21)
	public static enum Props {
		/**
		 * The program title.
		 * 
		 * @see IRecorderProgramInfo#getTitle()
		 */
		TITLE,
		
		/**
		 * The program sub-title.
		 * @see IRecorderProgramInfo#getSubtitle()
		 */
		SUBTITLE,
		
		/**
		 * A description about the program.
		 * @see IRecorderProgramInfo#getDescription()
		 */
		DESCRIPTION,
		
		/**
		 * The program category.
		 * <p>
		 * e.g. {@code comedy}
		 * 
		 * @see IRecorderProgramInfo#getCategory()
		 */
		CATEGORY,
		
		/**
		 * The scheduled start of the program.
		 * 
		 * @see IRecorderProgramInfo#getStartDateTime()
		 */
		@MythParameterType(Date.class)
		START_DATE_TIME,
		
		/**
		 * The scheduled end of the program.
		 * 
		 * @see IRecorderProgramInfo#getEndDateTime()
		 */
		@MythParameterType(Date.class)
		END_DATE_TIME,
		
		/**
		 * The channel abbreviation.
		 * <p>
		 * E.g. {@code ORF1}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_SIGN}.
		 * 
		 * @see IRecorderProgramInfo#getChannelSign()
		 */
		CHANNEL_SIGN,		
		
		/**
		 * The channel icon path.
		 * <p>
		 * 
		 * @see IRecorderProgramInfo#getChannelIconPath()
		 */
		CHANNEL_ICON_PATH,
		
		/**
		 * The channel number.
		 * <p>
		 * The user defined number of the channel: e.g. {@code 2}
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_NUMBER}.
		 * 
		 * @see IRecorderProgramInfo#getChannelNumber()
		 */
		CHANNEL_NUMBER,
		
		/**
		 * The channel ID.
		 * <p>
		 * The unique id of the channel: e.g. {@code 1001}.
		 * <br>
		 * This is similar to {@link IRecorderChannelInfo.Props#CHANNEL_ID}.
		 * 
		 * @see IRecorderProgramInfo#getChannelID()
		 */
		@MythParameterType(Integer.class)
		CHANNEL_ID,
		
		/**
	     * Series ID.
	     * <p>
	     * Is used for dup checks.
	     * 
		 * @see IRecorderProgramInfo#getSeriesID()
		 * @since {@mythProtoVersion 08}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_08)
		SERIES_ID,
		
		/**
		 * Program ID.
	     * <p>
	     * Is used for dup checks.
	     * 
		 * @see IRecorderProgramInfo#getProgramID()
		 * @since {@mythProtoVersion 08}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_08)
		PROGRAM_ID,
		
		/**
		 * Output filters.
	     * <p>
	     * Allows to specify a set of filters to be used when playing back recordings 
	     * from a given channel. This is useful for those with hardware encoders and 
	     * more than one source, since filters are rarely a one-size-fits-all thing.
	     * 
		 * @see IRecorderProgramInfo#getChannelOutputFilters()
		 * @since {@mythProtoVersion 10}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_10)
		CHANNEL_OUTPUT_FILTERS,
		
		/**
		 * Repeated program.
	     * <p>
	     * Specifies if this is a duplicate to a previously recorded program.
	     * 
		 * @see IRecorderProgramInfo#isRepeat()
		 * @since {@mythProtoVersion 13}
		 */
		@MythParameterType(Boolean.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_13)
		REPEAT,
		
		/**
		 * The original airdate.
		 * 
		 * @see IRecorderProgramInfo#getOritinalAirDate()
		 * 
		 * @since {@mythProtoVersion 13}
		 */
		@MythParameterType(Date.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_13)
		ORIGINAL_AIRDATE,
		
		/**
		 * Rating.
		 * 
		 * @see IRecorderProgramInfo#getStars()
		 * @since {@mythProtoVersion 13}
		 */
		@MythParameterType(Float.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_13)
		STARS
	}

	/* ===============================================
	 * IBasicProgramInfo methods
	 * =============================================== */	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#TITLE
	 */
	public abstract String getTitle();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#SUBTITLE
	 */
	public abstract String getSubtitle();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#DESCRIPTION
	 */
	public abstract String getDescription();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#CATEGORY
	 */
	public abstract String getCategory();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#START_DATE_TIME
	 */
	public abstract Date getStartDateTime();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#END_DATE_TIME
	 */
	public abstract Date getEndDateTime();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#SERIES_ID
	 */
	public abstract String getSeriesID();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#PROGRAM_ID
	 */
	public abstract String getProgramID();

	/* ===============================================
	 * IBasicChannelInfo methods
	 * =============================================== */
	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#CHANNEL_SIGN
	 */
	public abstract String getChannelSign();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#CHANNEL_NUMBER
	 */
	public abstract String getChannelNumber();

	/**
	 * {@inheritDoc}
	 * 
	 * @see IRecorderProgramInfo.Props#CHANNEL_ID
	 */
	public abstract Integer getChannelID();	
	
	/* ===============================================
	 * Additional methods
	 * =============================================== */
	
	/**
	 * Gets the path to the channel icon.
	 * 
	 * @return
	 * 		the channel icon path
	 * 
	 * @see IRecorderProgramInfo.Props#CHANNEL_ICON_PATH
	 */
	public abstract String getChannelIconPath();	
	
	/**
	 * Output filters.
	 * <p>
	 * Allows to specify a set of filters to be used when playing back recordings 
	 * from a given channel. This is useful for those with hardware encoders and 
	 * more than one source, since filters are rarely a one-size-fits-all thing.
	 * 
	 * @return
	 * 	the channle output filters
	 * 
	 * @see IRecorderProgramInfo.Props#CHANNEL_OUTPUT_FILTERS		
	 */
	public abstract String getChannelOutputFilters();

	/**
	 * Checks if the program is a duplicate to a previously recorded program.
	 * 
	 * @return
	 * 		if the program is a duplicate
	 * 
	 * @see IRecorderProgramInfo.Props#REPEAT
	 */
	public abstract Boolean isRepeat();

	/**
	 * Gets the air date of the program.
	 * @return
	 * 		the air date
	 * 
	 * @see IRecorderProgramInfo.Props#ORIGINAL_AIRDATE
	 */
	public abstract Date getOritinalAirDate();

	/**
	 * Gets the rating of the program.
	 * @return
	 * 		the rating
	 * 
	 * @see IRecorderProgramInfo.Props#STARS
	 */
	public abstract Float getStars();

}