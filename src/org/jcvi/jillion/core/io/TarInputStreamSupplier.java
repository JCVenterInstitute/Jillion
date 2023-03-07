package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class TarInputStreamSupplier extends AbstractFileInputStreamSupplier {

	public TarInputStreamSupplier(File file) {
		super(file);
	}

	@Override
	public InputStream get() throws IOException {
		TarArchiveInputStream in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(file)));
		in.getNextEntry();
		return in;
	}

	@Override
	public InputStream get(InputStreamReadOptions readOptions) throws IOException {
		//check if we have to do entry checking
		if(readOptions.getEntryNamePredicate() !=null) {
			TarArchiveInputStream in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(file)));
			Predicate<String> predicate = readOptions.getEntryNamePredicate();
			return InputStreamSupplierUtil.getInputStreamForFirstEntryThatMatches(in, predicate);
		}
		return super.get(readOptions);
	}

	

}
