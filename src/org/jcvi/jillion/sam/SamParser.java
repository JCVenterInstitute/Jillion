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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback.SamVisitorMemento;
import org.jcvi.jillion.sam.header.SamHeader;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
/**
 * {@code SamParser}
 * is an interface that can parse
 * SAM or BAM files and call the appropriate
 * methods on the given {@link SamVisitor}.
 * @author dkatzel
 * 
 * @see SamParserFactory
 *
 */
public interface SamParser {
	/**
	 * Can this handler accept new parse requests
	 * via the various parse() methods.
	 * 
	 * Some implementations of {@link SamParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the sam structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the Sam or Bam file and 
	 * and call the appropriate visit methods
	 * on the given {@link SamVisitor}.
	 * 
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the sam or bam file.
	 * @throws NullPointerException if visitor is null.
	 */
	void parse(SamVisitor visitor) throws IOException;
	
	/**
	 * Parse the Sam or Bam file and <strong>
	 * starting from the offset of the provided memento</strong>
	 * and call the appropriate visit methods
	 * on the given {@link SamVisitor}.
	 * 
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the sam or bam file.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the memento is invalid for this
	 * {@link SamParser} instance (wrong file, wrong parser implementation etc).
	 */
	void parse(SamVisitor visitor, SamVisitorMemento memento) throws IOException;
	/**
	 * Parse the Sam or Bam file and 
	 * but only visit the {@link SamRecord}s
	 * that map to the given reference.
	 * 
	 * @param referenceName the name of the Reference to visit
	 * the mapped records of; can not be null.
	 * 
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the sam or bam file.
	 * @throws NullPointerException if either referenceName or visitor are null.
	 * 
	 * @since 5.0
	 */
	void parse(String referenceName, SamVisitor visitor) throws IOException;
	/**
	 * Parse the Sam or Bam file and 
	 * but only visit the {@link SamRecord}s
	 * that map to the given reference and the read
	 * alignment intersects the reference
	 * to the provided Range. 
	 * 
	 * 
	 * @param referenceName the name of the Reference to visit
	 * the mapped records of; can not be null.
	 * 
	 * @param alignmentRange the {@link Range} on the Reference to visit
	 * the mapped records of; can not be null.
	 * 
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the sam or bam file.
	 * @throws NullPointerException if any parameters are null.
	 * 
	 * @since 5.0
	 */
	void parse(String referenceName, Range alignmentRange, SamVisitor visitor) throws IOException;
	
	/**
         * Parse the Sam or Bam file and 
         * but only visit the {@link SamRecord}s
         * that map to the given reference and the read
         * alignment intersects the reference
         * to the provided Range. 
         * 
         * 
         * @param options the name of the Reference to visit
         * the mapped records of; can not be null.
         *          
         * 
         * @param visitor the {@link SamVisitor}
         * to call the visit methods on;
         * can not be null.
         * @throws IOException if there is a problem parsing the sam or bam file.
         * @throws NullPointerException if any parameters are null.
         * 
         * @since 5.3
         */
        void parse(SamParserOptions options, SamVisitor visitor) throws IOException;
	/**
	 * Get the {@link SamHeader}
	 * for this SAM or BAM file.
	 * 
	 * @apiNote some SamParser implementations may cache the header
	 * parsed from the file so this calling this method is usually preferable than
	 * getting the header via one of the parse methods and then halting parsing.
	 * 
	 * @return a {@link SamHeader} object; will never be null but may be empty
	 * if there is no header information in the sam or bam file.
	 * 
	 * @throws IOException if there is a problem (re)parsing the header.
	 */
	SamHeader getHeader() throws IOException;
	
		@Data
		@Builder(toBuilder = true)
        public static class SamParserOptions{
			
			public static SamParserOptions DEFAULT = SamParserOptions.builder().build();
			
			@Getter(AccessLevel.NONE)
            private final boolean createMementos;
			
            private final String referenceName;
            @Singular
            private final List<@NonNull Range> referenceRanges;
            
            private final SamRecordFilter filter;
            private final SamVisitorMemento memento;
            
            
            
            Predicate<SamRecord> filterAsPredicate(){
            	if(filter==null) {
            		return r-> true;
            	}
            	return filter.asPredicate();
            }
            public boolean shouldCreateMementos() {
                return createMementos;
            }

            public Optional<String> getReferenceName() {
                return Optional.ofNullable(referenceName);
            }
            public Optional<List<Range>> getReferenceRanges() {
            	if(referenceRanges==null || referenceRanges.isEmpty()) {
            		return Optional.empty();
            	}
                return Optional.ofNullable(referenceRanges);
            }
            
            public Optional<SamRecordFilter> getFilter(){
            	return Optional.ofNullable(filter);
            }
            
            public static class SamParserOptionsBuilder{
            	public SamParserOptionsBuilder reference(String referenceName, Range referenceRange) {
            		return referenceName(referenceName)
            				.referenceRange(referenceRange);
            	}
            	public SamParserOptionsBuilder reference(String referenceName, List<Range> referenceRange) {
            		referenceName(referenceName);
            		referenceRange.forEach(this::referenceRange);
            		return this;
            	}
            	public SamParserOptionsBuilder reference(String referenceName) {
            		return referenceName(referenceName);
            	}
            }
        }
}
