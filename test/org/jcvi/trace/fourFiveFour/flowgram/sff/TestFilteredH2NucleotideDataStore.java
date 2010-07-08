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

package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.util.List;

import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

/**
 * @author dkatzel
 *
 *
 */
public class TestFilteredH2NucleotideDataStore extends AbstractTestFilteredNucleotideDataStore<NucleotideGlyph>{

    /**
    * {@inheritDoc}
    */
    @Override
    protected NucleotideDataStore createSut(File sffFile, DataStoreFilter filter)
            throws Exception {
        return new H2NucleotideSffDataStore(sffFile, new H2NucleotideDataStore(), filter);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected List<NucleotideGlyph> getRelaventDataFrom(Flowgram flowgram) {
        return flowgram.getBasecalls().decode();
    }

}
