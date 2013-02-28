package org.jcvi.jillion.assembly.ace;

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
	 * Called by {@link #visitEnd()}
	 * @param consensusTag
	 */
	protected abstract void visitConsensusTag(ConsensusAceTag consensusTag);
	@Override
	public void halted() {
		//no-op		
	}

}
