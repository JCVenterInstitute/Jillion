/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.DataInputStream;

public interface SFFReadDataCodec {

    SFFReadData decode(DataInputStream in, int numberOfFlows, int numberOfBases ) throws SFFDecoderException;
}
