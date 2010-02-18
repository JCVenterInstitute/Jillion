/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
