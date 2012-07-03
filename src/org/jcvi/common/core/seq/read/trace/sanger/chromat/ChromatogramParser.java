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

package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.MagicNumberInputStream;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.Ab1FileParser;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.AbiUtil;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFileParser;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFUtils;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFileParser;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRUtil;

public final class ChromatogramParser {

	private ChromatogramParser(){
		//can not instantiate
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
			SCFChromatogramFileParser.parse(mIn, visitor);
		}else{
			throw new IOException("unknown chromatogram format (not ab1, scf or ztr)");
		}
	}
	
	public static void parse(InputStream chromatogramStream, ChromatogramFileVisitor visitor) throws IOException{
		MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(chromatogramStream);	        
	        parseInputStream(visitor, mIn);
        }finally{
        	IOUtil.closeAndIgnoreErrors(mIn);
        }
	}
}
