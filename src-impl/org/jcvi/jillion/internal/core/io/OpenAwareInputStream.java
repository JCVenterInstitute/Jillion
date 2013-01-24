package org.jcvi.jillion.internal.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
/**
 * {@code OpenAwareInputStream} is an {@link InputStream}
 * that has an extra method
 * {@link #isOpen()}.  Marking is not supported.
 * @author dkatzel
 *
 */
public class OpenAwareInputStream extends InputStream{

	private static final int EOF = -1;
	
	private final PushbackInputStream in;
	/**
	 * Creates a new OpenAwareInputStream.
	 * @param in the InputStream to wrap; can not be null.
	 * @throws NullPointerException if inputstream is null.
	 */
	public OpenAwareInputStream(InputStream in){
		if(in ==null){
			throw new NullPointerException("inputstream can not be null");
		}
		this.in = new PushbackInputStream(in);
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}
	/**
	 * Is the inputStream still open (not closed).
	 * Performing this check will not advance
	 * the byte stream so future read calls
	 * will not miss any bytes.  An open
	 * inputstream may still return a {@literal -1}
	 * when {@link InputStream#read()} is called
	 * if the stream has reached the end of the file,
	 * but has not yet be closed via {@link InputStream#close()}.
	 * @return {@code true} 
	 * if reading additional bytes
	 * from the inputstream will
	 * not throw an IOException;
	 * {@code false otherwise}.
	 */
	public boolean isOpen(){
		//this is a hack to see if we 
		//have data left in the inputstream
		//inputStream#available is not guaranteed
		//to return a valid answer
		//so we need to actually read a byte
		//then push it back if we get something
		//other than EOF.
		try{
			int value =in.read();
			if(value != EOF){
				//put it back so we can read it again later
				in.unread(value);
			}
			
			return true;
		}catch(IOException e){
			//assume exception means closed?
			return false;
		}
		
	}
	
}
