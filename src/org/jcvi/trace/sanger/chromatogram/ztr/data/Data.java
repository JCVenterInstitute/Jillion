/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import org.jcvi.trace.TraceDecoderException;




/**
 * The actual chromatogram data in a ZTR file
 * is compressed and/or encoded.  There are many different methods
 * and it is common for different Data encodings to be chained together
 * to make the data even more compact.
 * @author dkatzel
 * *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public interface Data {


    /**
     * parse the data which may be encoded.
     * @param data the actual byte data to parse.
     * @return an decoded byte array which may be much larger than
     * length of the given input data.
     * @throws TraceDecoderException if there are any problems
     * parsing the data.
     */
    byte[] parseData(byte[] data)throws TraceDecoderException;


}
