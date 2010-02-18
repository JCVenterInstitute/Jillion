/*
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.PrintWriter;
import java.util.Properties;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class PhdTrace implements PhdFileVisitor{

    private final PrintWriter out;
    
    
    public PhdTrace(){
        this(new PrintWriter(System.out));
    }
    /**
     * @param out the {@link PrintWriter}
     * to write messages to.
     */
    public PhdTrace(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        out.println("\t"+base + " " + quality + "  " + tracePosition);
        
    }

    @Override
    public void visitBeginDna() {
        out.println("DNA{");
        
    }

    @Override
    public void visitBeginSequence(String id) {
        out.println("phd for id = "+ id);
        
    }

    @Override
    public void visitComment(Properties comments) {
        out.println("comments = " + comments);
        
    }

    @Override
    public void visitEndDna() {
       out.println("}");
        
    }

    @Override
    public void visitEndSequence() {
        out.println(" end phd record");
        
    }

    @Override
    public void visitLine(String line) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void visitEndTag() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void visitBeginTag(String tagName) {
        // TODO Auto-generated method stub
        
    }

}
