/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.ace;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jcvi.jillion.assembly.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.ace.AceContig;
import org.jcvi.jillion.assembly.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceContigBuilderReAbacus {
    PhdInfo phdInfo = new PhdInfo("traceName","phdName",new Date());
    
    @Test
    public void abacus(){
    	AceContigBuilder sut =  new AceContigBuilder("id",
                          "ACGT-----ACGT")
        
        .addRead("read1",   new NucleotideSequenceBuilder("GT-T---ACG").build(), 2, Direction.FORWARD, Range.of(2,7), phdInfo, 10)
        .addRead("read2", new NucleotideSequenceBuilder("ACGT--T--AC").build(), 0, Direction.FORWARD, Range.of(2,8), phdInfo, 10)
        .addRead("read3",    new NucleotideSequenceBuilder("T---T-ACGT").build(), 3, Direction.FORWARD, Range.of(2,8), phdInfo, 10);
        
        sut.getAssembledReadBuilder("read1").reAbacus(Range.of(2,6), asSequence("T"));
        sut.getAssembledReadBuilder("read2").reAbacus(Range.of(4,8), asSequence("T"));
        sut.getAssembledReadBuilder("read3").reAbacus(Range.of(1,5),asSequence("T"));
        sut.getConsensusBuilder().delete(Range.of(4,8)).insert(4, asSequence("T"));
           
        AceContig contig =sut.build();
        assertEquals("ACGTTACGT", contig.getConsensusSequence().toString());
        AceAssembledRead read1 = contig.getRead("read1");
        assertEquals("GTTACG", read1.getNucleotideSequence().toString());
        assertEquals(7, read1.getGappedEndOffset());
        
        AceAssembledRead read2 = contig.getRead("read2");
        assertEquals("ACGTTAC", read2.getNucleotideSequence().toString());
        assertEquals(6, read2.getGappedEndOffset());
        
        AceAssembledRead read3 = contig.getRead("read3");
        assertEquals("TTACGT", read3.getNucleotideSequence().toString());
        assertEquals(8, read3.getGappedEndOffset());
    }
    
    private NucleotideSequence asSequence(String bases) {
		
		return new NucleotideSequenceBuilder(bases).build();
	}

	@Test
    public void abacusAndShiftDownstreamReads(){
		AceContigBuilder sut =  new AceContigBuilder("id",
                          "ACGT-----ACGT")
        
        .addRead("read1",   new NucleotideSequenceBuilder("GT-T---ACG").build(), 2, Direction.FORWARD, Range.of(2,7), phdInfo, 10)
        .addRead("read2", new NucleotideSequenceBuilder("ACGT--T--AC").build(), 0, Direction.FORWARD, Range.of(2,8), phdInfo, 10)
        .addRead("read3",    new NucleotideSequenceBuilder("T---T-ACGT").build(), 3, Direction.FORWARD, Range.of(2,8), phdInfo, 10)
        .addRead("read4",          new NucleotideSequenceBuilder("ACGT").build(), 9, Direction.FORWARD, Range.of(2,4), phdInfo, 10);
        
        sut.getAssembledReadBuilder("read1").reAbacus(Range.of(2,6), asSequence("T"));
        sut.getAssembledReadBuilder("read2").reAbacus(Range.of(4,8), asSequence("T"));
        sut.getAssembledReadBuilder("read3").reAbacus(Range.of(1,5), asSequence("T"));
        sut.getConsensusBuilder().delete(Range.of(4,8)).insert(4, asSequence("T"));
        sut.getAssembledReadBuilder("read4").shift(-4);
           
        AceContig contig =sut.build();
        assertEquals("ACGTTACGT", contig.getConsensusSequence().toString());
        AceAssembledRead read1 = contig.getRead("read1");
        assertEquals("GTTACG", read1.getNucleotideSequence().toString());
        assertEquals(7, read1.getGappedEndOffset());
        
        AceAssembledRead read2 = contig.getRead("read2");
        assertEquals("ACGTTAC", read2.getNucleotideSequence().toString());
        assertEquals(6, read2.getGappedEndOffset());
        
        AceAssembledRead read3 = contig.getRead("read3");
        assertEquals("TTACGT", read3.getNucleotideSequence().toString());
        assertEquals(8, read3.getGappedEndOffset());
        
        AceAssembledRead read4 = contig.getRead("read4");
        assertEquals("ACGT", read4.getNucleotideSequence().toString());
        assertEquals(5, read4.getGappedStartOffset());
        assertEquals(8, read4.getGappedEndOffset());
    }
    
 
}
