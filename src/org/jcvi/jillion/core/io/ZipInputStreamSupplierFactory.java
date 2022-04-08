package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.jcvi.jillion.spi.io.InputStreamSupplierFactory;

public class ZipInputStreamSupplierFactory implements InputStreamSupplierFactory{

	@Override
	public int getMagicNumberLength() {
		return 4;
	}

	@Override
	public boolean supports(byte[] magicNumber) {
		
		return magicNumber[0] == (byte)0x50 && magicNumber[1] == (byte)0x4B && 
				magicNumber[2] == (byte)0x03 && magicNumber[3]== (byte) 0x04;
	}

	@Override
	public InputStreamSupplier createFromFile(File f) {
		return new BasicZipInputStreamSupplier(f);
	       
	}

	@Override
	public InputStream create(InputStream inputStream) throws IOException {
		
		ZipInputStream s= new ZipInputStream(inputStream);
		 //assume first record is the entry we care about?
        s.getNextEntry();
        return s;
	}
	
	

}
