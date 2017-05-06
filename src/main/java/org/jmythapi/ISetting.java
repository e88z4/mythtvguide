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
package org.jmythapi;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_17;

import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtocolSkipProperty;

/**
 * An interface to get the value of a MythTV setting.
 * <p>
 * This interface represents the response to a {@link IBackend#querySetting(String, String)} request.<br>
 * This interface is {@link org.jmythapi.IPropertyAware property-aware}. See the {@link ISetting.Props properties-list} for all properties of this interface.
 * 
 * <h3>Response example:</h3>
 * 
 * {@mythResponseExample
 * 		<pre><0>DATA_BASE64: DE</pre>
 * }
 */
@MythProtoVersionAnnotation(from = PROTO_VERSION_17)
public interface ISetting extends IVersionable, IPropertyAware<ISetting.Props> {
	/**
	 * Properties of an {@link ISetting} response.
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	@MythProtoVersionAnnotation(from = PROTO_VERSION_17)
	public static enum Props {
		/**
		 * @see ISetting#getData()
		 */
		@MythDatabaseColumn(table="settings",column="data")
		DATA,
		
		@MythProtocolSkipProperty
		@MythDatabaseColumn(table="settings",column="hostname")
		HOSTNAME,
		
		@MythProtocolSkipProperty
		@MythDatabaseColumn(table="settings",column="value")
		NAME
	};
	
	/**
	 * Gets the settings value
	 * 
	 * @return 
	 * 		The value of the setting.
	 * 
	 * @see ISetting.Props#DATA
	 */
	public String getData();
	
	/**
	 * Gets the settings value and converts it into the given data type.
	 * 
	 * @param <T>
	 * 		the data type
	 * @param dataType
	 * 		the data type
	 * @return
	 * 		the settings value
	 */
	public <T> T getData(Class<T> dataType);
	/**
	 * Gets the backends hostname this settings belongs to.
	 * @return the name of the host or {@code null}
	 */
	public String getHostname();

	/**
	 * The name of the setting.
	 * 
	 * @return
	 * 		the settings name
	 */
	public String getName();
}
