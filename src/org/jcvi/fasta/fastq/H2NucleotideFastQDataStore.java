/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class H2NucleotideFastQDataStore extends AbstractH2FastQDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> implements NucleotideDataStore{
    
    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @throws FileNotFoundException
     */
    public H2NucleotideFastQDataStore(
            File fastQFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore)
            throws FileNotFoundException {
        super(fastQFile, null, datastore);

    }

    @Override
    public boolean visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        try {
            this.getDatastore().insertRecord(this.getCurrentId(), 
                    NucleotideGlyph.convertToString(nucleotides.decode()));
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not insert qualities for into datastore",e);
        }
        return true;
    }


}
