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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.Builder;

/**
 * @author dkatzel
 *
 *
 */
public final class GappedNucleotideAlignmentDataStore {
	 
	private GappedNucleotideAlignmentDataStore(){
		//can not instantiate
	}
    public static NucleotideSequenceDataStore createFromAlnFile(File alnFile) throws IOException{
        GappedAlignmentDataStoreBuilder builder = new GappedAlignmentDataStoreBuilder();
        AlnParser.parse(alnFile, builder);
        return builder.build();
    }
   

    
    private static class GappedAlignmentDataStoreBuilder implements AlnVisitor, Builder<NucleotideSequenceDataStore>{
        private final Map<String, NucleotideSequenceBuilder> builders = new LinkedHashMap<String, NucleotideSequenceBuilder>();
       
        
        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceDataStore build() {
            Map<String, NucleotideSequence> map = new LinkedHashMap<String, NucleotideSequence>(builders.size());
            Iterator<Entry<String, NucleotideSequenceBuilder>> entrySet = builders.entrySet().iterator();
            //all the sequences in an aln file should be stretched to the same length
            //so we should be able to dramatically decrease memory usage by making
            //all the reads reference sequences aligned to the first read
            if(entrySet.hasNext()){
            	Entry<String, NucleotideSequenceBuilder> firstEntry = entrySet.next();
            	//the 1st sequence will become our reference for all others
            	NucleotideSequence firstSequence = firstEntry.getValue().build();
            	map.put(firstEntry.getKey(), firstSequence);
            	while(entrySet.hasNext()){
            		Entry<String, NucleotideSequenceBuilder> entry = entrySet.next();
            		NucleotideSequence seq = entry.getValue()
            									.setReferenceHint(firstSequence, 0)
            									.build();
            		map.put(entry.getKey(), seq);
            	}
	            builders.clear();
            }
            return DataStoreUtil.adapt(NucleotideSequenceDataStore.class, map);
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
        public void visitFile() {
        	//no-op
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfFile() {
        	//no-op
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginGroup() {
        	//no-op
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndGroup() {
        	//no-op
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitAlignedSegment(String id, String gappedAlignment) {
            if(!builders.containsKey(id)){
                builders.put(id, new NucleotideSequenceBuilder());
            }
            builders.get(id).append(gappedAlignment);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConservationInfo(
                List<ConservationInfo> conservationInfos) {
        	//no-op
        }
        
    }
  
}
