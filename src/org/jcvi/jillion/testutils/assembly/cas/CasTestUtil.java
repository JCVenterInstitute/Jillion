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
