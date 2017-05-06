package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_75;

import java.util.Date;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.IFileWritten;
import org.jmythapi.utils.EncodingUtils;

public class FileWritten extends AMythEvent<IFileWritten.Props> implements IFileWritten {
	public FileWritten(IMythPacket packet) {
		super(Props.class, packet);
	}

	public String getFilePath() {
		return this.getPropertyValueObject(Props.FILE_PATH);
	}
	
	public String getFileBaseName() {
		final String pathName = this.getFilePath();
		if(pathName == null) return null;
		
		int idx = pathName.lastIndexOf('/');
		final String baseName = (idx == -1) ? pathName : pathName.substring(idx+1);
		return baseName;
	}

	public Long getFileSize() {
		return this.getPropertyValueObject(Props.FILE_SIZE);
	}	
	
	public Integer getChannelID() {
		final String uniqueId = getUniqueRecordingID();
		if(uniqueId == null) return null;
		
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		final Object[] idParts = EncodingUtils.splitId(uniqueId, isUTC);
		if(idParts == null || idParts.length != 2) return null;
		
		return (Integer) idParts[0];
	}

	public Date getRecordingStartTime() {
		final String uniqueId = getUniqueRecordingID();
		if(uniqueId == null) return null;
		
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		final Object[] idParts = EncodingUtils.splitId(uniqueId, isUTC);
		if(idParts == null || idParts.length != 2) return null;
		
		return (Date) idParts[1];
	}

	public String getUniqueRecordingID() {
		final String fileBaseName = getFileBaseName();
		if(fileBaseName == null) return null;
		
		final int idx = fileBaseName.lastIndexOf(".");
		if(idx == -1) return null;
		
		final String uniqueId = fileBaseName.substring(0,idx-1);
		return uniqueId;
	}

}
