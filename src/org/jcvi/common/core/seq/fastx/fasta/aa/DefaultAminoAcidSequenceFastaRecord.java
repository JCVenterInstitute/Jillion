package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;

public class DefaultAminoAcidSequenceFastaRecord extends AbstractFastaRecord<AminoAcid,AminoAcidSequence> 
												implements	AminoAcidSequenceFastaRecord {
	private final AminoAcidSequence sequence;

    public DefaultAminoAcidSequenceFastaRecord(String identifier, AminoAcidSequence sequence){
    	this(identifier, null, sequence);
    }
    public DefaultAminoAcidSequenceFastaRecord(String identifier, String comments, AminoAcidSequence sequence){
    	super(identifier, comments);
    	this.sequence = sequence;
    }

    /**
     * @param identifier
     * @param sequence
     */
    public DefaultAminoAcidSequenceFastaRecord(String identifier, CharSequence sequence) {
        this(identifier, null,sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultAminoAcidSequenceFastaRecord(String identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments);
        String nonWhiteSpaceSequence = sequence.toString().replaceAll("\\s+", "");
        this.sequence = new AminoAcidSequenceBuilder(nonWhiteSpaceSequence).build();
    }


	
	@Override
	public AminoAcidSequence getSequence() {
		return this.sequence;
	}
	
	@Override
	protected CharSequence getRecordBody() {
        String result= this.sequence.toString().replaceAll("(.{60})", "$1"+FastaUtil.LINE_SEPARATOR);
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
