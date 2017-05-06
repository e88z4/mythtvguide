package org.jmythapi.protocol.events.impl;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IVideoListNoChange;

public class VideoListNoChange extends VideoList<IVideoListNoChange.Props> implements IVideoListNoChange {

	public VideoListNoChange(IMythPacket mythPacket) {
		super(Props.class,mythPacket);
	}
}
