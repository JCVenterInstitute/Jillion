package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.spi.io.InputStreamSupplierFactory;
import org.tukaani.xz.XZInputStream;

public class XZInputStreamSupplierFactory implements InputStreamSupplierFactory{

	@Override
	public int getMagicNumberLength() {
		return 6;
	}

	@Override
	public boolean supports(byte[] magicNumber) {
		
		//XZ 0xFD, '7', 'z', 'X', 'Z', 0x00
	   return magicNumber[0]== (byte)0xFD && magicNumber[1] == '7' && magicNumber[2] == 'z' 
			   && magicNumber[3]== 'X' && magicNumber[4]=='Z' && magicNumber[5]==0;
	}

	@Override
	public InputStreamSupplier createFromFile(File f) {
		return new XZInputStreamSupplier(f);
	       
	}

	@Override
	public InputStream create(InputStream inputStream) throws IOException {
        return new XZInputStream(inputStream);
	}
	
	

}
