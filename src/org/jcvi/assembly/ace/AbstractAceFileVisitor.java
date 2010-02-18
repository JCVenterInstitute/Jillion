/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractAceFileVisitor implements AceFileVisitor{

    private boolean initialized;
    
    public synchronized boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public synchronized void visitFile() {
        throwExceptionIfInitialized();
    }

    private synchronized void throwExceptionIfInitialized() {
        if(isInitialized()){
            throw new IllegalStateException("already initialized");
        }
    }
    @Override
    public synchronized void visitAssembledFromLine(String readId,
            SequenceDirection dir, int gappedStartOffset) {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitConsensusQualities() {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitBaseSegment(Range gappedConsensusRange, String readId) {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitHeader(int numberOfContigs, int totalNumberOfReads) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitQualityLine(int clearLeft,
            int clearRight, int alignLeft, int alignRight) {
        throwExceptionIfInitialized();       
    }

    @Override
    public synchronized void visitLine(String line) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitReadHeader(String readId, int gappedLength) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitTraceDescriptionLine(String traceName, String phdName,
            Date date) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public synchronized void visitBasesLine(String bases) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public synchronized void visitEndOfFile() {
        throwExceptionIfInitialized();        
        initialized = true;
        
    }

    @Override
    public synchronized void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String Data) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusTagComment(String comment) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusTagData(String data) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitEndConsensusTag() {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfInitialized();
        
    }

}
