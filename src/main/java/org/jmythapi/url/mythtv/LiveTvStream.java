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
package org.jmythapi.url.mythtv;

import java.io.IOException;
import java.io.InputStream;

import org.jmythapi.protocol.IRecorder;

public class LiveTvStream extends InputStream {
	
	private byte[] buffer = null;
	private int offset = -1;
	private int length = 0;
	
	private IRecorder mythRecorder = null;
	
	@SuppressWarnings("deprecation")
	public LiveTvStream(IRecorder mythRecorder) throws IOException {
		this.mythRecorder = mythRecorder;
				
		this.mythRecorder.annotateRingBuffer();
//		this.mythRecorder.seekRingBuffer(0,0,0);
	}

	@Override
	public int read() throws IOException {
		if (this.length == 0 || this.offset == this.length - 1) {
			this.buffer = new byte[65536]; 
//			this.length = this.mythRecorder.requestBlockRingBuffer(this.buffer,65536);
			this.offset = -1;
		} 
		
		if (this.length == -1) return -1;
		return this.buffer[++this.offset];
	}

}
