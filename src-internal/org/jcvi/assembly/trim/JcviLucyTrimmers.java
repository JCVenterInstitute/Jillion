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

package org.jcvi.assembly.trim;

import org.jcvi.common.core.symbol.qual.trim.LucyLikeQualityTrimmer;


/**
 * {@code JcviLucyTrimmers} contains all the specially
 * configured trimming parameters used by various JCVI applications.
 * Trimming parameters may vary
 * based on sequencer model and source DNA.
 * 
 * @author dkatzel
 *
 *
 */
public final class JcviLucyTrimmers {

    
    private JcviLucyTrimmers(){}
    /**
     * These are the trimming parameters JTrace uses
     * for non-bac sequences that were sequenced on 
     * a 3100 sequencer.
     * the actual parameters to Lucy JTrace uses is:
     * <pre>
     * -error 0.025 0.02 -bracket 10 0.02 -window 50 0.03 10 0.07 -alignment 8 12 16 -size 10 -threshold 20 -minimum 10
     * </pre>
     */
    public static final LucyLikeQualityTrimmer JTRACE_NON_BAC_3100 = new LucyLikeQualityTrimmer.Builder(10)
                                                            .addTrimWindow(50, 0.03F)
                                                            .addTrimWindow(10, 0.07F)
                                                            .build();
    /**
     * These are the trimming parameters JTrace uses
     * for bac sequences that were sequenced on 
     * a 3100 sequencer.
     * the actual parameters to Lucy JTrace uses is:
     * <pre>
     * -error 0.025 0.9 -bracket 10 0.02 -window 50 0.07 10 0.1 -alignment 8 12 16 -size 10 -threshold 20 -minimum 10
     * </pre>
     */
    public static final LucyLikeQualityTrimmer JTRACE_BAC_3100 = new LucyLikeQualityTrimmer.Builder(10)
                                                            .maxErrorAtEnds(.9F)
                                                            .addTrimWindow(50, 0.07F)
                                                            .addTrimWindow(10, 0.1F)
                                                            .build(); 
    
    /**
     * These are the trimming parameters JTrace uses
     * for non-bac sequences that were sequenced on 
     * a 3700 sequencer.
     * the actual parameters to Lucy JTrace uses is:
     * <pre>
     * -error 0.025 0.02 -bracket 10 0.02 -window 50 0.03 10 0.055 -alignment 8 12 16 -size 10 -threshold 20 -minimum 10
     * </pre>
     */
    public static final LucyLikeQualityTrimmer JTRACE_NON_BAC_3700 = new LucyLikeQualityTrimmer.Builder(10)
    .addTrimWindow(50, 0.03F)
    .addTrimWindow(10, 0.055F)
    .build();
    
    /**
     * These are the trimming parameters JTrace uses
     * for bac sequences that were sequenced on 
     * a 3700 sequencer.
     * the actual parameters to Lucy JTrace uses is:
     * <pre>
     * -error 0.025 0.9 -bracket 10 0.02 -window 50 0.07 10 0.1 -alignment 8 12 16 -size 10 -threshold 20 -minimum 10
     * </pre>
     */
    public static final LucyLikeQualityTrimmer JTRACE_BAC_3700 = new LucyLikeQualityTrimmer.Builder(10)
        .maxErrorAtEnds(.9F)
        .addTrimWindow(50, 0.07F)
        .addTrimWindow(10, 0.01F)
        .build(); 
    /**
     * Currently, the arguments JTrace uses to trim 3730 reads is the same
     * as the arguments it uses to trim 3700 reads.
     * @see {@link #JTRACE_NON_BAC_3700}
     */
    public static final LucyLikeQualityTrimmer JTRACE_NON_BAC_3730 = JTRACE_NON_BAC_3700;
    /**
     * Currently, the arguments JTrace uses to trim 3730 reads is the same
     * as the arguments it uses to trim 3700 reads.
     * @see {@link #JTRACE_BAC_3700}
     */
    public static final LucyLikeQualityTrimmer JTRACE_BAC_3730 = JTRACE_BAC_3700;
    /**
     * This is the trimming arguments used by the Elvira project
     * to trim viral sequences before assembly.
     */
    public static final LucyLikeQualityTrimmer ELVIRA = new LucyLikeQualityTrimmer.Builder(30)
                                                    .addTrimWindow(30, 0.1F)
                                                    .addTrimWindow(10, 0.35F)
                                                    .build();
}
