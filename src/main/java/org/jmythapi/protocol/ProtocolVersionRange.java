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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jmythapi.impl.AVersionRange;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.utils.CommandUtils;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * This class represents a MythTV protocol version range.<br>
 * It is mainly used by utility classes, such as {@link EnumUtils} or {@link CommandUtils}, to determine the valid 
 * protocol-version range for a response property or command.
 * <p>
 * See {@link MythProtoVersionAnnotation} for more information about protocol version ranges.
 * 
 * @see CommandUtils#getCommandVersionRange(String)
 * @see EnumUtils#getEnumVersionRange(Enum)
 */
public class ProtocolVersionRange extends AVersionRange<ProtocolVersion> {	
	
	/**
	 * The default protocol-version range. 
	 * <p>
	 * This range is used for commands or properties if no other range
	 * was specified via an {@link MythProtoVersionAnnotation}.
	 * 
	 * @see CommandUtils#getCommandVersionRange(String)
	 * @see EnumUtils#getEnumVersionRange(Enum)
	 */
	public static final ProtocolVersionRange DEFAULT_RANGE = new ProtocolVersionRange(
		PROTO_VERSION_00,
		PROTO_VERSION_LATEST
	); 
	
	/**
	 * Additional meta-data of the lower-version range.
	 * @see ProtocolVersionRange#fromMetaData
	 */
	private Map<String,String> fromMetaData = new TreeMap<String, String>();

	/**
	 * Additional meta-data of the lower-version range.
	 * @see ProtocolVersionRange#toMetaData
	 */
	private Map<String,String> toMetaData = new TreeMap<String, String>();	
	
	private ProtocolVersion fromFallback;
	
	private ProtocolVersion toFallback;
	
	/**
	 * Constructs a version-range object from an protocol-version annotation
	 * @param versionRange
	 * 		the protocol-version-annotation
	 */
	public ProtocolVersionRange(MythProtoVersionAnnotation versionRange) {
		this(
			versionRange.from(),
			toMap(versionRange.fromInfo()),
			null,
			versionRange.to(),
			toMap(versionRange.toInfo()),
			null
		);
	}

	public ProtocolVersionRange(ProtocolVersion from, ProtocolVersion to) {
		this(from,null,null,to,null,null);
	}
	
	public ProtocolVersionRange(
		ProtocolVersion from, Map<String,String> fromMetaData, ProtocolVersion fromFallback,  
		ProtocolVersion to, Map<String,String> toMetaData, ProtocolVersion toFallback
	) {
		super(
			from==null?PROTO_VERSION_00:from,
			to==null?PROTO_VERSION_LATEST:to
		);
		this.fromMetaData = fromMetaData;
		this.fromFallback = fromFallback;
		this.toMetaData = toMetaData;
		this.toFallback = toFallback;
	}
	
	/**
	 * Converts a list of metadata annotations into a key/value map.
	 * 
	 * @param metaDatas
	 * 		the metadata annotations 
	 * @return
	 * 		the generated map
	 */
	private static Map<String,String> toMap(MythProtoVersionMetadata[] metaDatas) {
		final Map<String,String> metaDataMap = new HashMap<String, String>();
		if(metaDatas != null) {
			for(MythProtoVersionMetadata metaData : metaDatas) {
				metaDataMap.put(metaData.key(),metaData.value());
			}
		}
		return metaDataMap;
	}
	
	public Map<String,String> fromInfo() {
		if(this.fromMetaData == null) return Collections.emptyMap();
		return this.fromMetaData;
	}
	
	public ProtocolVersion fromFallback() {
		return this.fromFallback;
	}
	
	public Map<String,String> toInfo() {
		if(this.toMetaData == null) return Collections.emptyMap();
		return this.toMetaData;
	}
	
	public ProtocolVersion toFallback() {
		return this.toFallback;
	}

	public boolean isInRange(int protoVersionNr) {
		final ProtocolVersion protoVersion = ProtocolVersion.valueOf(protoVersionNr);
		return isInRange(this.fromVersion, this.toVersion, protoVersion);
	}
	
	private static boolean isInRange(ProtocolVersion from, ProtocolVersion to, ProtocolVersion current) {	
		return !((from.compareTo(current)>0)||(!to.equals(PROTO_VERSION_LATEST) && to.compareTo(current)<=0));
	}
	
	/**
	 * Generates a new protocol-version-range depending on the values of this range
	 * and a parent range.
	 * 
	 * @param parentRange
	 * 		the parent version range
	 * @return
	 * 		the new version range
	 */
	public ProtocolVersionRange restrictRange(ProtocolVersionRange parentRange) {
		if(parentRange == null) return this;
		
		ProtocolVersion from = this.from();
		if(parentRange.from().compareTo(from)>0) {
			from = parentRange.from();
		}
		ProtocolVersion to = this.to();
		if(parentRange.to().compareTo(to)<0) {
			to = parentRange.to();
		}
		
		return new ProtocolVersionRange(from,to);
	}

}