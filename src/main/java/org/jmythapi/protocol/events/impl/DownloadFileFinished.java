package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.events.IDownloadFileFinished.Props.BYTES_TOTAL;
import static org.jmythapi.protocol.events.IDownloadFileFinished.Props.ERROR_CODE;
import static org.jmythapi.protocol.events.IDownloadFileFinished.Props.ERROR_TEXT;
import static org.jmythapi.protocol.events.IDownloadFileFinished.Props.LOCAL_URI;
import static org.jmythapi.protocol.events.IDownloadFileFinished.Props.REMOTE_URI;

import java.net.URI;
import java.util.List;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.events.IDownloadFileFinished;
import org.jmythapi.protocol.request.IMythCommand;

public class DownloadFileFinished extends AMythEvent<IDownloadFileFinished.Props> implements IDownloadFileFinished {

	public DownloadFileFinished(ProtocolVersion protoVersion, List<String> eventArguments) {
		super(protoVersion,Props.class, IMythCommand.BACKEND_MESSAGE_DOWNLOAD_FILE + " FINISHED",eventArguments);
	}

	public Long getBytesTotal() {
		return this.getPropertyValueObject(BYTES_TOTAL);
	}
	
	public Float getReceivedPercent() {
		return new Float(100);
	}

	public URI getLocalURI() {
		return this.getPropertyValueObject(LOCAL_URI);
	}

	public URI getRemoteURI() {
		return this.getPropertyValueObject(REMOTE_URI);
	}

	public Integer getErrorCode() {
		return this.getPropertyValueObject(ERROR_CODE);
	}

	public String getErrorText() {
		return this.getPropertyValueObject(ERROR_TEXT);
	}
	
}
