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
import org.jcvi.jillion.assembly.clc.cas.PhaseChangeCasAlignmentRegion;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhaseChangeCasAlignmentRegion {

    PhaseChangeCasAlignmentRegion sut = new PhaseChangeCasAlignmentRegion((byte)1);
    @Test
    public void getType(){
        assertEquals( CasAlignmentRegionType.PHASE_CHANGE, sut.getType());
    }
    @Test
    public void getPhaseChange(){
        assertEquals((byte)1, sut.getPhaseChange());
    }
    
    @Test
    public void sameReferenceShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesShouldBeEqual(){
        PhaseChangeCasAlignmentRegion same = new PhaseChangeCasAlignmentRegion((byte)1);        
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualToDifferentClass(){
        assertFalse(sut.equals("not a phase change"));
    }
    
    @Test
    public void differentValueShouldNotBeEqual(){
        PhaseChangeCasAlignmentRegion different = new PhaseChangeCasAlignmentRegion((byte)2);        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
}
