/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.tigr.ctg;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileWriter;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class TestContigFileWriter {
    ByteArrayOutputStream out;
    TigrContigFileWriter sut;
    static TigrContigDataStore dataStore;
    private static String pathToFile = "files/gcv_23918.contig";
    private final static ResourceHelper RESOURCES = new ResourceHelper(TestContigFileWriter.class);
    
    @BeforeClass
    public static void parseContigs() throws IOException{
    	NucleotideFastaDataStore fullLengthReads = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("files/gcv_23918.raw.seq.fasta.fasta"))
    															.build();
       dataStore = new TigrContigFileDataStoreBuilder(RESOURCES.getFile(pathToFile), fullLengthReads)
       						.build();
       
    }
    @Before
    public void setup(){
        out = new ByteArrayOutputStream();
        sut = new TigrContigFileWriter(out);
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
