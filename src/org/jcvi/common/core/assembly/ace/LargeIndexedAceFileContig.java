package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code IndexedAceFileContig} is an {@link AceContig}
 * that doesn't store all the data of this contig
 * in memory at any one time.  Instead of storing
 * {@link AceAssembledRead}s, only a map of offset ranges
 * into the ace file are stored.  Whenever a read
 * is requested, the file is re-opened, and 
 * <strong>just the lines for that one read</strong>
 * are re-parsed.  To improve performance,
 * an LRU cache is used to store the most recent fetched
 * reads.  This helps performance if the same reads
 * are requested over and over again in close proximity
 * (like  iterating through regions in a coverage map).
 * Currently the cache size is 
 * {@code max depth of coverage * 2}.
 * @author dkatzel
 *
 */
final class LargeIndexedAceFileContig extends AbstractIndexedAceFileContig{

	
	
	

	protected LargeIndexedAceFileContig(String contigId,
			Map<String, AlignedReadInfo> readInfoMap,
			Map<String, Range> readOffsetRanges, boolean isComplimented,
			NucleotideSequence consensus, File aceFile,
			long contigStartFileOffset, int maxCoverage,
			boolean aceFileHasSortedReads) {
		super(contigId, readInfoMap, readOffsetRanges, isComplimented, consensus,
				aceFile, contigStartFileOffset, maxCoverage, aceFileHasSortedReads);
	}
	
	@Override
	protected InputStream getInputStreamOfContig() throws IOException{
		InputStream in = new FileInputStream(getAceFile());
		//seek to start of contig
		IOUtil.blockingSkip(in, getContigStartFileOffset());
		return in;
	}
	
	@Override
	protected InputStream getInputStreamOfRead(Range offsetRange) throws IOException{
		return IOUtil.createInputStreamFromFile(getAceFile(), (int)offsetRange.getBegin(), (int)offsetRange.getLength());
		
	}
}
