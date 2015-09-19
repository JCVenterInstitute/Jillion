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
