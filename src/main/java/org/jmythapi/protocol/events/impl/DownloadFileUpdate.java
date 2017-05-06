package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.events.IDownloadFileUpdate.Props.BYTES_RECEIVED;
import static org.jmythapi.protocol.events.IDownloadFileUpdate.Props.BYTES_TOTAL;
import static org.jmythapi.protocol.events.IDownloadFileUpdate.Props.LOCAL_URI;
import static org.jmythapi.protocol.events.IDownloadFileUpdate.Props.REMOTE_URI;

import java.net.URI;
import java.util.List;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.events.IDownloadFileUpdate;
import org.jmythapi.protocol.request.IMythCommand;

public class DownloadFileUpdate extends AMythEvent<IDownloadFileUpdate.Props> implements IDownloadFileUpdate {

	public DownloadFileUpdate(ProtocolVersion protoVersion, List<String> eventArguments) {
		super(protoVersion,Props.class, IMythCommand.BACKEND_MESSAGE_DOWNLOAD_FILE + " UPDATE",eventArguments);
	}

	public Long getBytesReceived() {
		return this.getPropertyValueObject(BYTES_RECEIVED);
	}

	public Long getBytesTotal() {
		return this.getPropertyValueObject(BYTES_TOTAL);
	}
	
	public Float getReceivedPercent() {
		final Long received = this.getBytesReceived();
		if(received == null) return null;
		
		final Long total = this.getBytesTotal();
		if(total == null) return null;
		
		return Float.valueOf((received.floatValue() / total.floatValue()) * 100f);
	}

	public URI getLocalURI() {
		return this.getPropertyValueObject(LOCAL_URI);
	}

	public URI getRemoteURI() {
		return this.getPropertyValueObject(REMOTE_URI);
	}
	
}
