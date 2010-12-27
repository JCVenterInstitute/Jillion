package org.jcvi.io;

import java.util.Iterator;

import org.jcvi.fasta.NucleotideSequenceFastaRecord;


/*
 * {@code NucleotideFastaRecordIO} is the interface for the ObjectIO
 * of {@link NucleotideSequenceFastaRecord} objects. 
 * 
 * @author naxelrod
 */
 
public interface NucleotideFastaRecordIO extends ObjectIO<NucleotideSequenceFastaRecord> {

	// Reader
	Iterator<NucleotideSequenceFastaRecord> iterator();
	
	// Writer
	public boolean write( NucleotideSequenceFastaRecord sequence );
	public int write( Iterable<NucleotideSequenceFastaRecord> sequences );
	public int write();

}
