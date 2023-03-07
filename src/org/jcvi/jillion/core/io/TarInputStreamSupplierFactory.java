package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.jcvi.jillion.core.io.InputStreamSupplier.InputStreamReadOptions;
import org.jcvi.jillion.spi.io.InputStreamSupplierFactory;

public class TarInputStreamSupplierFactory implements InputStreamSupplierFactory{

	@Override
	public int getMagicNumberLength() {
		//post 1988 tar files have "ustar" as a kind of magic number starting at offset 257
		return 262;
	}

	@Override
	public boolean supports(byte[] magicNumber) {
		
		return magicNumber[257] == 'u' && magicNumber[258] == 's' &&
				magicNumber[259] == 't' && magicNumber[260] == 'a' && magicNumber[261] == 'r';
				
	}

	@Override
	public InputStreamSupplier createFromFile(File f) {
		return new TarInputStreamSupplier(f);
	       
	}

	@Override
	public InputStream create(InputStream inputStream) throws IOException {
		
		TarArchiveInputStream s= new TarArchiveInputStream(inputStream);
		 //assume first record is the entry we care about?
        s.getNextEntry();
        return s;
	}

	@Override
	public InputStream create(InputStream inputStream, InputStreamReadOptions options) throws IOException {
		if(options !=null && options.getEntryNamePredicate()!=null) {
			TarArchiveInputStream s= new TarArchiveInputStream(inputStream);
			InputStreamSupplierUtil.getInputStreamForFirstEntryThatMatches(s, options.getEntryNamePredicate());
	        return s;
		}
		return create(inputStream);
	}

	
	

}
