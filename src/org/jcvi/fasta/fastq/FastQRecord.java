/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface FastQRecord {

    String getId();
    String getComment();
    
    NucleotideEncodedGlyphs getNucleotides();
    
    EncodedGlyphs<PhredQuality> getQualities();
}
