/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.TextFileVisitor;

public interface FastQFileVisitor extends TextFileVisitor{

    boolean visitBeginBlock(String id, String optionalComment);
    
    boolean visitEndBlock();
    
    boolean visitNucleotides(NucleotideEncodedGlyphs nucleotides);
    
    boolean visitEncodedQualities(String encodedQualities);
}
