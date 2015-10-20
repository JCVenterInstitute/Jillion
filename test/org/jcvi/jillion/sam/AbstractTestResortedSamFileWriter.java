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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public abstract class AbstractTestResortedSamFileWriter extends AbstractTestSamWriter{

	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private final String extension;
	
	
	public AbstractTestResortedSamFileWriter(String extension) {
		this.extension = extension;
	}
	
	@Test
	public void coordinateSort() throws IOException{
		writeShuffledRecordsAndAssertWrittenInSortedOrder(SortOrder.COORDINATE);
		
	}
	@Test
	public void queryNameSort() throws IOException{
		writeShuffledRecordsAndAssertWrittenInSortedOrder(SortOrder.QUERY_NAME);
		
	}

	private void writeShuffledRecordsAndAssertWrittenInSortedOrder(
			SortOrder sortOrder) throws IOException {
		File f = createOutputSamOrBamFile();
		SamWriter writer = new SamFileWriterBuilder(f, getHeader())
									.setTempRootDir(tempDir.getRoot())
									.reSortBy(sortOrder)
									.build();
		List<SamRecord> unsortedRecords = getShuffledRecords();
		
		List<SamRecord> expectedRecords = createSortedList(unsortedRecords, sortOrder, getHeader());
		
		writeAllRecords(writer, unsortedRecords);
		writer.close();
		orderOfRecordsMatchesExactly(f, expectedRecords, sortOrder);
	}

	private List<SamRecord> getShuffledRecords() {
		List<SamRecord> unsortedRecords = getRecords();
		Collections.shuffle(unsortedRecords);
		return unsortedRecords;
	}
	
	private List<SamRecord> createSortedList(List<SamRecord> unsortedRecords,
			SortOrder sortOrder, SamHeader header) {
		List<SamRecord> list = new ArrayList<>(unsortedRecords);
		Collections.sort(list, sortOrder.createComparator(getHeader()));
		return list;
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
