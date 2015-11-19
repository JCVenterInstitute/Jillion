/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractProteinFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link ProteinFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link ProteinFastaRecord} is built
 * and the abstract method visitRecord(ProteinFastaRecord)
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractProteinFastaRecordVisitor implements FastaRecordVisitor{
	private final String id;
	private final String comment;
	private final ProteinSequenceBuilder sequenceBuilder = new ProteinSequenceBuilder();
	
	
	public AbstractProteinFastaRecordVisitor(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	@Override
	public final void visitBodyLine(String line) {
		sequenceBuilder.append(line);		
	}

	@Override
	public final void visitEnd() {
		ProteinFastaRecord record = new ProteinFastaRecordBuilder(id, sequenceBuilder.build())
												.comment(comment)
												.build();
		visitRecord(record);		
	}
	@Override
	public void halted() {
		//no-op				
	}
	
	protected abstract void visitRecord(ProteinFastaRecord fastaRecord);
	
}
