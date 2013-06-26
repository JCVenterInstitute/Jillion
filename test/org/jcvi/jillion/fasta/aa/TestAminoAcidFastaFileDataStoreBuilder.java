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
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.AbstractTestFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.aa.IndexedAminoAcidSequenceFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.aa.LargeAminoAcidSequenceFastaFileDataStore;

public class TestAminoAcidFastaFileDataStoreBuilder extends AbstractTestFastaFileDataStoreBuilder
<AminoAcid, AminoAcidSequence, AminoAcidFastaRecord, AminoAcidFastaDataStore>{
	
	public TestAminoAcidFastaFileDataStoreBuilder() throws IOException{
		super(new ResourceHelper(TestAminoAcidFastaFileDataStoreBuilder.class), "files/example.aa.fasta");
	}

	@Override
	protected AminoAcidFastaDataStore createDataStoreFromFile(File fasta)
			throws IOException {
		return new AminoAcidFastaFileDataStoreBuilder(fasta).build();

	}

	@Override
	protected AminoAcidFastaDataStore createDataStoreFromStream(InputStream in)
			throws FileNotFoundException, IOException {

		return new AminoAcidFastaFileDataStoreBuilder(in).build();

	}
	
	@Override
	protected AminoAcidFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint) throws IOException {
		return new AminoAcidFastaFileDataStoreBuilder(fasta)
						.hint(hint)	
						.build();
	}

	@Override
	protected AminoAcidFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreFilter filter) throws IOException {
		return new AminoAcidFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.build();
	}

	@Override
	protected AminoAcidFastaDataStore createDataStoreFromFile(File fasta,
			DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		return new AminoAcidFastaFileDataStoreBuilder(fasta)
		.filter(filter)
		.hint(hint)	
		.build();
	}

	@Override
	protected Class<?> getClassImplForRanomdAccessOptizeMem() {
		return IndexedAminoAcidSequenceFastaFileDataStore.Impl.class;
	}

	@Override
	protected Class<?> getClassImplForIterationOnly() {
		return LargeAminoAcidSequenceFastaFileDataStore.class;
	}

	@Override
	protected AminoAcidFastaDataStore createDataStoreFromStream(InputStream in,DataStoreProviderHint hint)
			throws FileNotFoundException, IOException {
		
			return new AminoAcidFastaFileDataStoreBuilder(in)
											.hint(hint)
											.build();
		
	}
	
	@Override
	protected AminoAcidFastaDataStore createDataStoreFromStream(DataStoreProviderHint hint,
			DataStoreFilter filter, InputStream in) throws IOException {
		return new AminoAcidFastaFileDataStoreBuilder(in)
										.hint(hint)
										.filter(filter)
										.build();
	}
}
