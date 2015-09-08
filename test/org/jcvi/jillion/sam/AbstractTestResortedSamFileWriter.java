/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
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
		List<SamRecord> list = new ArrayList<SamRecord>(unsortedRecords);
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
