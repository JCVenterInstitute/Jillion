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
 * Created on Oct 29, 2007
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.trace.Trace;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.TraceCodec;

/**
 * <code>ChromatogramXMLAdapter</code> is an adapts
 * {@link ChromatogramXMLSerializer} to conform to the interface
 * for {@link TraceCodec}.  This will allow {@link BasicChromatogram}
 * objects which have been serialized to XML by  {@link ChromatogramXMLSerializer}
 * to be parsed by an instance of {@link TraceCodec}.
 * @author dkatzel
 *
 *
 */
public class ChromatogramXMLAdapter implements TraceCodec {

    public Trace decode(InputStream inputStream) throws TraceDecoderException {
       return ChromatogramXMLSerializer.fromXML(inputStream);
    }

    @Override
    public void encode(Trace trace, OutputStream out) throws IOException {
        ChromatogramXMLSerializer.toXML((Chromatogram)trace , out);
        
    }

}
