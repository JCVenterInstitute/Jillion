/*
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.math.BigInteger;

import org.jcvi.TestUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFCommonHeader;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultSFFCommonHeader {
    private BigInteger indexOffset=BigInteger.valueOf(100000L);
    private int indexLength = 2000;
    private int numberOfReads = 2000000;
    private short numberOfFlowsPerRead = 400;
    private String flow = "TCAGTCAGTCAG";
    private String keySequence = "TCAG";
    private short headerLength = 440;

    DefaultSFFCommonHeader sut = new DefaultSFFCommonHeader(indexOffset,  indexLength,
             numberOfReads,  numberOfFlowsPerRead,  flow,
             keySequence,  headerLength);

    @Test
    public void constructor(){
        assertEquals(indexOffset, sut.getIndexOffset());
        assertEquals(indexLength, sut.getIndexLength() );
        assertEquals(numberOfReads, sut.getNumberOfReads());
        assertEquals(numberOfFlowsPerRead, sut.getNumberOfFlowsPerRead());
        assertEquals(flow , sut.getFlow());
        assertEquals(keySequence , sut.getKeySequence());
        assertEquals(headerLength , sut.getHeaderLength());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsWrongClass(){
        assertFalse(sut.equals("not a DefaultSFFCommonHeader"));
    }

    @Test
    public void equalsSameValues(){
        DefaultSFFCommonHeader sameValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence,  headerLength);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentIndexOffset(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset.add(BigInteger.valueOf(1)),  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentIndexLength(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength+1,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentNumberOfReads(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads+1,  numberOfFlowsPerRead,  flow,
                                    keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentNumberOfFlowsPerReads(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  (short)(numberOfFlowsPerRead+1),  flow,
                                    keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentHeaderLength(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    keySequence,  (short)(headerLength+1));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentFlow(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  "not"+flow,
                                    keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullFlow(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  null,
                                    keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullKeySequence(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    null,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentKeySequence(){
        DefaultSFFCommonHeader differentValues = new DefaultSFFCommonHeader(
                                    indexOffset,  indexLength,
                                    numberOfReads,  numberOfFlowsPerRead,  flow,
                                    "not"+keySequence,  headerLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
