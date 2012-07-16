package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

/*
 * Implementing an {@code AbstractPeptideSequenceFastaRecord} requires writing
 * two methods: 1) encodeAminoAcids, and 2) decodeAminoAcids
 *
 * @author naxelrod
 */

public abstract class AbstractAminoAcidSequenceFastaRecord extends AbstractFastaRecord<AminoAcid,AminoAcidSequence> implements
		AminoAcidSequenceFastaRecord {

    private final AminoAcidSequence sequence;
    private final int length;
    
    /**
     * Creates a new <code>PeptideSequenceFastaRecord</code>.
     */
    public AbstractAminoAcidSequenceFastaRecord(String identifier, String comments, CharSequence sequence)
    {
        super(identifier, comments);
        String nonWhiteSpaceSequence = sequence.toString().replaceAll("\\s+", "");
        this.sequence = encodeAminoAcids(nonWhiteSpaceSequence);
        this.length = nonWhiteSpaceSequence.length();
        
    }
    public AbstractAminoAcidSequenceFastaRecord(int identifier, String comments, CharSequence sequence) {
    	this(Integer.toString(identifier), comments, sequence);
    }
    
    public AbstractAminoAcidSequenceFastaRecord(String identifier, CharSequence sequence) {
    	this(identifier, null, sequence);
    }
    public AbstractAminoAcidSequenceFastaRecord(int identifier, CharSequence sequence) {
    	this(Integer.toString(identifier), null, sequence);
    }
    	
	protected abstract AminoAcidSequence encodeAminoAcids(String nonWhiteSpaceSequence);

	protected abstract CharSequence decodeAminoAcids();
	
	public int getLength() {
		return length;
	}
	
	@Override
	public AminoAcidSequence getSequence() {
		return this.sequence;
	}
	
	@Override
	protected CharSequence getRecordBody() {
        String result= this.decodeAminoAcids().toString().replaceAll("(.{60})", "$1"+FastaUtil.LINE_SEPARATOR);
        //some fasta parsers such as blast's formatdb
        //break if there is an extra blank line between records
        //this can happen if the sequence ends at the exact lenght of 1 line
        //(60 characters)
        long length = sequence.getLength();
        if(length >0 && length%60==0){
            return result.substring(0, result.length()-1);
        }
        return result;
	}
}
