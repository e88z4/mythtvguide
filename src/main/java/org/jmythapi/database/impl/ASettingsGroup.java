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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.jmythapi.ISetting;
import org.jmythapi.database.ISettingsProperty;
import org.jmythapi.database.utils.EnumUtils;
import org.jmythapi.protocol.ProtocolVersion;

public abstract class ASettingsGroup <E extends Enum<E> & ISettingsProperty> extends ADatabaseRow<E> {

	public ASettingsGroup(ProtocolVersion protoVersion, int dbVersion, Class<E> propsClass, Map<String,ISetting> settings) {
		super(protoVersion, dbVersion, propsClass, extractArgumentsList(settings, propsClass, dbVersion));
	}
	
	public static <E extends Enum<E> & ISettingsProperty> List<String> extractArgumentsList(Map<String,ISetting> settings, Class<E> propsClass, int dbVersion) {
		// determine all available properties of this settings-group
		final EnumSet<E> properties = EnumUtils.getEnums(propsClass, dbVersion);
		
		// create an empty settings array
		final List<String> values = new ArrayList<String>(properties.size());
		
		for(E property : properties) {
			String propValue = null;
			
			// determine the settings value
			final String propName = property.getSettingName();
			
			// read the proper value from the settings map
			if(settings != null && propName != null && settings.containsKey(propName)) {
				final ISetting setting = settings.get(propName);
				propValue = setting.getData();
			}
			
			values.add(propValue);
		}
		
		return values;
	}
	
	public static <E extends Enum<E> & ISettingsProperty> String[] getSettingsNames(Class<E> propsClass, int dbVersion) {
		// determine all available properties of this settings-group
		final EnumSet<E> properties = EnumUtils.getEnums(propsClass, dbVersion);
		
		// create a list for the settings names
		final List<String> names = new ArrayList<String>(properties.size());
		
		for(E property : properties) {
			// determine the settings value
			final String propName = property.getSettingName();
			if(propName == null || propName.length() == 0) continue;
			
			names.add(propName);
		}
		
		return names.toArray(new String[names.size()]);
	}
}
