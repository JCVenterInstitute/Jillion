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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.impl.MagicNumberInputStream;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.DefaultAbiChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.AbiUtil;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFile;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFUtils;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFile;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.SinglePhdFile;
/**
 * {@code SangerTraceParser} is a SangerTraceCodec singleton
 * that can decode both ZTR and SCF trace files.
 * @author dkatzel
 *
 *
 */
public enum SangerTraceParser {
    INSTANCE;


    public SangerTrace decode(File traceFile) throws IOException{
    	
    	MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(traceFile);
	        String id = traceFile.getName();
	        byte[] magicNumber = mIn.peekMagicNumber();
	        if(AbiUtil.isABIMagicNumber(magicNumber)){
	            return DefaultAbiChromatogram.create(id,mIn);
	        }else if(ZTRUtil.isMagicNumber(magicNumber)){
	        	return ZTRChromatogramFile.create(id,mIn);
	        }else if(SCFUtils.isMagicNumber(magicNumber)){
	        	return SCFChromatogramFile.create(id,mIn);
	        }else{
	        	//not a chromatogram file, try phd
	        	try{
	        		return SinglePhdFile.create(traceFile);
	        	}catch(IOException ioException){
	        		throw ioException;
	        	}catch(Exception e){
	        		//must not be a valid phd?
	        		throw new TraceDecoderException("unknown trace format",e);
	        	}
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(mIn);
        }
    }
    public SangerTrace decode(String id, InputStream in) throws TraceDecoderException {
    	MagicNumberInputStream mIn =null;
        try{
        	mIn= new MagicNumberInputStream(in);
	        byte[] magicNumber = mIn.peekMagicNumber();
	        if(AbiUtil.isABIMagicNumber(magicNumber)){
	            return DefaultAbiChromatogram.create(id,mIn);
	        }else if(ZTRUtil.isMagicNumber(magicNumber)){
	        	return ZTRChromatogramFile.create(id,mIn);
	        }else if(SCFUtils.isMagicNumber(magicNumber)){
	        	return SCFChromatogramFile.create(id,mIn);
	        }else{
	        	//not a chromatogram file, try phd
	        	return SinglePhdFile.create(mIn);
	        	
	        }
        }catch(Exception e){
    		//must not be a valid?
    		throw new TraceDecoderException("unknown trace format",e);
    	}finally{
        	IOUtil.closeAndIgnoreErrors(mIn);
        }
    }


}
