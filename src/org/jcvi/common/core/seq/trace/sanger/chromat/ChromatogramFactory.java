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

package org.jcvi.common.core.seq.trace.sanger.chromat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.impl.MagicNumberInputStream;
import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.ab1.Ab1FileParser;
import org.jcvi.common.core.seq.trace.sanger.chromat.ab1.AbiUtil;
import org.jcvi.common.core.seq.trace.sanger.chromat.ab1.DefaultAbiChromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogramFileParser;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFUtils;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRChromatogramFile;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRChromatogramFileParser;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRUtil;

public final class ChromatogramFactory {

	private ChromatogramFactory(){
		//can not instantiate
	}
	
	public static Chromatogram create(File chromatogramFile) throws IOException{
		MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(chromatogramFile);	  
        	String id = FileUtil.getBaseName(chromatogramFile);
        	return detectATypendCreateChromatogram(mIn, id);
        }finally{
        	IOUtil.closeAndIgnoreErrors(mIn);
        }
	}
	public static Chromatogram create(String id, InputStream in) throws IOException{
		MagicNumberInputStream mIn = new MagicNumberInputStream(in); 
        return detectATypendCreateChromatogram(mIn, id);
        
	}
	private static Chromatogram detectATypendCreateChromatogram(
			MagicNumberInputStream mIn, String id)
			throws TraceDecoderException, FileNotFoundException, IOException {
		byte[] magicNumber = mIn.peekMagicNumber();
		
		if(AbiUtil.isABIMagicNumber(magicNumber)){
			return DefaultAbiChromatogram.create(id, mIn);
		}else if(ZTRUtil.isMagicNumber(magicNumber)){
			return ZTRChromatogramFile.create(id, mIn);
		}else if(SCFUtils.isMagicNumber(magicNumber)){
			return new ScfChromatogramBuilder(id, mIn).build();
		}else{
			throw new IOException("unknown chromatogram format (not ab1, scf or ztr)");
		}
	}
	public static void parse(File chromatogramFile, ChromatogramFileVisitor visitor) throws IOException{
		MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(chromatogramFile);	        
	        parseInputStream(visitor, mIn);
        }finally{
        	IOUtil.closeAndIgnoreErrors(mIn);
        }
	}

	private static void parseInputStream(ChromatogramFileVisitor visitor,
			MagicNumberInputStream mIn) throws TraceDecoderException,
			IOException {
		byte[] magicNumber = mIn.peekMagicNumber();
		if(AbiUtil.isABIMagicNumber(magicNumber)){
			Ab1FileParser.parse(mIn, visitor);
		}else if(ZTRUtil.isMagicNumber(magicNumber)){
			ZTRChromatogramFileParser.parse(mIn, visitor);
		}else if(SCFUtils.isMagicNumber(magicNumber)){
			ScfChromatogramFileParser.parse(mIn, visitor);
		}else{
			throw new IOException("unknown chromatogram format (not ab1, scf or ztr)");
		}
	}
	
	
}
