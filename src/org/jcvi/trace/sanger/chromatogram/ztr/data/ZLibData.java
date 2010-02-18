/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;


import java.nio.ByteBuffer;
import java.util.zip.Inflater;
import java.util.zip.DataFormatException;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRUtil;

/**
 * <code>ZLibData</code> Data format uses a zipped algorithm
 * to compress the data.  Most common form of compression is
 * <code> HUFFMAN</code>
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class ZLibData implements Data {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data) throws TraceDecoderException {

        try{
            ByteBuffer size = ByteBuffer.allocate(4);
            size.put(data,1,4);    
            int uncompressedLength =(int) ZTRUtil.readInt(IOUtil.switchEndian(size.array()));            
            ByteBuffer compressedData = ByteBuffer.allocate(data.length-5);
            compressedData.put(data,5,data.length-5);
            //decompress the zipped data
            Inflater decompresser = new Inflater();
            decompresser.setInput(compressedData.array());
            ByteBuffer byteBuffer = ByteBuffer.allocate(uncompressedLength);
            decompresser.inflate(byteBuffer.array());
            
            decompresser.end();
            return byteBuffer.array();
        }catch(DataFormatException dfEx){
            throw new TraceDecoderException("could not parse ZLibData",dfEx);
        }
    }

}
