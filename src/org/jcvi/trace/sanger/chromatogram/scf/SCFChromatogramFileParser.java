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

package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;

/**
 * {@code SCFChromatogramFileParser} is a utility class 
 * for parsing SCF encoded chromatogram files.
 * @author dkatzel
 *
 *
 */
public final class SCFChromatogramFileParser {

    private static final SCFHeaderCodec HEADER_CODEC =new DefaultSCFHeaderCodec();
    private static final AbstractSCFCodec VERSION3 =Version3SCFCodec.INSTANCE;
    private static final AbstractSCFCodec VERSION2 =Version2SCFCodec.INSTANCE;
    
    private SCFChromatogramFileParser(){}
    /**
     * Parse the given SCF encoded chromatogram file
     * and call the appropriate visitXXX methods of the given
     * visitor while parsing.  This method can handle SCF version
     * 2 AND version 3 formats.
     * @param scfFile the SCF version 2 or version 3 chromatogram file
     * to parse.
     * @param visitor the visitor instance to call visitXXX methods on
     * (can not be null).
     * @throws TraceDecoderException if there is  a problem
     * parsing the SCF file.
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if visitor is null.
     */
    public static void parseSCFFile(File scfFile, ChromatogramFileVisitor visitor) throws TraceDecoderException, IOException{
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        InputStream fileInputStream =null;
        try{
            fileInputStream = new FileInputStream(scfFile);
            parseSCFFile(fileInputStream, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(fileInputStream);
        }
        
    }
    /**
     * Parse the given SCF encoded chromatogram InputStream
     * and call the appropriate visitXXX methods of the given
     * visitor while parsing.  This method can handle SCF version
     * 2 AND version 3 formats.
     * @param in the SCF version 2 or version 3 chromatogram file
     * Inputstream to parse.
     * @param visitor the visitor instance to call visitXXX methods on
     * (can not be null).
     * @throws TraceDecoderException if there is  a problem
     * parsing the SCF file.
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if visitor is null.
     */
    public static void parseSCFFile(InputStream in, ChromatogramFileVisitor visitor) throws TraceDecoderException{
        DataInputStream dIn = new DataInputStream(in);
        SCFHeader header =HEADER_CODEC.decode(dIn);
        if(header.getVersion()>=3F){
            VERSION3.parse(dIn,header, visitor);
        }else{
            VERSION2.parse(dIn, header,visitor);
        }
    }
}
