/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.Properties;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class AbstractPhdFileVisitor implements PhdFileVisitor{

    private boolean initialized;
    
    private synchronized void throwExceptionIfAlreadyInitialized(){
        if(initialized){
            throw new IllegalStateException("already initialized");
        }
    }
    @Override
    public synchronized void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        throwExceptionIfAlreadyInitialized();        
    }

    @Override
    public synchronized void visitBeginDna() {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitBeginSequence(String id) {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitBeginTag(String tagName) {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitComment(Properties comments) {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitEndDna() {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitEndSequence() {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitEndTag() {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitLine(String line) {
        throwExceptionIfAlreadyInitialized();
        
    }

    @Override
    public synchronized void visitEndOfFile() {
        throwExceptionIfAlreadyInitialized();
        initialized = true;
        
    }

    @Override
    public synchronized void visitFile() {
        throwExceptionIfAlreadyInitialized();
        
    }

}
