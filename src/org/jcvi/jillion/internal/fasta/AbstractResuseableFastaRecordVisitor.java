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
package org.jcvi.jillion.internal.fasta;

import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractResuseableFastaRecordVisitor}
 * is a {@link FastaRecordVisitor}
 * that gathers consecutive calls to
 * {@link #visitBodyLine(String)} to compile the entire
 * body of a fasta record.  This class can 
 * be reused by resetting the current id and comment
 * using {@link #prepareNewRecord(String, String)}
 * so we don't create new instances for each
 * fasta record to be visited. 
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractResuseableFastaRecordVisitor implements FastaRecordVisitor{
	private String currentId;
	private String currentComment;
	private StringBuilder builder;
	
	public final void prepareNewRecord(String id, String optionalComment){
		this.currentId = id;
		this.currentComment = optionalComment;
		builder = new StringBuilder();
	}
	@Override
	public final void visitBodyLine(String line) {
		builder.append(line);
		
	}

	@Override
	public final void visitEnd() {
		visitRecord(currentId, currentComment, builder.toString());		
	}
	@Override
	public void halted() {
		//no-op				
	}
	public abstract void visitRecord(String id, String optionalComment, String fullBody);
}

