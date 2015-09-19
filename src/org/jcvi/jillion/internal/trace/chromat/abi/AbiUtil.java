/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.trace.chromat.abi;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * Utility class for working
 * with Abi chromatogram files.
 * @author dkatzel
 *
 */
public final class AbiUtil {


	/**
	 * The magic number of an Ab1 file.
	 */
	private static final byte[] MAGIC_NUMBER = new byte[]{'A','B','I','F'};
    
	public static final int HEADER_SIZE = 30;
	
	private AbiUtil(){
		//can not instantiate
	}
	
	
	public static byte[] getMagicNumber() {
		//defensive copy since java arrays are mutable even if declared final
    	//(someone can still modify the contents just not the size)
        byte[] ret = new byte[MAGIC_NUMBER.length];
        System.arraycopy(MAGIC_NUMBER, 0, ret, 0, ret.length);
        return ret;
	}


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
