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


import java.util.Properties;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;
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
    private static final NucleotideGlyphFactory FACTORY = NucleotideGlyphFactory.getInstance();
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;

    private final ChannelGroup channelGroup;
    private final NucleotideEncodedGlyphs basecalls;
    private final Peaks peaks;
    private final EncodedGlyphs<PhredQuality> qualities;

    /**
     * Used to store the TEXT properties of a ZTR file.
     */
    private Properties properties;


    public BasicChromatogram(Chromatogram c){
        this(c.getBasecalls(),
                c.getQualities(),
                c.getPeaks(),
               c.getChannelGroup(),
                c.getProperties());
    }
    public BasicChromatogram(NucleotideEncodedGlyphs basecalls,EncodedGlyphs<PhredQuality> qualities, Peaks peaks,
            ChannelGroup channelGroup){
        this(basecalls, qualities, peaks, channelGroup, new Properties());
    }
    public BasicChromatogram(String basecalls, byte[] qualities,Peaks peaks,
            ChannelGroup channelGroup,
             Properties properties){
        this(new DefaultNucleotideEncodedGlyphs( FACTORY.getGlyphsFor(basecalls)),
                new DefaultEncodedGlyphs<PhredQuality>(RUN_LENGTH_CODEC,PhredQuality.valueOf(qualities)),
                peaks,
                     channelGroup, properties);
    }
    public BasicChromatogram(NucleotideEncodedGlyphs basecalls, EncodedGlyphs<PhredQuality> qualities,Peaks peaks,
           ChannelGroup channelGroup,
            Properties properties){
        canNotBeNull(basecalls, peaks, channelGroup, properties);
        this.peaks = peaks;        
        this.properties = properties;
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

    public NucleotideEncodedGlyphs getBasecalls() {
        return basecalls;
    }
    @Override
    public Peaks getPeaks() {
        return peaks;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
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
        if (this == obj)
            return true;
        if (!(obj instanceof BasicChromatogram)){
            return false;
        }
        final BasicChromatogram other = (BasicChromatogram) obj;

        return CommonUtil.similarTo(getBasecalls(), other.getBasecalls())
        && CommonUtil.similarTo(getPeaks(), other.getPeaks())
        && CommonUtil.similarTo(getChannelGroup(), other.getChannelGroup())
        && CommonUtil.similarTo(getProperties(), other.getProperties());
    }


    /**
     * @return the channelGroup
     */
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return qualities;
    }
    @Override
    public int getNumberOfTracePositions() {
        return getChannelGroup().getAChannel().getPositions().array().length;
    }



}
