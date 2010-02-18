/*
 * Created on Oct 31, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

/**
 * <code>SixteenBitToEightBitData</code> is the implementation of the ZTR 16 bit to 8 bit conversion format.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 */
public class SixteenBitToEightBitData extends AbstractToEightBitData {
    /**
     * Constructor.
     */
    public SixteenBitToEightBitData() {
        super(new ShortValueSizeStrategy());
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected int getMaxPossibleDecodedSize(int numberOfEncodedBytes) {
        return numberOfEncodedBytes*2;
    }
}