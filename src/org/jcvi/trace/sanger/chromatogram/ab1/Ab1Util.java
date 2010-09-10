package org.jcvi.trace.sanger.chromatogram.ab1;

import java.util.Arrays;

public final class Ab1Util {

	private Ab1Util(){}
	
	public static final int HEADER_SIZE = 30;
	public static String parseASCIIStringFrom(byte[] data){
		return new String(data);
	}
	
	public static String parsePascalStringFrom(byte[] data){
		return new String(data,1, data.length-1);
	}
}
