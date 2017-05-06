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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;
import org.jmythapi.protocol.UnknownCommandException;
import org.jmythapi.protocol.UnsupportedCommandException;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtocolCmd;
import org.jmythapi.protocol.request.AMythCommand;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.request.IMythRequest;

/**
 * This class provides protocol command related utility functions.
 * <p>
 * This class can be used to:
 * <ul>
 * 	<li>Determine the protocol-version range of a command ({@link #getCommandVersionRange link})</li>
 * 	<li>Check if a given command is supported by any known protocol version ({@link #isKnownCommand link})</li>
 * 	<li>Print out all supported commands for a specific protocol version ({@link #printCommands link})</li>
 * 	<li>Read a command from a string ({@link #readFrom link})</li>
 * 	<li>Write a command to an output stream ({@link #writeTo link})</li>
 * </ul>
 * 
 * @see IMythCommand
 * @see MythProtoVersionAnnotation
 */
@SuppressWarnings("unchecked")
public class CommandUtils {
	public static final String DELIM = " ";
	
	/**
	 * For logging
	 */
	private static final Logger logger = Logger.getLogger(RequestUtils.class.getName());	
	
	/**
	 * A list of known MythTV-protocol commands.<p/>
	 * The key of this map is the command-name, the value is an array containing two elements. The first
	 * element is the {@link Field} defined in {@link IMythCommand}, the second is a {@link LinkedHashMap}
	 * containing all sub-commands that belong to the command.
	 */
	public static final LinkedHashMap<String,Object[]> COMMANDS = new LinkedHashMap<String,Object[]>();
	
	/*
	 * Init commands list
	 */
	static {
		// get all known commands
		java.lang.reflect.Field[] fields = IMythCommand.class.getFields();
		if (fields != null) {
			// loop through the commands and read details
			for (Field field : fields) {
				Object obj = null;
				try {
					obj = field.get(null);
					if (obj instanceof String) {
						// getting the protocol annotation info
						MythProtocolCmd cmdInfo = field.getAnnotation(MythProtocolCmd.class);		
						String parent = (cmdInfo==null)?"":cmdInfo.parentCommand();
						if (cmdInfo == null) {
							logger.warning("No annotation found for command: " + obj);
						} else {
							final MythProtoVersionAnnotation versionRange = cmdInfo.protoVersion();	
							logger.finer(String.format(
								"New command detected: %s [%d,%d).",
								((String)obj).toString(),
								versionRange.from().getVersion(),
								versionRange.to().getVersion()
							));
						}
						
						LinkedHashMap<String, Object[]> commandList = null;
						if (parent.equals("")) {
							commandList = COMMANDS;
						} else {
							// find the parent in the list
							if (!COMMANDS.containsKey(parent)) {
								COMMANDS.put(parent, new Object[]{null,new LinkedHashMap<String, Object[]>()});
							}
							commandList = (LinkedHashMap<String, Object[]>) COMMANDS.get(parent)[1];
						}
							
						if (commandList.containsKey((String)obj)) {
							logger.warning("Command " + obj + " already exists in commands list!");
							commandList.get((String)obj)[0] = field;
						} else {						
							// add command to list
							commandList.put((String)obj, new Object[]{field,new LinkedHashMap<String, Object[]>()});
						}
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE,"Unexpected error while determining default fields",e);
				}
			}
		}
	}	
	
	/**
	 * Prints all MythTV-protocol-commands that are supported by the given protocol-version to stdout.
	 * 
	 * @param protoVersion the version for which a commandlist should be printed. 
	 *        Use {@link ProtocolVersion#PROTO_VERSION_LATEST} for the current <i>trunk</i> 
	 *        version or {@code null} to print all known commands independently of the 
	 *        protocol version.  
	 */
	public static void printCommands(ProtocolVersion protoVersion) {
		printCommands(protoVersion,COMMANDS);
	}
	
	private static void printCommands(ProtocolVersion protoVersion, LinkedHashMap<String,Object[]> commands) {
		for (String cmdName : commands.keySet()) {
			// get the defined field
			Object[] values = commands.get(cmdName);
			
			// get the cmd info
			final MythProtocolCmd cmdInfo = ((Field)values[0]).getAnnotation(MythProtocolCmd.class);
			final MythProtoVersionAnnotation protoVersionRange = cmdInfo.protoVersion();
			ProtocolVersion from = (cmdInfo==null)? PROTO_VERSION_00:protoVersionRange.from();
			ProtocolVersion to   = (cmdInfo==null)? PROTO_VERSION_LATEST:protoVersionRange.to();
			
			if (protoVersion != null && protoVersion.equals(PROTO_VERSION_LATEST)) {
				// for the current trunk version ....
				if (!to.equals(PROTO_VERSION_LATEST)) continue;
			} else if (protoVersion != null) {
				// for a given protocol version ...
				if (from.compareTo(protoVersion) > 0) continue;
				else if (!to.equals(PROTO_VERSION_LATEST) && to.compareTo(protoVersion)<=0) continue;
			} 
			
			String parentCmd = (cmdInfo == null) ? "" : cmdInfo.parentCommand();			
			System.out.println(String.format(
				"%-50s [%2s,%2s]",
				(!parentCmd.equals("")?parentCmd+": ":"") + cmdName,
				Integer.toString(from.getVersion()),
				to.equals(PROTO_VERSION_LATEST) ? "-" : Integer.toString(to.getVersion())
			));
			
			// print sub-commands
			if (((LinkedHashMap<String,Object[]>)values[1]).size()>0) {
				printCommands(protoVersion,(LinkedHashMap<String,Object[]>)values[1]);
			}
		}
	}	
	
	/**
	 * Checks if the command of a request is known to be supported by any supported MythTV version.
	 * 
	 * @param request
	 * 		the request whose command should be checked
	 * @return
	 * 		{@code true} if the given command is supported or {@code false} otherwise.
	 */
	public static boolean isKnownCommand(IMythRequest request) {
		return isKnownCommand(request.getCommand().getName());
	}
	
	/**
	 * Checks if the command is known to be supported by any supported MythTV version.
	 * 
	 * @param command
	 * 		the command to check
	 * @return
	 * 		{@code true} if the given command is supported or {@code false} otherwise.
	 */
	public static boolean isKnownCommand(IMythCommand command) {
		return isKnownCommand(command.getName());
	}
	
	/**
	 * Checks if a command is known to be supported by any supported MythTV version.
	 * 
	 * @param commandName 
	 * 		the name of the command, e.g. {@code QUERY_RECORDER}
	 * @return 
	 * 		{@code true} if the given command is supported or {@code false} otherwise.
	 */
	public static boolean isKnownCommand(String commandName) {
		return COMMANDS.containsKey(commandName);
	}
	
	/**
	 * Returns the version-range the given command is supported in.
	 * <p>
	 * A command can only be used in the version range returned by this method. Otherwise an
	 * {@link UnsupportedCommandException} will be thrown by the backend-connection.
	 * <p>
	 * 
	 * <i>Usage example:</i>
	 * <br>
	 * 
	 * {@mythCodeExample <pre>
	 *    // the following will return the version range [9,-1)
	 *    ProtocolVersionRange versionRange = CommandUtils.getCommandVersionRange(IMythCommand.GET_FREE_RECORDER_COUNT);
	 *    System.out.println(versionRange);
	 * </pre>}
	 * <br>
	 * 
	 * In the above example the command {@code GET_FREE_RECORDER_COUNT} was introduced in protocol version {@code 09}
	 * and is supported up to the current protocol version.
	 * 
	 * @param commandName
	 * 		the command name
	 * @return
	 * 		the protocol version range of the command
	 * @throws UnknownCommandException
	 * 		if the command is not supported by any known protocol-version.
	 */
	public static ProtocolVersionRange getCommandVersionRange(String commandName) throws UnknownCommandException {
		// getting the command declaration
		final MythProtocolCmd cmdInfo = getCommandDeclaration(commandName,null);
		
		// getting the command version
		final MythProtoVersionAnnotation versionRangeAnnotation = (cmdInfo==null)? null : cmdInfo.protoVersion();
		final ProtocolVersionRange versionRange = (versionRangeAnnotation==null)
			? ProtocolVersionRange.DEFAULT_RANGE
			: new ProtocolVersionRange(versionRangeAnnotation);
		return versionRange;
	}
	
	public static MythProtocolCmd getCommandDeclaration(String commandName, String subCommandName) throws UnknownCommandException {
		if (commandName == null || commandName.length() == 0) {
			throw new IllegalArgumentException("The command-name must not be null or empty.");
		}		
		
		// getting metadata about the command
		Object[] cmdDetails = COMMANDS.get(commandName);
		if (cmdDetails == null) {
			throw new UnknownCommandException(String.format("Command '%s' not known.",commandName));
		}
		
		if(subCommandName != null && cmdDetails.length > 1) {
			final LinkedHashMap<String,Object[]> subCommands = (LinkedHashMap<String, Object[]>) cmdDetails[1];
			if(subCommands == null) return null;
			else if(!subCommands.containsKey(subCommandName)) return null;
			cmdDetails = subCommands.get(subCommandName);
		}
		
		// getting the command annotation
		final Field cmdDef = (Field) cmdDetails[0];
		final MythProtocolCmd cmdInfo = cmdDef.getAnnotation(MythProtocolCmd.class);
		return cmdInfo;
	}

	/**
	 * Reads a MythTV protocol command from a string.
	 * <p>
	 * The given command string is splitted into parts (using the separator {@link #DELIM}) and thereafter
	 * a {@link IMythCommand} object is created with the extracted command-name and -arguments.
	 * 
	 * <h4>Usage example:</h4>
	 * {@mythCodeExample <pre>
	 *  String commandString = "ANN Monitor MythClient 0";
	 *  IMythCommand command = CommandUtils.readFrom(PROTO_VERSION_56, commandString);
	 * </pre>}
	 * @param protoVersion
	 * 		the protocol version of the command
	 * @param commandString
	 * 		the command string, e.g. {@code ANN Monitor MythClient 0}
	 * @return
	 * 		a command object
	 * @throws IllegalArgumentException
	 * 		if the given command string is invalid
	 */
	public static final IMythCommand readFrom(ProtocolVersion protoVersion, String commandString) throws IllegalArgumentException {
		if (protoVersion == null) throw new NullPointerException("No protocol version specified.");
		else if (commandString == null) throw new IllegalArgumentException("The command string was null");
		
		// splitting the command into parts
		final String[] cmdParts = commandString.split(DELIM);
		
		// the first part is the command-name
		final String cmdname = cmdParts[0];
		
		// reading the command-arguments
		ArrayList<String> cmdArgs = null;
		if (cmdParts.length > 1) {
			String[] tmpArgs = new String[cmdParts.length-1];
			System.arraycopy(cmdParts, 1, tmpArgs, 0, tmpArgs.length);
			cmdArgs = new ArrayList<String>(Arrays.asList(tmpArgs));
		} else {
			cmdArgs = new ArrayList<String>();
		}
		
		// creating an object
		return new AMythCommand(protoVersion, cmdname, cmdArgs);
	}
	
	/**
	 * Writes the given command object to an output stream.
	 * 
	 * @param command
	 * 		the command to write out
	 * @param output
	 * 		the output stream.
	 * @throws UnsupportedEncodingException
	 * 		on encoding errors
	 * @throws IOException
	 * 		on communication errors
	 */
	public static final void writeTo(IMythCommand command, OutputStream output) throws UnsupportedEncodingException, IOException {
		final String cmdName = command.getName();
		final List<String> cmdArgs = command.getCommandArguments();
		
		// write command name
		output.write(cmdName.getBytes("UTF-8"));
		
		if (cmdArgs != null) {
			for (String arg : cmdArgs) {
				if (arg == null || arg.length() == 0) continue;
				
				// write deliminater
				output.write(CommandUtils.DELIM.getBytes("UTF-8"));
				
				// write arg-value
				output.write(arg.getBytes("UTF-8"));
			}
		}
		output.flush();
	}
}
