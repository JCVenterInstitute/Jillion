/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.internal.sam.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.internal.sam.IndexerCallback;
import org.jcvi.jillion.sam.SamRecordI;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.ReferenceIndex;

public class BamIndexer implements IndexerCallback{

	private SamRecordI currentRecord;
	private long totalNumberOfUnmappedReads=0;
	
	private final SamHeader header;
	private final List<ReferenceIndexBuilder> indexBuilders;
	private ReferenceIndexBuilder currentBuilder;
	private String currentRefName;
	private Map<String, Integer> refSeqIndexMap = new HashMap<String, Integer>();
	public BamIndexer(SamHeader header) {
		
		this.header = header;
		Collection<SamReferenceSequence> referenceSequences = header.getReferenceSequences();
		this.indexBuilders = new ArrayList<ReferenceIndexBuilder>(referenceSequences.size());
		int i=0;
		for(SamReferenceSequence refSeq : referenceSequences){
			indexBuilders.add(new ReferenceIndexBuilder(refSeq.getLength()));
			refSeqIndexMap.put(refSeq.getName(), Integer.valueOf(i));
			i++;
		}
	}
	
	public void setCurrentRecord(SamRecordI record){
		this.currentRecord = record;
	}
	
	public void addRecord(SamRecordI record, VirtualFileOffset start, VirtualFileOffset end){
		if(record ==null){
			return;
		}
		if(record.mapped()){
			String ref = record.getReferenceName();
			if(!ref.equals(currentRefName)){				
				int refIndex = refSeqIndexMap.get(ref);
				currentBuilder = indexBuilders.get(refIndex);
				currentRefName = ref;				
			}
			int readStartOffset = record.getStartPosition() -1;
			int readLength = record.getCigar().getNumberOfReferenceBasesAligned();
			
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
