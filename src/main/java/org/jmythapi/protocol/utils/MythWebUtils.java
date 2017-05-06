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
package org.jmythapi.protocol.utils;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_23;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramInfo;
import org.jmythapi.protocol.response.IProgramRecordingStatus;
import org.jmythapi.protocol.response.IProgramRecordingStatus.Status;

public class MythWebUtils {
	public static URL getProgramStreamUrl(URL mythWebBaseUrl, IProgramInfo recording) throws MalformedURLException {
		if(mythWebBaseUrl == null) {
			mythWebBaseUrl = new URL(String.format(
				"http://%s/mythweb/",
				recording.getHostName()
			));
		}		
		
		// stream is only available for recording or recorded files
		final IProgramRecordingStatus status = recording.getRecordingStatus();
		if(status == null || !status.hasStatus(Status.RECORDED,Status.RECORDING)) {
			return null;
		}
				
		
		if(recording.getVersionNr().compareTo(PROTO_VERSION_23)>=0) {
			return new URL(
				mythWebBaseUrl,
				String.format(
					"pl/stream/%d/%d",
					recording.getChannelID(),
					recording.getRecordingStartTime().getTime() / 1000
				)			
			);	
		} else {
			return new URL(
				mythWebBaseUrl,
				String.format(
					"video_dir/%s",
					recording.getBaseName()
				)			
			);
		}		
	}
	
	public static URL getProgramDetailUrl(IProgramInfo recording) throws MalformedURLException {
		return getProgramDetailUrl(null,recording);
	}
	
	public static URL getProgramDetailUrl(URL mythWebBaseUrl, IProgramInfo recording) throws MalformedURLException {
		if(mythWebBaseUrl == null) {
			mythWebBaseUrl = new URL(String.format(
				"http://%s/mythweb/",
				recording.getHostName()
			));
		}
		
		Date recordingDate = null;
		final IProgramRecordingStatus status = recording.getRecordingStatus();
		if(status != null && status.hasStatus(Status.RECORDED,Status.RECORDING)) {
			recordingDate = recording.getRecordingStartTime();
		} else {
			recordingDate = recording.getStartDateTime();
		}
		
		if(recording.getVersionNr().compareTo(PROTO_VERSION_23)>=0) {
			return new URL(
				mythWebBaseUrl,
				String.format(
					"tv/detail/%d/%d",
					recording.getChannelID(),
					recordingDate.getTime() / 1000
				)			
			);	
		} else {
			return new URL(
				mythWebBaseUrl,
				String.format(
					"program_detail.php?chanid=%d&starttime=%d",
					recording.getChannelID(),
					recordingDate.getTime() / 1000
				)			
			);				
		}
	}
	
	public static URL getProgramListUrl(ProtocolVersion protoVersion, URL mythWebBaseUrl, Integer channelInfo, Date startDate) throws MalformedURLException {
		if(startDate == null) startDate = new Date();
		
		if(protoVersion.compareTo(PROTO_VERSION_23)>=0) {
			// return the list of all programs
			if(channelInfo == null) {
				return new URL(
					mythWebBaseUrl,
					String.format(
						"tv/list?time=%d",
						startDate.getTime() / 1000
					)			
				);	
			} 
			
			// return the list only for the given channel
			return new URL(
				mythWebBaseUrl,
				String.format(
					"tv/channel/%d/%d",
					channelInfo,
					startDate.getTime() / 1000
				)			
			);	
		} else {
			// return the list of all programs
			if(channelInfo == null) {
				return new URL(
					mythWebBaseUrl,
					String.format(
						"channel_detail.php?time=%d",
						startDate.getTime() / 1000
					)			
				);	
			} 				
			
			// return the list only for the given channel
			return new URL(
				mythWebBaseUrl,
				String.format(
					"channel_detail.php?chanid=%d&time=%d",
					channelInfo,
					startDate.getTime() / 1000
				)			
			);	
		}		
	}
}
