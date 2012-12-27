package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.impl.ByteBufferInputStream;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code MemoryMappedIndexedAceFileContig} is a 
 * {@link AceContig} implementation
 * that uses a memory mapped {@link ByteBuffer}
 * as a backing store.  All calls to get reads etc will
 * get re-parsed from the buffer.  This should
 * be faster than I/O to the filesystem or over the network.
 * <p/>
 * Due to limitations to Java memory mapping,
 * this will only work on contigs that take up 
 * < Integer.MAX_VALUE bytes in an ace file.
 * @author dkatzel
 *
 */
class MemoryMappedIndexedAceFileContig extends AbstractIndexedAceFileContig{

	
	
	private final ByteBuffer buffer;

	protected MemoryMappedIndexedAceFileContig(String contigId,
			Map<String, AlignedReadInfo> readInfoMap,
			Map<String, Range> readOffsetRanges, boolean isComplimented,
			NucleotideSequence consensus, File aceFile,
			long contigStartFileOffset, int maxCoverage,
			boolean aceFileHasSortedReads, int numberOfBytesInContig) throws IOException {
		super(contigId, readInfoMap, readOffsetRanges, isComplimented, consensus,
				aceFile, contigStartFileOffset, maxCoverage, aceFileHasSortedReads);
		
		FileChannel channel = new RandomAccessFile(aceFile, "r").getChannel();
		try{
			buffer =channel.map(MapMode.READ_ONLY, contigStartFileOffset, numberOfBytesInContig);
		}finally{
			channel.close();
		}
	}
	
	@Override
	protected synchronized InputStream getInputStreamOfContig() throws IOException{
		ByteBuffer copyOfBuffer = buffer.duplicate();
		copyOfBuffer.limit(buffer.capacity());
		copyOfBuffer.position(0);
		
		return new ByteBufferInputStream(copyOfBuffer);
	}
	
	@Override
	protected synchronized InputStream getInputStreamOfRead(Range offsetRange) throws IOException{
		Range adjustedRange = new Range.Builder(offsetRange)
									.shift(-getContigStartFileOffset())
									.build();
		ByteBuffer copyOfBuffer = buffer.duplicate();
		copyOfBuffer.limit((int)adjustedRange.getEnd()+1);
		copyOfBuffer.position((int)adjustedRange.getBegin());
		return new ByteBufferInputStream(copyOfBuffer);
		
	}

}
