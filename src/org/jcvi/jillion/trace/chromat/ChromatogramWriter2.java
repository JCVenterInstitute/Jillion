package org.jcvi.jillion.trace.chromat;

import java.io.Closeable;
import java.io.IOException;

public interface ChromatogramWriter2 extends Closeable{

	void write(Chromatogram c) throws IOException;
}
