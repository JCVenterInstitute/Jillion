package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.jcvi.jillion.core.io.InputStreamSupplier.InputStreamReadOptions;
import org.jcvi.jillion.core.util.streams.ThrowingFunction;
import org.jcvi.jillion.experimental.trace.archive2.TestTraceArchiveWriter;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestInputStreamEntryPredicate {

	@Parameters(name = "{0}")
	public static List<Object[]> inputFiles(){
		
		List<Object[]> list = new ArrayList<>();
		list.add(new Object[] {"tar.gz", true, function(o-> new TarArchiveOutputStream(new GzipCompressorOutputStream(o)))});
		list.add(new Object[] {"tar nested=false", false, function(o-> new TarArchiveOutputStream(o))});
		list.add(new Object[] {"tar nested=true", true, function(o-> new TarArchiveOutputStream(o))});
		
		list.add(new Object[] {"zip", false, function(o-> new ZipArchiveOutputStream(o))});
		return list;
	}
	
	private final boolean nested;
	private final ThrowingFunction<OutputStream, ArchiveOutputStream, IOException> archiveStreamFactory;
	private File input;
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	/**
	 * method to help java type inference
	 * @param func
	 * @return
	 */
	private static ThrowingFunction<OutputStream, ArchiveOutputStream, IOException> function(ThrowingFunction<OutputStream, ArchiveOutputStream, IOException> func){
		return func;
	}
	
	public TestInputStreamEntryPredicate(String ignoredMethodName, boolean nested, ThrowingFunction<OutputStream, ArchiveOutputStream, IOException> archiveStreamFactory) throws IOException {
		this.nested = nested;
		this.archiveStreamFactory = archiveStreamFactory;
	
	}
	
	private void createInputFileIfNeeded() throws IOException{
		if(input ==null) {
			this.input = compress();
		}
	}

	private File compress() throws IOException {
		
		File outputFile = tmpDir.newFile();
		ResourceHelper resources = new ResourceHelper(TestTraceArchiveWriter.class);
		File rootInputDir = resources.getFile("files/exampleTraceArchive");
		
		Path root = rootInputDir.toPath();
		List<Path> filesToArchive = Files.walk(root).filter(Files::isRegularFile).collect(Collectors.toList());
	
		try (OutputStream out = Files.newOutputStream(outputFile.toPath());
			ArchiveOutputStream o = archiveStreamFactory.apply(out)) {
			
		    for (Path f : filesToArchive) {
		        Path relPath = root.relativize(f);
		        StringBuilder builder = new StringBuilder(relPath.getName(0).toString());
		        
		        for(int i=1; i< relPath.getNameCount(); i++) {
		        	builder.append("/").append(relPath.getName(i));
		        }
		        ArchiveEntry entry = o.createArchiveEntry(f, builder.toString());
		        // potentially add more flags to entry
		        o.putArchiveEntry(entry);
		        
	            try (InputStream i = Files.newInputStream(f)) {
	                IOUtils.copy(i, o);
	            }
		        
		        o.closeArchiveEntry();
		    }
		    o.finish();
		}
		
		return outputFile;
	}
	@Test
	public void readTraceInfo() throws IOException {
		createInputFileIfNeeded();
		InputStreamSupplier sut = InputStreamSupplier.forFile(input);
		boolean found=false;
		try(InputStream in = sut.get(InputStreamReadOptions.builder()
				.nestedDecompress(nested)
				.entryNamePredicate(f-> f.equalsIgnoreCase("TRACEINFO.xml"))
				.build());
				
			Scanner scanner = new Scanner(in);
				){
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.contains("<trace_name>")) {
					found=true;
					break;
				}
			}
		}
		assertTrue(found);
	}
	
	@Test
	public void readChromatogram() throws IOException {
		createInputFileIfNeeded();
		InputStreamSupplier sut = InputStreamSupplier.forFile(input);
		
		try(InputStream in = sut.get(InputStreamReadOptions.builder()
				.nestedDecompress(nested)
				.entryNamePredicate(f-> f.endsWith(".ztr"))
				.build());
				
			
				){
			Chromatogram chromo = ChromatogramFactory.create("test", in);
			assertTrue(chromo.getLength() >0);
			assertEquals(chromo.getNucleotideSequence().getLength(), chromo.getQualitySequence().getLength());
			
		}
	}
}
