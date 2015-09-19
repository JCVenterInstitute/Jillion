/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data;

import java.io.IOException;




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
     * @throws IOException if there are any problems
     * parsing the data.
     */
    byte[] parseData(byte[] data) throws IOException;
    /**
     * Encode the given data and return the encoded values
     * as a byte array.
     * @param data the data to encode.
     * @return  the encoded data as a byte array.
     * @throws IOException if there are any problems
     * encoding the data.
     */
    byte[] encodeData(byte[] data) throws IOException;
    /**
     * Encode the given data and return the encoded values
     * as a byte array.
     * @param data the data to encode.
     * @param optionalParameter an optional parameter which some
     * encodings might need if their encoding
     * scheme can take parameters to vary how the data
     * is encoded. If no optional parameters can be
     * passed in then any value would be ignored.
     * @return  the encoded data as a byte array.
     * @throws TraceEncoderException if there are any problems
     * encoding the data.
     */
    byte[] encodeData(byte[] data, byte optionalParameter) throws IOException;


}
