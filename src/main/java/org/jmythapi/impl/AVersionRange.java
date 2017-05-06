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
package org.jmythapi.impl;

import org.jmythapi.IVersion;
import org.jmythapi.protocol.ProtocolVersion;


public abstract class AVersionRange <V extends Enum<V> & IVersion> {
	
	/**
	 * The lower-value of the version range.
	 */
	protected final V fromVersion;
	
	/**
	 * The higher-value of the version range.
	 */
	protected final V toVersion;
	
	/**
	 * Constructs a version-range object from two version objects
	 * @param from
	 * 		the lower bound
	 * @param to
	 * 		the upper bound
	 */	
	public AVersionRange(V from, V to) {
		this.fromVersion = from;
		this.toVersion = to;
	}
	
	/**
	 * Returns the version when a command or property was introduced.
	 * 
	 * @return 
	 * 		the version when a command or property was introduced.
	 */
	public V from() {
		return this.fromVersion;
	}
	
	/**
	 * Returns the version when a command or property was removed.
	 * <p> 
	 * For example a value of {@code 3} means, that the command or property 
	 * is supported till version {@code 2}.
	 * <p>
	 * The maximum supported version can be determined by calling 
	 * {@link ProtocolVersion#getMaxVersion()}.
	 * 
	 * @return 
	 * 		the protocol-version when a command or property was removed
	 */
	public V to() {
		return this.toVersion;
	}	
	
	/**
	 * Checks if the specified protocol-version is in this version-range.
	 * 
	 * @param protoVersion
	 * 		the protocol-version to check
	 * @return
	 * 		{@code true} if the given version is in the range.
	 */
	public boolean isInRange(V protoVersion) {
		return this.isInRange(protoVersion.getVersion());
	}
	
	/**
	 * Checks if the specified protocol-version is in this version-range.
	 * 
	 * @param protoVersionNr
	 * 		the protocol-version to check
	 * @return
	 * 		{@code true} if the given version is in the range.
	 */		
	public abstract boolean isInRange(int protoVersionNr);
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object paramObject) {
		if(!(paramObject instanceof AVersionRange)) return false;
		
		final AVersionRange other = (AVersionRange) paramObject;
		return(this.from().equals(other.from()) && this.to().equals(other.to()));
	}
	
	@Override
	public String toString() {
		final int length = Integer.toString(ProtocolVersion.values().length).length();
		return String.format(
			"[%0" + length + "d,%0" + length +"d)",
			Integer.valueOf(this.fromVersion.getVersion()),
			Integer.valueOf(this.toVersion.getVersion())
		);
	}	
}
