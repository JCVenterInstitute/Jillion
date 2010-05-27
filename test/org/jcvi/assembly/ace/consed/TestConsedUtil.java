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

package org.jcvi.assembly.ace.consed;

import org.jcvi.assembly.ace.ConsensusAceTag;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtil {

    private static final String ACE_GAPPED_BASECALLS =      "ACGT*ACGT**ACGT*ACGGT***AAGT";
    private static final String CONTIG_GAPPED_BASECALLS =   "ACGT-ACGT--ACGT-ACGGT---AAGT";
    
    @Test
    public void isContigRename(){
        ConsensusAceTag tag = createMock(ConsensusAceTag.class);
        expect(tag.getType()).andReturn("contigName");
        replay(tag);
        assertTrue(ConsedUtil.isContigRename(tag));
        verify(tag);
    }
    @Test
    public void isNotContigRename(){
        ConsensusAceTag tag = createMock(ConsensusAceTag.class);
        expect(tag.getType()).andReturn("somethingCompletelyDifferent");
        replay(tag);
        assertFalse(ConsedUtil.isContigRename(tag));
        verify(tag);
    }
    
    @Test
    public void convertAceGapsToContigGaps(){
        assertEquals(CONTIG_GAPPED_BASECALLS, ConsedUtil.convertAceGapsToContigGaps(ACE_GAPPED_BASECALLS));
    }
    @Test
    public void convertContigGapsToAceGaps(){
        assertEquals(ACE_GAPPED_BASECALLS, ConsedUtil.convertContigGapstoAceGaps(CONTIG_GAPPED_BASECALLS));
    }
    
    @Test
    public void getRenamedContigId(){
        String newId = "NewId";
        ConsensusAceTag tag = createMock(ConsensusAceTag.class);
        expect(tag.getType()).andReturn("contigName");
        expect(tag.getData()).andReturn("U"+newId);
        replay(tag);
        assertEquals(newId,ConsedUtil.getRenamedContigId(tag));
        verify(tag);
    }
    @Test
    public void getRenamedContigIdNotContigRenameTagShouldThrowIllegalArgumentException(){
        ConsensusAceTag tag = createMock(ConsensusAceTag.class);
        expect(tag.getType()).andReturn("somethingCompletelyDifferent");
        replay(tag);
        try{
            ConsedUtil.getRenamedContigId(tag);
            fail("should throw new IllegalArgumentException if given tag is not a rename");
        }catch(IllegalArgumentException e){
            assertEquals("not a contig rename", e.getMessage());
        }
        verify(tag);
    }
    @Test
    public void getRenamedContigIdNotValidContigRenameTagShouldThrowIllegalArgumentException(){
        ConsensusAceTag tag = createMock(ConsensusAceTag.class);
        expect(tag.getType()).andReturn("contigName");
        expect(tag.getData()).andReturn("invalid rename");
        replay(tag);
        try{
            ConsedUtil.getRenamedContigId(tag);
            fail("should throw new IllegalArgumentException if given tag is not a valid rename");
        }catch(IllegalArgumentException e){
            assertEquals("consensus tag does not contain rename info : "+tag, e.getMessage());
        }
        verify(tag);
    }
}
