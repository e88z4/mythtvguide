package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_75;
import static org.jmythapi.protocol.events.IPixmapGenerated.Props.PIXMAP_LAST_MODIFIED;
import static org.jmythapi.protocol.events.IPixmapGenerated.Props.STATUS_CODE;
import static org.jmythapi.protocol.events.IPixmapGenerated.Props.STATUS_MESSAGE;
import static org.jmythapi.protocol.events.IPixmapGenerated.Props.TOKEN;
import static org.jmythapi.protocol.events.IPixmapGenerated.Props.UNIQUE_RECORDING_ID;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.events.IPixmapGenerated;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IPixmap;
import org.jmythapi.protocol.response.impl.Pixmap;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.utils.EncodingUtils;

public class PixmapGenerated extends AMythEvent<IPixmapGenerated.Props> implements IPixmapGenerated {
	
	public PixmapGenerated(ProtocolVersion protoVersion, List<String> eventArguments) {
		super(protoVersion,Props.class,IMythCommand.BACKEND_MESSAGE_GENERATED_PIXMAP,eventArguments);
	}
	
	public String getPreviewImageName() {
		final String uid = this.getPropertyValueObject(UNIQUE_RECORDING_ID);
		final Object[] idParts = EncodingUtils.splitId(uid,false);		
		return String.format(
			"%s.mpg.png",
			EncodingUtils.generateId((Integer)idParts[0],(Date)idParts[1])
		);
	}

	public String getUniqueRecordingID() {
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		if(!isUTC) {
			return this.getPropertyValueObject(UNIQUE_RECORDING_ID);
		} else {
			return EncodingUtils.generateId(getChannelID(),getRecordingStartTime());
		}
	}
	

	public Integer getChannelID() {
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		
		final String uid = this.getPropertyValueObject(UNIQUE_RECORDING_ID);
		final Object[] idParts = EncodingUtils.splitId(uid,isUTC);
		if(idParts == null) return null;
		return (Integer) idParts[0];
	}
	
	public Date getRecordingStartTime() {
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		
		final String uid = this.getPropertyValueObject(UNIQUE_RECORDING_ID);
		final Object[] idParts = EncodingUtils.splitId(uid,isUTC);
		if(idParts == null) return null;
		return (Date) idParts[1];
	}

	public IPixmap getPixmap() {
		final String status = this.getStatusCode();
		if(!status.equalsIgnoreCase("OK")) return null;
		
		// determine the pixmap properties 
		final int startIdx = EnumUtils.getEnumPosition(PIXMAP_LAST_MODIFIED,this.protoVersion);
		final int length = EnumUtils.getEnumLength(IPixmap.Props.class,this.protoVersion);
		
		// read the data
		final List<String> pixmapProps = this.respArgs.subList(startIdx, startIdx+length);
		return new Pixmap(this.protoVersion,pixmapProps);
	}

	public String getStatusCode() {
		return this.getPropertyValueObject(STATUS_CODE);
	}

	public String getStatusMessage() {
		return this.getPropertyValueObject(STATUS_MESSAGE);
	}

	public String getToken() {
		return this.getPropertyValueObject(TOKEN);
	}
	
	public static PixmapGenerated valueOf(IMythPacket eventPacket) {
		final ProtocolVersion protoVersion = eventPacket.getVersionNr();
		
		final List<String> arguments = AMythEvent.extractArgumentsList(eventPacket);
		final int statusIdx = EnumUtils.getEnumPosition(Props.STATUS_CODE, protoVersion);
		final String status = arguments.get(statusIdx);
		
		if(status.equals("ERROR")) {
			// determine the amount of pixmap properties
			final int pixmapPropsIdx = EnumUtils.getEnumPosition(PIXMAP_LAST_MODIFIED,protoVersion);
			final int pixmapPropsCount = EnumUtils.getEnumLength(IPixmap.Props.class,protoVersion);
			
			// create dummy values for the missing pixmap
			final String[] pixmapArgs = new String[pixmapPropsCount];
			Arrays.fill(pixmapArgs,null);
			
			arguments.addAll(pixmapPropsIdx,Arrays.asList(pixmapArgs));
		}
		
		return new PixmapGenerated(protoVersion, arguments);
	}
}
