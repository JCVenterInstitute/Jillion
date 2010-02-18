/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.jcvi.util;

import org.junit.Assert;

import org.junit.Test;

public class TestStringUtilities {

    @Test
    public void testToCamelCase() 
    {
        Assert.assertEquals("Standard Title", "thisIsCamelCase", StringUtilities.toCamelCase("This is Camel Case").toString());
        Assert.assertEquals("Single Word", "test", StringUtilities.toCamelCase("Test").toString());
        Assert.assertEquals("Leading whitespace", "thisIsATest", StringUtilities.toCamelCase("   This is a Test").toString());
        Assert.assertEquals("Trailing whitespace", "thisIsATest", StringUtilities.toCamelCase("This is a Test   ").toString());
        Assert.assertEquals("Digits", "digit89Test", StringUtilities.toCamelCase("digit 89 test").toString());
        Assert.assertEquals("Standard Title (InitialCap)", "ThisIsCamelCase", StringUtilities.toCamelCase("This is Camel Case", true).toString());
        Assert.assertEquals("Leading whitespace (InitialCap)", "ThisIsATest", StringUtilities.toCamelCase("   This is a Test", true).toString());
        Assert.assertEquals("Trailing whitespace (InitialCap)", "ThisIsATest", StringUtilities.toCamelCase("This is a Test   ", true).toString());
    }

}
