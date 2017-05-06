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
package org.jmythapi.upnp;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_34;
import static org.jmythapi.protocol.ProtocolVersionInfo.DATE;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.utils.EnumUtils;

public class UpnpUtil {
	public static final String PROP_ST = "ST";
	public static final String PROP_LOCATION = "LOCATION";
	public static final String PROP_NT = "NT";
	public static final String PROP_NTS = "NTS";
	
	public static final String  BROADCAST_DEVICE_TYPE = "urn:schemas-mythtv-org:device:MasterMediaServer:1";
	public static final String  BROADCAST_IP = "239.255.255.250";
	public static final Integer BROADCAST_PORT = Integer.valueOf(1900);	
	
	/**
	 * For logging
	 */
	protected static final Logger logger = Logger.getLogger(EnumUtils.class.getName());	
	
	/**
	 * @param statusURI
	 * 		the URI to a backend {@code GetConnectionInfo} page.
	 * @throws IOException
	 * 		on communication errors
	 * 
	 * @see <a href="https://github.com/MythTV/mythtv/commit/85bb9769453db457b727e286e51306d32e87d874#diff-47">First Version</a> 
	 * @since {@mythProtoVersion 34}
	 */
	@MythProtoVersionAnnotation(from=PROTO_VERSION_34,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="85bb9769453db457b727e286e51306d32e87d874"),
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="13022"),
		@MythProtoVersionMetadata(key=DATE,value="2007-03-13")
	})
	public static Map<String,String> locateDatabase(URI statusURI, String pin) throws IOException {
		try {
			final URL connectionInfoURI = new URL(
				"http",
				statusURI.getHost(),
				statusURI.getPort(),
				"/Myth/GetConnectionInfo?Pin=" + pin
			);
			System.out.println(connectionInfoURI);
			
			final HttpURLConnection connection = (HttpURLConnection) connectionInfoURI.openConnection();
			
			
			InputStream data = null;
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				data = connection.getErrorStream();
			} else {
				data = connection.getInputStream();
			}
			
			final ByteArrayOutputStream response = new ByteArrayOutputStream();
			
			int c = -1;
			while((c = data.read()) != -1) {
				response.write(c);
			}
			data.close();
			
			final HashMap<String,String> values = new HashMap<String, String>();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				final Pattern pattern = Pattern.compile("<(\\w+)>([^>]*)</\\w+>");
				final Matcher matcher = pattern.matcher(response.toString());
				while(matcher.find()) {
					final String key = matcher.group(1);
					final String value = matcher.group(2);
					if(key.equals("Name") || key.equals("Port") || key.equals("Host") || key.equals("Password") || key.equals("UserName")) {
						values.put(key,value);
					}
				}
			} else {
				// values.put("Error",response.toString());
			}
				
			return values;
		} catch (Throwable e) {
			return Collections.emptyMap();
		}
	}
	
	public static URI waitForBackend(int timeout, String hostName) {
		MulticastSocket sock = null;
		try {			
	        final InetSocketAddress isa = new InetSocketAddress(BROADCAST_PORT);	        
	        final InetAddress group = InetAddress.getByName(BROADCAST_IP);
	        
	        logger.info(String.format(
	        	"Binding to multicast socket %s:%d ...",
	        	group.getHostAddress(),isa.getPort()
	        ));
	        sock = new MulticastSocket(null);        	        
	        sock.setReuseAddress(true);
	        sock.setBroadcast(true);
	        if(timeout > 0) {
	        	sock.setSoTimeout(timeout);
	        }
	        sock.bind(isa);
	        sock.joinGroup(group);
	        
	        logger.info("Doing an UPNP M-SEARCH ...");
	        final DatagramPacket pkt = buildDiscoveryMessage();        
	        sock.send(pkt);	        
	        
	        logger.info("Waiting for UPNP packets ...");
	        boolean found = false;
	        while(!found) {
	        	final long start = System.currentTimeMillis();
	        	
	        	// wait for a multicast packet
		        DatagramPacket rpkt = null;        
		        try {
		        	rpkt = new DatagramPacket(new byte[1024], 1024);
		            sock.receive(rpkt);
		        } catch (SocketTimeoutException e) {
		            sock.close();
		            logger.info("Timeout waiting for UPNP message");
		            break;
		        } 
		
		        // parsing the response
		        final Map<String,String> packetProps = parsePacket(rpkt);
	            logger.info("UPNP Message received: " + packetProps);
		        
		        if(packetProps.containsKey(PROP_LOCATION) && (
		        	// a notification message
		        	(packetProps.containsKey(PROP_NTS) && packetProps.get(PROP_NTS).equals("ssdp:alive") && 
		             packetProps.containsKey(PROP_NT) && packetProps.get(PROP_NT).equals(BROADCAST_DEVICE_TYPE)) ||
		            // the response to our M-SEARCH request
		            (packetProps.containsKey(PROP_ST) && packetProps.get(PROP_ST).equals(BROADCAST_DEVICE_TYPE))		           
		        )) {
		        	final URI location = URI.create(packetProps.get(PROP_LOCATION));
		        	if(hostName == null || location.getHost().equals(hostName)) return location;
		        	
		        	final InetAddress foundHost = InetAddress.getByName(location.getHost());
		        	final InetAddress requestedHost = InetAddress.getByName(hostName);
		        	if(foundHost.equals(requestedHost)) return location;
		        }
		        
		        // re-calculate timeout
		        if(timeout > 0) {
		        	timeout -= System.currentTimeMillis()-start;
		        	if(timeout <= 0) break;
		        	sock.setSoTimeout(timeout);
		        }
		        
	        }

	        // no backend found
	        return null;
		} catch(Throwable e) {
			logger.log(Level.WARNING,String.format(
				"Error while waiting for the backend %s to start.",
				hostName
			),e);
			return null;
        } finally {
        	if(sock != null) sock.close();
        }
	}
	
    public static Collection<URI> locateBackend() throws IOException {
    	final Integer broadcastTimeout = Integer.valueOf(8000);
    	
        final InetSocketAddress isa = new InetSocketAddress(BROADCAST_PORT);	        
        final InetAddress group = InetAddress.getByName(BROADCAST_IP);
        
        final MulticastSocket sock = new MulticastSocket(null);
        sock.setSoTimeout(broadcastTimeout);
        sock.setReuseAddress(true);
        sock.setBroadcast(true);
        sock.bind(isa);
        sock.joinGroup(group);
    	
        // sending the request
        logger.info("Doing an UPNP M-SEARCH ...");
        final DatagramPacket pkt = buildDiscoveryMessage();        
        sock.send(pkt);

        // receiving the response (if any)
        final Set<URI> backendLocations = new HashSet<URI>();
        boolean found = true;
        try {
        	logger.info("Waiting for UPNP packets ...");
	        do {
		        DatagramPacket rpkt = null;        
		        try {
		        	rpkt = new DatagramPacket(new byte[1024], 1024);
		            sock.receive(rpkt);
		        } catch (SocketTimeoutException e) {
		            sock.close();
		            logger.info("Timeout waiting for UPNP response");
		            found = false;
		            break;
		        } 
		
		        // parsing the response
		        final Map<String,String> packetProps = parsePacket(rpkt);
	            logger.info("UPNP Response received: " + packetProps);		        
		        
		        if(packetProps.containsKey(PROP_LOCATION) && packetProps.containsKey(PROP_ST) && packetProps.get(PROP_ST).equals(BROADCAST_DEVICE_TYPE)) {
		        	final URI backendLocation = URI.create(packetProps.get(PROP_LOCATION));
		        	if(backendLocations.add(backendLocation)) {
			        	logger.info(String.format(
			        		"New MythTV backend '%s' found.",
			        		backendLocation.getHost()
			        	));
		        	}
		        }
	        } while(found);
        } finally {
        	sock.close();
        }
        
        return backendLocations;
    }
    
    private static DatagramPacket buildDiscoveryMessage() throws UnsupportedEncodingException, UnknownHostException {
        final InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP);
    	
        // the upnp search request
        final String searchMessage = String.format(
        	"M-SEARCH * HTTP/1.1\r\n" +
        	"HOST: %s:%d\r\n" +
        	"MAN: \"ssdp:discover\"\r\n" + 
        	"MX: 2\r\n" + 
        	"ST: %s\r\n",
        	BROADCAST_IP,BROADCAST_PORT,
        	BROADCAST_DEVICE_TYPE
        );
        
        // building a udp packet
        final DatagramPacket pkt = new DatagramPacket(
        	searchMessage.getBytes("UTF-8"), 
        	searchMessage.getBytes("UTF-8").length, 
        	broadcastAddress, 
        	BROADCAST_PORT
        );
        
        return pkt;
    }
    
    public static Map<String,String> parsePacket(DatagramPacket rpkt) throws IOException {
    	if(rpkt == null || rpkt.getLength() == 0) return Collections.emptyMap();
    	
    	final Map<String,String> packetProps = new HashMap<String, String>();
    	
        String line = null;
        final BufferedReader reader = new BufferedReader(new StringReader(new String(rpkt.getData(), 0, rpkt.getLength())));        
        while((line = reader.readLine()) != null) {
        	int idx = line.indexOf(':');
        	if(idx == -1) continue;
        	
        	final String key = line.substring(0,idx).trim();
        	String value = line.substring(idx+1).trim();
        	if(value.length()==0)continue;
        	packetProps.put(key,value);

        	// extract mythtv version
        	if(key.equalsIgnoreCase("SERVER") && value.contains("MythTv")) {
        		idx = value.indexOf("MythTv");
        		value = value.substring(idx + 6).trim();
        		packetProps.put("MythTv",value);
        	}
        }
        
        return packetProps;
    }
}
