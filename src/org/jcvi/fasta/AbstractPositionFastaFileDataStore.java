/*
 * Created on Jan 27, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;

public abstract class AbstractPositionFastaFileDataStore extends AbstractFastaFileDataStore<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>>{

    private final PositionFastaRecordFactory fastaRecordFactory;

    /**
     * @param fastaRecordFactory
     */
    public AbstractPositionFastaFileDataStore(
            PositionFastaRecordFactory fastaRecordFactory) {
        this.fastaRecordFactory = fastaRecordFactory;
    }
    
    public AbstractPositionFastaFileDataStore(){
        this(DefaultPositionFastaRecordFactory.getInstance());
    }

    protected final PositionFastaRecordFactory getFastaRecordFactory() {
        return fastaRecordFactory;
    }

    
    

}

