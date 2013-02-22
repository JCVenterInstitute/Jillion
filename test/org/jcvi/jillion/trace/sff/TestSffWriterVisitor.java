package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestSffWriterVisitor {

	private static final class VisitorWriter implements SffFileVisitor{
		private final File outputFile;
		private SffWriter writer;
		
		public VisitorWriter(File outputFile) {
			this.outputFile = outputFile;
		}

		@Override
		public void end() {
			try {
				writer.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			try {
				writer = new SffWriterBuilder(outputFile, header)
								.includeIndex(true)
								.build();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			return new SffFileReadVisitor() {
				
				@Override
				public void visitReadData(SffReadData readData) {
					try {
						writer.write(readHeader, readData);
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
					
				}
				
				@Override
				public void visitEnd() {
					//no-op
					
				}
			};
		}
		
	}
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	@Test
	public void visitFileAndWriteIdenticalCopy() throws IOException{
		File output =  tempDir.newFile();
		VisitorWriter visitor = new VisitorWriter(output);
		ResourceHelper resources = new ResourceHelper(TestSffWriterVisitor.class);
		
		File inputFile = resources.getFile("files/5readExample_noXML.sff");
		SffFileParser.create(inputFile).accept(visitor);
		
		InputStream expectedInputStream = new BufferedInputStream(new FileInputStream(inputFile));
		InputStream actualInputStream = new BufferedInputStream(new FileInputStream(output));
		try{
			byte[] expected =IOUtil.toByteArray(expectedInputStream);
			byte[] actual =IOUtil.toByteArray(actualInputStream);
			assertArrayEquals(expected, actual);
		}finally{
			IOUtil.closeAndIgnoreErrors(actualInputStream, expectedInputStream);
		}
		
	}
}
