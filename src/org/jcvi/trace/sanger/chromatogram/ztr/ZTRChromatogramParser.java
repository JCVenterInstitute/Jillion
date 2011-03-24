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
package org.jcvi.trace.sanger.chromatogram.ztr;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.SangerTraceCodec;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.Chunk;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.ChunkException;


/**
 * This class parses a ZTR encoded file into a
 * {@link ZTRChromatogramImpl}.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public class ZTRChromatogramParser implements SangerTraceCodec {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public ZTRChromatogramImpl decode(InputStream inputStream) throws TraceDecoderException{
        parseHeader(inputStream);
        Chunk currentChunk = parseNextChunk(inputStream);
        ZTRChromatogramBuilder builder = new ZTRChromatogramBuilder();
        while(currentChunk !=null){
             currentChunk.parseChunk(builder, inputStream);

            currentChunk = parseNextChunk(inputStream);
        }
        return builder.build();
    }
    @Override
    public ZTRChromatogramImpl decode(File sangerTrace) throws TraceDecoderException,
            FileNotFoundException {
        InputStream in=null;
        try{
            in = new FileInputStream(sangerTrace);
            return decode(in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * parse the header of the .ztr file.  The header
     * should consist of 8 bytes for the ztr magic number
     * then 2 bytes for the .ztr version.
     * <p>
     * <pre>
        typedef struct {
        
                    unsigned char  magic[8];      0xae5a54520d0a1a0a (be)
                    unsigned char  version_major; // 1 //
                    unsigned char  version_minor; // 1 //
        } ztr_header_t;
                
        // The ZTR magic numbers //
        #define ZTR_MAGIC       "\256ZTR\r\n\032\n"
        #define ZTR_VERSION_MAJOR   1
        #define ZTR_VERSION_MINOR   1
        </pre>
        <p>
        So the total header will consist of:
        <pre>
        Byte number   0  1  2  3  4  5  6  7  8  9
                    +--+--+--+--+--+--+--+--+--+--+
        Hex values  |ae 5a 54 52 0d 0a 1a 0a|01 01|
                    +--+--+--+--+--+--+--+--+--+--+
                    </pre>
     * @throws TraceDecoderException
     */
    private void parseHeader(InputStream inputStream) throws TraceDecoderException{
        try {

            validateZTRMagicNumber(inputStream);
            checkVersion(inputStream);

        } catch (IOException ioEx) {
            throw new TraceDecoderException("error parsing ztr header",ioEx);
        }
    }

    private void checkVersion(InputStream inputStream) throws IOException,
            TraceDecoderException {
        int majorVersion = inputStream.read();
        int minorVersion = inputStream.read();
        if(majorVersion != 1 && minorVersion >2){
            String message = "Unsupported ZTR version";
            throw new TraceDecoderException(message);
        }
    }

    private void validateZTRMagicNumber(InputStream inputStream)
            throws TraceDecoderException, IOException {

        byte[] ztrMagic = readZTRMagicNumber(inputStream);
        if(!ZTRUtil.isMagicNumber(ztrMagic)){

           //does not match
            String message = "ZTR header magic number does not match expected " +new String(ztrMagic) ;
            throw new TraceDecoderException(message);
        }
    }

    private byte[] readZTRMagicNumber(InputStream inputStream) throws IOException,TraceDecoderException {
        byte[] ztrMagic = new byte[8];
        int bytesRead = inputStream.read(ztrMagic);
        if (bytesRead < ztrMagic.length) {
            // no
            String message = "File does not have a header";
            throw new TraceDecoderException(message);
        }
        return ztrMagic;

       
        
    }

    /**
     * Determine what type of chunk is next and return object.
     * @return the appropriate {@link Chunk} may be null.
     */
    private Chunk parseNextChunk(InputStream inputStream) throws TraceDecoderException{
        try{
            byte[] chunkType = new byte[4];
            int bytesRead = inputStream.read(chunkType);
            if(bytesRead ==-1){
                //end of file
                return null;
            }
            if(bytesRead < 4){
                throw new ChunkException("Can not parse Chunk Type");
            }
            return Chunk.getChunk(new String(chunkType));
        }
        catch(Exception e)
        {
            throw new TraceDecoderException("error parsing next chunk",e);
        }

    }

    @Override
    public void encode(SangerTrace trace, OutputStream out) throws IOException {
        throw new UnsupportedOperationException("can not yet encode ztrs");
        
    }



}
