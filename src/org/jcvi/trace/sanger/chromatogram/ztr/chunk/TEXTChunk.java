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
 * Created on Nov 6, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;


/**
 * Implementation of the ZTR TEXT Chunk.
 * Any information decoded from this chunk will be set as Trace Properties
 * which can be obtained via {@link Trace#getProperties()}.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class TEXTChunk extends Chunk {
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] decodedData, ZTRChromatogramBuilder builder)
            throws TraceDecoderException {
        InputStream in = new ByteArrayInputStream(decodedData);
        builder.properties(parseText(in));
    }

    protected Map<String,String> parseText(InputStream in)
            throws TraceDecoderException {
        Scanner scanner=null;
        Map<String,String> textProps = new HashMap<String, String>();
        try{
            //skip first byte
            in.read();
            scanner = new Scanner(in).useDelimiter("\0+");
             
            while(scanner.hasNext()){
                final String key = scanner.next();                
                final String value = scanner.next();
                textProps.put(key, value);

            }
            return textProps;
        }
        catch(IOException e){
            throw new TraceDecoderException("error reading text data", e);
        }
        finally{
            if(scanner !=null){
                scanner.close();
            }
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected String parseData(byte[] decodedData,
            ChromatogramFileVisitor visitor,String basecalls) throws TraceDecoderException {
        InputStream in = new ByteArrayInputStream(decodedData);
        final Map<String,String> comments = parseText(in);
        visitor.visitComments(comments);
        return basecalls;
        
    }

}
