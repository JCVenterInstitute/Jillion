/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.trace.archive2;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestXmlTraceInfoFactory {

	private final TraceArchiveInfo sut;
	private final File rootDir;
	public TestXmlTraceInfoFactory() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestXmlTraceInfoFactory.class);
		sut = XmlTraceArchiveInfoFactory.create(resources.getFile("files/exampleTraceArchive/TRACEINFO.xml"));
		rootDir = resources.getFile("files/exampleTraceArchive");
	}
	
	@Test
	public void numRecords(){
		assertEquals(4,sut.getRecordList().size());
	}
	
	@Test
	public void fastaRecordsExist(){
		for(TraceArchiveRecord record : sut.getRecordList()){
			assertTrue("missing seq fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.BASE_FILE))
						.exists());
			assertTrue("missing qual fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.QUAL_FILE))
						.exists());
			assertTrue("missing pos fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.PEAK_FILE))
						.exists());
			assertTrue("missing pos fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.PEAK_FILE))
						.exists());
		}
	}
}
