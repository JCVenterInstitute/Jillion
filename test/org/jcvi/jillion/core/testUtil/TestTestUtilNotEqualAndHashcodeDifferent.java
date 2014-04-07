/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
