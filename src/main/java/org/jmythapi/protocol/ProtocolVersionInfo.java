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
package org.jmythapi.protocol;

import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;


/**
 * This class defines constants used to further describe a protocol version.
 * <p>
 * The constants of this class are used to append additional metadata to a {@link ProtocolVersion} constant. 
 * The assigned metadata can be queried as runtime using {@code ProtocolVersion.getMetaData()}.<br>
 * Additionally it can be used to add metadata to an {@link MythProtoVersionAnnotation} via the {@link MythProtoVersionMetadata} annotation. The assigned metadata can be queried
 * using {@code MythProtoVersionAnnotation.fromInfo()} and {@code MythProtoVersionAnnotation.toInfo()} .
 * 
 * <h3>Usage example:</h3>
 * <h4>Adding Metadata to the ProtocolVersion</h4>
 * In the following example the SVN-revision, GIT-commit ID and the date of the protocol
 * version {@mythProtoVersion 01} is added as metadata to the enumeration constant.
 * 
 * {@mythCodeExample <pre>
 * PROTO_VERSION_01(1,null,new String[][]&#123;
 *    &#123;DATE,"2004-01-29"&#125;,
 *    &#123;SVN_COMMIT,"3021"&#125;,
 *    &#123;GIT_COMMIT,"e6ffdd37481937e09ec7"&#125;
 * &#125;)
 * </pre>}
 * 
 * <h4>Adding additional Metadata to the MythProtoVersionAnnotation:</h4>
 * In the following example the added enumeration constant belongs to protocol version 31, 
 * but was added in a separate commit. Therefore the SVN-revision and GIT-commit ID was added
 * as metadata to the protocol-version annotation.
 * 
 * {@mythCodeExample <pre>
 *    public static enum Status implements IVersionableValue &#123;
 *       ...
 *       
 *       // Recorder Failed.
 *       &#064;MythProtoVersionAnnotation(from=PROTO_VERSION_31,fromInfo=&#123;
 *          &#064;MythProtoVersionMetadata(key=SVN_COMMIT,value="11211"),
 *          &#064;MythProtoVersionMetadata(key=GIT_COMMIT,value="ff50f067a1b403779ac4")
 *       &#125;)
 *       FAILED(-9)
 *       
 *       ...
 *    &#125;
 * </pre>}
 * 

 * <p>
 * 
 * @see ProtocolVersion protocol-version
 * @see MythProtoVersionAnnotation protocol-version annotation
 * @see MythProtoVersionMetadata protocol-version metadata
 */
public class ProtocolVersionInfo {
	/**
	 * The date when the protocol version was introduced.
	 */
	public static final String DATE = "Date";
	
	/**
	 * The Git commit ID.
	 */
	public static final String GIT_COMMIT = "Git Commit";
	
	/**
	 * The SVN revision number of the commit. 
	 */
	public static final String SVN_COMMIT = "SVN-Rev.";
	
	/**
	 * The MythTV release version.
	 */
	public static final String MYTH_RELEASE = "MythTV-Release";
	
	/**
	 * The MythBuntu release version.
	 */
	public static final String MYTHBUNTU_RELEASE = "MythBuntu-Release";
}
