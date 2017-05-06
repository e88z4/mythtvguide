package org.jmythapi.protocol.events.impl;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IVideoList;

public class VideoList <E extends Enum<E>> extends AMythEvent<E> implements IVideoList<E> {	
	
	public VideoList(Class<E> propsClass, IMythPacket packet) {
		super(propsClass, packet);
	}

	public int getChangeCount() {
		return this.respArgs.size();
	}

	public boolean hasChanges() {
		return getChangeCount() > 0;
	}
}
