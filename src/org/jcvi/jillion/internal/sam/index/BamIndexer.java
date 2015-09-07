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
package org.jcvi.jillion.internal.sam.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.internal.sam.IndexerCallback;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.ReferenceIndex;

public class BamIndexer implements IndexerCallback{

	private SamRecord currentRecord;
	private long totalNumberOfUnmappedReads=0;
	
	private final SamHeader header;
	private final List<ReferenceIndexBuilder> indexBuilders;
	private ReferenceIndexBuilder currentBuilder;
	private String currentRefName;
	
	public BamIndexer(SamHeader header) {
		
		this.header = header;
		Collection<SamReferenceSequence> referenceSequences = header.getReferenceSequences();
		this.indexBuilders = new ArrayList<ReferenceIndexBuilder>(referenceSequences.size());

		for(SamReferenceSequence refSeq : referenceSequences){
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
