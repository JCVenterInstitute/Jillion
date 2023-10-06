/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.sam;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;

/**
 * {@code BgzfInputStream} is an {@link InputStream} implementation
 * for parsing BAM "BGZF" encoded files which are specially formated
 * concatenated GZIP blocks with extra fields.
 * <br>
 * Can't use {@link java.util.zip.GZIPInputStream} for 2 reasons:
 * <ul>
 * <li>GZIPInputStream had a bug in it for reading concatenated blocks
 * that wasn't fixed until Java 7 came out.  Since Jillion still supports
 * Java 6, moving to Java 7 wasn't an option.</li>
 * <li>To support BAM indexes, we have to know internal details about
 * how many compressed and uncompressed bytes have been parsed so far
 * at any given moment as well as some values set by the 
 * BGZF extra fields.  This information could not be obtained
 * from (a fixed) GZIPInputStream even via subclassing. Reflection might
 * have worked but would be brittle.</li>
 * </ul>
 * 
 * Therefore, it was decided to write a new implementation
 * to work around those problems.
 * 
 * @author		dkatzel
 *
 */
class BgzfInputStream extends InflaterInputStream {
	
	 private static final int BGZF_XLEN_LENGTH = 6;

	/**
     * GZIP header magic number.
     */
    private static final int GZIP_MAGIC_NUMBER = 0x8b1f;

    /*
     * GZIP block header flag 
     * names taken from constants
     * from RFC 1952
     */
    private static final int FHCRC      = 2; //bit 1
    private static final int FEXTRA     = 4; //bit 2
    private static final int FNAME      = 8; //bit 3
    private static final int FCOMMENT   = 16; //bit 4

    /**
     * This is the magic number in the extra fields
     * of a GZIP block that denote BGZF files.
     */
	private static final int BGZF_MAGIC_NUMBER = 0x4243;
	
    private static final int BGZF_EXTRA_FIELDS_LENGTH = 8;
    /**
     * The max buffer a compressed block can be.
     * We also use this as the GZIP buffer size
     * so it's easier to compute where we are in a block.
     */
    private static final int BUFFER_SIZE = 0xFFFF;

	/**
     * CRC-32 for uncompressed data.
     */
    protected CRC32 crc = new CRC32();

    /**
     * Indicates end of file
     * has been reached.
     * Note : This is different than
     * closed..
     */
    protected boolean eof;
    /**
     * Indicates that the user
     * has called the {@link #close()} method
     * before we reached the end of file.
     */
    private volatile boolean closed = false;
    /**
     * Number of compressed bytes
     * the full blocks we have read take up.
     * This is used to compute BAM
     * VirtualFileOffsets for BAM indexing. 
     */
    private long compressedBlockBytesReadSoFar=0;

    
    private int currentBlockSize;

    /**
     * Creates a new BgzfInputStream with a default buffer size.
     * 
     * @param bamFile The bam file to parse.
     *
     * @throws ZipException if a GZIP format error has occurred or the
     *                         compression method used is unsupported
     * @throws IOException if an I/O error has occurred
     * 
     * @since 5.0
     */
    public static BgzfInputStream create(File bamFile) throws IOException{
    	return new BgzfInputStream(bamFile);
    }
    /**
     * Creates a new {@link BgzfInputStream} starting
     * from the given {@link VirtualFileOffset}.
     * 
     * @param bamFile The bam file to parse.
     * @param vfs the {@link VirtualFileOffset} to use to seek to before reading
     *any bytes from the stream; can not be null.
     *
     * @throws ZipException if a GZIP format error has occurred or the
     *                         compression method used is unsupported
     *@throw IOException if an I/O error has occurred.
     * 
     * @throws NullPointerException if either parameter is null.
     * 
     * @since 5.0
     */
    static BgzfInputStream create(File bamFile, VirtualFileOffset vfs) throws IOException{
    	if(bamFile==null) {
    		throw new NullPointerException("bam file can not be null");
    	}
    	if(vfs==null) {
    		throw new NullPointerException("vfs can not be null");
    	}
    	long compressedBamBlockOffset = vfs.getCompressedBamBlockOffset();
    	InputStream in;
    	if(compressedBamBlockOffset>0){
    		in = new RandomAccessFileInputStream(bamFile, compressedBamBlockOffset);
    	}else{
    		in = new BufferedInputStream(new FileInputStream(bamFile));
    	}
		
    	BgzfInputStream bgzfStream = new BgzfInputStream(in);
    	//we have already read the header of the current block so append the amout of bytes we skipped
    	bgzfStream.compressedBlockBytesReadSoFar+= compressedBamBlockOffset;
    	IOUtil.blockingSkip(bgzfStream, vfs.getUncompressedOffset());
    	return bgzfStream;
    	
    }
    
    /**
     * Creates a new input stream with the specified buffer size.
     * @param in the input stream
     * @param size the input buffer size
     *
     * @throws ZipException if a GZIP format error has occurred or the
     *                         compression method used is unsupported
     * @throws IOException if an I/O error has occurred
     * @throws IllegalArgumentException if size is <= 0
     */
    private BgzfInputStream(InputStream in, int size) throws IOException {
        super(in, new Inflater(true), size);
        parseBlockHeader(in);
    }

    /**
     * Creates a new input stream with a default buffer size.
     * 
     * @param bam The bam file to parse.
     *
     * @throws ZipException if a GZIP format error has occurred or the
     *                         compression method used is unsupported
     * @throws IOException if an I/O error has occurred
     */
    public BgzfInputStream(File bam) throws IOException {
        this(new BufferedInputStream(new FileInputStream(bam),BUFFER_SIZE));
    }
    
    
    /**
     * Creates a new input stream with a default buffer size.
     * @param in the input stream
     *
     * @throws ZipException if a GZIP format error has occurred or the
     *                         compression method used is unsupported
     * @throws IOException if an I/O error has occurred
     */
    private BgzfInputStream(InputStream in) throws IOException {
        this(in, BUFFER_SIZE);
    }

    /**
     * Reads uncompressed data into an array of bytes. If <code>len</code> is not
     * zero, the method will block until some input can be decompressed; otherwise,
     * no bytes are read and <code>0</code> is returned.
     * @param buf the buffer into which the data is read
     * @param off the start offset in the destination array <code>b</code>
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read, or -1 if the end of the
     *          compressed input stream is reached
     *
     * @throws  NullPointerException If <code>buf</code> is <code>null</code>.
     * @throws  IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>buf.length - off</code>
     * @throws ZipException if the compressed input data is corrupt.
     * @throws IOException if an I/O error has occurred.
     *
     */
    public int read(byte[] buf, int off, int len) throws IOException {
        assertNotClosed();
        if (!hasMoreData()) {
            return -1;
        }
        //don't read anything
        //unless we have to
        if(len ==0){
        	return 0;
        }
        //Most of the try block below
        //was taken from InflaterInputStream
        //so we don't have to call super.read()
        //and since super.read() will return -1
        //and set eof flags which we don't want.
        //If we are at the end of the block
        //we can simplify our code by 
        //checking if we have more blocks immediately
        //instead of checking for -1 and 
        //resetting flags.
        try {
            int bytesRead;
            while ((bytesRead = inf.inflate(buf, off, len)) == 0) {
                if (inf.finished() || inf.needsDictionary()) {
                	//if we get here we've reached the end of the block
                	compressedBlockBytesReadSoFar +=currentBlockSize;
                    if (hasMoreBlocks()){
                    	return this.read(buf, off, len);
                    }
                    //if we get here
                    //then we are done parsing the BAM file
                    eof = true;
                    return -1;
                }
                if (inf.needsInput()) {
                    fill();
                }
            }  	
            crc.update(buf, off, bytesRead);
            
            return bytesRead;
        } catch (DataFormatException e) {
            String s = e.getMessage();
            throw new IOException(s == null ? "Invalid ZLIB data format" : s, e);
        }
        
    }

  
    
    private void assertNotClosed() throws IOException {
        if (closed) {
            throw new IOException("BAM file is closed");
        }
    }
    
    public boolean hasMoreData() {
		return !eof;
	}

    /**
     * Get the {@link VirtualFileOffset}
     * which represents where we are currently
     * in the {@link BgzfInputStream}.
     * Any read calls will change the value
     * returned by this method.
     * @return a new {@link VirtualFileOffset};
     * will never be null.  Calling
     * the method multiple times between reads
     * will return equal file offsets (but
     * may not be the same instance).
     */
    VirtualFileOffset getCurrentVirutalFileOffset(){    	
    	int uncompressedBytesReadInCurrentBlock = (int)this.inf.getBytesWritten();
    	if(uncompressedBytesReadInCurrentBlock > BUFFER_SIZE){
    		//this will cause an overflow in the encoded virtual file offset
    		//we should be able to just return the beginning of the next block...
    		return VirtualFileOffset.create(compressedBlockBytesReadSoFar + currentBlockSize, 0);
        	
    	}
    	return VirtualFileOffset.create(compressedBlockBytesReadSoFar, uncompressedBytesReadInCurrentBlock);
    	
    }
	

	/**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     * @exception IOException if an I/O error has occurred
     */
    public void close() throws IOException {
        if (!closed) {
            super.close();
            eof = true;
            closed = true;
            //since we don't have access to the 
            //InflaterInputStream's useDefaultInflater field
            //we have to do the cleanup ourselves
            this.inf.end();
        }
    }

   
	
	
    /**
     * Reads GZIP Blcok header and returns the total byte number
     * of this member header.
     */
    private int parseBlockHeader(InputStream currentStream) throws IOException {
        CheckedInputStream in = new CheckedInputStream(currentStream, crc);
        crc.reset();
        // Check header magic
        if (IOUtil.readUnsignedShort(in, ByteOrder.LITTLE_ENDIAN) != GZIP_MAGIC_NUMBER) {
            throw new ZipException("Not in GZIP format");
        }
        // Check compression method
        if (IOUtil.readUnsignedByte(in) != 8) {
            throw new ZipException("Unsupported compression method");
        }
        // Read flags
        int flg = IOUtil.readUnsignedByte(in);
        // Skip MTIME, XFL, and OS fields
        IOUtil.blockingSkip(in, BGZF_XLEN_LENGTH);
        
        int headerLength = 10;
        currentBlockSize = parseCurrentBgzfBlockSize(in, flg);

        headerLength += BGZF_EXTRA_FIELDS_LENGTH;
        // Skip optional file name if present
        if ((flg & FNAME) == FNAME) {
            do {
                headerLength++;
            } while (IOUtil.readUnsignedByte(in) != 0);
        }
        // Skip optional file comment if present
        if ((flg & FCOMMENT) == FCOMMENT) {
            do {
                headerLength++;
            } while (IOUtil.readUnsignedByte(in) != 0);
        }
        // Check optional header CRC
        if ((flg & FHCRC) == FHCRC) {
            int v = (int)crc.getValue() & 0xffff;
            if (IOUtil.readUnsignedShort(in, ByteOrder.LITTLE_ENDIAN) != v) {
                throw new ZipException("Corrupt GZIP header");
            }
            headerLength += 2;
        }
        crc.reset();
        return headerLength;
    }

	private int parseCurrentBgzfBlockSize(CheckedInputStream in, int flg)
			throws IOException {
        //BGZF files must have the F.EXTRA
        //flag set and have a payload of 6 bytes
        if ((flg & FEXTRA) == FEXTRA) {
            int extraLength = IOUtil.readUnsignedShort(in, ByteOrder.LITTLE_ENDIAN);
            if(extraLength !=6){
            	throw new IOException("invalid BGZF file, F.EXTRA not correct length");
            }
            int magic = IOUtil.readUnsignedShort(in);
            if(magic != BGZF_MAGIC_NUMBER){
            	throw new IOException("invalid BGZF file, F.EXTRA Subfield IDs are wrong " + Integer.toHexString(magic));                
            }
            
            if(IOUtil.readUnsignedShort(in, ByteOrder.LITTLE_ENDIAN) != 2){
            	throw new IOException("invalid BGZF file, F.EXTRA payload length not 2");                
            }
           return IOUtil.readUnsignedShort(in, ByteOrder.LITTLE_ENDIAN) +1;
            
        }else{
        	//not a BGZF file
        	throw new IOException("invalid BGZF file, F.EXTRA not set");
        }

	}

    /**
     * Checks to see if there are more
     * concatenated blocks in this 
     * GZIP inputStream. 
     * BGZF files have multiple
     * GZIP blocks.
     * If there are more blocks, then the 
     * wrapped InputSream is advanced to the correct position
     * to begin reading that next block seamlessly.
     * @return {@code true} if there are more blocks
     * {@code false} if there aren't.
     * @throws IOException
     */
    private boolean hasMoreBlocks() throws IOException {
        //GZIPBUG FIX
        //This next code block supports handling
        //concatenated blocks which the BGZF uses
        //Which broke Java pre Java 6u23 (or there-abouts)
    	
    	//code mostly taken from various bug reports
    	//that included work around code
    	
        InputStream in = this.in;
        int remaining = inf.getRemaining();
        if (remaining > 0) {
            in = new SequenceInputStream(
                        new ByteArrayInputStream(buf, len - remaining, remaining), in);
        }
        long actualCrc = crc.getValue();
        long expectedCrc = IOUtil.readUnsignedInt(in, ByteOrder.LITTLE_ENDIAN);
        
        if(expectedCrc != actualCrc){
        	throw new ZipException("invalid CRC: expected " + expectedCrc + " actual " + actualCrc);
        }
        // rfc1952; ISIZE is the input size % 2^32
        if(IOUtil.readUnsignedInt(in, ByteOrder.LITTLE_ENDIAN) != (inf.getBytesWritten() & 0xffffffffL) ){
        	throw new ZipException("invalid ISIZE");
        }
      
        
        // If there are more bytes available in "in" or
        // the leftover in the "inf" is > 26 bytes:
        // 8 bytes per footer and at least 10 bytes in the next header block
        //means we should have at least 10+ 2*8 bytes if we have another
        //block (because we still have our current footer + the next block's header and footer)

        if (this.in.available() > 0 || remaining > 26) {
            int m = 8;                  // this.footer
            try {
                m += parseBlockHeader(in);    // next.header
            } catch (IOException ze) {
                return false;  // ignore any malformed, do nothing
            }
            inf.reset();
            if (remaining > m){
                inf.setInput(buf, len - remaining + m, remaining - m);
            }
            return true;
        }
        return false;
    }
    
}
