/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

public abstract class AbstractSffFileVisitor implements SffFileVisitor {

    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        return true;
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
        return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        return true;
    }

    @Override
    public void visitEndOfFile() {

    }

    @Override
    public void visitFile() {

    }

}
