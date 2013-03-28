/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ctg;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class TestContigFileWriter {
    ByteArrayOutputStream out;
    CtgFileWriter sut;
    static TigrContigDataStore dataStore;
    private static String pathToFile = "files/gcv_23918.contig";
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestContigFileWriter.class);
    
    @BeforeClass
    public static void parseContigs() throws IOException{
    	NucleotideSequenceFastaDataStore fullLengthReads = new NucleotideSequenceFastaFileDataStoreBuilder(RESOURCES.getFile("files/gcv_23918.raw.seq.fasta.fasta"))
    															.build();
       dataStore = new TigrContigFileDataStoreBuilder(RESOURCES.getFile(pathToFile), fullLengthReads)
       						.build();
       
    }
    @Before
    public void setup(){
        out = new ByteArrayOutputStream();
        sut = new CtgFileWriter(out);
    }
    
    @Test
    public void write() throws IOException, DataStoreException{
    	StreamingIterator<TigrContig> iter = dataStore.iterator();
    	try{
        while(iter.hasNext()){
        	TigrContig contig = iter.next();
            sut.write(contig);
        }
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter,sut);
    	}
        InputStream inputStream=null;
        try{
	        inputStream= RESOURCES.getFileAsStream(pathToFile);
			byte[] expected =IOUtil.toByteArray(inputStream);
	        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
	        fileOut.write(out.toByteArray());
	        assertEquals(new String(expected), new String(out.toByteArray()));
        }finally{
        	IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

}
