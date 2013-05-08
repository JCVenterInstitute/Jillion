package org.jcvi.jillion.trace.chromat;

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code ChromatogramWriter} can write
 * {@link Chromatogram} objects to some kind out
 * output.
 * Implementations may encode the chromatogram
 * in different ways.
 * @author dkatzel
 *
 *
 */
public interface ChromatogramWriter extends Closeable{
	 /**
     * Writes the given {@link Chromatogram}.
     * @param chromatogram the {@link Chromatogram} to write.
     * @throws IOException if there are any problems encoding the chromatogram
     * or any problems writing to the output.
     */
	void write(Chromatogram c) throws IOException;
}
