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
package org.jcvi.jillion.trace.chromat.scf;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodecs;
import org.jcvi.jillion.internal.trace.chromat.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeaderCodec;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;

/**
 * {@code ScfChromatogramFileParser} is a utility class 
 * for parsing SCF encoded chromatogram files.
 * @author dkatzel
 *
 *
 */
public final class ScfChromatogramFileParser {

    /**
     * 
     */
    private static final float THREE = 3F;
    private static final SCFHeaderCodec HEADER_CODEC =DefaultSCFHeaderCodec.INSTANCE;
    
    private ScfChromatogramFileParser(){}
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
    public static void parse(File scfFile, ChromatogramFileVisitor visitor) throws TraceDecoderException, IOException{
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        InputStream fileInputStream =null;
        try{
            fileInputStream = new FileInputStream(scfFile);
            parse(fileInputStream, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(fileInputStream);
        }
        
    }
    /**
     * Parse the given SCF encoded chromatogram {@link InputStream}
     * and call the appropriate visitXXX methods of the given
     * visitor while parsing.  This method can handle SCF version
     * 2 AND version 3 formats.
     * @param in the SCF version 2 or version 3 chromatogram file
     * {@link InputStream} to parse.
     * @param visitor the visitor instance to call visitXXX methods on
     * (can not be null).
     * @throws TraceDecoderException if there is  a problem
     * parsing the SCF file.
     * @throws NullPointerException if visitor is null.
     */
    public static void parse(InputStream in, ChromatogramFileVisitor visitor) throws TraceDecoderException{
        DataInputStream dIn = new DataInputStream(in);
        SCFHeader header =HEADER_CODEC.decode(dIn);
        if(header.getVersion()>=THREE){
            SCFCodecs.VERSION_3.parse(dIn,header, visitor);
        }else{
            SCFCodecs.VERSION_2.parse(dIn, header,visitor);
        }
    }
}
