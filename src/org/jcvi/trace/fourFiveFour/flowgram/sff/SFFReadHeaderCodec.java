/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.DataInputStream;

public interface SFFReadHeaderCodec {

    SFFReadHeader decodeReadHeader(DataInputStream in) throws SFFDecoderException;
}
