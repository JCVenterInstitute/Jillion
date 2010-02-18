/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
