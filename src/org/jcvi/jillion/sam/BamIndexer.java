package org.jcvi.jillion.sam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.index.Bin;
import org.jcvi.jillion.sam.index.ReferenceIndexBuilder;

class BamIndexer implements IndexerCallback{

	private SamRecord currentRecord;
	
	private final SamHeader header;
	private final List<ReferenceIndexBuilder.Builder> indexBuilders;
	private ReferenceIndexBuilder.Builder currentBuilder;
	private String currentRefName;
	
	public BamIndexer(SamHeader header) {
		
		this.header = header;
		Collection<ReferenceSequence> referenceSequences = header.getReferenceSequences();
		this.indexBuilders = new ArrayList<ReferenceIndexBuilder.Builder>(referenceSequences.size());

		for(ReferenceSequence refSeq : referenceSequences){
			indexBuilders.add(new ReferenceIndexBuilder.Builder(refSeq.getLength()));
		}
	}
	
	public void setCurrentRecord(SamRecord record){
		this.currentRecord = record;
	}
	
	public void addRecord(SamRecord record, VirtualFileOffset start, VirtualFileOffset end){
		if(record !=null && record.mapped()){
			String ref = record.getReferenceName();
			if(!ref.equals(currentRefName)){
				int refIndex = header.getReferenceIndexFor(ref);
				currentBuilder = indexBuilders.get(refIndex);
				currentRefName = ref;				
			}
			int readStartOffset = record.getStartPosition() -1;
			int readLength = record.getCigar().getPaddedReadLength(ClipType.SOFT_CLIPPED);
			
			currentBuilder.addAlignment(readStartOffset, readStartOffset + readLength, 
					start, 
					end);
		}
	}
	
	@Override
	public void encodedIndex(long compressedStart, int uncompressedStart,
			long compressedEnd, int uncompressedEnd) {
		
		addRecord(currentRecord, 
				VirtualFileOffset.create(compressedStart, uncompressedStart),
				VirtualFileOffset.create(compressedEnd, uncompressedEnd));

	}

	public List<ReferenceIndexBuilder> createReferenceIndexes(){
		List<ReferenceIndexBuilder> list = new ArrayList<ReferenceIndexBuilder>(indexBuilders.size());
		for(ReferenceIndexBuilder.Builder builder : indexBuilders){
			ReferenceIndexBuilder refIndex = builder.build();
			
			System.out.println(refIndex.getBins().size());
			for(Bin bin : refIndex.getBins()){
				System.out.println(bin);
			}
			list.add(refIndex);
		}
		
		return list;
	}
	
	

}
