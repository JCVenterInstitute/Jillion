package org.jcvi.jillion.fasta.nt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;

public class TestDefaultNucleotideFastaDataStoreAsStream extends AbstractTestSequenceFastaDataStore {

    

    @Override
    protected NucleotideFastaDataStore parseFile(File file)
            throws IOException {
    	InputStream in = null;
    	try{
    		in = new BufferedInputStream(new FileInputStream(file));
    		return DefaultNucleotideFastaFileDataStore.create(in);
    	}finally{
    		IOUtil.closeAndIgnoreErrors(in);
    	}
        
    }

}
