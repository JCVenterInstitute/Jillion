/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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

	private static final class VisitorWriter implements SffVisitor{
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
		public void visitHeader(SffVisitorCallback callback,
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
		public SffFileReadVisitor visitRead(SffVisitorCallback callback,
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
		SffFileParser.create(inputFile).parse(visitor);
		
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
