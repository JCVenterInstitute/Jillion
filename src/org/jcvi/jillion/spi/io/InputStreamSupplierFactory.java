package org.jcvi.jillion.spi.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.InputStreamSupplier;

public interface InputStreamSupplierFactory {

	int getMagicNumberLength();
	
	boolean supports(byte[] magicNumber);
	
	InputStream create(InputStream inputStream) throws IOException;
	
	default InputStream create(InputStream inputStream, InputStreamSupplier.InputStreamReadOptions options) throws IOException{
		return create(inputStream);
	}
	
	InputStreamSupplier createFromFile(File f) throws IOException;
	
	
	
}
