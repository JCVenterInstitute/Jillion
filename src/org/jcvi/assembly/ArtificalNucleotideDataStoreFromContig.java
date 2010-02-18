/*
 * Created on Dec 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public class ArtificalNucleotideDataStoreFromContig extends AbstractArtificialDataStoreFromContig<NucleotideEncodedGlyphs> implements NucleotideDataStore{

    public ArtificalNucleotideDataStoreFromContig(
            DataStore<? extends Contig> contigDataStore) {
        super(contigDataStore);
    }
    @Override
    protected NucleotideEncodedGlyphs createArtificalTypefor(PlacedRead read){
        boolean isReverseComplimented = read.getSequenceDirection()==SequenceDirection.REVERSE;
        Range validRange = read.getValidRange();
        List<NucleotideGlyph> basecalls=NucleotideGlyph.convertToUngapped(read.getEncodedGlyphs().decode());
        if(isReverseComplimented){
            basecalls = NucleotideGlyph.reverseCompliment(basecalls);
        }
        List<NucleotideGlyph> fullRange = new ArrayList<NucleotideGlyph>();
        for(int i=0; i< validRange.getStart(); i++){
            fullRange.add(NucleotideGlyph.Unknown);
        }
        fullRange.addAll(basecalls);
        return new DefaultNucleotideEncodedGlyphs(fullRange);
        
    }
}
