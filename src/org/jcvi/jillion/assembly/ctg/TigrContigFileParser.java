package org.jcvi.jillion.assembly.ctg;

import java.util.regex.Pattern;

public abstract class TigrContigFileParser {

	  private static final Pattern NEW_CONTIG_PATTERN = Pattern.compile("##(\\S+).+");
	    private static final Pattern NEW_READ_PATTERN = Pattern.compile("#(\\S+)\\((-?\\d+)\\)\\s+\\[(.*)\\].+\\{(-?\\d+) (-?\\d+)\\}.+");
	  
	private TigrContigFileParser(){
		//can not instantiate outside of this file
	}
	
	private static class FileBasedTigrContigParser extends TigrContigFileParser{
		
	}
}
