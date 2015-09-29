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
package org.jcvi.jillion.sam.index;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.AbstractSamVisitor;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
/**
 * {@code BamIndex} is an object representation 
 * of an entire BAM index ({@literal .bai}) file.
 * @author dkatzel
 *
 */
public final class BamIndex {

	private final Map<String, Integer> indexOfRefNames;
	
	private final List<ReferenceIndex> indexes;
	
	private final Long totalNumberOfUnmappedReads;
	/**
	 * Create a new {@link BamIndex} instance 
	 * using the given sorted BAM file and corresponding
	 * BAM index file.  The sort
	 * order is assumed to be in {@link org.jcvi.jillion.sam.SortOrder#COORDINATE}
	 * even if the header says otherwise.
	 * Only the header is parsed from the BAM file to get header information.
	 * @param bam the BAM file to parse; file must exist.
	 * @param bai the BAM index file to parse; file must exist.  It is 
	 * assumed that the index corresponds to the the data in the BAM file.
	 * @return a new {@link BamIndex} instance; will never be null.
	 * @throws IOException if there is a problem parsing either file.
	 */
	public static BamIndex createFromFiles(File bam, File bai) throws IOException{
		SamHeaderParser headerParser = new SamHeaderParser();
		SamParserFactory.create(bam).accept(headerParser);
		SamHeader header = headerParser.getHeader();
		
		InputStream in=null;
		try{
			in = new BufferedInputStream(new FileInputStream(bai));
			return IndexUtil.parseIndex(in, header);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);;
		}
	}
	/**
	 * {@link SamVisitor} that just gets the {@link SamHeader}
	 * from the BAM file since that's all we need.
	 * @author dkatzel
	 *
	 */
	private static class SamHeaderParser extends AbstractSamVisitor{
		private SamHeader header;
		
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			this.header = header;
			//since we only need the header we can stop
			//parsing the BAM now before we get 
			//to any SAMRecords.
			callback.haltParsing();
			
		}

		public SamHeader getHeader() {
			return header;
		}
		
	}
	/**
	 * Convenience constructor with null total number of reads.
	 * This is the same as {@link #BamIndex(SamHeader,List,Long) new BamIndex(header, indexes, null)}.
	 * @param header the {@link SamHeader} to use; can not be null.
	 * The order of the {@link ReferenceSequence}s must match the order of the {@link ReferenceIndex}
	 * list.
	 * @param indexes the List of {@link ReferenceIndex} to use; can not be null, but may be empty if
	 * no reads mapped.
	 * @see #BamIndex(SamHeader,List,Long)
	 * @throws NullPointerException if either header or indexes are null.
	 */
	public BamIndex(SamHeader header, List<ReferenceIndex> indexes){
		this(header, indexes, null);
	}
	/**
	 * Create a new {@link BamIndex} instance that uses the given {@link SamHeader},
	 * ordered list of {@link ReferenceIndex}es and optional total number of unmapped
	 * reads.
	 * @param header the {@link SamHeader} to use; can not be null.
	 * The order of the {@link ReferenceSequence}s must match the order of the {@link ReferenceIndex}
	 * list.
	 * @param indexes the List of {@link ReferenceIndex} to use; can not be null, but may be empty if
	 * no reads mapped.
	 * @param totalNumberOfUnmappedReads optional total number of unmapped reads, only used for metadata collection,
	 * this value may be {@code null} if the value is unknown.
	 * @throws NullPointerException if either header or indexes are null.
	 */
	public BamIndex(SamHeader header, List<ReferenceIndex> indexes, Long totalNumberOfUnmappedReads){
		int refIndex=0;
		Collection<SamReferenceSequence> referenceSequences = header.getReferenceSequences();
		indexOfRefNames = new HashMap<String, Integer>(MapUtil.computeMinHashMapSizeWithoutRehashing(referenceSequences.size()));
		
		for(SamReferenceSequence ref :referenceSequences){
			indexOfRefNames.put(ref.getName(), refIndex);
			refIndex++;
		}
		
		this.indexes = new ArrayList<ReferenceIndex>(indexes);
		this.totalNumberOfUnmappedReads = totalNumberOfUnmappedReads;
	}
	
	/**
	 * Get the {@link ReferenceIndex} by the order given in the 
	 * header.
	 * @param i the ith {@link ReferenceIndex} to get;
	 * must be 0 <=i < {@link #getNumberOfReferenceIndexes()}
	 * @return the selected {@link ReferenceIndex} will never be null.
	 * @throws IndexOutOfBoundsException if i is not <0 or >= {@link #getNumberOfReferenceIndexes()}.
	 */
	public ReferenceIndex getReferenceIndex(int i){
		return indexes.get(i);
	}
	/**
	 * Get the number of {@link ReferenceIndex}es 
	 * in this bam index.
	 * @return an int >=0.
	 */
	public int getNumberOfReferenceIndexes(){
		return indexes.size();
	}
	/**
	 * Get the {@link ReferenceIndex} by its name
	 * as determined by {@link ReferenceSequence#getName()}.
	 * @param refName the refName to get, can not be null;
	 * @return a {@link ReferenceIndex} or {@code null}
	 * if there is no {@link ReferenceIndex} with that name.
	 * @throws NullPointerException if refName is null.
	 */
	public ReferenceIndex getReferenceIndex(String refName){
		if(refName ==null){
			throw new NullPointerException("refName can not be null");
		}
			
		Integer i = indexOfRefNames.get(refName);
		if(i==null){
			return null;
		}
		return indexes.get(i);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+  indexOfRefNames.hashCode();
		result = prime * result + indexes.hashCode();
		return result;
	}
	/**
	 * Two {@link BamIndex}es are equal if they have the 
	 * same {@link ReferenceIndex}es in the same order
	 * and the same names to the {@link ReferenceIndex}.
	 * <br/>
	 * <strong>Note:</strong> Since {@link #getTotalNumberOfUnmappedReads()}
	 * is optional, it is not used in equality comparisons.
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BamIndex)) {
			return false;
		}
		BamIndex other = (BamIndex) obj;
		if (!indexOfRefNames.equals(other.indexOfRefNames)) {
			return false;
		}
		if (!indexes.equals(other.indexes)) {
			return false;
		}
		return true;
	}
	/**
	 * Get the optionally provided total number of unmapped reads
	 * which some BamIndexes include as part of
	 * metadata.
	 * @return the number as a Long or {@code null}
	 * if the value is not available.
	 */
	public Long getTotalNumberOfUnmappedReads() {
		return totalNumberOfUnmappedReads;
	}
	
	
}
