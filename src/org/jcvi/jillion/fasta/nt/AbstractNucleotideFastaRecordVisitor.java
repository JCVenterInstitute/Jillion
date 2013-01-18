package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.fasta.AbstractFastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractNucleotideFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link NucleotideSequenceFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link NucleotideSequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(NucleotideSequenceFastaRecord)}
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractNucleotideFastaRecordVisitor extends  AbstractFastaRecordVisitor{

	public AbstractNucleotideFastaRecordVisitor(String id, String comment) {
		super(id,comment);
	}

	
	protected abstract void visitRecord(NucleotideSequenceFastaRecord fastaRecord);

	@Override
	protected final  void visitRecord(String id, String optionalComment,
			String fullBody) {
		NucleotideSequenceFastaRecord record = new NucleotideSequenceFastaRecordBuilder(id, fullBody)
													.comment(optionalComment)
													.build();
		visitRecord(record);
	}
	
}