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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreUtil;
import org.jcvi.common.core.util.Builder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

/**
 * {@code DefaultTigrAssemblerFileContigDataStore} is an implemenation
 * of {@link AbstractTigrAssemblerFileContigDataStore} that stores
 * all TIGR Assembler contigs in a HashMap, This may take up a lot 
 * of memory if the contigs are large or if there are many contigs.
 * @author dkatzel
 *
 *
 */
public final class DefaultTasmFileContigDataStore {

    
    public static TasmContigDataStore create(File tasmFile) throws FileNotFoundException{ 
    	BuilderImpl builder = new BuilderImpl();
    	 TasmFileParser.parse(tasmFile, builder);
    	 return builder.build();
    }
    
    private DefaultTasmFileContigDataStore(){
    	//can not instantiate
    }
   
    private static class BuilderImpl implements TasmFileVisitor, Builder<TasmContigDataStore> {

        private DefaultTasmContig.Builder currentBuilder;

        private String currentContigId;
        private NucleotideSequence currentContigConsensus;
        private Map<TasmContigAttribute, String> currentContigAttributes;
        
        private EnumMap<TasmReadAttribute, String> currentReadAttributes;
        private String currentReadId;
        private int currentOffset;
        private Range currentValidRange;
        private Direction currentDirection;
        
        private String currentReadBasecalls;
        private final Map<String, TasmContig> contigs = new LinkedHashMap<String, TasmContig>();
       
        
        @Override
		public TasmContigDataStore build() {
			return DataStoreUtil.adapt(TasmContigDataStore.class, contigs);
		}

		/**
        * {@inheritDoc}
        */
        @Override
        public void visitContigAttribute(String key, String value) {
            currentContigAttributes.put(TasmContigAttribute.getAttributeFor(key), value);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadAttribute(String key, String value) {
            currentReadAttributes.put(TasmReadAttribute.getAttributeFor(key), value);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusBasecallsLine(String lineOfBasecalls) {
            currentContigConsensus = new NucleotideSequenceBuilder(lineOfBasecalls).build();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitNewContig(String contigId) {        
            currentContigId = contigId;        
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitNewRead(String readId, int offset, Range validRange,
                Direction dir) {
            currentReadId = readId;
            currentOffset=offset;
            currentValidRange = validRange;
            currentDirection = dir;
            
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadBasecallsLine(String lineOfBasecalls) {
            currentReadBasecalls = lineOfBasecalls;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitLine(String line) {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfFile() {
        	 if(currentBuilder !=null){
                 visitContig(currentBuilder.build());
             }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginContigBlock() {
            if(currentBuilder !=null){
                visitContig(currentBuilder.build());
            }
            currentBuilder=null;
            currentContigAttributes= new EnumMap<TasmContigAttribute, String>(TasmContigAttribute.class);
        }
        /**
         * Visit the given {@link TasmContig} to this DataStore.
         * @param contig the TIGR Assembler contig being visited.
         */
        private  void visitContig(TasmContig contig){
        	contigs.put(contig.getId(), contig);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginReadBlock() {
            currentReadId=null;
            currentOffset=0;
            currentValidRange=null;
            currentDirection=null;
            currentReadAttributes = new EnumMap<TasmReadAttribute, String>(TasmReadAttribute.class);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndContigBlock() {
            currentBuilder = new DefaultTasmContig.Builder(currentContigId, currentContigConsensus,currentContigAttributes);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndReadBlock() {
        	if(currentReadId !=null){
	            this.currentBuilder.addReadAttributes(currentReadId, currentReadAttributes);
	            this.currentBuilder.addRead(currentReadId, currentOffset, currentValidRange,
	                    currentReadBasecalls, currentDirection,
	                    (int)currentValidRange.getEnd());
        	}
        }
       
}
    
    
    
}
