package org.jcvi.common.core.symbol.qual;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncodedQualitySequence {

	static private byte guard = Byte.valueOf((byte)70);

	private static final byte[] QUALITIES_BYTES = new byte[]{10,20,30,40,40,40,40,40,40,50,6,guard,12,15,guard,guard,30};
    
    RunLengthEncodedQualityCodec CODEC = new RunLengthEncodedQualityCodec(guard);
    
    static List<PhredQuality> decodedValues = PhredQuality.valueOf(
            QUALITIES_BYTES);
   
    private byte[] encodedData;
    QualitySequence sut;
    public TestRunLengthEncodedQualitySequence(){
    	encodedData = CODEC.encode(QUALITIES_BYTES);
    	sut = new RunLengthEncodedQualitySequence(encodedData);
    }
	@Test
	public void get(){
		for(int i=0; i<QUALITIES_BYTES.length; i++){
			assertEquals(QUALITIES_BYTES[i], sut.get(i).getQualityScore());
		}
	}
	
	@Test
	public void iterator(){
		Iterator<PhredQuality> iter = sut.iterator();
		int i=0;
		while(iter.hasNext()){
			PhredQuality next = iter.next();
			assertEquals(QUALITIES_BYTES[i], next.getQualityScore());
			i++;
		}
		try{
			iter.next();
			fail("should throw NoSuchElementException if no more elements");
		}catch(NoSuchElementException expected){
			//expected
		}
	}
	
	@Test
	public void rangedIterator(){
		Range range = Range.of(2,12);
		Iterator<PhredQuality> iter = sut.iterator(range);
		int i=(int)range.getBegin();
		while(iter.hasNext()){
			PhredQuality next = iter.next();
			assertEquals(QUALITIES_BYTES[i], next.getQualityScore());
			i++;
		}
		assertEquals(i, range.getEnd()+1);
		try{
			iter.next();
			fail("should throw NoSuchElementException if no more elements");
		}catch(NoSuchElementException expected){
			//expected
		}
	}
	
	@Test
	public void equalsDifferentlyEncodedSequence(){
		QualitySequence notRunLengthEncoded = new EncodedQualitySequence(
				DefaultQualitySymbolCodec.INSTANCE, decodedValues);
		TestUtil.assertEqualAndHashcodeSame(sut, notRunLengthEncoded);
	}
}
