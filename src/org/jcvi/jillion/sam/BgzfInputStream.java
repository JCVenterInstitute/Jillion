package org.jcvi.jillion.sam;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
/**
 * {@code BgzfInputStream}
 * is a work around for the bug in {@link GZIPInputStream}
 * documented with many bug reports including but not limited to:
 * <ul>
 * <li><a href ="http://bugs.java.com/view_bug.do?bug_id=2192186">
 * JDK-2192186 : GZIPInputStream fails to read concatenated .gz files</a></li>
 * 
 * <li><a href ="http://bugs.java.com/view_bug.do?bug_id=4691425">
 * JDK-4691425 : GZIPInputStream fails to read concatenated .gz files</a></li>
 * </ul>
 * 
 * This bug was eventually fixed in a late release of Java 6 and Java 7
 * but it is not well documented which release.  Java 7u15 seems to have the bug
 * fix but I'm not sure which versions prior to it do as well.
 * <p>
 * The bug in {@link GZIPInputStream} only reads the first
 * GZIP block so any additional blocks that are concatenated
 * in the file (like what BAM files do) are not read.
 * The work around is simple: we just creates a new {@link GZIPInputStream}
 * where the old one left off.
 * </p>
 * 
 * @author dkatzel
 *
 */
public final class BgzfInputStream extends InputStream{

	/**
	 * The original {@link InputStream}
	 * which may consist of several concatenated
	 * GZIP blocks.
	 */
    private final InputStream in;
    /**
     * The {@link GZIPInputStream}
     * which will read possibly
     * only 1 block.
     */
    private GZIPInputStream currentGzipStream;
    /**
     * The buffer size to use per block.
     */
    private final int bufferSize;
    /**
     * Have we reached the end of file yet.
     * This is used to determine
     * if we should create a new {@link GZIPInputStream}
     * or not after the current block finishes.
     */
    boolean eof=false;
    
    private long uncompressedBytesRead=0;
    
    private long compressedBytesRead=0;
    /**
     * Create a new {@link BgzfInputStream}.
     * @param in the {@link InputStream} to unzip;
     * may not be null.
     * @param size the buffer size to use when unzipping;
     * may not be negative.
     * @throws IOException if there is a problem
     * reading the first zip block.
     * @throws NullPointerException if in is null.
     * @throws IllegalArgumentException if size <= 0
     */
	public BgzfInputStream(InputStream in, int size)
			throws IOException {
		
		if(in == null){
			throw new NullPointerException("inputStream can not be null");
		}
		this.in = in;
		this.bufferSize = size;
		currentGzipStream = new GZIPInputStream(in, size);
	}
	/**
     * Creates a new {@link BgzfInputStream}
     * with a default buffer size.
     * @param in the {@link InputStream} to unzip;
     * may not be null.
     * @throws IOException if there is a problem
     * reading the first zip block.
     * @throws NullPointerException if in is null.
     */
	public BgzfInputStream(InputStream in) throws IOException {
		//default buffer size copied from GZIPInputStream
		this(in, 512);
	}
	@Override
	public int read() throws IOException {
		if(eof){
			return -1;
		}
		int bytesRead = currentGzipStream.read();
		if(bytesRead == -1 && hasConcatenatedBlock()){
			return read();
			
		}
		return bytesRead;
	}
	/**
	 * Try to read next concatenated block.
	 * SIDE EFFECT WARNING: if there is no next block,
	 * this method also sets "eof" to true.
	 * @return {@code true} if there is another block;
	 * {@code false} otherwise.
	 * 
	 */
	private boolean hasConcatenatedBlock() {
		try{
			currentGzipStream = new GZIPInputStream(in, bufferSize);
			return true;
		}catch(IOException e){
			//either EOF or malformed zipblock;
			//either way we're done.
			eof = true;
			return false;
		}
	}
	@Override
	public int read(byte[] buf, int off, int len) throws IOException {
		if(eof){
			return -1;
		}
		int bytesRead = currentGzipStream.read(buf, off, len);
		
		if(bytesRead == -1 && hasConcatenatedBlock()) {
			return read(buf,off, len);			
		}
		uncompressedBytesRead +=bytesRead;
		return bytesRead;
	}
	
	

}
