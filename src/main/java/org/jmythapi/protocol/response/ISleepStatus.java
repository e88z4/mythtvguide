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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_45;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * The sleep state of a MythTV backend.
 * <p>
 * This interface represents the response to an {@link IRemoteEncoder#getSleepStatus()} request.<br>
 * This interface is a {@link IEnumFlagGroup}. See {@link ISleepStatus.Flags flags-list} for a list of all available flags.
 * 
 * @see IRemoteEncoder#getSleepStatus()
 * @see IMythCommand#QUERY_REMOTEENCODER_GET_SLEEPSTATUS QUERY_REMOTEENCODER_GET_SLEEPSTATUS
 * 
 * @since {@mythProtoVersion 45}
 */
@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
	@MythProtoVersionMetadata(key=SVN_COMMIT,value="20084"),
	@MythProtoVersionMetadata(key=GIT_COMMIT,value="663cd89811cf7fc22f59")
})
public interface ISleepStatus extends IVersionable, IEnumFlagGroup<ISleepStatus.Flags> {

	/**
	 * The flags of an {@link ISleepStatus} response.
	 * <p>
	 * The following list contains all possible awake/sleep states of a MythTV backend slave.
	 * 
	 * {@mythProtoVersionMatrix}
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/tree/master/mythtv/libs/libmythtv/tv.h">tv.h</a>
	 * 
	 * @since {@mythProtoVersion 45}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
	public static enum Flags implements IFlag {
		/**
		 * Awaken.
		 * <p>
		 * A slave is awake when it is connected to the master
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		SSTATUS_AWAKE(0x00),
		
		/**
		 * Asleep.
		 * <p>
		 * A slave is considered asleep when it is not awake and not undefined.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		SSTATUS_ASLEEP(0x01),
		
		/**
		 * Falling asleep.
		 * <p>
		 * A slave is marked as falling asleep when told to shutdown by the master. 
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		SSTATUS_FALLING_ASLEEP(0x02),
		
		/**
		 * Waking.
		 * <p>
		 * A slave is marked as waking when the master runs the slave's wakeup command
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		SSTATUS_WAKING(0x05),
		
		/**
		 * Undefined.
		 * <p>
		 * A slave's sleep status is undefined when it has never connected 
		 * to the master backend or is not able to be put to sleep and 
		 * awakened.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_45)
		SSTATUS_UNDEFINED(0x08);
		
		private final long flag;
		Flags(long flag) {
			this.flag = flag;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(this.flag);
		}
	}
}