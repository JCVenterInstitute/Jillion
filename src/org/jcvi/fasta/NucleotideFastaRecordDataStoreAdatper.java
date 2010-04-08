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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class NucleotideFastaRecordDataStoreAdatper<F extends NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> extends FastaRecordDataStoreAdapter<NucleotideEncodedGlyphs,F> implements NucleotideDataStore{
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param <F> a {@code FastaRecord<Nucleotide>}.
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code DataStore<T>} which wraps the given datastore. 
     */
    public static <F extends NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> NucleotideFastaRecordDataStoreAdatper adapt(DataStore<F> datastoreOfFastaRecords){
        return new NucleotideFastaRecordDataStoreAdatper<F>(datastoreOfFastaRecords);
    }
    private NucleotideFastaRecordDataStoreAdatper(
            DataStore<F> datastoreOfFastaRecords) {
        super(datastoreOfFastaRecords);
    }

}
