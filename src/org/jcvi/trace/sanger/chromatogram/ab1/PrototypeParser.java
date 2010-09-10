package org.jcvi.trace.sanger.chromatogram.ab1;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.trace.TraceDecoderException;

public class PrototypeParser {

	public static void main(String[] args) throws FileNotFoundException, TraceDecoderException{
		File ab1File = new File("C:\\Documents and Settings\\Danny\\Desktop\\Run_3100-1211-010_2010-09-08_10-49_0128_20100909080202\\A-1045612-613_A04_TIGR_SDBHD01T00PB1A2266R_1045613_1126569695638_002.ab1");
	
		Ab1FileParser.parseAb1File(ab1File, new Ab1ChromatogramFilePrinter());
	}
}
