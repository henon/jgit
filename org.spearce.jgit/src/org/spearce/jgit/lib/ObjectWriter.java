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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class for writing loose objects.
 */
public class ObjectWriter extends DatabaseInserter {
	private final DatabaseInserter inserter;

	/**
	 * Construct an Object writer for the specified repository
	 * @param d
	 */
	public ObjectWriter(final Repository d) {
		inserter = d.getObjectDatabase().newInserter();
	}

	@Override
	public ObjectId insert(int type, byte[] data, int off, int len)
			throws IOException {
		return inserter.insert(type, data, off, len);
	}

	@Override
	public ObjectId insert(int type, long len, InputStream in)
			throws IOException {
		return inserter.insert(type, len, in);
	}

	@Override
	public ObjectId idFor(int type, byte[] data, int off, int len) {
		return inserter.idFor(type, data, off, len);
	}

	@Override
	public ObjectId idFor(int type, long len, InputStream in)
			throws IOException {
		return inserter.idFor(type, len, in);
	}

	/**
	 * Write a blob with the specified data
	 *
	 * @param b bytes of the blob
	 * @return SHA-1 of the blob
	 * @throws IOException
	 */
	public ObjectId writeBlob(final byte[] b) throws IOException {
		return insert(Constants.OBJ_BLOB, b);
	}

	/**
	 * Write a blob with the data in the specified file
	 *
	 * @param f
	 *            a file containing blob data
	 * @return SHA-1 of the blob
	 * @throws IOException
	 */
	public ObjectId writeBlob(final File f) throws IOException {
		final FileInputStream is = new FileInputStream(f);
		try {
			return writeBlob(f.length(), is);
		} finally {
			is.close();
		}
	}

	/**
	 * Write a blob with data from a stream
	 *
	 * @param len
	 *            number of bytes to consume from the stream
	 * @param is
	 *            stream with blob data
	 * @return SHA-1 of the blob
	 * @throws IOException
	 */
	public ObjectId writeBlob(final long len, final InputStream is)
			throws IOException {
		return insert(Constants.OBJ_BLOB, len, is);
	}

	/**
	 * Write a Tree to the object database.
	 *
	 * @param t
	 *            Tree
	 * @return SHA-1 of the tree
	 * @throws IOException
	 */
	public ObjectId writeTree(final Tree t) throws IOException {
		return inserter.insert(t);
	}

	/**
	 * Write a canonical tree to the object database.
	 *
	 * @param b
	 *            the canonical encoding of the tree object.
	 * @return SHA-1 of the tree
	 * @throws IOException
	 */
	public ObjectId writeCanonicalTree(final byte[] b) throws IOException {
		return insert(Constants.OBJ_TREE, b);
	}

	/**
	 * Write a Commit to the object database
	 *
	 * @param c
	 *            Commit to store
	 * @return SHA-1 of the commit
	 * @throws IOException
	 */
	public ObjectId writeCommit(final Commit c) throws IOException {
		return inserter.insert(c);
	}

	/**
	 * Write an annotated Tag to the object database
	 *
	 * @param c
	 *            Tag
	 * @return SHA-1 of the tag
	 * @throws IOException
	 */
	public ObjectId writeTag(final Tag c) throws IOException {
		return inserter.insert(c);
	}

	/**
	 * Compute the SHA-1 of a blob without creating an object. This is for
	 * figuring out if we already have a blob or not.
	 *
	 * @param len number of bytes to consume
	 * @param is stream for read blob data from
	 * @return SHA-1 of a looked for blob
	 * @throws IOException
	 */
	public ObjectId computeBlobSha1(final long len, final InputStream is)
			throws IOException {
		return inserter.idFor(Constants.OBJ_BLOB, len, is);
	}
}
