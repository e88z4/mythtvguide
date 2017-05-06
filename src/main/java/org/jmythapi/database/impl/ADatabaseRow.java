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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.database.DatabaseVersion;
import org.jmythapi.database.utils.EnumUtils;
import org.jmythapi.impl.AData;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.utils.EncodingUtils;

public abstract class ADatabaseRow <E extends Enum<E>> extends AData<E> implements IVersionable {
	
	private final int dbVersion;
	
	public ADatabaseRow(ProtocolVersion protoVersion, int dbVersion, Class<E> propsClass) {
		this(protoVersion, dbVersion, propsClass, null);
	}
	
	public ADatabaseRow(ProtocolVersion protoVersion, int dbVersion, Class<E> propsClass, List<String> data) {
		this.dbVersion = dbVersion;
		init(protoVersion,propsClass,data);
	}
	
	public int getDatabaseVersion() {
		return this.dbVersion;
	}
	
	/* ====================================================================
	 * Methods for ARGUMENTS handling
	 * ==================================================================== */	
	
	@Override
	public E getProperty(int idx) {	
		return EnumUtils.getEnum(this.propsClass,this.dbVersion,idx);
	}	
	
	@Override
	public int getPropertyIndex(E prop) {
		return EnumUtils.getEnumPosition(prop,this.dbVersion);
	}
	
	@Override
	public EnumSet<E> getProperties() {
		return EnumUtils.getEnums(this.propsClass, this.dbVersion);
	}
	
	/* ====================================================================
	 * ENCODING/DECODING methods
	 * ==================================================================== */
	
	@Override
	protected <T> T decodeProperty(Class<T> dataType, String dataValue) {
		final boolean isUTC = (this.dbVersion>=DatabaseVersion.DB_VERSION_1302.getVersion());
		return EncodingUtils.decodeString(
			dataType,
			this.protoVersion,
			this.dbVersion,
			isUTC,
			dataValue
		);		
	}
	
	@Override
	protected <S, T> String encodeProperty(java.lang.Class<S> propObjectType, S propValue, java.lang.Class<T> propStringType, String propDefaultValue) {
		final boolean isUTC = (this.dbVersion>=DatabaseVersion.DB_VERSION_1302.getVersion());
		return EncodingUtils.encodeObject(
			propObjectType, 
			this.protoVersion,
			this.dbVersion,
			isUTC,
			propValue,
			propStringType,
			propDefaultValue
		);			
	};

	/* ====================================================================
	 * UTILTIY methods
	 * ==================================================================== */
	/**
	 * This function is called immediately before the object is stored into the database
	 */
	public void onSaveDataItem() {
		// nothing todo here
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			// get the class to clone
			final Class<?> cloneClass = this.getClass();
			
			// determine the constructor to use
			final Constructor<?> constructor = cloneClass.getConstructor(ProtocolVersion.class, int.class, List.class);
			
			// create a new instance
			final Object clonedObject = constructor.newInstance(
				this.protoVersion, this.dbVersion, new ArrayList<String>(this.respArgs)
			);
			return clonedObject;
		} catch (Throwable e) {
			final CloneNotSupportedException c = new CloneNotSupportedException("Unable to clone object");
			c.initCause(e);
			throw c;
		}
	}

	/**
	 * Tests objects for equality.
	 * <p>
	 * Data objects are seen to be equal if the response to the following function calls are equal:
	 * <ol>
	 * 	<li>{@link #getClass()}</li>
	 * 	<li>{@link #getVersionNr()}</li>
	 * 	<li>{@link #getDatabaseVersion()}</li>
	 * 	<li>{@link #getPropertyClass()}</li>
	 * 	<li>{@link #getPropertyValues()}</li>
	 * </ol>
	 */	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) return false;
		return ((ADatabaseRow<?>)obj).dbVersion == this.dbVersion;
	}
}
