package org.jcvi.jillion_experimental.align.blast.btab;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion_experimental.align.blast.BlastHit;

public interface BtabWriter extends Closeable{

	void write(BlastHit hit) throws IOException;
}
