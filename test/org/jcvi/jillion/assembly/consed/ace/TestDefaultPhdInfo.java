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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestDefaultPhdInfo {

    Date date = new Date(123456789L);
    Date differentDate = new Date(0L);
    String phdName = "phdName";
    String traceName = "traceName";
    PhdInfo sut = new PhdInfo(traceName, phdName, date);
    @Test
    public void constructor(){
        assertEquals(phdName, sut.getPhdName());
        assertEquals(traceName, sut.getTraceName());
        assertEquals(date, sut.getPhdDate());
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a phdinfo"));
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        PhdInfo sameValues = new PhdInfo(traceName, phdName, date);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentTraceNameShouldNotBeEqual(){
        PhdInfo differentTraceName = new PhdInfo("different"+traceName, phdName, date);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentTraceName);
    }
    
    @Test
    public void nullTraceNameShouldThrowNPE(){
    	try{
        new PhdInfo(null, phdName, date);
    	}catch(NullPointerException e){
    		assertTrue(e.getMessage().contains("trace name"));
    	}
    }
    
    @Test
    public void differentPhdNameShouldNotBeEqual(){
        PhdInfo differentPhdName = new PhdInfo(traceName, "different"+phdName, date);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentPhdName);
    }
    
    @Test
    public void nullPhdNameShouldNotBeEqual(){        
        try{
            new PhdInfo(traceName, null, date);
    	}catch(NullPointerException e){
    		assertTrue(e.getMessage().contains("phd name"));
    	}
    }
    
    @Test
    public void differentDateShouldNotBeEqual(){
        PhdInfo hasDifferentDate = new PhdInfo(traceName, phdName, differentDate);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentDate);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullDateShouldThrowNPE(){
        new PhdInfo(traceName, phdName, null);
    }
}
