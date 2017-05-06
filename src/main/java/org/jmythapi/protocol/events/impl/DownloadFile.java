package org.jmythapi.protocol.events.impl;

import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.UnsupportedCommandException;

public class DownloadFile {	
	public static AMythEvent<?> valueOf(IMythPacket eventPacket) throws UnsupportedCommandException {
		final List<String> arguments = AMythEvent.extractArgumentsList(eventPacket);
		
		// a event packet with sub-type
		final String subEventType = arguments.isEmpty()?null:arguments.remove(0);
		if (subEventType != null && "UPDATE".equals(subEventType)) {
			return new DownloadFileUpdate(eventPacket.getVersionNr(),arguments);
		} else if (subEventType != null && "FINISHED".equals(subEventType)) {
			return new DownloadFileFinished(eventPacket.getVersionNr(),arguments);
		}
		
		throw new UnsupportedCommandException("Unknown event type " + subEventType);
	}
}
