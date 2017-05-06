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

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get informations about a locked MythTV recorder.
 * <p>
 * This interface represents the response to a {@link IBackend#lockTuner} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link ITunerInfo.Props properties-list} for all properties of this interface.
 * <p>
 * 
 * @see IBackend#lockTuner()
 * @see IMythCommand#LOCK_TUNER LOCK_TUNER
 */
public interface ITunerInfo extends IVersionable, IPropertyAware<ITunerInfo.Props> {
	/**
	 * Properties of the {@link ITunerInfo} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * Card ID.
		 * <p>
		 * The card ID of the tuner.
		 * 
		 * @see ITunerInfo#getCardID()
		 */
		@MythParameterType(Integer.class)
		CARD_ID,
		
		/**
		 * Video Device.
		 * 
		 * @see ITunerInfo#getVideoDevice()
		 */
		VIDEO_DEVICE,
		
		/**
		 * Audio Device.
		 * 
		 * @see ITunerInfo#getAudioDevice()
		 */
		AUDIO_DEVICE,
		
		/**
		 * VBI Device.
		 * 
		 * @see ITunerInfo#getVbiDevice()
		 */
		VBI_DEVICE
	}	
	
	/**
	 * Gets the card ID of the locked tuner.
	 * @return
	 * 		the card ID.
	 * 
	 * @see ITunerInfo.Props#CARD_ID
	 */
	public Integer getCardID();
	
	/**
	 * Gets the video device of the locked tuner.
	 * 
	 * @return
	 * 		the video device.
	 * 
	 * @see ITunerInfo.Props#VIDEO_DEVICE
	 */
	public String getVideoDevice();
	
	/**
	 * Gets the audio device of the locked tuner.
	 * 
	 * @return
	 * 		the audio device
	 * 
	 * @see ITunerInfo.Props#AUDIO_DEVICE
	 */
	public String getAudioDevice();
	
	/**
	 * Gets the vdi device of the locked tuner.
	 * 
	 * @return
	 * 		the vbi device
	 * 
	 * @see ITunerInfo.Props#VBI_DEVICE
	 */
	public String getVbiDevice();
}
