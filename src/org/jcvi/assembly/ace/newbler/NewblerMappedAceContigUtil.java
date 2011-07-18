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
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.newbler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.DefaultAceContig;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.read.trace.TraceDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;

public class NewblerMappedAceContigUtil {

    private static final Pattern ACTUAL_CONSENSUS_ID_PATTERN = Pattern.compile("contig\\d+");
    /**
     * Removes the reference from a Newbler mapped
     * ace file and sets the consensus to the actual consensus of the underlying reads.
     * Newbler mapped ace files are different from traditional ace files, in that
     * sequences are shown in context to their reference sequence.
     * The actual reference is the "contig" consensus in the ace and the actual
     * contig consensus is a fake read with the special name "contig0000x".
     * there is also a read whose id is the reference and matches the "consensus"
     * 1:1.
     * @param newblerMappedContig a newbler mapped ace contig
     * @param phdDataStore a {@link DataStore} of {@link Phd} containing
     * only the "real" reads, not reference and no contig0000x.
     * @return a List of new AceContigs that are the real contigs
     * containing only the real reads using contig0000x reads as the consensus.
     * 
     * @throws DataStoreException
     * @see Page 211 of the 454 Data Analysis Software Manual for more details.
     */
    public static List<AceContig> removeReferenceFrom(AceContig newblerMappedContig,
            TraceDataStore<Phd> phdDataStore) throws DataStoreException{
        Map<String,Range> ranges = new HashMap<String, Range>();
        String idPrefix=null;
        for(AcePlacedRead read : newblerMappedContig.getPlacedReads()){
            String id = read.getId();
            Matcher matcher = ACTUAL_CONSENSUS_ID_PATTERN.matcher(id);
            if(matcher.matches()){
                ranges.put(id,Range.buildRange(read.getStart(),read.getEnd()));
                
            }
            else if(!phdDataStore.contains(id)){
                idPrefix = id;
            }
        }
        List<AceContig> result = new ArrayList<AceContig>(ranges.size());
        boolean in1Contig = ranges.size()==1;
        for(Entry<String, Range> entry : ranges.entrySet()){
            String consensusId = entry.getKey();
            String contigId = in1Contig? idPrefix: String.format("%s_%s", idPrefix,consensusId);
            result.add(buildAceContigFor(newblerMappedContig, phdDataStore,contigId, consensusId, entry.getValue()));
        }
        return result;
    }

    private static AceContig buildAceContigFor(AceContig originalAceContig, TraceDataStore<Phd> phdDataStore,String id,String consensusId,Range contigRange) throws DataStoreException{
        
        final AcePlacedRead consensusRead = originalAceContig.getPlacedReadById(consensusId);
        final String consensus = NucleotideGlyph.convertToString(consensusRead.getEncodedGlyphs().decode());
        DefaultAceContig.Builder builder = new DefaultAceContig.Builder(id,consensus);
        for(AcePlacedRead read : originalAceContig.getPlacedReads()){
            
            Matcher matcher = ACTUAL_CONSENSUS_ID_PATTERN.matcher(id);
            Range readRange = Range.buildRange(read.getStart(), read.getEnd());
            if(!matcher.matches() && phdDataStore.contains(read.getId()) && readRange.isSubRangeOf(contigRange) ){
              //  System.out.println(read.getId());
                final int newOffset = (int)(read.getStart() -consensusRead.getStart());
                builder.addRead(read.getId(), 
                        NucleotideGlyph.convertToString(read.getEncodedGlyphs().decode()),
                        newOffset,
                        read.getSequenceDirection(),
                        read.getValidRange(), read.getPhdInfo(),
                        read.getUngappedFullLength());
            }
        }
        return builder.build();
        
    }

   
}
