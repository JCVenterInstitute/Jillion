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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.PrivateDataImpl;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPrivateData {
    private static final byte[] DIFFERENT_DATA = new byte[]{1,2,3,4,5};
    private static final byte[] DATA = new byte[]{20,30,40, -20, -67,125};
    private PrivateData sut = new PrivateDataImpl(DATA);

    @Test
    public void constructor(){
        assertArrayEquals(DATA, sut.getBytes());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        PrivateData sameValues = new PrivateDataImpl(DATA);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not PrivateData"));
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsPrivateDataIsEmpty(){
        PrivateData nullData = new PrivateDataImpl(new byte[0]);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    @Test
    public void notEqualsPrivateDataIsDifferent(){
        PrivateData nullData = new PrivateDataImpl(DIFFERENT_DATA);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    
    
}
