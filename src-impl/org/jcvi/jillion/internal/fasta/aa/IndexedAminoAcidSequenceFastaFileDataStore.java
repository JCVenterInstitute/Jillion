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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.fasta.aa.AbstractAminoAcidFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

/**
 * {@code IndexedAminoAcidSequenceFastaFileDataStore} is an implementation of 
 * {@link AminoAcidSequenceFastaDataStore} that only stores an index containing
 * file offsets to the various {@link FastaRecord}s contained
 * inside the fasta file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fasta record
 * must be seeked to and then re-parsed each time and the fasta file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedAminoAcidSequenceFastaFileDataStore{

	/**
	 * Creates a new {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedAminoAcidSequenceFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static AminoAcidFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	
	/**
	 * Creates a new {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * for.
	 * @param filter the {@link DataStoreFilter} instance used to filter out the fasta records;
	 * can not be null.
	 * @return a new instance of {@link IndexedAminoAcidSequenceFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static AminoAcidFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2 builder = createBuilder(fastaFile,filter);
		builder.initialize();
		return builder.build();
	}
	
	
	/**
	 * Creates a new {@link AminoAcidSequenceFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link AminoAcidSequenceFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link AminoAcidSequenceFastaDataStoreBuilderVisitor#addFastaRecord(AminoAcidSequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link AminoAcidSequenceFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws IOException 
	 * @throws NullPointerException if the input fasta file is null.
	 */
	private static IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2 createBuilder(File fastaFile, DataStoreFilter filter) throws IOException{
		if(fastaFile ==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new FileNotFoundException(fastaFile.getAbsolutePath());
		}
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		return new IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2(fastaFile, filter);
	}
	
	
	
	
	private static final class IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2 implements FastaVisitor, Builder<AminoAcidFastaDataStore> {
	
		private final DataStoreFilter filter;
		private final FastaFileParser parser;
		private final File fastaFile;
		
		private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos = new LinkedHashMap<String, FastaVisitorCallback.FastaVisitorMemento>();
		private IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2(File fastaFile, DataStoreFilter filter) throws IOException {
			this.fastaFile = fastaFile;
			this.filter = filter;
			this.parser = FastaFileParser.create(fastaFile);

		}

		public void initialize() throws IOException {
			parser.accept(this);
			
		}

		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(filter.accept(id)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("must be able to create memento");
				}
				mementos.put(id, callback.createMemento());
			}
			//always skip records since we don't care about the details of any records
			//during the initial parse
			return null;
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		@Override
		public void halted() {
			//no-op			
		}

		@Override
		public AminoAcidFastaDataStore build() {
			return new IndexedAminoAcidSequenceFastaFileDataStore2(fastaFile,parser, filter,mementos);
		}
	
	}
	
	public static final class IndexedAminoAcidSequenceFastaFileDataStore2 implements AminoAcidFastaDataStore {
		private volatile boolean closed =false;
		private final File fastaFile;
		private final FastaFileParser parser;
		private final DataStoreFilter filter;
		private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos;
		
		
		public IndexedAminoAcidSequenceFastaFileDataStore2(File fastaFile,
				FastaFileParser parser, DataStoreFilter filter, Map<String, FastaVisitorMemento> mementos) {
			this.fastaFile = fastaFile;
			this.parser = parser;
			this.mementos = mementos;
			this.filter = filter;
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this,mementos.keySet().iterator());
		}

		@Override
		public AminoAcidFastaRecord get(String id)
				throws DataStoreException {
			throwExceptionIfClosed();
			if(!mementos.containsKey(id)){
				return null;
			}
			SingleRecordVisitor visitor = new SingleRecordVisitor();
			try {
				parser.accept(visitor, mementos.get(id));
				return visitor.fastaRecord;
			} catch (IOException e) {
				throw new DataStoreException("error reading fasta file",e);
			}
		}

		@Override
		public StreamingIterator<AminoAcidFastaRecord> iterator() throws DataStoreException {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this,
					LargeAminoAcidSequenceFastaIterator.createNewIteratorFor(fastaFile,filter));
		}
		private void throwExceptionIfClosed() throws DataStoreException{
			if(closed){
				throw new DataStoreClosedException("datastore is closed");
			}
		}
		public boolean contains(String id) throws DataStoreException {
			throwExceptionIfClosed();
			return mementos.containsKey(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			throwExceptionIfClosed();
			return mementos.size();
		}

		@Override
		public boolean isClosed(){
			return closed;
		}

		@Override
		public void close() {
			closed=true;
			
		}

	}

	private static class SingleRecordVisitor implements FastaVisitor{
		private AminoAcidFastaRecord fastaRecord=null;
		@Override
		public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(fastaRecord !=null){
				callback.haltParsing();
				return null;
			}
			return new AbstractAminoAcidFastaRecordVisitor(id, optionalComment) {
				
				@Override
				protected void visitRecord(AminoAcidFastaRecord fastaRecord) {
					SingleRecordVisitor.this.fastaRecord = fastaRecord;
					
				}
			};
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public void halted() {
			//no-op			
		}
	}
}

