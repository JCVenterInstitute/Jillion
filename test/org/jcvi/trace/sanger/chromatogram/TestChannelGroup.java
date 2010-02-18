/*
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;


import org.jcvi.TestUtil;
import org.jcvi.trace.sanger.chromatogram.Channel;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;
import org.jcvi.trace.sanger.chromatogram.DefaultChannelGroup;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestChannelGroup {


    private byte[] differentLengthQualities = new byte[]{40,40,40};
    private short[] differentLengthPositions = new short[]{100,200,300};

    private Channel aChannel = new Channel(new byte[]{20,20,20,20},
                                            new short[]{10,20,30,40});

    private Channel cChannel = new Channel(new byte[]{22,22,22,22},
                                            new short[]{11,21,31,41});

    private Channel gChannel = new Channel(new byte[]{30,30,30,30},
                                new short[]{15,25,35,45});

    private Channel tChannel = new Channel(new byte[]{40,40,40,40},
                                new short[]{17,27,37,47});

    private ChannelGroup sut = new DefaultChannelGroup(aChannel, cChannel, gChannel, tChannel);

    @Test
    public void constructor(){
        assertEquals(aChannel, sut.getAChannel());
        assertEquals(cChannel, sut.getCChannel());
        assertEquals(gChannel, sut.getGChannel());
        assertEquals(tChannel, sut.getTChannel());
    }

    @Test
    public void nullAChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(null, cChannel, gChannel, tChannel);
            fail("should throw NullPointerException when a channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullCChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(aChannel, null, gChannel, tChannel);
            fail("should throw NullPointerException when c channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }

    @Test
    public void nullGChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(aChannel, cChannel, null, tChannel);
            fail("should throw NullPointerException when g channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }

    @Test
    public void nullTChannelShouldThrowNullPointerException(){
        try{
            new DefaultChannelGroup(aChannel, cChannel, gChannel, null);
            fail("should throw NullPointerException when t channel is null");
        }
        catch(NullPointerException expected){
            assertEquals("channels can not be null", expected.getMessage());
        }
    }

    @Test
    public void AConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(differentLengthQualities,
                                        aChannel.getPositions().array());
            new DefaultChannelGroup(differentConfidenceLength, cChannel, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void CConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(differentLengthQualities,
                                        aChannel.getPositions().array());
            new DefaultChannelGroup(aChannel, differentConfidenceLength, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void GConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(differentLengthQualities,
                                        aChannel.getPositions().array());
            new DefaultChannelGroup(aChannel, cChannel, differentConfidenceLength, tChannel);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void TConfidenceDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(differentLengthQualities,
                                        aChannel.getPositions().array());
            new DefaultChannelGroup(aChannel, cChannel, gChannel, differentConfidenceLength);
            fail("should throw IllegalArgumentException when a channel confidence is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("confidences must all have the same length", expected.getMessage());
        }
    }
    @Test
    public void APositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(aChannel.getConfidence().getData(),
                                                differentLengthPositions);
            new DefaultChannelGroup(differentConfidenceLength, cChannel, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }
    @Test
    public void CPositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(aChannel.getConfidence().getData(),
                                                differentLengthPositions);
            new DefaultChannelGroup(aChannel,differentConfidenceLength, gChannel, tChannel);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }

    @Test
    public void GPositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(aChannel.getConfidence().getData(),
                                                differentLengthPositions);
            new DefaultChannelGroup(aChannel,gChannel,differentConfidenceLength, tChannel);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }


    @Test
    public void TPositionDifferentLengthShouldThrowIllegalArgumentException(){
        try{
            Channel differentConfidenceLength = new Channel(aChannel.getConfidence().getData(),
                                                differentLengthPositions);
            new DefaultChannelGroup(aChannel, cChannel, gChannel, differentConfidenceLength);
            fail("should throw IllegalArgumentException when a channel position is different length");
        }
        catch(IllegalArgumentException expected){
            assertEquals("positions must all have the same length", expected.getMessage());
        }
    }


    @Test
    public void testEqualsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a ChannelGroup"));
    }

    @Test
    public void equalsSameValues(){
        ChannelGroup sameValues = new DefaultChannelGroup(aChannel, cChannel, gChannel, tChannel);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }



    @Test
    public void notEqualsDifferentAChannel(){
        ChannelGroup differentAChannel = createGroupWithDifferentAChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentAChannel);
    }

    @Test
    public void notEqualsDifferentCChannel(){
        ChannelGroup differentCChannel = createGroupWithDifferentCChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCChannel);
    }

    @Test
    public void notEqualsDifferentGChannel(){
        ChannelGroup differentGChannel = createGroupWithDifferentGChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentGChannel);
    }

    @Test
    public void notEqualsDifferentTChannel(){
        ChannelGroup differentTChannel = createGroupWithDifferentTChannel();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentTChannel);
    }

    private ChannelGroup createGroupWithDifferentAChannel() {
        return new DefaultChannelGroup(cChannel, cChannel, gChannel, tChannel);
    }

    private ChannelGroup createGroupWithDifferentCChannel() {
        return new DefaultChannelGroup(aChannel, aChannel, gChannel, tChannel);
    }

    private ChannelGroup createGroupWithDifferentGChannel() {
        return new DefaultChannelGroup(aChannel, cChannel, aChannel, tChannel);
    }

    private ChannelGroup createGroupWithDifferentTChannel() {
        return new DefaultChannelGroup(aChannel, cChannel, gChannel, aChannel);
    }
}
