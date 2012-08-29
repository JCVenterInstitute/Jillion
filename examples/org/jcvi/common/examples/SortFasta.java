package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fastx.fasta.nt.IndexedNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class SortFasta {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("/local/netapp_scratch/dkatzel/draft_submission_validation_comparisons/swiv/INS/NIGSP_INS_00077.fastacas2consed.ace.1.consensus.fasta");
		File sortedOutputFasta = new File("/local/netapp_scratch/dkatzel/draft_submission_validation_comparisons/swiv/INS/NIGSP_INS_00077.fastacas2consed.ace.1.consensus.fasta.sorted");
		
		NucleotideSequenceFastaDataStore dataStore = IndexedNucleotideFastaFileDataStore.create(inputFasta);
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
		NucleotideSequenceFastaRecordWriter out = new DefaultNucleotideSequenceFastaRecordWriter.Builder(sortedOutputFasta)
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
