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
import java.net.URL;
import java.net.URLConnection;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IRecorder;

public class MythURLConnection extends URLConnection {

	@SuppressWarnings("unused")
	private IBackend mythBackend = null;
	private IRecorder mythRecorder = null;
	
	protected MythURLConnection(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {
		try {
//			// init backend
//			this.mythBackend = new Backend(this.url.getHost());
//
//			// establish a connection
//			this.mythBackend.connect();
//			
//			// annotate for playback
//			this.mythBackend.annotatePlayback();	
//			
//			// get the next free recorder
//			this.mythRecorder = this.mythBackend.getNextFreeRecorder();
//			if (this.mythRecorder == null) {
//				throw new IOException("No free recorder found");
//			}
//			
//			// setup ringbuffer
//			RingBuffer buf = this.mythRecorder.setupRingBuffer();
//			
//			// spawn live tv
//			if (!this.mythRecorder.spawnLiveTV()) {
//				throw new IOException("Unable to spawn live tv");
//			}
//			
//			// wait a few ms
//			Thread.sleep(200);
		} catch (Exception e) {
			if (e instanceof IOException) throw (IOException) e;
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new LiveTvStream(this.mythRecorder);
	}
	
	@Override
	public int getContentLength() {
		return -1;
	}
	
	@Override
	public String getContentType() {
		return "video/mpeg";
	}
	
	@Override
	public void setReadTimeout(int timeout) {
		super.setReadTimeout(timeout);
	}
}
