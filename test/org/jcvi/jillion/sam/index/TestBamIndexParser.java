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

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Test;

public class TestBamIndexParser {

	private static final boolean IGNORE_METADATA = true;
	ResourceHelper resources = new ResourceHelper(TestBamIndexParser.class);
	
	@Test
	public void parsedBaiMatchesRecordsInBam() throws IOException{
		File bamfile = resources.getFile("index_test.bam");
		File expectedBaiFile = resources.getFile("index_test.bam.bai");

			
		BamIndex expectedIndex = BamIndex.createFromFiles(bamfile, expectedBaiFile);
		
		BamIndex actualIndex = createIndexFromBam(bamfile);
		BamIndexTestUtil.assertIndexesEqual(expectedIndex, actualIndex, IGNORE_METADATA);
	
		
		
	}


	private BamIndex createIndexFromBam(File bam) throws IOException {
		BamIndexSamVisitor visitor = new BamIndexSamVisitor();
		SamParserFactory.create(bam).accept(visitor);
		return visitor.getBamIndex();
	}
	
	private static class BamIndexSamVisitor implements SamVisitor {
		BamIndexer indexer;
		SamHeader header;
		
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			indexer.addRecord(record, start, end);
			
		}
		
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			indexer = new BamIndexer(header);
			this.header = header;
		}
		
		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void halted() {
			// TODO Auto-generated method stub
			
		}

		public BamIndex getBamIndex() {
			return indexer.createBamIndex();
		}

		public SamHeader getHeader() {
			return header;
		}
		
		
	}
}
