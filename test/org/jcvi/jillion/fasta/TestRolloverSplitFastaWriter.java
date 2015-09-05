package org.jcvi.jillion.fasta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.SplitFastaWriter.FastaRecordWriterFactory;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class TestRolloverSplitFastaWriter {

	
	private static FastaRecordWriterFactory<NucleotideFastaWriter> IGNORE = i-> new NucleotideFastaWriterBuilder(new ByteArrayOutputStream()).build();
	private static FilenameFilter FASTA_FILE_FILTER = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".fasta");
		}
	};
	
	private static Comparator<File> FILENAME_COMPARATOR = new Comparator<File>() {

		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private NucleotideFastaDataStore datastore;
	
	@Before
	public void setupDataStore() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestRolloverSplitFastaWriter.class);
		
		datastore = new NucleotideFastaFileDataStoreBuilder(helper.getFile("nt/files/19150.fasta"))
							.build();
	}
	
	@Test(expected = NullPointerException.class)
	public void nullSupplierShouldThrowException() throws IOException{
		SplitFastaWriter.rollover(NucleotideFastaWriter.class, 5, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void zeroRecordsPerFileShouldThrowException() throws IOException{
		SplitFastaWriter.rollover(NucleotideFastaWriter.class, 0, IGNORE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeRecordsPerFileShouldThrowException() throws IOException{
		SplitFastaWriter.rollover(NucleotideFastaWriter.class, -1, IGNORE);
	}
	
	@Test
	public void noRecordsWrittenShouldStillCreateEmptyFasta() throws IOException{
		File actualDir = tmpDir.newFolder("actual");
		try(NucleotideFastaWriter sut = createRolloverWriter(1, i -> new NucleotideFastaWriterBuilder(new File(actualDir, i +".fasta"))
													.build())){
		}
		
		File expected = new File(actualDir, "1.fasta");
		assertFalse(expected.exists());
	}
	
	@Test
	public void tryingtoWriteRecordAfterCloseShouldThrowException() throws IOException{
		NucleotideFastaWriter sut = createRolloverWriter(1, IGNORE);
		sut.close();
		expectedException.expect(IOException.class);
		expectedException.expectMessage("already closed");
		sut.write("foo", NucleotideSequenceTestUtil.create("ACGT"));
		
	}
	@Test
        public void callingCloseAgainIsNoOp() throws IOException{
                NucleotideFastaWriter sut = createRolloverWriter(1, IGNORE);
                sut.close();
                sut.close();
                
        }
	@Test
	public void createOneFilePerRecord() throws IOException, DataStoreException{
		File actualDir = tmpDir.newFolder("actual");
		File expectedDir = tmpDir.newFolder("expected");
		
		
		try(NucleotideFastaWriter sut = createRolloverWriter(1, i -> new NucleotideFastaWriterBuilder(new File(actualDir, i +".fasta"))
																			.build());
			StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();	
			){
			
			for(int i=1; i<= 9;i++){
				NucleotideFastaRecord fastaRecord = iter.next();
				sut.write(fastaRecord);
				
				try(NucleotideFastaWriter expected = new NucleotideFastaWriterBuilder(new File(expectedDir, i +".fasta"))
															.build()){
					expected.write(fastaRecord);
				}
			}
		}
		fastaFilesMatch(expectedDir, actualDir);
		
		
		
	}
	
	@Test
	public void createOneFileEvery5Records() throws IOException, DataStoreException{
		File actualDir = tmpDir.newFolder("actual");
		File expectedDir = tmpDir.newFolder("expected");
		
		
		try(NucleotideFastaWriter sut = createRolloverWriter(5, i -> new NucleotideFastaWriterBuilder(new File(actualDir, i +".fasta"))
																			.build());
			StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();	
			){
			try(NucleotideFastaWriter expected = new NucleotideFastaWriterBuilder(new File(expectedDir, "1.fasta"))
															.build()){
				for(int i=1; i<= 5;i++){
					NucleotideFastaRecord fastaRecord = iter.next();
					sut.write(fastaRecord);				
				
					expected.write(fastaRecord);
				}
			}
			
			try(NucleotideFastaWriter expected = new NucleotideFastaWriterBuilder(new File(expectedDir, "2.fasta"))
														.build()){
				for (int i = 1; i <= 4; i++) {
					NucleotideFastaRecord fastaRecord = iter.next();
					sut.write(fastaRecord);

					expected.write(fastaRecord);
				}
			}
		}
		fastaFilesMatch(expectedDir, actualDir);
		
		
	}
	
	@Test
        public void allOneFile() throws IOException, DataStoreException{
                File actualDir = tmpDir.newFolder("actual");
                File expectedDir = tmpDir.newFolder("expected");
                
                
                try(NucleotideFastaWriter sut = createRolloverWriter(10, i -> new NucleotideFastaWriterBuilder(new File(actualDir, i +".fasta"))
                                                                                                                                                        .build());
                        StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();   
                        ){
                        try(NucleotideFastaWriter expected = new NucleotideFastaWriterBuilder(new File(expectedDir, "1.fasta"))
                                                                                                                        .build()){
                                for(int i=1; i<= 9;i++){
                                        NucleotideFastaRecord fastaRecord = iter.next();
                                        sut.write(fastaRecord);                         
                                
                                        expected.write(fastaRecord);
                                }
                        }
                       
                }
                fastaFilesMatch(expectedDir, actualDir); 
        }
	
	@Test
        public void writeIdSequenceAndComment() throws IOException, DataStoreException{
                File actualDir = tmpDir.newFolder("actual");
                File expectedDir = tmpDir.newFolder("expected");
                
                
                try(NucleotideFastaWriter sut = createRolloverWriter(10, i -> new NucleotideFastaWriterBuilder(new File(actualDir, i +".fasta"))
                                                                                                                                                        .build());
                        StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();   
                        ){
                        try(NucleotideFastaWriter expected = new NucleotideFastaWriterBuilder(new File(expectedDir, "1.fasta"))
                                                                                                                        .build()){
                                for(int i=1; i<= 9;i++){
                                        NucleotideFastaRecord fastaRecord = iter.next();
                                        sut.write(fastaRecord.getId(), fastaRecord.getSequence(), "comment");                         
                                
                                        expected.write(fastaRecord.getId(), fastaRecord.getSequence(), "comment");
                                }
                        }
                       
                }
                fastaFilesMatch(expectedDir, actualDir); 
        }
	
	@Test
        public void writeIdAndSequenceOnlyShouldMakeNullComment() throws IOException, DataStoreException{
                File actualDir = tmpDir.newFolder("actual");
                File expectedDir = tmpDir.newFolder("expected");
                
                
                try(NucleotideFastaWriter sut = createRolloverWriter(10, i -> new NucleotideFastaWriterBuilder(new File(actualDir, i +".fasta"))
                                                                                                                                                        .build());
                        StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();   
                        ){
                        try(NucleotideFastaWriter expected = new NucleotideFastaWriterBuilder(new File(expectedDir, "1.fasta"))
                                                                                                                        .build()){
                                for(int i=1; i<= 9;i++){
                                        NucleotideFastaRecord fastaRecord = iter.next();
                                        sut.write(fastaRecord.getId(), fastaRecord.getSequence());                         
                                
                                        expected.write(fastaRecord.getId(), fastaRecord.getSequence(), null);
                                }
                        }
                       
                }
                fastaFilesMatch(expectedDir, actualDir); 
        }
	
	private static void fastaFilesMatch(File expectedDir, File actualDir) throws IOException{
		File[] expectedFiles = expectedDir.listFiles(FASTA_FILE_FILTER);
		File[] actualFiles = actualDir.listFiles(FASTA_FILE_FILTER);
		
		
		Arrays.sort(expectedFiles, FILENAME_COMPARATOR);
		Arrays.sort(actualFiles, FILENAME_COMPARATOR);
		
		assertEquals("number of files must match", expectedFiles.length, actualFiles.length);
		
		for(int i=0; i< expectedFiles.length ; i++){
			//check name matches
			File expected = expectedFiles[i];
			File actual = actualFiles[i];
			assertEquals(expected.getName(), actual.getName());
			TestUtil.contentsAreEqual(expected, actual);
		}
	}
	
	private static NucleotideFastaWriter createRolloverWriter(int numPerFile, FastaRecordWriterFactory<NucleotideFastaWriter> supplier) throws IOException{
		return SplitFastaWriter.rollover(NucleotideFastaWriter.class, numPerFile, supplier);
	}
}
