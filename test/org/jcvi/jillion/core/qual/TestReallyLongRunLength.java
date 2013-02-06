package org.jcvi.jillion.core.qual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
/**
 * Ravi noticed if a quality sequence
 * that is longer than {@link Short#MAX_VALUE}
 * will crash the run length encoder
 * since it will cause a negative count.
 * 
 * I have created a bug on sourceforge's Jillion
 * bug tracker.
 * 
 * 
 * @author dkatzel
 * @see <a href= "https://sourceforge.net/tracker/?func=detail&atid=1278566&aid=3603580&group_id=303297">
 * Jillion Bug Report</a>
 */
public class TestReallyLongRunLength {

	@Test
	public void testLongerThanShortMax(){
		byte[] quals = new byte[Short.MAX_VALUE +1];
		byte value = (byte)60;
		Arrays.fill(quals, value);
		
		QualitySequence seq = new QualitySequenceBuilder(quals).build();
		assertEquals(quals.length,seq.getLength());
		Iterator<PhredQuality> iter = seq.iterator();
		assertTrue(iter.hasNext());
		while(iter.hasNext()){
			assertEquals(value, iter.next().getQualityScore());
		}
	}
}
