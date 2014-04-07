/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.chromat.scf;

import java.io.BufferedInputStream;
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
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
/**
 * {@code ScfChromatogramParser} parses SCF encoded {@link org.jcvi.jillion.trace.chromat.Chromatogram}
 * files.  This class can handle SCF version
 * 2 AND version 3 formats.
 */
public abstract class ScfChromatogramParser {

	private static final float THREE = 3F;
    private static final SCFHeaderCodec HEADER_CODEC =DefaultSCFHeaderCodec.INSTANCE;
    /**
	 * Create a new parser object that will
	 * parse the given SCF file.
	 * @param scfFile the SCF encoded file to parse;
	 * can not be null.
	 * @return a new ScfChromatogramParser instance;
	 * will never be null.
	 * @throws NullPointerException if scfFile is null.
	 */
    public static ScfChromatogramParser create(File scfFile){
    	return new ScfFileChromatogramParser(scfFile);
    }
    /**
	 * Create a new parser object that will
	 * parse the given SCF encoded {@link InputStream}.
	 * @param in the SCF encoded {@link InputStream} to parse;
	 * can not be null.
	 * @return a new ScfChromatogramParser instance;
	 * will never be null.
	 * @throws NullPointerException if in is null.
	 */
    public static ScfChromatogramParser create(InputStream in){
    	return new InputStreamChromatogramParser(in);
    }
    
	private ScfChromatogramParser(){
		//can not instantiate outside of this file
	}
	 /**
     * Parse the SCF encoded {@link org.jcvi.jillion.trace.chromat.Chromatogram}
     * and call the appropriate visitXXX methods of the given
     * visitor while parsing.
     * @param visitor the visitor instance to call visitXXX methods on
     * (can not be null).
     * @throws IOException if there is  a problem
     * parsing the SCF file.
     * @throws NullPointerException if visitor is null.
     */
	public abstract void accept(ChromatogramFileVisitor visitor) throws IOException;

    
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
    protected void parse(InputStream in, ChromatogramFileVisitor visitor) throws IOException{
        DataInputStream dIn = new DataInputStream(in);
        SCFHeader header =HEADER_CODEC.decode(dIn);
        if(header.getVersion()>=THREE){
            SCFCodecs.VERSION_3.parse(dIn,header, visitor);
        }else{
            SCFCodecs.VERSION_2.parse(dIn, header,visitor);
        }
    }
	
    private static final class ScfFileChromatogramParser  extends ScfChromatogramParser{
    	private final File scfFile;

		public ScfFileChromatogramParser(File scfFile) {
			if(scfFile ==null){
				throw new NullPointerException("scf file can not be null");
			}
			this.scfFile = scfFile;
		}

		@Override
		public void accept(ChromatogramFileVisitor visitor) throws IOException {
			InputStream in = new BufferedInputStream(new FileInputStream(scfFile));
			try{
				parse(in, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
    	
    	
    }
    
    private static final class InputStreamChromatogramParser  extends ScfChromatogramParser{
    	private final InputStream in;
    	private volatile boolean readAlready=false;
    	
		public InputStreamChromatogramParser(InputStream in) {
			if(in ==null){
				throw new NullPointerException("inputstream can not be null");
			}
			this.in = in;
		}

		@Override
		public synchronized void accept(ChromatogramFileVisitor visitor) throws IOException {
			if(readAlready){
				throw new IllegalStateException("already parsed inputstream");
			}
			readAlready=true;
			parse(in, visitor);			
			
		}
    	
    	
    }
}
