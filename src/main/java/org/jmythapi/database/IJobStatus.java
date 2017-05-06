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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1057;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1074;

import org.jmythapi.IPositionalValue;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.response.IEnumGroup;

public interface IJobStatus extends IEnumGroup<IJobStatus.Status> {
	public static enum Status implements IPositionalValue {
		/**
		 * Unknown.
		 */
	    UNKNOWN(0x0000),
	    
	    /**
	     * Queued.
	     */
	    QUEUED(0x0001),
	    
	    /**
	     * Pending.
	     */
	    PENDING(0x0002),
	    
	    /**
	     * Starting.
	     */
	    STARTING(0x0003),
	    
	    /**
	     * Running.
	     */
	    RUNNING(0x0004),
	    
	    /**
	     * Stopping.
	     */
	    STOPPING(0x0005),
	    
	    /**
	     * Paused.
	     */
	    PAUSED(0x0006),
	    
	    /**
	     * Retrying.
	     */
	    RETRY(0x0007),
	    
	    /**
	     * Erroring.
	     * 
	     * @since 1057 61b7d3af98c7db9d6196322bfa90d989bb6fe14f
	     */
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    ERRORING(0x0008),
	    
	    /**
	     * Aborting.
	     * 
	     * @since 1057 61b7d3af98c7db9d6196322bfa90d989bb6fe14f
	     */
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1057)
	    ABORTING(0x0009),
	    
	    /**
	     * Done (Invalid status).
	     * <p>
	     * JOB_DONE is a mask to indicate the job is done no matter what the is.
	     */
	    DONE(0x0100),
	    
	    /**
	     * Finished.
	     */
	    FINISHED(0x0110),
	    
	    /**
	     * Aborted.
	     */
	    ABORTED(0x0120),
	    
	    /**
	     * Errored.
	     */
	    ERRORED(0x0130),
	    
	    /**
	     * Cancelled.
	     * 
	     * @since 1074:  98dc2e0ef19168108a0760e910b61273c17e22bc
	     */
	    @MythDatabaseVersionAnnotation(from=DB_VERSION_1074)
	    CANCELLED(0x0140);

		private int value;
		
		private Status(int value) {
			this.value = value;
		}
		
		public int getPosition() {
			return this.value;
		}
	}
}
