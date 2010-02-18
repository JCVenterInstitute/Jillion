/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
/**
 * {@code AbstractQualityFastaFileDataStore} is an implementation
 * of {@link AbstractFastaFileDataStore} for {@code  FastaRecord<EncodedGlyphs<PhredQuality>>}s.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractQualityFastaFileDataStore extends AbstractFastaFileDataStore<QualityFastaRecord<EncodedGlyphs<PhredQuality>>>{

    private final QualityFastaRecordFactory fastaRecordFactory;

    /**
     * @param fastaRecordFactory
     */
    public AbstractQualityFastaFileDataStore(
            QualityFastaRecordFactory fastaRecordFactory) {
        this.fastaRecordFactory = fastaRecordFactory;
    }
    /**
     * Convenience constructor using the {@link DefaultQualityFastaRecordFactory}.
     * This call is the same as {@link #AbstractQualityFastaFileDataStore(QualityFastaRecordFactory)
     * new AbstractQualityFastaFileDataStore(DefaultQualityFastaRecordFactory.getInstance());}
     */
    public AbstractQualityFastaFileDataStore(){
        this(DefaultQualityFastaRecordFactory.getInstance());
    }
    protected final QualityFastaRecordFactory getFastaRecordFactory() {
        return fastaRecordFactory;
    }

}
