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
package org.jmythapi.protocol.response.impl;

import static org.jmythapi.ISetting.Props.DATA;

import org.jmythapi.ISetting;
import org.jmythapi.protocol.IMythPacket;

public class Setting extends AMythResponse<ISetting.Props> implements ISetting {
	private String hostname;
	
	private String name;
	
	public Setting(IMythPacket packet) {
		super(ISetting.Props.class, packet);
	}

	public String getData() {
		return this.getPropertyValueObject(DATA);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> dataType) {
		if(dataType == null) dataType = (Class<T>) String.class;
		return this.getPropertyValueObject(dataType, DATA);
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}
}
