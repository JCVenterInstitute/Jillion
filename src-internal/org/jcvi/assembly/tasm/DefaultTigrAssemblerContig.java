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

package org.jcvi.assembly.tasm;

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
import org.jcvi.common.core.assembly.contig.AbstractContigBuilder;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.DefaultContig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.PlacedReadBuilder;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;

/**
 * {@code DefaultTigrAssemblerContig} is a {@link Contig}
 * implementation for TIGR Assembler contig data.
 * @author dkatzel
 *
 *
 */
public class DefaultTigrAssemblerContig extends DefaultContig<TigrAssemblerPlacedRead> implements TigrAssemblerContig{
    private final Map<TigrAssemblerContigAttribute,String> attributes;
    /**
     * @param id
     * @param consensus
     * @param placedReads
     * @param circular
     */
    protected DefaultTigrAssemblerContig(String id,
            NucleotideSequence consensus,
            Set<TigrAssemblerPlacedRead> placedReads, 
            EnumMap<TigrAssemblerContigAttribute, String> attributes) {
        super(id, consensus, placedReads);
        this.attributes = Collections.unmodifiableMap(new EnumMap(attributes));
    }

    @Override
	public String getAttributeValue(TigrAssemblerContigAttribute attribute) {
		if(!hasAttribute(attribute)){
			throw new NoSuchElementException("contig does not have an attribute "+attribute);
		}
		return attributes.get(attribute);
	}


	@Override
	public boolean hasAttribute(TigrAssemblerContigAttribute attribute) {
		return attributes.containsKey(attribute);
	}
    @Override
    public Map<TigrAssemblerContigAttribute, String> getAttributes() {
        return attributes;
    }
    

    


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof TigrAssemblerContig)) {
			return false;
		}
		TigrAssemblerContig other = (TigrAssemblerContig) obj;
		if (attributes == null) {
			if (other.getAttributes() != null) {
				return false;
			}
		} else if (!attributes.equals(other.getAttributes())) {
			return false;
		}
		return true;
	}





	public static class Builder extends AbstractContigBuilder<TigrAssemblerPlacedRead, DefaultTigrAssemblerContig>{
        private EnumMap<TigrAssemblerContigAttribute,String> contigAttributes = new EnumMap<TigrAssemblerContigAttribute,String>(TigrAssemblerContigAttribute.class);
        private Map<String, EnumMap<TigrAssemblerReadAttribute,String>> readAttributeMaps = new LinkedHashMap<String, EnumMap<TigrAssemblerReadAttribute,String>>();
        /**
         * @param id
         * @param consensus
         */
        public Builder(String id, NucleotideSequence consensus) {
            super(id, consensus);
        }
        public Builder(String id, NucleotideSequence consensus,Map<TigrAssemblerContigAttribute,String> attributes) {
            super(id, consensus);
            this.contigAttributes.putAll(attributes);
        }
        public Builder addAttribute(TigrAssemblerContigAttribute attribute, String value){
            this.contigAttributes.put(attribute, value);
            return this;
        }
        public Builder removeAttribute(TigrAssemblerContigAttribute attribute){
            this.contigAttributes.remove(attribute);
            return this;
        }
        @Override
        public DefaultTigrAssemblerContig build() {
            Set<TigrAssemblerPlacedRead> reads = new LinkedHashSet<TigrAssemblerPlacedRead>();
            for(PlacedReadBuilder<TigrAssemblerPlacedRead> builder : getAllPlacedReadBuilders()){
               ((TigrAssemblerPlacedReadBuilder)builder).addAllAttributes(readAttributeMaps.get(builder.getId()));
                reads.add(builder.build());
            }
            return new DefaultTigrAssemblerContig(getContigId(),getConsensusBuilder().build(),
                    reads,contigAttributes);
        }
    
        public Builder addReadAttributes(String id, EnumMap<TigrAssemblerReadAttribute, String> readAttributes) {
            readAttributeMaps.put(id, readAttributes);
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected TigrAssemblerPlacedReadBuilder createPlacedReadBuilder(
                TigrAssemblerPlacedRead read) {
            TigrAssemblerPlacedReadBuilder builder =DefaultTigrAssemblerPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    read.getNucleotideSequence().toString(), 
                    (int)read.getStart(), 
                    read.getDirection(), 
                    read.getValidRange(),
                    read.getUngappedFullLength());
            for(Entry<TigrAssemblerReadAttribute,String> entry : read.getAttributes().entrySet()){
                builder.addAttribute(entry.getKey(), entry.getValue());
            }
            return builder;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected TigrAssemblerPlacedReadBuilder createPlacedReadBuilder(
                String id, int offset, Range validRange, String basecalls,
                Direction dir) {
            return DefaultTigrAssemblerPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    id, 
                    basecalls, 
                    offset, 
                    dir, 
                    validRange,
                    //TODO need to actually compute ungapped full length here
                    //should we pull from frg or db?
                    (int)validRange.getEnd());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected TigrAssemblerPlacedRead createPlacedRead(
                Read<ReferenceEncodedNucleotideSequence> read, long offset,
                Direction dir, int ungappedFullLength, Range validRange) {
            return DefaultTigrAssemblerPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    Nucleotides.asString(read.getNucleotideSequence().asList()), 
                    (int)offset, 
                    dir, 
                    validRange,
                    ungappedFullLength)
                    .build();
        }
        
        
    }

	
}
