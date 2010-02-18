/*
 * Created on Sep 26, 2008
 *
 * @author dkatzel
 */
package org.jcvi.sequence;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.jcvi.TestUtil;
import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.DefaultConfidence;
import org.junit.Test;
public class TestDefaultConfidence {

    private byte[] confidence = new byte[]{20,30,40,50};
    private byte[] differentConfidence = new byte[]{30,40,20,50};
    private Confidence sut = new DefaultConfidence(confidence);

    @Test
    public void constructor(){
        assertArrayEquals(confidence, sut.getData());
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        Confidence sameValues = new DefaultConfidence(confidence);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsWrongClass(){
        assertFalse(sut.equals("not a Confidence"));
    }

    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentValues(){
        Confidence differentValues = new DefaultConfidence(differentConfidence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void ByteBufferConstructor(){
        Confidence sameValues = new DefaultConfidence(ByteBuffer.wrap(confidence));
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void equalsByteBufferAtDifferentPositions(){
        final ByteBuffer buffer = ByteBuffer.wrap(confidence);
        Confidence sameValues = new DefaultConfidence(buffer);
        buffer.position(2);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }


}
