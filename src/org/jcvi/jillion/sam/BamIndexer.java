package org.jcvi.jillion.sam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.ReferenceIndex;
import org.jcvi.jillion.sam.index.ReferenceIndexBuilder;

public class BamIndexer implements IndexerCallback{

	private SamRecord currentRecord;
	private long totalNumberOfUnmappedReads=0;
	
	private final SamHeader header;
	private final List<ReferenceIndexBuilder> indexBuilders;
	private ReferenceIndexBuilder currentBuilder;
	private String currentRefName;
	
	public BamIndexer(SamHeader header) {
		
		this.header = header;
		Collection<ReferenceSequence> referenceSequences = header.getReferenceSequences();
		this.indexBuilders = new ArrayList<ReferenceIndexBuilder>(referenceSequences.size());

		for(ReferenceSequence refSeq : referenceSequences){
			indexBuilders.add(new ReferenceIndexBuilder(refSeq.getLength()));
		}
	}
	
	public void setCurrentRecord(SamRecord record){
		this.currentRecord = record;
	}
	
	public void addRecord(SamRecord record, VirtualFileOffset start, VirtualFileOffset end){
		if(record ==null){
			return;
		}
		if(record.mapped()){
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
		}else{
			totalNumberOfUnmappedReads++;
			//Picard doesn't increment the unmapped
			//read to the current reference so we won't
			//either to be byte for byte compatible.
			/*
			if(currentBuilder !=null){
				//assume we are in the current reference?
				currentBuilder.incrementUnmappedCount();
			}
			*/
		}
	}
	
	@Override
	public void encodedIndex(VirtualFileOffset start, VirtualFileOffset end) {
		
		addRecord(currentRecord, start, end);

	}

	public BamIndex createBamIndex(){
		return new BamIndex(header, createReferenceIndexes(), totalNumberOfUnmappedReads);
	}
	private List<ReferenceIndex> createReferenceIndexes(){
		List<ReferenceIndex> list = new ArrayList<ReferenceIndex>(indexBuilders.size());
		for(ReferenceIndexBuilder builder : indexBuilders){
			ReferenceIndex refIndex = builder.build();
			
			list.add(refIndex);
		}
		
		return list;
	}

	public long getTotalNumberOfUnmappedReads() {
		return totalNumberOfUnmappedReads;
	}
	
	

}
