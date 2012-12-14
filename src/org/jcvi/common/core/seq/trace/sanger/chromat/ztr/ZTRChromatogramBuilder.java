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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.ztr;


import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.BasicChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.impl.ZTRChromatogramImpl;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * <code>ZTRChromatogramBuilder</code> uses the Builder Pattern
 * to build a {@link ZTRChromatogram} instance.
 * @author dkatzel
 *
 *
 */
public final class ZTRChromatogramBuilder implements Builder<ZTRChromatogram>{
    
    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;

    private final BasicChromatogramBuilder basicBuilder;
    
    
    public ZTRChromatogramBuilder(String id){
        basicBuilder = new BasicChromatogramBuilder(id);
    }
    
    public ZTRChromatogramBuilder(Chromatogram copy){
       basicBuilder = new BasicChromatogramBuilder(copy);        
    }
    public ZTRChromatogramBuilder(ZTRChromatogram copy){
        this((Chromatogram)copy);
        clip(copy.getClip());
     }
   /**
    * Gets the ZTR's clip points..
    * @return a Clip, may be null.
    */
    public final Range clip() {
        return clip;
    }
    /**
     * Sets the clip.
     * @param clip the clip to set.
     * @return this.
     */
    public final ZTRChromatogramBuilder clip(Range clip) {
        this.clip = clip;
        return this;
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public ZTRChromatogram build() {
        return new ZTRChromatogramImpl(basicBuilder.build(),
                clip());
    }
    
    public final PositionSequence peaks() {
        return basicBuilder.peaks();
    }

    public ZTRChromatogramBuilder peaks(short[] peaks) {
        basicBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence basecalls() {
        return basicBuilder.basecalls();
    }

    public ZTRChromatogramBuilder basecalls(NucleotideSequence basecalls) {
        basicBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] aConfidence() {
        return basicBuilder.aConfidence();
    }

    public final ZTRChromatogramBuilder aConfidence(byte[] confidence) {
        basicBuilder.aConfidence(confidence);
        return this;
    }

    public final byte[] cConfidence() {
        return basicBuilder.cConfidence();
    }

    public final ZTRChromatogramBuilder cConfidence(byte[] confidence) {
        basicBuilder.cConfidence(confidence);
        return this;
    }

    public final byte[] gConfidence() {
        return basicBuilder.gConfidence();
    }

    public final ZTRChromatogramBuilder gConfidence(byte[] confidence) {
        basicBuilder.gConfidence(confidence);
        return this;
    }

    public final byte[] tConfidence() {
        return basicBuilder.tConfidence();
    }

    public final ZTRChromatogramBuilder tConfidence(byte[] confidence) {
        basicBuilder.tConfidence(confidence);
        return this;
    }

    public final short[] aPositions() {
        return basicBuilder.aPositions();
    }

    public final ZTRChromatogramBuilder aPositions(short[] positions) {
        basicBuilder.aPositions(positions);
        return this;
    }

    public final short[] cPositions() {
        return basicBuilder.cPositions();
    }

    public final ZTRChromatogramBuilder cPositions(short[] positions) {
        basicBuilder.cPositions(positions);
        return this;
    }

    public final short[] gPositions() {
        return basicBuilder.gPositions();
    }

    public final ZTRChromatogramBuilder gPositions(short[] positions) {
        basicBuilder.gPositions(positions);
        return this;
    }

    public final short[] tPositions() {
        return basicBuilder.tPositions();
    }

    public final ZTRChromatogramBuilder tPositions(short[] positions) {
        basicBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> properties() {
        return basicBuilder.properties();
    }

    public final ZTRChromatogramBuilder properties(Map<String,String> properties) {
        basicBuilder.properties(properties);
        return this;
    }
}
