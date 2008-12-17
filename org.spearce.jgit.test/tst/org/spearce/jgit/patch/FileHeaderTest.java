		assertEquals(-1, fh.parseGitFileName(0, fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		final FileHeader fh = data("a/ b/");
		assertEquals(-1, fh.parseGitFileName(0, fh.buf.length));
		final FileHeader fh = data("\n");
		assertEquals(-1, fh.parseGitFileName(0, fh.buf.length));
		final FileHeader fh = data("\n\n");
		assertEquals(1, fh.parseGitFileName(0, fh.buf.length));
		assertEquals(gitLine(name).length(), fh.parseGitFileName(0,
				fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		assertTrue(fh.parseGitFileName(0, fh.buf.length) > 0);
		assertFalse(fh.hasMetaDataChanges());
		assertEquals(gitLine(name).length(), fh.parseGitFileName(0,
				fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		assertEquals(dqGitLine(dqName).length(), fh.parseGitFileName(0,
				fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		assertEquals(dqGitLine(dqName).length(), fh.parseGitFileName(0,
				fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		assertEquals(gitLine(name).length(), fh.parseGitFileName(0,
				fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		assertEquals(header.length(), fh.parseGitFileName(0, fh.buf.length));
		assertFalse(fh.hasMetaDataChanges());
		assertSame(FileHeader.PatchType.UNIFIED, fh.getPatchType());
		assertTrue(fh.hasMetaDataChanges());
		assertSame(FileMode.MISSING, fh.getOldMode());
		assertSame(FileHeader.PatchType.UNIFIED, fh.getPatchType());
		assertTrue(fh.hasMetaDataChanges());
		assertSame(FileMode.MISSING, fh.getNewMode());
		assertSame(FileHeader.PatchType.UNIFIED, fh.getPatchType());
		assertTrue(fh.hasMetaDataChanges());
		int ptr = fh.parseGitFileName(0, fh.buf.length);
		ptr = fh.parseGitHeaders(ptr, fh.buf.length);
		assertSame(FileHeader.PatchType.UNIFIED, fh.getPatchType());
		assertTrue(fh.hasMetaDataChanges());
		int ptr = fh.parseGitFileName(0, fh.buf.length);
		ptr = fh.parseGitHeaders(ptr, fh.buf.length);
		assertSame(FileHeader.PatchType.UNIFIED, fh.getPatchType());
		assertTrue(fh.hasMetaDataChanges());
		int ptr = fh.parseGitFileName(0, fh.buf.length);
		ptr = fh.parseGitHeaders(ptr, fh.buf.length);
		assertSame(FileHeader.PatchType.UNIFIED, fh.getPatchType());
		assertTrue(fh.hasMetaDataChanges());
		assertFalse(fh.hasMetaDataChanges());
		assertFalse(fh.hasMetaDataChanges());
		assertFalse(fh.hasMetaDataChanges());
		assertFalse(fh.hasMetaDataChanges());
		int ptr = fh.parseGitFileName(0, fh.buf.length);
		ptr = fh.parseGitHeaders(ptr, fh.buf.length);