package org.jcvi.common.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.jcvi.common.core.io.IOUtil;
/**
 * {@code ProcessStreamReader}
 * is a  class that takes an {@link InputStream}
 * from a {@link Process} (usually {@link Process#getInputStream()}
 * and {@link Process#getErrorStream()} ) and reads their contents
 * in a background thread.  Depending on the native
 * system {@link Process}
 * being executed is running on and the amount of data output
 * to STDOUT and STDERR, not draining these streams
 * concurrently while the {@link Process} is executing
 * could 
 * block or even cause deadlock (see {@link Process} javadoc more more details).  
 * @author dkatzel
 * @see Process
 *
 */
public class ProcessStreamReader implements Runnable{

	private final InputStream in;
	private StringBuilder buffer;
	private final Charset charSet;
	/**
	 * Create a new {@link ProcessStreamReader} instance
	 * using the given {@link InputStream} and assume
	 * the {@link Charset} is UTF-8.
	 * The contents of the {@link InputStream} will be read
	 * but not saved; {@link #getCurrentContentsAsString()}
	 * will return null.
	 * @param in the {@link InputStream} to read.
	 * @return a new {@link ProcessStreamReader} instance.
	 */
	public static ProcessStreamReader createAndIgnoreOutput(InputStream in){
		return createAndIgnoreOutput(in, IOUtil.UTF_8);
	}
	/**
	 * Create a new {@link ProcessStreamReader} instance
	 * using the given {@link InputStream}.
	 * The contents of the {@link InputStream} will be read
	 * but not saved; {@link #getCurrentContentsAsString()}
	 * will return null.
	 * @param in the {@link InputStream} to read.
	 * @param charSet the {@link Charset} the data in the {@link InputStream}
	 * is encoded with.
	 * @return a new {@link ProcessStreamReader} instance.
	 */
	public static ProcessStreamReader createAndIgnoreOutput(InputStream in, Charset charSet){
		ProcessStreamReader reader = new ProcessStreamReader(in, charSet,true);
		new Thread(reader).start();
		return reader;
	}
	/**
	 * Create a new {@link ProcessStreamReader} instance
	 * using the given {@link InputStream} and assume
	 * the {@link Charset} is UTF-8.
	 * The contents of the {@link InputStream}
	 * can be retrieved as a String using {@link #getCurrentContentsAsString()}.
	 * @param in the {@link InputStream} to read.
	 * @return a new {@link ProcessStreamReader} instance.
	 */
	public static ProcessStreamReader create(InputStream in){
		return create(in, IOUtil.UTF_8);
	}
	/**
	 * Create a new {@link ProcessStreamReader} instance
	 * using the given {@link InputStream} and {@link Charset}.
	 * The contents of the {@link InputStream}
	 * can be retrieved as a String using {@link #getCurrentContentsAsString()}.
	 * @param in the {@link InputStream} to read.
	 * @param charSet the {@link Charset} the data in the {@link InputStream}
	 * is encoded with.
	 * @return a new {@link ProcessStreamReader} instance.
	 */
	public static ProcessStreamReader create(InputStream in, Charset charSet){
		ProcessStreamReader reader = new ProcessStreamReader(in, charSet,true);
		new Thread(reader).start();
		return reader;
	}
	
	private ProcessStreamReader(InputStream in, Charset charSet, boolean captureOutput) {
		this.in = in;
		this.charSet = charSet;
		if(captureOutput){
			buffer = new StringBuilder();
		}else{
			buffer=null;
		}
	}
	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(in, charSet));
		String line =null;
		try {
			while((line = br.readLine()) !=null){
				if(buffer !=null){
					buffer.append(line).append('\n');
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Return the contents of the stream read
	 * so far as a String; or null if output was
	 * ignored.
	 * @return contents of the stream read
	 * so far as a String; or null if output was
	 * ignored.
	 */
	public String getCurrentContentsAsString(){
		if(buffer ==null){
			return null;
		}
		return buffer.toString();
	}
}
