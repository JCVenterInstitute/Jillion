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
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code PaddedSamTransformationService}
 * is a class that can parse a <em>padded</em>SAM file
 * was produced from a denovo assembler
 * and contains the padded read alignments
 * to a padded reference/consensus
 * and call the appropriate methods
 * on a given {@link AssemblyTransformer}
 * so the transformer can get assembly and alignment 
 * information from the padded SAM file without knowing
 * anything about how SAM files are formatted.
 * 
 * @author dkatzel
 *
 */
public final class PaddedSamTransformationService implements AssemblyTransformationService{

	
	private final SamParser parser;
	
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given padded SAM encoded file  which
	 * should already include the gapped reference sequences
	 * inside it.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * 
	 * @throws IOException if there is a problem parsing the input file.
	 * @throws NullPointerException if the file is null.
	 */
	public PaddedSamTransformationService(File samFile) throws IOException {
		
		parser = SamParserFactory.create(samFile);
		
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
			SamTransformerVisitor visitor = new SamTransformerVisitor(transformer);
			parser.accept(visitor);
		} catch (Exception e) {
			throw new IllegalStateException("error parsing sam file", e);
		}
	}
	
	
	private static final class SamTransformerVisitor implements SamVisitor{

		private static final Set<SamRecordFlags> FILTERED_AND_UNMAPPED = SamRecordFlags.parseFlags(516);
		private final AssemblyTransformer transformer;
		private Map<String,GrowableIntArray> gapOffsetMap;
		private Map<String, NucleotideSequence> paddedReferenceSequences;
		private Set<String> referenceNames;
		
		
		public SamTransformerVisitor(AssemblyTransformer transformer) throws DataStoreException {
			this.transformer = transformer;
		
		}

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			
			Collection<ReferenceSequence> referenceSequences = header.getReferenceSequences();
			int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(referenceSequences.size());
			referenceNames = new HashSet<>(capacity);
			paddedReferenceSequences = new HashMap<>(capacity);
			gapOffsetMap = new HashMap<>(capacity);
			for(ReferenceSequence refSeq : referenceSequences){
				referenceNames.add(refSeq.getName());
			}
			
		}
			
		

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			visitRecord(callback, record);
		}

		private boolean isReference(SamRecord record){
			//according to the SAMv1 spec
			//padded references should have
			//an identical RNAME, POS set to 1 and FLAG to 516 (filtered and unmapped)
			return referenceNames.contains(record.getQueryName()) 
					&& record.getStartPosition() == 1
					&& record.getFlags().equals(FILTERED_AND_UNMAPPED);
				
		}
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			
			if(record.isPrimary()){
				
				if(record.mapped()){
					String refName = record.getReferenceName();
					
					NucleotideSequence referenceSeq = paddedReferenceSequences.get(refName);
					if(referenceSeq ==null){
						throw new IllegalStateException("error padded reference sequence not defined in sam/bam file : " + refName);
					}
					Direction dir = record.getDirection();
					Cigar cigar = record.getCigar();
					int rawLength = cigar.getUnpaddedReadLength(ClipType.RAW);
					Range validRange;
					//padded sams use gapped offset so don't have to convert
					int gappedStartOffset = record.getStartPosition()-1;
					
					
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
					System.out.println(record.getQueryName());
					NucleotideSequence gappedReadSequence =removeHardClipsFrom(cigar).toGappedTrimmedSequence(record.getSequence());
					
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
					
				}else if(isReference(record)){
					//add padded sequence to our map of references
					String name = record.getQueryName();
					Cigar cigar = record.getCigar();
					NucleotideSequence gappedSequence = cigar.toGappedTrimmedSequence(record.getSequence());
					paddedReferenceSequences.put(name, gappedSequence);
					gapOffsetMap.put(name, new GrowableIntArray(gappedSequence.getGapOffsets()));
					
					transformer.referenceOrConsensus(name, gappedSequence);
				}else{
					transformer.notAligned(record.getQueryName(), record.getSequence(), record.getQualities(), null, null);
					
				}
			}
				
		}

		private Cigar removeHardClipsFrom(Cigar cigar) {
			return new Cigar.Builder(cigar)
					.removeHardClips()
					.build();
		}

		@Override
		public void visitEnd() {
			transformer.endAssembly();			
		}
		
		@Override
		public void halted() {
			transformer.endAssembly();		
		}

	
	
}
}
