package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.util.regex.Pattern;

import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.ObjectsUtil;
/**
 * {@code NucleotideSequenceFastaRecord} is an implementation
 * of {@link FastaRecord} whose sequences are {@link NucleotideSequence}s.
 * @author dkatzel
 *
 */
class UnCommentedNucleotideSequenceFastaRecord implements FastaRecord<Nucleotide,NucleotideSequence>{

	private static final int NUMBER_OF_BASES_PER_LINE = 60;
	private static final Pattern LINE_SPLITTER_PATTERN = Pattern.compile(String.format("(.{%s})", NUMBER_OF_BASES_PER_LINE));
	private static final String LINE_SPLITTER_REPLACEMENT = "$1"+FastaUtil.LINE_SEPARATOR;
	
	private final NucleotideSequence sequence;
	private final String id;

    public UnCommentedNucleotideSequenceFastaRecord(String id, NucleotideSequence sequence){
    	if(id == null){
            throw new IllegalArgumentException("identifier can not be null");
        }
        this.id = id;
         if(sequence ==null){
         	throw new NullPointerException("sequence can not be null");
         }
         this.sequence = sequence;
    }
   
    

    /**
     * @return A <code>String</code>.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return A <code>String</code>.
     */
    public String getComment()
    {
        return null;
    }
    @Override
    public NucleotideSequence getSequence() 
    {
        return this.sequence;
    }
    public String toFormattedString()
    {
    	int bufferSize = computeFormattedBufferSize();
        final StringBuilder record = new StringBuilder(bufferSize);
        record.append(FastaUtil.HEADER_PREFIX).append(
                this.getId());
        if (this.getComment() != null) {
        	record.append(' ').append(this.getComment());
        }
        record.append(FastaUtil.LINE_SEPARATOR);
        record.append(this.getRecordBody());
        record.append(FastaUtil.LINE_SEPARATOR);
        
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
    * Gets the entire formatted fasta record as a String,
    * same as {@link #toFormattedString()}.
    * @see #toFormattedString()
     */
    @Override
    public String toString()
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
        if (!(obj instanceof DefaultNucleotideSequenceFastaRecord)){
            return false;
        }
        DefaultNucleotideSequenceFastaRecord other = (DefaultNucleotideSequenceFastaRecord)obj;
		return 
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence()) 
        && ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }   
   
}
