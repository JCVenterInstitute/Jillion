package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.internal.fasta.DefaultFastaIndex;
import org.jcvi.jillion.internal.fasta.FastaIndex;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestFaiWriterBuilder {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	ResourceHelper helper;
	File fasta;
	
	@Before
	public void setup() throws IOException{
		helper = new ResourceHelper(getClass());
		fasta = helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs");
	}
	
	@Test(expected=NullPointerException.class)
	public void nullFastaShouldThrowNPE() throws IOException{
		new FaiNucleotideWriterBuilder(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void nullEolShouldThrowNPE() throws IOException{
		new FaiNucleotideWriterBuilder(fasta)
			.eol(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void nullCharsetShouldThrowNPE() throws IOException{
		new FaiNucleotideWriterBuilder(fasta)
			.charset(null);
	}
	
	@Test(expected=IOException.class)
	public void fastaThatDoesNotExistShouldThrowIOException() throws IOException{
		new FaiNucleotideWriterBuilder(new File(tmp.getRoot(), "missing"));
	}
	
	@Test
	public void createFaiFileWithDefaultOutput() throws IOException{
		File copyOfFasta = tmp.newFile(fasta.getName());
		copy(fasta, copyOfFasta);
		
		
		File actualFai = new FaiNucleotideWriterBuilder(copyOfFasta)
								.build();
		
		assertOutputFaiMatches(actualFai);
	}
	
	@Test
	public void createFaiFileWithNonDefaultEOL() throws IOException{
		File copyOfFasta = tmp.newFile(fasta.getName());
		copy(fasta, copyOfFasta);
		
		for(String eol : Arrays.asList("\n","\r\n")){
			File actualFai = new FaiNucleotideWriterBuilder(copyOfFasta)
									.eol(eol)
									.build();

			assertOutputFaiMatches(actualFai);
			assertLinesEndWith(actualFai, eol);
		}
	}
	
	@Test
	public void createFaiFileWithNonDefaultCharset() throws IOException{
		File copyOfFasta = tmp.newFile(fasta.getName());
		copy(fasta, copyOfFasta);
		
		for(Charset charset : Arrays.asList(StandardCharsets.US_ASCII,
											StandardCharsets.ISO_8859_1,
											StandardCharsets.UTF_16,
											StandardCharsets.UTF_8
											)){
			File actualFai = new FaiNucleotideWriterBuilder(copyOfFasta)
									.charset(charset)
									.build();

			assertOutputFaiMatches(actualFai, charset.name());
		}
	}

	private void assertLinesEndWith(File actualFai, String eol) throws IOException {
		try(TextLineParser parser = new TextLineParser(actualFai)){
			while(parser.hasNextLine()){
				assertTrue(parser.nextLine().endsWith(eol));
			}
		}
		
	}
	private void assertOutputFaiMatches(File actualFai) throws IOException {
		assertOutputFaiMatches(actualFai, IOUtil.UTF_8_NAME);
	}
	private void assertOutputFaiMatches(File actualFai, String charset) throws IOException {
		FastaIndex actualIndex = DefaultFastaIndex.parse(actualFai, charset);
		
		FastaIndex expectedIndex = DefaultFastaIndex.parse(helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs.fai"));
		
		for(String id : Arrays.asList("MAINa", "MAINb")){
			assertEquals(id, expectedIndex.getIndexFor(id), actualIndex.getIndexFor(id));
		}
	}
	
	@Test
	public void createFaiFileWithSpecifiedOutput() throws IOException{
		
		File faiToWrite = tmp.newFile("myFai.out");
		
		
		File actualFai = new FaiNucleotideWriterBuilder(fasta)
								.outputFile(faiToWrite)
								.build();
		
		assertEquals(faiToWrite, actualFai);
		
		assertOutputFaiMatches(actualFai);
	}

	private void copy(File from, File to) throws IOException, FileNotFoundException {
		try(InputStream in = new BufferedInputStream(new FileInputStream(from));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
		){
			IOUtil.copy(in, out);
		}
	}

}
