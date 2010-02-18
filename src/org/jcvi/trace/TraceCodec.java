/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TraceCodec {

    /**
     * parse the trace in the inputStream file and return
     * the Chromatogram object that the file represents.
     *
     * @return A populated <code>Chromatogram</code> object.
     */
    Trace decode(InputStream inputStream) throws TraceDecoderException;
    
    /**
     * Encodes the given {@link Trace}
     * and writes the encoded data to the given {@link OutputStream}.
     * @param trace the {@link Trace} to encode.
     * @param out the outputStream to write to.
     * @throws IOException if there are any problems encoding the trace
     * or any problems writing to the {@link OutputStream}.
     */
    void encode(Trace trace, OutputStream out) throws IOException;
    
}
