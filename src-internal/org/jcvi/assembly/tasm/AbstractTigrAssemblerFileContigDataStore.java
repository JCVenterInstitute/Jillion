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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EnumMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.read.SequenceDirection;

/**
 * {@code AbstractTigrAssemblerFileContigDataStore} is a {@link DataStore}
 * of {@link TigrAssemblerContig}s.  This abstract class handles all the details
 * with parsing TIGR Assembler File, concrete implementations deal with
 * how (and what) to store of that parsed information. 
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTigrAssemblerFileContigDataStore extends AbstractDataStore<TigrAssemblerContig> implements TigrAssemblyFileVisitor,TigrAssemblerContigDataStore {

        private DefaultTigrAssemblerContig.Builder currentBuilder;

        private String currentContigId;
        private NucleotideSequence currentContigConsensus;
        private Map<TigrAssemblerContigAttribute, String> currentContigAttributes;
        
        private EnumMap<TigrAssemblerReadAttribute, String> currentReadAttributes;
        private String currentReadId;
        private int currentOffset;
        private Range currentValidRange;
        private SequenceDirection currentDirection;
        
        private String currentReadBasecalls;
        
        public AbstractTigrAssemblerFileContigDataStore(File tasmFile) throws FileNotFoundException{
            initialize(tasmFile);
            TigrAssemblyFileParser.parse(tasmFile, this);
        }
        /**
         * Perform any initialization that is needed before
         * the TIGR Assembly File is visited.
         * @param tasmFile the TIGR Assembly File to be visited, this is the 
         * same value that was passed in the constructor.
         */
        protected abstract void initialize(File tasmFile);
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitContigAttribute(String key, String value) {
            currentContigAttributes.put(TigrAssemblerContigAttribute.getAttributeFor(key), value);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadAttribute(String key, String value) {
            currentReadAttributes.put(TigrAssemblerReadAttribute.getAttributeFor(key), value);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusBasecallsLine(String lineOfBasecalls) {
            currentContigConsensus = new DefaultNucleotideSequence(lineOfBasecalls);
            
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
                SequenceDirection dir) {
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
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfFile() {
            visitBeginContigBlock();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
            
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
            currentContigAttributes= new EnumMap<TigrAssemblerContigAttribute, String>(TigrAssemblerContigAttribute.class);
        }
        /**
         * Visit the given {@link TigrAssemblerContig} to this DataStore.
         * @param contig the TIGR Assembler contig being visited.
         */
        protected abstract void visitContig(TigrAssemblerContig contig);
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginReadBlock() {
            currentReadId=null;
            currentOffset=0;
            currentValidRange=null;
            currentDirection=null;
            currentReadAttributes = new EnumMap<TigrAssemblerReadAttribute, String>(TigrAssemblerReadAttribute.class);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndContigBlock() {
            currentBuilder = new DefaultTigrAssemblerContig.Builder(currentContigId, currentContigConsensus,currentContigAttributes);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndReadBlock() {
            this.currentBuilder.addReadAttributes(currentReadId, currentReadAttributes);
            this.currentBuilder.addRead(currentReadId, currentOffset, currentValidRange,
                    currentReadBasecalls, currentDirection);
            
        }
       
}
