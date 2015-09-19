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
package org.jcvi.jillion.internal.trace.fastq;

import org.jcvi.jillion.trace.fastq.FastqQualityCodec;

public class CommentedParsedFastqRecord extends ParsedFastqRecord {

    private final String comment;
  
    public CommentedParsedFastqRecord(String id, String nucleotideSequence,
            String encodedQualities, FastqQualityCodec qualityCodec,
            boolean turnOffCompression, String optionalComment) {
        super(id, nucleotideSequence, encodedQualities, qualityCodec, turnOffCompression);
        this.comment = optionalComment;
    }
    
    @Override
    public String getComment() {
        return comment;
    }
    

}
