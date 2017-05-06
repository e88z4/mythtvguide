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
package org.jmythapi.protocol.request;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.utils.CommandUtils;

/**
 * The implementation of a MythTV-protocol command.
 */
public class AMythCommand implements IMythCommand, IVersionable {

	
	private final ProtocolVersion protoVersion;
	private final String cmdName; 
	private final List<String> cmdArgs;
	
	public AMythCommand(ProtocolVersion protoVersion, String commandName) {
		this(protoVersion, commandName,new ArrayList<String>(0));
	}
	
	public AMythCommand(ProtocolVersion protoVersion, String commandName, String... commandArgs) {
		this(protoVersion, commandName,(commandArgs==null)?null:Arrays.asList(commandArgs));
	}
	
	public AMythCommand(ProtocolVersion protoVersion, String commandName, List<String> commandArgs) {
		if (commandName == null || commandName.length() == 0) {
			throw new NullPointerException("The command name was not set properly.");
		}
		this.protoVersion = protoVersion;
		this.cmdName = commandName;
		this.cmdArgs = commandArgs;
	}
	
	public String getName() {
		return this.cmdName;
	}
	
	public List<String> getCommandArguments() {
		return this.cmdArgs;
	}
	
	public String getCommandArgument(int idx) {
		if (this.cmdArgs == null) return null;
		return this.cmdArgs.get(idx);
	}
	
	public int getCommandArgumentsLength() {
		return (this.cmdArgs==null)?0:this.cmdArgs.size();
	}
	
	public String toString() {
		try {
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			CommandUtils.writeTo(this, bout);
			return bout.toString("UTF-8");
		} catch (Exception e) {
			assert(false) : "this should never occure";
			return null;
		}
	}

	public ProtocolVersion getVersionNr() {
		return this.protoVersion;
	}
}
