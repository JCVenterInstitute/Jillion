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
/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.core.util.RunLength;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLength {

    String value = "string value";
    int length = 10;
    
    RunLength<String> sut = new RunLength<String>(value, length);
    @Test
    public void constructor(){
        assertEquals(value, sut.getValue());
        assertEquals(length, sut.getLength());
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a RunLength"));
    }
    
    @Test
    public void equalsSameValue(){
        RunLength<String> sameValues = new RunLength<String>(value, length);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentLengthShouldNotEqual(){
        RunLength<String> differentLength = new RunLength<String>(value, length+1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentLength);
    }
    @Test
    public void differentValueShouldNotEqual(){
        RunLength<String> differentValue = new RunLength<String>("different"+value, length);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValue);
    }
    @Test
    public void nullValueShouldNotEqual(){
        RunLength<String> nullValue = new RunLength<String>(null, length);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullValue);
    }
}
