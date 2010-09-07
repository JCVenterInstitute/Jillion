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

package org.jcvi.trace.sanger.chromatogram.ztr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;

/**
 * @author dkatzel
 *
 *
 */
public class ZTRChromatogramFile implements ZTRChromatogramFileVisitor, ZTRChromatogram{

    private ZTRChromatogram delegate;
    private ZTRChromatogramBuilder builder;
    public ZTRChromatogramFile(){
        builder = new ZTRChromatogramBuilder();
    }
    public ZTRChromatogramFile(File ztrFile) throws FileNotFoundException, TraceDecoderException{
        this();
        ZTRChromatogramFileParser.parseZTRFile(ztrFile, this);
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        delegate = builder.build();
        builder =null;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBasecalls(String basecalls) {
        builder.basecalls(basecalls);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitPeaks(short[] peaks) {
        builder.peaks(peaks);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitClipRange(Range clipRange) {
        builder.clip(clipRange);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitComments(Properties comments) {
        builder.properties(comments);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAPositions(short[] positions) {
        builder.aPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCPositions(short[] positions) {
       builder.cPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGPositions(short[] positions) {
        builder.gPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTPositions(short[] positions) {
        builder.tPositions(positions);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public ChannelGroup getChannelGroup() {        
        return delegate.getChannelGroup();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Properties getProperties() {
        return delegate.getProperties();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Peaks getPeaks() {
        return delegate.getPeaks();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfTracePositions() {
        return delegate.getNumberOfTracePositions();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return delegate.getBasecalls();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return delegate.getQualities();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getClip() {
        return delegate.getClip();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAConfidence(byte[] confidence) {
        builder.aConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCConfidence(byte[] confidence) {
        builder.cConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGConfidence(byte[] confidence) {
        builder.gConfidence(confidence);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTConfidence(byte[] confidence) {
        builder.tConfidence(confidence);
        
    }
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }
}
