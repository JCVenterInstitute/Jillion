/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
