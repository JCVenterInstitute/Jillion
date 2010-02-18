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
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jcvi.Builder;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractSinglePhdFileVisitor<T extends Phd> implements Builder<T>, PhdFileVisitor{
    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    private List<NucleotideGlyph> bases = new ArrayList<NucleotideGlyph>();
    private List<PhredQuality> qualities = new ArrayList<PhredQuality>();
    private List<ShortGlyph> positions = new ArrayList<ShortGlyph>();
    private boolean initialized = false;
    private Properties comments;
    private String id;
    
    @Override
    public synchronized T build() {
       if(!initialized){
           throw new IllegalStateException("must visit phd file");
       }
        return buildPhd(id, bases, qualities, positions, comments);
    }

    protected abstract T buildPhd(String id,
            List<NucleotideGlyph> bases,
            List<PhredQuality> qualities,
            List<ShortGlyph> positions, 
            Properties comments);

    @Override
    public synchronized void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        checkNotYetInitialized();
       bases.add(base);
       qualities.add(quality);
       positions.add(PEAK_FACTORY.getGlyphFor(tracePosition));            
    }



    private void checkNotYetInitialized() {
        if(initialized){
            throw new IllegalStateException("can only create 1 phd multiple phds detected");
        }
    }



    @Override
    public void visitBeginDna() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitBeginSequence(String id) {
        checkNotYetInitialized();
        this.id = id;
    }



    @Override
    public synchronized void visitComment(Properties comments) {
        checkNotYetInitialized();
        this.comments = comments;
    }



    @Override
    public synchronized void visitEndDna() {
        initialized =true;
        
    }



    @Override
    public synchronized void visitEndSequence() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitLine(String line) {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitEndOfFile() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitFile() {
        checkNotYetInitialized();
    }

}
