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
package org.jcvi.jillion.sam.index;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

public class BamIndex {

	private final Map<String, Integer> indexOfRefNames;
	
	private final List<ReferenceIndex> indexes;
	
	private final Long totalNumberOfUnmappedReads;
	/**
	 * Create a new {@link BamIndex} instance 
	 * using the given sorted BAM file and corresponding
	 * BAM index file.  The sort
	 * order is assumed to be in {@link org.jcvi.jillion.sam.SortOrder#COORDINATE}
	 * even if the header says otherwise.
	 * Only the header is parsed from the BAM file to get header information.
	 * @param bam the BAM file to parse; file must exist.
	 * @param bai the BAM index file to parse; file must exist.  It is 
	 * assumed that the index corresponds to the the data in the BAM file.
	 * @return a new {@link BamIndex} instance; will never be null.
	 * @throws IOException if there is a problem parsing either file.
	 */
	public static BamIndex createFromFiles(File bam, File bai) throws IOException{
		SamHeaderParser headerParser = new SamHeaderParser();
		SamParserFactory.create(bam).accept(headerParser);
		SamHeader header = headerParser.getHeader();
		
		InputStream in=null;
		try{
			in = new BufferedInputStream(new FileInputStream(bai));
			return IndexUtil.parseIndex(in, header);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);;
		}
	}
	
	private static class SamHeaderParser implements SamVisitor{
		private SamHeader header;
		
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			this.header = header;
			callback.haltParsing();
			
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			//no-op
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			//no-op
		}

		@Override
		public void visitEnd() {
			//no-op
		}

		@Override
		public void halted() {
			//no-op
		}

		public SamHeader getHeader() {
			return header;
		}
		
		
		
	}
	public BamIndex(SamHeader header, List<ReferenceIndex> indexes){
		this(header, indexes, null);
	}
	public BamIndex(SamHeader header, List<ReferenceIndex> indexes, Long totalNumberOfUnmappedReads){
		int refIndex=0;
		Collection<ReferenceSequence> referenceSequences = header.getReferenceSequences();
		indexOfRefNames = new HashMap<String, Integer>(MapUtil.computeMinHashMapSizeWithoutRehashing(referenceSequences.size()));
		
		for(ReferenceSequence ref :referenceSequences){
			indexOfRefNames.put(ref.getName(), refIndex);
			refIndex++;
		}
		
		this.indexes = new ArrayList<ReferenceIndex>(indexes);
		this.totalNumberOfUnmappedReads = totalNumberOfUnmappedReads;
	}
	
	
	public ReferenceIndex getReferenceIndex(int i){
		return indexes.get(i);
	}
	
	public int getNumberOfReferenceIndexes(){
		return indexes.size();
	}
	
	public ReferenceIndex getReferenceIndex(String refName){
		Integer i = indexOfRefNames.get(refName);
		if(i==null){
			return null;
		}
		return indexes.get(i);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexOfRefNames == null) ? 0 : indexOfRefNames.hashCode());
		result = prime * result + ((indexes == null) ? 0 : indexes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BamIndex)) {
			return false;
		}
		BamIndex other = (BamIndex) obj;
		if (indexOfRefNames == null) {
			if (other.indexOfRefNames != null) {
				return false;
			}
		} else if (!indexOfRefNames.equals(other.indexOfRefNames)) {
			return false;
		}
		if (indexes == null) {
			if (other.indexes != null) {
				return false;
			}
		} else if (!indexes.equals(other.indexes)) {
			return false;
		}
		return true;
	}
	
	public Long getTotalNumberOfUnmappedReads() {
		return totalNumberOfUnmappedReads;
	}
	
	
}
