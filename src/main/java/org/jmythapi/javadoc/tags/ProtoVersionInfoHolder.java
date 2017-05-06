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
package org.jmythapi.javadoc.tags;

import java.util.Collections;
import java.util.Map;

import org.jmythapi.protocol.ProtocolVersion;

public class ProtoVersionInfoHolder {
	private final ProtocolVersion protoVersion;
	private final String protoVersionDescription;
	private final Map<String,String> additionalInfos;
	
	public ProtoVersionInfoHolder(ProtocolVersion protoVersion) {
		this(protoVersion,null,null);
	}
	
	public ProtoVersionInfoHolder(ProtocolVersion protoVersion, Map<String,String> additionalInfos, String descripton) {
		this.protoVersion = protoVersion;
		this.protoVersionDescription = descripton;
		this.additionalInfos = additionalInfos;
	}
	
	public ProtocolVersion getVersionEnum() {
		return this.protoVersion;
	}
	
	public String getDescription() {
		return this.protoVersionDescription;
	}
	
	public Map<String,String> getAdditionalInfos() {
		if(additionalInfos == null) return Collections.emptyMap();
		return additionalInfos;
	}
}
