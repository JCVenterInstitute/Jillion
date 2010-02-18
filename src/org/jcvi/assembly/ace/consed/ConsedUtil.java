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
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.consed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.DefaultAceContig;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class ConsedUtil {
    public static String convertAceGapsToContigGaps(String basecallsWithAceGaps) {
        return basecallsWithAceGaps.replace('*', '-');
    }
    
    public static String convertContigGapstoAceGaps(String basecallsWithAceGaps) {
        return basecallsWithAceGaps.replace('-', '*');
    }
    public static List<AceContig> split0xContig(AceContig contig, CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap){
        List<Range> coveredRegions = new ArrayList<Range>();
        for(CoverageRegion region : coverageMap){
            if(region.getCoverage()>0){
                final Range contigRange = Range.buildRange(region.getStart(), region.getEnd())
                                            .convertRange(CoordinateSystem.RESIDUE_BASED);
                coveredRegions.add(contigRange);
            }
        }
        
        List<Range> contigRanges =Range.mergeRanges(coveredRegions);
        if(contigRanges.size()==1){
            //no 0x region
            return Arrays.asList(contig);
        }
        List<AceContig> newContigs = new ArrayList<AceContig>(contigRanges.size());
        String originalContigId= contig.getId();
        NucleotideEncodedGlyphs consensus = contig.getConsensus();
        for(Range contigRange : contigRanges){
            Set<String> contigReads = new HashSet<String>();
            
            for(CoverageRegion<AcePlacedRead> region : coverageMap.getRegionsWithin(contigRange)){
                for(AcePlacedRead read : region.getElements()){
                    contigReads.add(read.getId());
                }
            }
            String contigConsensus =NucleotideGlyph.convertToString(consensus.decode(contigRange));
            String contigId = String.format("%s_%d_%d",originalContigId, contigRange.getStart(), contigRange.getEnd());
            DefaultAceContig.Builder builder = new DefaultAceContig.Builder(contigId, contigConsensus);
            
            for(String readId : contigReads){
                final VirtualPlacedRead<AcePlacedRead> placedReadById = contig.getPlacedReadById(readId);
                if(placedReadById ==null){
                    throw new NullPointerException("got a null read for id " + readId);
                }
                AcePlacedRead read = placedReadById.getRealPlacedRead();
                builder.addRead(readId, 
                        NucleotideGlyph.convertToString(read.getEncodedGlyphs().decode()), 
                        (int)(read.getStart() - contigRange.getStart()), 
                        read.getSequenceDirection(), read.getValidRange(), read.getPhdInfo());
            }
            newContigs.add(builder.build());
        }
        return newContigs;
    }
}
