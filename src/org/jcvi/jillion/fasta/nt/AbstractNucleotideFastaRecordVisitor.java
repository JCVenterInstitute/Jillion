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
package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.fasta.AbstractFastaRecordVisitor;
/**
 * {@code AbstractNucleotideFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link NucleotideFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link NucleotideSequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(NucleotideFastaRecord)}
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

	
	protected abstract void visitRecord(NucleotideFastaRecord fastaRecord);

	@Override
	protected final  void visitRecord(String id, String optionalComment,
			String fullBody) {
		NucleotideFastaRecord record = new NucleotideFastaRecordBuilder(id, fullBody)
													.comment(optionalComment)
													.build();
		visitRecord(record);
	}
	
}
