package org.jcvi.fastX.fasta.aa;

import org.jcvi.fastX.fasta.FastaRecord;
import org.jcvi.glyph.aa.AminoAcid;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;
import org.jcvi.glyph.aa.DefaultAminoAcidEncodedGlyphs;

public class DefaultAminoAcidEncodedSequenceFastaRecord extends AbstractAminoAcidSequenceFastaRecord {

/**
 * Default implementation of Amino Acid encoding for {@link FastaRecord} objects
 * @author naxelrod
 *
 */

    public DefaultAminoAcidEncodedSequenceFastaRecord(String identifier, AminoAcidEncodedGlyphs glyphs){
    	super(identifier, AminoAcid.convertToString(glyphs.decode()));
    }
    
	/**
     * @param identifier
     * @param sequence
     */
    public DefaultAminoAcidEncodedSequenceFastaRecord(int identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultAminoAcidEncodedSequenceFastaRecord(int identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    /**
     * @param identifier
     * @param sequence
     */
    public DefaultAminoAcidEncodedSequenceFastaRecord(String identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultAminoAcidEncodedSequenceFastaRecord(String identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }


	@Override
	protected CharSequence decodeAminoAcids() {
		StringBuilder result = new StringBuilder();
		for(AminoAcid aa : getValue().decode()){
			result.append(aa.getAbbreviation());
		}
		return result;
	}

	@Override
	protected AminoAcidEncodedGlyphs encodeAminoAcids(String sequence) {
		return new DefaultAminoAcidEncodedGlyphs( AminoAcid.getGlyphsFor(sequence) );
	}

	
	
}
