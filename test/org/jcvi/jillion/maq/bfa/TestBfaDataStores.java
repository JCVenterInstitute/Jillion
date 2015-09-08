/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestBfaDataStores {
	//name parameter says if test fails show
	//this string uses message format to include
	//toString() of parameters in object array so
	//"{0}" means the first parameter which is the hint type.
	@Parameters(name="provider hint = {0}")
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[]{DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY},
				new Object[]{DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED},
				new Object[]{DataStoreProviderHint.ITERATION_ONLY});
	}
	
	private static NucleotideFastaDataStore expected;
	private static ResourceHelper resourceHelper = new ResourceHelper(TestBfaDataStores.class) ;
	
	@BeforeClass
	public static void parseFastqFile() throws IOException{
		expected = new NucleotideFastaFileDataStoreBuilder(resourceHelper.getFile("seqs.fasta"))
						.build();
	}
	
	@AfterClass
	public static void freeDataStore(){
		expected = null;
	}
	
	private DataStoreProviderHint providerHint;
	
	
	
	public TestBfaDataStores(DataStoreProviderHint providerHint) {
		this.providerHint = providerHint;
	}

	@Test
	public void allRecordsPresent() throws IOException, DataStoreException{
		DataStoreFilter filter = DataStoreFilters.alwaysAccept();
		NucleotideFastaDataStore datastore = createDataStore(filter);
		assertEquals(2, datastore.getNumberOfRecords());
		StreamingIterator<NucleotideFastaRecord> iter =null;
		try{
			iter =expected.iterator();
			while(iter.hasNext()){
				NucleotideFastaRecord r = iter.next();
				assertEquals(r, datastore.get(r.getId()));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		assertIteratorMatches(datastore, filter);
	}
	private void assertIteratorMatches(NucleotideFastaDataStore actual, DataStoreFilter filter) throws DataStoreException{
		StreamingIterator<NucleotideFastaRecord> actualIter =null;
		StreamingIterator<NucleotideFastaRecord> expectedIter =null;
		
		try{
			actualIter = actual.iterator();
			expectedIter =expected.iterator();
			while(expectedIter.hasNext()){
				NucleotideFastaRecord expected = expectedIter.next();
				if(filter.accept(expected.getId())){
					assertEquals(expected, actualIter.next());
				}
				
			}
			assertFalse(actualIter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actualIter, expectedIter);
		}
	}
	@Test
	public void filterAllReads() throws IOException, DataStoreException{
		NucleotideFastaDataStore datastore = createDataStore(DataStoreFilters.neverAccept());
		assertEquals(0, datastore.getNumberOfRecords());
		
	}
	@Test
	public void onlyIncludeSomeIds() throws IOException, DataStoreException{
		String id = "IWKNA01T07A01PB2A1F";
		NucleotideFastaRecord fastaRecord = expected.get(id);
		DataStoreFilter filter = DataStoreFilters.newIncludeFilter(Collections.singleton(id));
		NucleotideFastaDataStore datastore = createDataStore(filter);
		assertEquals(1, datastore.getNumberOfRecords());
		
		assertEquals(fastaRecord, datastore.get(id));
		
		StreamingIterator<NucleotideFastaRecord> iter =null;
		try{
			iter =datastore.iterator();
			assertTrue(iter.hasNext());
			
			assertEquals(fastaRecord, iter.next());
			assertFalse(iter.hasNext());
			
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		
		assertIteratorMatches(datastore, filter);
	}
	
	
	private NucleotideFastaDataStore createDataStore(DataStoreFilter filter) throws IOException{
		File bfa = resourceHelper.getFile("seqs.bfa");
		FastaParser parser = BfaParser.create(bfa, ByteOrder.LITTLE_ENDIAN);
		return new NucleotideFastaFileDataStoreBuilder(parser)
						.hint(providerHint)
						.filter(filter)
						.build();
	}
	
}
