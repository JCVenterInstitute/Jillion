/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultFastQRecord implements FastQRecord {

    private final String id;
    private final String comments;
    private final NucleotideEncodedGlyphs nucleotides;
    private final EncodedGlyphs<PhredQuality> qualities;
    
    public DefaultFastQRecord(String id, NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities){
        this(id, nucleotides, qualities,null);
    }
    /**
     * @param id
     * @param nucleotides
     * @param qualities
     * @param comments
     */
    public DefaultFastQRecord(String id, NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities, String comments) {
        this.id = id;
        this.nucleotides = nucleotides;
        this.qualities = qualities;
        this.comments = comments;
    }

    @Override
    public String getComment() {
        return comments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public NucleotideEncodedGlyphs getNucleotides() {
        return nucleotides;
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return qualities;
    }

}
