/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class DefaultNucleotideFastaRecordFactory implements NucleotideFastaRecordFactory{

    private static final DefaultNucleotideFastaRecordFactory INSTANCE = new DefaultNucleotideFastaRecordFactory();
    
    private DefaultNucleotideFastaRecordFactory(){}
    
    public static DefaultNucleotideFastaRecordFactory getInstance(){
        return INSTANCE;
    }
    @Override
    public NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> createFastaRecord(
            String id, String comments, String recordBody) {
        return new DefaultEncodedNucleotideFastaRecord(id,comments, recordBody.replace("\\s+", ""));
    }

    @Override
    public NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs> createFastaRecord(
            String id, String recordBody) {
        return createFastaRecord(id, null,recordBody);
    }

}
