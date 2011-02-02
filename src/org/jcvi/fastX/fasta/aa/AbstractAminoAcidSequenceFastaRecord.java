package org.jcvi.fastX.fasta.aa;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.jcvi.fastX.fasta.AbstractFastaRecord;
import org.jcvi.fastX.fasta.FastaUtil;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.aa.AminoAcid;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;

/*
 * Implementing an {@code AbstractPeptideSequenceFastaRecord} requires writing
 * two methods: 1) encodeAminoAcids, and 2) decodeAminoAcids
 *
 * @author naxelrod
 */

public abstract class AbstractAminoAcidSequenceFastaRecord extends AbstractFastaRecord<AminoAcidEncodedGlyphs> implements
		AminoAcidSequenceFastaRecord {

    private final AminoAcidEncodedGlyphs sequence;
    private final int length;
    private final long checksum;
    
    /**
     * Creates a new <code>PeptideSequenceFastaRecord</code>.
     */
    public AbstractAminoAcidSequenceFastaRecord(String identifier, String comments, CharSequence sequence)
    {
        super(identifier, comments);
        String nonWhiteSpaceSequence = sequence.toString().replaceAll("\\s+", "");
        this.checksum = this.calculateCheckSum(nonWhiteSpaceSequence);
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
    	
	protected abstract AminoAcidEncodedGlyphs encodeAminoAcids(String nonWhiteSpaceSequence);

	protected abstract CharSequence decodeAminoAcids();
	
	public int getLength() {
		return length;
	}
	
	@Override
	public AminoAcidEncodedGlyphs getValue() {
		return this.sequence;
	}
	
	@Override
	protected CharSequence getRecordBody() {
        String result= this.decodeAminoAcids().toString().replaceAll("(.{60})", "$1"+FastaUtil.CR);
        //some fasta parsers such as blast's formatdb
        //break if there is an extra blank line between records
        //this can happen if the sequence ends at the exact lenght of 1 line
        //(60 characters)
        if(getLength() >0 && getLength()%60==0){
            return result.substring(0, result.length()-1);
        }
        return result;
	}
	
    protected long calculateCheckSum(CharSequence data)
    {
        final Checksum checksummer = new CRC32();
        for (int i = 0; i < data.length(); i++)
        {
            checksummer.update(data.charAt(i));
        }
        return checksummer.getValue();
    }


}
