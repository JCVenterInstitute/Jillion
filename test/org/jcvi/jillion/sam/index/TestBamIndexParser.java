/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.sam.AbstractSamVisitor;
import org.jcvi.jillion.sam.MultiSamVisitor;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParser.SamParserOptions;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback.SamVisitorMemento;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Test;

import lombok.Data;

import static org.junit.Assert.*;

public class TestBamIndexParser {

	private static final boolean IGNORE_METADATA = true;
	ResourceHelper resources = new ResourceHelper(TestBamIndexParser.class);
	
	@Test
	public void parsedBaiMatchesRecordsInBam() throws IOException{
		File bamfile = resources.getFile("index_test.bam");
		File expectedBaiFile = resources.getFile("index_test.bam.bai");

			
		BamIndex expectedIndex = BamIndex.createFromFiles(bamfile, expectedBaiFile);
		
		BamIndex actualIndex = createIndexFromBam(bamfile);
		BamIndexTestUtil.assertIndexesEqual(expectedIndex, actualIndex, IGNORE_METADATA);
	
		
		
	}
	
	@Test
	public void parseAlignedRangeWithNoReadsIntersecting() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		parser.parse(rangeParser);
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		Range rangeWithReadsOnEitherSidesButNoneIntersecting = Range.of(2000, 11_000);
		
		parser.parse(
				SamParserOptions.builder()
				.reference(rangeParser.referenceName, rangeWithReadsOnEitherSidesButNoneIntersecting)
				.build(),
				new AssertNoReadsOutsideRange(rangeWithReadsOnEitherSidesButNoneIntersecting));
	}
	@Test
	public void parseAlignedRangeWithFileOffsetCoordinatesCorrect() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		OffsetRecorder offsetRecorder = new OffsetRecorder();
		parser.parse(SamParserOptions.builder().createMementos(true).build(),
				MultiSamVisitor.of(rangeParser,offsetRecorder ));
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		Range rangeWithReadsOnEitherSidesButNoneIntersecting = Range.of(2000, 11_000);
		OffsetRecorder offsetRecorder2 = new OffsetRecorder();
		parser.parse(
				SamParserOptions.builder()
				.createMementos(true)
				.reference(rangeParser.referenceName, rangeWithReadsOnEitherSidesButNoneIntersecting)
				.build(),
				offsetRecorder2);
		
		assertFalse(offsetRecorder2.pairs.isEmpty());
		Set<NameOffsetPair> allPairsSet = new HashSet<>(offsetRecorder.pairs);
		
		for(NameOffsetPair pair : offsetRecorder2.pairs) {
			assertTrue(allPairsSet.contains(pair));
		}
	}
	
	@Test
	public void parseAlignedRangeWithIntersectingStart() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		parser.parse(rangeParser);
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		Range rangeWithReadsOnEitherSidesButNoneIntersecting = Range.of(1700, 11_000);
		
		parser.parse(
				SamParserOptions.builder()
				.reference(rangeParser.referenceName, rangeWithReadsOnEitherSidesButNoneIntersecting)
				.build(),
				new AssertIntersectStartAndNoneStartAfter(rangeWithReadsOnEitherSidesButNoneIntersecting));
	}
	
	@Test
	public void parseAlignedRangeWithAllIntersect() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		parser.parse(rangeParser);
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		Range rangeWithReadsOnEitherSidesButNoneIntersecting = Range.of(1700, 11_000);
		
		parser.parse(
				SamParserOptions.builder()
				.reference(rangeParser.referenceName, rangeWithReadsOnEitherSidesButNoneIntersecting)
				.build(),
				new AssertAllIntersect(rangeWithReadsOnEitherSidesButNoneIntersecting));
	}
	@Test
	public void parseAlignedRangeBeyondAlignedReads() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		parser.parse(rangeParser);
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

		//last aligned offset = 16558
//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		
		parser.parse(
				SamParserOptions.builder()
				.reference(rangeParser.referenceName,  rangeParser.fullReferenceRange)
				.build(),
				new AssertAllIntersect( rangeParser.fullReferenceRange));
	}
	
	@Test
	public void parseAlignedRangeBeyondReferenceLength() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		parser.parse(rangeParser);
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

		//last aligned offset = 16558
//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		
		Range r = Range.ofLength(rangeParser.fullReferenceRange.getLength() *2);
		parser.parse(
				SamParserOptions.builder()
				.reference(rangeParser.referenceName, r)
				.build(),
				new AssertAllIntersect( r));
	}
	@Test
	public void parseAlignedRangeReallyBeyondReferenceLength() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		RangeParser rangeParser = new RangeParser();
		parser.parse(rangeParser);
		
//		System.out.println("full ref range = " + rangeParser.fullReferenceRange);
		//full ref range = [ 0 .. 16570 ]/0B
//		aligned range = [[ 1518 .. 1568 ]/0B, [ 1686 .. 1736 ]/0B, [ 2983 .. 3033 ]/0B, [ 3131 .. 3181 ]/0B, [ 4867 .. 4917 ]/0B, [ 5017 .. 5067 ]/0B, [ 8907 .. 8957 ]/0B, [ 9059 .. 9109 ]/0B, [ 10387 .. 10476 ]/0B, [ 11146 .. 11196 ]/0B, [ 11289 .. 11339 ]/0B, [ 12105 .. 12155 ]/0B, [ 12249 .. 12299 ]/0B, [ 12864 .. 12914 ]/0B, [ 13017 .. 13067 ]/0B, [ 13762 .. 13812 ]/0B, [ 13920 .. 13970 ]/0B, [ 14755 .. 14805 ]/0B, [ 15404 .. 15454 ]/0B, [ 15568 .. 15618 ]/0B, [ 16357 .. 16407 ]/0B, [ 16508 .. 16558 ]/0B]

		//last aligned offset = 16558
//		System.out.println("aligned range = " + Ranges.merge(rangeParser.seenAlignedRanges));
		
		
		Range r = Range.ofLength(Integer.MAX_VALUE);
		parser.parse(
				SamParserOptions.builder()
				.reference(rangeParser.referenceName, r)
				.build(),
				new AssertAllIntersect( r));
	}
	
	private static class AssertNoReadsOutsideRange extends AbstractSamVisitor{
		private final Range range;

		public AssertNoReadsOutsideRange(Range range) {
			this.range = range;
		}

		

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			Range r =record.getAlignmentRange();
			if(r !=null) {
				assertTrue(r.toString(), range.startsBefore(r));
				assertTrue(r.toString(), range.endsAfter(r));
			}
			
		}	
		
	}
	
	private static class AssertIntersectStartAndNoneStartAfter extends AbstractSamVisitor{
		private final Range range;

		public AssertIntersectStartAndNoneStartAfter(Range range) {
			this.range = range;
		}

		

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			Range r =record.getAlignmentRange();
			if(r !=null) {
				assertTrue(r.toString(), range.intersects(r));
				assertFalse(r.toString(), r.startsAfter(range));
			}
			
		}	
		
	}
	
	private static class AssertAllIntersect extends AbstractSamVisitor{
		private final Range range;

		public AssertAllIntersect(Range range) {
			this.range = range;
		}

		

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			Range r =record.getAlignmentRange();
			if(r !=null) {
				assertTrue(r.toString(), range.intersects(r));
			}
			
		}	
		
	}

	private static class RangeParser extends AbstractSamVisitor{

		private String referenceName;
		private Range fullReferenceRange;
		private Set<Range> seenAlignedRanges = new HashSet<>();
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			referenceName = header.getReferenceNames().get(0);
			fullReferenceRange = Range.ofLength(header.getReferenceSequence(referenceName).getLength());
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			if(record.mapped() && referenceName.equals(record.getReferenceName())) {
				seenAlignedRanges.add(record.getAlignmentRange());
			}
			
		}
		
	}
	@Test
	public void startingAtMementoStartsAtCorrectOfset() throws IOException {

		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		OffsetRecorder offsetRecorder = new OffsetRecorder();
		SamParserOptions options =  SamParserOptions.builder().createMementos(true).build();
		
		parser.parse(options, offsetRecorder);
		
		NameOffsetPair pair = offsetRecorder.pairs.get(1234);
		
		parser.parse(options.toBuilder()
				.memento(pair.memento)
				.build(), new AssertVisitorStartsAtMemento(pair.memento));
	}
	
	private static class AssertVisitorStartsAtMemento implements SamVisitor{

		private final SamVisitorMemento expectedMemento;
		
		
		public AssertVisitorStartsAtMemento(SamVisitorMemento expectedMemento) {
			this.expectedMemento = expectedMemento;
		}

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			SamVisitorMemento actual = callback.createMemento();
			assertEquals(expectedMemento, actual);
			callback.haltParsing();
		}

		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void halted() {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Test
	public void skipToOffset() throws IOException {
		File bamfile = resources.getFile("index_test.bam");
		
		SamParser parser = SamParserFactory.create(bamfile);
		OffsetRecorder offsetRecorder = new OffsetRecorder();
		SamParserOptions options =  SamParserOptions.builder().createMementos(true).build();
		
		parser.parse(options, offsetRecorder);
		
		NameOffsetPair pair = offsetRecorder.pairs.get(1234);
		OffsetRecorder secondOffsetRecorder = new OffsetRecorder();
	
		parser.parse(options.toBuilder().memento(pair.getMemento()).build(), secondOffsetRecorder);
		
		offsetRecorder.assertMatchSince(secondOffsetRecorder, pair);
	}
	@Data
	private static class NameOffsetPair{
		
		private final String name;
		private final SamVisitorMemento memento;
		
	}
	private static class OffsetRecorder implements SamVisitor{

		private List<NameOffsetPair> pairs = new ArrayList<>();
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			// TODO Auto-generated method stub
			
		}

		public void assertMatchSince(OffsetRecorder secondOffsetRecorder, NameOffsetPair pair) {
			Iterator<NameOffsetPair> otherIter = secondOffsetRecorder.pairs.iterator();
			Iterator<NameOffsetPair> iter = pairs.iterator();
			//assert otherIter has this pair 1st
			assertEquals(pair, otherIter.next());
			
			boolean found=false;
			while(iter.hasNext()) {
				NameOffsetPair n = iter.next();
				if(pair.equals(n)) {
					found=true;
					break;
				}
			}
			if(!found) {
				throw new NoSuchElementException("could not find " + pair);
			}
			
			while(iter.hasNext() && otherIter.hasNext()) {
				NameOffsetPair a = iter.next();
				NameOffsetPair b = otherIter.next();
				
				if(!a.equals(b)) {
					throw new AssertionError(a + " does not match " + b);
				}
			}
			if(iter.hasNext() || otherIter.hasNext()) {
				throw new AssertionError("unequal length lists");
			}
			
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			pairs.add(new NameOffsetPair(record.getQueryName(), callback.createMemento()));	
		}

		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void halted() {
			// TODO Auto-generated method stub
			
		}
		
	}


	private BamIndex createIndexFromBam(File bam) throws IOException {
		BamIndexSamVisitor visitor = new BamIndexSamVisitor();
		SamParserFactory.create(bam).parse(visitor);
		return visitor.getBamIndex();
	}
	
	private static class BamIndexSamVisitor extends AbstractSamVisitor {
		BamIndexer indexer;
		

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			indexer = new BamIndexer(header);
		}
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			indexer.addRecord(record, start, end);
			
		}
	
		
		

		public BamIndex getBamIndex() {
			return indexer.createBamIndex();
		}

		
	}
}
