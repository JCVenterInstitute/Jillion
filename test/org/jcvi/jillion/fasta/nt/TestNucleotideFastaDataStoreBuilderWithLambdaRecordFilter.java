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
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter {

	private static File fastaFile;
	private static File gzippedFile;
	
	private final Consumer<NucleotideFastaFileDataStoreBuilder> hinter;
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter.class);
		fastaFile = helper.getFile("files/giv_XX_15050.seq");
		gzippedFile = new File(fastaFile.getParentFile(), fastaFile.getName() +".gz");
	}
	
	@Parameters
	public static List<Object[]> data(){
		Consumer<NucleotideFastaFileDataStoreBuilder> inMem = builder->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
		Consumer<NucleotideFastaFileDataStoreBuilder> memento = builder->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);		
		Consumer<NucleotideFastaFileDataStoreBuilder> iterOnly = builder->builder.hint(DataStoreProviderHint.ITERATION_ONLY);
		
		return Arrays.asList(
				new Object[]{inMem},
				new Object[]{memento},
				new Object[]{iterOnly});
	}
	
	public TestNucleotideFastaDataStoreBuilderWithLambdaRecordFilter(Consumer<NucleotideFastaFileDataStoreBuilder> hinter){
		this.hinter = hinter;
	}
	
	@Test
	public void noFilter() throws IOException, DataStoreException{
		noFilter(fastaFile);
	}
	
	@Test
        public void noFilterGzipped() throws IOException, DataStoreException{
                noFilter(gzippedFile);
        }

    private void noFilter(File file) throws IOException, DataStoreException {
        NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(file);
		hinter.accept(builder);
		try(NucleotideFastaDataStore sut = builder.build()){
			assertEquals(274, sut.getNumberOfRecords());
		}
    }
	
	@Test
	public void onlyKeepLongReadsNormalFile() throws IOException, DataStoreException{
		keepOnlyLongReads(fastaFile);
	}
	@Test
        public void onlyKeepLongReadsGZippedFile() throws IOException, DataStoreException{
                keepOnlyLongReads(gzippedFile);
        }
	
	@Test
        public void onlyKeepLongReadsNewLengthMethodNormalFile() throws IOException, DataStoreException{
	    keepOnlyLongReadsNewLengthMethod(fastaFile);
        }
        @Test
        public void onlyKeepLongReadsNewLengthMethodGZippedFile() throws IOException, DataStoreException{
            keepOnlyLongReadsNewLengthMethod(gzippedFile);
        }

    private void keepOnlyLongReads(File file) throws IOException,
            DataStoreException {
        NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(file);
		hinter.accept(builder);
		
		try(NucleotideFastaDataStore sut =builder
							.filterRecords(record-> record.getSequence().getLength() >1000)
							.build();
			StreamingIterator<NucleotideFastaRecord> iter = sut.iterator();
			){
			assertEquals(33, sut.getNumberOfRecords());
			while(iter.hasNext()){
				assertTrue(iter.next().getSequence().getLength() > 1000);
			}
		}
    }
    
    private void keepOnlyLongReadsNewLengthMethod(File file) throws IOException,
    DataStoreException {
NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(file);
        hinter.accept(builder);
        
        try(NucleotideFastaDataStore sut =builder
                                                .filterRecords(record-> record.getLength() >1000)
                                                .build();
                StreamingIterator<NucleotideFastaRecord> iter = sut.iterator();
                ){
                assertEquals(33, sut.getNumberOfRecords());
                while(iter.hasNext()){
                        assertTrue(iter.next().getSequence().getLength() > 1000);
                }
        }
}
	
}
