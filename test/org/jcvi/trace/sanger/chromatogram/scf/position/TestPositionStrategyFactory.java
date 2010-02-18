/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.position;

import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.position.BytePositionStrategy;
import org.jcvi.trace.sanger.chromatogram.scf.position.PositionStrategy;
import org.jcvi.trace.sanger.chromatogram.scf.position.PositionStrategyFactory;
import org.jcvi.trace.sanger.chromatogram.scf.position.ShortPositionStrategy;
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
