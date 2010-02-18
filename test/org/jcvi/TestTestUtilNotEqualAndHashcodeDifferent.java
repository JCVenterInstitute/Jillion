/*
 * Created on Jul 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class TestTestUtilNotEqualAndHashcodeDifferent extends AbstractTestTestUtil{
    private static final String FAIL_MESSAGE =
        "if objects are equal or have same hashcode,"
        +" throw AssertionError";
    private static final class DifferentHashCodes{
        private static AtomicInteger counter= new AtomicInteger(0);
        /**
         * Always return true.
         */
        @Override
        public boolean equals(Object obj) {
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
           return counter.incrementAndGet();
        }

    }
    @Test
    public void sameReferenceShouldThrowAssertionError(){
        try{
            TestUtil.assertNotEqualAndHashcodeDifferent(STRING, STRING);
            fail(FAIL_MESSAGE);
        }
        catch(AssertionError pass){}
    }

    @Test
    public void sameValueShouldThrowAssertionError(){
        String sameValue = "a string";
        try{
            TestUtil.assertNotEqualAndHashcodeDifferent(STRING, sameValue);
            fail(FAIL_MESSAGE);
        }
        catch(AssertionError pass){}
    }
    @Test
    public void differentClass(){
        TestUtil.assertNotEqualAndHashcodeDifferent(STRING, ZERO);
    }


    @Test
    public void sameClassNotEqual(){
        TestUtil.assertNotEqualAndHashcodeDifferent(STRING, DIFF_STRING);
    }

    @Test
    public void differentEqualsButHashCodes(){
        try{
            TestUtil.assertNotEqualAndHashcodeDifferent(
                    new DifferentHashCodes(),
                    new DifferentHashCodes());
            fail(FAIL_MESSAGE);
        }
        catch(AssertionError pass){}
    }
}
