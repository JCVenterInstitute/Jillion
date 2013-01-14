package org.jcvi.jillion.fasta.qual;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;

public class TestDefaultQualitySequenceFastaRecordWriter {
	private final QualitySequenceFastaRecord record1 = new QualitySequenceFastaRecordBuilder("id_1", 
						new QualitySequenceBuilder(new byte[]{8,9,10,11,12,13,14,15}).build())
						.comment("a comment")
						.build();
	private final QualitySequenceFastaRecord record2 = 
			new QualitySequenceFastaRecordBuilder("id_2", 
									new QualitySequenceBuilder(new byte[]{20,20,20,20,30,30,30,30,40,40,40,40})
											.build())
			.build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new QualitySequenceFastaRecordWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws FileNotFoundException{
		new QualitySequenceFastaRecordWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new QualitySequenceFastaRecordWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new QualitySequenceFastaRecordWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualitySequenceFastaRecordWriter sut = new QualitySequenceFastaRecordWriterBuilder(out)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11 12 13 14 15\n"+
							">id_2\n"+
							"20 20 20 20 30 30 30 30 40 40 40 40\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void multiLineFastas() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualitySequenceFastaRecordWriter sut = new QualitySequenceFastaRecordWriterBuilder(out)
								.numberPerLine(5)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11 12\n13 14 15\n"+
							">id_2\n"+
							"20 20 20 20 30\n30 30 30 40 40\n40 40\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualitySequenceFastaRecordWriter sut = new QualitySequenceFastaRecordWriterBuilder(out)
								.numberPerLine(4)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11\n12 13 14 15\n"+
							">id_2\n"+
							"20 20 20 20\n30 30 30 30\n40 40 40 40\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void differentCharSet() throws IOException{
		Charset charSet = Charset.forName("UTF-16");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualitySequenceFastaRecordWriter sut = new QualitySequenceFastaRecordWriterBuilder(out)
								.numberPerLine(5)	
								.charset(charSet)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11 12\n13 14 15\n"+
							">id_2\n"+
							"20 20 20 20 30\n30 30 30 40 40\n40 40\n";
		byte[] expectedBytes = expected.getBytes(charSet);
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
	
	@Test
	public void parseAndWriteShouldMatch() throws IOException, DataStoreException{
		ResourceHelper resources = new ResourceHelper(TestDefaultQualitySequenceFastaRecordWriter.class);
		File expectedFasta = resources.getFile("files/19150.qual");
		QualitySequenceFastaDataStore datastore = LargeQualityFastaFileDataStore.create(expectedFasta);
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualitySequenceFastaRecordWriter sut = new QualitySequenceFastaRecordWriterBuilder(out).build();
		StreamingIterator<QualitySequenceFastaRecord> iter=null;
		
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				sut.write(iter.next());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		sut.close();
		InputStream in = new FileInputStream(expectedFasta);
		byte[] expectedBytes;
		try{
			expectedBytes = IOUtil.toByteArray(in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
}
