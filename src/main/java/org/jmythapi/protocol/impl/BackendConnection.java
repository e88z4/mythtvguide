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
package org.jmythapi.protocol.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_62;
import static org.jmythapi.protocol.request.IMythCommand.BACKEND_MESSAGE;
import static org.jmythapi.protocol.utils.PacketUtils.SIZE_STRING_LENGTH;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;
import org.jmythapi.protocol.UnsupportedCommandException;
import org.jmythapi.protocol.events.IMythEventPacketListener;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.AMythRequest;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.request.IMythRequest;
import org.jmythapi.protocol.utils.CommandUtils;
import org.jmythapi.protocol.utils.PacketUtils;

public class BackendConnection implements IVersionable, Closeable, IBackendConnection {
	/**
	 * A list of commands that are supported till the {@link IMythCommand#ANN} command is send.
	 */
	private static final HashSet<String> NON_PLAYBACKSOCK_COMMANDS = new HashSet<String>(Arrays.asList(new String[]{
		IMythCommand.MYTH_PROTO_VERSION,
		IMythCommand.ANN,
		IMythCommand.DONE
	}));

	/**
	 * For message logging
	 */
	private Logger msgLogger = Logger.getLogger(this.getClass().getName() + ".messages");	
	
	/**
	 * For logging
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * A stream to print received and transmitted messages to.
	 * This can be used for debugging.
	 */
	private PrintStream msgDebugStream;
	
	/**
	 * The currently used protocol-version
	 */
	private ProtocolVersion protoVersion = ProtocolVersion.getMaxVersion();
	
	/**
	 * The hostname of the MythTV backend, this connection is connected to
	 */
	private String mythHostName = null;
	
	/**
	 * The port this connection is connected to
	 */
	private int mythHostPort = DEFAULT_COMMAND_PORT;

	/**
	 * Indicates if the {@link IMythCommand#ANN} command as already send.
	 */
	private boolean ann = false;	
	
	/**
	 * The client-socket to the MythTV-backend
	 */
	private Socket socket = null;
	
	/**
	 * @see #socket
	 */
	private InputStream socketInputStream = null;
	
	/**
	 * @see #socket
	 */
	private OutputStream socketOutputStream = null;
	
	/**
	 * The connection connectTimeout
	 */
	private int connectTimeout = 10*60*1000;
	
	private int readTimeout = 10*60*1000;
	
	private LinkedBlockingQueue<IMythPacket> packetReaderQueue;
	
	private LinkedBlockingQueue<IMythPacket> eventReaderQueue;
	
	private BackendEventReader backendEventReader;
	
	private BackendEventDispatcher eventProcessorThread;
	
	/**
	 * A list of registered packet listeners.
	 */
	private List<IMythEventPacketListener> eventListener = new CopyOnWriteArrayList<IMythEventPacketListener>();
	
	public BackendConnection(String hostname) {
		this(hostname,DEFAULT_COMMAND_PORT);
	}
	
	public BackendConnection(String hostname, int port) {
		if (hostname == null || hostname.length() == 0) throw new IllegalArgumentException("Wrong hostname");
		if (port < 0) throw new IllegalArgumentException("Wrong host-port");
		
		this.mythHostName = hostname;
		this.mythHostPort = port;		
	}
	
	public void setInitialVersionNr(ProtocolVersion initialProtoVersion) throws IllegalStateException {
		if(this.isOpen()) throw new IllegalStateException("Connection already opened");
		this.protoVersion = initialProtoVersion;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setMsgDebugOut(PrintStream out) {
		this.msgDebugStream = out;
	}
	
	public String getHostname() {
		return this.mythHostName;
	}
	
	public int getPort() {
		return this.mythHostPort;
	}
	
	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}
	
	public void open() throws IOException {
		this.open(65536,true);
	}
		
	public void open(int streamBufferSize, boolean tcpNoDelay) throws IOException {		
		boolean negotiated = false;
		do {
	        // creating the socket
	        this.socket = new Socket();
	        
	        // creating a socket address
	        final InetSocketAddress address = new InetSocketAddress(this.mythHostName, this.mythHostPort);
	        
	        // trying to establish a connection to the address
	        this.socket.connect(address,this.connectTimeout);
	        
	        // setting socket connectTimeout and keep alive behaviour
	        this.socket.setSoTimeout(this.readTimeout); // waiting time for reads
//	        this.socket.setKeepAlive(true);
//	        this.socket.setTcpNoDelay(tcpNoDelay);
	        
	        // TODO: this may speedup connection
	         this.socket.setReceiveBufferSize(streamBufferSize);
	        
	        // getting sockets
	        this.socketInputStream = new BufferedInputStream(this.socket.getInputStream(),streamBufferSize);
	        this.socketOutputStream = new BufferedOutputStream(this.socket.getOutputStream(),streamBufferSize);
//	        this.socketInputStream = new BufferedInputStream(this.socket.getInputStream());
//	        this.socketOutputStream = new BufferedOutputStream(this.socket.getOutputStream());
	        
	        // negotiate protocol version to use
	        negotiated = this.negotiate();
		} while (!negotiated && this.protoVersion.compareTo(PROTO_VERSION_00) > 0);
	}
	
	public void close() {
		// check for an already closed connection
		if(this.isClosed()) {
			logger.info("Connection already closed");
			return;
		}
		
		// shutdown the packet reader thread
		if(this.backendEventReader != null) {
			try {
				if(this.backendEventReader.isAlive()) {
					// interrupt the reader thread
					this.backendEventReader.interrupt();
					
					// wait for the thread to stop
					this.backendEventReader.join(2000);
				}
				
				// clear variables
				this.backendEventReader = null;
				this.packetReaderQueue = null;
			} catch (Throwable e) {
				logger.log(Level.WARNING,"Unexpected error while terminating the packet-reader thread.",e);
			}
		}
		
		// shutdown the event reader thread
		if(this.eventProcessorThread != null) {
			try {
				if(this.eventProcessorThread.isAlive()) {
					// interrupt the reader thread
					this.eventProcessorThread.interrupt();
					
					// wait for the thread to stop
					this.eventProcessorThread.join(2000);
				}
				
				// clear variables
				this.eventProcessorThread = null;
				this.eventProcessorThread = null;
			} catch (Throwable e) {
				logger.log(Level.WARNING,"Unexpected error while terminating the packet-reader thread.",e);
			}
		}		
		
		// send out the DONE command
		try {
			this.writeMessage(new AMythRequest(new AMythCommand(this.protoVersion,IMythCommand.DONE)));
		} catch (EOFException e) {
			// this is ok in this situation
		} catch (Exception e) {
			logger.log(Level.WARNING,"Unexpected error while closing the connection.",e);
		} finally {
			try { 
				this.socketInputStream.close(); 
			} catch (IOException e) {
				// this is ok
			} finally {
				this.socketInputStream = null;
			}
			try { 
				this.socketOutputStream.close();
			} catch (IOException e) { 
				// this is ok
			} finally {
				this.socketOutputStream = null;
			}
			try { 
				this.socket.close();
			} catch (IOException e) { 
				// this is ok
			} finally {
				this.socket = null;
			}
		}
		
		
	}
	
	public boolean isClosed() {
		if (this.socket == null) return true;
		return !this.socket.isConnected() || this.socket.isClosed();
	}
	
	public boolean isOpen() {
		if(this.socket == null) return false;
		return this.socket.isConnected();
	}
	
	public boolean isAnnotated() {
		return this.ann;
	}
	
	protected boolean negotiate() throws IOException {
		// prepare the command args
		final List<String> args = new ArrayList<String>();
		args.add(Integer.toString(this.protoVersion.getVersion()));
		if(this.protoVersion.compareTo(PROTO_VERSION_62)>=0) {
			// since version 62 an additional token is required
			args.add(this.protoVersion.getToken());
		}
		
		// send the version info
		final IMythRequest req = new AMythRequest(
			new AMythCommand(
				this.protoVersion,
				IMythCommand.MYTH_PROTO_VERSION,
				args
			)
		);	
		this.writeMessage(req);
		
		// read the response
		final IMythPacket resp = this.readPacket();
		
		// check the response
		final String status = resp.getPacketArg(0);
		if (status.equals("ACCEPT")) {
			return true;
		} else if (status.equals("REJECT")) {
			// getting the backend protocol version
			final Integer backendProtoVersion = Integer.valueOf(resp.getPacketArg(1));
			
			// check if we support the given protocol version
			this.protoVersion = ProtocolVersion.valueOf(backendProtoVersion.intValue());
			if(this.protoVersion == null) {
				throw new ProtocolException(String.format(
					"The backend speaks unsupported protocol version: %d.",
					backendProtoVersion
				));
			}
			return false;
		} else {
			throw new ProtocolException("Unable to negotiate protocol version");
		} 
	}

	public IMythPacket readPacket() throws IOException {
		IMythPacket packet = null;
		
		if(this.packetReaderQueue != null) {
			// read the response packet from the queue
			packet = this.readPacketFromQueue();
		} else {
			// reading the response from the socket
			packet = this.readPacketFromSocket();
		}
		
//		final File tempFile = new File("src/test/resources" + System.currentTimeMillis() + ".txt");
//		final FileOutputStream fout = new FileOutputStream(tempFile);
//		PacketUtils.writeTo(packet,fout);
//		fout.close();
		
		return packet;
	}
	
	protected IMythPacket readPacketFromQueue() throws IOException {
		if(this.packetReaderQueue == null) throw new IllegalStateException("No packet queue available");
		
		try {
			return this.packetReaderQueue.take();
		} catch(InterruptedException e) {
			final IOException ioe = new IOException("Unexpected interruption while waiting for the response packet");
			ioe.initCause(e);
			throw ioe;
		}		
	}
	
	protected IMythPacket readPacketFromSocket() throws IOException {
		final IMythPacket resp = PacketUtils.readFrom(this.protoVersion, this.socketInputStream);
		if(msgLogger.isLoggable(Level.FINEST)) {
			msgLogger.finest("< " + resp.toString());
		}
		if(this.msgDebugStream != null) {
			this.msgDebugStream.println("< " + resp.toString());
		}
		return resp;
	}
	
	public boolean canReadPacket() throws IOException {
		if(this.packetReaderQueue == null) {
			final int bytesToRead = this.socketInputStream.available();
			return bytesToRead >= SIZE_STRING_LENGTH;
		} else {
			return !this.packetReaderQueue.isEmpty();
		}
	}
	
	public void writePacket(IMythPacket packet) throws IOException {
		// determine the name of the command to send
		String commandName = null;
		if(packet.getPacketArgsLength() > 0) {
			final String command = packet.getPacketArg(0);
			final int idx = command.indexOf(' ');
			if(idx == -1) {
				commandName = command;
			} else {
				commandName = command.substring(0,idx);
			}
		}
		
		// the command of the request must have a proper type
		if (!ann && !NON_PLAYBACKSOCK_COMMANDS.contains(commandName)) {
			throw new ProtocolException("Unexpected command. ANN command not sent so far.");
		} else if (ann && commandName.equals(IMythCommand.ANN)) {
			throw new ProtocolException("ANN command already sent.");
		}
		
		// write the packet
		writePacketInternal(packet);
		
		// remember annotation commands
		if (IMythCommand.ANN.equals(commandName)) this.ann = true;		
	}
	
	private void writePacketInternal(IMythPacket packet) throws IOException {
		// writing out the data to the stream
		PacketUtils.writeTo(packet, this.socketOutputStream);
		
		// log packet data
		if(msgLogger.isLoggable(Level.FINEST)) {
			msgLogger.finest("> " + packet.toString());
		}
		if(this.msgDebugStream != null) {
			this.msgDebugStream.println("> " + packet.toString());
		}				
	}
	
	public void writeMessage(IMythRequest msg) throws IOException {
		// the request must not be null
		if (msg == null) throw new NullPointerException("Command was null");
		
		// getting the command name
		final String commandName = msg.getCommand().getName();
		
		// the command of the request must have a proper type
		if (!ann && !NON_PLAYBACKSOCK_COMMANDS.contains(commandName)) {
			throw new ProtocolException("Unexpected command. ANN command not sent so far.");
		} else if (ann && commandName.equals(IMythCommand.ANN)) {
			throw new ProtocolException("ANN command already sent.");
		}
		
		// checking the version
		if (!commandName.equals(IMythCommand.MYTH_PROTO_VERSION) && msg.getVersionNr() != this.protoVersion) {
			throw new ProtocolException(String.format(
				"The request has a wrong version '%s'. The backend is speaking '%s'.",
				msg.getVersionNr(), this.protoVersion
			));
		}
		
		/*
		 * Check if the command is supported by the current backend
		 */	
		final ProtocolVersionRange versionRange = CommandUtils.getCommandVersionRange(commandName);
		if (!versionRange.isInRange(this.protoVersion)) {
			throw new UnsupportedCommandException(String.format(
				"The command '%s' is only supported in the protocol-version range %s.",
				commandName,
				versionRange.toString()
			));
		}
		
		// writing out the data to the stream
		final IMythPacket packet = msg.getPacket();
		this.writePacketInternal(packet);
		
		// remember annotation commands
		if (commandName.equals(IMythCommand.ANN)) this.ann = true;
	}
	
	public int readData(byte[] b, int offset, int len) throws IOException {
		return this.socketInputStream.read(b,offset,len);
	}
	
	public boolean canReadData() throws IOException {		
		return this.socketInputStream.available() > 0;
	}
	
	public void enableEventListening() {
		if(this.backendEventReader != null) return;
		
		// check for proper initialization
		if(!this.isOpen()) {
			throw new IllegalStateException("No connection established so far.");
		} else if(!this.isAnnotated()) {
			throw new IllegalStateException(String.format(
				"Unable to start event listening. No %s command was sent so far.",
				IMythCommand.ANN
			));
		}
		
		// initializing and starting the thread
		this.eventProcessorThread = new BackendEventDispatcher();
		this.eventProcessorThread.start();		
		this.backendEventReader = new BackendEventReader();
		this.backendEventReader.start();
	}
	
	public void addEventListener(IMythEventPacketListener listener) {
		this.eventListener.add(listener);
	}
	
	public void removeEventListener(IMythEventPacketListener listener) {
		this.eventListener.remove(listener);
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		
        buf.append("[").append(!this.socket.isConnected()?"CLOSED":"OPEN: ");
     
        if (!this.socket.isClosed()) {
        	buf.append(this.socket.getLocalSocketAddress())
        	.append(" -> ")
        	.append(this.socket.getRemoteSocketAddress());   
        }
        buf.append("] ");
		
		return buf.toString();		
	}
	
	private class BackendEventDispatcher extends Thread {
		public BackendEventDispatcher() {
			super("BackendEventDispatcher");
			
			// init events queue
			eventReaderQueue = new LinkedBlockingQueue<IMythPacket>();
		}
		
		@Override
		public void run() {
			while(!this.isInterrupted()) {
				try {
					// wait for the next event packet
					final IMythPacket event = eventReaderQueue.take();
					
					// dispatch event to all listeners
					for(IMythEventPacketListener listener : eventListener) {
						try {
							listener.fireEvent(event);
						} catch(Throwable x) {
							logger.log(Level.WARNING,String.format(
								"Unexpected %s while passing an event to the listener: %s",
								x.getClass().getName(),
								listener.getClass().getName()
							),x);
						}
					}
				} catch (InterruptedException e) {
					break;					
				} catch (Exception e) {
					if(this.isInterrupted()) {
						break;
					}
					logger.log(Level.SEVERE,"Unexpected error processing an event packet.",e);
				}
			}
		}
	}

	private class BackendEventReader extends Thread {
		public BackendEventReader() {
			super("BackendEventReader");
			
			// init the reader queue
			packetReaderQueue = new LinkedBlockingQueue<IMythPacket>();
		}
		
		@Override
		public void run() {
			long readStartTime = 0;
			while(!this.isInterrupted()) {
				try {
					// read next packet from socket
					readStartTime = System.currentTimeMillis();
					final IMythPacket mythPacket = readPacketFromSocket();
					
					// check packet type
					final String firstArg = mythPacket.getPacketArg(0);
					
					// insert events into event queue
					if(firstArg.equals(BACKEND_MESSAGE)) {
						eventReaderQueue.put(mythPacket);
					} 
					
					// insert response packets into response queue
					else {
						packetReaderQueue.put(mythPacket);
					}
				} catch (InterruptedException e) {
					break;
				} catch (SocketTimeoutException e) {
					// this should be ok
					break;
				} catch (Exception e) {
					// notify all listeners about the error
					final ClientErrorPacket errorPacket = new ClientErrorPacket(protoVersion,e);
					eventReaderQueue.add(errorPacket);
					
					// handle the exception
					if(this.isInterrupted() && SocketException.class.isAssignableFrom(e.getClass()) && "Socket closed".equals(e.getMessage())) {
						break;
					} else if (e instanceof SocketTimeoutException) {
						final long readDuration = System.currentTimeMillis() - readStartTime;
						if(readDuration >= readTimeout) {
							// this is ok
							continue;
						} else {
							logger.log(Level.SEVERE,"Unexpected read timeout. Connection seems to be broken. Exiting...",e);
							break;
						}
					} else if (e instanceof EOFException) {
						logger.log(Level.SEVERE,"Unexpected EOF. Connection seems to be broken. Exiting...");
						break;
					}
					logger.log(Level.SEVERE,"Unexpected error while reading packets from socket.",e);
				}
			}
		}
	}
}
