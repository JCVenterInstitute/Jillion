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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.InputStreamSupplier;
/**
 * Creates a {@link FastqParser}
 * that parses the given fastq encoded file
 * along with additional configuration options.
 * 
 * 
 * @author dkatzel
 * 
 * @since 5.0
 */
public final class FastqFileParserBuilder {

    private final InputStreamSupplier inputStreamSupplier;
    private final InputStream in;
    
    private boolean hasComments;
    private boolean multiline;
    /**
	 * Create a new Builder instance
	 * that will parse the given fastq encoded
	 * {@link InputStream}.  Please Note that inputStream implementations
	 * of the FastqFileParser can only be parsed once 
	 * and will not not create {@link FastqVisitorMemento}s
	 * or use {@link #accept(FastqVisitor, FastqVisitorMemento)}
	 * method.
	 * 
	 * @apiNote if you have a compressed file that you want to be
	 * able to parse multiple times, use {@link #FastqFileParserBuilder(File, Function)}
	 * 
	 * @param in the fastq encoded {@link InputStream} to parse; can not be null.
	 * 
	 * @throws NullPointerException if inputstream is null.
	 * 
	 * @return a new {@link FastqParser} instance; will never be null.
	 * 
	 * @see #FastqFileParserBuilder(File, Function)
	 */
    public FastqFileParserBuilder(InputStream in){
        Objects.requireNonNull(in, "Inputstream can not be null");
        
        this.in = in;
        this.inputStreamSupplier = null;
    }
    /**
     * Create a Builder that will parse the given
     * fastq encoded File.
     * @param fastqFile the fastq file to be parsed; can not be null.
     * 
     * @throws IOException if the file is not readable
     * @throws NullPointerException if file is null.
     */
    public FastqFileParserBuilder(File fastqFile) throws IOException{
    	 this(InputStreamSupplier.forFile(fastqFile));
    }

    public FastqFileParserBuilder(InputStreamSupplier inputStreamSupplier) {
	Objects.requireNonNull(inputStreamSupplier);
        this.in = null;
        this.inputStreamSupplier = inputStreamSupplier;
    }
    /**
	 * Does this fastq file contain comments on the deflines.
	 * If not called, then by default this builder uses hasComments = false.
	 * 
	 * @param hasComments
	 *            do the deflines of the sequences contain comments. If set to
	 *            {@code true} then a more computationally intensive parsing is
	 *            performed to try to distinguish the id from the comment.
	 *            Remember some Fastq id's can have spaces which makes comment
	 *            detection difficult and complex.
	 * @return this
	 */
    public FastqFileParserBuilder hasComments(boolean hasComments){
        this.hasComments = hasComments;
        return this;
    }
    /**
     * Does this fastq file contain records that may have multiple lines for the sequence
     * and qualities sequences.
     * Most fastq files define each record in 4 lines:
     * <ol>
     * <li>The defline</li>
     * <li>nucleotide sequence</li>
     * <li>The quality defline</li>
     * <li>quality sequence</li>
     * </ol>
     * 
     * However, some fastq files split the nucleotide and quality sequences
     * over multiple lines.  For performance reasons,
     * checking for multi-lines has been turned off by default
     * and must be explicitly turned on by calling this method.
     * 
     * @param multiline {@code true} if this file may contain multilines;
     * {@code false} otherwise (defaults to {@code false}.
     * 
     * @return this
     */
    public FastqFileParserBuilder hasMultilineSequences(boolean multiline){
        this.multiline = multiline;
        return this;
    }
    /**
     * Creates a new {@link FastqParser} object
     * which will parse the given Fastq encoded file
     * using the provided options.
     * 
     * @return a new {@link FastqParser}; will never be null.
     * @throws IOException if there is a problem creating the parser object.
     */
    public FastqParser build() throws IOException{
        if(in ==null){
            return FastqFileParser.create(inputStreamSupplier, hasComments, multiline);
        }
        return FastqFileParser.create(in, hasComments, multiline);
    }
    
    
}
