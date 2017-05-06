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
package org.jmythapi.protocol.response;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_49;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to read the preview image of a recording.
 * <p>
 * This interface represents the response to a {@link IBackend#queryPixmapIfModified(IProgramInfo, Date, Long)} request. <br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IPixmap.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample 
 * <0>LAST_MODIFIED: Mon Jan 09 11:33:48 CET 2012 | <1>DATA_SIZE: 92867 | <2>DATA_CHECKSUM: 2660 | <3>DATA_BASE64: iVBORw0KGgoAAAANSUhEUgAAAUAA...
 * }
 * 
 * @see IMythCommand#QUERY_PIXMAP_GET_IF_MODIFIED QUERY_PIXMAP_GET_IF_MODIFIED
 * @see IBackend#queryPixmapIfModified(IProgramInfo, Date, Long)
 * @see IBackend#queryPixmap(IProgramInfo)
 *  
 * @since {@mythProtoVersion 49}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_49)
public interface IPixmap extends IVersionable, IPropertyAware<IPixmap.Props> {
	/**
	 * The properties of an {@link IPixmap} response.
	 * 
	 * {@mythProtoVersionMatrix}.
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_49)
	public static enum Props {
		/**
		 * Last modified timestamp.
		 */
		@MythParameterType(Date.class)
		LAST_MODIFIED,
		
		/**
		 * Size of pixmap in bytes.
		 */
		@MythParameterType(Long.class)
		DATA_SIZE,
		
		/**
		 * Checksum of the pixmap file.
		 */
		DATA_CHECKSUM,
		
		/**
		 * Pixmap data as base64 encoded string. 
		 */
		DATA_BASE64
	}
	
	public Date getLastModified();
	
	/**
	 * Gets the data size in bytes.
	 * @return
	 * 		the amount of returned bytes
	 */
	public Long getDataSize();
	
	/**
	 * Gets the pixmap checksum.
	 * @return
	 * 		a checksum.
	 */
	public String getDataCheckSum();
	
	/**
	 * Gets the pixmap as base64 encoded string.
	 * 
	 * @return
	 * 		the encoded pixmap
	 */
	public String getDataBase64();
	
	/**
	 * Gets the pixmap as byte array.
	 * 
	 * @return
	 * 		the pixmap as byte array.
	 */
	public byte[] getData();
	
	/**
	 * Gets the pixmap as stream.
	 * 
	 * @return
	 * 		a pixmap stream.
	 */
	public InputStream getDataStream();
	
	/**
	 * Gets the pixmap as image.
	 * 
	 * @return
	 * 		the pixmap as image.
	 * @throws IOException
	 * 		on io errors
	 */
	public BufferedImage getImage() throws IOException;
}
