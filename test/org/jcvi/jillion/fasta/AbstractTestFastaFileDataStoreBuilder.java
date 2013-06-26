/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public abstract class AbstractTestFastaFileDataStoreBuilder<T, S extends Sequence<T>, F extends FastaRecord<T,S>, D extends DataStore<F>> {
	
	private final File fasta;
	
	public AbstractTestFastaFileDataStoreBuilder(ResourceHelper helper, String pathToFasta) throws IOException{
		fasta = helper.getFile(pathToFasta);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws IOException{
		createDataStoreFromFile(null);
	}
	@Test(expected = NullPointerException.class)
	public void nullStreamShouldThrowNPE() throws IOException{
		createDataStoreFromStream((InputStream)null);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullFilterShouldThrowNPE() throws IOException{
		createDataStoreFromFile(fasta, (DataStoreProviderHint)null);

	}
	
	
	
	@Test
	public void filteredFile() throws IOException, DataStoreException{
		D datastore =createDataStoreFromFile(fasta,DataStoreFilters.neverAccept());

		
		assertEquals(0, datastore.getNumberOfRecords());
	}
	
	@Test
	public void filteredStream() throws IOException, DataStoreException{
		InputStream in = new BufferedInputStream(new FileInputStream(fasta));
		try{
		D datastore =createDataStoreFromStream(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED,
											DataStoreFilters.neverAccept());
		
		assertEquals(0, datastore.getNumberOfRecords());
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	@Test
	public void streamEqualsDefault() throws IOException, DataStoreException{
		D fromFile =createDataStoreFromFile(fasta);

		D fromStream = createDataStoreFromStream();
		assertContainSameData(fromFile, fromStream);
	
		
		
	}
	
	protected abstract D createDataStoreFromFile(File fasta) throws IOException;
	protected abstract D createDataStoreFromFile(File fasta, DataStoreProviderHint hint) throws IOException;
	protected abstract D createDataStoreFromFile(File fasta, DataStoreFilter filter) throws IOException;
	
	protected abstract D createDataStoreFromFile(File fasta, DataStoreProviderHint hint, DataStoreFilter filter) throws IOException;
	
	
	private D createDataStoreFromStream()
			throws FileNotFoundException, IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(fasta));
		try{
			return createDataStoreFromStream(in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
	protected abstract D createDataStoreFromStream(InputStream in)
			throws FileNotFoundException, IOException;
	
	protected abstract D createDataStoreFromStream(InputStream in,DataStoreProviderHint hint)
			throws FileNotFoundException, IOException ;
	
	private D createDataStoreFromStream(DataStoreProviderHint hint)
			throws FileNotFoundException, IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(fasta));
		try{
			return createDataStoreFromStream(in,hint);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	private D createDataStoreFromStream(DataStoreProviderHint hint, DataStoreFilter filter)
			throws FileNotFoundException, IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(fasta));
		try{
			return createDataStoreFromStream(hint, filter, in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}

	protected abstract D createDataStoreFromStream(DataStoreProviderHint hint,
			DataStoreFilter filter, InputStream in) throws IOException ;
	
	@Test
	public void streamImplementationSameNoMatterWhatHintProvided() throws FileNotFoundException, IOException{
		
		D defaultDataStore = createDataStoreFromStream();
		for(DataStoreProviderHint hint : DataStoreProviderHint.values()){
			assertEquals(createDataStoreFromStream(hint).getClass(), defaultDataStore.getClass());
		}
		
	}
	
	@Test
	public void optimizedMemoryUsesIndexedImpl() throws IOException, DataStoreException{
		D lowmem = createDataStoreFromFile(fasta,DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);
		assertEquals(getClassImplForRanomdAccessOptizeMem(),lowmem.getClass());
	}
	
	protected abstract Class<?> getClassImplForIterationOnly();
	protected abstract Class<?> getClassImplForRanomdAccessOptizeMem();
	@Test
	public void iterationOnlyImpl() throws IOException, DataStoreException{
		D indexed =createDataStoreFromFile(fasta,DataStoreProviderHint.ITERATION_ONLY);
		assertEquals(indexed.getClass(),getClassImplForIterationOnly());
	}

	
	private void assertContainSameData(D fromFile,
			D fromStream) throws DataStoreException {
		assertEquals(fromFile.getNumberOfRecords(), fromStream.getNumberOfRecords());
		StreamingIterator<F> iter=null;
		try{
			iter = fromFile.iterator();
			while(iter.hasNext()){
				F expected =iter.next();
				F actual = fromStream.get(expected.getId());
				assertEquals(expected, actual);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
