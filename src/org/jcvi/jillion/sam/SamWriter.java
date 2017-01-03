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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.index.BamIndexFileWriterBuilder;
/**
 * {@code SamWriter} is an interface for
 * writing SAM or BAM encoded files.
 * @author dkatzel
 *
 */
public interface SamWriter extends Closeable{
	/**
	 * Write the given record to the SAM or BAM file.
	 * Different implementations of {@link SamWriter}
	 * might delay actually writing out the {@link SamRecord}
	 * either to improve disk writing performance
	 * or to re-sort records.
	 * @param record the {@link SamRecord} to write;
	 * can not be null.
	 * @throws IOException if there is a problem writing the {@link SamRecord}
	 * @throws NullPointerException if record is null.
	 */
	void writeRecord(SamRecord record) throws IOException;
	
	/**
	 * Create a new SamWriter that will write out a sorted BAM file and corresponding index.
	 * The index file will be named "$outputFile#getName.bam.bai" will also be created in the same directory
         * as the outputFile. 
	 * @param outputBam  the output file to write to; can not be null. The file extension must be ".bam".
	 * 
	 * @param header the SamHeader; can not be null.  The sort order specified in the header is ignored
	 * and overridden to be {@link SortOrder#COORDINATE} to be a valid sorted BAM file.
	 *  
	 * @return a new SamWriter that will write a sorted BAM file with a corresponding index file.
	 * @throws IOException if there is a problem creating the writer.
	 * 
	 * @since 5.3
	 * 
	 * @apiNote This is a convenience method that is the same as:
	 * <pre>
	 * new SamFileWriterBuilder(outputBam, header)
                   .reSortBy(SortOrder.COORDINATE)
                    //create index with extra metadata
                   .createBamIndex(true, true)                             
                   .build();
	 * </pre>
	 * 
	 * @see SamFileWriterBuilder
	 */
	public static SamWriter newSortedBamWriter(File outputBam, SamHeader header) throws IOException{
	    String ext = FileUtil.getExtension(outputBam);
	    if(!"bam".equals(ext)){
	        throw new IllegalArgumentException("output file must have a '.bam' extension : " + outputBam);
	    }
	    return new SamFileWriterBuilder(outputBam, header)
                    .reSortBy(SortOrder.COORDINATE)
                    //create index with extra metadata
                   .createBamIndex(true, true)                             
                   .build();
	}
	/**
         * Write a new Bam index file ( {@code .bai} file) for the given input BAM that
         * will write a Bam index file to the same directory as the
         * input bam and name it {@code sortedBamFile.getName() +".bai"}.
         * 
         * @apiNote this is the same as
         * <pre>
         * {@code 
         * new BamIndexFileWriterBuilder(sortedBamFile)
                    .includeMetaData(true) //includes metadata that Picard and samtools use
                    .assumeSorted(true)
                    .build()
         * }
         * </pre>
         * @param sortedBamFile the sorted input BAM file to parse and create
         *                      an index from; can not be null, must exist
         *                      and end with {@literal ".bam"}.
         * @throws IOException if there are any problems creating any missing output files or if the
         * input BAM file does not exist.
         * 
         * @throws NullPointerException if inputBamFile is null.
         * @throws IllegalArgumentException if the file extensions aren't correct.
         * 
         * @see BamIndexFileWriterBuilder
         * 
         * @since 5.3
         */
	public static File writeBamIndexFor(File sortedBamFile) throws IOException{
	    return new BamIndexFileWriterBuilder(sortedBamFile)
                    .includeMetaData(true) //includes metadata that Picard and samtools use
                    .assumeSorted(true)
                    .build();
	}
	
	
}
