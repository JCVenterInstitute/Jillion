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

package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;

/**
 * {@code SCFChromatogramFile} is a {@link SCFChromatogramFileVisitor} implementation
 * that once populated can function as a {@link SCFChromatogram}.
 * @author dkatzel
 *
 *
 */
public final class SCFChromatogramFile implements SCFChromatogram, SCFChromatogramFileVisitor{

    
    private SCFChromatogram delegate;
    private SCFChromatogramBuilder builder;
    
    /**
     * Create a new {@link SCFChromatogram} instance from the given
     * SCF encoded file.
     * @param scfFile the SCF encoded file to parse
     * @return a new {@link SCFChromatogram} instance containing data
     * from the given SCF file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static SCFChromatogram create(File scfFile) throws TraceDecoderException, IOException{
        return new SCFChromatogramFile(scfFile);
    }
    /**
     * Create a new {@link SCFChromatogram} instance from the given
     * SCF encoded InputStream, This method will close the input stream regardless
     * if this method returns or throws an exception.
     * @param scfInputStream the SCF encoded input stream to parse
     * @return a new {@link SCFChromatogram} instance containing data
     * from the given SCF file.
     * @throws FileNotFoundException if the file does not exist
     * @throws TraceDecoderException if the file is not correctly encoded.
     */
    public static SCFChromatogram create(InputStream scfInputStream) throws TraceDecoderException, IOException{
        try{
            return new SCFChromatogramFile(scfInputStream);
        }finally{
            IOUtil.closeAndIgnoreErrors(scfInputStream);
        }
    }
    /**
     * Create an "unset" SCFChromatogramFile which needs to be 
     * populated via {@link SCFChromatogramFileVisitor}
     * method calls.  While this is still being populated
     * via visitor method calls, this object is not thread safe.
     * @return a new SCFChromatogramFile instance that needs to be populated.
     */
    public static SCFChromatogramFile createUnset(){
        return new SCFChromatogramFile();
    }
    
    private SCFChromatogramFile(){
        builder = new SCFChromatogramBuilder();
    }
    
    private SCFChromatogramFile(File scfFile) throws TraceDecoderException, IOException{
       this();
       SCFChromatogramFileParser.parseSCFFile(scfFile, this);
    }
    private SCFChromatogramFile(InputStream scfInputStream) throws TraceDecoderException, IOException{
        this();
        SCFChromatogramFileParser.parseSCFFile(scfInputStream, this);
     }
    @Override
    public ChannelGroup getChannelGroup() {
        return delegate.getChannelGroup();
    }

    @Override
    public Map<String,String> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return delegate.getBasecalls();
    }

    @Override
    public QualityEncodedGlyphs getQualities() {
        return delegate.getQualities();
    }

    @Override
    public void visitBasecalls(String basecalls) {
        builder.basecalls(basecalls);
        
    }

    @Override
    public void visitPeaks(short[] peaks) {
        builder.peaks(peaks);
        
    }

    @Override
    public void visitAPositions(short[] positions) {
        builder.aPositions(positions);
        
    }

    @Override
    public void visitCPositions(short[] positions) {
        builder.cPositions(positions);
        
    }

    @Override
    public void visitGPositions(short[] positions) {
        builder.gPositions(positions);
        
    }

    @Override
    public void visitTPositions(short[] positions) {
        builder.tPositions(positions);
        
    }

    @Override
    public void visitAConfidence(byte[] confidence) {
        builder.aConfidence(confidence);
        
    }

    @Override
    public void visitCConfidence(byte[] confidence) {
        builder.cConfidence(confidence);
        
    }

    @Override
    public void visitGConfidence(byte[] confidence) {
        builder.gConfidence(confidence);
        
    }

    @Override
    public void visitTConfidence(byte[] confidence) {
        builder.tConfidence(confidence);
        
    }

    @Override
    public void visitComments(Map<String,String> comments) {
        builder.properties(comments);
        
    }

    @Override
    public void visitFile() {        
        
    }

    @Override
    public void visitEndOfFile() {
        delegate = builder.build();
        builder =null;
        
    }

    @Override
    public Peaks getPeaks() {
        return delegate.getPeaks();
    }

    @Override
    public int getNumberOfTracePositions() {
        return delegate.getNumberOfTracePositions();
    }

    @Override
    public void visitPrivateData(byte[] privateData) {
        builder.privateData(privateData);
        
    }

    @Override
    public void visitSubstitutionConfidence(byte[] confidence) {
        builder.substitutionConfidence(confidence);
        
    }

    @Override
    public void visitInsertionConfidence(byte[] confidence) {
        builder.insertionConfidence(confidence);
        
    }

    @Override
    public void visitDeletionConfidence(byte[] confidence) {
        builder.deletionConfidence(confidence);
        
    }

    @Override
    public PrivateData getPrivateData() {
        return delegate.getPrivateData();
    }

    @Override
    public Confidence getSubstitutionConfidence() {
        return delegate.getSubstitutionConfidence();
    }

    @Override
    public Confidence getInsertionConfidence() {
        return delegate.getInsertionConfidence();
    }

    @Override
    public Confidence getDeletionConfidence() {
        return delegate.getDeletionConfidence();
    }
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitNewTrace() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfTrace() {
        
    }

    
    
}
