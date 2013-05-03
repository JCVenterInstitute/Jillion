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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.easymock.IAnswer;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.jcvi.jillion_experimental.trace.archive2.TraceArchiveWriter.TraceArchiveRecordCallback;
import org.jcvi.jillion_experimental.trace.archive2.TraceArchiveWriter.TraceArchiveRecordDataException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
public class TestTraceArchiveWriter {

	
	private static File outputDir;
	
	private final File rootInputDir;
	private TraceArchiveWriter.TraceArchiveRecordCallback mockCallback;
	
	@BeforeClass
	public static void createOutputDir() throws IOException{
		File tempFile = File.createTempFile("temp", null);
		//now that we have a new empty file
        //we need to delete it and then create it again, but this
        //time as a directory
        if(!tempFile.delete() || !tempFile.mkdir()){
            throw new IOException("Could not create temp directory: " + tempFile.getAbsolutePath());
        }
        outputDir = tempFile;
	}
	
	@AfterClass
	public static void deleteOutputDir() throws IOException{
		IOUtil.recursiveDelete(outputDir);
	}
	
	
	public TestTraceArchiveWriter() throws IOException, TraceArchiveRecordDataException{
		ResourceHelper resources = new ResourceHelper(TestTraceArchiveWriter.class);
		rootInputDir = resources.getFile("files/exampleTraceArchive");
		
		writeTraceArchive();
	}
	
	private void writeTraceArchive() throws IOException, TraceArchiveRecordDataException{
		mockCallback = createMock(TraceArchiveWriter.TraceArchiveRecordCallback.class);
		TraceArchiveWriter writer = new TraceArchiveWriter(outputDir, mockCallback);
		
		File k18TraceFile = new File(rootInputDir, "trace/P030546_K18_JTC_swineorigininfluenza_1064144674928_1064144674997_069_1119369016061.ztr");
		mockCallback.addMetaData(eq("K18"), eq(k18TraceFile), isA(TraceArchiveRecordBuilder.class));
		expectLastCall().andAnswer(new IAnswer<Object>() {

			@Override
			public Object answer() throws Throwable {
				TraceArchiveRecordBuilder builder = (TraceArchiveRecordBuilder)getCurrentArguments()[2];
				builder.put(TraceInfoField.PLATE_ID, "Plate for K18");
				return null;
			}
		});
		
		File i11TraceFile = new File(rootInputDir, "trace/P030548_I11_JTC_swineorigininfluenza_1064144673279_1064144673333_040_1119369014702.ztr");
		mockCallback.addMetaData(eq("I11"), eq(i11TraceFile), isA(TraceArchiveRecordBuilder.class));
		expectLastCall().andAnswer(new IAnswer<Object>() {

			@Override
			public Object answer() throws Throwable {
				TraceArchiveRecordBuilder builder = (TraceArchiveRecordBuilder)getCurrentArguments()[2];
				builder.put(TraceInfoField.PLATE_ID, "Plate for I11");
				return null;
			}
		});
		
		replay(mockCallback);
		writer.addTrace("K18", k18TraceFile);
		writer.addTrace("I11", i11TraceFile);
		writer.close();
	
	}
	
	@Test
	public void  parseTraceInfo() throws IOException, DataStoreException{
		TraceArchiveInfo actualInfo = XmlTraceArchiveInfoFactory.create(new File(outputDir,"TRACEINFO.XML"));
		assertEquals(2, actualInfo.getRecordList().size());
	
		TraceArchiveRecord i11 = actualInfo.get("I11");
		assertEquals("I11", i11.getAttribute(TraceInfoField.TRACE_NAME));
		
		File i11TraceFile = new File(rootInputDir, "trace/P030548_I11_JTC_swineorigininfluenza_1064144673279_1064144673333_040_1119369014702.ztr");
		assertEquals("traces/I11.ztr", i11.getAttribute(TraceInfoField.TRACE_FILE));
		TestUtil.contentsAreEqual(i11TraceFile, new File(outputDir, "trace/I11.ztr"));
		
		ZtrChromatogram i11Chromo = new ZtrChromatogramBuilder(i11TraceFile.getName(), i11TraceFile)
									.build();
		
		File k18FastaFile = new File(rootInputDir, "base/1119369014798.base");
		assertEquals("fasta/I11.fasta", i11.getAttribute(TraceInfoField.BASE_FILE));
		NucleotideSequenceFastaDataStore expectedFastaDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(k18FastaFile).build();
		NucleotideSequenceFastaDataStore actualFastaDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(new File(outputDir, "fasta/I11.fasta")).build();
		assertEquals(expectedFastaDataStore.get("1119369014798").getSequence(), actualFastaDataStore.get("I11").getSequence());
	
		assertEquals("chromotogram bases don't match fasta", i11Chromo.getNucleotideSequence(), actualFastaDataStore.get("I11").getSequence());
	}
	
	
}
