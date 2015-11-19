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
package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.fasta.AbstractFastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractQualityFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link QualityFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link QualityFastaDataStore} is built
 * and the abstract method visitRecord(QualityFastaDataStore)
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractQualityFastaRecordVisitor  extends AbstractFastaRecordVisitor{
	
	
	public AbstractQualityFastaRecordVisitor(String id, String comment) {
		super(id,comment);
	}
	
	@Override
	protected void visitRecord(String id, String comment,
			String fullBody) {
		QualityFastaRecord record = new QualityFastaRecordBuilder(id, fullBody)
												.comment(comment)
												.build();
		visitRecord(record);
		
	}


	protected abstract void visitRecord(QualityFastaRecord fastaRecord);
	

}
