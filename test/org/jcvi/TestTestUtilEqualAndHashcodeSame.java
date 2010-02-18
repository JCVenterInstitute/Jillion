/*
 * Created on Jul 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

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
