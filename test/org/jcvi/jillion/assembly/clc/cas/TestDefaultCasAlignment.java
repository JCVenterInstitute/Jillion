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
package org.jcvi.jillion.assembly.clc.cas;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.DefaultCasAlignment;
import org.jcvi.jillion.assembly.clc.cas.DefaultCasAlignmentRegion;
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
        assertEquals(contigSequenceId,sut.getReferenceIndex());
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
