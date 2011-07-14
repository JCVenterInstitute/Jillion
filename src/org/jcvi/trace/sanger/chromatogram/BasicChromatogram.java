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
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;


import java.util.HashMap;
import java.util.Map;
import org.jcvi.CommonUtil;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.EncodedQualitySequence;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualitySequence;
import org.jcvi.sequence.Peaks;



/**
 * <code>Chromatgoram</code> is an implementation
 * of {@link Trace} which is used to reference Sanger
 * Chromatograms.
 * @author dkatzel
 *
 *
 */
public class BasicChromatogram implements Chromatogram {
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;

    private final ChannelGroup channelGroup;
    private final NucleotideSequence basecalls;
    private final Peaks peaks;
    private final QualitySequence qualities;

    /**
     * Used to store the TEXT properties of a ZTR file.
     */
    private Map<String,String> properties;


    public BasicChromatogram(Chromatogram c){
        this(c.getBasecalls(),
                c.getQualities(),
                c.getPeaks(),
               c.getChannelGroup(),
                c.getComments());
    }
    public BasicChromatogram(NucleotideSequence basecalls,QualitySequence qualities, Peaks peaks,
            ChannelGroup channelGroup){
        this(basecalls, qualities, peaks, channelGroup, new HashMap<String,String>());
    }
    public BasicChromatogram(String basecalls, byte[] qualities,Peaks peaks,
            ChannelGroup channelGroup,
            Map<String,String> comments){
        this(new DefaultNucleotideSequence( NucleotideGlyph.getGlyphsFor(basecalls)),
                new EncodedQualitySequence(RUN_LENGTH_CODEC,PhredQuality.valueOf(qualities)),
                peaks,
                     channelGroup, comments);
    }
    public BasicChromatogram(NucleotideSequence basecalls, QualitySequence qualities,Peaks peaks,
           ChannelGroup channelGroup,
           Map<String,String> comments){
        canNotBeNull(basecalls, peaks, channelGroup, comments);
        this.peaks = peaks;        
        this.properties = comments;
        this.channelGroup =channelGroup;
        this.basecalls = basecalls;
        this.qualities = qualities;
    }
   
    private void canNotBeNull(Object...objects ) {
        for(Object obj : objects){
            if(obj == null){
                throw new IllegalArgumentException("null parameter");
            }
        }
        
    }

    public NucleotideSequence getBasecalls() {
        return basecalls;
    }
    @Override
    public Peaks getPeaks() {
        return peaks;
    }

    public Map<String,String> getComments() {
        return properties;
    }

    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }





    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getChannelGroup().hashCode();
        result = prime * result +  basecalls.hashCode();
        result = prime * result +  peaks.hashCode();
        result = prime * result +  properties.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Chromatogram)){
            return false;
        }
        final Chromatogram other = (Chromatogram) obj;

        return CommonUtil.similarTo(getBasecalls(), other.getBasecalls())
        && CommonUtil.similarTo(getPeaks(), other.getPeaks())
        && CommonUtil.similarTo(getChannelGroup(), other.getChannelGroup())
        && CommonUtil.similarTo(getComments(), other.getComments());
    }


    /**
     * @return the channelGroup
     */
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
    @Override
    public QualitySequence getQualities() {
        return qualities;
    }
    @Override
    public int getNumberOfTracePositions() {
        return getChannelGroup().getAChannel().getPositions().array().length;
    }



}
