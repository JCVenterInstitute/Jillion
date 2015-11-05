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
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code AbstractAceConsensusTagVisitor} is an
 * {@link AceConsensusTagVisitor} that collects the visit calls
 * for a single consensus tag and
 * populates an
 * {@link ConsensusAceTag} instance  
 * which will be sent to subclasses when it is fully built
 * via {@link #visitConsensusTag(ConsensusAceTag)}.
 * @author dkatzel
 *
 */
public abstract class AbstractAceConsensusTagVisitor implements AceConsensusTagVisitor{

	private final ConsensusAceTagBuilder consensusTagBuilder;
	
	public AbstractAceConsensusTagVisitor(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient){
		consensusTagBuilder = new ConsensusAceTagBuilder(id, 
	             type, creator, creationDate, Range.of(gappedStart, gappedEnd), isTransient);

	}
	@Override
	public void visitComment(String comment) {
		consensusTagBuilder.addComment(comment);
		
	}

	@Override
	public void visitData(String data) {
		consensusTagBuilder.appendData(data);
		
	}

	@Override
	public void visitEnd() {
		visitConsensusTag(consensusTagBuilder.build());
		
	}
	/**
	 * Visit the fully populated {@link ConsensusAceTag} instance.
	 * Called by {@link #visitEnd()}.
	 * 
	 * @param consensusTag the current {@link ConsensusAceTag} being visited;
	 * will never be null.
	 * 
	 */
	protected abstract void visitConsensusTag(ConsensusAceTag consensusTag);
	@Override
	public void halted() {
		//no-op		
	}

}
