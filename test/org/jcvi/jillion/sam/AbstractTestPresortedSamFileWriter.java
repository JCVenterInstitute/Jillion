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
