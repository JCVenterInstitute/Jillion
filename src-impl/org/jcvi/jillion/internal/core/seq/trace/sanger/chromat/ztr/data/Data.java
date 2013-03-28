/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;




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
    /**
     * Encode the given data and return the encoded values
     * as a byte array.
     * @param data the data to encode.
     * @return  the encoded data as a byte array.
     * @throws TraceEncoderException if there are any problems
     * encoding the data.
     */
    byte[] encodeData(byte[] data) throws TraceEncoderException;
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
    byte[] encodeData(byte[] data, byte optionalParameter) throws TraceEncoderException;


}
