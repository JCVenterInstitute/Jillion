/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.jcvi.Range;

public class DefaultAceFileTagMap extends AbstractAceFileVisitor implements AceTagMap {

    private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
    private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
    private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
    
    private DefaultConsensusAceTag.Builder consensusTagBuilder;
    
    private synchronized void  checkAlreadyInitialized(){
        if(!isInitialized()){
            throw new IllegalStateException("not yet initialized");
        } 
    }
    
    @Override
    public synchronized List<ConsensusAceTag> getConsensusTags() {
        checkAlreadyInitialized();
        return Collections.unmodifiableList(consensusTags);
    }

    @Override
    public synchronized List<ReadAceTag> getReadTags() {
        checkAlreadyInitialized();
        return Collections.unmodifiableList(readTags);
    }

    @Override
    public synchronized List<WholeAssemblyAceTag> getWholeAssemblyTags() {
        checkAlreadyInitialized();
        return Collections.unmodifiableList(wholeAssemblyTags);
    }


    @Override
    public void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        super.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
        consensusTagBuilder = new DefaultConsensusAceTag.Builder(id, 
                type, creator, creationDate, Range.buildRange(gappedStart, gappedEnd), isTransient);

    }

    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String data) {
        super.visitWholeAssemblyTag(type, creator, creationDate, data);
        wholeAssemblyTags.add(new DefaultWholeAssemblyAceTag(type, creator, creationDate, data));
    }
    

    @Override
    public void visitConsensusTagComment(String comment) {
        super.visitConsensusTagComment(comment);
        consensusTagBuilder.addComment(comment);

    }

    @Override
    public void visitConsensusTagData(String data) {
        super.visitConsensusTagData(data);
        consensusTagBuilder.appendData(data);

    }

   

    @Override
    public void visitEndConsensusTag() {
        super.visitEndConsensusTag();
        consensusTags.add(consensusTagBuilder.build());

    }

   

    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        super.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
        readTags.add(new DefaultReadAceTag(id, type, creator, creationDate, 
                Range.buildRange(gappedStart,gappedEnd), isTransient));

    }

   

}
