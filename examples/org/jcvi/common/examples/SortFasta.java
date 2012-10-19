package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaFileDataStoreFactory;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreType;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class SortFasta {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input.fasta");
		File sortedOutputFasta = new File("path/to/sorted/output.fasta");
		
		NucleotideSequenceFastaDataStore dataStore = NucleotideSequenceFastaFileDataStoreFactory.create(inputFasta, FastaFileDataStoreType.INDEXED);
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
			IOUtil.closeAndIgnoreErrors(out);
		}
		
	}

}
