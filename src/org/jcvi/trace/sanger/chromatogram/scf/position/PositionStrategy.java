/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.position;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
/**
 * <code>PositionStrategy</code> hides the implementation
 * details of how the SCF file stores position information.
 * @author dkatzel
 *
 *
 */
public interface PositionStrategy {
    /**
     * Get the next position from the SCF data.
     * @param in {@link DataInputStream} of the SCF data.
     * @return a <code>short</code> which is the next position.
     * @throws IOException if there are any problems
     * fetching the next position.
     */
    short getPosition(DataInputStream in) throws IOException;
    /**
     * encode the given position into the given {@link ByteBuffer}.
     * @param position the position to encode.
     * @param buffer the ByteBuffer to write the encoded position.
     */
    void setPosition(short position,ByteBuffer buffer);
    /**
     * The maximum value a position is allowed to be for this
     * {@link PositionStrategy} implementation.
     * @return the max possible value a position can be.
     */
    int getMaxAllowedValue();
    /**
     * The number of bytes required for each encoded position.
     * @return a very small number, probably <code>1</code> or <code>2</code>.
     */
    byte getSampleSize();
}
