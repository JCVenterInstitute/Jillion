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
 * Created on Nov 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.Range;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
/**
 * {@code OntheFlyAceBestSegmentMap} is an implementation of {@link AceBestSegmentMap}
 * that does not precompute AceBestSegments which saves memory but at the price of 
 * extra computation.  This can be useful when storing
 * AceBestSegments takes up a lot of memory and random access to the map is not
 * required. 
 * @author dkatzel
 *
 *
 */
public class OnTheFlyAceBestSegmentMap implements AceBestSegmentMap{
    private final SliceMap sliceMap;
    private final NucleotideEncodedGlyphs consensus;
    
    public OnTheFlyAceBestSegmentMap(SliceMap sliceMap, NucleotideEncodedGlyphs consensus){
        if(sliceMap==null || consensus ==null){
            throw new NullPointerException();
        }
        
        this.sliceMap=sliceMap;
        this.consensus=consensus;
    }
    @Override
    public AceBestSegment getBestSegmentFor(long gappedConsensusOffset) {
        Range targetRange = Range.buildRange(gappedConsensusOffset,gappedConsensusOffset);
        List<AceBestSegment> segments = getBestSegmentsFor(targetRange);
        if(segments.isEmpty()){
            return null;
        }
        return segments.get(0);
    }

    @Override
    public List<AceBestSegment> getBestSegmentsFor(Range gappedConsensusRange) {
        List<AceBestSegment> result = new ArrayList<AceBestSegment>();
        Iterator<AceBestSegment> iter =iterator();
        while(iter.hasNext()){
            AceBestSegment segment =iter.next();
            if(segment.getGappedConsensusRange().intersects(gappedConsensusRange)){
                result.add(segment);
            }
        }
        return result;
    }

    @Override
    public int getNumberOfBestSegments() {
        int counter=0;
        Iterator<AceBestSegment> iter =iterator();
        while(iter.hasNext()){
            iter.next();
            counter++;
        }
        return counter;
    }

    @Override
    public Iterator<AceBestSegment> iterator() {
        return new BestSegmentIterator(consensus.getLength());
    }

    private class  BestSegmentIterator implements Iterator<AceBestSegment>{
        private final Object endOfIterating=new Object();
        private int currentStart=0;
        private Object nextSegment;
        private final long maxStart;
        BestSegmentIterator(long maxStart){
            this.maxStart = maxStart;
            computeNextSegment();
        }
        @Override
        public boolean hasNext() {
            return nextSegment !=endOfIterating;
        }

        @Override
        public AceBestSegment next() {
            if(hasNext()){
                AceBestSegment result = (AceBestSegment)nextSegment;
                computeNextSegment();
                return result;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
            
        }
        
        private void computeNextSegment(){
            if(currentStart>=maxStart){
                nextSegment= endOfIterating;
                return;
            }
            String currentElement =null;
            int start=currentStart;
            int i=start;
            int end=i;
            Range previouslyEnteredRange=null;
            while(i<maxStart){
                Slice slice = sliceMap.getSlice(i);
                NucleotideGlyph consensusCall = consensus.get(i);
                
                //slice is null there is no real best segment...
                
                if(slice ==null){
                    throw new NullPointerException(
                            "slice "+i + " can not be null for best segment (size = "+sliceMap.getSize()+")");
                }
                //short circuit to try our currentElement first
                //to see if we can extend the current best segment
                if(slice.containsElement(currentElement)){
                    if(slice.getSliceElement(currentElement).getBase().equals(consensusCall)){
                        end=i;
                        i++;
                        continue;
                    }
                }
                if(slice.getCoverageDepth()>0){
                    
                    for(SliceElement element : slice){
                        if(element.getBase().equals(consensusCall)){
                            //we can only get here if element is 
                            //different than currentElement so 
                            //we don't have to check if we match currentelement
                            if(currentElement ==null){
                                //first time through...
                                currentElement = element.getId();
                                break;
                            }
                                //entering new best segment so we are done...
                                
                                previouslyEnteredRange = Range.buildRange(start,end);
                                nextSegment =new DefaultAceBestSegment(currentElement, previouslyEnteredRange);
                                currentStart=end+1;
                                return;
                            
                        }
                    }
                    
                }else{
                    currentElement = slice.getSliceElements().get(0).getId();
                }
                i++;
            }     
                nextSegment =new DefaultAceBestSegment(currentElement, Range.buildRange(start,end));
                currentStart=end+1;
                return;
           
        }
    }
}
