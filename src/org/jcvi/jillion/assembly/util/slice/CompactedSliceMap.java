/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.slice;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

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
    public Iterator<Slice> iterator() {
        return Arrays.<Slice>asList(slices).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Slice getSlice(long offset) {
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
