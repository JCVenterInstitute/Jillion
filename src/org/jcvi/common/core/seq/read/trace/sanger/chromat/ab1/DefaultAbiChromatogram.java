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

package org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;

/**
 * @author dkatzel
 *
 *
 */
public final class DefaultAbiChromatogram {

	private DefaultAbiChromatogram(){
		//can not instantiate
	}
    public static AbiChromatogram of(File abiFile) throws FileNotFoundException, TraceDecoderException{
        AbiChromatogramBuilder builder = new AbiChromatogramBuilder(abiFile.getName());
        Ab1FileParser.parse(abiFile, builder);        
        return builder.build();
    }
    
    public static AbiChromatogram create(String id, InputStream abiStream) throws TraceDecoderException{
    	 AbiChromatogramBuilder builder = new AbiChromatogramBuilder(id);
         Ab1FileParser.parse(abiStream, builder);            
         return builder.build();
    }
   
    
}
