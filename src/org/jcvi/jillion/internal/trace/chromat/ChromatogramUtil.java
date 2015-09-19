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
