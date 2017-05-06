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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_44;

import java.util.Date;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * An interface to get the files of a MythTV storage group.
 * <p>
 * This interface represents the response of a {@link IBackend#queryStorageGroupFile(String, String, String)} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link IStorageGroupFile.Props properties-list} for all properties of this interface.
 * 
 * <h3>A response example:</h3>
 * 
 * {@mythResponseExample
 * 		A recording file:
 * 		<pre><0>FILE_TYPE: file | <1>FILE_PATH: /var/lib/mythtv/recordings/11123_20110321200900.mpg | <2>LAST_MOD: Mon Mar 21 22:39:28 CET 2011 | <3>FILE_SIZE: 3815729968</pre>
 * 		A preview image:
 * 		<pre><0>FILE_TYPE: file | <1>FILE_PATH: /var/lib/mythtv/recordings/1063_20110329201000.mpg.png | <2>LAST_MOD: Sun Apr 10 09:46:32 CEST 2011 | <3>FILE_SIZE: 86681</pre>
 *      The reponse to a fileNameOnly query:
 *      <pre><0>FILE_TYPE: file | <1>FILE_PATH: 1063_20110329203500.mpg | <2>LAST_MOD: null | <3>FILE_SIZE: null</pre>
 *      A storage-group directory:
 *      <pre><0>FILE_TYPE: sgdir | <1>FILE_PATH: /var/lib/mythtv/recordings | <2>LAST_MOD: null | <3>FILE_SIZE: null</pre>
 * }
 * 
 * @see IBackend#queryStorageGroupFile
 * @see IMythCommand#QUERY_SG_FILEQUERY QUERY_SG_FILEQUERY
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
public interface IStorageGroupFile extends IVersionable, IPropertyAware<IStorageGroupFile.Props> {

	/**
	 * The properties of an {@link IStorageGroupFile} response.
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_44)
	public static enum Props {
		/**
		 * The type of the entry.
		 * @see IStorageGroupFile#getFileType()
		 */
		FILE_TYPE,
		
		/**
		 * The full path to the file.
		 * 
		 * @see IStorageGroupFile#getFilePath()
		 */
		FILE_PATH,
		
		/**
		 * The last modified date.
		 * 
		 * @see IStorageGroupFile#getLastModified()
		 */
		@MythParameterType(Date.class)
		LAST_MOD,
		
		/**
		 * The file size.
		 * 
		 * @see IStorageGroupFile#getFileSize()
		 */
		@MythParameterType(Long.class)
		FILE_SIZE
	}
	
	/**
	 * Checks if the current entry is a directory.
	 * @return
	 * 		{@code true} if the entry is a directory.
	 */
	public boolean isDirectory();
	
	/**
	 * Gets the type of the file.
	 * 
	 * @return
	 * 		{@code file} if the entry is a file.
	 * 
	 * @see IStorageGroupFile.Props#FILE_TYPE
	 */
	public String getFileType();
	
	/**
	 * Gets the full path to the file
	 * @return
	 * 		the full file path
	 * 
	 * @see IStorageGroupFile.Props#FILE_PATH
	 */
	public String getFilePath();
	
	/**
	 * Gets the last modified date of the file
	 * @return
	 * 		the files last-mod date
	 * 
	 * @see IStorageGroupFile.Props#LAST_MOD
	 */
	public Date getLastModified();
	
	/**
	 * Gets the file size
	 * @return
	 * 		the file size.
	 * 
	 * @see IStorageGroupFile.Props#FILE_SIZE
	 */
	public Long getFileSize();	
}
