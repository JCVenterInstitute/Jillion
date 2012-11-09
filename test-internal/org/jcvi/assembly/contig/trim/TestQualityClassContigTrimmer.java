package org.jcvi.assembly.contig.trim;

import java.util.Collections;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.glyph.qualClass.QualityClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestQualityClassContigTrimmer {

	private final int maxNumberOfBasesToTrimOn5prime=15;
	private final int  maxNumberOfBasesToTrimOn3primer=13;
	QualityClassContigTrimmer sut = new QualityClassContigTrimmer(maxNumberOfBasesToTrimOn5prime, maxNumberOfBasesToTrimOn3primer,Collections.<QualityClass>emptySet());
	
	NucleotideSequence referenceSequence = new NucleotideSequenceBuilder("ACGT").build();
	@Test
	public void computeNewValidRangeFor3primeTrimmedUngappedForwardRead(){
		Range oldValidRange = Range.of(10,13);
		ReadInfo readInfo = new ReadInfo(oldValidRange, 20);
		ReferenceMappedNucleotideSequence seq = new NucleotideSequenceBuilder("ACGA")
													.setReferenceHint(referenceSequence, 0)
													.buildReferenceEncodedNucleotideSequence();
		
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getNucleotideSequence()).andStubReturn(seq);
		expect(read.getDirection()).andStubReturn(Direction.FORWARD);
		expect(read.getReadInfo()).andStubReturn(readInfo);
		replay(read);
		
		Range newValidRange = sut.computeNewValidRange(read, 3);
		assertNotNull(newValidRange);
		assertEquals(new Range.Builder(oldValidRange)
						.contractEnd(1)
						.build(), 
						newValidRange);
		
	}
	
	@Test
	public void computeNewValidRangeFor5primeTrimmedUngappedForwardRead(){
		Range oldValidRange = Range.of(10,13);
		ReadInfo readInfo = new ReadInfo(oldValidRange, 20);
		ReferenceMappedNucleotideSequence seq = new NucleotideSequenceBuilder("GCGT")
													.setReferenceHint(referenceSequence, 0)
													.buildReferenceEncodedNucleotideSequence();
		
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getNucleotideSequence()).andStubReturn(seq);
		expect(read.getDirection()).andStubReturn(Direction.FORWARD);
		expect(read.getReadInfo()).andStubReturn(readInfo);
		replay(read);
		
		Range newValidRange = sut.computeNewValidRange(read, 0);
		assertNotNull(newValidRange);
		assertEquals(new Range.Builder(oldValidRange)
						.contractBegin(1)
						.build(),
						newValidRange);
		
	}
	
	@Test
	public void tooMuchToTrimShouldReturnNullValidRange5primeForwardRead(){
		Range oldValidRange = Range.of(maxNumberOfBasesToTrimOn5prime,maxNumberOfBasesToTrimOn5prime+4);
		ReadInfo readInfo = new ReadInfo(oldValidRange, 20);
		ReferenceMappedNucleotideSequence seq = new NucleotideSequenceBuilder("GCGT")
													.setReferenceHint(referenceSequence, 0)
													.buildReferenceEncodedNucleotideSequence();
		
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getNucleotideSequence()).andStubReturn(seq);
		expect(read.getDirection()).andStubReturn(Direction.FORWARD);
		expect(read.getReadInfo()).andStubReturn(readInfo);
		replay(read);
		
		Range newValidRange = sut.computeNewValidRange(read, 0);
		assertNull(newValidRange);
		
	}
	
	@Test
	public void computeNewValidRangeFor3primeTrimmedUngappedReverseRead(){
		Range oldValidRange = Range.of(10,13);
		ReadInfo readInfo = new ReadInfo(oldValidRange, 20);
		ReferenceMappedNucleotideSequence seq = new NucleotideSequenceBuilder("ACGA")
													.setReferenceHint(referenceSequence, 0)
													.buildReferenceEncodedNucleotideSequence();
		
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getNucleotideSequence()).andStubReturn(seq);
		expect(read.getDirection()).andStubReturn(Direction.REVERSE);
		expect(read.getReadInfo()).andStubReturn(readInfo);
		replay(read);
		
		Range newValidRange = sut.computeNewValidRange(read, 3);
		assertNotNull(newValidRange);
		assertEquals(Range.of(11,13), newValidRange);
		
	}
	
	@Test
	public void tooMuchToTrimShouldReturnNullValidRange3primeTrimmedUngappedReverseRead(){
		Range oldValidRange = Range.of(maxNumberOfBasesToTrimOn3primer,maxNumberOfBasesToTrimOn3primer+4);
		ReadInfo readInfo = new ReadInfo(oldValidRange, 20);
		ReferenceMappedNucleotideSequence seq = new NucleotideSequenceBuilder("ACGA")
													.setReferenceHint(referenceSequence, 0)
													.buildReferenceEncodedNucleotideSequence();
		
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getNucleotideSequence()).andStubReturn(seq);
		expect(read.getDirection()).andStubReturn(Direction.REVERSE);
		expect(read.getReadInfo()).andStubReturn(readInfo);
		replay(read);
		
		Range newValidRange = sut.computeNewValidRange(read, 3);
		assertNull(newValidRange);
		
	}
	
	@Test
	public void computeNewValidRangeFor5primeTrimmedUngappedReverseRead(){
		Range oldValidRange = Range.of(10,13);
		ReadInfo readInfo = new ReadInfo(oldValidRange, 20);
		ReferenceMappedNucleotideSequence seq = new NucleotideSequenceBuilder("GCGT")
													.setReferenceHint(referenceSequence, 0)
													.buildReferenceEncodedNucleotideSequence();
		
		AssembledRead read = createMock(AssembledRead.class);
		expect(read.getNucleotideSequence()).andStubReturn(seq);
		expect(read.getDirection()).andStubReturn(Direction.REVERSE);
		expect(read.getReadInfo()).andStubReturn(readInfo);
		replay(read);
		
		Range newValidRange = sut.computeNewValidRange(read, 0);
		assertNotNull(newValidRange);
		assertEquals(Range.of(10,12), newValidRange);
		
	}
}
