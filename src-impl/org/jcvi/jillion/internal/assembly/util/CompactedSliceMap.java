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
package org.jcvi.jillion.internal.assembly.util;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.SliceMap;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 * 
 * 
 */
public final class CompactedSliceMap implements SliceMap {
    private static final PhredQuality DEFAULT_QUALITY = PhredQuality.valueOf(30);
	private final Slice[] slices;
	

    public static <PR extends AssembledRead> CompactedSliceMap create(Contig<PR> contig,QualitySequenceDataStore qualityDataStore,GapQualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(contig, qualityDataStore, qualityValueStrategy, DEFAULT_QUALITY);
    }
    
    public static <PR extends AssembledRead> CompactedSliceMap create(
    		StreamingIterator<PR> iter, NucleotideSequence consensusSequence,
    		QualitySequenceDataStore qualityDataStore,GapQualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(iter, consensusSequence, qualityDataStore, qualityValueStrategy, DEFAULT_QUALITY);
    }
    public static <PR extends AssembledRead> CompactedSliceMap create(
    		StreamingIterator<PR> iter, NucleotideSequence consensusSequence,
    		PhredQuality defaultQuality,GapQualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(iter, consensusSequence, null, qualityValueStrategy, defaultQuality);
    }
   
    private <PR extends AssembledRead, C extends Contig<PR>>  CompactedSliceMap(
            C contig, QualitySequenceDataStore qualityDataStore,GapQualityValueStrategy qualityValueStrategy, PhredQuality defaultQuality) throws DataStoreException {
		this(contig.getReadIterator(), contig.getConsensusSequence(), qualityDataStore,
				qualityValueStrategy,defaultQuality);
    }
    private <PR extends AssembledRead, C extends Contig<PR>>  CompactedSliceMap(StreamingIterator<PR> readIter,
    		NucleotideSequence consensusSequence, QualitySequenceDataStore qualityDataStore,
			GapQualityValueStrategy qualityValueStrategy, PhredQuality defaultQuality)
			throws DataStoreException {
		SliceBuilder builders[] = initializeSliceBuilders(consensusSequence);
		
    	try{
    		while(readIter.hasNext()){
    			PR read = readIter.next();
    			int start = (int)read.getGappedStartOffset();
    			int i=0;
    			String id =read.getId();
    			Direction dir = read.getDirection();
    			Iterator<PhredQuality> validRangeGappedQualitiesIterator =null;
    			if(qualityDataStore==null){
    				validRangeGappedQualitiesIterator = createNewDefaultQualityIterator(defaultQuality);

    			}else{
    				QualitySequence fullQualities = qualityDataStore.get(id);
        			
        			if(fullQualities ==null){
        				throw new NullPointerException("could not get qualities for "+id);
        			}
        			validRangeGappedQualitiesIterator = qualityValueStrategy.getGappedValidRangeQualitySequenceFor(read, fullQualities)
        													.iterator();
        			
    			}
    			Iterator<Nucleotide> baseIterator = read.getNucleotideSequence().iterator();
    			while(baseIterator.hasNext()){
    				Nucleotide base = baseIterator.next();
    				PhredQuality quality = validRangeGappedQualitiesIterator.next();
    				builders[start+i].add(id, base, quality, dir);
    				i++;
    			}
    		}
    		//done building
    		this.slices = new Slice[builders.length];
    		for(int i=0; i<slices.length; i++){
    			slices[i]= builders[i].build();
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(readIter);
    	}
	}

    private SliceBuilder[] initializeSliceBuilders(NucleotideSequence consensus){
    	SliceBuilder builders[] = new SliceBuilder[(int)consensus.getLength()];
		int i=0;
		Iterator<Nucleotide> iter = consensus.iterator();
		while(iter.hasNext()){
			builders[i++] = new SliceBuilder().setConsensus(iter.next());
		}
    	return builders;
    }

    private Iterator<PhredQuality> createNewDefaultQualityIterator(
			final PhredQuality defaultQuality) {
		return new Iterator<PhredQuality>(){

			@Override
			public boolean hasNext() {
				//always return true
				return true;
			}

			@Override
			public PhredQuality next() {
				return defaultQuality;
			}

			@Override
			public void remove() {
				//no-op				
			}
			
		};
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(slices);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SliceMap)) {
			return false;
		}
		SliceMap other = (SliceMap) obj;
		Iterator<Slice> iter = iterator();
		Iterator<Slice> otherIter = other.iterator();
		while(iter.hasNext()){
			if(!otherIter.hasNext()){
				return false;
			}
			if(!iter.next().equals(otherIter.next())){
				return false;
			}
		}
		if(otherIter.hasNext()){
			return false;
		}
		
		return true;
	}

    
}
