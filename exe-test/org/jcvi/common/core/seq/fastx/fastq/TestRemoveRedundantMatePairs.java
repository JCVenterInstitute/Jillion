package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.qual.DefaultEncodedPhredGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
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
		left = new DefaultFastqRecord("id", 
				new NucleotideSequenceBuilder()
		.append("ACGTACGTACGTACGTACGT").build(), 
		new EncodedQualitySequence(new DefaultEncodedPhredGlyphCodec(),
				qualities));
		
		right = new DefaultFastqRecord("id", 
				new NucleotideSequenceBuilder()
		.append("AAAAAAAAAAATTTTTTTTT").build(), 
		new EncodedQualitySequence(new DefaultEncodedPhredGlyphCodec(),
				qualities));
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
		
		OutputStream out1=null;
		OutputStream out2=null;
		try{
			out1 = new FileOutputStream(mate1File);
			out2 = new FileOutputStream(mate2File);
			
			out1.write(left.toFormattedString().getBytes(IOUtil.UTF_8));
			out2.write(right.toFormattedString().getBytes(IOUtil.UTF_8));
			for(int i=0; i<numDups; i++){
				FastqRecord newLeft = new DefaultFastqRecord(left.getId()+i, left.getNucleotides(), left.getQualities());
				out1.write(newLeft.toFormattedString().getBytes(IOUtil.UTF_8));
				
				FastqRecord newRight= new DefaultFastqRecord(right.getId()+i, right.getNucleotides(), right.getQualities());
				
				out2.write(newRight.toFormattedString().getBytes(IOUtil.UTF_8));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(out1,out2);
		}
		
		return new MatePairFiles(mate1File, mate2File);
	}
protected MatePairFiles createCompletelyRedundantUpToNBasesData(int numDups) throws IOException {
		
		
		File mate1File = tempFolder.newFile("mate1");
		File mate2File = tempFolder.newFile("mate2");
		
		OutputStream out1=null;
		OutputStream out2=null;
		try{
			out1 = new FileOutputStream(mate1File);
			out2 = new FileOutputStream(mate2File);
			Range subRange = Range.createOfLength(numberOfBasesToCompare);
			out1.write(left.toFormattedString().getBytes(IOUtil.UTF_8));
			out2.write(right.toFormattedString().getBytes(IOUtil.UTF_8));
			for(int i=0; i<numDups; i++){
				FastqRecord newLeft = new DefaultFastqRecord(left.getId()+i, 
						new NucleotideSequenceBuilder(left.getNucleotides())
								.subSequence(subRange)
								.append("NNNNNNNNN")
								.build(), 
				left.getQualities());
				out1.write(newLeft.toFormattedString().getBytes(IOUtil.UTF_8));
				
				FastqRecord newRight= new DefaultFastqRecord(right.getId()+i, 
						new NucleotideSequenceBuilder(right.getNucleotides())
							.subSequence(subRange)
							.append("NNNNNNNNN")
							.build(), 
				right.getQualities());
				
				out2.write(newRight.toFormattedString().getBytes(IOUtil.UTF_8));
		
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(out1,out2);
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
