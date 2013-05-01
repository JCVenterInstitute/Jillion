package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestIOUtil_numberOfBitsIn {

	private final int value, expectedNumberOfBits;
	
	 @Parameters
    public static Collection<?> data(){
        List<Integer[]> data = new ArrayList<Integer[]>();
        data.add(new Integer[]{0,1}); //special case
        data.add(new Integer[]{1,1});
        data.add(new Integer[]{2,2});
        data.add(new Integer[]{3,2});
        data.add(new Integer[]{4,3});
        data.add(new Integer[]{5,3});
        data.add(new Integer[]{6,3});
        data.add(new Integer[]{7,3});
        data.add(new Integer[]{8,4});
        
        data.add(new Integer[]{100,7});
        data.add(new Integer[]{1024,11});
        return data;
 	}

	public TestIOUtil_numberOfBitsIn(int value, int expectedNumberOfBits) {
		this.value = value;
		this.expectedNumberOfBits = expectedNumberOfBits;
	}
	 
	@Test
	public void computeNumberOfBits(){
		assertEquals("value " + value, expectedNumberOfBits, IOUtil.computeNumberOfBitsIn(value));
	}
	 
	 

}
