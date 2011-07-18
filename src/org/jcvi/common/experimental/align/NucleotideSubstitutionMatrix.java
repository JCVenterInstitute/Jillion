/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * NucleotideSubstitutionMatrix
 *
 * Created: Aug 7, 2009 - 10:52:31 AM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.common.experimental.align;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.jcvi.common.core.seq.nuc.NucleotideGlyph;

/**
 * A <code>NucleotideSubstitutionMatrix</code> is a simple implementation of a 
 * {@link SubstitutionMatrix} using DNA nucleotides.  It supports all recognized ambiguity 
 * codes as well as the "empty" <code>N</code> ambiguity code.
 *
 * @author jsitz@jcvi.org
 */
public class NucleotideSubstitutionMatrix implements SubstitutionMatrix<NucleotideGlyph>
{
    
    /** The single-dimensional size of the matrix. */
    private static final int MATRIX_SIZE = NucleotideGlyph.values().length;
    
    /** The name of this substitution matrix. */
    private final String name;
    
    /** A {@link ByteBuffer} containing the flattened matrix of score values. */
    private final ByteBuffer scores;
    
    /**
     * Creates a new <code>NucleotideScoringMatrix</code>.
     * 
     * @param name The name of this scoring matrix.
     * @param defaultScore The default score to fill the matrix with.
     */
    private NucleotideSubstitutionMatrix(String name, int defaultScore, int identityScore,
            int gapScore, int unspecifiedMatchScore,int ambiguityScore)
    {
        
        super();
        
        this.name = name;
        this.scores = ByteBuffer.allocate(NucleotideSubstitutionMatrix.MATRIX_SIZE * NucleotideSubstitutionMatrix.MATRIX_SIZE);
        
        final byte score = this.normalizeScore(defaultScore);
        while(this.scores.hasRemaining())
        {
            this.scores.put(score);
        }
        this.scores.flip();
        setIdentityScore(identityScore);
        setGapScore(gapScore);
        setUnspecifiedMatchScore(unspecifiedMatchScore);
        setAmbiguityScore(ambiguityScore);
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#setIdentityScore(int)
     */
    private void setIdentityScore(int score)
    {
        for (NucleotideGlyph glyph : NucleotideGlyph.values())
        {
            this.setScore(glyph, glyph, score);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#setGapScore(int)
     */
    private void setGapScore(int score)
    {
        this.setAllScores(NucleotideGlyph.Gap, score);
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#setUnspecifiedMatchScore(int)
     */
    private void setUnspecifiedMatchScore(int score)
    {
        this.setAllScores(NucleotideGlyph.Unknown, score);
    }
    
    /**
     * Sets the scores assigned to matches made from a real nucleotide to an ambiguity code.
     * 
     * @param score The score to apply to a ambiguity match.
     */
    private void setAmbiguityScore(int score)
    {
        for(NucleotideGlyph g : NucleotideGlyph.getGlyphsFor("ACGT")){
            this.setScores(g, score, g.getNucleotides());
        }
       
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#setScores(char, int, char)
     */
    private void setScores(NucleotideGlyph a, int value, Collection<NucleotideGlyph> bs)
    {
        final byte boundedValue = this.normalizeScore(value);
        final int aIndex = a.ordinal();
        
        for (NucleotideGlyph b : bs)
        {
            final int bIndex = b.ordinal();
            this.setScore(aIndex, bIndex, boundedValue);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#setAllScores(char, int)
     */
    private void setAllScores(NucleotideGlyph a, int value)
    {
        final byte boundedValue = this.normalizeScore(value);
        final int aIndex = a.ordinal();
        for (int i = 0; i < NucleotideSubstitutionMatrix.MATRIX_SIZE; i++)
        {
            this.setScore(aIndex, i, boundedValue);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#setScore(char, char, int)
     */
    private void setScore(NucleotideGlyph a, NucleotideGlyph b, int value)
    {
        this.setScore(a.ordinal(), b.ordinal(), this.normalizeScore(value));
    }
    
    /**
     * Sets the score at a particular pair of coordinates in the matrix.  Since ordering does 
     * not matter, this will set both <code>[a, b]</code> and <code>[b, a]</code>.
     * 
     * @param a The first coordinate.
     * @param b The second coordinate.
     * @param value The scoring value to set.
     */
    private void setScore(int a, int b, byte value)
    {
        final int stdIndex = (a * NucleotideSubstitutionMatrix.MATRIX_SIZE) + b;
        final int revIndex = (a * NucleotideSubstitutionMatrix.MATRIX_SIZE) + b;
        this.scores.put(stdIndex, value);
        this.scores.put(revIndex, value);
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#getScore(char, char)
     */
    public byte getScore(char a, char b)
    {
        return this.getScore(this.indexOf(a), this.indexOf(b));
    }
    @Override
    public byte getScore(NucleotideGlyph a, NucleotideGlyph b)
    {
        return this.getScore(a.ordinal(), b.ordinal());
    }
    /**
     * Returns the effective matrix coordinate of a given sequence character. If the sequence
     * character is unrecognized, the value returned by {@link #getDefaultCharacter()} will
     * be used instead.  This coordinate is the row or column index containing scoring values 
     * for this character.
     * 
     * @param a The sequence character to look up.
     * @return The integer coordinate of the character in the matrix.
     */
    private int indexOf(char a)
    {
        final NucleotideGlyph glyph = NucleotideGlyph.getGlyphFor(a);
        if(glyph ==null){
            return NucleotideGlyph.Unknown.ordinal();
        }
        return glyph.ordinal();
       
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.SubstitutionMatrix#getDefaultCharacter()
     */
    @Override
    public NucleotideGlyph getDefaultCharacter()
    {
        return NucleotideGlyph.Unknown;
    }
    
    /**
     * Returns the score for the given logical row and column in the matrix.
     * 
     * @param a The index of the row.
     * @param b The inex of the column.
     * @return The score as a <code>byte</code>.
     */
    private byte getScore(int a, int b)
    {
        return this.scores.get((a * NucleotideSubstitutionMatrix.MATRIX_SIZE) + b);
    }
    
    /**
     * Normalizes the value of an integer score, forcing it into the ranges acceptable to the
     * matrix.
     * 
     * @param intScore The integer score value.
     * @return The score value as a bounded <code>byte</code>.
     */
    private byte normalizeScore(int intScore)
    {
        return (byte)(Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, intScore)));
    }
    
    public static class Builder implements org.jcvi.Builder<NucleotideSubstitutionMatrix>{

        private final String name;
        private Integer defaultScore,identityScore,
                gapScore,unspecifiedMatchScore,ambiguityScore;
        
        public Builder(String name){
            if(name ==null){
                throw new IllegalArgumentException("name can not be null");
            }
            this.name = name;
        }
        public Builder defaultScore(int defaultScore){
            this.defaultScore = defaultScore;
            return this;
        }
        public Builder identityScore(int identityScore){
            this.identityScore = identityScore;
            return this;
        }
        public Builder gapScore(int gapScore){
            this.gapScore = gapScore;
            return this;
        }
        public Builder unspecifiedMatchScore(int unspecifiedMatchScore){
            this.unspecifiedMatchScore = unspecifiedMatchScore;
            return this;
        }
        public Builder ambiguityScore(int ambiguityScore){
            this.ambiguityScore = ambiguityScore;
            return this;
        }
        @Override
        public NucleotideSubstitutionMatrix build() {
            //all Integers should be autoboxed so unset values 
            //will throw NullPointerExceptions
            return new NucleotideSubstitutionMatrix(name, defaultScore, identityScore, gapScore, unspecifiedMatchScore, ambiguityScore);
        }
        
    }
}
