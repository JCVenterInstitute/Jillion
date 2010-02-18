/*
 * Created on Nov 3, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;


/**
* <code>ThirtyTwoToEightBitData</code> is the implementation of the ZTR 32 bit to 8 bit conversion format.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class ThirtyTwoToEightBitData extends AbstractToEightBitData {
    /**
     * Constructor.
     */
    public ThirtyTwoToEightBitData() {
        super(new IntValueSizeStrategy());
    }

    @Override
    protected int getMaxPossibleDecodedSize(int numberOfEncodedBytes) {
        return numberOfEncodedBytes*4;
    }


}
