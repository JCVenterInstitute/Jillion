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
package org.jcvi.jillion.assembly.clc.cas.align;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.align.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.align.DefaultCasAlignment;
import org.jcvi.jillion.assembly.clc.cas.align.DefaultCasAlignmentRegion;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultCasAlignment {

    long contigSequenceId = 1234l;
    long startOfMatch = 50;
    boolean isReadReversed=false;
    
    DefaultCasAlignment sut = new DefaultCasAlignment.Builder(contigSequenceId, startOfMatch, isReadReversed)
                                .addRegion(CasAlignmentRegionType.INSERT, 10)
                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 7)
                                .build();
    
    @Test
    public void getStartOfMatch(){
        assertEquals(startOfMatch,sut.getStartOfMatch());
    }
    
    @Test
    public void getContigSequenceId(){
        assertEquals(contigSequenceId,sut.contigSequenceId());
    }
    
    @Test
    public void readReversed(){
        assertEquals(isReadReversed,sut.readIsReversed());
    }
    @Test
    public void regions(){
        List<CasAlignmentRegion> expected = new ArrayList<CasAlignmentRegion>();
        expected.add(new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, 10));
        expected.add(new DefaultCasAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 7));
        assertEquals(expected, sut.getAlignmentRegions());
    }
    
    @Test
    public void copy(){
        DefaultCasAlignment copy = new DefaultCasAlignment.Builder(sut)
                                    .build();
        TestUtil.assertEqualAndHashcodeSame(sut, copy);
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void differentContigSequenceIdShouldNotBeEqual(){
        DefaultCasAlignment different = new DefaultCasAlignment.Builder(contigSequenceId +1, startOfMatch, isReadReversed)
                                    .addRegion(CasAlignmentRegionType.INSERT, 10)
                                    .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 7)
                                    .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void differentStartOfMatchShouldNotBeEqual(){
        DefaultCasAlignment different = new DefaultCasAlignment.Builder(contigSequenceId , startOfMatch+1, isReadReversed)
                                    .addRegion(CasAlignmentRegionType.INSERT, 10)
                                    .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 7)
                                    .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void differentReadDirectionShouldNotBeEqual(){
        DefaultCasAlignment different = new DefaultCasAlignment.Builder(contigSequenceId , startOfMatch, !isReadReversed)
                                    .addRegion(CasAlignmentRegionType.INSERT, 10)
                                    .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 7)
                                    .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentNumberOfRegionsShouldNotBeEqual(){
        DefaultCasAlignment different = new DefaultCasAlignment.Builder(sut)
                                    .addRegion(CasAlignmentRegionType.INSERT, 4)
                                    .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
}
