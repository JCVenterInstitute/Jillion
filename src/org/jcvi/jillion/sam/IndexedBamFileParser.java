/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.Bin;
import org.jcvi.jillion.sam.index.Chunk;
import org.jcvi.jillion.sam.index.ReferenceIndex;

class IndexedBamFileParser extends BamFileParser{
	private final BamIndex index;
	private static VirtualFileOffset BEGINING_OF_FILE = new VirtualFileOffset(0L);
	
	public IndexedBamFileParser(File bamFile, File baiFile, SamAttributeValidator validator) throws IOException {
		super(bamFile, validator);
		try(InputStream in = new BufferedInputStream(new FileInputStream(baiFile))){
			index = IndexUtil.parseIndex(in, this.getHeader());
//			System.out.println(index.getNumberOfReferenceIndexes());
//			System.out.println(index.getTotalNumberOfUnmappedReads());
//			System.out.println("\t"+index.getReferenceIndex(0).getNumberOfAlignedReads());
//			System.out.println("\t"+index.getReferenceIndex(0).getNumberOfUnAlignedReads());
//			System.out.println("\t"+index.getReferenceIndex(0).getNumberOfBins());
//			for(int i=0; i<index.getReferenceIndex(0).getNumberOfBins(); i++ ) {
//				Bin bin = index.getReferenceIndex(0).getBins().get(i);
//				System.out.println("\t\t"+ bin);
//				bin.getChunks().forEach(c-> System.out.println("\t\t\t"+ c));
//			}
		}
	}

   

    @Override
	public void parse(SamParserOptions options, SamVisitor visitor) throws IOException {
		if(options.getReferenceName().isEmpty()) {
			 super.parse(options,visitor);
			 return;
		}
		String referenceName = options.getReferenceName().get();
		ReferenceIndex refIndex =index.getReferenceIndex(referenceName);
		if(refIndex ==null){
			throw new IllegalArgumentException("no reference with name '"+ referenceName +"'");
		}
		if(options.getReferenceRanges().isPresent()) {
			List<Range> alignmentRanges = options.getReferenceRanges().get();
			VirtualFileOffset[] start= new VirtualFileOffset[1];
			start[0] = refIndex.getHighestEndOffset();
			
			if(start[0]==null) {
				//nothing matched this range
				visitor.visitEnd();
				return;
			}
			VirtualFileOffset[] end = new VirtualFileOffset[1];
			end[0] = refIndex.getLowestStartOffset();
			
			
			refIndex.findBinsForAlignmentRange(alignmentRanges, (r,b)->{
				for(Chunk chunk : b.getChunks()) {
					if(chunk.getBegin().compareTo(start[0]) < 0) {
						start[0] = chunk.getBegin();
					}
					if(chunk.getEnd().compareTo(end[0]) > 0) {
						end[0] = chunk.getEnd();
					}
				}
			});
			AtomicBoolean keepParsing = new AtomicBoolean(true);
			Range incluiveAlignmentRange = Ranges.createInclusiveRange(alignmentRanges);
			Predicate<SamRecord> recordBinFilter = (record) -> {
				if(!referenceName.equals(record.getReferenceName())){
					return false;
				}
			
				Range readAlignmentRange = record.getAlignmentRange();
				
				if(readAlignmentRange ==null) {
					return false;
				}
//				System.out.println(readAlignmentRange);
//				int bin = SamUtil.computeBinFor(readAlignmentRange);
				/*
				 * int[] readBins = SamUtil.getCandidateOverlappingBins(readAlignmentRange);
				 * boolean found=false; for(int i=0; i< readBins.length; i++) {
				 * if(Arrays.binarySearch(overlappingBins, readBins[i]) <0){ found=true; break;
				 * } } if(!found) { return false; }
				 */
				boolean intersects= incluiveAlignmentRange.intersects(readAlignmentRange);
				
//				boolean intersects= Ranges.intersects(alignmentRanges, readAlignmentRange);
//				if(intersects) {
//					System.out.println(readAlignmentRange);
//				}
//				if(!intersects && readAlignmentRange.startsAfter(incluiveAlignmentRange)){
//					System.out.println("beyond alignment range " + readAlignmentRange);
//					keepParsing.set(false);
//				}
				return intersects;
			};
			if(options.getFilter().isPresent()) {
				recordBinFilter = recordBinFilter.and(options.getFilter().get().asPredicate());
			}
			boolean parsedHeaderAlready=false;
			if(!BEGINING_OF_FILE.equals(start[0])) {
				//parse the header
				try(BgzfInputStream in = BgzfInputStream.create(bamFile)){
					parseHeaderOnly(visitor, in);
				}
				parsedHeaderAlready=true;
			}
			try(BgzfInputStream in = BgzfInputStream.create(bamFile, start[0])){
				//assume anything in this interval matches?
		
				options.getFilter().ifPresent(f->f.begin());
				if(!parsedHeaderAlready) {
					//parse the header
					try(BgzfInputStream in2 = BgzfInputStream.create(bamFile)){
						parseHeaderOnly(visitor, in2);
					}
				}

				this.parseBamRecords(visitor, 
						recordBinFilter,
						(vfs)-> vfs.compareTo(end[0]) <=0,
//						(vfs)-> true,
						in,
						keepParsing,
						options.shouldCreateMementos() ? new BamCallback(keepParsing) :new MementoLessBamCallback(keepParsing),
//						end[0],
								null
						);
				options.getFilter().ifPresent(f->f.end());
			}
		}else {
			VirtualFileOffset start = refIndex.getLowestStartOffset();
			if(start==null) {
				//nothing matched this range
				visitor.visitEnd();
				return;
			}
			VirtualFileOffset end = refIndex.getHighestEndOffset();
			
			Predicate<SamRecord> recordMatchPredicate =(record) ->referenceName.equals(record.getReferenceName());
			if(options.getFilter().isPresent()) {
				recordMatchPredicate = recordMatchPredicate.and(options.getFilter().get().asPredicate());
			}
			Predicate<VirtualFileOffset> endPredicate =(vfs) ->vfs.compareTo(end) <=0;
			try(BgzfInputStream in = BgzfInputStream.create(bamFile, start)){
				options.getFilter().ifPresent(f->f.begin());
				if(BEGINING_OF_FILE.equals(start)){
					this.parseBamFromBeginning(visitor, 
					        options.shouldCreateMementos(),
							recordMatchPredicate,
							endPredicate, in);
				}else{
					//assume anything in this interval matches?
					AtomicBoolean keepParsing = new AtomicBoolean(true);
					this.parseBamRecords(visitor, 
							recordMatchPredicate,
							endPredicate,
							in,
							keepParsing, options.shouldCreateMementos() ? new BamCallback(keepParsing) :new MementoLessBamCallback(keepParsing),
							end);
				}
				options.getFilter().ifPresent(f->f.end());
			}
		}
	}



	

	

}
