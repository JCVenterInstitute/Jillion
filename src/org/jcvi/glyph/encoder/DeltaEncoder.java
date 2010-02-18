/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

public interface DeltaEncoder {

    long computeDelta(long lastValue, long secondToLastValue, long thirdToLastValue);
    
}
