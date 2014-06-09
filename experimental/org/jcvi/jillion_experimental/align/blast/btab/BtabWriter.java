package org.jcvi.jillion_experimental.align.blast.btab;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion_experimental.align.blast.BlastHit;
/**
 * {@code BtabWriter} is an object that writes out {@link BlastHit}
 * information in Btab format which is described in:
 * {@literal Dubnick M. (1992) Btab--a Blast output parser. Comput Appl Biosci 8(6):601-2}.
 * <br/>
 * The parsed BLAST output is presented as single line HSP descriptions, with tab-delimited
 * fields in the following order:
 * <ol>
 * <li>Query Sequence Name</li>
 * <li>Run Date</li>
 * <li>Query Sequence Length</li>
 * <li>Blast Program Name</li>
 * <li>Blast Database Name</li>
 * <li>Subject Sequence Name</li>
 * <li>Query Align Left (1-based) - if the alignment orientation is reversed,
 *  the this value is the <em>end</em> position. </li>
 * <li>Query Align Right (1-based) - if the alignment orientation is reversed,
 *  the this value is the <em>begin</em> position. </li>
 * <li>Subject Align Left (1-based) - if the alignment orientation is reversed,
 *  the this value is the <em>end</em> position. </li>
 * <li>Subject Align Right (1-based)- if the alignment orientation is reversed,
 *  the this value is the <em>begin</em> position. </li>
 * <li>Percent Identity - The fraction of residues which are absolute 
 * matches between the query and subject sequence, expressed in percent with valid values between 0-100.</li>
 * <li>Percent Positives - The fraction of residues which are positive 
 * matches (but may not be identical matches) between the query and subject sequence, expressed in percent with valid values between 0-100.</li>
 * <li>HSP Score</li>
 * <li>Bit Score</li>
 * <li>???</li>
 * <li>Description - A freeform text field which contains the biological
# description field from the database for the subject sequence.</li>
 * <li>Query Frame - possible values (1, 2, 3, -1, -2, -3)</li>
 * <li>Query Strand - Plus or Minus</li>
 * <li>Subject full sequence length (which may include parts that did not align)</li>
 * <li>E-value</li>
 * <li>P-value - Poisson value.  Many blast tools don't compute this field so implementations
 * might either leave it blank, or duplicate the E-value.</li>
 * </ol>
 * 
 * <strong>Note</strong> fields which are described as "???" are not currently supported
 * and remain to support compatibility with other existing btab implementations.
 * 
 *  
 *  @author dkatzel
 *  @see <a href = "http://bioinformatics.oxfordjournals.org/content/8/6/601.full.pdf+html">
 *  Dubnick M. (1992) Btab--a Blast output parser. Comput Appl Biosci 8(6):601-2</a>
 */
public interface BtabWriter extends Closeable{

	void write(BlastHit hit) throws IOException;
}
