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
 * <code>ZtrChromatogramBuilder</code> uses the Builder Pattern
 * to build a {@link ZtrChromatogram} instance.
 * @author dkatzel
 *
 *
 */
public final class ZtrChromatogramBuilder implements Builder<ZtrChromatogram>{
    
    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;

    private final BasicChromatogramBuilder basicBuilder;
    
    
    public ZtrChromatogramBuilder(String id){
        basicBuilder = new BasicChromatogramBuilder(id);
    }
    
    public ZtrChromatogramBuilder(Chromatogram copy){
       basicBuilder = new BasicChromatogramBuilder(copy);        
    }
    public ZtrChromatogramBuilder(ZtrChromatogram copy){
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
    public final ZtrChromatogramBuilder clip(Range clip) {
        this.clip = clip;
        return this;
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public ZtrChromatogram build() {
        return new ZTRChromatogramImpl(basicBuilder.build(),
                clip());
    }
    
    public final PositionSequence peaks() {
        return basicBuilder.peaks();
    }

    public ZtrChromatogramBuilder peaks(short[] peaks) {
        basicBuilder.peaks(new PositionSequenceBuilder(peaks).build());
        return this;
    }

    public final NucleotideSequence basecalls() {
        return basicBuilder.basecalls();
    }

    public ZtrChromatogramBuilder basecalls(NucleotideSequence basecalls) {
        basicBuilder.basecalls(basecalls);
        return this;
    }

    public final byte[] aConfidence() {
        return basicBuilder.aConfidence();
    }

    public final ZtrChromatogramBuilder aConfidence(byte[] confidence) {
        basicBuilder.aConfidence(confidence);
        return this;
    }

    public final byte[] cConfidence() {
        return basicBuilder.cConfidence();
    }

    public final ZtrChromatogramBuilder cConfidence(byte[] confidence) {
        basicBuilder.cConfidence(confidence);
        return this;
    }

    public final byte[] gConfidence() {
        return basicBuilder.gConfidence();
    }

    public final ZtrChromatogramBuilder gConfidence(byte[] confidence) {
        basicBuilder.gConfidence(confidence);
        return this;
    }

    public final byte[] tConfidence() {
        return basicBuilder.tConfidence();
    }

    public final ZtrChromatogramBuilder tConfidence(byte[] confidence) {
        basicBuilder.tConfidence(confidence);
        return this;
    }

    public final short[] aPositions() {
        return basicBuilder.aPositions();
    }

    public final ZtrChromatogramBuilder aPositions(short[] positions) {
        basicBuilder.aPositions(positions);
        return this;
    }

    public final short[] cPositions() {
        return basicBuilder.cPositions();
    }

    public final ZtrChromatogramBuilder cPositions(short[] positions) {
        basicBuilder.cPositions(positions);
        return this;
    }

    public final short[] gPositions() {
        return basicBuilder.gPositions();
    }

    public final ZtrChromatogramBuilder gPositions(short[] positions) {
        basicBuilder.gPositions(positions);
        return this;
    }

    public final short[] tPositions() {
        return basicBuilder.tPositions();
    }

    public final ZtrChromatogramBuilder tPositions(short[] positions) {
        basicBuilder.tPositions(positions);
        return this;
    }

    public final Map<String,String> properties() {
        return basicBuilder.properties();
    }

    public final ZtrChromatogramBuilder properties(Map<String,String> properties) {
        basicBuilder.properties(properties);
        return this;
    }
}
