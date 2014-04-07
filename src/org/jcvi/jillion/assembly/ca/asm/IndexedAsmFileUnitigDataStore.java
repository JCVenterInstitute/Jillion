/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.ca.asm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.AsmVisitorCallback.AsmVisitorMemento;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code IndexedAsmFileContigDataStore} is an {@link AsmUnitigDataStore}
 * implementation that only stores indexes and range offsets
 * of unitigs in the input asm file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each unitig
 * must be re-parsed each time and the asm file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedAsmFileUnitigDataStore  implements AsmUnitigDataStore{
	
private final  DataStore<NucleotideSequence> fullLengthSequences;
	private final Map<String, AsmVisitorMemento> contigMementos;
	private final AsmParser parser;
	
	private volatile boolean closed =false;
	
	public static AsmUnitigDataStore create(File asmFile, DataStore<NucleotideSequence> fullLengthSequences, DataStoreFilter filter) throws IOException{
		VisitorBuilder visitorBuilder = new VisitorBuilder(filter);
		AsmParser parser = AsmFileParser.create(asmFile);
		parser.parse(visitorBuilder);
		return visitorBuilder.build(parser, fullLengthSequences);
	}
	
	public IndexedAsmFileUnitigDataStore(AsmParser parser,
			DataStore<NucleotideSequence> fullLengthSequences,
			Map<String, AsmVisitorMemento> contigMementos) {
		this.parser = parser;
		this.fullLengthSequences = fullLengthSequences;
		this.contigMementos = contigMementos;
	}

	private void checkNotClosed(){
		if(closed){
			throw new DataStoreClosedException("datastore is closed");
		}
	}
	
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		checkNotClosed();		
		return DataStoreStreamingIterator.create(this, contigMementos.keySet().iterator());
	}


	@Override
	public AsmUnitig get(String id) throws DataStoreException {
		checkNotClosed();
		AsmVisitorMemento memento = contigMementos.get(id);
		if(memento ==null){
			return null;
		}
		//since read valid range and id info is
		//defined before the memento for the contig
		//must parse portions of the file 3 times
		//1. parse unitig starting from memento to get reads we need.
		//2. parse beginning of file to get read info
		//3. parse unitig starting from memento again to build unitig object
		try {
			UnitigReadIdCollector visitor = new UnitigReadIdCollector(id);
			parser.parse(visitor, memento);
			Set<String> reads = visitor.getReadsInContig();
			ValidRangeVisitor validRangeVisitor = new ValidRangeVisitor(reads);
			parser.parse(validRangeVisitor);
			SingleContigVisitorBuilder contigBuilder = new SingleContigVisitorBuilder(validRangeVisitor.getValidRanges());
			parser.parse(contigBuilder, memento);
			return contigBuilder.build();
		} catch (IOException e) {
			throw new DataStoreException("error parsing asm file", e);
		}		
	}


	@Override
	public boolean contains(String id) throws DataStoreException {
		checkNotClosed();
		return contigMementos.containsKey(id);
	}


	@Override
	public long getNumberOfRecords() throws DataStoreException {
		checkNotClosed();
		return contigMementos.size();
	}


	@Override
	public boolean isClosed() {
		return closed;
	}


	@Override
	public StreamingIterator<AsmUnitig> iterator() throws DataStoreException {
		return new DataStoreIterator<AsmUnitig>(this);
	}


	@Override
	public StreamingIterator<DataStoreEntry<AsmUnitig>> entryIterator()
			throws DataStoreException {
		 StreamingIterator<DataStoreEntry<AsmUnitig>> iter = new StreamingIterator<DataStoreEntry<AsmUnitig>>(){
			 
    		 StreamingIterator<AsmUnitig> delegate =iterator();
			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}

			@Override
			public void close() {
				delegate.close();
			}

			@Override
			public DataStoreEntry<AsmUnitig> next() {
				AsmUnitig trace = delegate.next();
				return new DataStoreEntry<AsmUnitig>(trace.getId(), trace);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
    		 
    	 };
		return DataStoreStreamingIterator.create(this, iter);
	}

	@Override
	public void close() throws IOException {
		closed=true;		
	}
	
	
	private static class VisitorBuilder implements AsmVisitor{
		private final Map<String,AsmVisitorMemento> mementos = new LinkedHashMap<String, AsmVisitorMemento>();
		private final DataStoreFilter filter;
		public VisitorBuilder(DataStoreFilter filter) {
			this.filter = filter;
		}
		@Override
		public void visitLibraryStatistics(String externalId, long internalId,
				float meanOfDistances, float stdDev, long min, long max,
				List<Long> histogram) {
			//no-op			
		}
		@Override
		public void visitRead(String externalId, long internalId,
				MateStatus mateStatus, boolean isSingleton, Range clearRange) {
			//no-op
			
		}
		@Override
		public void visitMatePair(String externalIdOfRead1,
				String externalIdOfRead2, MateStatus mateStatus) {
			//no-op			
		}
		@Override
		public AsmUnitigVisitor visitUnitig(AsmVisitorCallback callback,
				String externalId, long internalId, float aStat,
				float measureOfPolymorphism, UnitigStatus status,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads) {
			if(filter.accept(externalId)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("must be able to create mementos");
				}
				mementos.put(externalId, callback.createMemento());
			}
			return null;
		}
		@Override
		public void visitUnitigLink(String externalUnitigId1,
				String externalUnitigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				boolean isPossibleChimera, int numberOfEdges,
				float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}
		@Override
		public void visitContigLink(String externalContigId1,
				String externalContigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}
		@Override
		public AsmContigVisitor visitContig(AsmVisitorCallback callback,
				String externalId, long internalId, boolean isDegenerate,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads,
				long numberOfUnitigs, long numberOfVariants) {
			
			//always skip since we don't care about underlying contig data here
			return null;
		}
		@Override
		public AsmScaffoldVisitor visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, int numberOfContigPairs) {
			//no-op 
			return null;
		}
		@Override
		public void visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, String externalContigId) {
			//no-op 			
		}
		@Override
		public void visitScaffoldLink(String externalScaffoldId1,
				String externalScaffoldId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op 			
		}
		
		@Override
		public void visitEnd() {
			//no-op			
		}

		@Override
		public void halted() {
			//no-op
			
		}
		
		public AsmUnitigDataStore build(AsmParser parser, DataStore<NucleotideSequence> fullLengthSequences){
			return new IndexedAsmFileUnitigDataStore(parser, fullLengthSequences, mementos);
		}
	}

	private static class UnitigReadIdCollector implements AsmVisitor{
		private Set<String> readsInContig;
		private final String contigId;
		
		public UnitigReadIdCollector(String contigId) {
			this.contigId = contigId;
		}

		@Override
		public void visitLibraryStatistics(String externalId, long internalId,
				float meanOfDistances, float stdDev, long min, long max,
				List<Long> histogram) {
			//no-op			
		}

		@Override
		public void visitRead(String externalId, long internalId,
				MateStatus mateStatus, boolean isSingleton, Range clearRange) {
			//no-op				
		}

		@Override
		public void visitMatePair(String externalIdOfRead1,
				String externalIdOfRead2, MateStatus mateStatus) {
			//no-op			
		}

		@Override
		public AsmUnitigVisitor visitUnitig(final AsmVisitorCallback callback,
				String externalId, long internalId, float aStat,
				float measureOfPolymorphism, UnitigStatus status,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads) {
			if(contigId.equals(externalId)){
				readsInContig = new HashSet<String>((int)numberOfReads);
				return new AsmUnitigVisitor() {
					
					@Override
					public void visitReadLayout(char readType, String externalReadId,
							DirectedRange readRange, List<Integer> gapOffsets) {
						readsInContig.add(externalReadId);						
					}
					
					@Override
					public void halted() {
						//no-op						
					}
					
					@Override
					public void visitEnd() {
						//we finished the current contig
						//halt parsing
						callback.haltParsing();					
					}
					
					
				};
			}
			return null;
		}

		@Override
		public void visitUnitigLink(String externalUnitigId1,
				String externalUnitigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				boolean isPossibleChimera, int numberOfEdges,
				float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}

		@Override
		public void visitContigLink(String externalContigId1,
				String externalContigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}

		@Override
		public AsmContigVisitor visitContig(final AsmVisitorCallback callback,
				String externalId, long internalId, boolean isDegenerate,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads,
				long numberOfUnitigs, long numberOfVariants) {
			//always skip
			return null;
		}

		@Override
		public AsmScaffoldVisitor visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, int numberOfContigPairs) {
			//no-op
			return null;
		}

		@Override
		public void visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, String externalContigId) {
			//no-op
			
		}

		@Override
		public void visitScaffoldLink(String externalScaffoldId1,
				String externalScaffoldId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}
		
		@Override
		public void visitEnd() {
			//no-op			
		}

		@Override
		public void halted() {
			//no-op
			
		}
		public final Set<String> getReadsInContig() {
			if(readsInContig ==null){
				throw new IllegalStateException("contig not found");
			}
			return readsInContig;
		}
	}
	
	private static class ValidRangeVisitor implements AsmVisitor{
		private final Set<String> readsInContig;
		private final Map<String,Range> validRanges;
		
		public ValidRangeVisitor(Set<String> readsInContig) {
			this.readsInContig = readsInContig;
			validRanges = new HashMap<String, Range>(MapUtil.computeMinHashMapSizeWithoutRehashing(readsInContig.size()));
		}

		public Map<String,Range>  getValidRanges() {
			return validRanges;
		}

		@Override
		public void visitLibraryStatistics(String externalId, long internalId,
				float meanOfDistances, float stdDev, long min, long max,
				List<Long> histogram) {
			//no-op			
		}

		@Override
		public void visitRead(String externalId, long internalId,
				MateStatus mateStatus, boolean isSingleton, Range clearRange) {
			if(readsInContig.contains(externalId)){
				validRanges.put(externalId, clearRange);
			}
			
		}

		@Override
		public void visitMatePair(String externalIdOfRead1,
				String externalIdOfRead2, MateStatus mateStatus) {
			//no-op			
		}

		@Override
		public AsmUnitigVisitor visitUnitig(AsmVisitorCallback callback,
				String externalId, long internalId, float aStat,
				float measureOfPolymorphism, UnitigStatus status,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads) {
			//if we get here stop parsing
			callback.haltParsing();
			return null;
		}

		@Override
		public void visitUnitigLink(String externalUnitigId1,
				String externalUnitigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				boolean isPossibleChimera, int numberOfEdges,
				float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}

		@Override
		public void visitContigLink(String externalContigId1,
				String externalContigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op
			
		}

		@Override
		public AsmContigVisitor visitContig(AsmVisitorCallback callback,
				String externalId, long internalId, boolean isDegenerate,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads,
				long numberOfUnitigs, long numberOfVariants) {
			//if we get here stop parsing
			callback.haltParsing();
			return null;
		}

		@Override
		public AsmScaffoldVisitor visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, int numberOfContigPairs) {
			//if we get here stop parsing
			callback.haltParsing();
			return null;
		}

		@Override
		public void visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, String externalContigId) {
			//if we get here stop parsing
			callback.haltParsing();			
		}

		@Override
		public void visitScaffoldLink(String externalScaffoldId1,
				String externalScaffoldId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op
			
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
	
	private class SingleContigVisitorBuilder implements AsmVisitor{
		private final Map<String, Range> validRanges;
		private AsmUnitigBuilder builder;
		
		public SingleContigVisitorBuilder(Map<String, Range> validRanges) {
			this.validRanges = validRanges;
		}

		@Override
		public void visitLibraryStatistics(String externalId, long internalId,
				float meanOfDistances, float stdDev, long min, long max,
				List<Long> histogram) {
			//no-op			
		}

		@Override
		public void visitRead(String externalId, long internalId,
				MateStatus mateStatus, boolean isSingleton, Range clearRange) {
			//no-op
			
		}

		@Override
		public void visitMatePair(String externalIdOfRead1,
				String externalIdOfRead2, MateStatus mateStatus) {
			//no-op			
		}

		@Override
		public AsmUnitigVisitor visitUnitig(final AsmVisitorCallback callback,
				String externalId, long internalId, float aStat,
				float measureOfPolymorphism, UnitigStatus status,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads) {
			//should be the first unitig we see
			return new AbstractAsmUnitigBuilder(externalId,consensusSequence,
					fullLengthSequences, validRanges) {
				
				@Override
				protected void visitUnitig(AsmUnitigBuilder builder) {
					SingleContigVisitorBuilder.this.builder = builder;
					callback.haltParsing();
				}
			};
		}

		@Override
		public void visitUnitigLink(String externalUnitigId1,
				String externalUnitigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				boolean isPossibleChimera, int numberOfEdges,
				float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}

		@Override
		public void visitContigLink(String externalContigId1,
				String externalContigId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op			
		}

		@Override
		public AsmContigVisitor visitContig(final AsmVisitorCallback callback,
				String externalId, long internalId, boolean isDegenerate,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads,
				long numberOfUnitigs, long numberOfVariants) {
			
			return null;
		}

		@Override
		public AsmScaffoldVisitor visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, int numberOfContigPairs) {
			//no-op
			return null;
		}

		@Override
		public void visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, String externalContigId) {
			//no-op			
		}

		@Override
		public void visitScaffoldLink(String externalScaffoldId1,
				String externalScaffoldId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//no-op
			
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}

		@Override
		public void halted() {
			//no-op			
		}

		public AsmUnitig build(){
			if(builder ==null){
				throw new IllegalStateException("could not find contig in asm file");
			}
			return builder.build();
		}
	}
}
