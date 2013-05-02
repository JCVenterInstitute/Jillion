package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestRandomAccessFileInputStream {

	private byte[] expectedBytes;
	
	
	public TestRandomAccessFileInputStream(){
		expectedBytes = new byte[1024];
		for(int i=0; i<expectedBytes.length; i++){
			expectedBytes[i] = (byte)(i%Byte.MAX_VALUE);
		}
	}
	@Rule
	public TemporaryFolder tempdir = new TemporaryFolder();
	
	
	
	@Test
	public void readFullyOneByteAtATime() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(raf);
			byte[] actual = new byte[expectedBytes.length];
			for(int i=0; i<actual.length; i++){
				actual[i] =(byte)sut.read();
			}
			assertArrayEquals(expectedBytes, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut,raf);
		}
		
	}
	
	@Test
	public void readFullyAllAtOnce() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(raf);
			byte[] actual = new byte[expectedBytes.length];
			sut.read(actual);
			assertArrayEquals(expectedBytes, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut,raf);
		}
		
	}
	@Test
	public void readFullyAllInLargeBlocks() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(raf);
			byte[] actual = new byte[expectedBytes.length];
			sut.read(actual,0,actual.length/2);
			sut.read(actual,actual.length/2,actual.length/2);
			assertArrayEquals(expectedBytes, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut,raf);
		}
		
	}
	
	@Test
	public void readFullyReadPastEOF() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(raf);
			byte[] actual = new byte[expectedBytes.length+2];
			assertEquals(expectedBytes.length, sut.read(actual));

			byte[] subArray = new byte[expectedBytes.length];
			System.arraycopy(actual, 0, subArray, 0, subArray.length);
			assertArrayEquals(expectedBytes, subArray);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut,raf);
		}
	}
	
	@Test
	public void legnthSetToEOFReadFullyOneByteAtATime() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(f,0,expectedBytes.length);
			byte[] actual = new byte[expectedBytes.length];
			for(int i=0; i<actual.length; i++){
				actual[i] =(byte)sut.read();
			}
			assertArrayEquals(expectedBytes, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void legnthSetToEOFReadFullyAllAtOnce() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(f,0,expectedBytes.length);
			byte[] actual = new byte[expectedBytes.length];
			assertEquals(expectedBytes.length,sut.read(actual));
			
			assertArrayEquals(expectedBytes, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void legnthSetToHalfReadFullyOneByteAtATime() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(f,0,expectedBytes.length/2);
			byte[] actual = new byte[expectedBytes.length/2];
			for(int i=0; i<actual.length; i++){
				actual[i] =(byte)sut.read();
			}
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, 0, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void legnthSetToHalfReadFullyAllAtOnce() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(f,0,expectedBytes.length/2);
			byte[] actual = new byte[expectedBytes.length/2];
			assertEquals(actual.length, sut.read(actual));
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, 0, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void legnthSetToHalfReadInChunks() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			sut = new RandomAccessFileInputStream(f,0,expectedBytes.length/2);
			byte[] actual = new byte[expectedBytes.length/2];
			assertEquals(actual.length/2, sut.read(actual,0,actual.length/2));
			assertEquals(actual.length/2, sut.read(actual,actual.length/2,actual.length/2));
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, 0, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void seektoMiddleReadTillEOF() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFileInputStream sut = null;
		try{
			int start = expectedBytes.length/4;
			sut = new RandomAccessFileInputStream(f,start);
			byte[] actual = new byte[expectedBytes.length - expectedBytes.length/4];
			for(int i=0; i<actual.length; i++){
				actual[i] =(byte)sut.read();
			}
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, start, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	
	@Test
	public void seektoMiddleReadMultipleChunksTillEOF() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFileInputStream sut = null;
		try{
			int start = expectedBytes.length/4;
			sut = new RandomAccessFileInputStream(f,start);
			byte[] actual = new byte[expectedBytes.length - expectedBytes.length/4];
			assertEquals(actual.length/2, sut.read(actual, 0, actual.length/2));
			assertEquals(actual.length/2, sut.read(actual, actual.length/2, actual.length/2));
			
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, start, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	
	@Test
	public void seekToMiddleAndOnlyReadPoritionReadFullyOneByteAtATime() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			int start = expectedBytes.length/4;
			sut = new RandomAccessFileInputStream(f,start,expectedBytes.length/4);
			byte[] actual = new byte[expectedBytes.length/4];
			for(int i=0; i<actual.length; i++){
				actual[i] =(byte)sut.read();
			}
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, start, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void seekToMiddleAndOnlyReadPoritionReadAllAtOnce() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			int start = expectedBytes.length/4;
			sut = new RandomAccessFileInputStream(f,start,expectedBytes.length/4);
			byte[] actual = new byte[expectedBytes.length/4];
			assertEquals(actual.length, sut.read(actual));
			
			byte[] subArray = new byte[actual.length];
			System.arraycopy(expectedBytes, start, subArray, 0, subArray.length);
			assertArrayEquals(subArray, actual);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	@Test
	public void seekToMiddleAndOnlyReadPoritionReadBeyondEOF() throws IOException{
		File f =createFileWithExpectedBytes();
	
		RandomAccessFileInputStream sut = null;
		try{
			int start = expectedBytes.length/4;
			sut = new RandomAccessFileInputStream(f,start,expectedBytes.length/4);
			byte[] actual = new byte[expectedBytes.length/4+10];
			assertEquals(expectedBytes.length/4, sut.read(actual));
			
			byte[] expectedSubArray = new byte[expectedBytes.length/4];
			System.arraycopy(expectedBytes, start, expectedSubArray, 0, expectedSubArray.length);
			
			byte[] actualSubArray = new byte[expectedBytes.length/4];
			
			System.arraycopy(actual, 0, actualSubArray, 0, actualSubArray.length);
			
			assertArrayEquals(expectedSubArray, actualSubArray);
			assertEquals(-1,sut.read());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
		
	}
	
	@Test
	public void dontOwnFileCloseKeepFileOpen() throws IOException{
		File f =createFileWithExpectedBytes();
		RandomAccessFile ras = new RandomAccessFile(f, "r");
		try{
			RandomAccessFileInputStream sut = new RandomAccessFileInputStream(ras);
			sut.close();
			assertEquals(expectedBytes[0],ras.read());
		}finally{
			ras.close();
		}
	}
	
	private File createFileWithExpectedBytes() throws IOException{
		File f =tempdir.newFile();
		FileOutputStream out = new FileOutputStream(f);
		out.write(expectedBytes);
		out.close();
		return f;
	}
}
