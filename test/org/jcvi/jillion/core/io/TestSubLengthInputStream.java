/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestSubLengthInputStream {
	
	
	@Parameters
	public static List<Object[]> data(){
		Random rand = new Random();
		int length= 100;
		byte[] bytes = new byte[length];	
		rand.nextBytes(bytes);
		
		return Arrays.asList(
				new Object[]{ sut(()->new SubLengthInputStream(new ByteArrayInputStream(bytes), length)), 
								expected(()->new ByteArrayInputStream(bytes))},
				//first half
				new Object[]{sut( ()->new SubLengthInputStream(new ByteArrayInputStream(bytes), 50)),
						expected(()->new ByteArrayInputStream(bytes,0, 50))},
				//second half
				new Object[]{sut( ()->new SubLengthInputStream(new ByteArrayInputStream(bytes, 51, 50), 50)),
						expected(()->new ByteArrayInputStream(bytes,51, 50))},
				//middle
				new Object[]{sut( ()->new SubLengthInputStream(new ByteArrayInputStream(bytes, 33, 33), 33)),
						expected(()->new ByteArrayInputStream(bytes,33, 33))},
				
				//partial
				new Object[]{sut( ()->new SubLengthInputStream(new ByteArrayInputStream(bytes, 33, 66), 33)),
						expected(()->new ByteArrayInputStream(bytes,33, 33))}
				);
		
	}
	/**
	 * Intent revealing method that says which lambda expression
	 * is our system under test; also helps Java 8 type inference
	 * so we don't get compiler errors.
	 * @param s the lambda to create a new InputStream.
	 * @return s
	 */
	private static Supplier<InputStream> sut(Supplier<InputStream> s){
		return s;
	}
	/**
	 * Intent revealing method that says which lambda expression
	 * is our expected InputStream in the test; also helps Java 8 type inference
	 * so we don't get compiler errors.
	 * @param s the lambda to create a new InputStream.
	 * @return s
	 */
	private static Supplier<InputStream> expected(Supplier<InputStream> s){
		return s;
	}
	
	private final Supplier<InputStream> sutSupplier, expectedSupplier;
	
	
	public TestSubLengthInputStream(Supplier<InputStream> sutSupplier, Supplier<InputStream> expectedSupplier) {
		this.sutSupplier = sutSupplier;
		this.expectedSupplier = expectedSupplier;
	}


	@Test
	public void read() throws IOException{
		
		
		try(InputStream sut = sutSupplier.get();
			InputStream expected = expectedSupplier.get()){
			int v;
			while( (v=expected.read()) != -1){
				assertEquals(v, sut.read());
			}
			assertEquals(-1, sut.read());
		}
		
	}
	
	@Test
	public void readAll() throws IOException{
		
		
		try(InputStream sut = sutSupplier.get();
			InputStream expected = expectedSupplier.get()){
			
			byte[] actualArray = new byte[1000];
			byte[] expectedArray = new byte[1000];
			int actualRead = sut.read(actualArray);
			int expectedRead = expected.read(expectedArray);
			
			assertEquals(expectedRead, actualRead);
			
			assertArrayEquals(expectedArray, actualArray);
		}
		
	}
	
	
	@Test
	public void multipleBulkReads() throws IOException{
		try(InputStream sut = sutSupplier.get();
			InputStream expected = expectedSupplier.get()){
			
			//go by 10s
			byte[] expectedBuf = new byte[10];
			byte[] actualBuf = new byte[10];
			
			int expectedRead;
			do{
				expectedRead = expected.read(expectedBuf);
				int actualRead = sut.read(actualBuf);
				assertEquals(expectedRead, actualRead);
				
				assertArrayEquals(expectedBuf, actualBuf);
			}while(expectedRead != -1);
		}
	}
}
