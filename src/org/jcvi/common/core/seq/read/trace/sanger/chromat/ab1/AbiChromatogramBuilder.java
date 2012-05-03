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

package org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChannelGroup;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.Ab1LocalDate;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.Ab1LocalTime;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.ByteArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.DateTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.FloatArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.IntArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.ShortArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.StringTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.TimeTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.UserDefinedTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.rate.ScanRate;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.rate.ScanRateTaggedDataType;
import org.jcvi.common.core.symbol.pos.SangerPeak;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public class AbiChromatogramBuilder implements AbiChromatogramFileVisitor, org.jcvi.common.core.util.Builder<AbiChromatogram>{

    private final BasicChromatogramBuilder currentBuilder;
    private final BasicChromatogramBuilder originalBuilder;
    private List<Nucleotide> channelOrder;
    private String id;
    AbiChromatogramBuilder(String id){
    	currentBuilder = new BasicChromatogramBuilder(id);
    	originalBuilder = new BasicChromatogramBuilder(id);
    	this.id = id;
    }
    
    
    public String id(){
    	return id;
    }
    
    public AbiChromatogramBuilder id(String id){
    	this.id = id;
    	return this;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAPositions(short[] positions) {
        currentBuilder.aPositions(positions);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCPositions(short[] positions) {
        currentBuilder.cPositions(positions);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGPositions(short[] positions) {
        currentBuilder.gPositions(positions);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTPositions(short[] positions) {
        currentBuilder.tPositions(positions);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitAConfidence(byte[] confidence) {
        currentBuilder.aConfidence(confidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitCConfidence(byte[] confidence) {
        currentBuilder.cConfidence(confidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGConfidence(byte[] confidence) {
        currentBuilder.gConfidence(confidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTConfidence(byte[] confidence) {
        currentBuilder.tConfidence(confidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitNewTrace() {
     // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfTrace() {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitBasecalls(NucleotideSequence basecalls) {
       currentBuilder.basecalls(basecalls);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitPeaks(short[] peaks) {
        currentBuilder.peaks(peaks);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitComments(Map<String, String> comments) {
        currentBuilder.properties(comments);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitOriginalBasecalls(NucleotideSequence originalBasecalls) {
        originalBuilder.basecalls(originalBasecalls);     
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitChannelOrder(List<Nucleotide> order) {
        channelOrder = new ArrayList<Nucleotide>(order);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitPhotometricData(short[] rawTraceData, int opticalFilterId) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGelVoltageData(short[] gelVoltage) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGelCurrentData(short[] gelCurrent) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitElectrophoreticPower(short[] electrophoreticPowerData) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitGelTemperatureData(short[] gelTemp) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitOriginalPeaks(short[] originalPeaks) {
        originalBuilder.peaks(originalPeaks);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitOriginalAConfidence(byte[] originalConfidence) {
        originalBuilder.aConfidence(originalConfidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitOriginalCConfidence(byte[] originalConfidence) {
        originalBuilder.aConfidence(originalConfidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitOriginalGConfidence(byte[] originalConfidence) {
        originalBuilder.gConfidence(originalConfidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitOriginalTConfidence(byte[] originalConfidence) {
        originalBuilder.tConfidence(originalConfidence);        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitScaleFactors(short aScale, short cScale, short gScale,
            short tScale) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(ByteArrayTaggedDataRecord record,
            byte[] data) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(UserDefinedTaggedDataRecord record,
            byte[] data) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(ScanRateTaggedDataType record,
            ScanRate scanRate) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(ShortArrayTaggedDataRecord record,
            short[] data) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(IntArrayTaggedDataRecord record,
            int[] data) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(FloatArrayTaggedDataRecord record,
            float[] data) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(StringTaggedDataRecord record, String data) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(TimeTaggedDataRecord record,
            Ab1LocalTime time) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(DateTaggedDataRecord record,
            Ab1LocalDate date) {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AbiChromatogram build() {
        return new AbiChromatogramImp(id,
                currentBuilder.build(),
                this.originalBuilder.build(), 
                channelOrder);
    }

    private static class AbiChromatogramImp implements AbiChromatogram{

        private final Chromatogram delegate;
        private final Chromatogram originalChromatogram;
        private final List<Nucleotide> channelOrder;
        private final String id;
        public AbiChromatogramImp(String id, Chromatogram delegate,Chromatogram originalChromatogram, List<Nucleotide> channelOrder) {
            this.id = id;
        	this.delegate = delegate;
            this.originalChromatogram = originalChromatogram;
            this.channelOrder = channelOrder;
        }
        
        

        @Override
		public String getId() {
			return id;
		}



		/**
         * @return the originalChromatogram
         */
        @Override
        public Chromatogram getOriginalChromatogram() {
            return originalChromatogram;
        }



        /**
         * @return the channelOrder
         */
        @Override
        public List<Nucleotide> getChannelOrder() {
            return channelOrder;
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
        public Map<String, String> getComments() {
            return delegate.getComments();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public SangerPeak getPeaks() {
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
        public NucleotideSequence getNucleotideSequence() {
            return delegate.getNucleotideSequence();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public QualitySequence getQualities() {
            return delegate.getQualities();
        }
        
    }
}
