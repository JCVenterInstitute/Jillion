package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
public class TestRemoveRedundantMatePairs {

	private int numberOfBasesToCompare =10;
	
	private final FastqRecord left, right;
	
	public TestRemoveRedundantMatePairs(){
		byte[] qualities = new byte[20];
		Arrays.fill(qualities, (byte)20);
		left = FastqRecordFactory.create("id", 
				new NucleotideSequenceBuilder()
		.append("ACGTACGTACGTACGTACGT").build(), 
		new QualitySequenceBuilder(qualities).build());
		
		right = FastqRecordFactory.create("id", 
				new NucleotideSequenceBuilder()
		.append("AAAAAAAAAAATTTTTTTTT").build(), 
		new QualitySequenceBuilder(qualities).build());
	}

	@Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void emptyInputFilesShouldCreateEmptyOutputFiles() throws IOException, DataStoreException{
		MatePairFiles inputMates = createEmptyMatePairs();
		MatePairFiles outputMates = dedupe(inputMates);
		
		assertTrue(outputMates.getFile1().length() ==0L);
		assertTrue(outputMates.getFile2().length() ==0L);
	}
	
	@Test
	public void completelyRedundantDataShouldOnlyWriteOutOneRecord() throws IOException, DataStoreException{
		MatePairFiles inputMates = createCompletelyRedundantData(5);
		MatePairFiles outputMates = dedupe(inputMates);
		
		FastqDataStore filtered1 = DefaultFastqFileDataStore.create(outputMates.getFile1(), FastqQualityCodec.SANGER);
		FastqDataStore filtered2 = DefaultFastqFileDataStore.create(outputMates.getFile2(), FastqQualityCodec.SANGER);
		
		assertEquals(1L, filtered1.getNumberOfRecords());
		assertEquals(1L, filtered2.getNumberOfRecords());
		
		assertEquals(left, filtered1.get(left.getId()));
		assertEquals(right, filtered2.get(right.getId()));
	}
	
	@Test
	public void recordsThatMatchFirstNBasesShouldBeRedundant() throws IOException, DataStoreException{
		MatePairFiles inputMates = createCompletelyRedundantUpToNBasesData(5);
		MatePairFiles outputMates = dedupe(inputMates);
		
		FastqDataStore filtered1 = DefaultFastqFileDataStore.create(outputMates.getFile1(), FastqQualityCodec.SANGER);
		FastqDataStore filtered2 = DefaultFastqFileDataStore.create(outputMates.getFile2(), FastqQualityCodec.SANGER);
		
		assertEquals(1L, filtered1.getNumberOfRecords());
		assertEquals(1L, filtered2.getNumberOfRecords());
		
		assertEquals(left, filtered1.get(left.getId()));
		assertEquals(right, filtered2.get(right.getId()));
	}
	
	

	private MatePairFiles dedupe(MatePairFiles inputMates) throws IOException,
			DataStoreException {
		File outputDir =tempFolder.newFolder("output");
		RemoveRedundantMatePairs.main(new String[]{
				"-mate1", inputMates.getFile1().getAbsolutePath(),
				"-mate2", inputMates.getFile2().getAbsolutePath(),
				"-prefix", "prefix",
				"-s", "100",
				"-n", Integer.toString(numberOfBasesToCompare),
				"-o", outputDir.getAbsolutePath(),
				"-sanger"
				
		});
		return new MatePairFiles(
				new File(outputDir,"prefix_1.fastq"), new File(outputDir,"prefix_2.fastq"));
	}
	protected MatePairFiles createEmptyMatePairs() throws IOException{
		File mate1File = tempFolder.newFile("mate1");
		File mate2File = tempFolder.newFile("mate2");
		
		
		return new MatePairFiles(mate1File, mate2File);
	}
	
	protected MatePairFiles createCompletelyRedundantData(int numDups) throws IOException {
		
		
		File mate1File = tempFolder.newFile("mate1");
		File mate2File = tempFolder.newFile("mate2");
		FastqRecordWriter writer1 = new DefaultFastqRecordWriter.Builder(mate1File)
									.build();
		FastqRecordWriter writer2 = new DefaultFastqRecordWriter.Builder(mate2File)
									.build();
		
		try{

			
			writer1.write(left);
			writer2.write(right);
			for(int i=0; i<numDups; i++){
				writer1.write(left.getId()+i, left.getNucleotideSequence(), left.getQualitySequence());				
				writer2.write(right.getId()+i, right.getNucleotideSequence(), right.getQualitySequence());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(writer1,writer2);
		}
		
		return new MatePairFiles(mate1File, mate2File);
	}
protected MatePairFiles createCompletelyRedundantUpToNBasesData(int numDups) throws IOException {
		
		
		File mate1File = tempFolder.newFile("mate1");
		File mate2File = tempFolder.newFile("mate2");
		
		FastqRecordWriter writer1 = new DefaultFastqRecordWriter.Builder(mate1File)
		.build();
		FastqRecordWriter writer2 = new DefaultFastqRecordWriter.Builder(mate2File)
				.build();
		try{
			Range subRange = new Range.Builder(numberOfBasesToCompare).build();
			writer1.write(left);
			writer2.write(right);
			for(int i=0; i<numDups; i++){
				FastqRecord newLeft = FastqRecordFactory.create(left.getId()+i, 
						new NucleotideSequenceBuilder(left.getNucleotideSequence())
								.trim(subRange)
								.append("NNNNNNNNNN")
								.build(), 
				left.getQualitySequence());
				writer1.write(newLeft);
				
				FastqRecord newRight= FastqRecordFactory.create(right.getId()+i, 
						new NucleotideSequenceBuilder(right.getNucleotideSequence())
							.trim(subRange)
							.append("NNNNNNNNNN")
							.build(), 
				right.getQualitySequence());
				
				writer2.write(newRight);
		
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(writer1,writer2);
		}
		
		return new MatePairFiles(mate1File, mate2File);
	}
	private static final class MatePairFiles{
		private final File file1, file2;

		private MatePairFiles(File file1, File file2) {
			this.file1 = file1;
			this.file2 = file2;
		}

		public File getFile1() {
			return file1;
		}

		public File getFile2() {
			return file2;
		}
		
		
	}
	    
}
