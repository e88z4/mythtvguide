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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.jmythapi.impl.AData;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.impl.MythPacket;
import org.jmythapi.protocol.response.IMythResponse;
import org.jmythapi.protocol.utils.EnumUtils;
import org.jmythapi.utils.EncodingUtils;

public abstract class AMythResponse <E extends Enum<E>> extends AData<E> implements IMythResponse<E> {
	/**
	 * A constructor to create a response object with an empty response-arguments list.
	 * <p>
	 * This constructor should be used if the response properties should be set via 
	 * the corresponding setter methods afterwards. 
	 * 
	 * @param protoVersion
	 * 		the protocol version
	 * @param propsClass
	 * 		a enumeration class defining all supported response properties
	 */
	public AMythResponse(ProtocolVersion protoVersion, Class<E> propsClass) {
		this(protoVersion,propsClass,(List<String>)null);
	}

	/**
	 * A constructor to create a response object from a given packet object.
	 * 
	 * @param propsClass
	 * 		a enumeration class defining all supported response properties.
	 * @param packet
	 * 		the packet object.
	 */
	public AMythResponse(Class<E> propsClass, IMythPacket packet) {
		this(packet.getVersionNr(), propsClass, packet.getPacketArgs());
	}	
	
	/**
	 * A constructor to create a response object from the given response-arguments array.
	 * 
	 * @param protoVersion
	 * 		the protocol version.
	 * @param propsClass
	 * 		a enumeration class defining all supported response properties
	 * @param responseArgs
	 * 		the array of response arguments
	 */
	public AMythResponse(ProtocolVersion protoVersion, Class<E> propsClass, String... responseArgs) {
		this(protoVersion, propsClass, responseArgs==null?null:Arrays.asList(responseArgs));
	}
	
	/**
	 * A constructor to create a response object from the given response-arguments list.
	 *  
	 * @param protoVersion
	 * 		the protocol version.
	 * @param propsClass
	 * 		a enumeration class defining all supported response properties
	 * @param responseArgs
	 * 		the list of response arguments.
	 */
	public AMythResponse(ProtocolVersion protoVersion, Class<E> propsClass, List<String> responseArgs) {
		init(protoVersion, propsClass, responseArgs);
		
		// check if the response message is valid in size
		this.checkSize(this.respArgs);
	}	
	
	/* ====================================================================
	 * ISendable Method
	 * ==================================================================== */
	
	/**
	 * <p>Converts this response-object into a {@link IMythPacket}.</p>
	 */
	public IMythPacket getPacket() {
		// return result
		return new MythPacket(this.protoVersion, this.respArgs);
	}
	
	/* ====================================================================
	 * Methods for ARGUMENTS handling
	 * ==================================================================== */
	
	@Override
	public E getProperty(int idx) {
		return EnumUtils.getEnum(this.propsClass,this.protoVersion,idx);
	}	 

	@Override
	public int getPropertyIndex(E prop) {
		return EnumUtils.getEnumPosition(prop, this.protoVersion);
	}
	
	@Override
	public EnumSet<E> getProperties() {
		return EnumUtils.getEnums(this.propsClass, this.protoVersion);
	}	
	
	/* ====================================================================
	 * ENCODING/DECODING methods
	 * ==================================================================== */
	
	@Override
	protected <T> T decodeProperty(Class<T> dataType, String dataValue) {
		return EncodingUtils.decodeString(
			dataType,
			this.protoVersion,
			-1, 				// we do not care about the database version 
			false,				// we use the local timezone
			dataValue
		);		
	}
	
	@Override
	protected <S, T> String encodeProperty(java.lang.Class<S> propObjectType, S propValue, java.lang.Class<T> propStringType, String propDefaultValue) {
		return EncodingUtils.encodeObject(
			propObjectType, 
			this.protoVersion,
			-1,					// we do not care about the database version 
			false,				// we use the local timezone
			propValue,
			propStringType,
			propDefaultValue
		);			
	};
	
	/* ====================================================================
	 * Methods to check arguments validity
	 * ==================================================================== */
	
	/**
	 * <p>This function returns the expected size of the response-argument-list.</p> 
	 * The expected size may be different depending on the current MythTV-protocol-version.<br/>
	 * 
	 * The default implementation of this function uses {@link EnumUtils#getEnumLength(Class, int)} 
	 * to determine the expected size of the argument-list.
	 * 
	 * @param responseArgs the response-argument-list
	 */
	protected int getExpectedSize(List<String> responseArgs) {
		final int expectedSize = EnumUtils.getEnumLength(this.propsClass, this.protoVersion);
		return expectedSize;		
	}
	
	/**
	 * <p>A function to check if the given response-argument-liste contains all expected arguments.</p>
	 * The expected argument-list-size is calculated using {@link #getExpectedSize(ArrayList)}. 
	 * 
	 * @param responseArgs the response-argument-list
	 * @throws IllegalArgumentException if expected arguments are missing.
	 */
	protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
		final int expectedSize = this.getExpectedSize(responseArgs);		
		if (expectedSize < 0) {
			return;
		}
		
		if (responseArgs == null || responseArgs.size() != expectedSize) {
			throw new IllegalArgumentException(String.format(
				"%d args expected but %d args found.",
				expectedSize,
				responseArgs==null?0:responseArgs.size()
			));
		}
	}
	

}
