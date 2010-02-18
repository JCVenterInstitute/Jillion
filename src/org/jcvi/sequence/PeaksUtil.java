/*
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.nio.ShortBuffer;

public final class PeaksUtil {

    private PeaksUtil(){}
    /**
     * Generate Fake Peak Data for a given number of Bases.
     * @param numberOfPeaks the number of peaks to fake.
     * @return a {@link Peaks}.
     * @throws IllegalArgumentException if {@code numberOfPeaks < 0 }
     */
    public static Peaks generateFakePeaks(int numberOfPeaks){
        ShortBuffer buf = ShortBuffer.allocate(numberOfPeaks);
        for(int i=0; i<buf.capacity(); i++){
            buf.put((short)(i*10 +5));
        }
        return new Peaks(buf);
    }
}
