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
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.fasta.AbstractTestFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.qual.IndexedQualityFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;

public class TestQualityFastaFileDataStoreBuilder extends AbstractTestFastaFileDataStoreBuilder
<PhredQuality, QualitySequence, QualityFastaRecord, QualityFastaDataStore>{
	
	public TestQualityFastaFileDataStoreBuilder() throws IOException{
		super(new ResourceHelper(TestQualityFastaFileDataStoreBuilder.class), "files/19150.qual");
	}

	@Override
	protected QualityFastaDataStore createDataStoreFromFile(File fasta)
			throws IOException {
		return new QualityFastaFileDataStoreBuilder(fasta).build();

	}

	@Override
	protected QualityFastaDataStore createDataStoreFromStream(InputStream in)
			throws FileNotFoundException, IOException {

		return new QualityFastaFileDataStoreBuilder(in).build();

	}
	
	@Override
	protected QualityFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint) throws IOException {
		return new QualityFastaFileDataStoreBuilder(fasta)
						.hint(hint)	
						.build();
	}

	@Override
	protected QualityFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreFilter filter) throws IOException {
		return new QualityFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.build();
	}

	@Override
	protected QualityFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		return new QualityFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.hint(hint)	
		.build();
	}

	@Override
	protected Class<?> getClassImplForRanomdAccessOptizeMem() {
		return IndexedQualityFastaFileDataStore.Impl.class;
	}

	@Override
	protected Class<?> getClassImplForIterationOnly() {
		return LargeQualityFastaFileDataStore.class;
	}

	@Override
	protected QualityFastaDataStore createDataStoreFromStream(InputStream in,DataStoreProviderHint hint)
			throws FileNotFoundException, IOException {
		
			return new QualityFastaFileDataStoreBuilder(in)
											.hint(hint)
											.build();
		
	}
	
	@Override
	protected QualityFastaDataStore createDataStoreFromStream(DataStoreProviderHint hint,
			DataStoreFilter filter, InputStream in) throws IOException {
		return new QualityFastaFileDataStoreBuilder(in)
										.hint(hint)
										.filter(filter)
										.build();
	}
}
