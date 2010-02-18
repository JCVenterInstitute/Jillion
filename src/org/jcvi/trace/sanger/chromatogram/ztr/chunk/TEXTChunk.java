/*
 * Created on Nov 6, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.jcvi.trace.TraceDecoderException;
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

    protected Properties parseText(InputStream in)
            throws TraceDecoderException {
        Scanner scanner=null;
        Properties textProps = new Properties();
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

}
