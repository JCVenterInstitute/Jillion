package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.jcvi.jillion.core.io.InputStreamSupplier.InputStreamReadOptions;
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
	
	@Override
	public InputStream create(InputStream inputStream, InputStreamReadOptions options) throws IOException {
		if(options !=null && options.getEntryNamePredicate()!=null) {
			ZipArchiveInputStream s= new ZipArchiveInputStream(inputStream);
			InputStreamSupplierUtil.getInputStreamForFirstEntryThatMatches(s, options.getEntryNamePredicate());
	        return s;
		}
		return create(inputStream);
	}

}
