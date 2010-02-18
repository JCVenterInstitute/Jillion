/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import org.jcvi.trace.TraceDecoderException;



/**
 * This is the implementation of the ZTR Raw Data Format.  This data
 * has no encoding.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public class RawData implements Data {
    /**
     * Since the given data is not encoded, 
     * return the data back as is.
     * @param data the already completely decoded data
     * @return the same reference to the given data.
     */
    @Override
    public byte[] parseData(byte[] data) throws TraceDecoderException {
        //this is raw data
       return data;
    }

}
