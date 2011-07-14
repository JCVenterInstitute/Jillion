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

package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.glyph.phredQuality.PhredQuality;

/**
 * {@code H2PhdNucleotideDataStore} is a {@link NucleotideDataStore}
 * implementation of {@link AbstractH2PhdDataStore}.
 * 
 * @author dkatzel
 *
 *
 */
public class H2PhdNucleotideDataStore extends AbstractH2PhdDataStore<NucleotideGlyph, NucleotideSequence> implements NucleotideDataStore{

    /**
     * @param phdFile
     * @param h2Datastore
     * @throws FileNotFoundException
     */
    public H2PhdNucleotideDataStore(
            File phdFile,
            H2NucleotideDataStore h2Datastore)
            throws FileNotFoundException {
        super(phdFile, h2Datastore);

    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected String visitSingleBaseCall(NucleotideGlyph base,
            PhredQuality quality, int tracePosition) {
        return base.toString();
    }

}
