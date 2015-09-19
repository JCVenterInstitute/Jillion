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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
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
