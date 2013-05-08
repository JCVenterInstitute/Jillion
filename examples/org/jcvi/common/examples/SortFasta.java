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
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriterBuilder;

public class SortFasta {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input.fasta");
		File sortedOutputFasta = new File("path/to/sorted/output.fasta");
		
		NucleotideFastaDataStore dataStore = new NucleotideFastaFileDataStoreBuilder(inputFasta)
														.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
														.build();
		SortedSet<String> sortedIds = new TreeSet<String>();
		StreamingIterator<String> iter=null;
		try {
			iter =dataStore.idIterator();
			while(iter.hasNext()){
				sortedIds.add(iter.next());
			}
		} finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		NucleotideFastaRecordWriter out = new NucleotideFastaRecordWriterBuilder(sortedOutputFasta)
												.build();
		try{
			for(String id : sortedIds){
				out.write(dataStore.get(id));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(out,dataStore);
		}
		
	}

}
