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
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.assembly;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public final class DefaultContig<T extends AssembledRead> implements Contig<T>{

	private final NucleotideSequence consensus;
    private final String id;
    private final Map<String, T> mapById;
    
    public DefaultContig(String id, NucleotideSequence consensus, Set<T> assembledReads){
    	if(id==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(consensus==null){
    		throw new NullPointerException("consensus can not be null");
    	}
        this.id = id;
        this.consensus = consensus;
        int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(assembledReads.size());
        mapById = new LinkedHashMap<String, T>(capacity);
        for(T r : assembledReads){
            mapById.put(r.getId(), r);
        }
       
    }
    public DefaultContig(String id, NucleotideSequence consensus, Map<String, T> assembledReads){
    	if(id==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(consensus==null){
    		throw new NullPointerException("consensus can not be null");
    	}
    	if(assembledReads ==null){
    		throw new NullPointerException("assembled read map can not be null");
    	}
        this.id = id;
        this.consensus = consensus;
        mapById = assembledReads;
       
    }

    @Override
    public NucleotideSequence getConsensusSequence() {
        return consensus;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getNumberOfReads() {
        return mapById.size();
    }
    @Override
    public T getRead(String id) {
        return mapById.get(id);
    }
    @Override
    public StreamingIterator<T> getReadIterator() {       
        return IteratorUtil.createStreamingIterator(mapById.values().iterator());
    }

    


    @Override
    public boolean containsRead(String placedReadId) {
        return mapById.containsKey(placedReadId);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ consensus.hashCode();
		result = prime * result +  id.hashCode();
		result = prime * result +  mapById.hashCode();
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
		if (!(obj instanceof Contig)) {
			return false;
		}
		Contig<?> other = (Contig<?>) obj;
		
		if (!id.equals(other.getId())) {
			return false;
		}
		if (!consensus.equals(other.getConsensusSequence())) {
			return false;
		}
		if (getNumberOfReads()!=other.getNumberOfReads()) {
			return false;
		}
		for(Entry<String, T> entry : mapById.entrySet()){
			String readId = entry.getKey();
			if(!other.containsRead(readId)){
				return false;
			}
			if(!entry.getValue().equals(other.getRead(readId))){
				return false;
			}
		}
		
		return true;
	}

	public static final class Builder extends AbstractContigBuilder<AssembledRead, Contig<AssembledRead>>{
        public Builder(String id, String consensus){
           this(id, new NucleotideSequenceBuilder(consensus)
           					.build());
        }
        
        public <R extends AssembledRead, C extends Contig<R>> Builder(C copy){
            this(copy.getId(), copy.getConsensusSequence());
            StreamingIterator<R> iter =null;
            try{
            	 iter = copy.getReadIterator();
            	 while(iter.hasNext()){
            		 R read = iter.next();
            		 addRead(read);
            	 }
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter);
            }
         }
        public Builder(String id, NucleotideSequence consensus){
            super(id,consensus);
        }
        public Builder addRead(String id, int offset,String basecalls){
            return addRead(id, offset, basecalls, Direction.FORWARD);
        }
        public Builder addRead(String id, int offset,String basecalls, Direction dir){
            int numberOfGaps = computeNumberOfGapsIn(basecalls);
            int ungappedLength = basecalls.length()-numberOfGaps;
            return addRead(id, offset, 
            		Range.ofLength(ungappedLength),basecalls, 
            		dir,ungappedLength);
        }
        /**
         * @param basecalls
         * @return
         */
        private int computeNumberOfGapsIn(String basecalls) {
            int count=0;
            for(int i=0; i<basecalls.length(); i++){
                if(basecalls.charAt(i) == '-'){
                    count++;
                }
            }
            return count;
        }
        @Override
        public Builder addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){            
            if(offset <0){
                throw new IllegalArgumentException("circular reads not supported");
                
              }
            super.addRead(id, offset, validRange, basecalls, dir,fullUngappedLength);
            return this;            
        }
        
       
        public DefaultContig<AssembledRead> build(){
        	 if(consensusCaller !=null){
      			recallConsensusNow();
              }
            Set<AssembledRead> reads = new LinkedHashSet<AssembledRead>();
            for(AssembledReadBuilder<AssembledRead> builder : getAllAssembledReadBuilders()){
                reads.add(builder.build());
            }
            return new DefaultContig<AssembledRead>(getContigId(), getConsensusBuilder().build(), reads);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected AssembledReadBuilder<AssembledRead> createPlacedReadBuilder(
                AssembledRead read) {
            return DefaultAssembledRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    read.getNucleotideSequence().toString(), 
                    (int)read.getGappedStartOffset(), 
                    read.getDirection(), 
                    read.getReadInfo().getValidRange(),
                    read.getReadInfo().getUngappedFullLength());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected AssembledReadBuilder<AssembledRead> createPlacedReadBuilder(
                String id, int offset, Range validRange, String basecalls,
                Direction dir, int fullUngappedLength) {
            return DefaultAssembledRead.createBuilder(
                    getConsensusBuilder().build(), 
                    id, 
                    basecalls, 
                    offset, 
                    dir, 
                    validRange,
                    fullUngappedLength);
        }
    }

}
