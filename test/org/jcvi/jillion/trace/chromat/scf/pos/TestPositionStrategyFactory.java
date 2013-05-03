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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.pos;

import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.BytePositionStrategy;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.PositionStrategy;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.PositionStrategyFactory;
import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.ShortPositionStrategy;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestPositionStrategyFactory {

    SCFHeader mockHeader;
    @Before
    public void setupHeader(){
        mockHeader = createMock(SCFHeader.class);
    }
    @Test
    public void getPositionStrategyByIntShouldReturnByte(){
        PositionStrategy actual =PositionStrategyFactory.getPositionStrategy(Byte.MAX_VALUE);
        assertTrue(actual instanceof BytePositionStrategy);
    }
    @Test
    public void getPositionStrategyByIntShouldReturnShort(){
        PositionStrategy actual =PositionStrategyFactory.getPositionStrategy(Byte.MAX_VALUE+1);
        assertTrue(actual instanceof ShortPositionStrategy);
    }

    @Test
    public void getPositionStrategyByIntTooBigShouldThrowIllegalArgumentException(){
        final int tooBig = Integer.MAX_VALUE;
        try{
            PositionStrategyFactory.getPositionStrategy(tooBig);
            fail("should throw IllegalArugmentException when max value > short max");
        }catch(IllegalArgumentException expected){
            assertEquals("no Position Strategy implementation available for max value "+tooBig,
                    expected.getMessage());
        }

    }
    @Test
    public void getPositionStrategyByHeaderSampleSize1ShouldReturnByte(){
        expect(mockHeader.getSampleSize()).andReturn((byte)1);
        replay(mockHeader);
        PositionStrategy actual =PositionStrategyFactory.getPositionStrategy(mockHeader);
        assertTrue(actual instanceof BytePositionStrategy);
        verify(mockHeader);
    }

    @Test
    public void getPositionStrategyByHeaderSampleSize2ShouldReturnShort(){
        expect(mockHeader.getSampleSize()).andReturn((byte)2);
        replay(mockHeader);
        PositionStrategy actual =PositionStrategyFactory.getPositionStrategy(mockHeader);
        assertTrue(actual instanceof ShortPositionStrategy);
        verify(mockHeader);
    }
    @Test
    public void getPositionStrategyByHeaderSampleSizeTooBigShouldThrowIllegalArgumentException(){
        expect(mockHeader.getSampleSize()).andReturn((byte)3);
        replay(mockHeader);
        try{
            PositionStrategyFactory.getPositionStrategy(mockHeader);
            fail("should throw illegalArugmentException if sample size >2");
        }catch(IllegalArgumentException expected){
            assertEquals("no Position Strategy implementation available for sample size "+(byte)3,
            expected.getMessage());
        }

        verify(mockHeader);
    }
}
