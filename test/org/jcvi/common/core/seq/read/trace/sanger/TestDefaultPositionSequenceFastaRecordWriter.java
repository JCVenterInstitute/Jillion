package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultPositionSequenceFastaRecordWriter {

	private final File peakFile;
	private final PositionSequenceFastaRecord fasta;
	public TestDefaultPositionSequenceFastaRecordWriter() throws IOException, DataStoreException{
		ResourceFileServer resources = new ResourceFileServer(TestDefaultPositionSequenceFastaRecordWriter.class);
		peakFile = resources.getFile("1119369023656.peak");
		
		fasta = DefaultPositionFastaFileDataStore.create(peakFile).get("1119369023656");
	}
	
	@Test
	public void rewriteShouldMatchExactly() throws IOException{
		
		String asString = asString(peakFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PositionSequenceFastaRecordWriter sut = new DefaultPositionSequenceFastaRecordWriter.Builder(out)
												.build();
		
		sut.write(fasta);
		sut.close();
		assertEquals(asString, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void differentCharSet() throws IOException{
		
		String asString = asString(peakFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Charset charset = Charset.forName("UTF-16");
		PositionSequenceFastaRecordWriter sut = new DefaultPositionSequenceFastaRecordWriter.Builder(out)
												.charset(charset)
												.build();
		
		sut.write(fasta);
		sut.close();
		assertEquals(asString, new String(out.toByteArray(), charset));
	}
	private String asString(File fastaFile) throws IOException{
		InputStream in = new FileInputStream(fastaFile);
		try{
			return IOUtil.toString(in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
}
