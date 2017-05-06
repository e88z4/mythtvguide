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
package org.jmythapi.protocol.response.impl;

import static org.jmythapi.protocol.response.IPixmap.Props.DATA_BASE64;
import static org.jmythapi.protocol.response.IPixmap.Props.DATA_CHECKSUM;
import static org.jmythapi.protocol.response.IPixmap.Props.DATA_SIZE;
import static org.jmythapi.protocol.response.IPixmap.Props.LAST_MODIFIED;
import static org.jmythapi.protocol.utils.EnumUtils.getEnumLength;
import static org.jmythapi.protocol.utils.EnumUtils.getEnumPosition;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IFileTransfer;
import org.jmythapi.protocol.response.IPixmap;
import org.jmythapi.utils.Base64Coder;
import org.jmythapi.utils.EncodingUtils;

public class Pixmap extends AMythResponse<IPixmap.Props> implements IPixmap {
	
	public Pixmap(IMythPacket packet) {
		super(Props.class, packet);
	}
	
	public Pixmap(ProtocolVersion protoVersion, String... responseArgs) {
		super(protoVersion,Props.class,responseArgs);
	}
	
	public Pixmap(ProtocolVersion protoVersion, List<String> responseArgs) {
		super(protoVersion,Props.class,responseArgs);
	}
	
	public Date getLastModified() {
		return this.getPropertyValueObject(LAST_MODIFIED);
	}
	
	public Long getDataSize() {
		return this.getPropertyValueObject(DATA_SIZE);
	}
	
	public String getDataCheckSum() {
		return this.getPropertyValueObject(DATA_CHECKSUM);
	}
	
	public String getDataBase64() {
		return this.getPropertyValueObject(Props.DATA_BASE64);
	}
	
	public byte[] getData() {
		final String base64 = this.getDataBase64();
		if(base64 == null) return null;
		
		final byte[] data = Base64Coder.decode(base64);
		return data;
	}
	
	public InputStream getDataStream() {
		final byte[] data = this.getData();
		if(data == null) return null;
		return new ByteArrayInputStream(data);
	}
	
	public BufferedImage getImage() throws IOException {
		InputStream in = null;
		try {
			in = this.getDataStream();
			if(in == null) return null;
			
			return ImageIO.read(in);
		} finally {
			if(in != null) in.close();
		}

	}
	
	public static Pixmap valueOf(IMythPacket packet) {
		// if only the last-mod timestamp is available
		if(packet.getPacketArgsLength() == 1) {
			// getting the version number
			final ProtocolVersion protoVersion = packet.getVersionNr();
			
			// build the arguments list
			final String[] args = new String[getEnumLength(Props.class, protoVersion)];
			Arrays.fill(args,null);
			args[getEnumPosition(LAST_MODIFIED,protoVersion)] = packet.getPacketArg(0);
					
			// create the object
			return new Pixmap(protoVersion,args);
		}
		
		return new Pixmap(packet);
	}
	
	public static Pixmap valueOf(ProtocolVersion protoVersion, Date lastModifiedDate) {
		final String unixTimeStamp = EncodingUtils.formatDateTimeToUnixTimestamp(lastModifiedDate);	
		
		// build the arguments list
		final String[] args = new String[getEnumLength(Props.class, protoVersion)];
		Arrays.fill(args,null);
		args[getEnumPosition(LAST_MODIFIED,protoVersion)] = unixTimeStamp;		
		
		// create the object
		return new Pixmap(protoVersion,args);
	}
	
	/**
	 * This method creates a new pixmap object.
	 * 
	 * @param fileTransfer
	 * 		the file-transfer object that is used to download the data
	 * @param lastModifiedDate
	 * 		the last modified date of the pixmap, if any.
	 * @return
	 * 		the pixmap object
	 * @throws IOException
	 * 		on communication errors.
	 */
	public static Pixmap valueOf(IFileTransfer fileTransfer, Date lastModifiedDate) throws IOException {
		final ProtocolVersion protoVersion = fileTransfer.getVersionNr();
		final String fileDate = lastModifiedDate==null?null:EncodingUtils.formatDateTimeToUnixTimestamp(lastModifiedDate);	
		final String fileSize = Long.toString(fileTransfer.getFileSize());
		
		// read the pixmap data
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		fileTransfer.transferTo(bout);
		bout.close();
		fileTransfer.close();
		final String base64data = String.valueOf(Base64Coder.encode(bout.toByteArray()));
		
		// build the arguments list
		final String[] args = new String[getEnumLength(Props.class, protoVersion)];
		Arrays.fill(args,null);
		args[getEnumPosition(LAST_MODIFIED,protoVersion)] = fileDate;
		args[getEnumPosition(DATA_SIZE,protoVersion)] = fileSize;
		args[getEnumPosition(DATA_BASE64,protoVersion)] = base64data;
				
		// create the object
		return new Pixmap(protoVersion,args);		
	}
}
