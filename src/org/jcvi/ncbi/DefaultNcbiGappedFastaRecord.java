/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
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

package org.jcvi.ncbi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * {@code DefaultNcbiGappedFastaRecord} is the default
 * implementation of {@link NcbiGappedFastaRecord}.
 * @author dkatzel
 *
 *
 */
public class DefaultNcbiGappedFastaRecord implements NcbiGappedFastaRecord {

    private final String id;
    private final String comments;
    private final List<NucleotideEncodedGlyphs> sequences;
    private final List<Gap> gaps;
    
    
    /**
     * @param id
     * @param comments
     * @param sequences
     * @param gaps
     */
    private DefaultNcbiGappedFastaRecord(String id, String comments,
            List<NucleotideEncodedGlyphs> sequences, List<Gap> gaps) {
        this.id = id;
        this.comments = comments;
        this.sequences = sequences;
        this.gaps = gaps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComments() {
        return comments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getStringRecord() {
        StringBuilder result = new StringBuilder(String.format(">%s",
                getIdentifier()));
        if(comments !=null){
            result.append(" ");
            result.append(comments);
        }
        result.append("\n");
        Iterator<Gap> gapIterator = gaps.iterator();
        for(NucleotideEncodedGlyphs sequence : sequences){
            result.append(formatSequence(sequence));
            if(gapIterator.hasNext()){
                result.append("\n>?");
                Gap gap = gapIterator.next();
                if(gap.type == Gap_Type.UNKNOWN){
                    result.append("unk");
                }
                result.append(gap.length)
                        .append("\n");
            }
        }
        return result.toString();
    }
    protected String formatSequence(NucleotideEncodedGlyphs basecalls){
        return NucleotideGlyph.convertToString(basecalls.decode()).replaceAll("(.{60})", "$1\n");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getChecksum() {
        return 0;
    }

    @Override
    public String toString() {
        return getStringRecord().toString();
    }

    /**
     * Generate a single {@link NucleotideEncodedGlyphs}
     * with {@link NucleotideGlyph#Gap}s filling
     * in the gaps between sequences.
     */
    @Override
    public NucleotideEncodedGlyphs getValues() {
        StringBuilder result = new StringBuilder();
        Iterator<NucleotideEncodedGlyphs> sequenceIterator = sequences.iterator();
        Iterator<Gap> gapIterator = gaps.iterator();
        while(sequenceIterator.hasNext()){
            result.append(
                    NucleotideGlyph.convertToString(
                            sequenceIterator.next().decode()));
            if(gapIterator.hasNext()){
                Gap gap = gapIterator.next();
                for(int i=0; i< gap.length; i++) {
                    result.append(NucleotideGlyph.Gap);
                }
            }
        }
        return new DefaultNucleotideEncodedGlyphs(result.toString());
    }
    
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + gaps.hashCode();
        result = prime * result + id.hashCode();
        result = prime * result
                + sequences.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultNcbiGappedFastaRecord)) {
            return false;
        }
        DefaultNcbiGappedFastaRecord other = (DefaultNcbiGappedFastaRecord) obj;
        
        
        if (!id.equals(other.id)) {
            return false;
        }
        if (!sequences.equals(other.sequences)) {
            return false;
        }
        if (!gaps.equals(other.gaps)) {
            return false;
        }
        if (comments == null) {
            if (other.comments != null) {
                return false;
            }
        } else if (!comments.equals(other.comments)) {
            return false;
        }
        return true;
    }




    private static class Gap{
        private final Gap_Type type;
        private final long length;
        /**
         * @param type
         * @param length
         */
        private Gap(Gap_Type type, long length) {
            this.type = type;
            this.length = length;
        }
        @Override
        public String toString() {            
            return String.format("?%s%d",
                    type ==Gap_Type.UNKNOWN? "unk":"",
                            length);
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (length ^ (length >>> 32));
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Gap)) {
                return false;
            }
            Gap other = (Gap) obj;
            if (length != other.length) {
                return false;
            }
            if (type != other.type) {
                return false;
            }
            return true;
        }
       
        
    }
    /**
     * {@code Builder} builds a {@link DefaultNcbiGappedFastaRecord}.
     * Adding Gaps and Sequences can be done separately, however,
     * the order of the gaps and sequences matter.  The first Gap
     * will be placed between the first and second sequences etc.
     * @author dkatzel
     *
     *
     */
    public static class Builder implements org.jcvi.Builder<DefaultNcbiGappedFastaRecord>{

        private final List<NucleotideEncodedGlyphs> sequences = new ArrayList<NucleotideEncodedGlyphs>();
        private final List<Gap> gaps = new ArrayList<Gap>();
        
        private final String id;
        private final String comments;
        /**
         * Constructs a new Builder with the given Id.
         * this is the same as {@link #DefaultNcbiGappedFastaRecord.Builder(String, String) new DefaultNcbiGappedFastaRecord.Builder(id,null)}
         * @param id the id of the Gapped Fasta Record.
         * @see {@link #DefaultNcbiGappedFastaRecord.Builder(String, String)}
         */
        public Builder(String id){
            this(id,null);
        }
        /**
         * Constructs a new Builder with the given Id and comments.
         * @param id the id of the Gapped Fasta Record.
         * @param comments any comments to attach to the record (may be null).
         */
        public Builder(String id, String comments) {
            if(id==null){
                throw new NullPointerException("id can not be null");
            }
            this.id = id;
            this.comments = comments;
        }
        /**
         * Add a Gap of Unknown size between the flanking sequences.
         * @return this.
         */
        public Builder addGap(){
            gaps.add(new Gap(Gap_Type.UNKNOWN, 100));
            return this;
        }
        /**
         * Add a Gap of known size between the flanking sequences.
         * @param length the length of the gap.
         * @return this.
         */
        public Builder addGap(long length){
            gaps.add(new Gap(Gap_Type.KNOWN, length));
            return this;
        }
        /**
         * Add a sequence which will be separated by other sequences
         * (to be given by additional addSequence calls)
         * by gaps.
         * @param sequence the basecalls of the sequence to add.
         * @return this.
         * @throws NullPointerException if sequence is null.
         */
        public Builder addSequence(String sequence){
            return addSequence(new DefaultNucleotideEncodedGlyphs(sequence));
        }
        /**
         * Add a sequence which will be separated by other sequences
         * (to be given by additional addSequence calls)
         * by gaps.
         * @param sequence the basecalls of the sequence to add.
         * @return this.
         * @throws NullPointerException if sequence is null.
         */
        public Builder addSequence(List<NucleotideGlyph> sequence){
            return addSequence(new DefaultNucleotideEncodedGlyphs(sequence));
        }
        /**
         * Add a sequence which will be separated by other sequences
         * (to be given by additional addSequence calls)
         * by gaps.
         * @param sequence the basecalls of the sequence to add.
         * @return this.
         * @throws NullPointerException if sequence is null.
         */
        public Builder addSequence(NucleotideEncodedGlyphs sequence){
            if(sequence ==null){
                throw new NullPointerException("sequence can not be null");
            }
            sequences.add(sequence);
            return this;
        }
        /**
        * {@inheritDoc}
        * @throws IllegalStateException if 
        * the number of given sequences is not 
        * equal to one more than the number of gaps given.
        */
        @Override
        public DefaultNcbiGappedFastaRecord build() {
            if(sequences.size() != gaps.size()+1){
                throw new IllegalStateException("invalid number of sequences vs gaps "+sequences.size() + " vs "+ gaps.size());
            }
            return new DefaultNcbiGappedFastaRecord(id, comments, sequences, gaps);
        }
        
    }

}
