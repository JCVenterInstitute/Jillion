/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public abstract class SingleNucleotideFastaVisitor extends AbstractNucleotideFastaVisitor{

    private NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> record=null;
    @Override
    protected synchronized void visitNucleotideFastaRecord(
            NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> fastaRecord) {
        //only accept first record
        if(acceptVisitedFasta()){
            record = fastaRecord;        
        }
    }
    protected abstract boolean acceptVisitedFasta();
    public synchronized NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> getRecord() {
        return record;
    }
    
    

}
