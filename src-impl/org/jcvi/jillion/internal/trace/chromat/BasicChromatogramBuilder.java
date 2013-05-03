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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
        private byte[] aConfidence=EMPTY_BYTE_ARRAY;
        private byte[] cConfidence=EMPTY_BYTE_ARRAY;
        private byte[] gConfidence=EMPTY_BYTE_ARRAY;
        private byte[] tConfidence=EMPTY_BYTE_ARRAY;

        private short[] aPositions;
        private short[] cPositions;
        private short[] gPositions;
        private short[] tPositions;

        private Map<String,String> properties;
        
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
            properties(properties);
        }
        
        private void channelGroup(ChannelGroup channelGroup){
        	 aConfidence =toByteArray(channelGroup.getAChannel().getConfidence());
             aPositions =channelGroup.getAChannel().getPositions().toArray();            
             cConfidence= toByteArray(channelGroup.getCChannel().getConfidence());
             cPositions = channelGroup.getCChannel().getPositions().toArray();
             gConfidence = toByteArray(channelGroup.getGChannel().getConfidence());
             gPositions = channelGroup.getGChannel().getPositions().toArray();
             tConfidence =toByteArray(channelGroup.getTChannel().getConfidence());
             tPositions = channelGroup.getTChannel().getPositions().toArray();
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
		       copy.getPositionSequence(),
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

        public byte[] aConfidence() {
            return Arrays.copyOf(aConfidence, aConfidence.length);
        }

        public final BasicChromatogramBuilder aConfidence(byte[] confidence) {
            aConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public byte[] cConfidence() {
            return Arrays.copyOf(cConfidence, cConfidence.length);
        }

        public BasicChromatogramBuilder cConfidence(byte[] confidence) {
            cConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public byte[] gConfidence() {
            return Arrays.copyOf(gConfidence, gConfidence.length);
        }

        public BasicChromatogramBuilder gConfidence(byte[] confidence) {
            gConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public byte[] tConfidence() {
            return Arrays.copyOf(tConfidence, tConfidence.length);
        }

        public BasicChromatogramBuilder tConfidence(byte[] confidence) {
            tConfidence = Arrays.copyOf(confidence, confidence.length);
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

        public Map<String,String> properties() {
            return properties ==null? null :new HashMap<String, String>(properties);
        }

        public BasicChromatogramBuilder properties(Map<String,String> properties) {
            this.properties = new HashMap<String, String>();
            //need to manually add properties because default implementation
            //is to use input as "default" but will return empty map!!!
            for(Entry<String,String> entry : properties.entrySet()){
                this.properties.put(entry.getKey(), entry.getValue());
            }
            return this;
        }

        private QualitySequence generateQualities(ChannelGroup channelGroup) {
        	int length = (int)basecalls.getLength();
            QualitySequenceBuilder builder = new QualitySequenceBuilder(length);
            int i=0;
            for(Nucleotide base : basecalls){
            	QualitySequence qualitySequence = channelGroup.getChannel(base).getConfidence();
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
                    new DefaultChannel(aConfidence(),aPositions()),
                    new DefaultChannel(cConfidence(),cPositions()),
                    new DefaultChannel(gConfidence(),gPositions()),
                    new DefaultChannel(tConfidence(),tPositions()));
            
            return new BasicChromatogram(id,
                    basecalls(),
                    generateQualities(channelGroup),                        
                        peaks(),
                    channelGroup,
                    properties());
        }
}
