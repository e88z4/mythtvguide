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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_32;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_59;

import java.util.Date;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the status of a file on a MythTV backend.
 * <p>
 * This interface represents the response to a {@link IBackend#queryCheckFile IBackend.queryCheckFile} 
 * or {@link IBackend#queryFileExists IBackend.queryFileExists} request.<br>
 * This interface is {@link IPropertyAware property-aware}. See the {@link IFileStatus.Props properties-list} for all properties of a file-status object.
 * 
 * <h3>A request example:</h3>
 * 
 * {@mythCodeExample <pre>
 *      IBackend backend = ....;     // an already connected backend
 *      IProgramInfo program = ....; // a previously recorded program
 *      
 *      // query if the preview image exists
 *      String previewImageName = program.getPreviewImageName();
 *      IFileStatus fileStatus = backend.queryFileExists(previewImageName,"Default");	
 *      if (fileStatus.fileExists()) &#123;
 *         System.out.println("Preview image found: " + fileStatus.getFilePath());
 *      &#125; else &#123;
 *         System.out.println("Preview image not found.");
 *      &#125;
 * </pre>}
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample 
 * 		For a recording preview image (protocol version 56):
 * 		<pre><0>FILE_EXISTS: true | <1>FILE_PATH: /var/lib/mythtv/recordings//1063_20110329201000.mpg.png</pre>
 * 		For a Live-TV recording (protocol version 56):
 * 		<pre><0>FILE_EXISTS: true | <1>FILE_PATH: /var/lib/mythtv/livetv/1063_20110410104434.mpg</pre>
 * 		For a scheduled recording (protocol version 63):
 * 		<pre><00>FILE_EXISTS: true | <01>FILE_PATH: /var/lib/mythtv/recordings//1000_20110701123000.mpg | <02>DEV: 2049 | <03>INO: 136043 | <04>MODE: 33188 | <05>NLINK: 1 | <06>UID: 109 | <07>GID: 117 | <08>RDEV: 0 | <09>FILE_SIZE: 65792000 | <10>BLKSIZE: 4096 | <11>BLOCKS: 128504 | <12>ATIME: Fri Jul 01 12:30:09 CEST 2011 | <13>MTIME: Fri Jul 01 12:31:00 CEST 2011 | <14>CTIME: Fri Jul 01 12:31:00 CEST 2011</pre>
 * }
 * 
 * @see IBackend#queryCheckFile
 * @see IBackend#queryFileExists
 * @see IMythCommand#QUERY_CHECKFILE QUERY_CHECKFILE
 * @see IMythCommand#QUERY_FILE_EXISTS QUERY_FILE_EXISTS
 */
public interface IFileStatus extends IVersionable, IPropertyAware<IFileStatus.Props> {
	
	/**
	 * The properties of an {@link IFileStatus} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Props {
		/**
		 * Specifies if the requested file exists.
		 * 
		 * @see IFileStatus#fileExists()
		 */
		@MythParameterType(Boolean.class)
		FILE_EXISTS,
		
		/**
		 * The path to the file.
		 * 
		 * @see IFileStatus#getFilePath()
		 * @since {@mythProtoVersion 32}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_32)
		FILE_PATH,
		
		/**
		 * The id of the device containing the file.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		DEV,
		
		/**
		 * Inode number.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		INO,
		
		/**
		 * Protection.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		MODE,
		
		/**
		 * Number of hard links.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		NLINK,
		
		/**
		 * User id of owner.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		UID,

		/**
		 * Group id of owner.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		GID,
		
		/**
		 * Device id (if special file).
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		RDEV,
		
		/**
		 * Total size, in bytes.
		 * 
		 * @see IFileStatus#getFileSize()
		 * @since {@mythProtoVersion 59}
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		@MythParameterType(Long.class)
		SIZE,

		/**
		 * Blocksize for filesystem I/O.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		BLKSIZE,
		
		/**
		 * Number of blocks allocated.
		 */
		@MythParameterType(Integer.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		BLOCKS,
		
		/**
		 * Time of last access.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Date.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		ATIME,
		
		/**
		 * Time of last modification.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Date.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		MTIME,
		
		/**
		 * Time of last status change.
		 * @since {@mythProtoVersion 59}
		 */
		@MythParameterType(Date.class)
		@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
		CTIME;
	};
	
	/**
	 * Checks if the queried file exists.
	 * 
	 * @return
	 * 		{@code true} if the queried file exists.
	 */
	public boolean fileExists();
	
	/**
	 * Gets the full path of queried file.
	 * 
	 * @return
	 * 		the files full path, or {@code null} if the file was not found.
	 */
	public String getFilePath();
	
	/**
	 * Gets the size of the file in bytes.
	 * @return
	 * 		the file size in bytes.
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
	public Long getFileSize();
	
	@MythProtoVersionAnnotation(from=PROTO_VERSION_59)
	public Date getLastModified();
}
