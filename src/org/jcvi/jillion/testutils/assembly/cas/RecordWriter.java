package org.jcvi.jillion.testutils.assembly.cas;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface RecordWriter extends Closeable{
	
	static final PhredQuality DEFAULT_QV  = PhredQuality.valueOf(30);
	
	void write(String id, NucleotideSequence seq) throws IOException;
	boolean canWriteAnotherRecord();
	
	File getFile();
}