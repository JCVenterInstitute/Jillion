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
