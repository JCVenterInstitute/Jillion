package org.jcvi.jillion.sam.transform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.sam.SamFileParser;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

public class SamTransformationService {

	private final File samFile;
	private final NucleotideSequenceDataStore referenceDataStore;

	public SamTransformationService(File samFile, File referenceFasta) throws IOException {
		this.samFile = samFile;
		NucleotideFastaDataStore ungappedReferenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
																	.build();
		
		referenceDataStore = SamGappedReferenceBuilderVisitor.createGappedReferencesFrom(samFile, ungappedReferenceDataStore);
	}
	
	public void transform(final AssemblyTransformer transformer){
		
		try {
			SamTransformerVisitor visitor = new SamTransformerVisitor(referenceDataStore, transformer);
			new SamFileParser(samFile).accept(visitor);
		} catch (Exception e) {
			throw new IllegalStateException("error parsing sam file", e);
		}
	}
	
	
	private static final class SamTransformerVisitor implements SamVisitor{

		private final AssemblyTransformer transformer;
		private SamHeader header;
		private final NucleotideSequenceDataStore referenceDataStore;
		private Map<String,GrowableIntArray> gapOffsetMap;
		
		public SamTransformerVisitor(NucleotideSequenceDataStore referenceDataStore, AssemblyTransformer transformer) throws DataStoreException {
			this.referenceDataStore = referenceDataStore;
			this.transformer = transformer;
			gapOffsetMap = new HashMap<String, GrowableIntArray>(MapUtil.computeMinHashMapSizeWithoutRehashing(referenceDataStore.getNumberOfRecords()));
			
		}

		@Override
		public void visitHeader(SamHeader header) {
			this.header = header;
			
			for(ReferenceSequence refSeq : header.getReferenceSequences()){
				String id = refSeq.getName();
				try {
					NucleotideSequence ref = referenceDataStore.get(id);
					if(ref ==null){
						throw new IllegalStateException("error could not find reference sequence in fasta file with id " + id);
					}
					System.out.println(id + " num gaps = " + ref.getNumberOfGaps());
					transformer.referenceOrConsensus(id, ref);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error getting reference sequence from fasta file", e);
				}
				
			}
		}
			
		

		@Override
		public void visitRecord(SamRecord record) {
			if(record.isPrimary()){
				
				if(record.mapped()){
					String refName = record.getReferenceName();
					try {
						NucleotideSequence referenceSeq = referenceDataStore.get(refName);
					
					Direction dir = record.getDirection();
					Cigar cigar = record.getCigar();
					int rawLength = cigar.getRawUnPaddedReadLength();
					Range validRange = cigar.getValidRange();
					int gappedStartOffset = referenceSeq.getGappedOffsetFor(record.getStartPosition()-1);
					/*if(record.getQueryName().equals("1IONJCVI_0163_3X7JL:1:1:03038:01555#CCTGGTTGTCGAT/1")){
						System.out.println("here");
					}
					*/
					//extra insertions have been added to the reference
					//from other reads that we don't know about
					//modify the cigar accordingly
					transformer.aligned(record.getQueryName(), record.getSequence(), record.getQualities(), null, null, 
							refName, 
							gappedStartOffset, 
							dir, 
							cigar.toGappedTrimmedSequence(record.getSequence()), 
							
							new ReadInfo(validRange, rawLength));
					} catch (DataStoreException e) {
						throw new IllegalStateException("unknown reference " + refName, e);
					}
				}else{
					transformer.notAligned(record.getQueryName(), record.getSequence(), record.getQualities(), null, null);
					
				}
			}
				
		}

		@Override
		public void visitEnd() {
			transformer.endAssembly();
			
		}
			
		}

		
	
	
}
