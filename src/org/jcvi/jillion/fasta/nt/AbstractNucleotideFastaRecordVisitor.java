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

import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.AbstractFastaRecordVisitor;
/**
 * Abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link NucleotideFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link NucleotideFastaRecord} is built
 * and the abstract method visitRecord(NucleotideFastaRecord)
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractNucleotideFastaRecordVisitor extends  AbstractFastaRecordVisitor{

	private final boolean turnOffCompression;

	public AbstractNucleotideFastaRecordVisitor(String id, String comment) {
		this(id,comment, false);
	}

	/**
	 *  Create new visitor instance for a single fasta record with the given id and comment.
	 * @param id the id of the record.
	 * @param comment the optional comment (may be null).
	 * @param turnOffCompression turn off sequence compression for a runtime
	 *                           performance improvement but at the cost of taking up more memory.
	 *
	 * @since 5.3.3
	 */
	public AbstractNucleotideFastaRecordVisitor(String id, String comment, boolean turnOffCompression) {
		super(id,comment);
		this.turnOffCompression = turnOffCompression;
	}


	protected abstract void visitRecord(NucleotideFastaRecord fastaRecord);

	@Override
	protected final  void visitRecord(String id, String optionalComment,
			String fullBody) {
		NucleotideFastaRecord record = new NucleotideFastaRecordBuilder(id,
																new NucleotideSequenceBuilder(fullBody)
																	.turnOffDataCompression(turnOffCompression).build())
													.comment(optionalComment)
													.build();
		visitRecord(record);
	}
	
}
