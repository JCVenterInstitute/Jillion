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

package org.jcvi.trace.nextera;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.trim.DefaultPrimerTrimmer;
import org.jcvi.assembly.trim.PrimerTrimmer;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;

/**
 * @author dkatzel
 *
 *
 */
public final class NexteraTransposonTrimmer implements PrimerTrimmer{

    private static final NucleotideDataStore FORWARD_TRANSPOSON;
    
    private static final NucleotideDataStore REVERSE_TRANSPOSON ;
    private final PrimerTrimmer nexteraTransposonTrimmer;
    
    
    static{
        Map<String, NucleotideEncodedGlyphs> forwardTransposon = new HashMap<String, NucleotideEncodedGlyphs>();
        forwardTransposon.put("5'", TransposonEndSequences.FORWARD);
        Map<String, NucleotideEncodedGlyphs> revesrseTransposon = new HashMap<String, NucleotideEncodedGlyphs>();
        
        revesrseTransposon.put("3'", TransposonEndSequences.REVERSE);
        
       FORWARD_TRANSPOSON = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideEncodedGlyphs>(forwardTransposon));
        
       REVERSE_TRANSPOSON = new NucleotideDataStoreAdapter(new SimpleDataStore<NucleotideEncodedGlyphs>(revesrseTransposon));
        
    }

    public NexteraTransposonTrimmer(){
        this(13, .9f);
    }
    
    public NexteraTransposonTrimmer(int minLength, double minMatch){
        nexteraTransposonTrimmer = new DefaultPrimerTrimmer(minLength, minMatch,false);
    }
    
    public Range trim(NucleotideEncodedGlyphs sequence){
        Range forwardClearRange =nexteraTransposonTrimmer.trim(sequence, FORWARD_TRANSPOSON);
        
        Range reverseClearRange =nexteraTransposonTrimmer.trim(sequence, REVERSE_TRANSPOSON);
        
        return computeClearRange(forwardClearRange, reverseClearRange);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(NucleotideEncodedGlyphs sequence,
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
        return trim(new DefaultNucleotideEncodedGlyphs(sequence));
    }
    
    
}
