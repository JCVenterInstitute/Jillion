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
	private ReferenceIndex.Builder currentBuilder;
	private String currentRefName;
	
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
			if(!ref.equals(currentRefName)){
				int refIndex = header.getReferenceIndexFor(ref);
				currentBuilder = indexBuilders.get(refIndex);
				currentRefName = ref;				
			}
							
			int readStartOffset = currentRecord.getStartPosition() -1;
			int readLength = currentRecord.getCigar().getPaddedReadLength(ClipType.SOFT_CLIPPED);
			
			currentBuilder.addAlignment(readStartOffset, readStartOffset + readLength, 
					VirtualFileOffset.create(compressedStart, uncompressedStart), 
					VirtualFileOffset.create(compressedEnd, uncompressedEnd));
		
		}
	}

	public List<ReferenceIndex> createReferenceIndexes(){
		List<ReferenceIndex> list = new ArrayList<ReferenceIndex>(indexBuilders.size());
		for(ReferenceIndex.Builder builder : indexBuilders){
			ReferenceIndex refIndex = builder.build();
			
			System.out.println(refIndex.getBins().size());
			for(Bin bin : refIndex.getBins()){
				System.out.println(bin);
			}
			list.add(refIndex);
		}
		
		return list;
	}
	
	

}
