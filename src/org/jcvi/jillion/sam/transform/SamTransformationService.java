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
package org.jcvi.jillion.sam.transform;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
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
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code SamTransformationService}
 * is a class that can parse a SAM file
 * and call the appropriate methods
 * on a given {@link AssemblyTransformer}
 * so the transformer can get assembly and alignment 
 * information from the SAM file without knowing
 * anything about how SAM files are formatted.
 * 
 * @author dkatzel
 *
 */
public final class SamTransformationService implements AssemblyTransformationService{

	
	private final NucleotideSequenceDataStore referenceDataStore;
	private final SamParser parser;
	
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given SAM encoded file and a fasta file of the ungapped
	 * references sequences referred to in the SAM.  The ids in the fasta file
	 * must match the reference sequence names in the SAM file (@SQ SN:$ID) in the SAM header.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * @param referenceFasta the reference fasta file; can not be null and must exist.
	 * @throws IOException if there is a problem parsing the input files.
	 * @throws NullPointerException if either parameter is null.
	 */
	public SamTransformationService(File samFile, File referenceFasta) throws IOException {
		
		parser = SamParserFactory.create(samFile);
		NucleotideFastaDataStore ungappedReferenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
																	.build();
		
		referenceDataStore = SamGappedReferenceBuilderVisitor.createGappedReferencesFrom(parser, ungappedReferenceDataStore);
	}
	/**
	 * Parse the SAM file and call the appropriate methods on the given
	 * {@link AssemblyTransformer}.
	 * @param transformer the {@link AssemblyTransformer} instance to call
	 * the methods on; can not be null.
	 * @throws NullPointerException if transformer is null.
	 */
	@Override
	public void transform(final AssemblyTransformer transformer){
		if(transformer ==null){
			throw new NullPointerException("transformer can not be null");
		}
		try {
			SamTransformerVisitor visitor = new SamTransformerVisitor(referenceDataStore, transformer);
			parser.accept(visitor);
		} catch (Exception e) {
			throw new IllegalStateException("error parsing sam file", e);
		}
	}
	
	
	private static final class SamTransformerVisitor implements SamVisitor{

		private final AssemblyTransformer transformer;
		private final NucleotideSequenceDataStore referenceDataStore;
		private final Map<String,GrowableIntArray> gapOffsetMap;
		
		public SamTransformerVisitor(NucleotideSequenceDataStore referenceDataStore, AssemblyTransformer transformer) throws DataStoreException {
			this.referenceDataStore = referenceDataStore;
			this.transformer = transformer;
			gapOffsetMap = new HashMap<String, GrowableIntArray>(MapUtil.computeMinHashMapSizeWithoutRehashing(referenceDataStore.getNumberOfRecords()));
			
		}

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			
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
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			visitRecord(callback, record);
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			if(record.isPrimary()){
				
				if(record.mapped()){
					String refName = record.getReferenceName();
					try {
						NucleotideSequence referenceSeq = referenceDataStore.get(refName);
					
					Direction dir = record.getDirection();
					Cigar cigar = record.getCigar();
					int rawLength = cigar.getUnpaddedReadLength(ClipType.RAW);
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
					
				
					//if the read is mated SAM doesn't put the /1 or /2 ?
					//what happens in CASAVA 1.8 reads?
					String readName = record.getQueryName();
					EnumSet<SamRecordFlags> flags = record.getFlags();
					//from the SAMv1 spec
					//The leftmost segment has a plus sign and the rightmost has a
					//minus sign. The sign of segments in the middle is undefined. 
					//It is set as 0 for single-segment
					//template or when the information is unavailable.
					if(flags.contains(SamRecordFlags.HAS_MATE_PAIR)){
						if(record.getObservedTemplateLength() >=0){
							//first read
							readName +="/1";
						}else{
							readName +="/2";
						}
						
					}
					//update valid range?
					transformer.aligned(readName, rawSequence, quals, null, null, 
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
		
		@Override
		public void halted() {
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
			if(e.getOp() == CigarOperation.PADDING){
				//remove this many gaps
				builder.delete(new Range.Builder(1)
										.shift(builder.getLength()-1)
										.build());
			}
			else if(e.getOp() ==CigarOperation.DELETION ||e.getOp() == CigarOperation.SKIPPED){
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
