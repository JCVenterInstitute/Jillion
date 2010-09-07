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
 * Created on Jan 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;

public class BasicChromatogramBuilder {
    private static NucleotideGlyphFactory NUCLEOTIDE_FACTORY = NucleotideGlyphFactory.getInstance();
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[]{};
        private short[] peaks;
        private String basecalls;
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

        private Properties properties;
        /**
         * empty constructor.
         */
        public BasicChromatogramBuilder(){}
        /**
         * Builds a builder starting with the following default values.
         * @param basecalls the basecalls may be null.
         * @param peaks the peaks cannot be null.
         * @param channelGroup the channel group containing
         *  position and confidence data on all 4 channels can not be null.
         * @param properties the properties may be null.
         */
        public BasicChromatogramBuilder(String basecalls, short[] peaks, ChannelGroup channelGroup, Properties properties){
            basecalls(basecalls);
            peaks(peaks);
            aConfidence(channelGroup.getAChannel().getConfidence().getData());
            aPositions(channelGroup.getAChannel().getPositions().array());            
            cConfidence(channelGroup.getCChannel().getConfidence().getData());
            cPositions(channelGroup.getCChannel().getPositions().array());
            gConfidence(channelGroup.getGChannel().getConfidence().getData());
            gPositions(channelGroup.getGChannel().getPositions().array());
            tConfidence(channelGroup.getTChannel().getConfidence().getData());
            tPositions(channelGroup.getTChannel().getPositions().array());
            properties(properties);
        }
        public final short[] peaks() {
            return Arrays.copyOf(peaks, peaks.length);
        }

        public final BasicChromatogramBuilder peaks(short[] peaks) {
            this.peaks = Arrays.copyOf(peaks, peaks.length);
            return this;
        }

        public final String basecalls() {
            return basecalls;
        }

        public final BasicChromatogramBuilder basecalls(String basecalls) {
            this.basecalls = basecalls;
            return this;
        }

        public final byte[] aConfidence() {
            return Arrays.copyOf(aConfidence, aConfidence.length);
        }

        public final BasicChromatogramBuilder aConfidence(byte[] confidence) {
            aConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public final byte[] cConfidence() {
            return Arrays.copyOf(cConfidence, cConfidence.length);
        }

        public final BasicChromatogramBuilder cConfidence(byte[] confidence) {
            cConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public final byte[] gConfidence() {
            return Arrays.copyOf(gConfidence, gConfidence.length);
        }

        public final BasicChromatogramBuilder gConfidence(byte[] confidence) {
            gConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public final byte[] tConfidence() {
            return Arrays.copyOf(tConfidence, tConfidence.length);
        }

        public final BasicChromatogramBuilder tConfidence(byte[] confidence) {
            tConfidence = Arrays.copyOf(confidence, confidence.length);
            return this;
        }

        public final short[] aPositions() {
            return Arrays.copyOf(aPositions, aPositions.length);
        }

        public final BasicChromatogramBuilder aPositions(short[] positions) {
            aPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public final short[] cPositions() {
            return Arrays.copyOf(cPositions, cPositions.length);
        }

        public final BasicChromatogramBuilder cPositions(short[] positions) {
            cPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public final short[] gPositions() {
            return Arrays.copyOf(gPositions, gPositions.length);
        }

        public final BasicChromatogramBuilder gPositions(short[] positions) {
            gPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public final short[] tPositions() {
            return Arrays.copyOf(tPositions, tPositions.length);
        }

        public final BasicChromatogramBuilder tPositions(short[] positions) {
            tPositions = Arrays.copyOf(positions, positions.length);
            return this;
        }

        public final Properties properties() {
            return properties ==null? null :(Properties)properties.clone();
        }

        public final BasicChromatogramBuilder properties(Properties properties) {
            this.properties = (Properties)properties.clone();
            return this;
        }

        private EncodedGlyphs<PhredQuality> generateQualities(ChannelGroup channelGroup) {
            List<PhredQuality> qualities = new ArrayList<PhredQuality>(basecalls.length());
            
            
            for(int i=0; i< basecalls.length(); i++){
                NucleotideGlyph base = NucleotideGlyph.getGlyphFor(basecalls.charAt(i));
                final byte[] data = channelGroup.getChannel(base).getConfidence().getData();
                //only read as many qualities as we have...
                if(i == data.length){
                    break;
                }
                qualities.add(PhredQuality.valueOf(data[i]));
            }
            return new DefaultEncodedGlyphs<PhredQuality>(RUN_LENGTH_CODEC,qualities);
        }
        
        public Chromatogram build() {
            final ChannelGroup channelGroup = new DefaultChannelGroup(
                    new Channel(aConfidence(),aPositions()),
                    new Channel(cConfidence(),cPositions()),
                    new Channel(gConfidence(),gPositions()),
                    new Channel(tConfidence(),tPositions()));
            
            return new BasicChromatogram(
                    new DefaultNucleotideEncodedGlyphs(NUCLEOTIDE_FACTORY.getGlyphsFor(basecalls())),
                    generateQualities(channelGroup),                        
                        new Peaks(peaks()),
                                                channelGroup,
                                                properties());
        }
}
