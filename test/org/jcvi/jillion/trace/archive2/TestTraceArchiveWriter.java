package org.jcvi.jillion.trace.archive2;

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
import org.jcvi.jillion.trace.archive2.TraceArchiveInfo;
import org.jcvi.jillion.trace.archive2.TraceArchiveRecord;
import org.jcvi.jillion.trace.archive2.TraceArchiveRecordBuilder;
import org.jcvi.jillion.trace.archive2.TraceArchiveWriter;
import org.jcvi.jillion.trace.archive2.TraceInfoField;
import org.jcvi.jillion.trace.archive2.XmlTraceArchiveInfoFactory;
import org.jcvi.jillion.trace.archive2.TraceArchiveWriter.TraceArchiveRecordDataException;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
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