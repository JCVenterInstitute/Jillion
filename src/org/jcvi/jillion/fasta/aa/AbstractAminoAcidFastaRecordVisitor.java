package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractAminoAcidFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link AminoAcidSequenceFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link AminoAcidSequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(AminoAcidSequenceFastaRecord)}
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractAminoAcidFastaRecordVisitor implements FastaRecordVisitor{
	private final String id;
	private final String comment;
	private final AminoAcidSequenceBuilder sequenceBuilder = new AminoAcidSequenceBuilder();
	
	
	public AbstractAminoAcidFastaRecordVisitor(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	@Override
	public final void visitBodyLine(String line) {
		sequenceBuilder.append(line);		
	}

	@Override
	public final void visitEnd() {
		AminoAcidSequenceFastaRecord record = new AminoAcidSequenceFastaRecordBuilder(id, sequenceBuilder.build())
												.comment(comment)
												.build();
		visitRecord(record);		
	}
	
	protected abstract void visitRecord(AminoAcidSequenceFastaRecord fastaRecord);
	
}