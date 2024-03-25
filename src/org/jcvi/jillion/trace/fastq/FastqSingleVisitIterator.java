package org.jcvi.jillion.trace.fastq;

import java.io.Closeable;
/**
 * An iterator that will call a single {@link FastqVisitor#visitDefline(org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback, String, String)}
 * for each time {@link #next(FastqVisitor)} is called.
 * <p>
 * This is a way to control when the next record in a fastq file is visited.
 * Previously since all visiting is performed beyond the control of the caller, pausing
 * the visits to perform other computations required complicated multi-threaded code
 * with blocking.  That is error prone and less efficient.  
 * </p>
 * 
 * Consider needed to parse two paired fastq files simultaneously. It would be
 * very hard to write code such that each record in the files were visited 
 * so that the paired reads could be processed together in an efficient way.
 * However with these iterators, it is simple:
 * 
 * Create 2 iterators, one for each file.  Then
 * call {@link #next(FastqVisitor)}
 * on the first iterator and then call {@link #next(FastqVisitor)} on the other iterator.
 * Repeat for all other paired reads as long {@link #hasNext()} is {@code true}.
 * <pre>
 * <code>
 * try(FastqSingleVisitIterator read1Iter = fastq1.iterator();
 *     FastqSingleVisitIterator read2Iter = fastq2.iterator()){
 *     
 *        FastqVisitor visitor1 = ...
 *        FastqVisitor visitor2 = ...
 *        while(read1Iter.hasNext() && read2Iter.hasNext()){
 *          read1Iter.next(visitor1);
 *          read2iter.next(visitor2);
 *        }
 *     }
 * </code>
 * </pre>
 * @author dkatzel
 * 
 * @since 6.0.2
 *
 */
public interface FastqSingleVisitIterator extends Closeable{
	boolean hasNext();
	void next(FastqVisitor visitor);
}