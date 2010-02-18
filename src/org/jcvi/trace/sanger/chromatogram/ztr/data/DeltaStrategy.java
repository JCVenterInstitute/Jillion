/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
/**
 * There are several different possible Delta strategies
 * that can be used to compute the delta between 2 consecutive
 * values.
 * @author dkatzel
 *
 *
 */
public interface DeltaStrategy {
    /**
     * use the delta strategy computation
     * to uncompress the next value from the given compressed buffer
     * and write it to the given out buffer. 
     * @param compressed buffer containing compressed data.
     * @param out buffer to write uncompressed (undelta'ed) value.
     */
    void unCompress(ByteBuffer compressed, ByteBuffer out);
}
