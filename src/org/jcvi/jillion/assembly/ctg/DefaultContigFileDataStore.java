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
/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;


public final class DefaultContigFileDataStore {
  
    public static CtgContigDataStore create(NucleotideSequenceDataStore fullLengthSequenceDataStore, File contigFile) throws FileNotFoundException{
    	DefaultContigDataStoreBuilder builder = new DefaultContigDataStoreBuilder(fullLengthSequenceDataStore);
    	ContigFileParser.parse(contigFile, builder);
    	return builder.build();
    }
    public static CtgContigDataStore create(NucleotideSequenceFastaDataStore fullLengthSequenceDataStore, File contigFile) throws FileNotFoundException{
    	DefaultContigDataStoreBuilder builder = new DefaultContigDataStoreBuilder(fullLengthSequenceDataStore);
    	ContigFileParser.parse(contigFile, builder);
    	return builder.build();
    }
    public static CtgContigDataStore create(File contigFile) throws FileNotFoundException{
    	DefaultContigDataStoreBuilder builder = new DefaultContigDataStoreBuilder();
    	ContigFileParser.parse(contigFile, builder);
    	return builder.build();
    }

   
    
    private static final class DefaultContigDataStoreBuilder extends AbstractContigFileVisitorBuilder implements Builder<CtgContigDataStore>{
    	 Map<String, Contig<AssembledRead>> map= new LinkedHashMap<String, Contig<AssembledRead>>();

		public DefaultContigDataStoreBuilder() {
			super();
		}

		public DefaultContigDataStoreBuilder(
				NucleotideSequenceDataStore fullLengthSequenceDataStore) {
			super(fullLengthSequenceDataStore);
		}

		public DefaultContigDataStoreBuilder(
				NucleotideSequenceFastaDataStore fullLengthSequenceDataStore) {
			super(fullLengthSequenceDataStore);
		}

		@Override
		protected void addContig(Contig<AssembledRead> contig) {
			map.put(contig.getId(), contig);			
		}

		@Override
		public CtgContigDataStore build() {
			return DataStoreUtil.adapt(CtgContigDataStore.class, map);
		}
    	 
    	 
    }
}
