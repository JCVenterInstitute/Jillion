package org.jcvi.jillion.sam.index;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.AbstractSamVisitor;
import org.jcvi.jillion.sam.SamFileWriterBuilder;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordBuilder;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamReferenceSequenceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCorruptBamIndex {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	ResourceHelper resources = new ResourceHelper(TestCorruptBamIndex.class);
	
	@Test
	public void noMappedReadsWithIndexShouldStillBeParsable() throws IOException {
		File bamFile = tmpDir.newFile("example.bam");
		SamHeader header = new SamHeaderBuilder()
								.addReferenceSequence(new SamReferenceSequenceBuilder("foo", 100).build())
								.build();
		try(SamWriter writer = new SamFileWriterBuilder(bamFile, header).createBamIndex(true, true)
									.forceHeaderSortOrder(SortOrder.COORDINATE)
									.build()){
			writer.writeRecord(new SamRecordBuilder(header)
									.setMapped(false)
									.setQueryName("foo")
									.build());
		}
		SingleThreadAdder mapped=new SingleThreadAdder(), unmapped=new SingleThreadAdder();
		SamParserFactory.create(bamFile).parse(new AbstractSamVisitor() {
			@Override
			public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
					VirtualFileOffset end) {
				if(record.mapped()) {
					mapped.increment();
				}else {
					unmapped.increment();
				}
			}
		});
		
		assertEquals(0, mapped.intValue());
		assertEquals(1, unmapped.intValue());
	}
	
	@Test
	public void corruptedIndexBamStillParsable() throws IOException {
		File outputDir = tmpDir.getRoot();
		File inputBam = new File(outputDir, "input.bam");
		File inputBamIndex = new File(outputDir, "input.bam.bai");
		try(FileOutputStream out = new FileOutputStream(inputBam)){
			IOUtil.copy(resources.getFileAsStream("corrupted.bam"), out);
		}
		try(FileOutputStream out = new FileOutputStream(inputBamIndex)){
			IOUtil.copy(resources.getFileAsStream("corrupted.bam.bai"), out);
		}
		
		SingleThreadAdder mapped=new SingleThreadAdder(), unmapped=new SingleThreadAdder();
		SamParser sut = SamParserFactory.create(inputBam);
		AbstractSamVisitor visitor = new AbstractSamVisitor() {
			@Override
			public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
					VirtualFileOffset end) {
				if(record.mapped()) {
					mapped.increment();
				}else {
					unmapped.increment();
				}
			}
		};
		sut.parse(visitor);
		
		assertEquals(0, mapped.intValue());
		assertEquals(341964, unmapped.intValue());
		//test fetch ref 
		mapped.set(0);
		unmapped.set(0);
		
		sut.parse(sut.getHeader().getReferenceNames().get(0), visitor);
		assertEquals(0, mapped.intValue());
		assertEquals(0, unmapped.intValue());
		//test fetch ref range
		mapped.set(0);
		unmapped.set(0);
		
		sut.parse(sut.getHeader().getReferenceNames().get(0), Range.of(1000,2000), visitor);
		assertEquals(0, mapped.intValue());
		assertEquals(0, unmapped.intValue());
	}
}
