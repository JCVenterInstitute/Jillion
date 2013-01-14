package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;

public class SortFasta {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input.fasta");
		File sortedOutputFasta = new File("path/to/sorted/output.fasta");
		
		NucleotideSequenceFastaDataStore dataStore = new NucleotideSequenceFastaFileDataStoreBuilder(inputFasta)
														.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_MEMORY)
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
		NucleotideSequenceFastaRecordWriter out = new NucleotideSequenceFastaRecordWriterBuilder(sortedOutputFasta)
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
