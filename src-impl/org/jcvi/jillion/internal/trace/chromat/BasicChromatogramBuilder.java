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
/*
 * Created on Jan 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.Chromatogram;

public final class BasicChromatogramBuilder {
    
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[]{};
        private PositionSequence peaks;
        private NucleotideSequence basecalls;
        //default to empty confidences (which may happen if read is really
        //trashy
        private byte[] aQualities=EMPTY_BYTE_ARRAY;
        private byte[] cQualities=EMPTY_BYTE_ARRAY;
        private byte[] gConfidence=EMPTY_BYTE_ARRAY;
        private byte[] tQualities=EMPTY_BYTE_ARRAY;

        private short[] aPositions;
        private short[] cPositions;
        private short[] gPositions;
        private short[] tPositions;

        private Map<String,String> comments;
        
        private String id;
        /**
         * Set the id of this Builder.
         * @param id the id of this Builder; can not be null.
         * @throws NullPointerException if id is null.
         */
        public BasicChromatogramBuilder(String id){
        	if(id ==null){
        		throw new NullPointerException("id can not be null");
        	}
        	this.id=id;
        }
        /**
         * Builds a builder starting with the following default values.
         * @param basecalls the basecalls may be null.
         * @param peaks the peaks cannot be null.
         * @param channelGroup the channel group containing
         *  position and confidence data on all 4 channels can not be null.
         * @param properties the properties may be null.
         */
        private BasicChromatogramBuilder(String id, NucleotideSequence basecalls,
        		PositionSequence peaks, ChannelGroup channelGroup, Map<String,String> properties){
            id(id);
        	basecalls(basecalls);
            peaks(peaks);
            channelGroup(channelGroup);           
            comments(properties);
        }
        
        private void channelGroup(ChannelGroup channelGroup){
        	 aQualities =toByteArray(channelGroup.getAChannel().getQualitySequence());
             aPositions =channelGroup.getAChannel().getPositionSequence().toArray();            
             cQualities= toByteArray(channelGroup.getCChannel().getQualitySequence());
             cPositions = channelGroup.getCChannel().getPositionSequence().toArray();
             gConfidence = toByteArray(channelGroup.getGChannel().getQualitySequence());
             gPositions = channelGroup.getGChannel().getPositionSequence().toArray();
             tQualities =toByteArray(channelGroup.getTChannel().getQualitySequence());
             tPositions = channelGroup.getTChannel().getPositionSequence().toArray();
        }
        private byte[] toByteArray(QualitySequence sequence){
        	byte[] array = new byte[(int)sequence.getLength()];
        	int i=0;
        	for(PhredQuality q : sequence){
        		array[i]=q.getQualityScore();
        		i++;
        	}
        	return array;
        }
        
       
        
        public BasicChromatogramBuilder(Chromatogram copy){
		       this(copy.getId(), copy.getNucleotideSequence(),
		       copy.getPeakSequence(),
		       copy.getChannelGroup(),
		       copy.getComments()
		       );
        
        }
        public final PositionSequence peaks() {
            return peaks;
        }

        public final BasicChromatogramBuilder peaks(PositionSequence peaks) {
            this.peaks = peaks;
            return this;
        }
        public final String id(){
        	return id;
        }
        
        public BasicChromatogramBuilder id(String id){
        	if(id ==null){
        		throw new NullPointerException("id can not be null");
        	}
        	this.id = id;
        	return this;
        }
        public NucleotideSequence basecalls() {
            return basecalls;
        }

        public BasicChromatogramBuilder basecalls(NucleotideSequence basecalls) {
            this.basecalls = basecalls;
            return this;
        }

        public byte[] aQualities() {
            return Arrays.copyOf(aQualities, aQualities.length);
        }

        public final BasicChromatogramBuilder aQualities(byte[] qualities) {
            aQualities = Arrays.copyOf(qualities, qualities.length);
            return this;
        }

        public byte[] cQualities() {
            return Arrays.copyOf(cQualities, cQualities.length);
        }

        public BasicChromatogramBuilder cQualities(byte[] qualities) {
            cQualities = Arrays.copyOf(qualities, qualities.length);
            return this;
        }

        public byte[] gQualities() {
            return Arrays.copyOf(gConfidence, gConfidence.length);
        }

        public BasicChromatogramBuilder gQualities(byte[] qualities) {
            gConfidence = Arrays.copyOf(qualities, qualities.length);
            return this;
        }

        public byte[] tQualities() {
            return Arrays.copyOf(tQualities, tQualities.length);
        }

        public BasicChromatogramBuilder tQualities(byte[] qualities) {
            tQualities = Arrays.copyOf(qualities, qualities.length);
            return this;
        }

        public short[] aPositions() {
            if(aPositions ==null){
                return new short[]{};
            }
            return Arrays.copyOf(aPositions, aPositions.length);
        }

        public BasicChromatogramBuilder aPositions(short[] positions) {
            aPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public short[] cPositions() {
            if(cPositions ==null){
                return new short[]{};
            }
            return Arrays.copyOf(cPositions, cPositions.length);
        }

        public BasicChromatogramBuilder cPositions(short[] positions) {
            cPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public short[] gPositions() {
            if(gPositions ==null){
                return new short[]{};
            }
            return Arrays.copyOf(gPositions, gPositions.length);
        }

        public BasicChromatogramBuilder gPositions(short[] positions) {
            gPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public short[] tPositions() {
            if(tPositions ==null){
                return new short[]{};
            }
            return Arrays.copyOf(tPositions, tPositions.length);
        }

        public BasicChromatogramBuilder tPositions(short[] positions) {
            tPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public Map<String,String> comments() {
            return comments ==null? Collections.<String,String>emptyMap() :new HashMap<String, String>(comments);
        }

        public BasicChromatogramBuilder comments(Map<String,String> comments) {
            this.comments = new HashMap<String, String>(comments);
            return this;
        }

        private QualitySequence generateQualities(ChannelGroup channelGroup) {
        	int length = (int)basecalls.getLength();
            QualitySequenceBuilder builder = new QualitySequenceBuilder(length);
            int i=0;
            for(Nucleotide base : basecalls){
            	QualitySequence qualitySequence = channelGroup.getChannel(base).getQualitySequence();
            	//only read as many qualities as we have...
                if(i == qualitySequence.getLength()){
                	break;
                }
                builder.append(qualitySequence.get(i));
                i++;
            }            
            return  builder.build();
        }
        
        public Chromatogram build() {
            final ChannelGroup channelGroup = new DefaultChannelGroup(
                    new DefaultChannel(aQualities(),aPositions()),
                    new DefaultChannel(cQualities(),cPositions()),
                    new DefaultChannel(gQualities(),gPositions()),
                    new DefaultChannel(tQualities(),tPositions()));
            
            return new BasicChromatogram(id,
                    basecalls(),
                    generateQualities(channelGroup),                        
                        peaks(),
                    channelGroup,
                    comments());
        }
}
