/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestSFFUtil_convertFlowgramValues {

    short encodedValue;
    float expectedFloat;


    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                {(short)0, 0F},
                {(short)12, 0.12F},
                {(short)112, 1.12F},
                {(short)5526, 55.26F},
                {(short)836, 8.36F},
                {(short)8, 0.08F},
        });
    }

    public TestSFFUtil_convertFlowgramValues(short encodedValue,
                                    float expectedFloat){
        this.encodedValue =encodedValue;
        this.expectedFloat =expectedFloat;
    }

    @Test
    public void convert(){
        assertEquals(expectedFloat, SffUtil.convertFlowgramValue(encodedValue),0);
    }

}
