package org.jcvi.jillion.trace.fastq;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.InputStreamSupplier;
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
	    File gzipped = gzip(fastqFile);
	    return FastqFileParser.create(InputStreamSupplier.forFile(gzipped), false, false);
	}
	
	
}
