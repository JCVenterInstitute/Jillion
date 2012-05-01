/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.ace;

import java.util.Collection;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ContigBuilder;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceContigBuilderReAbacus {
    PhdInfo read1PhdInfo = createMock(PhdInfo.class);
   
    
    @Test
    public void abacus(){
        AceContigBuilderTestDouble sut =  new AceContigBuilderTestDouble("id",
                          "ACGT-----ACGT")
        
        .addRead("read1",   "GT-T---ACG", 2, Direction.FORWARD, Range.create(2,7), read1PhdInfo, 10)
        .addRead("read2", "ACGT--T--AC", 0, Direction.FORWARD, Range.create(2,8), read1PhdInfo, 10)
        .addRead("read3",    "T---T-ACGT", 3, Direction.FORWARD, Range.create(2,8), read1PhdInfo, 10);
        
        sut.getPlacedReadBuilder("read1").reAbacus(Range.create(2,6), "T");
        sut.getPlacedReadBuilder("read2").reAbacus(Range.create(4,8), "T");
        sut.getPlacedReadBuilder("read3").reAbacus(Range.create(1,5), "T");
        sut.getConsensusBuilder().delete(Range.create(4,8)).insert(4, "T");
           
        AceContig contig =sut.build();
        assertEquals("ACGTTACGT", contig.getConsensus().toString());
        AcePlacedRead read1 = contig.getRead("read1");
        assertEquals("GTTACG", read1.getNucleotideSequence().toString());
        assertEquals(7, read1.getGappedEndOffset());
        
        AcePlacedRead read2 = contig.getRead("read2");
        assertEquals("ACGTTAC", read2.getNucleotideSequence().toString());
        assertEquals(6, read2.getGappedEndOffset());
        
        AcePlacedRead read3 = contig.getRead("read3");
        assertEquals("TTACGT", read3.getNucleotideSequence().toString());
        assertEquals(8, read3.getGappedEndOffset());
    }
    
    @Test
    public void abacusAndShiftDownstreamReads(){
    	AceContigBuilderTestDouble sut =  new AceContigBuilderTestDouble("id",
                          "ACGT-----ACGT")
        
        .addRead("read1",   "GT-T---ACG", 2, Direction.FORWARD, Range.create(2,7), read1PhdInfo, 10)
        .addRead("read2", "ACGT--T--AC", 0, Direction.FORWARD, Range.create(2,8), read1PhdInfo, 10)
        .addRead("read3",    "T---T-ACGT", 3, Direction.FORWARD, Range.create(2,8), read1PhdInfo, 10)
        .addRead("read4",           "ACGT", 9, Direction.FORWARD, Range.create(2,4), read1PhdInfo, 10);
        
        sut.getPlacedReadBuilder("read1").reAbacus(Range.create(2,6), "T");
        sut.getPlacedReadBuilder("read2").reAbacus(Range.create(4,8), "T");
        sut.getPlacedReadBuilder("read3").reAbacus(Range.create(1,5), "T");
        sut.getConsensusBuilder().delete(Range.create(4,8)).insert(4, "T");
        sut.getPlacedReadBuilder("read4").shiftLeft(4);
           
        AceContig contig =sut.build();
        assertEquals("ACGTTACGT", contig.getConsensus().toString());
        AcePlacedRead read1 = contig.getRead("read1");
        assertEquals("GTTACG", read1.getNucleotideSequence().toString());
        assertEquals(7, read1.getGappedEndOffset());
        
        AcePlacedRead read2 = contig.getRead("read2");
        assertEquals("ACGTTAC", read2.getNucleotideSequence().toString());
        assertEquals(6, read2.getGappedEndOffset());
        
        AcePlacedRead read3 = contig.getRead("read3");
        assertEquals("TTACGT", read3.getNucleotideSequence().toString());
        assertEquals(8, read3.getGappedEndOffset());
        
        AcePlacedRead read4 = contig.getRead("read4");
        assertEquals("ACGT", read4.getNucleotideSequence().toString());
        assertEquals(5, read4.getGappedStartOffset());
        assertEquals(8, read4.getGappedEndOffset());
    }
    
 private static class AceContigBuilderTestDouble implements AceContigBuilder{
	 private final AceContigBuilder delegate;
	 
	 public AceContigBuilderTestDouble(String id, String consensus){
		 delegate =DefaultAceContig.createBuilder("id",
                 "ACGT-----ACGT");
	 }
	@Override
	public ContigBuilder<AcePlacedRead, AceContig> setContigId(String contigId) {
		delegate.setContigId(contigId);
		return this;
	}

	@Override
	public String getContigId() {
		return delegate.getContigId();
	}

	@Override
	public int numberOfReads() {
		return delegate.numberOfReads();
	}

	@Override
	public ContigBuilder<AcePlacedRead, AceContig> addRead(
			AcePlacedRead placedRead) {
		delegate.addRead(placedRead);
		return this;
	}

	@Override
	public ContigBuilder<AcePlacedRead, AceContig> addAllReads(
			Iterable<AcePlacedRead> reads) {
		delegate.addAllReads(reads);
		return this;
	}

	@Override
	public void removeRead(String readId) {
		delegate.removeRead(readId);
		
	}

	@Override
	public NucleotideSequenceBuilder getConsensusBuilder() {
		return delegate.getConsensusBuilder();
	}

	@Override
	public AceContig build() {
		return delegate.build();
	}

	@Override
	public AceContigBuilderTestDouble addRead(String readId,
			NucleotideSequence validBases, int offset, Direction dir,
			Range clearRange, PhdInfo phdInfo, int ungappedFullLength) {
		delegate.addRead(readId, validBases, offset, dir, clearRange, phdInfo, ungappedFullLength);
		return this;
	}
	public AceContigBuilderTestDouble addRead(String readId,
			String validBases, int offset, Direction dir,
			Range clearRange, PhdInfo phdInfo, int ungappedFullLength) {
		return addRead(readId, new NucleotideSequenceBuilder(validBases).build(), offset, dir, clearRange, phdInfo, ungappedFullLength);
	}
	@Override
	public AcePlacedReadBuilder getPlacedReadBuilder(String readId) {
		return delegate.getPlacedReadBuilder(readId);
	}

	@Override
	public Collection<AcePlacedReadBuilder> getAllPlacedReadBuilders() {
		return delegate.getAllPlacedReadBuilders();
	}

	@Override
	public AceContigBuilder setComplemented(boolean complemented) {
		delegate.setComplemented(complemented);
		return this;
	}
    	
    }
}
