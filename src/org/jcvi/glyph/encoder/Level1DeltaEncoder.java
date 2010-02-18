/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

public class Level1DeltaEncoder implements DeltaEncoder{

    @Override
    public long computeDelta(long lastValue, long secondToLastValue,
            long thirdToLastValue) {
        return lastValue;
    }
   
    
    
}
