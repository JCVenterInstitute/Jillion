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
 * Created on Jan 27, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

public class DefaultPositionFastaFileDataStore{

	public static PositionFastaDataStoreBuilderVisitor createBuilder(){
		return createBuilder(null);
	}
	public static PositionFastaDataStoreBuilderVisitor createBuilder(FastXFilter filter){
		return new PositionFastaDataStoreBuilderVisitorImpl(filter);
	}
	
	public static PositionFastaDataStore create(File fastaFile) throws FileNotFoundException{
		return create(fastaFile,null);
	}
	public static PositionFastaDataStore create(File fastaFile, FastXFilter filter) throws FileNotFoundException{
		PositionFastaDataStoreBuilderVisitor builder = createBuilder(filter);
		FastaParser.parseFasta(fastaFile, builder);
		return builder.build();
	}
	
	public static PositionFastaDataStore create(InputStream in) throws FileNotFoundException{
		return create(in,null);
	}
	public static PositionFastaDataStore create(InputStream in, FastXFilter filter) throws FileNotFoundException{
		try{
			PositionFastaDataStoreBuilderVisitor builder = createBuilder(filter);
			FastaParser.parseFasta(in, builder);
			return builder.build();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	private static class PositionFastaDataStoreBuilderVisitorImpl extends 
	AbstractFastaFileDataStoreBuilderVisitor<ShortSymbol, Sequence<ShortSymbol>, PositionSequenceFastaRecord<Sequence<ShortSymbol>>, PositionFastaDataStore> implements PositionFastaDataStoreBuilderVisitor{
	
		@Override
		public <F extends PositionSequenceFastaRecord<Sequence<ShortSymbol>>> PositionFastaDataStoreBuilderVisitorImpl addFastaRecord(
				F fastaRecord) {
			super.addFastaRecord(fastaRecord);
			return this;
		}
	
		public PositionFastaDataStoreBuilderVisitorImpl() {
			super(new DefaultPositionFastaDataStoreBuilder());
		}
		public PositionFastaDataStoreBuilderVisitorImpl(FastXFilter filter) {
			super(new DefaultPositionFastaDataStoreBuilder(), filter);
		}
	
		@Override
		protected PositionSequenceFastaRecord<Sequence<ShortSymbol>> createFastaRecord(String id,
				String comment, String entireBody) {
			return DefaultPositionFastaRecordFactory.getInstance().createFastaRecord(id, comment, entireBody);
		}
		
	}
}
