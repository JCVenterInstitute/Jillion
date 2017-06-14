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
package org.jcvi.jillion.experimental.trace.archive2;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.easymock.IAnswer;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.experimental.trace.archive2.TraceArchiveWriter.TraceArchiveRecordDataException;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestTraceArchiveWriter {

	
	private final File rootInputDir;
	private TraceArchiveWriter.TraceArchiveRecordCallback mockCallback;
	
	@Rule
	public TemporaryFolder outputDir = new TemporaryFolder();
	
	
	
	public TestTraceArchiveWriter() throws IOException, TraceArchiveRecordDataException{
		ResourceHelper resources = new ResourceHelper(TestTraceArchiveWriter.class);
		rootInputDir = resources.getFile("files/exampleTraceArchive");
		
		
	}
	
	private void writeTraceArchive() throws IOException, TraceArchiveRecordDataException{
		mockCallback = createMock(TraceArchiveWriter.TraceArchiveRecordCallback.class);
		try(TraceArchiveWriter writer = new TraceArchiveWriter(outputDir.getRoot(), mockCallback, "volumeName", new Date(), "volumeName")){
		
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
		
		assertEquals(2, writer.getNumberOfTracesWritten());
		}
	
	}
	
	@Test
	public void  parseTraceInfo() throws IOException, DataStoreException, TraceArchiveRecordDataException{
	    writeTraceArchive();
	    
	    
		TraceArchiveInfo actualInfo = XmlTraceArchiveInfoFactory.create(new File(outputDir.getRoot(),"TRACEINFO.XML"));
		assertEquals(2, actualInfo.getRecordList().size());
	
		TraceArchiveRecord i11 = actualInfo.get("I11");
		assertEquals("I11", i11.getAttribute(TraceInfoField.TRACE_NAME));
		
		File i11TraceFile = new File(rootInputDir, "trace/P030548_I11_JTC_swineorigininfluenza_1064144673279_1064144673333_040_1119369014702.ztr");
		assertEquals("./traces/I11.ztr", i11.getAttribute(TraceInfoField.TRACE_FILE));
		TestUtil.assertContentsAreEqual(i11TraceFile, new File(outputDir.getRoot(), "traces/I11.ztr"));
		
		ZtrChromatogram i11Chromo = new ZtrChromatogramBuilder(i11TraceFile.getName(), i11TraceFile)
									.build();
		
		File k18FastaFile = new File(rootInputDir, "base/1119369014798.base");
		assertEquals("./fasta/I11.fasta", i11.getAttribute(TraceInfoField.BASE_FILE));
		
		
		try(    NucleotideSequenceDataStore expectedFastaDataStore = NucleotideFastaFileDataStore.fromFile(k18FastaFile)
		                                                                                .adapt(NucleotideSequenceDataStore.class, NucleotideFastaRecord::getSequence);
		        NucleotideSequenceDataStore actualFastaDataStore = NucleotideFastaFileDataStore.fromFile(new File(outputDir.getRoot(), "fasta/I11.fasta"))
		                                                                                .adapt(NucleotideSequenceDataStore.class, NucleotideFastaRecord::getSequence);
		                ){
    		assertEquals(expectedFastaDataStore.get("1119369014798"), actualFastaDataStore.get("I11"));
    	
    		assertEquals("chromotogram bases don't match fasta", i11Chromo.getNucleotideSequence(), actualFastaDataStore.get("I11"));
		}
	}
	
	
}
