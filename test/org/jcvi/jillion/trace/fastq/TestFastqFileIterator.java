package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqFileReader.Results;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
import org.junit.Before;
import org.junit.Test;

public class TestFastqFileIterator {

	private final ResourceHelper helper = new ResourceHelper(this);

	private File fastqFile;

	@Before
	public void setUp() throws IOException {
		fastqFile = helper.getFile("files/giv_XX_15050.fastq");
	}

	@Test
	public void iteratorMatchesStream() throws IOException {

		SingleThreadAdder numberIterated = new SingleThreadAdder();

		try (Results results = FastqFileReader.read(fastqFile);
				ThrowingStream<FastqRecord> stream = results.records();
				FastqSingleVisitIterator iter = ((FastqFileParser) FastqFileParser.create(fastqFile)).iterator()) {
			assertTrue(iter.hasNext());
			FastqRecord actual[] = new FastqRecord[1];
			FastqVisitor visitor = new AbstractFastqVisitor() {

				@Override
				public FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id,
						String optionalComment) {
					return new AbstractFastqRecordVisitor(id, optionalComment, results.getCodec()) {

						@Override
						protected void visitRecord(FastqRecord record) {
							actual[0] = record;

						}
					};
				}

			};
			stream.forEach(expected -> {
				numberIterated.increment();
				assertTrue(iter.hasNext());
				iter.next(visitor);
				assertEquals(expected, actual[0]);

			});

			assertFalse(iter.hasNext());
			assertThrows(NoSuchElementException.class, ()-> iter.next(visitor));
		}

		try (Results results = FastqFileReader.read(fastqFile);
				ThrowingStream<FastqRecord> stream = results.records();) {
			long expected = stream.count();
			assertEquals(expected, numberIterated.longValue());
		}
	}
	
	@Test
	public void iteratorMementoMatchesStream() throws IOException {

		FastqVisitorMemento memento[] = new FastqVisitorMemento[1];
		FastqFileParser.create(fastqFile)
						.parse(new AbstractFastqVisitor() {
							int i=0;
							@Override
							public FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id,
									String optionalComment) {
								i++;
								if(i==10) {
									memento[0]= callback.createMemento();
									callback.haltParsing();
								}
								return null;
							}
							
						});
				
		List<FastqRecord> expectedList = new ArrayList<>();		
		FastqFileParser.create(fastqFile).parse(new AbstractFastqVisitor() {

			@Override
			public FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id, String optionalComment) {
				
				return new AbstractFastqRecordVisitor(id, optionalComment, FastqQualityCodec.SANGER) {
					
					@Override
					protected void visitRecord(FastqRecord record) {
						expectedList.add(record);
						
					}
				};
			}
			
		}, 
				memento[0]);
		
		try(FastqSingleVisitIterator actualIter = ((FastqFileParser)FastqFileParser.create(fastqFile)).iterator(memento[0])){
			Iterator<FastqRecord> expectedIter = expectedList.iterator();
			
			assertTrue(expectedIter.hasNext());
			assertEquals(expectedIter.hasNext(), actualIter.hasNext());
			
			FastqRecord r[] = new FastqRecord[1];
			FastqVisitor visitor = new AbstractFastqVisitor() {

				@Override
				public FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id,
						String optionalComment) {
					return new AbstractFastqRecordVisitor(id, optionalComment, FastqQualityCodec.SANGER) {						 
						
						@Override
						protected void visitRecord(FastqRecord record) {
							r[0]= record;
							
						}
					};
				}
				
			};
			
			while(expectedIter.hasNext()) {
				
				assertTrue(actualIter.hasNext());
				actualIter.next(visitor);
				assertEquals(expectedIter.next(),r[0]);
			}
			assertFalse(actualIter.hasNext());
			assertThrows(NoSuchElementException.class, ()-> actualIter.next(visitor));
		}
		
				
	}

}
