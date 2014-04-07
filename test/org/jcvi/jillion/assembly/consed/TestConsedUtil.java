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
package org.jcvi.jillion.assembly.consed;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jcvi.jillion.assembly.consed.ConsedUtil.ClipPointsType;
import org.jcvi.jillion.assembly.consed.ace.ConsensusAceTag;
import org.junit.Test;
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
    
    @Test
    public void clipPointTypeAllLowQuality(){
    	assertEquals(ClipPointsType.ALL_LOW_QUALITY, ClipPointsType.getType(-1, -1, 0, 50));
    }
    
    @Test
    public void clipPointTypeAllNegativeValidRange(){
    	assertEquals(ClipPointsType.NEGATIVE_VALID_RANGE, ClipPointsType.getType(20, 10, 0, 50));
    }
    
    @Test
    public void clipPointTypeNoIntersectionOfGoodQualityAndAlignRange(){
    	assertEquals(ClipPointsType.NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION, ClipPointsType.getType(634,649, 851, 1758));
    }
    
    @Test
    public void clipPointTypeNegativeAlignRange(){
    	assertEquals(ClipPointsType.NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION, ClipPointsType.getType(16, 219, -1, -1 ));
    }
    
}
