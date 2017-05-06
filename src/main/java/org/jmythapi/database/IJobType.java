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

import org.jmythapi.IPositionalValue;
import org.jmythapi.protocol.response.IEnumGroup;

public interface IJobType extends IEnumGroup<IJobType.Type> {
	public static enum Type implements IPositionalValue {
		/**
		 * No Job.
		 */
		NONE(0x0000),
		
		/**
		 * System Job.
		 */
		SYSTEMJOB(0x00ff),
		
		/**
		 * Transcoding Job.
		 */
		TRANSCODE(0x0001),
		
		/**
		 * Commercial Flagging Job.
		 */
		COMMFLAG(0x0002),
		/**
		 * TODO when was this introduced?
		 */
		METADATA(0x0004),
		
		/**
		 * User Job.
		 */
		USERJOB(0xff00),
		
		/**
		 * User Job 1.
		 * <p>
		 * See MythTV Settings {@code UserJobDesc1}
		 */
		USERJOB1(0x0100),
		
		/**
		 * User Job 2.
		 * <p>
		 * See MythTV Settings {@code UserJobDesc2}
		 */
		USERJOB2(0x0200),
		
		/**
		 * User Job 3.
		 * <p>
		 * See MythTV Settings {@code UserJobDesc3}
		 */
		USERJOB3(0x0400),
		
		/**
		 * User Job 4.
		 * <p>
		 * See MythTV Settings {@code UserJobDesc4}
		 */
		USERJOB4(0x0800);

		private int value;
		
		private Type(int value) {
			this.value = value;
		}
		
		public int getPosition() {
			return this.value;
		}		
	}

}
