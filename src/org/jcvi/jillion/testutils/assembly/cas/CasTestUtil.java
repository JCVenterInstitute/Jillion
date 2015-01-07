package org.jcvi.jillion.testutils.assembly.cas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.AbstractCasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStoreBuilderVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.assembly.clc.cas.CasUtil;

public final class CasTestUtil {

	private CasTestUtil(){
		//can not instantiate
	}
	
	public static CasGappedReferenceDataStore createGappedReferenceDataStore(CasParser parser) throws IOException{
		CasGappedReferenceDataStoreBuilderVisitor gappedRefVisitor = new CasGappedReferenceDataStoreBuilderVisitor(parser.getWorkingDir());
		
		parser.parse(gappedRefVisitor);
		return gappedRefVisitor.build();
	}
	
	/**
	 * Get the list of {@link File}s used as input to the
	 * Cas assembly. 
	 * 
	 * @param parser the {@link CasParser} instance to parse
	 * the read files from; can not be null.
	 * 
	 * @return a List of Files of the read files.  The order
	 * in the list is the order of the files in the cas.
	 * This order is crucial to parsing any alignment data.
	 * 
	 * @throws IOException if there is a problem parsing the cas file.
	 * @throws NullPointerException if parser is null.
	 */
	public static List<File> getReadFiles(CasParser parser) throws IOException{
		List<File> files = new ArrayList<File>();
		parser.parse(new AbstractCasFileVisitor() {

			@Override
			public void visitReadFileInfo(CasFileInfo readFileInfo) {
				for(String path :readFileInfo.getFileNames()){
					try {
						files.add(CasUtil.getFileFor(parser.getWorkingDir(), path));
					} catch (FileNotFoundException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
			
		});
		return files;
	}
}
