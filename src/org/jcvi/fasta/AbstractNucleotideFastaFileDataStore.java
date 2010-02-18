/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
/**
 * {@code AbstractNucleotideFastaFileDataStore} is an implementation
 * of {@link AbstractFastaFileDataStore} for {@link NucleotideSequenceFastaRecord}s.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractNucleotideFastaFileDataStore extends AbstractFastaFileDataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>>{

    private final NucleotideFastaRecordFactory fastaRecordFactory;

    /**
     * @param fastaRecordFactory
     */
    public AbstractNucleotideFastaFileDataStore(
            NucleotideFastaRecordFactory fastaRecordFactory) {
        this.fastaRecordFactory = fastaRecordFactory;
    }
    
    public AbstractNucleotideFastaFileDataStore(){
        this(DefaultNucleotideFastaRecordFactory.getInstance());
    }

    protected final NucleotideFastaRecordFactory getFastaRecordFactory() {
        return fastaRecordFactory;
    }

    
    

}
