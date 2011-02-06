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
 * Created on Feb 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
/**
 * {@code H2PhdQualityDataStore} is a {@link QualityDataStore}
 * implementation of {@link AbstractH2PhdDataStore}.
 * @author dkatzel
 *
 *
 */
public class H2PhdQualityDataStore extends AbstractH2PhdDataStore<PhredQuality, QualityEncodedGlyphs> implements QualityDataStore{
    public H2PhdQualityDataStore(File phdFile,H2QualityDataStore qualityDataStore) throws FileNotFoundException{
        super(phdFile, qualityDataStore);
    }

    @Override
    protected String visitSingleBaseCall(NucleotideGlyph base,
            PhredQuality quality, int tracePosition) {
        //add space so parser can tell positions apart.
        return quality.getNumber()+" ";
    }
    
}
