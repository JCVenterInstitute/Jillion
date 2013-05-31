package org.jcvi.jillion.fasta.qual;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;

public class TestDefaultQualityFastaDataStoreAsStream extends AbstractTestQualityFastaDataStore{
    
    @Override
    protected QualityFastaDataStore createDataStore(File file) throws IOException{
        InputStream in = null;
        try{
        	in = new BufferedInputStream(new FileInputStream(file));
        	return DefaultQualityFastaFileDataStore.create(file);
        }finally{
        	IOUtil.closeAndIgnoreErrors(in);
        }
    	
    }

}
