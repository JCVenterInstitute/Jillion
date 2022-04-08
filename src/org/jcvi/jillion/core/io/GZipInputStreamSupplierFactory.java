package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.jcvi.jillion.spi.io.InputStreamSupplierFactory;
import org.tukaani.xz.XZInputStream;

public class GZipInputStreamSupplierFactory implements InputStreamSupplierFactory{

	@Override
	public int getMagicNumberLength() {
		return 2;
	}

	@Override
	public boolean supports(byte[] magicNumber) {
		
	   return magicNumber[0] == (byte) 0x1F && magicNumber[1] == (byte)0x8B;
	}

	@Override
	public InputStreamSupplier createFromFile(File f) {
		return new GZipInputStreamSupplier(f);
	       
	}

	@Override
	public InputStream create(InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
	}
	
	

}
