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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.AbstractSamVisitor;
import org.jcvi.jillion.sam.ReplayableMockSamVisitor;
import org.jcvi.jillion.sam.SamFileWriterBuilder;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestBamIndexWriter {
	ResourceHelper resources = new ResourceHelper(TestBamIndexWriter.class);
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private File bamFile, expectedBaiFile;
	
	@Before
	public void setup() throws IOException{
		bamFile = resources.getFile("index_test.bam");
		expectedBaiFile = resources.getFile("index_test.bam.bai");
	}
	
	@Test(expected = NullPointerException.class)
	public void nullInputFileShouldThrowNPE() throws IOException{
		
		new BamIndexFileWriterBuilder(null, new File("a.bai"));
	}
	@Test(expected = NullPointerException.class)
	public void nullOutputFileShouldThrowNPE() throws IOException{
		
		new BamIndexFileWriterBuilder(new File("a.bam"), null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void wrongExtensionForOutputFileShouldThrowException() throws IOException{
		
		new BamIndexFileWriterBuilder(new File("a.bam"), new File("notABai"));
	}
	@Test(expected = IllegalArgumentException.class)
	public void wrongExtensionForInputFileShouldThrowException() throws IOException{
		
		new BamIndexFileWriterBuilder(new File("notABam"), new File("a.bai"));
	}
	
	@Test(expected = FileNotFoundException.class)
	public void inputFileDoesNotExistShouldThrowFileNotFoundException() throws IOException{
		File nonExistentFile = new File(tmpDir.getRoot(), "dne.bam");
		new BamIndexFileWriterBuilder(nonExistentFile, new File("a.bai"));
	}
	
	@Test
	public void writterWithMetaDataMatchesPicard() throws IOException{
		
		
		File actualBaiFile = tmpDir.newFile("actual.bai");
		
		File out = new BamIndexFileWriterBuilder(bamFile, actualBaiFile)
					.includeMetaData(true)
					.build();
		
		assertEquals(actualBaiFile, out);
		
		TestUtil.assertContentsAreEqual(expectedBaiFile, actualBaiFile);
	}
	
	private void assertIndexesAreSimilar(BamIndex expected, BamIndex actual){
	    //because the indexes are based on the same data
	    //but the input bam may be compressed differently, the offsets will be different
	    //so just check that the number of bins and which bins are printed and intervals match etc.
	    
	    assertEquals(expected.getNumberOfReferenceIndexes(), actual.getNumberOfReferenceIndexes());
	    assertEquals(expected.getTotalNumberOfUnmappedReads(), actual.getTotalNumberOfUnmappedReads());
	    
	    for(int i=0; i< expected.getNumberOfReferenceIndexes(); i++){
	        ReferenceIndex expectedIndex = expected.getReferenceIndex(i);
	        ReferenceIndex actualIndex = actual.getReferenceIndex(i);
	        
	        assertEquals(expectedIndex.getNumberOfBins(), actualIndex.getNumberOfBins());
	        assertEquals(expectedIndex.getNumberOfAlignedReads(), actualIndex.getNumberOfAlignedReads());
	        assertEquals(expectedIndex.getNumberOfUnAlignedReads(), actualIndex.getNumberOfUnAlignedReads());
	        List<Bin> expectedBins = expectedIndex.getBins();
	        List<Bin> actualBins = actualIndex.getBins();
	        
	        assertEquals(expectedBins.size(), actualBins.size());
	        for(int j = 0; j<expectedBins.size(); j++){
	            Bin expectedBin = expectedBins.get(j);
	            Bin actualBin = actualBins.get(j);
	            
	            assertEquals(expectedBin.getBinNumber(), actualBin.getBinNumber());
	        }
	        //I don't think we can trust intervals because it's based on fileoffset?
                
	    }
	}
	
	@Test
	public void SamBuilderWithBaiWriterAndMetaDataShouldMatchPicardData() throws IOException{
		
		
		SamHeader originalHeader = parseSamHeaderFrom(bamFile);
		File outputFile = tmpDir.newFile("copy.bam");
		try(SamWriter writer = new SamFileWriterBuilder(outputFile, originalHeader)
				.createBamIndex(true, true)
				.build();
				){
			writeAllRecords(bamFile, writer);
		}
		
		assertSamFilesMatch(bamFile, outputFile);
		
		
		File actualBaiFile = new File(tmpDir.getRoot(),"copy.bam.bai");
		
		
		BamIndex actualIndex = IndexUtil.parseIndex(new FileInputStream(actualBaiFile), originalHeader);
		BamIndex expectedIndex = IndexUtil.parseIndex(new FileInputStream(expectedBaiFile), originalHeader);
		assertIndexesAreSimilar(expectedIndex, actualIndex);

	}

	
	private void assertSamFilesMatch(File expected, File actual) throws IOException{
		assertSamFilesMatch(true,  expected, actual);
	}
	private void assertSamFilesMatch(boolean checkHeader, File expected, File actual) throws IOException{
		SamParser expectedParser = SamParserFactory.create(expected);
		SamParser actualParser = SamParserFactory.create(actual);
		
		ReplayableMockSamVisitor matcher = new ReplayableMockSamVisitor(checkHeader);
		expectedParser.parse(matcher);
		actualParser.parse(matcher);
		
	}
	
	@Test
	public void writingNonCoordinateSortedBamShouldThrowException() throws IOException{

		SamHeader originalHeader = parseSamHeaderFrom(bamFile);
		
		File incorrectlySortedFile = tmpDir.newFile("wrongSort.bam");
		File actualBaiFile = tmpDir.newFile("actual.bai");
		for(SortOrder incorrectSortOrder : Arrays.asList(SortOrder.QUERY_NAME, SortOrder.UNKNOWN, SortOrder.UNSORTED)){
			SamWriter writer = new SamFileWriterBuilder(incorrectlySortedFile, originalHeader)
														.forceHeaderSortOrder(incorrectSortOrder)
														.build();
			
			writeAllRecords(bamFile, writer);
			verifyIndexWriterThrowsException(incorrectlySortedFile,	actualBaiFile, incorrectSortOrder);
		}
	}
	@Test
	public void assumeSortedFlagSetWillWriteIndexEvenIfHeaderSaysOtherwise() throws IOException{

		SamHeader originalHeader = parseSamHeaderFrom(bamFile);
		
		File incorrectlySortedFile = tmpDir.newFile("wrongSort.bam");

		
			
			//the input bam file is from an old version of picard
			//which didn't compress as well (at all?)
			//so since we are writing out bams using compression,
			//also the different header sort order cause the header
			//length to be different so we have to compensate for that as well.
			//(see createHeader() method for more details)
			//Therefore, we need to write out a new file so that our
			//indexes will match.		
			//File newBam = reWriteBam(bamFile, createHeader(originalHeader, SortOrder.COORDINATE));
			//File expectedBai = createIndex(newBam);
			
			SamWriter writer = new SamFileWriterBuilder(incorrectlySortedFile, createHeader(originalHeader, SortOrder.QUERY_NAME))
														.forceHeaderSortOrder(SortOrder.COORDINATE)
														.build();

			
			writeAllRecords(bamFile, writer);
			assertSamFilesMatch(false, bamFile, incorrectlySortedFile);
		
	}

	private SamHeader createHeader(SamHeader originalHeader, SortOrder order){
		SamHeaderBuilder builder = new SamHeaderBuilder(originalHeader);
		//because the index uses byte offsets,
		//the different sort orders will cause the header to be different
		//byte lengths which will throw off the index offsets by a few bytes.
		//To get around this, we will add a comment String to the header
		//that is a variable amount depending on the sort order so that
		//all the records start at the same byte offset.
		//since coordinate sort is the largest word, we will padd compared to that.
		if(order ==SortOrder.COORDINATE){
			builder.addComment("");
		}else{
			int padding = SortOrder.COORDINATE.getEncodedName().length() -order.getEncodedName().length();
			StringBuilder paddedString = new StringBuilder(padding);
			for(int i=0; i<padding; i++){
				paddedString.append("*");
			}
			builder.addComment(paddedString.toString());
		}
		return builder.build();
		
	}
	

	private void verifyIndexWriterThrowsException(File incorrectlySortedFile,
			File actualBaiFile, SortOrder sortOrder) throws IOException {
		try{
			new BamIndexFileWriterBuilder(incorrectlySortedFile, actualBaiFile)
					.build();
			fail("should throw IllegalStateException when " + sortOrder);
		}catch(IllegalStateException expected){
			//expected
		}
	}
	
	private void writeAllRecords(File bamFile, final SamWriter writer) throws IOException {
		try{
			SamParserFactory.create(bamFile)
			.parse(new AbstractSamVisitor() {
				
				@Override
				public void visitRecord(SamVisitorCallback callback, SamRecord record,
						VirtualFileOffset start, VirtualFileOffset end) {
					try {
						writer.writeRecord(record);
					} catch (IOException e) {
						throw new UncheckedIOException("error writing out record",e);
					}
					
				}
				
			
			});
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}
		
	}

	private SamHeader parseSamHeaderFrom(File samOrBam) throws IOException{
		//create final array so we can reference it in our 
		//anonymous class
		final SamHeader[] singleHeaderBuilder = new SamHeader[1];
		SamParserFactory.create(samOrBam)
						.parse(new AbstractSamVisitor() {
							
							
							@Override
							public void visitHeader(SamVisitorCallback callback, SamHeader header) {
								singleHeaderBuilder[0] = header;
								callback.haltParsing();
							}
							
						});
		
		return singleHeaderBuilder[0];
	}
}
