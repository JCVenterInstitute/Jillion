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
package org.jcvi.jillion.sam.index;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.AbstractSamVisitor;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
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
		SamParserFactory.create(bam).parse(visitor);
		return visitor.getBamIndex();
	}
	
	private static class BamIndexSamVisitor extends AbstractSamVisitor {
		BamIndexer indexer;
		

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			indexer = new BamIndexer(header);
		}
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			indexer.addRecord(record, start, end);
			
		}
	
		
		

		public BamIndex getBamIndex() {
			return indexer.createBamIndex();
		}

		
	}
}
