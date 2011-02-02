package org.jcvi.io;

import java.util.Iterator;

import org.jcvi.fastX.fasta.aa.AminoAcidSequenceFastaRecord;


/*
 * {@code PeptideFastaRecordIO} is the interface for the
 * ObjectIO of {@link PeptideSequenceFastaRecord} objects. 
 * 
 * @author naxelrod
 */
 
public interface PeptideFastaRecordIO extends ObjectIO<AminoAcidSequenceFastaRecord> {

	// Reader
	Iterator<AminoAcidSequenceFastaRecord> iterator();
	
	// Writer
	public boolean write( AminoAcidSequenceFastaRecord sequence );
	public int write( Iterable<AminoAcidSequenceFastaRecord> sequences );
	public int write();

}
