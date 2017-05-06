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
package org.jmythapi.protocol;

import java.io.IOException;
import java.io.PrintStream;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.events.IMythEventPacketListener;
import org.jmythapi.protocol.request.IMythRequest;
import org.jmythapi.protocol.response.IMythResponse;
import org.jmythapi.protocol.utils.PacketUtils;

/**
 * This interface represents a connection to a MythTV-backend, -recorder or -encoder.
 * <p>
 * It is used by the {@link IBackend}, {@link IRecorder} or {@link IRemoteEncoder} interfaces to send commands to a backend and 
 * to receive the response messages. But it is also possible to create the protocol packets directly and to send them over 
 * a backend connection to the MythTV backend.<br>
 * See {@link IMythPacket} for a detailed description how the content of a packet needs to be structured.
 * 
 * <h3>Using Request/Response Messages or Packets</h3>
 * 
 * When using a backend connection, you can work with packets or messages.
 * <p>
 * A request message provide a more structured to set request arguments. 
 * A response message has additional advantages. All response messages are {@link IPropertyAware property-aware}. 
 * This means that each type of response message has an enumeration property class, describing at which position in the response-arguments list, 
 * the value for a specific property can be found. Additionally for each property a {@link MythProtoVersionAnnotation version-range} is specified,
 * describing in which protocol version a specific property is part of a response. 
 * <p>
 * Read {@link IMythRequest} and {@link IMythResponse} for detailed informations about request- and response-messages, and {@link MythProtoVersionAnnotation} for 
 * more infos about protocol versions.
 * <p>
 * See <a href='#usage_packets'>here</a> for an example how to read request- and response-packets.<br>
 * See <a href='#usage_messages'>here</a> for an example how to read request- and response-messages.<br>
 * 
 * <h3>Usage Examples:</h3>
 * <h4><a name='usage_packets'>Writing request- and read response-packets:</a></h4>
 * 
 * The following example shows how packets can be written to and read from a backend connection.
 * 
 * {@mythCodeExample <pre>
 *   // establish a connection 
 *   IBackendConnection connection = new BackendConnection("mythbox",6543);
 *   connection.open();
 *   
 *   // annotate the client as monitoring client
 *   connection.writePacket(new MythPacket(connection.getVersionNr(),"ANN Monitor MythClient 0"));
 *   
 *   // check the response
 *   IMythPacket annResponse = connection.readPacket();
 *   if(!annResponse.getPacketArg(0).equals("OK")) &#123;
 *      System.err.println("ANN command failed");
 *   &#125; else &#123;
 *      // get the next free recorder
 *      connection.writePacket(new MythPacket(connection.getVersionNr(),new String[]&#123;
 *         // the command (incl. space separated command arguments, if required)
 *         "GET_NEXT_FREE_RECORDER",
 *         // additional request arguments
 *         "-1"
 *      &#125;));
 *      IMythPacket recorder = connection.readPacket();
 *      if(recorder.getPacketArg(1).equals("nohost")) &#123;
 *      	System.out.println("No free recoder available");
 *      &#125; else &#123;
 *          // do something with the recorder ....
 *          System.out.println("Next free recorder is: " + recorder.getPacketArg(0));
 *      &#125;
 *   &#125;
 *   
 *   // closing the connection
 *   connection.close();	
 * </pre>}
 * 
 * In the above example the following packets will be send and received.
 * 
 * {@mythProtoExample notitle
 * Establish a connection
 * 21      MYTH_PROTO_VERSION 65
 * 13      ACCEPT[]:[]65
 * Annotate the client as monitoring client
 * 24      ANN Monitor MythClient 0
 * 2       OK
 * Get the next free recorder
 * 29      GET_NEXT_FREE_RECORDER[]:[]-1
 * 26      1[]:[]192.168.0.2[]:[]6543
 * Close the connection
 * 4       DONE
 * }
 * 
 * <h4><a name='usage_messages'>Write request- and read response messages:</a></h4>
 * 
 * To make reading and writing of request and response packets more easy, it is also possible
 * to send {@link IMythRequest request messages} to the backend and to convert response packets 
 * into more easily to read {@link IMythResponse response messages}.
 * 
 * {@mythCodeExample <pre>
 *     // get the next free recorder
 *    connection.writeMessage(new AMythRequest(
 *       // the command (and additional command arguments, if any)
 *       new AMythCommand(
 *          connection.getVersionNr(),
 *          "GET_NEXT_FREE_RECORDER"
 *       ),
 *       // additional request arguments
 *       "-1"
 *    ));
 * 
 *    // read the response packet
 *    IMythPacket resp = connection.readPacket();
 *    
 *    // convert it into a proper response object
 *    RecorderInfo recorder = ResponseUtils.readFrom(RecorderInfo.class, resp);
 *    if(recorder == null) &#123;
 *       System.out.println("No free recoder available");
 *    &#125; else &#123;
 *       // do something with the recorder ....
 *       System.out.println("Next free recorder is: " + recorder.getRecorderID());
 *    &#125;
 * </pre>}
 * <br>
 * @see IMythPacket
 */
public interface IBackendConnection extends IVersionable {

	/**
	 * The default MythTV-backend port.
	 */
	public static final int DEFAULT_COMMAND_PORT = 6543;

	
	public int getConnectTimeout();

	public void setConnectTimeout(int connectTimeout);

	public int getReadTimeout();

	public void setReadTimeout(int readTimeout);
	
	/**
	 * Sets a print stream used to log packets.
	 * <p>
	 * This method sets a print-stream that is used to log
	 * incoming and outgoing packets.<br>
	 * This is mainly used for debugging.
	 * 
	 * <h4>Usage example</h4>
	 * Printing the packets to std-out.
	 * {@mythCodeExample <pre>
	 *    backendConnection.setMsgDebugOut(System.out);
	 * </pre> }
	 * 
	 * Due to the above statement the following will be printed to std-out, e.g.
	 * <pre>
	 * </pre>
	 * 
	 * @param out
	 * 		a print-stream to log messages.
	 */
	public void setMsgDebugOut(PrintStream out);
	
	/**
	 * Sets the initla protocol-version to use.
	 * <p>
	 * This function sets the initial protocol-version that will be used by the connection
	 * during version negotiation in the course of {@link #open()}.
	 * <p>
	 * This is mainly usefull to speedup connection establishing.
	 * 
	 * @param initialProtoVersion
	 * 		the initial protocol version.
	 * 
	 * @throws IllegalStateException
	 * 		if the connection is already opened.
	 */
	public void setInitialVersionNr(ProtocolVersion initialProtoVersion) throws IllegalStateException;	
	
	/**
	 * Gets the host name of the backend. 
	 * @return
	 * 		the host name of the backend.
	 */
	public abstract String getHostname();

	/**
	 * Gets the port of the backend.
	 * 
	 * @return
	 * 		the port of the backend.
	 */
	public abstract int getPort();

	/**
	 * Opens the connection.
	 * <p>
	 * This function opens the connection to the MythTV-backend and negotiates the protocol version to use.
	 * 
	 * @throws IOException
	 * 		on communication errors.
	 */
	public abstract void open() throws IOException;
	
	/**
	 * Opens the connection.
	 * <p>
	 * This function opens the connection to the MythTV-backend and negotiates the protocol version to use.
	 * <p>
	 * 
	 * @param streamBufferSize
	 * 		the stream buffer size to use
	 * @param tcpNoDelay
	 * @throws IOException
	 * 		on communication errors
	 */	
	public void open(int streamBufferSize, boolean tcpNoDelay) throws IOException;

	/**
	 * Closes the connection to the MythTV-backend.
	 */
	public abstract void close();

	/**
	 * Gets the curernt connection state.
	 * @return
	 * 		{@code true} if the connection was not established so far or is already closed.
	 */
	public abstract boolean isClosed();

	/**
	 * Receives the next packet from the backend.
	 * 
	 * @return
	 * 		the next read packet.
	 * @throws IOException
	 * 		on communication errors.
	 */
	public abstract IMythPacket readPacket() throws IOException;
	
	/**
	 * Checks if at least {@link PacketUtils#SIZE_STRING_LENGTH} bytes 
	 * can be read from the underlying input stream without blocking.
	 * <p>
	 * This function can be used to check if we should start reading 
	 * the next packet. 
	 * 
	 * @return
	 * 		{@code true} if the next packet can be read
	 * @throws IOException
	 * 		on communication errors
	 */
	public boolean canReadPacket() throws IOException;
	
	/**
	 * Sends a packet to the backend.
	 * 
	 * @param packet
	 * 		to packet to transfer
	 * @throws IOException
	 * 		on communication errors.
	 */
	public abstract void writePacket(IMythPacket packet) throws IOException;

	/**
	 * Sends a request-message to the backend.
	 * 
	 * @param msg 
	 * 		the request message to be send
	 * @throws IOException
	 * 		on communication errors
	 * @throws UnknownCommandException 
	 * 		if the given command is unknown
	 * @throws UnsupportedCommandException 
	 * 		if the given command is not supported by the given protocol version
	 */
	public abstract void writeMessage(IMythRequest msg) throws IOException;

	/**
	 * Reads bytes from the socket.
	 * 
	 * @param b
	 * 		the byte array to fill with data
	 * @param offset
	 * 		to offset used to start writing into the array
	 * @param len
	 * 		the amount of bytes to read
	 * @return
	 * 		the amount of read bytes.
	 * @throws IOException
	 * 		on communication errors
	 */
	public abstract int readData(byte[] b, int offset, int len) throws IOException;

	/**
	 * Checks if data is available to read without blocking.
	 * 
	 * @return
	 * 		{@code true} if at least one byte is available to read without blocking.
	 * @throws IOException
	 * 		on communication errors
	 */
	public boolean canReadData() throws IOException;
	
	/**
	 * Enables event listening.
	 * <p>
	 * This function starts a new thread, which reads incoming {@link IMythPacket} and passes them
	 * to event listeners, if the received packets are event packets.
	 */
	public void enableEventListening();
	
	/**
	 * Registers a new event listener.
	 * 
	 * @param listener
	 * 		the listener to register
	 */
	public void addEventListener(IMythEventPacketListener listener);
	
	/**
	 * Unregisters an event listener.
	 * 
	 * @param listener
	 * 		the listener to unregister.
	 */
	public void removeEventListener(IMythEventPacketListener listener);
}