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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.IOException;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.assembly.tigr.tasm.TasmContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;

public class TestDefaultTasmDataStore extends AbstractTestTasmDataStore{

	static{
		ResourceHelper resources = new ResourceHelper(TestDefaultTasmDataStore.class);

		NucleotideFastaDataStore fullLengthFastas;
		try {
			fullLengthFastas = new NucleotideFastaFileDataStoreBuilder(
					resources.getFile("files/giv-15050.fasta")).hint(
					DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
					.build();
			
			contigDataStore = new TigrContigFileDataStoreBuilder(
					resources.getFile("files/giv-15050.contig"),
					fullLengthFastas).build();
			tasmDataStore = new TasmContigFileDataStoreBuilder(resources.getFile("files/giv-15050.tasm"),	fullLengthFastas)
									.build();
		} catch (IOException e) {
			throw new IllegalStateException("error creating datastores",e);
		}
	}

}
