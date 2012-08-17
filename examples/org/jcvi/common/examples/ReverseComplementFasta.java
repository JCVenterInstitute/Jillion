package org.jcvi.common.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.LargeNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordFactory2;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class ReverseComplementFasta {

	public static void main(String[] args) throws FileNotFoundException, DataStoreException {
		File inputFasta = new File("path/to/input/fasta");
		File reverseComplimentOutputFasta = new File("/path/to/output.sorted.fasta");
		
		NucleotideSequenceFastaDataStore dataStore = LargeNucleotideSequenceFastaFileDataStore.create(inputFasta);
		PrintWriter out = new PrintWriter(reverseComplimentOutputFasta);
		StreamingIterator<NucleotideSequenceFastaRecord> iter=null;
		try {
			iter =dataStore.iterator();
			while(iter.hasNext()){
				NucleotideSequenceFastaRecord record =iter.next();
				NucleotideSequence reverseSequence = new NucleotideSequenceBuilder(record.getSequence())
															.reverseComplement()
															.build();
				
				NucleotideSequenceFastaRecord reverseRecord = NucleotideSequenceFastaRecordFactory2.create(
														record.getId(), 
														reverseSequence,
														record.getComment()); 			
				//formattedString contains newline already
				//so use .print() instead of .println()
				out.print(reverseRecord.toFormattedString());
			}
		} finally{
			IOUtil.closeAndIgnoreErrors(iter,out);
		}		
	}
}
