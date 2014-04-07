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
package org.jcvi.jillion.internal.trace.chromat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.MagicNumberInputStream;
import org.jcvi.jillion.internal.trace.chromat.abi.AbiUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;

public final class ChromatogramUtil {

	private ChromatogramUtil(){
		//can not instantiate
	}

	public static boolean isChromatogram(File f) throws IOException{
		MagicNumberInputStream in = null;
		try{
			in =  new MagicNumberInputStream(new BufferedInputStream(new FileInputStream(f))); 
			byte[] magicNumber = in.peekMagicNumber();
			return isChromatogram(magicNumber);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	private static boolean isChromatogram(byte[] magicNumber){				
		return AbiUtil.isABIMagicNumber(magicNumber) || ZTRUtil.isMagicNumber(magicNumber) || SCFUtils.isMagicNumber(magicNumber);
	}
}
