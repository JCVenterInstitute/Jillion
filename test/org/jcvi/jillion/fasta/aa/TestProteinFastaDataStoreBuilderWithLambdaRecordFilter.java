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
package org.jcvi.jillion.fasta.aa;

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
public class TestProteinFastaDataStoreBuilderWithLambdaRecordFilter {

	private static File fastaFile;
	
	private final Consumer<ProteinFastaFileDataStoreBuilder> hinter;
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestProteinFastaDataStoreBuilderWithLambdaRecordFilter.class);
		fastaFile = helper.getFile("files/example.aa.fasta");
	}
	
	@Parameters
	public static List<Object[]> data(){
		Consumer<ProteinFastaFileDataStoreBuilder> inMem = builder->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
		Consumer<ProteinFastaFileDataStoreBuilder> memento = builder->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);		
		Consumer<ProteinFastaFileDataStoreBuilder> iterOnly = builder->builder.hint(DataStoreProviderHint.ITERATION_ONLY);
		
		return Arrays.asList(
				new Object[]{inMem},
				new Object[]{memento},
				new Object[]{iterOnly});
	}
	
	public TestProteinFastaDataStoreBuilderWithLambdaRecordFilter(Consumer<ProteinFastaFileDataStoreBuilder> hinter){
		this.hinter = hinter;
	}
	
	@Test
	public void noFilter() throws IOException, DataStoreException{
		ProteinFastaFileDataStoreBuilder builder = new ProteinFastaFileDataStoreBuilder(fastaFile);
		hinter.accept(builder);
		try(ProteinFastaDataStore sut = builder.build()){
			assertEquals(13, sut.getNumberOfRecords());
		}
	}
	
	@Test
	public void onlyKeepSeqsFrom2004() throws IOException, DataStoreException{
		ProteinFastaFileDataStoreBuilder builder = new ProteinFastaFileDataStoreBuilder(fastaFile);
		hinter.accept(builder);
		
		try(ProteinFastaDataStore sut =builder
													.filterRecords(record-> record.getComment().contains("collection_date=2004"))
													.build();
			StreamingIterator<ProteinFastaRecord> iter = sut.iterator();
			){
			assertEquals(4, sut.getNumberOfRecords());
			while(iter.hasNext()){
				assertTrue(iter.next().getComment().contains("collection_date=2004"));
			}
		}
	}
	
}
