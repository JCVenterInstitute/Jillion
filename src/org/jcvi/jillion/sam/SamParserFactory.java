package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;

public final class SamParserFactory {

	private SamParserFactory(){
		//can not instantiate
	}
	public static SamParser create(File f) throws IOException{
		return create(f, ReservedAttributeValidator.INSTANCE);
	}
	public static SamParser create(File f, SamAttributeValidator validator) throws IOException{
		
		String extension = FileUtil.getExtension(f);
		if(validator == null){
			throw new NullPointerException("validator can not be null");
		}
		if("sam".equalsIgnoreCase(extension)){
			return new SamFileParser(f,validator);
		}
		if("bam".equalsIgnoreCase(extension)){
			return new BamFileParser(f, validator);
		}
		throw new IllegalArgumentException("unknown file format " + f.getName());
	}
}
