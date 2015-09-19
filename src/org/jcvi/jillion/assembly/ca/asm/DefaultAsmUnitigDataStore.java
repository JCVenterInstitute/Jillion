/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ca.asm;

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


final class DefaultAsmUnitigDataStore {

	public static AsmUnitigDataStore create(File asmFile, DataStore<NucleotideSequence> fullLengthSequences, DataStoreFilter filter) throws IOException{
		VisitorBuilder visitorBuilder = new VisitorBuilder(filter, fullLengthSequences);
		AsmFileParser.create(asmFile).parse(visitorBuilder);
		return visitorBuilder.build();
	}
	
	private DefaultAsmUnitigDataStore(){
		//can not instantiate
	}
	
	private static class VisitorBuilder implements AsmVisitor{
		private final Map<String,Range> validRanges = new HashMap<String, Range>();
		private final  DataStore<NucleotideSequence> fullLengthSequences;
		private final DataStoreFilter filter;
		
		private final Map<String, AsmUnitig> contigs = new LinkedHashMap<String, AsmUnitig>();
		
		
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
				final String externalId, long internalId, float aStat,
				float measureOfPolymorphism, UnitigStatus status,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads) {
			if(filter.accept(externalId)){
				return new AbstractAsmUnitigBuilder(externalId,consensusSequence,
						fullLengthSequences, validRanges) {
					
					@Override
					protected void visitUnitig(AsmUnitigBuilder builder) {
						contigs.put(externalId, builder.build());						
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
		public AsmContigVisitor visitContig(AsmVisitorCallback callback,
				final String externalId, long internalId, boolean isDegenerate,
				NucleotideSequence consensusSequence,
				QualitySequence consensusQualities, long numberOfReads,
				long numberOfUnitigs, long numberOfVariants) {
			//always skip
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
		
		
		@Override
		public void visitEnd() {
			//no-op			
		}

		@Override
		public void halted() {
			//no-op
			
		}

		public AsmUnitigDataStore build(){
			return DataStoreUtil.adapt(AsmUnitigDataStore.class, contigs);
		}
	}
}
