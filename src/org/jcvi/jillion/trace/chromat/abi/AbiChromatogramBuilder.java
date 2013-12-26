/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.chromat.abi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogramBuilder;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.Ab1LocalDate;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.Ab1LocalTime;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.ByteArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.DateTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.FloatArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.IntArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.ShortArrayTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.StringTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TimeTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.UserDefinedTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.rate.ScanRate;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.rate.ScanRateTaggedDataType;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.Chromatogram;

/**
 * {@code AbiChromatogramBuilder} uses the Builder pattern
 * to create a new {@link AbiChromatogram}
 * instance.
 * 
 * @author dkatzel
 *
 *
 */
public class AbiChromatogramBuilder implements Builder<AbiChromatogram>{

    private final BasicChromatogramBuilder currentBuilder;
    private final BasicChromatogramBuilder originalBuilder;
    private String id;
    /**
     * Create a new {@link AbiChromatogramBuilder} instance with
     * all fields except for the id unset.  In order
     * to successfully build a valid {@link AbiChromatogram}
     * object please use
     * the various setter methods
     * in this class to set everything
     * before calling {@link #build()}.
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null.
     * @throws NullPointerException if id is null.
     */
    public AbiChromatogramBuilder(String id){
    	currentBuilder = new BasicChromatogramBuilder(id);
    	originalBuilder = new BasicChromatogramBuilder(id);
    	this.id = id;
    }
    /**
     * Create a new {@link AbiChromatogramBuilder} instance
     * with the given id and all fields initially set
     * to the the values encoded in the given ab1 file.
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null. 
     * @param abiFile a AB1 encoded file that contains the initial values
     * that this builder should have; can not be null.
     * @throws IOException if there is a problem reading the file or if
     * the given file is not a valid ab1 encoded file.
     * @throws NullPointerException if either field is null.
     */
    public AbiChromatogramBuilder(String id, File abiFile) throws IOException{
    	AbiChromatogramBuilderVisitor visitor = new AbiChromatogramBuilderVisitor(id);
    	AbiChromatogramParser.create(abiFile).accept(visitor);
    	currentBuilder = visitor.currentBuilder;
    	originalBuilder = visitor.originalBuilder;
    	this.id=id;
    }
    /**
     * Create a new {@link AbiChromatogramBuilder} instance
     * with the given id and all fields initially set
     * to the the values encoded in the given ab1 encoded inputStream.
     * The {@link InputStream} will NOT be closed by this constructor,
     * client code must close the stream themselves after returning 
     * from this method (preferably in a finally block).
     * @param id the id for this {@link Chromatogram} object to have;
     * can not be null. 
     * @param abiFileStream a ab1 encoded {@link InputStream} that contains the initial values
     * that this builder should have; can not be null.
     * @throws IOException if there is a problem reading the file or
     * if the {@link InputStream} does not contain valid ab1 encoded data.
     * @throws NullPointerException if either field is null.
     */
    public AbiChromatogramBuilder(String id, InputStream abiFileStream) throws IOException{
    	AbiChromatogramBuilderVisitor visitor = new AbiChromatogramBuilderVisitor(id);
    	AbiChromatogramParser.create(abiFileStream).accept(visitor);
    	currentBuilder = visitor.currentBuilder;
    	originalBuilder = visitor.originalBuilder;
    	this.id=id;
    }
    
    public String id(){
    	return id;
    }
    /**
     * Changes the id of both
     * the current and original trace ids.
     * @param id the id of this chromatogram, usually the read name.
     */
    public AbiChromatogramBuilder id(String id){
    	this.id = id;
    	currentBuilder.id(id);
    	originalBuilder.id(id);
    	return this;
    }
    
    
    public final PositionSequence peaks() {
        return currentBuilder.peaks();
    }

    public AbiChromatogramBuilder peaks(short[] peaks) {
        currentBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence basecalls() {
        return currentBuilder.basecalls();
    }

    public AbiChromatogramBuilder basecalls(NucleotideSequence basecalls) {
        currentBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] aQualities() {
        return currentBuilder.aQualities();
    }

    public final AbiChromatogramBuilder aQualities(byte[] qualities) {
        currentBuilder.aQualities(qualities);
        return this;
    }

    public final byte[] cQualities() {
        return currentBuilder.cQualities();
    }

    public final AbiChromatogramBuilder cQualities(byte[] qualities) {
        currentBuilder.cQualities(qualities);
        return this;
    }

    public final byte[] gQualities() {
        return currentBuilder.gQualities();
    }

    public final AbiChromatogramBuilder gQualities(byte[] qualities) {
        currentBuilder.gQualities(qualities);
        return this;
    }

    public final byte[] tQualities() {
        return currentBuilder.tQualities();
    }

    public final AbiChromatogramBuilder tQualities(byte[] qualities) {
        currentBuilder.tQualities(qualities);
        return this;
    }

    public final short[] aPositions() {
        return currentBuilder.aPositions();
    }

    public final AbiChromatogramBuilder aPositions(short[] positions) {
        currentBuilder.aPositions(positions);
        return this;
    }

    public final short[] cPositions() {
        return currentBuilder.cPositions();
    }

    public final AbiChromatogramBuilder cPositions(short[] positions) {
        currentBuilder.cPositions(positions);
        return this;
    }

    public final short[] gPositions() {
        return currentBuilder.gPositions();
    }

    public final AbiChromatogramBuilder gPositions(short[] positions) {
        currentBuilder.gPositions(positions);
        return this;
    }

    public final short[] tPositions() {
        return currentBuilder.tPositions();
    }

    public final AbiChromatogramBuilder tPositions(short[] positions) {
        currentBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> comments() {
        return currentBuilder.comments();
    }

    public final AbiChromatogramBuilder comments(Map<String,String> comments) {
        currentBuilder.comments(comments);
        return this;
    }
    
    public AbiChromatogramBuilder originalPeaks(short[] peaks) {
        originalBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence originalBasecalls() {
        return originalBuilder.basecalls();
    }

    public AbiChromatogramBuilder originalBasecalls(NucleotideSequence basecalls) {
        originalBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] originalAQualities() {
        return originalBuilder.aQualities();
    }

    public final AbiChromatogramBuilder originalAQualities(byte[] qualities) {
        originalBuilder.aQualities(qualities);
        return this;
    }

    public final byte[] originalCQualities() {
        return originalBuilder.cQualities();
    }

    public final AbiChromatogramBuilder originalCQualities(byte[] qualities) {
        originalBuilder.cQualities(qualities);
        return this;
    }

    public final byte[] originalGQualities() {
        return originalBuilder.gQualities();
    }

    public final AbiChromatogramBuilder originalGQualities(byte[] qualities) {
        originalBuilder.gQualities(qualities);
        return this;
    }

    public final byte[] originalTQualities() {
        return originalBuilder.tQualities();
    }

    public final AbiChromatogramBuilder originalTQualities(byte[] qualities) {
        originalBuilder.tQualities(qualities);
        return this;
    }

    public final short[] originalAPositions() {
        return originalBuilder.aPositions();
    }

    public final AbiChromatogramBuilder originalAPositions(short[] positions) {
        originalBuilder.aPositions(positions);
        return this;
    }

    public final short[] originalCPositions() {
        return originalBuilder.cPositions();
    }

    public final AbiChromatogramBuilder originalCPositions(short[] positions) {
        originalBuilder.cPositions(positions);
        return this;
    }

    public final short[] originalGPositions() {
        return originalBuilder.gPositions();
    }

    public final AbiChromatogramBuilder originalGPositions(short[] positions) {
        originalBuilder.gPositions(positions);
        return this;
    }

    public final short[] originalTPositions() {
        return originalBuilder.tPositions();
    }

    public final AbiChromatogramBuilder originalTPositions(short[] positions) {
        originalBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> originalComments() {
        return originalBuilder.comments();
    }

    public final AbiChromatogramBuilder originalComments(Map<String,String> comments) {
        originalBuilder.comments(comments);
        return this;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AbiChromatogram build() {
        return new AbiChromatogramImp(id,
                currentBuilder.build(),
                this.originalBuilder.build());
    }

    private static class AbiChromatogramImp implements AbiChromatogram{

        private final Chromatogram delegate;
        private final Chromatogram originalChromatogram;
        private final String id;
        public AbiChromatogramImp(String id, Chromatogram delegate,Chromatogram originalChromatogram) {
            this.id = id;
        	this.delegate = delegate;
            this.originalChromatogram = originalChromatogram;
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
        public QualitySequence getQualitySequence() {
            return delegate.getQualitySequence();
        }

		@Override
		public PositionSequence getPeakSequence() {
			return delegate.getPeakSequence();
		}



		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ delegate.hashCode();
			return result;
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof AbiChromatogramImp)) {
				return false;
			}
			AbiChromatogramImp other = (AbiChromatogramImp) obj;			
			if (!delegate.equals(other.delegate)) {
				return false;
			}
			return true;
		}
        
    }
    
    
    public static class AbiChromatogramBuilderVisitor implements AbiChromatogramFileVisitor{

        private final BasicChromatogramBuilder currentBuilder;
        private final BasicChromatogramBuilder originalBuilder;
        
        public AbiChromatogramBuilderVisitor(String id){
        	currentBuilder = new BasicChromatogramBuilder(id);
        	originalBuilder = new BasicChromatogramBuilder(id);
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
            currentBuilder.aQualities(confidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitCConfidence(byte[] confidence) {
            currentBuilder.cQualities(confidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitGConfidence(byte[] confidence) {
            currentBuilder.gQualities(confidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitTConfidence(byte[] confidence) {
            currentBuilder.tQualities(confidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEnd() {
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
            currentBuilder.peaks(new PositionSequenceBuilder(peaks).build());        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitComments(Map<String, String> comments) {
            currentBuilder.comments(comments);        
            //copy comments to original?
            originalBuilder.comments(comments);
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
           //no-op
            
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
            originalBuilder.peaks(new PositionSequenceBuilder(originalPeaks).build());        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitOriginalAConfidence(byte[] originalConfidence) {
            originalBuilder.aQualities(originalConfidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitOriginalCConfidence(byte[] originalConfidence) {
            originalBuilder.cQualities(originalConfidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitOriginalGConfidence(byte[] originalConfidence) {
            originalBuilder.gQualities(originalConfidence);        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitOriginalTConfidence(byte[] originalConfidence) {
            originalBuilder.tQualities(originalConfidence);        
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
        public void visitTaggedDataRecord(UserDefinedTaggedDataRecord<?,?> record,
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


    }
}
