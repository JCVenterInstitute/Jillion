package org.jcvi.jillion.internal.trace.chromat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	public static boolean isChromatogram(File f) throws FileNotFoundException, IOException{
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
