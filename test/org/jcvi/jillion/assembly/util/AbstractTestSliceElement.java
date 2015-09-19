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
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSliceElement {

    String id = "id";
    Nucleotide base = Nucleotide.Adenine;
    PhredQuality quality = PhredQuality.valueOf(50);
    Direction dir = Direction.FORWARD;
    SliceElement sut;
    protected abstract SliceElement create(String id, Nucleotide base, PhredQuality qual,Direction dir);
    
    @Before
    public void setup(){
        sut = create(id,base, quality,dir);
    }
    @Test
    public void constructor(){       
        assertEquals(id, sut.getId());
        assertEquals(base, sut.getBase());
        assertEquals(quality, sut.getQuality());
        assertEquals(dir, sut.getDirection());
    }
    
    @Test
    public void equalsSameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void equalsSameValuesShouldBeEqual(){
        SliceElement sameValues = create(id,base, quality,dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentIdShouldNotBeEqual(){
        SliceElement differentValues = create("different"+id,base, quality,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    
    @Test
    public void differentBaseShouldNotBeEqual(){
        SliceElement differentValues = create(id,Nucleotide.Cytosine, quality,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void differentQualityShouldNotBeEqual(){
        SliceElement differentValues = create(id,base, PhredQuality.valueOf(10),dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void differentDirectionShouldNotBeEqual(){
        SliceElement differentValues = create(id,base, quality,Direction.REVERSE);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    
}
