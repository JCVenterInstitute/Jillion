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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta.nt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fasta.impl.AbstractFastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code DefaultNucleotideFastaFileDataStore} is the default implementation
 * of {@link NucleotideSequenceFastaDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeNucleotideSequenceFastaFileDataStore
 *
 */
final class DefaultNucleotideSequenceFastaFileDataStore{
	
	private DefaultNucleotideSequenceFastaFileDataStore(){
		//can not instantiate.
	}
	public static NucleotideFastaDataStoreBuilderVisitor createBuilder(){
		return createBuilder(null);
	}
	public static NucleotideFastaDataStoreBuilderVisitor createBuilder(DataStoreFilter filter){
		return new NucleotideFastaDataStoreBuilderVisitorImpl(filter);
	}
	
	public static NucleotideSequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		return create(fastaFile,null);
	}
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		NucleotideFastaDataStoreBuilderVisitor builder = createBuilder(filter);
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	
	public static NucleotideSequenceFastaDataStore create(InputStream in) throws FileNotFoundException{
		return create(in,null);
	}
	public static NucleotideSequenceFastaDataStore create(InputStream in, DataStoreFilter filter) throws FileNotFoundException{
		try{
			NucleotideFastaDataStoreBuilderVisitor builder = createBuilder(filter);
			FastaFileParser.parse(in, builder);
			return builder.build();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
    

    private static class NucleotideFastaDataStoreBuilderVisitorImpl extends AbstractFastaFileDataStoreBuilderVisitor<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>implements NucleotideFastaDataStoreBuilderVisitor{

		@Override
		public NucleotideFastaDataStoreBuilderVisitor addFastaRecord(
				NucleotideSequenceFastaRecord fastaRecord) {
			super.addFastaRecord(fastaRecord);
			return this;
		}

		public NucleotideFastaDataStoreBuilderVisitorImpl() {
			super(new DefaultNucleotideSequenceFastaDataStoreBuilder());
		}
		public NucleotideFastaDataStoreBuilderVisitorImpl(DataStoreFilter filter) {
			super(new DefaultNucleotideSequenceFastaDataStoreBuilder(), filter);
		}

		@Override
		protected NucleotideSequenceFastaRecord createFastaRecord(String id,
				String comment, String entireBody) {
			return new NucleotideSequenceFastaRecordBuilder(id, entireBody)
						.comment(comment)
						.build();
		}
    	
    }
}
