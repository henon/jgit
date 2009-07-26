/*
 * Copyright (C) 2007, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 * Copyright (C) 2009, Google Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name of the Git Development Community nor the
 *   names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.spearce.jgit.lib;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/** Support base class for implementations of {@link DatabaseInserter}. */
abstract class AbstractDatabaseInserter extends DatabaseInserter {
	/** Temporary working buffer for streaming data through. */
	private byte[] tempBuffer;

	/** Digest to compute the name of an object. */
	protected final MessageDigest md;

	/**
	 * Create a new inserter for a database.
	 * 
	 * @param cfg
	 *            repository (or user) configuration the database should honor
	 *            when working with buffers.
	 */
	protected AbstractDatabaseInserter(final Config cfg) {
		md = Constants.newMessageDigest();
	}

	/** @return a temporary byte array for use by the caller. */
	protected byte[] buffer() {
		if (tempBuffer == null)
			tempBuffer = new byte[8192];
		return tempBuffer;
	}

	/**
	 * Insert a single object into the store, returning its unique name.
	 * <p>
	 * This method is implemented in terms of creating an {@code InputStream}
	 * for the relevant region of {@code data} and passing that stream to the
	 * method {@link #insert(int, long, InputStream)}.
	 * 
	 * @param type
	 *            type code of the object to store.
	 * @param data
	 *            complete content of the object.
	 * @param off
	 *            first position within {@code data}.
	 * @param len
	 *            number of bytes to copy from {@code data}.
	 * @return the name of the object.
	 * @throws IOException
	 *             the object could not be stored.
	 */
	@Override
	public ObjectId insert(int type, byte[] data, int off, int len)
			throws IOException {
		return insert(type, len, new ByteArrayInputStream(data, off, len));
	}

	@Override
	public ObjectId idFor(int type, byte[] data, int off, int len) {
		md.reset();
		md.update(Constants.encodedTypeString(type));
		md.update((byte) ' ');
		md.update(Constants.encodeASCII(len));
		md.update((byte) 0);
		md.update(data, off, len);
		return ObjectId.fromRaw(md.digest());
	}

	@Override
	public ObjectId idFor(final int objectType, long length,
			final InputStream in) throws IOException {
		final byte[] buf = buffer();
		md.reset();
		md.update(Constants.encodedTypeString(objectType));
		md.update((byte) ' ');
		md.update(Constants.encodeASCII(length));
		md.update((byte) 0);
		while (length > 0) {
			final int n = in.read(buf, 0, (int) Math.min(length, buf.length));
			if (n < 0)
				throw new EOFException("Unexpected end of input");
			md.update(buf, 0, n);
			length -= n;
		}
		return ObjectId.fromRaw(md.digest());
	}
}
