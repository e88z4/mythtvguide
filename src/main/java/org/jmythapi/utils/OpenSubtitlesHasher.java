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
package org.jmythapi.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Hash code is based on Media Player Classic. In natural language it calculates: size + 64bit
 * checksum of the first and last 64k (even if they overlap because the file is smaller than
 * 128k).
 * <p>
 * This method was copied from <a href="http://trac.opensubtitles.org/projects/opensubtitles/wiki/HashSourceCodes">opensubtitles.org</a>.
 */
public class OpenSubtitlesHasher {

	/**
	 * Size of the chunks that will be hashed in bytes (64 KB)
	 */
	private static final int HASH_CHUNK_SIZE = 64 * 1024;


	public static String computeHash(File file) throws IOException {
		long size = file.length();
		long chunkSizeForFile = Math.min(HASH_CHUNK_SIZE, size);

		FileChannel fileChannel = new FileInputStream(file).getChannel();

		try {
			long head = computeHashForChunk(fileChannel.map(MapMode.READ_ONLY, 0, chunkSizeForFile));
			long tail = computeHashForChunk(fileChannel.map(MapMode.READ_ONLY, Math.max(size - HASH_CHUNK_SIZE, 0), chunkSizeForFile));

			return Long.toHexString(size + head + tail);
		} finally {
			fileChannel.close();
		}
	}


	public static String computeHash(InputStream stream, long length) throws IOException {

		int chunkSizeForFile = (int) Math.min(HASH_CHUNK_SIZE, length);

		// buffer that will contain the head and the tail chunk, chunks will overlap if length is smaller than two chunks
		byte[] chunkBytes = new byte[(int) Math.min(2 * HASH_CHUNK_SIZE, length)];

		DataInputStream in = new DataInputStream(stream);

		// first chunk
		in.readFully(chunkBytes, 0, chunkSizeForFile);

		long position = chunkSizeForFile;
		long tailChunkPosition = length - chunkSizeForFile;

		// seek to position of the tail chunk, or not at all if length is smaller than two chunks
		while (position < tailChunkPosition && (position += in.skip(tailChunkPosition - position)) >= 0);

		// second chunk, or the rest of the data if length is smaller than two chunks
		in.readFully(chunkBytes, chunkSizeForFile, chunkBytes.length - chunkSizeForFile);

		long head = computeHashForChunk(ByteBuffer.wrap(chunkBytes, 0, chunkSizeForFile));
		long tail = computeHashForChunk(ByteBuffer.wrap(chunkBytes, chunkBytes.length - chunkSizeForFile, chunkSizeForFile));

		return Long.toHexString(length + head + tail);
	}


	private static long computeHashForChunk(ByteBuffer buffer) {

		LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
		long hash = 0;

		while (longBuffer.hasRemaining()) {
			hash += longBuffer.get();
		}

		return hash;
	}

}
