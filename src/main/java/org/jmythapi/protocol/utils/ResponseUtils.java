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
package org.jmythapi.protocol.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IMythResponse;
import org.jmythapi.protocol.response.impl.AMythResponse;

/**
 * This class provides MythTV-response related utility function.
 * <p>
 * <i>Usage example:</i>
 * <br>
 * {@mythCodeExample <pre>
 *    // read the response packet from the connection
 *    IMythPacket resp = cmdConnection.readMessage();
 *    
 *    // convert the response packet into a specific response object
 *    IRecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
 * </pre>}
 * <br>
 * In the above example a new instance of the class {@code RecorderInfo} is created and
 * initialized with the arguments of the response packet.
 * 
 * @see IMythResponse
 */
public class ResponseUtils {
	public static final String STATUS_OK = "OK";
	
	/**
	 * For logging
	 */
	private static final Logger logger = Logger.getLogger(ResponseUtils.class.getName());
	
	/**
	 * Inspects a class for the existence of the {@code valueOf(IMythPacket)} method.
	 * 
	 * @param <E>
	 * 		the type of the response object properties
	 * @param <Resp>
	 * 		the type of the response object
	 * @param respClass
	 * 		the class of the response object
	 * @return
	 * 		the valueOf method, or {@code null}
	 */
	private static final <E extends Enum<E>, Resp extends AMythResponse<E> & IMythResponse<E>> Method getValueOf(
		Class<Resp> respClass
	) {
		try {
			final Method valueOf = respClass.getMethod("valueOf", new Class[]{IMythPacket.class});
			final Class<?> returnType = valueOf.getReturnType();
			if(returnType.equals(respClass)) return valueOf;
		} catch(NoSuchMethodException nsme) {
			// this is ok here
		}
		return null;
	}
	
	/**
	 * Converts a protocol packet into a response object.
	 * <p>
	 * This function create a new subclass of the {@link AMythResponse} object and initializes
	 * it with the data from the given protocol packet.
	 * <p>
	 * Response objects passed to this function must provide one of the following functions:
	 * <ul>
	 * 	<li>a static function {@code valueOf(IMythPacket)}</li>
	 *  <li>a constructor {@code ResponseObject(IMythPacket)}</li>
	 * </ul>
	 * 
	 * <i>Usage example:</i>
	 * <br>
	 * {@mythCodeExample <pre>
	 *    // the backend connection
	 *    IBackendConnection cmdConnection = ....;
	 * 
	 *    // read response		
	 *    IMythPacket resp = cmdConnection.readMessage();
	 *    
	 *    // create response-wrapper object
	 *    IRecorderInfo recorderInfo = ResponseUtils.readFrom(RecorderInfo.class, resp);
	 * </pre>}
	 * <br>
	 * 
	 * @param <E> 
	 * 		the type of the response object properties
	 * @param <Resp> 
	 * 		the type of the response object
	 * @param respClass 
	 * 		the class of the response object
	 * @param packet 
	 * 		the myth-packet
	 * @return 
	 * 		the instantiated class
	 * @throws IllegalArgumentException
	 * 		if the created class is not compatible with the given packet arguments.
	 */	
	public static final <E extends Enum<E>, Resp extends AMythResponse<E> & IMythResponse<E>> Resp readFrom(
		Class<Resp> respClass, IMythPacket packet
	) throws IllegalArgumentException {
		// the constructor of the msg 
		try {
			// first we test if there is an valueOf method
			final Method valueOf = getValueOf(respClass);
			if(valueOf != null) {
				@SuppressWarnings("unchecked")
				final Resp msgObject = (Resp) valueOf.invoke(null,packet);
				return msgObject;
			}

			// otherwise we try to use the proper constructor
			final Constructor<Resp> msgClassConstructor = respClass.getConstructor(new Class[] { 
				IMythPacket.class
			});
	
			// creating a new message instance
			final Resp msgObject = msgClassConstructor.newInstance(new Object[] {packet});        
			return msgObject;
		} catch (Exception e) {
			logger.log(Level.SEVERE,String.format(
				"Unable to create a response object '%s' from packet:\r\n%s",
				respClass,packet
			),e);
			return null; // TODO: should we throw an exception here?
		}
	}

	public static final <E extends Enum<E>, Resp extends AMythResponse<E>> Resp readFrom(
		ProtocolVersion protoVersion, Class<Resp> respClass, InputStream input
	) throws IOException {
		// read package
		final IMythPacket packet = PacketUtils.readFrom(protoVersion, input);
		
		// return response object
		return readFrom(respClass, packet);
	}

	public static final <E extends Enum<E>, Resp extends AMythResponse<E>> Resp readFrom(
		ProtocolVersion protoVersion, Class<Resp> respClass, String input
	) {		
		try {
			final InputStream in = new ByteArrayInputStream(input.getBytes("UTF-8"));			
			return readFrom(protoVersion, respClass, in);
		} catch (UnsupportedEncodingException e) {
			assert(false) : "this should never occure";
			return null;
		} catch (IOException e) {
			assert(false) : "this should never occure";
			return null;
		}		
	}


	public static final <E extends Enum<E>, S extends AMythResponse<E>, F extends Enum<F>, T extends AMythResponse<F>> void copyArguments(S source, T target) {
		// determine the protocol version to use
		final ProtocolVersion sourceProtocol = source.getVersionNr();
		final ProtocolVersion targetProtocol = target.getVersionNr();
		
		// get the property classes
		final Class<E> sourceProps = source.getPropertyClass();
		final Class<F> targetProps = target.getPropertyClass();
		
		// determine all available source- and target-property enums
		final Set<String> nextProgramProps = EnumUtils.getEnumNames(sourceProps,sourceProtocol);
		final Set<String> programInfoProps = EnumUtils.getEnumNames(targetProps,targetProtocol);
		
		// determine the properties to copy
		final HashSet<String> copyProps = new HashSet<String>(programInfoProps);
		copyProps.retainAll(nextProgramProps);
		
		// copy data
		for(String copyPropName : copyProps) {
			// determine the source- and target-property by name
			final E sourceProp = EnumUtils.getEnum(sourceProps,sourceProtocol,copyPropName);
			final F targetProp = EnumUtils.getEnum(targetProps,sourceProtocol,copyPropName);
			
			// copy value
			final String value = source.getPropertyValue(sourceProp);
			target.setPropertyValue(targetProp, value);
		}
	}
	
	/**
	 * This class should not be instantiated.
	 */
	private ResponseUtils() {
	}
}
