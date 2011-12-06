/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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

import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;

public final class AbiUtil {

	private AbiUtil(){}
	/**
	 * The magic number of an Ab1 file.
	 */
	private static final byte[] MAGIC_NUMBER = new byte[]{(char)'A',(char)'B',(char)'I',(char)'F'};
    
	public static final int HEADER_SIZE = 30;
	public static String parseASCIIStringFrom(byte[] data){
		return new String(data,IOUtil.UTF_8);
	}
	
	public static String parsePascalStringFrom(byte[] data){
		return new String(data,1, data.length-1,IOUtil.UTF_8);
	}
	
	public static boolean isABIMagicNumber(byte[] magicNumber){
	    return Arrays.equals(AbiUtil.MAGIC_NUMBER, magicNumber);
	}
}
