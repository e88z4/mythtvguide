package org.jmythapi.protocol.events.impl;

import java.util.ArrayList;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IVideoListChange;

public class VideoListChange extends VideoList<IVideoListChange.Props> implements IVideoListChange {

	public VideoListChange(IMythPacket mythPacket) {
		super(Props.class,mythPacket);
	}
	
	@Override
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		// the event contains a variable list or values
	}

	public List<IVideoChange> getChanges() {
		final List<IVideoChange> changes = new ArrayList<IVideoChange>();
		
		for(String changeString : this.respArgs) {
			final IVideoChange change = VideoChange.valueOf(changeString);
			if(change != null) changes.add(change);
		}
		
		return changes;
	}
	
	public static class VideoChange implements IVideoChange {
		private ChangeType type;
		private Integer id;
		
		public VideoChange(ChangeType type, Integer id) {
			this.type = type;
			this.id = id;
		}

		public Integer getId() {
			return this.id;
		}

		public ChangeType getType() {
			return this.type;
		}
		
		public static VideoChange valueOf(String changeStr) {
			if(changeStr == null) return null;
			
			final String[] parts = changeStr.split("::");
			return new VideoChange(
				ChangeType.valueOf(parts[0]),
				Integer.valueOf(parts[1])
			);
		}
	}
}
