/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core.residue;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestFrame {

    @Test
    public void getNumberOfBasesSkipped() {
    	assertEquals(0, Frame.ONE.getNumberOfBasesSkipped());
    	assertEquals(1, Frame.TWO.getNumberOfBasesSkipped());
    	assertEquals(2, Frame.THREE.getNumberOfBasesSkipped());
    	
    	assertEquals(0, Frame.NEGATIVE_ONE.getNumberOfBasesSkipped());
    	assertEquals(1, Frame.NEGATIVE_TWO.getNumberOfBasesSkipped());
    	assertEquals(2, Frame.NEGATIVE_THREE.getNumberOfBasesSkipped());
    }
    @Test
    public void shitPositiveStrand(){
        assertEquals(Frame.TWO, Frame.ONE.shift(1));
        assertEquals(Frame.THREE, Frame.ONE.shift(2));
        assertEquals(Frame.ONE, Frame.ONE.shift(3));
        
        assertEquals(Frame.THREE, Frame.TWO.shift(1));
        assertEquals(Frame.ONE, Frame.TWO.shift(2));
        assertEquals(Frame.TWO, Frame.TWO.shift(3));
        
        assertEquals(Frame.ONE, Frame.THREE.shift(1));
        assertEquals(Frame.TWO, Frame.THREE.shift(2));
        assertEquals(Frame.THREE, Frame.THREE.shift(3));
        
        assertEquals(Frame.TWO, Frame.ONE.shift(4));
        assertEquals(Frame.THREE, Frame.ONE.shift(5));
        assertEquals(Frame.ONE, Frame.ONE.shift(6));
        
        assertEquals(Frame.THREE, Frame.TWO.shift(4));
        assertEquals(Frame.ONE, Frame.TWO.shift(5));
        assertEquals(Frame.TWO, Frame.TWO.shift(6));
        
        assertEquals(Frame.ONE, Frame.THREE.shift(4));
        assertEquals(Frame.TWO, Frame.THREE.shift(5));
        assertEquals(Frame.THREE, Frame.THREE.shift(6));
    }
    
    @Test
    public void shitNegativeStrand(){
        assertEquals(Frame.NEGATIVE_TWO, Frame.NEGATIVE_ONE.shift(1));
        assertEquals(Frame.NEGATIVE_THREE, Frame.NEGATIVE_ONE.shift(2));
        assertEquals(Frame.NEGATIVE_ONE, Frame.NEGATIVE_ONE.shift(3));
        
        assertEquals(Frame.NEGATIVE_THREE, Frame.NEGATIVE_TWO.shift(1));
        assertEquals(Frame.NEGATIVE_ONE, Frame.NEGATIVE_TWO.shift(2));
        assertEquals(Frame.NEGATIVE_TWO, Frame.NEGATIVE_TWO.shift(3));
        
        assertEquals(Frame.NEGATIVE_ONE, Frame.NEGATIVE_THREE.shift(1));
        assertEquals(Frame.NEGATIVE_TWO, Frame.NEGATIVE_THREE.shift(2));
        assertEquals(Frame.NEGATIVE_THREE, Frame.NEGATIVE_THREE.shift(3));
        
        assertEquals(Frame.NEGATIVE_TWO, Frame.NEGATIVE_ONE.shift(4));
        assertEquals(Frame.NEGATIVE_THREE, Frame.NEGATIVE_ONE.shift(5));
        assertEquals(Frame.NEGATIVE_ONE, Frame.NEGATIVE_ONE.shift(6));
        
        assertEquals(Frame.NEGATIVE_THREE, Frame.NEGATIVE_TWO.shift(4));
        assertEquals(Frame.NEGATIVE_ONE, Frame.NEGATIVE_TWO.shift(5));
        assertEquals(Frame.NEGATIVE_TWO, Frame.NEGATIVE_TWO.shift(6));
        
        assertEquals(Frame.NEGATIVE_ONE, Frame.NEGATIVE_THREE.shift(4));
        assertEquals(Frame.NEGATIVE_TWO, Frame.NEGATIVE_THREE.shift(5));
        assertEquals(Frame.NEGATIVE_THREE, Frame.NEGATIVE_THREE.shift(6));
    }
}
