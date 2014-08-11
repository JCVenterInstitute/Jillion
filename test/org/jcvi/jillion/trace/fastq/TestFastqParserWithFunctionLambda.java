package org.jcvi.jillion.trace.fastq;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
/**
 * Tests {@link FastqParser} but uses the new Java 8 {@link Function}
 * to test converting the file encoding into some other kind of InputStream
 * (in this case gzipping it).  
 * @author dkatzel
 *
 */
public class TestFastqParserWithFunctionLambda extends TestFastqParser{

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	
	Function<File, InputStream> toGzipInputStream = f -> {
		try {
			return new GZIPInputStream(new FileInputStream(f));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	};
	
	private File gzip(File f) throws IOException{
		File out = tmp.newFile();
		
		try(InputStream in = new BufferedInputStream(new FileInputStream(f));
			OutputStream o = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(out)));
			){
				IOUtil.copy(in, o);
			}
		
		return out;
	}

	@Override
	protected FastqParser createSut(File fastqFile) throws IOException {
		return FastqFileParser.create(gzip(fastqFile),toGzipInputStream);
	}
	
	
}
