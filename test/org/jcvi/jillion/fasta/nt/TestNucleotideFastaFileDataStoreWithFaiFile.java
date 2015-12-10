/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestNucleotideFastaFileDataStoreWithFaiFile {

	@Parameters
	public static List<Object[]> data() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestNucleotideFastaFileDataStoreWithFaiFile.class);
		
		File fastaFile = resources.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs"); 
		File faiFile = resources.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs.fai"); 
		
		NucleotideFastaDataStore withoutFai = DefaultNucleotideFastaFileDataStore.create(fastaFile);
		
		List<Supplier<NucleotideFastaFileDataStoreBuilder>> supplierList = new ArrayList<>();
		//autoDetect
		supplierList.add(()->{
			try {
				return new NucleotideFastaFileDataStoreBuilder(fastaFile);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
		
		//explicit
				supplierList.add(()->{
			try {
				return new NucleotideFastaFileDataStoreBuilder(fastaFile, faiFile);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
		
		List<Object[]> ret = new ArrayList<>();
		Consumer<NucleotideFastaFileDataStoreBuilder> noHint = (builder)->{};
		Consumer<NucleotideFastaFileDataStoreBuilder> allInMemory = (builder)->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
		Consumer<NucleotideFastaFileDataStoreBuilder> mementos = (builder)->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);
		Consumer<NucleotideFastaFileDataStoreBuilder> iterationOnly = (builder)->builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);
		
		for(Supplier<NucleotideFastaFileDataStoreBuilder> supplier : supplierList){
			ret.add(new Object[]{withoutFai, supplier, noHint});
			ret.add(new Object[]{withoutFai, supplier, allInMemory});
			ret.add(new Object[]{withoutFai, supplier, mementos});
			ret.add(new Object[]{withoutFai, supplier, iterationOnly});
		}
		
		
		return ret;
	}
	
	
	
	private NucleotideFastaDataStore sut;
	private NucleotideFastaDataStore withoutFai;
	
	public TestNucleotideFastaFileDataStoreWithFaiFile(NucleotideFastaDataStore withoutFai, Supplier<NucleotideFastaFileDataStoreBuilder> builderSupplier,
			Consumer<NucleotideFastaFileDataStoreBuilder> providerHintConsumer) throws IOException {
		this.withoutFai = withoutFai;
		
		NucleotideFastaFileDataStoreBuilder builder = builderSupplier.get();
		providerHintConsumer.accept(builder);
		sut = builder.build();
								
	}

	
	@Test
	public void usesFaiFile(){
		assertTrue(sut instanceof FaiNucleotideFastaFileDataStore);
	}
	
	@Test
	public void getMatches() throws DataStoreException{
		try(StreamingIterator<NucleotideFastaRecord> iter = withoutFai.iterator()){
			while(iter.hasNext()){
				NucleotideFastaRecord record = iter.next();
				assertEquals(record, sut.get(record.getId()));
			}
		}
	}
	
	@Test
	public void getSequence() throws DataStoreException{
		try(StreamingIterator<String> iter = withoutFai.idIterator()){
			while(iter.hasNext()){
				String id = iter.next();
				assertEquals(withoutFai.getSequence(id), sut.getSequence(id));
			}
		}
	}
	
	@Test
	public void getSubSequenceStartAt0() throws DataStoreException{
		try(StreamingIterator<String> iter = withoutFai.idIterator()){
			while(iter.hasNext()){
				String id = iter.next();
				assertEquals(withoutFai.getSubSequence(id, 0), sut.getSubSequence(id, 0));
			}
		}
	}
	
	@Test
	public void getSubSequence() throws DataStoreException{
		try(StreamingIterator<String> iter = withoutFai.idIterator()){
			while(iter.hasNext()){
				String id = iter.next();
				assertEquals(withoutFai.getSubSequence(id, 86), sut.getSubSequence(id, 86));
			}
		}
	}
	
	@Test
	public void getSubSequenceRange() throws DataStoreException{
		Range range = Range.of(321, 567);
		try(StreamingIterator<String> iter = withoutFai.idIterator()){
			while(iter.hasNext()){
				String id = iter.next();
				assertEquals(withoutFai.getSubSequence(id, range), sut.getSubSequence(id, range));
			}
		}
	}
	
	
}
