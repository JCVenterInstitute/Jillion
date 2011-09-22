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

package org.jcvi.common.core.align.blast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.Builder;

/**
 * @author dkatzel
 *
 *
 */
public final class BlastHitMapper{
    private final SortedSet<String> orderedSubjects;
    private final Map<String, List<DirectedRange>> referenceRanges;
    private final Map<String, Map<String, List<DirectedRange>>> reference2ContigMappings ;
    private final Map<String, Map<String, List<DirectedRange>>> contig2ReferenceSubjectMappings ;
    
    public static BlastHitMapperBuilder createBuilder(){
        return new BlastHitMapperBuilder();
    }
    
    private BlastHitMapper(
            SortedSet<String> orderedSubjects,
            Map<String, List<DirectedRange>> referenceRanges,
            Map<String, Map<String, List<DirectedRange>>> reference2ContigMappings,
            Map<String, Map<String, List<DirectedRange>>> contig2ReferenceSubjectMappings) {
        this.orderedSubjects = orderedSubjects;
        this.referenceRanges = referenceRanges;
        this.reference2ContigMappings = reference2ContigMappings;
        this.contig2ReferenceSubjectMappings = contig2ReferenceSubjectMappings;
    }
    public Map<String , List<DirectedRange>> getQueryHitsBySubject(String subjectId){
        return reference2ContigMappings.get(subjectId);
    }
    public Map<String , List<DirectedRange>> getSubjectHitsByQuery(String queryId){
        return contig2ReferenceSubjectMappings.get(queryId);
    }
    public List<DirectedRange> getAllHitsBySubject(String subjectId){
        return  referenceRanges.get(subjectId);
    }
    
    public String getBestSubject(){
        return orderedSubjects.first();
    }
    
    
    public  static final class BlastHitMapperBuilder implements BlastVisitor, Builder<BlastHitMapper>{
        Map<String, List<DirectedRange>> referenceRanges = new HashMap<String, List<DirectedRange>>();
        Map<String, Map<String, List<DirectedRange>>> contig2ReferenceQueryMappings = new HashMap<String, Map<String,List<DirectedRange>>>();
        Map<String, Map<String, List<DirectedRange>>> reference2ContigMappings = new HashMap<String, Map<String,List<DirectedRange>>>();
        Map<String, Map<String, List<DirectedRange>>> contig2ReferenceSubjectMappings = new HashMap<String, Map<String,List<DirectedRange>>>();
        
        SortedMap<String, List<Range>> sortedReferenceMap;
       
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitLine(String line) {
            // TODO Auto-generated method stub
            
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
            // TODO Auto-generated method stub
            
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfFile() {
            //sort references by numberOfBasesHit
            final Map<String, Long> basesHit = new HashMap<String, Long>();
            Map<String,List<Range>> referenceMergedMap = new HashMap<String, List<Range>>();
            for(Entry<String, List<DirectedRange>> entry : referenceRanges.entrySet()){
                List<DirectedRange> directedRanges = entry.getValue();
                List<Range> ranges = new ArrayList<Range>(directedRanges.size());
                for(DirectedRange directedRange : directedRanges){
                    ranges.add(directedRange.getRange());
                }
                List<Range> mergedRanges = Range.mergeRanges(ranges);
                long covered=0;
                for(Range range : mergedRanges){
                    covered+=range.getLength();
                }
                basesHit.put(entry.getKey(), covered);
                referenceMergedMap.put(entry.getKey(), mergedRanges);
            }
            sortedReferenceMap = new TreeMap<String, List<Range>>(new Comparator<String>() {
    
                @Override
                public int compare(String o1, String o2) {
                    return -1* basesHit.get(o1).compareTo(basesHit.get(o2));
                }                
            });
            sortedReferenceMap.putAll(referenceMergedMap);
            
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitHsp(Hsp blastHit) {
            String reference = blastHit.getSubjectId();
            if(!referenceRanges.containsKey(reference)){
                referenceRanges.put(reference, new ArrayList<DirectedRange>());
            }
            referenceRanges.get(reference).add(blastHit.getSubjectRange());
            
           
            String contigId = blastHit.getQueryId();
            if(!contig2ReferenceQueryMappings.containsKey(contigId)){
                contig2ReferenceQueryMappings.put(contigId, new TreeMap<String, List<DirectedRange>>());
            }
            Map<String, List<DirectedRange>> contigMap =contig2ReferenceQueryMappings.get(contigId);
            if(!contigMap.containsKey(reference)){
                contigMap.put(reference, new ArrayList<DirectedRange>());
            }
            contigMap.get(reference).add(blastHit.getQueryRange());
            
            if(!reference2ContigMappings.containsKey(reference)){
                reference2ContigMappings.put(reference, new TreeMap<String, List<DirectedRange>>());
            }
            Map<String, List<DirectedRange>> refMap= reference2ContigMappings.get(reference);
            if(!refMap.containsKey(contigId)){
                refMap.put(contigId, new ArrayList<DirectedRange>());
            }
            refMap.get(contigId).add(blastHit.getQueryRange());
            
            if(!contig2ReferenceSubjectMappings.containsKey(contigId)){
                contig2ReferenceSubjectMappings.put(contigId, new TreeMap<String, List<DirectedRange>>());
            }
            Map<String, List<DirectedRange>> subjectMap= contig2ReferenceSubjectMappings.get(contigId);
            if(!subjectMap.containsKey(reference)){
                subjectMap.put(reference, new ArrayList<DirectedRange>());
            }
            subjectMap.get(reference).add(blastHit.getSubjectRange());
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public BlastHitMapper build() {
            SortedSet<String> sortedRefSet = new TreeSet<String>(sortedReferenceMap.comparator());
            sortedRefSet.addAll(sortedReferenceMap.keySet());
            return new BlastHitMapper(
                    sortedRefSet, 
                    referenceRanges, 
                    reference2ContigMappings, 
                    contig2ReferenceSubjectMappings);
        }
    
    }
    
    
}
