package org.jcvi.jillion.core.util;

import org.jcvi.jillion.internal.core.util.GrowableBitArray;
import org.junit.Test;

import java.util.BitSet;
import static org.junit.Assert.*;
public class TestGrowableBitArray {

    @Test
    public void fromBitSet(){
        BitSet bs = new BitSet();

        bs.set(1);
        bs.set(5);
        bs.set(10);

        GrowableBitArray sut = new GrowableBitArray(bs);


        for(int i=0; i< 11; i++){
            if(i==1 || i == 5 || i == 10){
                assertTrue(sut.get(i));
            }else{
                assertFalse(sut.get(i));
            }
        }
    }

    @Test
    public void toBitSet(){
        BitSet bs = new BitSet();

        bs.set(1);
        bs.set(5);
        bs.set(10);

        GrowableBitArray sut = new GrowableBitArray(bs);
        assertEquals(bs, sut.asBitSet());
    }

    @Test
    public void append(){
        BitSet bs = new BitSet();

        bs.set(1);
        bs.set(5);
        bs.set(10);

        GrowableBitArray sut = new GrowableBitArray(bs);
        sut.append(true);

        BitSet expected = new BitSet();

        expected.set(1);
        expected.set(5);
        expected.set(10);
        expected.set(11);

        assertEquals(expected, sut.asBitSet());
    }

    @Test
    public void prepend(){
        BitSet bs = new BitSet();

        bs.set(1);
        bs.set(5);
        bs.set(10);

        GrowableBitArray sut = new GrowableBitArray(bs);
        sut.prepend(true);

        BitSet expected = new BitSet();
        expected.set(0);
        expected.set(2);
        expected.set(6);
        expected.set(11);

        assertEquals(expected, sut.asBitSet());
    }
}
