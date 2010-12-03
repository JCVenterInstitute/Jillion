package org.jcvi.io;

import java.io.Closeable;
import java.util.Iterator;

import org.jcvi.fasta.FastaRecord;

public interface FastaRecordIterator extends Iterable<FastaRecord>, Closeable {

	Iterator<FastaRecord> iterator();
}
