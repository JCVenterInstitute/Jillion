package org.jcvi.fasta;

import org.jcvi.glyph.aa.AminoAcid;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;
import org.jcvi.glyph.aa.DefaultAminoAcidEncodedGlyphs;

public class DefaultEncodedPeptideFastaRecord extends AbstractPeptideSequenceFastaRecord {

/**
 * Default implementation of Amino Acid encoding for {@link FastaRecord} objects
 * @author naxelrod
 *
 */

    public DefaultEncodedPeptideFastaRecord(String identifier, AminoAcidEncodedGlyphs glyphs){
    	super(identifier, AminoAcid.convertToString(glyphs.decode()));
    }
    
	/**
     * @param identifier
     * @param sequence
     */
    public DefaultEncodedPeptideFastaRecord(int identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultEncodedPeptideFastaRecord(int identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    /**
     * @param identifier
     * @param sequence
     */
    public DefaultEncodedPeptideFastaRecord(String identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultEncodedPeptideFastaRecord(String identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }


	@Override
	protected CharSequence decodeAminoAcids() {
		StringBuilder result = new StringBuilder();
		for(AminoAcid aa : getValues().decode()){
			result.append(aa.getAbbreviation());
		}
		return result;
	}

	@Override
	protected AminoAcidEncodedGlyphs encodeAminoAcids(String sequence) {
		return new DefaultAminoAcidEncodedGlyphs( AminoAcid.getGlyphsFor(sequence) );
	}

	
	
}
