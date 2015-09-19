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
 * Created on Jul 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.testUtil;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestTestUtilEqualAndHashcodeSame extends AbstractTestTestUtil{

    private static final String FAIL_MESSAGE =
                "if objects are not equal or don't have same hashcode,"
                +" throw AssertionError";

    @Test
    public void sameReference(){
        TestUtil.assertEqualAndHashcodeSame(STRING, STRING);
    }
    @Test
    public void sameValue(){
        String sameValue = "a string";
        TestUtil.assertEqualAndHashcodeSame(STRING, sameValue);
    }
    @Test
    public void differentClassShouldThrowAssertionError(){
        try{
            TestUtil.assertEqualAndHashcodeSame(STRING, ZERO);
            fail(FAIL_MESSAGE);
        }
        catch(AssertionError pass){}
    }

    @Test
    public void sameClassNotEqualShouldThrowAssertionError(){
        try{
            TestUtil.assertEqualAndHashcodeSame(STRING, DIFF_STRING);
            fail(FAIL_MESSAGE);
        }
        catch(AssertionError pass){}
    }
}
