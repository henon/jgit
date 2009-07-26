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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.spearce.jgit.errors.ObjectWritingException;

/** Inserts objects into an existing {@code ObjectDatabase}. */
public abstract class DatabaseInserter {
	private static final byte[] htree = Constants.encodeASCII("tree");

	private static final byte[] hparent = Constants.encodeASCII("parent");

	private static final byte[] hauthor = Constants.encodeASCII("author");

	private static final byte[] hcommitter = Constants.encodeASCII("committer");

	private static final byte[] hencoding = Constants.encodeASCII("encoding");

	/**
	 * Write a Tree to the object database.
	 *
	 * @param t
	 *            Tree
	 * @return SHA-1 of the tree
	 * @throws IOException
	 */
	public final ObjectId insert(final Tree t) throws IOException {
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		final TreeEntry[] items = t.members();
		for (int k = 0; k < items.length; k++) {
			final TreeEntry e = items[k];
			final ObjectId id = e.getId();

			if (id == null)
				throw new ObjectWritingException("Object at path \""
						+ e.getFullName() + "\" does not have an id assigned."
						+ "  All object ids must be assigned prior"
						+ " to writing a tree.");

			e.getMode().copyTo(o);
			o.write(' ');
			o.write(e.getNameUTF8());
			o.write(0);
			id.copyRawTo(o);
		}
		return insert(Constants.OBJ_TREE, o.toByteArray());
	}

	/**
	 * Write a Commit to the object database
	 *
	 * @param c
	 *            Commit to store
	 * @return SHA-1 of the commit
	 * @throws IOException
	 */
	public final ObjectId insert(final Commit c) throws IOException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		String encoding = c.getEncoding();
		if (encoding == null)
			encoding = Constants.CHARACTER_ENCODING;
		final OutputStreamWriter w = new OutputStreamWriter(os, encoding);

		os.write(htree);
		os.write(' ');
		c.getTreeId().copyTo(os);
		os.write('\n');

		ObjectId[] ps = c.getParentIds();
		for (int i=0; i<ps.length; ++i) {
			os.write(hparent);
			os.write(' ');
			ps[i].copyTo(os);
			os.write('\n');
		}

		os.write(hauthor);
		os.write(' ');
		w.write(c.getAuthor().toExternalString());
		w.flush();
		os.write('\n');

		os.write(hcommitter);
		os.write(' ');
		w.write(c.getCommitter().toExternalString());
		w.flush();
		os.write('\n');

		if (!encoding.equals(Constants.CHARACTER_ENCODING)) {
			os.write(hencoding);
			os.write(' ');
			os.write(Constants.encodeASCII(encoding));
			os.write('\n');
		}

		os.write('\n');
		w.write(c.getMessage());
		w.flush();

		return insert(Constants.OBJ_COMMIT, os.toByteArray());
	}

	/**
	 * Write an annotated Tag to the object database
	 *
	 * @param c
	 *            Tag
	 * @return SHA-1 of the tag
	 * @throws IOException
	 */
	public final ObjectId insert(final Tag c) throws IOException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final OutputStreamWriter w = new OutputStreamWriter(os,
				Constants.CHARSET);

		w.write("object ");
		c.getObjId().copyTo(w);
		w.write('\n');

		w.write("type ");
		w.write(c.getType());
		w.write("\n");

		w.write("tag ");
		w.write(c.getTag());
		w.write("\n");

		w.write("tagger ");
		w.write(c.getAuthor().toExternalString());
		w.write('\n');

		w.write('\n');
		w.write(c.getMessage());
		w.close();

		return insert(Constants.OBJ_TAG, os.toByteArray());		
	}

	/**
	 * Insert a single object into the store, returning its unique name.
	 *
	 * @param type
	 *            type code of the object to store.
	 * @param data
	 *            complete content of the object.
	 * @return the name of the object.
	 * @throws IOException
	 *             the object could not be stored.
	 */
	public final ObjectId insert(final int type, final byte[] data)
			throws IOException {
		return insert(type, data, 0, data.length);
	}

	/**
	 * Insert a single object into the store, returning its unique name.
	 *
	 * @param objectType
	 *            type code of the object to store.
	 * @param objectData
	 *            complete content of the object.
	 * @param offset
	 *            first position within {@code objectData}.
	 * @param length
	 *            number of bytes to copy from {@code objectData}.
	 * @return the name of the object.
	 * @throws IOException
	 *             the object could not be stored.
	 */
	public abstract ObjectId insert(int objectType, byte[] objectData,
			int offset, int length) throws IOException;

	/**
	 * Insert a single object into the store, returning its unique name.
	 *
	 * @param objectType
	 *            type code of the object to store.
	 * @param length
	 *            number of bytes to copy from {@code in}.
	 * @param in
	 *            stream providing the object content. The caller is responsible
	 *            for closing the stream.
	 * @return the name of the object.
	 * @throws IOException
	 *             the object could not be stored, or the source stream could
	 *             not be read.
	 */
	public abstract ObjectId insert(int objectType, long length, InputStream in)
			throws IOException;

	/**
	 * Compute the name of an object, without inserting it.
	 *
	 * @param objectType
	 *            type code of the object to store.
	 * @param objectData
	 *            complete content of the object.
	 * @param offset
	 *            first position within {@code objectData}.
	 * @param length
	 *            number of bytes to copy from {@code objectData}.
	 * @return the name of the object.
	 */
	public abstract ObjectId idFor(int objectType, byte[] objectData,
			int offset, int length);

	/**
	 * Compute the name of an object, without inserting it.
	 *
	 * @param objectType
	 *            type code of the object to store.
	 * @param length
	 *            number of bytes to copy from {@code in}.
	 * @param in
	 *            stream providing the object content. The caller is responsible
	 *            for closing the stream.
	 * @return the name of the object.
	 * @throws IOException
	 *             the source stream could not be read.
	 */
	public abstract ObjectId idFor(int objectType, long length, InputStream in)
			throws IOException;
}
