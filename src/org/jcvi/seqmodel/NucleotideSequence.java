package org.jcvi.seqmodel;

import java.util.List;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * {@code NucleotideSequence} is the implementation of the {@link SequenceI} interface
 * using the {@link DefaultNucleotideGlyphCodec} to encode the sequence as 
 * {@link NucleotideGlyphs}.  
 * 
 * @author naxelrod
 */

public class NucleotideSequence extends AbstractEncodedSequence<NucleotideGlyph> {

	public NucleotideSequence() {
		super(DefaultNucleotideGlyphCodec.getInstance());
	}
	
	// Copy a SequenceI object for constructor
	public NucleotideSequence(SequenceI s) {
		this();
		this.id = s.getId();
		this.type = s.getType();
		this.giNumber = s.getGiNumber();
		this.accession = s.getAccession();
		this.start = s.getStart();
		this.end = s.getEnd();
		if (s instanceof NucleotideSequence) {
			NucleotideSequence ns = (NucleotideSequence) s;
			this.glyphs = ns.getGlyphs();
		} else {
			this.sequence = s.getSequence();
		}
	}
	
	public NucleotideSequence(String sequence) {
		this();
		this.setSequence(sequence);
	}
	public NucleotideSequence(String id, String sequence) {
		this(sequence);
		this.id = id;
	}
	public NucleotideSequence(EncodedGlyphs<NucleotideGlyph> glyphs) {
		this();
		this.glyphs = glyphs;
	}
	public NucleotideSequence(String id, EncodedGlyphs<NucleotideGlyph> glyphs) {
		this(glyphs);
		this.id = id;
	}
	
	public NucleotideSequence(String id, String accession, Long giNumber, String sequence) {
		this(id, sequence);
		this.accession = accession;
		this.giNumber = giNumber;
	}	
	
	public void setSequence(String sequence) {
		this.glyphs = new DefaultNucleotideEncodedGlyphs(sequence);
	}
	
	public List<NucleotideGlyph> convertStringToGlyph(String sequence) {
		return NucleotideGlyph.getGlyphsFor(sequence);
	}

	
}
