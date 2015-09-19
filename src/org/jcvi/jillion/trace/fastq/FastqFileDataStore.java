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
package org.jcvi.jillion.trace.fastq;
/**
 * {@link FastqFileDataStore} is a {@link FastqDataStore}
 * where all the {@link FastqRecord}s in the datastore
 * belong to the same {@code .fastq} file.
 * @author dkatzel
 * @since 5.0
 */
public interface FastqFileDataStore extends FastqDataStore{
    /**
     * Get the {@link FastqQualityCodec} that was 
     * used to encode all the {@link FastqRecord}s
     * in this file.  This is useful for when
     * processing a fastq file and you want any output
     * fastq files to use the same fastq quality codec
     * as the input files.
     * 
     * @return a {@link FastqQualityCodec}; may be null
     * if there are no records in the fastq file
     * depending on the implementation of the {@link FastqDataStore}.
     */
    FastqQualityCodec getQualityCodec();
}
