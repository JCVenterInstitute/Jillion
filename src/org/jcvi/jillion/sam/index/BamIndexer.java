package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.sam.BgzfOutputStream;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

public class BamIndexer implements BgzfOutputStream.IndexerCallback{

	private SamRecord currentRecord;
	
	private final SamHeader header;
	private List<ReferenceIndex.Builder> indexBuilders;
	
	
	public BamIndexer(SamHeader header) {
		
		this.header = header;
		Collection<ReferenceSequence> referenceSequences = header.getReferenceSequences();
		this.indexBuilders = new ArrayList<ReferenceIndex.Builder>(referenceSequences.size());

		for(ReferenceSequence refSeq : referenceSequences){
			indexBuilders.add(new ReferenceIndex.Builder(refSeq.getLength()));
		}
	}
	
	public void setCurrentRecord(SamRecord record){
		this.currentRecord = record;
	}
	
	@Override
	public void encodedIndex(long compressedStart, int uncompressedStart,
			long compressedEnd, int uncompressedEnd) {
		if(currentRecord !=null && currentRecord.mapped()){
			String ref = currentRecord.getReferenceName();
			int readStartOffset = currentRecord.getStartPosition() -1;
			int readLength = currentRecord.getCigar().getPaddedReadLength(ClipType.SOFT_CLIPPED);
			int refIndex = header.getReferenceIndexFor(ref);
			if(refIndex >=0 ){
				indexBuilders.get(refIndex).addAlignment(readStartOffset, readStartOffset + readLength -1, 
						VirtualFileOffset.create(compressedStart, uncompressedStart), 
						VirtualFileOffset.create(compressedEnd, uncompressedEnd));
			}
		}
	}

	public List<ReferenceIndex> createReferenceIndexes(){
		List<ReferenceIndex> list = new ArrayList<ReferenceIndex>(indexBuilders.size());
		for(ReferenceIndex.Builder builder : indexBuilders){
			list.add(builder.build());
		}
		
		return list;
	}
	
	

}
