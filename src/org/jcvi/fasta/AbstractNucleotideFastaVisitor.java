/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public abstract class AbstractNucleotideFastaVisitor extends AbstractFastaVisitor{

    @Override
    public void visitRecord(String id, String comment, String sequence) {
        visitNucleotideFastaRecord(
                new DefaultEncodedNucleotideFastaRecord(id, comment, sequence.replace("\\s+", "")));
    }
    
    protected abstract void visitNucleotideFastaRecord(NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> fastaRecord);

}
