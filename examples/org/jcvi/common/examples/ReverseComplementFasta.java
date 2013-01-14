package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public class ReverseComplementFasta {

	public static void main(String[] args) throws DataStoreException, IOException {
		File inputFasta = new File("path/to/input/fasta");
		File reverseComplimentOutputFasta = new File("/path/to/output.sorted.fasta");
		
		NucleotideSequenceFastaDataStore dataStore = new NucleotideSequenceFastaFileDataStoreBuilder(inputFasta)
														.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
														.build();
		NucleotideSequenceFastaRecordWriter out = new NucleotideSequenceFastaRecordWriterBuilder(reverseComplimentOutputFasta)
															.build();

		StreamingIterator<NucleotideSequenceFastaRecord> iter=null;
		try {
			iter =dataStore.iterator();
			while(iter.hasNext()){
				NucleotideSequenceFastaRecord record =iter.next();
				NucleotideSequence reverseSequence = new NucleotideSequenceBuilder(record.getSequence())
															.reverseComplement()
															.build();
				out.write(record.getId(), reverseSequence, record.getComment());
			}
		} finally{
			IOUtil.closeAndIgnoreErrors(iter,out);
		}		
	}
}
