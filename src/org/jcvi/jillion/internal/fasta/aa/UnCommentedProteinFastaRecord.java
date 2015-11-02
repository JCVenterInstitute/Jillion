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
package org.jcvi.jillion.internal.fasta.aa;

import java.util.regex.Pattern;

import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.internal.fasta.FastaUtil;
/**
 * {@code UnCommentedProteinFastaRecord} is an implementation
 * of {@link ProteinFastaRecord} that saves
 * memory by not having a reference to a comment.
 * All calls to {@link #getComment()} will return null.
 * @author dkatzel
 *
 */
public class UnCommentedProteinFastaRecord implements ProteinFastaRecord{

	private static final int NUMBER_OF_BASES_PER_LINE = 60;
	private static final Pattern LINE_SPLITTER_PATTERN = Pattern.compile(String.format("(.{%s})", NUMBER_OF_BASES_PER_LINE));
	private static final String LINE_SPLITTER_REPLACEMENT = "$1"+FastaUtil.getLineSeparator();
	
	private final ProteinSequence sequence;
	private final String id;

    public UnCommentedProteinFastaRecord(String id, ProteinSequence sequence){
    	if(id == null){
            throw new NullPointerException("identifier can not be null");
        }        
         if(sequence ==null){
         	throw new NullPointerException("sequence can not be null");
         }
         this.id = id;
         this.sequence = sequence;
    }
   
    

    /**
     * @return A <code>String</code>.
     */
    public final String getId()
    {
        return this.id;
    }
    /**
     *{@inheritDoc}.
     */
    @Override
    public String getComment()
    {
        return null;
    }
    @Override
    public final ProteinSequence getSequence() 
    {
        return this.sequence;
    }
    private final String toFormattedString()
    {
    	int bufferSize = computeFormattedBufferSize();
        final StringBuilder record = new StringBuilder(bufferSize);
        record.append(FastaUtil.getHeaderPrefix()).append(
                this.getId());
        if (this.getComment() != null) {
        	record.append(' ').append(this.getComment());
        }
        record.append(FastaUtil.getLineSeparator())
		        .append(this.getRecordBody())
		        .append(FastaUtil.getLineSeparator());
        
        return record.toString();
    }
    
    private int computeFormattedBufferSize() {
    	//2 extra bytes for '>' and '\n'
		int size = 2 + id.length();
		if(getComment()!=null){
			//extra byte for the space
			size +=1 + getComment().length();
		}
		int seqLength=(int)sequence.getLength();
		int numberOfLines = seqLength/NUMBER_OF_BASES_PER_LINE +1;
		return size + seqLength+numberOfLines;
	}
	
    
    /**
     * 
    * Gets the entire formatted fasta record as a String.
     */
    @Override
    public final String toString()
    {
        return this.toFormattedString();
    }
    
    private String getRecordBody()
    {
        String result= LINE_SPLITTER_PATTERN.matcher(this.sequence.toString()).replaceAll(LINE_SPLITTER_REPLACEMENT);
        //some fasta parsers such as blast's formatdb
        //break if there is an extra blank line between records
        //this can happen if the sequence ends at the exact length of 1 line
        long length = sequence.getLength();
        if(length >0 && length%NUMBER_OF_BASES_PER_LINE==0){
            return result.substring(0, result.length()-1);
        }
        return result;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id.hashCode();
        result = prime * result + this.getSequence().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof ProteinFastaRecord)){
            return false;
        }
        ProteinFastaRecord other = (ProteinFastaRecord)obj;
		return 
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence()) 
        && ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }   
   
}

