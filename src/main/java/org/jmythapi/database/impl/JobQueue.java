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
package org.jmythapi.database.impl;

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1056;
import static org.jmythapi.database.IJobQueue.Props.CHANNEL_ID;
import static org.jmythapi.database.IJobQueue.Props.COMMENT;
import static org.jmythapi.database.IJobQueue.Props.ID;
import static org.jmythapi.database.IJobQueue.Props.INSERT_TIME;
import static org.jmythapi.database.IJobQueue.Props.REC_START_TIME;
import static org.jmythapi.database.IJobQueue.Props.STATUS;
import static org.jmythapi.database.IJobQueue.Props.STATUS_TIME;
import static org.jmythapi.database.IJobQueue.Props.TYPE;

import java.util.Date;
import java.util.List;

import org.jmythapi.database.IJobQueue;
import org.jmythapi.database.IJobStatus;
import org.jmythapi.database.IJobType;
import org.jmythapi.database.IJobStatus.Status;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.ProtocolVersion;

@MythDatabaseVersionAnnotation(from=DB_VERSION_1056)
public class JobQueue extends ADatabaseRow<IJobQueue.Props> implements IJobQueue {
	public JobQueue(ProtocolVersion protoVersion, int dbVersion) {
		this(protoVersion,dbVersion,null);
	}
	
	public JobQueue(ProtocolVersion protoVersion, int dbVersion, List<String> data) {
		super(protoVersion, dbVersion, IJobQueue.Props.class, data);
	}
	
	public Integer getId() {
		return this.getPropertyValueObject(ID);
	}	
	
	public Integer getChannelId() {
		return this.getPropertyValueObject(CHANNEL_ID);
	}
	
	public Date getRecordingStartTime() {
		return this.getPropertyValueObject(REC_START_TIME);
	}
	
	public Date getInsertTime() {
		return this.getPropertyValueObject(INSERT_TIME);
	}
	
	public IJobType getType() {
		return this.getPropertyValueObject(TYPE);
	}
	
	public IJobStatus getStatus() {
		return this.getPropertyValueObject(STATUS);
	}
	
	public Date getStatusTime() {
		return this.getPropertyValueObject(STATUS_TIME);
	}
	
	public boolean hasFinished() {
		final IJobStatus status = this.getStatus();
		if(status == null) return false;
		
		return status.hasEnum(Status.DONE,Status.FINISHED,Status.ABORTED,Status.ERRORED,Status.CANCELLED);
	}
	
	public String getComment() {
		return this.getPropertyValueObject(COMMENT);
	}
}
