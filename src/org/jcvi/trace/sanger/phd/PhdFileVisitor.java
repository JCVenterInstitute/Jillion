/*
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.Properties;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.TextFileVisitor;

public interface PhdFileVisitor extends TextFileVisitor{

    void visitBeginSequence(String id);
    
    void visitEndSequence();
    
    void visitComment(Properties comments);
    
    void visitBeginDna();
    
    void visitEndDna();
    
    void visitBasecall(NucleotideGlyph base, PhredQuality quality, int tracePosition);

    void visitBeginTag(String tagName);
    
    void visitEndTag();
    
}
