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

package org.jcvi.common.core.assembly.util.slice;

import java.util.Iterator;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.util.iter.ArrayIterator;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 * 
 * 
 */
public final class CompactedSliceMap implements SliceMap {
    private static final PhredQuality DEFAULT_QUALITY = PhredQuality.valueOf(30);
	private final CompactedSlice[] slices;

    public static <PR extends AssembledRead> CompactedSliceMap create(Contig<PR> contig,QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(contig, qualityDataStore, qualityValueStrategy);
    }
    public static <PR extends AssembledRead> CompactedSliceMap create(StreamingIterator<PR> readIterator, int consensusLength,QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(readIterator,consensusLength, qualityDataStore, qualityValueStrategy);
    }
   
   
    private <PR extends AssembledRead, C extends Contig<PR>>  CompactedSliceMap(
            C contig, QualitySequenceDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException {
		this(contig.getReadIterator(), (int)contig.getConsensusSequence().getLength(), qualityDataStore,
				qualityValueStrategy);
    }
    private <PR extends AssembledRead, C extends Contig<PR>>  CompactedSliceMap(StreamingIterator<PR> readIter,
			int consensusLength, QualitySequenceDataStore qualityDataStore,
			QualityValueStrategy qualityValueStrategy)
			throws DataStoreException {
		CompactedSlice.Builder builders[] = new CompactedSlice.Builder[consensusLength];
    
    	try{
    		while(readIter.hasNext()){
    			PR read = readIter.next();
    			int start = (int)read.getGappedStartOffset();
    			int i=0;
    			String id =read.getId();
    			Direction dir = read.getDirection();
    			QualitySequence fullQualities =null;
    			if(qualityDataStore!=null){
    				fullQualities = qualityDataStore.get(id);
        			
        			if(fullQualities ==null){
        				throw new NullPointerException("could not get qualities for "+id);
        			}
    			}
    			
    			for(Nucleotide base : read.getNucleotideSequence()){
    				
    				final PhredQuality quality;
    				if(fullQualities==null){
    					quality = DEFAULT_QUALITY;
    				}else{
    					quality= qualityValueStrategy.getQualityFor(read, fullQualities, i);
    				}
    				if(builders[start+i] ==null){
    					builders[start+i] = new CompactedSlice.Builder();
    				}
    				builders[start+i].addSliceElement(id, base, quality, dir);
    				i++;
    			}
    		}
    		//done building
    		this.slices = new CompactedSlice[builders.length];
    		for(int i=0; i<slices.length; i++){
    			if(builders[i]==null){
    				slices[i] = CompactedSlice.EMPTY;
    			}else{
    				slices[i]= builders[i].build();
    			}
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(readIter);
    	}
	}


    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<IdedSlice> iterator() {
        return new ArrayIterator<IdedSlice>(slices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdedSlice getSlice(long offset) {
        return slices[(int) offset];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return slices.length;
    }

}
