package org.jcvi.jillion.sam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.ReferenceIndex;

class IndexedBamFileParser extends BamFileParser{
	private final BamIndex index;
	private static VirtualFileOffset BEGINING_OF_FILE = new VirtualFileOffset(0L);
	
	public IndexedBamFileParser(File bamFile, File baiFile, SamAttributeValidator validator) throws IOException {
		super(bamFile, validator);
		try(InputStream in = new BufferedInputStream(new FileInputStream(baiFile))){
			index = IndexUtil.parseIndex(in, this.getHeader());
		}
	}

	@Override
	public void accept(String referenceName, SamVisitor visitor) throws IOException {
		Objects.requireNonNull(referenceName);
		Objects.requireNonNull(visitor);
		
		Integer indexOffset = index.getReferenceIndexOffset(referenceName);
		
		if(indexOffset ==null){
			throw new IllegalArgumentException("no reference with name '"+ referenceName +"'");
		}
		ReferenceIndex refIndex =index.getReferenceIndex(indexOffset);
		
		VirtualFileOffset start = refIndex.getLowestStartOffset();
		
		VirtualFileOffset end = refIndex.getHighestEndOffset();
		
		Predicate<SamRecord> recordMatchPredicate =(record) ->referenceName.equals(record.getReferenceName());
		
		Predicate<VirtualFileOffset> endPredicate =(vfs) ->vfs.compareTo(end) <0;
		
		
		
		try(BgzfInputStream in = BgzfInputStream.create(bamFile, start)){
			if(BEGINING_OF_FILE.equals(start)){
				this.parseBamFromBeginning(visitor, 
						recordMatchPredicate,
						endPredicate,
						in);
			}else{
				//assume anything in this interval matches?
				this.parseBamRecords(visitor, 
						recordMatchPredicate,
						endPredicate,
						in);
			}
		}
	}

	@Override
	public void accept(String referenceName, Range alignmentRange, SamVisitor visitor) throws IOException {
		Objects.requireNonNull(referenceName);
		Objects.requireNonNull(visitor);
		
		ReferenceIndex refIndex =index.getReferenceIndex(referenceName);
		if(refIndex ==null){
			throw new IllegalArgumentException("no reference with name '"+ referenceName +"'");
		}
		//only let things pass that match some bin
		int[] overlappingBins = SamUtil.getCandidateOverlappingBins(alignmentRange);
		
		//TODO can probably do better filtering by specific bins...
		VirtualFileOffset start = refIndex.getLowestStartOffset();
		VirtualFileOffset end = refIndex.getHighestEndOffset();
		
		Predicate<SamRecord> recordBinFilter = (record) -> {
			if(!referenceName.equals(record.getReferenceName())){
				return false;
			}
		
			Range readAlignmentRange = record.getAlignmentRange();
			int bin = SamUtil.computeBinFor(readAlignmentRange);
			if(!(Arrays.binarySearch(overlappingBins, bin) >=0)){
				return false;
			}
			return readAlignmentRange.isSubRangeOf(alignmentRange);
		};
		
		try(BgzfInputStream in = BgzfInputStream.create(bamFile, start)){
			//assume anything in this interval matches?
			this.parseBamRecords(visitor, 
					recordBinFilter,
					(vfs)-> vfs.compareTo(end) <=0,
					in);
		}
	}

	

}
