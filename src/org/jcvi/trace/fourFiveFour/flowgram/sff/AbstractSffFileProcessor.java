/*
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFCommonHeader;

import org.jcvi.trace.fourFiveFour.flowgram.sff.SffFileVisitor;


public abstract class AbstractSffFileProcessor implements SffFileVisitor{

    private final SffFileVisitor parent;
    /**
     * @param parent
     */
    public AbstractSffFileProcessor(SffFileVisitor parent) {
        
        this.parent = parent;
    }

    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        if(parent !=null){
            return parent.visitCommonHeader(commonHeader);
        }
        return true;
    }

    

    public SffFileVisitor getParent() {
        return parent;
    }

    @Override
    public void visitEndOfFile() {
        if(parent !=null){
            parent.visitEndOfFile();
        }
    }

    @Override
    public void visitFile() {
        if(parent !=null){
            parent.visitFile();
        }
        
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
        if(parent !=null){
            return parent.visitReadData(readData);
        }
        return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        if(parent !=null){
            parent.visitReadHeader(readHeader);
        }
        return true;
    }

}
