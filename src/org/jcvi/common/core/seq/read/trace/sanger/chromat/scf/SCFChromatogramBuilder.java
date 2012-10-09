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
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import java.util.Arrays;
import java.util.Map;

import org.jcvi.common.core.seq.read.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * {@code SCFChromatogramBuilder} uses the Builder pattern
 * to create a new {@link SCFChromatogram} instance.
 * @author dkatzel
 *
 *
 */
public final class SCFChromatogramBuilder implements Builder<SCFChromatogram>{

    
    private QualitySequence substitutionConfidence;
    private QualitySequence insertionConfidence;
    private QualitySequence deletionConfidence;

    private byte[] privateData;
   
    private final BasicChromatogramBuilder basicBuilder;

    public SCFChromatogramBuilder(String id){
        basicBuilder = new BasicChromatogramBuilder(id);
    }
    
    public SCFChromatogramBuilder(Chromatogram copy){
       basicBuilder = new BasicChromatogramBuilder(copy);        
    }
    public SCFChromatogramBuilder(SCFChromatogram copy){
        this((Chromatogram)copy);
        this.substitutionConfidence =copy.getSubstitutionConfidence();
        this.deletionConfidence =copy.getDeletionConfidence();
        this.insertionConfidence =copy.getInsertionConfidence();
        privateData(copy.getPrivateData().getBytes());
     }
    /**
     * @return the substitutionConfidence
     */
    public QualitySequence substitutionConfidence() {
        return substitutionConfidence;
    }

    /**
     * @param substitutionConfidence the substitutionConfidence to set
     */
    public SCFChromatogramBuilder substitutionConfidence(byte[] substitutionConfidence) {
        this.substitutionConfidence = substitutionConfidence ==null
        		? null
        		: new QualitySequenceBuilder(substitutionConfidence).build();
        return this;
    }

    /**
     * @return the insertionConfidence
     */
    public QualitySequence insertionConfidence() {
        return insertionConfidence;
    }

    /**
     * @param insertionConfidence the insertionConfidence to set
     */
    public SCFChromatogramBuilder insertionConfidence(byte[] insertionConfidence) {
        this.insertionConfidence = insertionConfidence ==null
        		? null
        	:  	new QualitySequenceBuilder(insertionConfidence).build();
        return this;
    }

    /**
     * @return the deletionConfidence
     */
    public QualitySequence deletionConfidence() {
        return deletionConfidence;
    }

    /**
     * @param deletionConfidence the deletionConfidence to set
     */
    public SCFChromatogramBuilder deletionConfidence(byte[] deletionConfidence) {
        this.deletionConfidence = deletionConfidence ==null?null:
        	new QualitySequenceBuilder(deletionConfidence).build();
        
        return this;
    }

    /**
     * @return the privateData
     */
    public byte[] privateData() {
        return privateData==null? null:Arrays.copyOf(privateData, privateData.length);
    }

    /**
     * @param privateData the privateData to set
     */
    public SCFChromatogramBuilder privateData(byte[] privateData) {
        this.privateData = privateData==null? null:Arrays.copyOf(privateData, privateData.length);
        return this;
    }

    public SCFChromatogram build() {
        Chromatogram basicChromo = basicBuilder.build();
        return new SCFChromatogramImpl(basicChromo,
                substitutionConfidence(),
                insertionConfidence(),
                deletionConfidence(),
                createPrivateData());
    }
   
    private PrivateData createPrivateData() {
        if(privateData() ==null){
            return null;
        }
        return new PrivateDataImpl(privateData());
    }

    public final PositionSequence peaks() {
        return basicBuilder.peaks();
    }

    public SCFChromatogramBuilder peaks(short[] peaks) {
        basicBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence basecalls() {
        return basicBuilder.basecalls();
    }

    public SCFChromatogramBuilder basecalls(NucleotideSequence basecalls) {
        basicBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] aConfidence() {
        return basicBuilder.aConfidence();
    }

    public final SCFChromatogramBuilder aConfidence(byte[] confidence) {
        basicBuilder.aConfidence(confidence);
        return this;
    }

    public final byte[] cConfidence() {
        return basicBuilder.cConfidence();
    }

    public final SCFChromatogramBuilder cConfidence(byte[] confidence) {
        basicBuilder.cConfidence(confidence);
        return this;
    }

    public final byte[] gConfidence() {
        return basicBuilder.gConfidence();
    }

    public final SCFChromatogramBuilder gConfidence(byte[] confidence) {
        basicBuilder.gConfidence(confidence);
        return this;
    }

    public final byte[] tConfidence() {
        return basicBuilder.tConfidence();
    }

    public final SCFChromatogramBuilder tConfidence(byte[] confidence) {
        basicBuilder.tConfidence(confidence);
        return this;
    }

    public final short[] aPositions() {
        return basicBuilder.aPositions();
    }

    public final SCFChromatogramBuilder aPositions(short[] positions) {
        basicBuilder.aPositions(positions);
        return this;
    }

    public final short[] cPositions() {
        return basicBuilder.cPositions();
    }

    public final SCFChromatogramBuilder cPositions(short[] positions) {
        basicBuilder.cPositions(positions);
        return this;
    }

    public final short[] gPositions() {
        return basicBuilder.gPositions();
    }

    public final SCFChromatogramBuilder gPositions(short[] positions) {
        basicBuilder.gPositions(positions);
        return this;
    }

    public final short[] tPositions() {
        return basicBuilder.tPositions();
    }

    public final SCFChromatogramBuilder tPositions(short[] positions) {
        basicBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> properties() {
        return basicBuilder.properties();
    }

    public final SCFChromatogramBuilder properties(Map<String,String> properties) {
        basicBuilder.properties(properties);
        return this;
    }

}
