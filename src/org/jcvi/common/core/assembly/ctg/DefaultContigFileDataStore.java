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
/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.util.Builder;


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
			return MapDataStoreAdapter.adapt(CtgContigDataStore.class, map);
		}
    	 
    	 
    }
}
