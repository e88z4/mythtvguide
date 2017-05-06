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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmythapi.utils.EncodingUtils;

public class RequestUtils {
	/**
	 * For logging
	 */
	private static final Logger logger = Logger.getLogger(RequestUtils.class.getName());
	
    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        	logger.log(Level.SEVERE,"Unable to determine the clients hostname",e);
        	return "MythTV-API";
        }        
    }
    
    public static int addressToInt(InetAddress address) {
		final byte[] ip = address.getAddress();
		
		int addressInt = 0;
		for (int i=0; i<ip.length;i++) {
			final int ipPart = ip[i] & 0xff;
			final int shift = (int)Math.pow(256, (ip.length-1)-i);
			addressInt += ipPart * shift;					
		}
		return addressInt;
    }
    
    public static InetAddress intToAddress(int addressInt) throws UnknownHostException {
    	return InetAddress.getByAddress(new byte[]{
			(byte) ((addressInt >> 24) & 0xff),
			(byte) ((addressInt >> 16) & 0xff),
			(byte) ((addressInt >>  8) & 0xff),
			(byte) ((addressInt) 	   & 0xff)
		});
    }
    
    public static int networkPrefixToNetmask(int prefixLength) {
		int netmask = 0;
		
        for (int j = 0; j < prefixLength; ++j) {
            netmask |= (1 << 31-j);
        }  
        
        return netmask;
    }
    
    public static InetAddress getNetworkAddress(InterfaceAddress interfaceAddress) throws UnknownHostException {
		final InetAddress ipAddress = interfaceAddress.getAddress();
		if(!(ipAddress instanceof Inet4Address)) throw new RuntimeException("Only IPv4 supported.");
		
		// convert the ip address to an int
		final int ipAddressInt = addressToInt(ipAddress);
		
		// getting the network mask
		final int networkPrefixLength = interfaceAddress.getNetworkPrefixLength();
		final int netmask = networkPrefixToNetmask(networkPrefixLength);
		
		// calculate the network address
		int networkAddressInt = (int)ipAddressInt & netmask;
		return intToAddress(networkAddressInt);
    }
    
    /**
     * TODO: this seems only to work for a local machine
     */    
    public static String getMacAddress(String ipAddress) throws IOException {
    	try {
    		// getting the IP
    		final InetAddress address = InetAddress.getByName(ipAddress);
    		
    		// getting the interface
    		final NetworkInterface ni = NetworkInterface.getByInetAddress(address);
    		if(ni == null) {
    			final String errorMsg = "Unable to determine a network-interface for address: " + address;
    			logger.log(Level.WARNING,errorMsg);
    			throw new IOException(errorMsg);
    		}
    		
    		final byte[] mac = ni.getHardwareAddress();
    		if(mac == null) {
    			final String errorMsg = "Unable to determine the hardware address for interface: " + ni;
    			logger.log(Level.WARNING,errorMsg);
    			throw new IOException(errorMsg);
    		}
    		
    		final StringBuilder macAddr = new StringBuilder();
    		for (int i = 0; i < mac.length; i++) {
    			macAddr.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
    		}
    		return macAddr.toString();
    	} catch (Exception e) {
    		logger.log(Level.SEVERE,e.getMessage(),e);
    		throw new IOException(e.getMessage());
    	}
    }    
    
    /**
     * Parts of this function was copied from:
     * http://coding.derkeiler.com/Archive/Java/comp.lang.java.help/2008-01/msg00013.html
     * @param networkAddress
     * 		the network address
     * @param macAddress
     * 		the mac address
     * @throws UnknownHostException
     * 		if the network address can not be resolved 
     */
    public static void wakeOnLan(String networkAddress, String macAddress) throws UnknownHostException {
    	final InetAddress address = InetAddress.getByName(networkAddress);
    	wakeOnLan(address, macAddress);
    }
    
    public static void wakeOnLan(InetAddress networkAddress, String macAddress) {
    	final int PORT = 9;

    	try {
    		final byte[] macBytes = getMacBytes(macAddress);
    		final byte[] bytes = new byte[6 + 16 * macBytes.length];
    		for (int i = 0; i < 6; i++) {
    			bytes[i] = (byte) 0xff;
    		}
    		for (int i = 6; i < bytes.length; i += macBytes.length) {
    			System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
    		}
    		
    		final DatagramPacket packet = new DatagramPacket(bytes,bytes.length, networkAddress, PORT);
    		final DatagramSocket socket = new DatagramSocket();
    		socket.send(packet);
    		socket.close();

    		logger.fine(String.format(
    			"WOL packet sent to mac address %s and network %s",
    			macAddress, networkAddress
    		));
    	} catch (Exception e) {
    		logger.log(Level.SEVERE,"Unable to send WOL packet.",e);
    	}

    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
    	byte[] bytes = null;
    	try {
	    	final String[] hex = macStr.split("(\\:|\\-)");
	    	if (hex.length != 6) {
	    		throw new IllegalArgumentException("Invalid MAC address.");
	    	}
	    	
	    	bytes = new byte[6];    	
    		for (int i = 0; i < 6; i++) {
    			bytes[i] = (byte) Integer.parseInt(hex[i], 16);
    		}
    	} catch (NumberFormatException e) {
    		throw new IllegalArgumentException(String.format(
    			"Invalid hex digit in MAC address '%s'.",macStr
    		));
    	}
    	return bytes;
    }
    
    public static Map<InetAddress,String> getArpCache() throws IOException, InterruptedException {
    	final String systemName = System.getProperty("os.name");
    	String arpCommand;
    	
    	// determine the command to use
    	if("Linux".equalsIgnoreCase(systemName)) {
    		arpCommand = "arp -n";
    	} else {
    		arpCommand = "arp -a";
    	}
    	
    	// execute the command
    	final Runtime runtime = Runtime.getRuntime();
    	final Process process = runtime.exec(arpCommand);
    	process.waitFor();
    	
    	final StringBuilder out = new StringBuilder();
    	final BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
    	String line = null;
    	while((line = buf.readLine())!=null) {
    		out.append(line).append("\n");
    	}
    	
    	// parse the data
    	Map<InetAddress,String> arpCache = null;
    	if("Linux".equals(systemName)) {
    		arpCache = parseArpCacheLinux(out.toString());
    	} else {
    		arpCache = parseArpCacheWindows(out.toString());
    	}
    	
    	return arpCache;
    }
    
    public static Map<InetAddress,String> parseArpCacheWindows(String arpCacheData) {
    	final Pattern pattern = Pattern.compile("\\s*(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+(\\w+-\\w+-\\w+-\\w+-\\w+-\\w+)\\s+");
    	return parseArpCache(arpCacheData, pattern);
    }
    
    public static Map<InetAddress,String> parseArpCacheLinux(String arpCacheData) {
    	final Pattern pattern = Pattern.compile("\\s*(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+\\w+\\s+(\\w+:\\w+:\\w+:\\w+:\\w+:\\w+)\\s+");
    	return parseArpCache(arpCacheData, pattern);
    }    
    
    public static Map<InetAddress,String> parseArpCache(String arpCacheData, Pattern pattern) {    
    	if(arpCacheData == null) return Collections.emptyMap();
    	
    	try {
	    	final HashMap<InetAddress,String> arpCache = new HashMap<InetAddress, String>();

	    	final BufferedReader reader = new BufferedReader(new StringReader(arpCacheData));	    	
	    	String line = null;
	    	
	    	while((line = reader.readLine())!= null) {    	
		    	final Matcher m = pattern.matcher(line);
		    	if(m.find()) {
		    		try { 
			    		final String ipAddressString = m.group(1);
			    		final InetAddress ipAddress = InetAddress.getByName(ipAddressString);
			    		
			    		String macAddress = m.group(2);
			    		macAddress = macAddress.replace('-',':');    		
			    		arpCache.put(ipAddress, macAddress);
		    		} catch (UnknownHostException e) {
		    			// ignore this
		    		}
		    	}
	    	}
	    	
	    	return arpCache;
    	} catch(IOException e) {
    		// can be ignored
    		return Collections.emptyMap();
    	}
    }
    
    public static String getChainID() {
    	return getChainID("livetv");
    }
    
    public static String getChainID(String chainPrefix) {
    	return String.format(
    		"%s-%s-%s",
    		chainPrefix,
    		getHostname(),
    		EncodingUtils.formatDateTime(new Date(), false)
    	);
    }
}
