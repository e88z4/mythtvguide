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
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.impl.MythPacket;

/**
 * This class provides MythTV-packet related utility function.
 * 
 * <h3>Usage example:</h3>
 * 
 * {@mythCodeExample <pre>
 *    // write a packet to the output stream
 *    IMythPacket resp = sendableObj.getPacket();
 *    PacketUtils.writeTo(req, this.socketOutputStream);
 * 
 *    // read the response packet from the input stream
 *    IMythPacket resp = PacketUtils.readFrom(protoVersion, this.socketInputStream);
 * </pre>}
 * 
 * To write a request-packet to the stream function {@link #writeTo writeTo} is used. 
 * To read the response-packet from the stream function {@link #readFrom readFrom} is used.
 * 
 * @see IMythPacket
 */
public class PacketUtils {
	/**
	 * The length of the size string of a packet.
	 */
	public static final int SIZE_STRING_LENGTH = 8;
	
	/**
	 * A regular expression used to split packet arguments when
	 * receiving them over network.
	 */
	public static final String DELIM_REGEXP = "[\\[][\\]]:[\\[][\\]]";
	
	/**
	 * Converts an MythTV data-line into a list of arguments.
	 * <p>
	 * E.g. "{@code 1002[]:[]224[]:[]853[]:[]853}" will be converted to {@code [1002,224,853,853]}.
	 * 
	 * @param data 
	 * 		the data-string that should be parsed.
	 * @return 
	 * 		the data-items as list
	 */
	public static ArrayList<String> split(String data) {
		final String[] args = data.split(DELIM_REGEXP,-1);
		final ArrayList<String> responseArgs = new ArrayList<String>(Arrays.asList(args));
		return responseArgs;
	}

	/**
	 * Reads a MythTV-packet from a string.
	 * <p>
	 * 
	 * @param protoVersion 
	 * 		the protocol version of the packet
	 * @param inputString 
	 * 		the string representing the packet, e.g. "<code>13&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2[]:[]4[]:[]6</code>".
	 * @return 
	 * 		the read packet
	 * @throws IOException
	 * 		on communication errors
	 */
	public static final IMythPacket readFrom(ProtocolVersion protoVersion, String inputString) throws IOException {
		if (protoVersion == null) throw new NullPointerException("No protocol-version specified");
		else if (inputString == null) throw new NullPointerException("The input-string is null");
		
		ByteArrayInputStream byteInput = null;
		try {
			if(!inputString.matches("^\\d+\\s+.*")) {
				inputString = PacketUtils.formatPayloadSizeString(inputString.getBytes("UTF-8").length) + inputString;
			}			
			
			byteInput = new ByteArrayInputStream(inputString.getBytes("UTF-8")); 
			return PacketUtils.readFrom(protoVersion, byteInput);
		} finally {
			if (byteInput != null) byteInput.close();
		}
	}

	/**
	 * Reads a MythTV-packet from an input-stream.
	 * <p>
	 *  
	 * @param protoVersion 
	 * 		the protocol version of the packet
	 * @param input 
	 * 		the input stream containing the packet data
	 * @return 
	 * 		the read packet
	 * @throws IOException
	 * 		on communication errors
	 */
	public static final IMythPacket readFrom(ProtocolVersion protoVersion, InputStream input) throws IOException {
		if (protoVersion == null) throw new NullPointerException("No protocol-version specified");
		else if (input == null) throw new NullPointerException("The input-stream is null");
		
		// read the first 8 bytes containing the message length
		final int messageLength = readPayloadSize(input);
		
		// reading the payload
		final byte[] buf = new byte[messageLength];
	    int p = 0, c = 0;
	    do {
	    	c = input.read(buf, p, buf.length - p);
	    	if(c == -1) throw new EOFException("Unable to read the full packet.");
	    	p += c;
		} while (c > 0 && p < messageLength);
		
	    // Split the data into packet arguments
	    final String respLine = new String(buf,0,p,"UTF-8");
	    
	    // creating a new message class
		return new MythPacket(protoVersion, respLine);
	}
	
	public static final int readPayloadSize(InputStream input) throws IOException {
		// read the first 8 bytes containing the message length
		final byte[] buf = new byte[SIZE_STRING_LENGTH];
	    int p = 0, c = 0;
	    do {
	    	c = input.read(buf, p, buf.length - p);
	    	if(c == -1) throw new EOFException("Unable to read the packet payload size");
	    	p += c;
		} while (c > 0 && p < SIZE_STRING_LENGTH);	    
		
		// parsing the message length
		final String messageLengthString = new String(buf,"UTF-8").trim();
		final int messageLength = Integer.valueOf(messageLengthString).intValue();
		
		return messageLength;
	}
	
	/**
	 * Writes a MythTV-packet to an output-stream.
	 * <p>
	 * 
	 * @param packet 
	 * 		the packet that should be written to the stream.
	 * @param output 
	 * 		the output-stream to write the date to.
	 * @throws IOException
	 * 		on communication errors
	 */
	public static final void writeTo(IMythPacket packet, OutputStream output) throws IOException {
		if (packet == null) throw new NullPointerException("The packet is null");
		else if (output == null) throw new NullPointerException("The output-stream is null");
		
		final ByteArrayOutputStream payload = new ByteArrayOutputStream();
		
		final List<String> args = packet.getPacketArgs();
		if (args != null) {
			for (int i=0; i < args.size(); i++) {
				// getting the next argument. Null values are converted to empty strings.
				String arg = args.get(i);
				if (arg == null) arg = "";
				
				// writ the argument
				payload.write(arg.getBytes("UTF-8"));
				
				// write the separator 
				if (i < args.size()-1) payload.write(IMythPacket.DELIM.getBytes("UTF-8"));
			}
		}

		// writing the payload size
		final String payloadSize = formatPayloadSizeString(payload.size());
		output.write(payloadSize.getBytes("UTF-8"));
		
		// writing the payload
		output.write(payload.toByteArray());
		output.flush();
	}
	
	/**
	 * Generates the payload-size string.
	 * @param size
	 * 		the size of the payload
	 * @return
	 * 		the size string, e.g. "<code>13&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</code>"
	 */
	public static final String formatPayloadSizeString(int size) {
		final StringBuilder buff = new StringBuilder();
		buff.append(size);
		for (int i=buff.length(); i < SIZE_STRING_LENGTH; i++) buff.append(' ');
		return buff.toString();
	}
	
	/**
	 * This class should not be instantiated.
	 */
	private PacketUtils() {
	}
}
