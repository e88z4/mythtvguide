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
package org.jmythapi.protocol.annotation;

import org.jmythapi.protocol.ProtocolVersionInfo;

/**
 * This annotation is used to append additional metadata to a protocol-version range.
 * <p>
 * See {@link ProtocolVersionInfo} for constants, that can be used as key for this annotation.
 * 
 * <h3>Usage example:</h3>
 * 
 * In the following example we have added the SVN-Rev. and GIT-Commit ID as metadata to the lower value of the version
 * range. This additional metadata is currently just used for documentation purposes. 
 * 
 * {@mythCodeExample <pre>
 *    &#064;MythProtoVersionAnnotation(from=PROTO_VERSION_15, fromInfo=&#123;
 *        &#064;MythProtoVersionMetadata(key=SVN_COMMIT, value="5156"),
 *        &#064;MythProtoVersionMetadata(key=GIT_COMMIT, value="e6c87e28e6e54e046882")
 *    &#125;)
 *    public abstract boolean rescheduleRecordings(Integer recordingId) throws IOException;
 * </pre>}
 * 
 * @see MythProtoVersionAnnotation protocol-version range
 * @see ProtocolVersionInfo metadata constants
 */
public @interface MythProtoVersionMetadata {
	/**
	 * Gets the metadata key. 
	 * <p>
	 * This is one of the constants defined in {@link ProtocolVersionInfo}.
	 * 
	 * @return
	 * 		the metadata key.
	 */
	public String key();
	
	/**
	 * Gets the metadata value.
	 * 
	 * @return
	 * 		the metadata value.
	 */
	public String value();
}
