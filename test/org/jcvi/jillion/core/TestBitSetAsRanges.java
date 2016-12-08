package org.jcvi.jillion.core;

import java.util.BitSet;

import org.jcvi.jillion.internal.core.util.GrowableIntArray;

public class TestBitSetAsRanges extends AbstractTestAsRangeOpperations<BitSet>{

    public TestBitSetAsRanges() {
        super(Ranges::asRanges, Ranges::asRanges);
    }
    protected BitSet createInput(GrowableIntArray array){
        return createInput(array.toArray());
    }
    protected BitSet createInput(int...ints){
        BitSet bs = new BitSet();
        for(int i=0; i< ints.length;i++){
            bs.set(ints[i]);
        }
        
        return bs;
    }
}
