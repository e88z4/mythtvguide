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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1056;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1182;

import java.util.Date;

import org.jmythapi.IPropertyAware;
import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.database.impl.JobCommands;
import org.jmythapi.database.impl.JobStatus;
import org.jmythapi.database.impl.JobType;
import org.jmythapi.protocol.annotation.MythParameterDefaultValue;
import org.jmythapi.protocol.annotation.MythParameterType;

/**
 * 
 * <h3>A response example:</h3>
 * {@mythResponseExample
 * 		<00>ID: 5 | <01>CHANNEL_ID: 1091 | <02>REC_START_TIME: Thu Jan 01 01:18:11 CET 1970 | <03>INSERT_TIME: Tue Jan 24 06:52:07 CET 2012 | <04>TYPE: 2 | <05>COMMANDS: 0 | <06>FLAGS: 1 | <07>STATUS: 1 | <08>STATUS_TIME: Tue Jan 24 06:52:07 CET 2012 | <09>HOSTNAME:  | <10>ARGS: [B@1fa39bb | <11>COMMENT: Queued via MythWeb | <12>SCHEDULED_RUN_TIME: Mon Jan 01 00:00:00 CET 2007
 * }
 */
@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)	
public interface IJobQueue extends IPropertyAware<IJobQueue.Props> {
	public static enum Props {
		/**
		 * Job ID.
		 * <p>
		 * A unique identifier for entries in the table.
		 * <p>
		 * <pre>id INTEGER NOT NULL AUTO_INCREMENT</pre>
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(column="id")
		ID,
		
		/**
		 * Channel ID.
		 * <p>
		 * Relates to the chanid field of the channel table.
		 * <p>
		 * <pre>chanid INTEGER(10) NOT NULL</pre>
		 */
		@MythParameterType(Integer.class)
		@MythDatabaseColumn(column="chanid",nullable=false)
		CHANNEL_ID,
		
		/**
		 * Recording Start Time.
		 * <p>
		 * the start time of the recording.
		 * <p>
		 * <pre>starttime DATETIME NOT NULL</pre>
		 */
		@MythParameterType(Date.class)
		@MythDatabaseColumn(column="starttime",nullable=false)
		REC_START_TIME,
		
		/**
		 * Insert Time.
		 * <p>
		 * the time the job was queued.

		 * <p>
		 * <pre>inserttime DATETIME NOT NULL</pre>
		 */
		@MythParameterType(Date.class)
		@MythDatabaseColumn(column="inserttime",nullable=false)		
		INSERT_TIME,
		
		/**
		 * Job Type.
		 * <p>
		 * the type of job
		 * <p>
		 * <pre>type INTEGER NOT NULL</pre>
		 */
		@MythParameterType(JobType.class)
		@MythDatabaseColumn(column="type",nullable=false)		
		TYPE,
		
		/**
		 * Commands.
		 * <p>
		 * RUN = 0x0000, PAUSE = 0x0001, RESUME = 0x0002, STOP = 0x0004, RESTART = 0x0008 
		 * <p>
		 * <pre>cmds INTEGER NOT NULL DEFAULT 0/<pre>
		 */
		@MythParameterType(JobCommands.class)
		@MythParameterDefaultValue("0")
		@MythDatabaseColumn(column="cmds",nullable=false)		
		COMMANDS,
		
		/**
		 * Flags.
		 * <p>
		 * NO_FLAGS = 0x0000, USE_CUTLIST = 0x0001, LIVE_REC = 0x0002, EXTERNAL = 0x0004 
		 * <p>
		 * <pre>flags INTEGER NOT NULL DEFAULT 0</pre>
		 */
		@MythParameterType(Integer.class)
		@MythParameterDefaultValue("0")
		@MythDatabaseColumn(column="flags",nullable=false)		
		FLAGS,
		
		/**
		 * Status.
		 * <p>
		 * UNKNOWN = 0x0000, QUEUED = 0x0001, PENDING = 0x0002, STARTING = 0x0003, RUNNING = 0x0004, STOPPING = 0x0005, 
		 * PAUSED = 0x0006, RETRY = 0x0007, ERRORING = 0x0008, ABORTING = 0x0009, DONE = 0x0100, FINISHED = 0x0110, 
		 * ABORTED = 0x0120, ERRORED = 0x0130, CANCELLED = 0x0140
		 * <p>
		 * <pre>status INTEGER NOT NULL DEFAULT 0</pre>
		 */
		@MythParameterType(JobStatus.class)
		@MythParameterDefaultValue("0")
		@MythDatabaseColumn(column="status",nullable=false)		
		STATUS,
		
		/**
		 * Status Time.
		 * <p>
		 * the time the status was last updated.
		 * <p>
		 * <pre>statustime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP</pre>
		 */
		@MythParameterType(Date.class)
		@MythDatabaseColumn(column="statustime",nullable=false)
		STATUS_TIME,
		
		/**
		 * Hostname.
		 * <p>
		 * <pre>hostname varchar(64) NOT NULL DEFAULT ''</pre>
		 */
		@MythParameterType(String.class)
		@MythParameterDefaultValue("")
		@MythDatabaseColumn(column="hostname",nullable=false,length=64)		
		HOSTNAME,
		
		/**
		 * Args.
		 * <p>
		 * <pre>args BLOB NOT NULL DEFAULT ''/<pre>
		 */
		@MythParameterType(String.class)
		@MythParameterDefaultValue("")
		@MythDatabaseColumn(column="args",nullable=false)	
		ARGS,
		
		/**
		 * Comment.
		 * <p>
		 * <pre>comment VARCHAR(128) NOT NULL DEFAULT ''/<pre>
		 */
		@MythParameterType(String.class)
		@MythParameterDefaultValue("")
		@MythDatabaseColumn(column="comment",nullable=false,length=128)			
		COMMENT,		
		
		/**
		 * Scheduled Run Time.
		 * <p>
		 * the time that the job should run.
		 * <p>
		 * 
		 * TODO: what about the default value?
		 * <pre>"ALTER TABLE jobqueue ADD schedruntime datetime NOT NULL default '2007-01-01 00:00:00'</pre>
		 */
		@MythParameterType(Date.class)
		@MythDatabaseColumn(column="schedruntime",nullable=false)
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1182)
		SCHEDULED_RUN_TIME
	}
	
	public Integer getId();
	
	public Integer getChannelId();
	
	public Date getRecordingStartTime();
	
	public Date getInsertTime();
	
	public IJobType getType();
	
	public IJobStatus getStatus();
	
	public Date getStatusTime();
	
	public boolean hasFinished();
	
	public String getComment();
}
