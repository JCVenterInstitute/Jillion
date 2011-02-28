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
package org.jcvi;

import org.junit.Assert;

import org.junit.Test;

public class TestRangeDepartureComparator 
{
    private Range a;
    private Range b;
    private Range c;
    private Range d;
    private Range e;
    private Range f;
    private Range g;
    
    private Range.Comparators comp = Range.Comparators.DEPARTURE;
    
    public TestRangeDepartureComparator() 
    {
        super();
        
        this.a = Range.buildRange(10, 20);
        this.b = Range.buildRange(10, 18);
        this.c = Range.buildRange(12, 20);
        this.d = Range.buildRange(12, 18);
        this.e = Range.buildEmptyRange();
        this.f = Range.buildEmptyRange(-1);
        this.g = Range.buildEmptyRange(30);
    }
    
   
    
    @Test
    public void testCompare_inverseCommutativity() 
    {
        Range[] ranges = new Range[] { a, b, c, d, e, f };
        
        for (Range r1 : ranges )
        {
            for (Range r2 : ranges)
            {
                Assert.assertTrue("Failed inverse commutativity for " + r1 + " vs. " + r2,
                        this.comp.compare(a, c) == -(this.comp.compare(c, a)));
            }
        }
    }
    
    @Test
    public void testCompare_simple() 
    {
        Assert.assertEquals(1, this.comp.compare(a, d));
    }

    @Test
    public void testCompare_identicalStart() 
    {
        Assert.assertEquals(1, this.comp.compare(a, b));
    }
    
    @Test
    public void testCompare_identicalStop() 
    {
        Assert.assertEquals(-1, this.comp.compare(a, c));
    }

    @Test
    public void testCompare_defaultEmpty() 
    {
        Assert.assertEquals(1, this.comp.compare(a, e));
        Assert.assertEquals(-1, this.comp.compare(e, a));
    }

    @Test
    public void testCompare_negativeEmpty() 
    {
        Assert.assertEquals(1, this.comp.compare(a, f));
    }

    @Test
    public void testCompare_highConstantEmpty() 
    {
        Assert.assertEquals(-1, this.comp.compare(a, g));
    }

    @Test
    public void testCompare_emptyDefaultVsNegative() 
    {
        Assert.assertEquals(1, this.comp.compare(e, f));
    }

    @Test
    public void testCompare_emptyDefaultVsHighConstant() 
    {
        Assert.assertEquals(-1, this.comp.compare(e, g));
    }

    @Test
    public void testCompare_emptyNegativeVsHighConstant() 
    {
        Assert.assertEquals(-1, this.comp.compare(f, g));
    }

    @Test
    public void testCompare_self() 
    {
        Assert.assertEquals(0, this.comp.compare(a, a));
    }

    @Test
    public void testCompare_nullFirstParam() 
    {
        try
        {
            this.comp.compare(null, a);
        }
        catch (IllegalArgumentException e) 
        {
            return;
        }
        Assert.fail("No exception thrown for null parameter.");
    }

    @Test
    public void testCompare_nullLastParam() 
    {
        try
        {
            this.comp.compare(a, null);
        }
        catch (IllegalArgumentException e) 
        {
            return;
        }
        Assert.fail("No exception thrown for null parameter.");
    }

    @Test
    public void testCompare_nullBothParam() 
    {
        try
        {
            this.comp.compare(null, null);
        }
        catch (IllegalArgumentException e) 
        {
            return;
        }
        Assert.fail("No exception thrown for all null parameters.");
    }

}
