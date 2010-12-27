package org.jcvi.io;

import java.util.Iterator;

import org.jcvi.fasta.PeptideSequenceFastaRecord;


/*
 * {@code PeptideFastaRecordIO} is the interface for the
 * ObjectIO of {@link PeptideSequenceFastaRecord} objects. 
 * 
 * @author naxelrod
 */
 
public interface PeptideFastaRecordIO extends ObjectIO<PeptideSequenceFastaRecord> {

	// Reader
	Iterator<PeptideSequenceFastaRecord> iterator();
	
	// Writer
	public boolean write( PeptideSequenceFastaRecord sequence );
	public int write( Iterable<PeptideSequenceFastaRecord> sequences );
	public int write();

}
