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

package org.jcvi.common.core.seq.read.trace.nextera;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.seq.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.seq.nuc.NucleotideDataStore;
import org.jcvi.common.core.seq.nuc.NucleotideDataStoreAdapter;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.trim.DefaultPrimerTrimmer;
import org.jcvi.common.core.seq.trim.PrimerTrimmer;

/**
 * @author dkatzel
 *
 *
 */
public final class NexteraTransposonTrimmer implements PrimerTrimmer{

    private static final NucleotideDataStore FORWARD_TRANSPOSON;
    
    private static final NucleotideDataStore REVERSE_TRANSPOSON ;
    private final PrimerTrimmer nexteraTransposonTrimmer;
    
    public static final int DEFAULT_MIN_LENGTH=13;
    public static final double DEFAULT_MIN_MATCH = .9D;
    static{
        Map<String, NucleotideSequence> forwardTransposon = new HashMap<String, NucleotideSequence>();
        forwardTransposon.put("5'", TransposonEndSequences.FORWARD);
        Map<String, NucleotideSequence> revesrseTransposon = new HashMap<String, NucleotideSequence>();
        
        revesrseTransposon.put("3'", TransposonEndSequences.REVERSE);
        
       FORWARD_TRANSPOSON = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideSequence>(forwardTransposon));
        
       REVERSE_TRANSPOSON = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideSequence>(revesrseTransposon));
        
    }

    public NexteraTransposonTrimmer(){
        this(DEFAULT_MIN_LENGTH, DEFAULT_MIN_MATCH);
    }
    
    public NexteraTransposonTrimmer(int minLength, double minMatch){
        nexteraTransposonTrimmer = new DefaultPrimerTrimmer(minLength, minMatch,false);
    }
    
    public Range trim(NucleotideSequence sequence){
        Range forwardClearRange =nexteraTransposonTrimmer.trim(sequence, FORWARD_TRANSPOSON);
        
        Range reverseClearRange =nexteraTransposonTrimmer.trim(sequence, REVERSE_TRANSPOSON);
        
        return computeClearRange(forwardClearRange, reverseClearRange);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(NucleotideSequence sequence,
            NucleotideDataStore primersToTrimAgainst) {
       return trim(sequence);
        
    }

    private Range computeClearRange(Range forwardClearRange,
            Range reverseClearRange) {
        if(reverseClearRange.isSubRangeOf(forwardClearRange)){
            return Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                    forwardClearRange.getLocalStart(), reverseClearRange.getLocalEnd());
        }
        return forwardClearRange.intersection(reverseClearRange)
                            .convertRange(CoordinateSystem.RESIDUE_BASED);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(String sequence, NucleotideDataStore primersToTrimAgainst) {
        return trim(new DefaultNucleotideSequence(sequence));
    }
    
    
}
