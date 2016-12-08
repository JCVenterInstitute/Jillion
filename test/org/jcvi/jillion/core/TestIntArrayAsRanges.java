package org.jcvi.jillion.core;


import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestIntArrayAsRanges extends AbstractTestAsRangeOpperations<int[]>{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    public TestIntArrayAsRanges() {
        super(Ranges::asRanges, Ranges::asRanges);
    }
    protected int[] createInput(GrowableIntArray array){
        return array.toArray();
    }
    protected int[] createInput(int...ints){
        return ints;
    }

    @Test
    public void unsortedArrayShouldthrowException(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must be sorted");
        Ranges.asRanges(new int[]{8,6,7,5,3,0,9});
    }
    
    @Test
    public void reverseSortedArrayShouldthrowException(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must be sorted");
        Ranges.asRanges(new int[]{9,8,7,6,5,4,3,2,1});
    }
}
