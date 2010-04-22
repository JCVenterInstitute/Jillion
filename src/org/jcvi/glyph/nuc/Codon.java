/**
 * Codon.java
 *
 * Created: Apr 20, 2010 - 11:40:15 AM (jsitz@jcvi.org)
 *
 * Copyright 2010 J. Craig Venter Institute
 */
package org.jcvi.glyph.nuc;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * A <code>Codon</code> represents a triplet of {@link NucleotideGlyph}s which specify an 
 * amino acid.
 *
 * @author jsitz@jcvi.org
 */
public class Codon
{
    /** An array of three glyphs representing the codon. */
    private final NucleotideGlyph[] codonGlyphs;

   /**
     * Creates a new <code>Codon</code>.
     */
    private Codon()
    {
        this.codonGlyphs = new NucleotideGlyph[3];
    }

    /**
     * Creates a new <code>Codon</code>.
     */
    public Codon(NucleotideGlyph base1, NucleotideGlyph base2, NucleotideGlyph base3)
    {
        this();

        this.codonGlyphs[0] = base1;
        this.codonGlyphs[1] = base2;
        this.codonGlyphs[2] = base3;
    }
    
    /**
     * Creates a new <code>Codon</code>.
     *
     * @param sequence The sequence to extract the codon from.
     * @param offset The offset of the first nucleotide of the codon in the sequence.
     */
    public Codon(List<NucleotideGlyph> sequence, int offset)
    {
        this(sequence.get(offset), sequence.get(offset+1), sequence.get(offset+2));
    }

    /**
     * Creates a new <code>Codon</code>.
     *
     * @param sequence The sequence to extract the codon from.
     * @param offset The offset of the first nucleotide of the codon in the sequence.
     */
    public Codon(NucleotideEncodedGlyphs sequence, int offset)
    {
        this(sequence.decode(Range.buildRange(CoordinateSystem.RESIDUE_BASED, offset, offset+2)), offset);
    }

    /**
     * Creates a new <code>Codon</code>.
     */
    public Codon(String bases)
    {
        this();

        if (bases.length() != 3) throw new IllegalStateException("Codon initialization strings must be 3 characters in length");

        this.codonGlyphs[0] = NucleotideGlyph.getGlyphFor(bases.charAt(0));
        this.codonGlyphs[1] = NucleotideGlyph.getGlyphFor(bases.charAt(1));
        this.codonGlyphs[2] = NucleotideGlyph.getGlyphFor(bases.charAt(2));
    }

    public boolean matches(Codon that)
    {
        for (int i = 0; i < 3; i++)
        {
            final NucleotideGlyph base = this.codonGlyphs[i];
            final NucleotideGlyph query = that.codonGlyphs[i];

            if (!base.matches(query)) return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder();

        for (final NucleotideGlyph nucleotide : this.codonGlyphs)
        {
            str.append(nucleotide.getCharacter());
        }

        return str.toString();
    }
}
