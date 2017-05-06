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
package org.jmythapi.protocol.events.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_75;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.events.ISystemEvent;
import org.jmythapi.utils.EncodingUtils;

public class SystemEvent extends AMythEvent<ISystemEvent.Props> implements ISystemEvent {

	public SystemEvent(IMythPacket packet) {
		super(Props.class, packet);
	}

	@Override
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		final int expectedSize = this.getExpectedSize(responseArgs);		
		if (expectedSize < 0) {
			return;
		}
		
		if (responseArgs == null || responseArgs.size() == 0 || (responseArgs.size()-1)%2 != 0) {
			throw new IllegalArgumentException(String.format(
				"%d (+ x*2) args expected but %d args found.",
				expectedSize,
				responseArgs==null?0:responseArgs.size()
			));
		}		
	}
	
	public String getEventType() {
		return this.getPropertyValueObject(Props.EVENT_TYPE);
	}
	
	public Map<String,String> getSystemProperties() {
		final boolean isUTC = (this.getVersionNr().compareTo(PROTO_VERSION_75)>=0);
		final int propStartIdx = this.getPropertyIndex(Props.EVENT_TYPE);
		
		final LinkedHashMap<String,String> additionalProps = new LinkedHashMap<String, String>();
		if(propStartIdx + 1 < this.getPropertyCount()) {
			for(int i=propStartIdx+1; i<this.getPropertyCount();i=i+2) {
				final String key = this.getPropertyValue(i);
				String value = this.getPropertyValue(i+1);
				if(isUTC && key.endsWith("TIME") && value != null && value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")) {
					final Date dateValue = EncodingUtils.parseDate(value,true);
					value = EncodingUtils.formatDateTime(dateValue, false);
				}
				additionalProps.put(key,value);
			}
		} 
		return additionalProps;
	}
	
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		buff.append(super.toString());
		
		final Map<String,String> sysProps = this.getSystemProperties();
		if(!sysProps.isEmpty()) {
			buff.append(" ").append(sysProps);
		}
		
		return buff.toString();
	}	
}
