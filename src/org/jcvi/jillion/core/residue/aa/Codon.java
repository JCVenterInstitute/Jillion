/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;

/**
 * A <code>Codon</code> represents a triplet of {@link Nucleotide}s which specify an 
 * amino acid.  
 *
 * @author dkatzel
 * @author jsitz@jcvi.org
 */
public final class Codon
{
    
    private static final Codon START_CODON;
    private static final List<Codon> STOP_CODONS;
    private static final Map<List<Nucleotide>, Codon> CODON_MAP;
    
    private static final Map<String, AminoAcid> AMINO_ACID_MAP;
    /** An array of three glyphs representing the codon. */
    private final Nucleotide[] codonGlyphs;
    /**
     * The AminoAcid this Codon translates into.
     */
    private final AminoAcid aminoAcid;
    
    static{
        int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(175);
		AMINO_ACID_MAP = new HashMap<String, AminoAcid>(mapSize);
        
        AMINO_ACID_MAP.put("TTC", AminoAcid.Phenylalanine);
        AMINO_ACID_MAP.put("TTT", AminoAcid.Phenylalanine);
        AMINO_ACID_MAP.put("TTY", AminoAcid.Phenylalanine);
        
        AMINO_ACID_MAP.put("TCT", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCC", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCA", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCG", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCN", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCM", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCR", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCW", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCS", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCY", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCK", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCV", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCH", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCD", AminoAcid.Serine);
        AMINO_ACID_MAP.put("TCB", AminoAcid.Serine);
        
        AMINO_ACID_MAP.put("TAT", AminoAcid.Tyrosine);
        AMINO_ACID_MAP.put("TAC", AminoAcid.Tyrosine);
        AMINO_ACID_MAP.put("TAY", AminoAcid.Tyrosine);
        //stops
        AMINO_ACID_MAP.put("TAA", null);
        AMINO_ACID_MAP.put("TAG", null);
        AMINO_ACID_MAP.put("TAR", null);
        //start
        AMINO_ACID_MAP.put("TGA", null);        
        
        AMINO_ACID_MAP.put("TGT", AminoAcid.Cysteine);
        AMINO_ACID_MAP.put("TGC", AminoAcid.Cysteine);
        AMINO_ACID_MAP.put("TGY", AminoAcid.Cysteine);        
        
        AMINO_ACID_MAP.put("TGG", AminoAcid.Tryptophan);
        
        AMINO_ACID_MAP.put("CTT", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTC", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTA", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTG", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTM", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTR", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTW", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTS", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTY", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTK", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTV", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTH", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTD", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTB", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("CTN", AminoAcid.Leucine);
        
        AMINO_ACID_MAP.put("TTA", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("TTG", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("TTR", AminoAcid.Leucine);
        
        AMINO_ACID_MAP.put("YTA", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("YTG", AminoAcid.Leucine);
        AMINO_ACID_MAP.put("YTR", AminoAcid.Leucine);
        
        AMINO_ACID_MAP.put("CCT", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCC", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCA", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCG", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCM", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCR", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCW", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCS", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCY", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCK", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCV", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCH", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCD", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCB", AminoAcid.Proline);
        AMINO_ACID_MAP.put("CCN", AminoAcid.Proline);
        
        AMINO_ACID_MAP.put("CAT", AminoAcid.Histidine);
        AMINO_ACID_MAP.put("CAC", AminoAcid.Histidine);
        AMINO_ACID_MAP.put("CAY", AminoAcid.Histidine);
        
        AMINO_ACID_MAP.put("CAA", AminoAcid.Glutamine);
        AMINO_ACID_MAP.put("CAG", AminoAcid.Glutamine); 
        AMINO_ACID_MAP.put("CAR", AminoAcid.Glutamine);
        
        AMINO_ACID_MAP.put("CGT", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGC", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGA", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGG", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGM", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGR", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGW", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGS", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGY", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGK", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGV", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGH", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGD", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGB", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("CGN", AminoAcid.Arginine);
        
        AMINO_ACID_MAP.put("AGA", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("AGG", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("AGR", AminoAcid.Arginine);
        
        AMINO_ACID_MAP.put("MGA", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("MGG", AminoAcid.Arginine);
        AMINO_ACID_MAP.put("MGR", AminoAcid.Arginine);
        
        
        AMINO_ACID_MAP.put("ATT", AminoAcid.Isoleucine);
        AMINO_ACID_MAP.put("ATC", AminoAcid.Isoleucine);
        AMINO_ACID_MAP.put("ATA", AminoAcid.Isoleucine);
        AMINO_ACID_MAP.put("ATY", AminoAcid.Isoleucine);
        AMINO_ACID_MAP.put("ATW", AminoAcid.Isoleucine);
        AMINO_ACID_MAP.put("ATM", AminoAcid.Isoleucine);
        AMINO_ACID_MAP.put("ATH", AminoAcid.Isoleucine);
        
        AMINO_ACID_MAP.put("ATG", AminoAcid.Methionine);
        
        AMINO_ACID_MAP.put("ACT", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACC", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACA", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACG", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACM", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACR", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACW", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACS", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACY", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACK", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACV", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACH", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACD", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACB", AminoAcid.Threonine);
        AMINO_ACID_MAP.put("ACN", AminoAcid.Threonine);
        
        AMINO_ACID_MAP.put("AAT", AminoAcid.Asparagine);
        AMINO_ACID_MAP.put("AAC", AminoAcid.Asparagine);
        AMINO_ACID_MAP.put("AAY", AminoAcid.Asparagine);
        
        AMINO_ACID_MAP.put("AAA", AminoAcid.Lysine);
        AMINO_ACID_MAP.put("AAG", AminoAcid.Lysine);
        AMINO_ACID_MAP.put("AAR", AminoAcid.Lysine);
        
        AMINO_ACID_MAP.put("AGT", AminoAcid.Serine);
        AMINO_ACID_MAP.put("AGC", AminoAcid.Serine);
        AMINO_ACID_MAP.put("AGY", AminoAcid.Serine);
        
        AMINO_ACID_MAP.put("GTT", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTC", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTA", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTG", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTM", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTR", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTW", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTS", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTY", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTK", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTV", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTH", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTD", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTB", AminoAcid.Valine);
        AMINO_ACID_MAP.put("GTN", AminoAcid.Valine);
        
        AMINO_ACID_MAP.put("GCT", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCC", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCA", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCG", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCM", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCR", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCW", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCS", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCY", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCK", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCV", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCH", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCD", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCB", AminoAcid.Alanine);
        AMINO_ACID_MAP.put("GCN", AminoAcid.Alanine);
        
        AMINO_ACID_MAP.put("GAT", AminoAcid.Aspartic_Acid);
        AMINO_ACID_MAP.put("GAC", AminoAcid.Aspartic_Acid);
        AMINO_ACID_MAP.put("GAY", AminoAcid.Aspartic_Acid);
        
        AMINO_ACID_MAP.put("GAA", AminoAcid.Glutamic_Acid);
        AMINO_ACID_MAP.put("GAG", AminoAcid.Glutamic_Acid);
        AMINO_ACID_MAP.put("GAR", AminoAcid.Glutamic_Acid);
        
        AMINO_ACID_MAP.put("GGT", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGC", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGA", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGG", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGM", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGR", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGW", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGS", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGY", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGK", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGV", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGH", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGD", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGB", AminoAcid.Glycine);
        AMINO_ACID_MAP.put("GGN", AminoAcid.Glycine);
        
        CODON_MAP = new HashMap<List<Nucleotide>, Codon>(mapSize);
        for(Entry<String, AminoAcid> entry : AMINO_ACID_MAP.entrySet()){
            List<Nucleotide> codon = asList(new NucleotideSequenceBuilder(entry.getKey()));
            CODON_MAP.put(codon, new Codon(codon.get(0),codon.get(1),codon.get(2), 
                    entry.getValue()));
        }
        
        START_CODON= Codon.getCodonFor("ATG");
        STOP_CODONS= Arrays.asList(
                Codon.getCodonFor("TAA"),
                Codon.getCodonFor("TAG"),
                Codon.getCodonFor("TAR"),
                Codon.getCodonFor("TGA"),
                Codon.getCodonFor("TRA"));
    }
    
    private static List<Nucleotide> asList(NucleotideSequenceBuilder builder){
    	List<Nucleotide> list = new ArrayList<Nucleotide>((int)builder.getLength());
    	for(Nucleotide n : builder){
    		list.add(n);
    	}
    	return list;
    }
   
    protected static final Map<List<Nucleotide>, Codon> getCodonMap() {
        return CODON_MAP;
    }
    public static List<Codon> getCodonsFor(String basecalls){
        return getCodonsFor(basecalls, Frame.ZERO);
    }
    public static List<Codon> getCodonsFor(String basecalls, Frame frame){
        if(frame ==null){
            throw new IllegalArgumentException("frame can not be null");
        }
        List<Codon> codons = new ArrayList<Codon>(basecalls.length()/3 );
        for(int i=frame.getFrame(); i<=basecalls.length()-3; i+=3){
            codons.add(getCodonFor(basecalls.substring(i, i+3)));
        }
        return codons;
    }
    public static List<Codon> getCodonsFor(NucleotideSequence basecalls){
       return getCodonsFor(basecalls,Frame.ZERO);
    }
    public static List<Codon> getCodonsFor(NucleotideSequence basecalls, Frame frame){
        String ungappedString = new NucleotideSequenceBuilder(basecalls)
        								.ungap()
        								.toString();
    	return getCodonsFor(ungappedString,frame);
     }
    public static Codon getCodonFor(Nucleotide base1, Nucleotide base2, Nucleotide base3){
        return getCodonFor(Arrays.asList(base1,base2,base3));
        
    }
    public static Codon getCodonFor(String triplet){
        if(triplet.length() !=3){
            throw new IllegalArgumentException("triplet must have 3 bases");
        }
        return getCodonFor(asList(new NucleotideSequenceBuilder(triplet.substring(0, 3))));
    }
    public static Codon getCodonFor(List<Nucleotide> triplet){
        return getCodonByOffset(triplet,0);
    }
    public static Codon getCodonFor(NucleotideSequence triplet){
        return getCodonByOffset(triplet, 0);
    }
    
    public static Codon getCodonByOffset(String basecalls, int offset){
        final String triplet = basecalls.substring(offset,offset+3);
        return getCodonByOffset(
                asList(new NucleotideSequenceBuilder(triplet)),
                0);
    }
    public static Codon getCodonByOffset(NucleotideSequence basecalls, int offset){
        if(offset<0){
            throw new IllegalArgumentException("offset must be >=0 "+ offset);
        }
        if(basecalls.getLength()<offset+3){
            throw new IllegalArgumentException("must have at least 3 nucleotides after given offset "+ (basecalls.getLength()-(offset+3)));
        }
        List<Nucleotide> list = new ArrayList<Nucleotide>(3);
        Iterator<Nucleotide> iter = basecalls.iterator(new Range.Builder(3).shift(offset).build());
        list.add(iter.next());
        list.add(iter.next());
        list.add(iter.next());
        return CODON_MAP.get(list);
    }
    public static Codon getCodonByOffset(List<Nucleotide> bases, int offset){
        if(offset<0){
            throw new IllegalArgumentException("offset must be >=0 "+ offset);
        }
        if(bases.size()<offset+3){
            throw new IllegalArgumentException("must have at least 3 nucleotides after given offset "+ (bases.size()-(offset+3)));
        }
        return CODON_MAP.get(bases.subList(offset, offset+3));
    }
    /**
     * Creates a new <code>Codon</code>.
     */
    private Codon(Nucleotide base1, Nucleotide base2, Nucleotide base3, AminoAcid aminoAcid)
    {
        this.codonGlyphs = new Nucleotide[]{base1,base2,base3};
        this.aminoAcid = aminoAcid;
    }
    
    
    /**
     * Get the Amino Acid that is translated from this Codon.
     * @return the AminoAcid, this will be {@code null} for 
     * stop codons.
     */
    public AminoAcid getAminoAcid() {
        return aminoAcid;
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(codonGlyphs);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Codon)) {
            return false;
        }
        Codon other = (Codon) obj;
        return Arrays.equals(codonGlyphs, other.codonGlyphs);
    }
    
    
    /**
	 * @return the codonGlyphs
	 */
	public List<Nucleotide> getNucleotides() {
		List<Nucleotide> triplet = new ArrayList<Nucleotide>(3);
		for(int i=0; i< 3; i++){
			triplet.add(codonGlyphs[i]);
		}
		return triplet;
	}
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder("[");

        for (final Nucleotide nucleotide : this.codonGlyphs)
        {
            str.append(nucleotide.getCharacter());
        }
        str.append(']');
        return str.toString();
    }
    /**
     * Is this Codon instance the start codon?
     * @return {@code true} if this Codon is the start codon; {@code false}
     * otherwise.
     */
    public boolean isStartCodon(){
        return equals(START_CODON);
    }
    /**
     * Is this Codon instance a stop codon?
     * @return {@code true} if this Codon is a stop codon; {@code false}
     * otherwise.
     */
    public boolean isStopCodon(){
        for(Codon stopCodon : STOP_CODONS){
            if(equals(stopCodon)){
                return true;
            }
        }
        return false;
    }
    /**
     * Get the Codon instance that represents the Start Codon.
     * @return the Codon that represents a start Codon (not null).
     */
    public static Codon getStartCodon() {
        return START_CODON;
    }
    /**
     * Get the List of the stop codons.
     * @return a list containing the stop codons (not null).
     */
    public static List<Codon> getStopCodons() {
        return STOP_CODONS;
    }
    
    public static enum Frame{
        ZERO(0),
        ONE(1),
        TWO(2);
        
        private int frame;
        
        public  final int getFrame() {
            return frame;
        }
        Frame(int frame){
            this.frame = frame;
        }
        /**
         * Parse a {@link Frame} from the given int value.
         * Valid values are <code>0</code> to <code>2</code>
         * inclusive.
         * @param frame
         * @return a {@link Frame}
         * @throws IllegalArgumentException if <code> frame < 0 || frame > 2</code>
         */
        public static Frame parseFrame(int frame){
            for(Frame f : Frame.values()){
                if(f.frame == frame){
                    return f;
                }
            }
         
            throw new IllegalArgumentException("unable to parse frame " + frame);
        }
    }
    
}
