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

package org.jcvi.common.core.seq.trace.sanger.chromat.ztr;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.ztr.ZTRUtil;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.ztr.chunk.ChunkException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code ZTRChromatogramFileParser} is a utility class 
 * for parsing ZTR encoded chromatogram files.
 * @author dkatzel
 *
 *
 */
public final class ZtrChromatogramFileParser {
   
	private ZtrChromatogramFileParser(){
		//can not instantiate
	}
    /**
     * Parse the given ZTR encoded chromatogram file
     * and call the appropriate visitXXX methods of the given
     * visitor while parsing.
     * @param ztrFile the ZTR chromatogram file
     * to parse.
     * @param visitor the visitor instance to call visitXXX methods on
     * (can not be null).
     * @throws TraceDecoderException if there is  a problem
     * parsing the ZTR file.
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if visitor is null.
     */
    public static void parse(File ztrFile, ChromatogramFileVisitor visitor) throws FileNotFoundException, TraceDecoderException{
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        InputStream in = new FileInputStream(ztrFile);
        try{
            parse(in, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }
    /**
     * Parse the given ZTR encoded chromatogram inputStream
     * and call the appropriate visitXXX methods of the given
     * visitor while parsing.
     * @param ztrFile the ZTR chromatogram input stream
     * to parse.
     * @param visitor the visitor instance to call visitXXX methods on
     * (can not be null).
     * @throws TraceDecoderException if there is  a problem
     * parsing the ZTR file.
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if visitor is null.
     */
    public static void parse(InputStream ztrStream, ChromatogramFileVisitor visitor) throws TraceDecoderException{
        visitor.visitFile();
        parseHeader(ztrStream);
        visitor.visitNewTrace();
        Chunk currentChunk = parseNextChunk(ztrStream);
        NucleotideSequence basecalls = null;
        while(currentChunk !=null){
             basecalls =currentChunk.parseChunk(ztrStream, visitor,basecalls);

            currentChunk = parseNextChunk(ztrStream);
        }
        visitor.visitEndOfTrace();
        visitor.visitEndOfFile();
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
    private static void parseHeader(InputStream inputStream) throws TraceDecoderException{
        try {

            validateZTRMagicNumber(inputStream);
            checkVersion(inputStream);

        } catch (IOException ioEx) {
            throw new TraceDecoderException("error parsing ztr header",ioEx);
        }
    }

    private static void checkVersion(InputStream inputStream) throws IOException,
            TraceDecoderException {
        int majorVersion = inputStream.read();
        int minorVersion = inputStream.read();
        if(majorVersion != 1 && minorVersion >2){
            String message = "Unsupported ZTR version";
            throw new TraceDecoderException(message);
        }
    }

    private static  void validateZTRMagicNumber(InputStream inputStream)
            throws TraceDecoderException, IOException {

        byte[] ztrMagic = readZTRMagicNumber(inputStream);
        if(!ZTRUtil.isMagicNumber(ztrMagic)){

           //does not match
            String message = "ZTR header magic number does not match expected " +new String(ztrMagic,IOUtil.UTF_8) ;
            throw new TraceDecoderException(message);
        }
    }

    private static byte[] readZTRMagicNumber(InputStream inputStream) throws TraceDecoderException {
       try{
           return IOUtil.toByteArray(inputStream, 8);
       }catch(IOException e){
           throw new TraceDecoderException("invalid ZTR header",e);
       }
    }
    
    /**
     * Determine what type of chunk is next and return object.
     * @return the appropriate {@link Chunk} may be null.
     */
    private static Chunk parseNextChunk(InputStream inputStream) throws TraceDecoderException{
        try{
            byte[] chunkType = new byte[4];
            try{
            	IOUtil.blockingRead(inputStream, chunkType);
            }catch(EOFException e){
            	return null;
            }                      
            return Chunk.getChunk(new String(chunkType,IOUtil.UTF_8));
        }
        catch(Exception e)
        {
            throw new ChunkException("error parsing next chunk",e);
        }

    }

}
