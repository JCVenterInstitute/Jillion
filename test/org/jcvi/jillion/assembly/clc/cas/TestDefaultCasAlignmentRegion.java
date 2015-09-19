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

import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.DefaultCasAlignmentRegion;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultCasAlignmentRegion {
    
    long length = 12345L;
    
    DefaultCasAlignmentRegion sut = new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, length);
    @Test
    public void constructor(){
        assertEquals(length, sut.getLength());
        assertEquals(CasAlignmentRegionType.INSERT, sut.getType());
    }
    
    @Test(expected = NullPointerException.class)
    public void nullTypeShouldThrowNPE(){
        new DefaultCasAlignmentRegion(null, length);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void negativeLengthShouldThrowIllegalArgumentException(){
        new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, -1);
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        DefaultCasAlignmentRegion same = new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, length);
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    
    @Test
    public void differentLengthShouldNotBeEqual(){
        DefaultCasAlignmentRegion differentLength = new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, length+10);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentLength);
    }
    
    @Test
    public void differentTypeShouldNotBeEqual(){
        DefaultCasAlignmentRegion differentType = new DefaultCasAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, length);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentType);
    }
    @Test
    public void differentClassShouldNotBeEqual(){
        assertFalse(sut.equals("not a alignment region"));
    }
    
    @Test
    public void shouldNotBeEqualToNull(){
        assertFalse(sut.equals(null));
    }
}
