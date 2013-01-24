package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.junit.Test;

public class TestOpenAwareInputStream {

	byte[] bytes = new byte[]{1,2,3,4,123,Byte.MAX_VALUE, Byte.MIN_VALUE, -1,5,10};
	
	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNPE(){
		new OpenAwareInputStream(null);
	}
	
	@Test
	public void readEntireStreamWithoutCheckingIsOpen() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		while(true){
			int b = sut.read();
			if(b ==-1){
				break;
			}
			out.write(b);
		}
		assertArrayEquals(bytes, out.toByteArray());
		//still open even though we see EOF
		assertTrue(sut.isOpen());
	}
	@Test
	public void readAndCheckIsOpenEachTime() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		while(sut.isOpen()){
			int b = sut.read();
			if(b ==-1){
				break;
			}
			out.write(b);
			
		}
		assertArrayEquals(bytes, out.toByteArray());
		//still open even though we see EOF
		assertTrue(sut.isOpen());
	}
	@Test
	public void readArrayEntireStreamWithoutCheckingIsOpen() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		while(true){
			byte[] temp = new byte[5];
			
			int bytesRead =sut.read(temp);
			if(bytesRead == -1){
				break;
			}
			out.write(temp, 0, bytesRead);
		}
		assertArrayEquals(bytes, out.toByteArray());
		//still open even though we see EOF
		assertTrue(sut.isOpen());
	}
	@Test
	public void readArrayEntireStreamInChunksWhileCheckingIsOpen() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		while(sut.isOpen()){
			byte[] temp = new byte[5];
			
			int bytesRead =sut.read(temp);
			if(bytesRead == -1){
				break;
			}
			out.write(temp, 0, bytesRead);
		}
		assertArrayEquals(bytes, out.toByteArray());
		//still open even though we see EOF
		assertTrue(sut.isOpen());
	}
	
	@Test
	public void closingStreamShouldMakeIsOpenReturnFalse() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		assertTrue(sut.isOpen());
		sut.close();
		assertFalse(sut.isOpen());
	}
}
