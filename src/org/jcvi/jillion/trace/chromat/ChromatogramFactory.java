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
package org.jcvi.jillion.trace.chromat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.MagicNumberInputStream;
import org.jcvi.jillion.internal.trace.chromat.abi.AbiUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.chromat.abi.AbiChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.abi.AbiFileParser;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramFileParser;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramFileParser;

public final class ChromatogramFactory {

	private ChromatogramFactory(){
		//can not instantiate
	}
	
	public static Chromatogram create(File chromatogramFile) throws IOException{
		String id = FileUtil.getBaseName(chromatogramFile);
		return create(id,chromatogramFile);
	}
	
	public static Chromatogram create(String id, File chromatogramFile) throws IOException{		
		MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(chromatogramFile); 
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
			return new AbiChromatogramBuilder(id, mIn).build();
		}else if(ZTRUtil.isMagicNumber(magicNumber)){
			return new ZtrChromatogramBuilder(id, mIn).build();
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
			AbiFileParser.parse(mIn, visitor);
		}else if(ZTRUtil.isMagicNumber(magicNumber)){
			ZtrChromatogramFileParser.parse(mIn, visitor);
		}else if(SCFUtils.isMagicNumber(magicNumber)){
			ScfChromatogramFileParser.parse(mIn, visitor);
		}else{
			throw new IOException("unknown chromatogram format (not ab1, scf or ztr)");
		}
	}
	
	
}
