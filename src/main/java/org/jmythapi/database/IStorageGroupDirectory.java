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
package org.jmythapi.database;

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1170;

import org.jmythapi.IPropertyAware;
import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.annotation.MythParameterDefaultValue;
import org.jmythapi.protocol.annotation.MythParameterType;

/**
 * <h3>A response example:</h3>
 * {@mythResponseExample
 * Default Storage Group (with two directories):
 * <pre><0>DIRECTORY_ID: 1 | <1>GROUPNAME: Default | <2>HOSTNAME: brain | <3>DIRNAME: /var/lib/mythtv/recordings</pre>
 * <pre><0>DIRECTORY_ID: 13 | <1>GROUPNAME: Default | <2>HOSTNAME: brain | <3>DIRNAME: /var/lib/mythtv/recordings2</pre>
 * 
 * Live-TV Storage Group:
 * <pre><0>DIRECTORY_ID: 10 | <1>GROUPNAME: LiveTV | <2>HOSTNAME: brain | <3>DIRNAME: /var/lib/mythtv/livetv/</pre>
 * }
 */
@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)	
public interface IStorageGroupDirectory extends IPropertyAware<IStorageGroupDirectory.Props> {
	public static enum Props {
		/**
		 * Storage group id.
		 * <p>
		 * <pre>id           INT(11) NOT NULL auto_increment</pre>
		 */
	    @MythParameterType(Integer.class)
	    @MythDatabaseColumn(column="id",nullable=false)
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)		
		DIRECTORY_ID,
		
		/**
		 * Storage group name.
		 * <p>
		 * <pre>groupname    VARCHAR(32) NOT NULL</pre>
		 */
	    @MythDatabaseColumn(column="groupname",nullable=false)
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)
		GROUPNAME,
		
		/**
		 * Host name.
		 * <p>
		 * <pre>hostname     VARCHAR(64) NOT NULL DEFAULT ''</>
		 */
		@MythDatabaseColumn(column="hostname",nullable=false)
		@MythParameterDefaultValue("")
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)
		HOSTNAME,
		
		/**
		 * Directory name.
		 */
		@MythDatabaseColumn(column="dirname",nullable=false)
		@MythParameterDefaultValue("")		
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)
		DIRNAME		
	}
	
	public Integer getDirectoryId();
	
	public String getGroupName();
	
	public String getHostName();
	
	public String getDirName();
}
