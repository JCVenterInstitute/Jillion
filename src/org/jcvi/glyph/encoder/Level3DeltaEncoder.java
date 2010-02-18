/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

public class Level3DeltaEncoder implements DeltaEncoder{

    @Override
    public long computeDelta(long lastValue, long secondToLastValue,
            long thirdToLastValue) {
        return 3*lastValue - 3*secondToLastValue + thirdToLastValue;
    }

}
