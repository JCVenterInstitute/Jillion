package org.jcvi.jillion.sam.transform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.sam.SamFileParser;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;
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
					gapOffsetMap.put(id,  new GrowableIntArray(ref.getGapOffsets()));
					
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
					Range validRange;
					int gappedStartOffset = referenceSeq.getGappedOffsetFor(record.getStartPosition()-1);
					
					
					//extra insertions have been added to the reference
					//from other reads that we don't know about
					//modify the cigar accordingly
					NucleotideSequence rawSequence;
					QualitySequence quals;
					if(dir == Direction.FORWARD){
						rawSequence = record.getSequence();
						quals = record.getQualities();
						validRange = cigar.getValidRange();
					}else{
						rawSequence = new NucleotideSequenceBuilder(record.getSequence())
											.reverseComplement()
											.build();
						if(record.getQualities() ==null){
							quals = null;
						}else{
							quals = new QualitySequenceBuilder(record.getQualities())
											.reverse()
											.build();
						}
						validRange = AssemblyUtil.reverseComplementValidRange(cigar.getValidRange(), rawLength);
					}
					
					NucleotideSequence gappedReadSequence =toGappedTrimmedSequenceBuilder(cigar, rawSequence, gapOffsetMap.get(refName), gappedStartOffset, dir)
															.build();
					
				
					//update valid range?
					transformer.aligned(record.getQueryName(), rawSequence, quals, null, null, 
							refName, 
							gappedStartOffset, 
							dir, 
							gappedReadSequence, 
							
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
			
		
	private NucleotideSequenceBuilder toGappedTrimmedSequenceBuilder(Cigar cigar, NucleotideSequence rawUngappedSequence, GrowableIntArray refGaps, int gappedStartOffset, Direction dir) {
		if(rawUngappedSequence.getNumberOfGaps() !=0){
			throw new IllegalArgumentException("rawUngapped Sequence can not have gaps");
		}
		
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder((int)rawUngappedSequence.getLength());
		int referenceOffset = gappedStartOffset;
		
		Iterator<Nucleotide> ungappedBasesIter;
		if(dir == Direction.FORWARD){
			ungappedBasesIter= rawUngappedSequence.iterator();
		}else{
			ungappedBasesIter= new NucleotideSequenceBuilder(rawUngappedSequence)
									.reverseComplement()
									.iterator();
		}
		for(CigarElement e : cigar){
			if(e.getOp() == CigarOperation.HARD_CLIP ||e.getOp() == CigarOperation.SOFT_CLIP ){
				//skip over clipped bases
				for(int i=0; i<e.getLength(); i++){
					ungappedBasesIter.next();
				}
				continue;
			}
			referenceOffset = appendBases(builder, ungappedBasesIter, refGaps, referenceOffset, e);
			
		}
		
		return builder;
	}
	
	private int appendBases(NucleotideSequenceBuilder builder, Iterator<Nucleotide> ungappedReadBaseIterator, GrowableIntArray refGaps, int refOffset, CigarElement e){
		int ret = refOffset;
		for(int i=0; i<e.getLength(); i++){
			if(e.getOp() != CigarOperation.INSERTION){
				while(refGaps.binarySearch(ret) >=0){
					//insert gap
					builder.append(Nucleotide.Gap);
					ret++;
				}
			}
			if(e.getOp() ==CigarOperation.DELETION || e.getOp() ==CigarOperation.PADDING){
				//insert gap
				builder.append(Nucleotide.Gap);
				
			}else{
				builder.append(ungappedReadBaseIterator.next());			
				
			}
			ret++;
		}
		return ret;
	}
	
}
}
