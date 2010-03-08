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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jcvi.assembly.AbstractContigBuilder;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

/**
 * {@code DefaultTigrAssemblerContig} is a {@link Contig}
 * implementation for TIGR Assembler contig data.
 * @author dkatzel
 *
 *
 */
public class DefaultTigrAssemblerContig extends DefaultContig<TigrAssemblerPlacedRead> implements TigrAssemblerContig{
    private final Map<String,String> attributes;
    /**
     * @param id
     * @param consensus
     * @param virtualReads
     * @param circular
     */
    protected DefaultTigrAssemblerContig(String id,
            NucleotideEncodedGlyphs consensus,
            Set<VirtualPlacedRead<TigrAssemblerPlacedRead>> virtualReads, boolean circular, Map<String,String> attributes) {
        super(id, consensus, virtualReads, circular);
        this.attributes = Collections.unmodifiableMap(new HashMap<String, String>(attributes));
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public static class Builder extends AbstractContigBuilder<TigrAssemblerPlacedRead, DefaultTigrAssemblerContig>{
        private Map<String,String> contigAttributes = new HashMap<String, String>();
        private Map<String, Map<String,String>> readAttributeMaps = new HashMap<String, Map<String,String>>();
        /**
         * @param id
         * @param consensus
         */
        public Builder(String id, NucleotideEncodedGlyphs consensus) {
            super(id, consensus);
        }
        public Builder(String id, NucleotideEncodedGlyphs consensus,Map<String,String> attributes) {
            super(id, consensus);
            this.contigAttributes.putAll(attributes);
        }
        public Builder addAttribute(String key, String value){
            this.contigAttributes.put(key, value);
            return this;
        }
        public Builder removeAttribute(String key){
            this.contigAttributes.remove(key);
            return this;
        }
        @Override
        public DefaultTigrAssemblerContig build() {
            return new DefaultTigrAssemblerContig(getId(),getConsensus(),
                    getVirtualReads(),isCircular(),contigAttributes);
        }
    
        public Builder addReadAttributes(String id, Map<String, String> readAttributes) {
            readAttributeMaps.put(id, readAttributes);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected TigrAssemblerPlacedRead createPlacedRead(
                Read<ReferencedEncodedNucleotideGlyphs> read, long offset,
                SequenceDirection dir) {
            return new DefaultTigrAssemblerPlacedRead(read, offset, dir,readAttributeMaps.get(read.getId()));
        }
        
        
    }
}
