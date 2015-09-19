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
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public abstract class AbstractTestPresortedSamFileWriter extends AbstractTestSamWriter{

	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private final String extension;
	
	
	public AbstractTestPresortedSamFileWriter(String extension) {
		this.extension = extension;
	}
	@Test
	public void outputFileMatchesExactly() throws IOException{
		File f = createOutputSamOrBamFile();
		
		SamWriter writer = new SamFileWriterBuilder(f, getHeader())
									.setTempRootDir(tempDir.getRoot())
									.build();
		List<SamRecord> expectedRecords = getRecords();
		
		writeAllRecords(writer, expectedRecords);
		writer.close();
		orderOfRecordsMatchesExactly(f, expectedRecords);
		
	}
	@Test
	public void settingSortOrderUnknownDoesNotAlterWriteOrder() throws IOException{
		File f = createOutputSamOrBamFile();
		
		SamWriter writer = new SamFileWriterBuilder(f, getHeader())
									.setTempRootDir(tempDir.getRoot())
									.reSortBy(SortOrder.UNKNOWN)
									.build();
		List<SamRecord> expectedRecords = getRecords();
		
		writeAllRecords(writer, expectedRecords);
		writer.close();
		orderOfRecordsMatchesExactly(f, expectedRecords, SortOrder.UNKNOWN);
		
	}
	
	@Test
	public void settingSortOrderUnSortedDoesNotAlterWriteOrder() throws IOException{
		File f = createOutputSamOrBamFile();
		
		SamWriter writer = new SamFileWriterBuilder(f, getHeader())
									.setTempRootDir(tempDir.getRoot())
									.reSortBy(SortOrder.UNSORTED)
									.build();
		List<SamRecord> expectedRecords = getRecords();
		
		writeAllRecords(writer, expectedRecords);
		writer.close();
		orderOfRecordsMatchesExactly(f, expectedRecords, SortOrder.UNSORTED);
		
	}

	private void writeAllRecords(SamWriter writer,
			List<SamRecord> expectedRecords) throws IOException {
		for(SamRecord r : expectedRecords){
			writer.writeRecord(r);
		}
	}

	private File createOutputSamOrBamFile() throws IOException {
		return tempDir.newFile("out"+extension);
	}
	
}
