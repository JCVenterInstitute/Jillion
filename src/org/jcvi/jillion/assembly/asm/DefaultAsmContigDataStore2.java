package org.jcvi.jillion.assembly.asm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;


public class DefaultAsmContigDataStore2 {

	public static AsmContigDataStore create(File asmFile, DataStore<NucleotideSequence> fullLengthSequences, DataStoreFilter filter) throws IOException{
		VisitorBuilder visitorBuilder = new VisitorBuilder(filter, fullLengthSequences);
		AsmFileParser.create(asmFile).accept(visitorBuilder);
		return visitorBuilder.build();
	}
	
	
	private static class VisitorBuilder implements AsmVisitor2{
		private final Map<String,Range> validRanges = new HashMap<String, Range>();
		private final  DataStore<NucleotideSequence> fullLengthSequences;
		private final DataStoreFilter filter;
		
		private final LinkedHashMap<String, AsmContig> contigs = new LinkedHashMap<String, AsmContig>();
		
		
		public VisitorBuilder(DataStoreFilter filter,
				DataStore<NucleotideSequence> fullLengthSequences) {
			this.filter = filter;
			this.fullLengthSequences = fullLengthSequences;
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
			validRanges.put(externalId, clearRange);			
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
			// always skip
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
				final String externalId, long internalId, boolean isDegenerate,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads,
				long numberOfUnitigs, long numberOfVariants) {
			if(filter.accept(externalId)){
				return new AbstractAsmContigBuilder(externalId,consensusSequence, isDegenerate,
						fullLengthSequences, validRanges) {
					
					@Override
					protected void visitContig(AsmContigBuilder builder) {
						contigs.put(externalId, builder.build());						
					}
				};
			}
			return null;
		}

		@Override
		public AsmScaffoldVisitor visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, int numberOfContigPairs) {
			//always skip
			return null;
		}

		@Override
		public void visitScaffold(AsmVisitorCallback callback,
				String externalId, long internalId, String externalContigId) {
			//always skip			
		}

		@Override
		public void visitScaffoldLink(String externalScaffoldId1,
				String externalScaffoldId2, LinkOrientation orientation,
				OverlapType overlapType, OverlapStatus status,
				int numberOfEdges, float meanDistance, float stddev,
				Set<MatePairEvidence> matePairEvidence) {
			//always skip			
		}
		
		
		public AsmContigDataStore build(){
			return DataStoreUtil.adapt(AsmContigDataStore.class, contigs);
		}
	}
}
