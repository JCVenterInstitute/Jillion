package org.jcvi.jillion.sam;

import java.io.Closeable;
import java.io.IOException;

public interface SamWriter extends Closeable{

	void writeRecord(SamRecord record) throws IOException;
}
