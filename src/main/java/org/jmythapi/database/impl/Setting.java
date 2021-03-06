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

import static org.jmythapi.ISetting.Props.DATA;

import java.util.List;

import org.jmythapi.ISetting;
import org.jmythapi.protocol.ProtocolVersion;

public class Setting extends ADatabaseRow<ISetting.Props> implements ISetting {
	public Setting(ProtocolVersion protoVersion, int dbVersion, List<String> data) {
		super(protoVersion, dbVersion, ISetting.Props.class, data);
	}

	public String getData() {
		return this.getPropertyValueObject(Props.DATA);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> dataType) {
		if(dataType == null) dataType = (Class<T>) String.class;
		return this.getPropertyValueObject(dataType, DATA);
	}	

	public String getHostname() {
		return this.getPropertyValueObject(Props.HOSTNAME);
	}

	public String getName() {
		return this.getPropertyValueObject(Props.NAME);
	}

}
