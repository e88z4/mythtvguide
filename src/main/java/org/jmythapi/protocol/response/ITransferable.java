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
package org.jmythapi.protocol.response;

import java.io.Closeable;
import java.io.IOException;

import org.jmythapi.IPropertyAware;
import org.jmythapi.IVersionable;

public interface ITransferable <E extends Enum<E>> extends Closeable, IVersionable, IPropertyAware<E> {


	/**
	 * The properties to seek within a file during {@link IFileTransfer}.
	 */
	public static enum Seek {
		/**
		 * [00]
		 */
	    ABSOLUTE,
	    /**
	     * [01]
	     */
	    RELATIVE,
	    /**
	     * [02]
	     */
	    EOF    
	}	
	
	public int readBlock(byte[] buffer, int requestedSize) throws IOException;
	
	public abstract long seek(long currentPos, long newPos, ITransferable.Seek whence) throws IOException;
	
	public abstract boolean isOpen() throws IOException;
	
	public abstract boolean done() throws IOException;
	
	public abstract void close() throws IOException;
}
