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

package org.jcvi.common.core.assembly.tasm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AbstractContigBuilder;
import org.jcvi.common.core.assembly.AssembledReadBuilder;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code DefaultTasmContig} is a {@link Contig}
 * implementation for TIGR Assembler contig data.
 * @author dkatzel
 *
 *
 */
public final class DefaultTasmContig implements TasmContig{
    private final Map<TasmContigAttribute,String> attributes;
    private final Contig<TasmAssembledRead> contig;
    /**
     * @param id
     * @param consensus
     * @param placedReads
     * @param circular
     */
    private DefaultTasmContig(String id, NucleotideSequence consensus, Set<TasmAssembledRead> reads, 
            EnumMap<TasmContigAttribute, String> attributes) {
        contig = new DefaultContig<TasmAssembledRead>(id,consensus,reads);
        this.attributes = Collections.unmodifiableMap(new EnumMap<TasmContigAttribute, String>(attributes));
    }

    @Override
	public String getAttributeValue(TasmContigAttribute attribute) {
		if(!hasAttribute(attribute)){
			throw new NoSuchElementException("contig does not have an attribute "+attribute);
		}
		return attributes.get(attribute);
	}


	@Override
	public boolean hasAttribute(TasmContigAttribute attribute) {
		return attributes.containsKey(attribute);
	}
    @Override
    public Map<TasmContigAttribute, String> getAttributes() {
        return attributes;
    }



	@Override
	public String getId() {
		return contig.getId();
	}



	@Override
	public long getNumberOfReads() {
		return contig.getNumberOfReads();
	}



	@Override
	public NucleotideSequence getConsensusSequence() {
		return contig.getConsensusSequence();
	}



	@Override
	public TasmAssembledRead getRead(String id) {
		return contig.getRead(id);
	}



	@Override
	public boolean containsRead(String readId) {
		return contig.containsRead(readId);
	}



	@Override
	public StreamingIterator<TasmAssembledRead> getReadIterator() {
		return contig.getReadIterator();
	}


    


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = contig.hashCode();
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}		
		if (!(obj instanceof TasmContig)) {
			return false;
		}
		TasmContig other = (TasmContig) obj;
		if (!contig.getId().equals(other.getId())) {
			return false;
		}
		if (!contig.getConsensusSequence().equals(other.getConsensusSequence())) {
			return false;
		}
		if (contig.getNumberOfReads()!=other.getNumberOfReads()) {
			return false;
		}
		StreamingIterator<TasmAssembledRead> readIter=null;
		try{
			readIter = contig.getReadIterator();
			while(readIter.hasNext()){
				TasmAssembledRead read = readIter.next();
				String readId = read.getId();
				if(!other.containsRead(readId)){
					return false;
				}
				if(!read.equals(other.getRead(readId))){
					return false;
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(readIter);
		}			

		if (attributes == null) {
			if (other.getAttributes() != null) {
				return false;
			}
		} else if (!attributes.equals(other.getAttributes())) {
			return false;
		}
		return true;
	}





	public static class Builder extends AbstractContigBuilder<TasmAssembledRead, TasmContig>{
        private final EnumMap<TasmContigAttribute,String> contigAttributes = new EnumMap<TasmContigAttribute,String>(TasmContigAttribute.class);
        private final Map<String, EnumMap<TasmReadAttribute,String>> readAttributeMaps = new LinkedHashMap<String, EnumMap<TasmReadAttribute,String>>();

        /**
         * @param id
         * @param consensus
         */
        public Builder(String id, NucleotideSequence consensus) {
        	super(id,consensus);
        }
        public Builder(String id, NucleotideSequence consensus,Map<TasmContigAttribute,String> attributes) {
            super(id, consensus);
            this.contigAttributes.putAll(attributes);
        }
        public <R extends TasmAssembledRead, C extends Contig<R>> Builder(C copy){
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
       
       
        @Override
        public Builder addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){            
            if(offset <0){
                throw new IllegalArgumentException("circular reads not supported");
                
              }
            super.addRead(id, offset, validRange, basecalls, dir,fullUngappedLength);
            return this;            
        }
        
       
       
       
        public Builder addAttribute(TasmContigAttribute attribute, String value){
            this.contigAttributes.put(attribute, value);
            return this;
        }
        public Builder removeAttribute(TasmContigAttribute attribute){
            this.contigAttributes.remove(attribute);
            return this;
        }
        @Override
        public DefaultTasmContig build() {
            Set<TasmAssembledRead> reads = new LinkedHashSet<TasmAssembledRead>();
            for(AssembledReadBuilder<TasmAssembledRead> builder : getAllAssembledReadBuilders()){
               ((TasmAssembledReadBuilder)builder).addAllAttributes(readAttributeMaps.get(builder.getId()));
                reads.add(builder.build());
            }
            return new DefaultTasmContig(getContigId(),getConsensusBuilder().build(),
                    reads,contigAttributes);
        }
    
        public Builder addReadAttributes(String id, EnumMap<TasmReadAttribute, String> readAttributes) {
            readAttributeMaps.put(id, readAttributes);
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected TasmAssembledReadBuilder createPlacedReadBuilder(
                TasmAssembledRead read) {
            TasmAssembledReadBuilder builder =DefaultTasmAssembledRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    read.getNucleotideSequence().toString(), 
                    (int)read.getGappedStartOffset(), 
                    read.getDirection(), 
                    read.getReadInfo().getValidRange(),
                    read.getReadInfo().getUngappedFullLength());
            for(Entry<TasmReadAttribute,String> entry : read.getAttributes().entrySet()){
                builder.addAttribute(entry.getKey(), entry.getValue());
            }
            return builder;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected TasmAssembledReadBuilder createPlacedReadBuilder(
                String id, int offset, Range validRange, String basecalls,
                Direction dir, int fullUngappedLength) {
            return DefaultTasmAssembledRead.createBuilder(
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
