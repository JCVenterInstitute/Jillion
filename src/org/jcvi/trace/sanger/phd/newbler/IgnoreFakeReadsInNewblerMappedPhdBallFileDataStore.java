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
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd.newbler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.trace.sanger.phd.PhdTag;
import org.jcvi.util.IndexedFileRange;

public class IgnoreFakeReadsInNewblerMappedPhdBallFileDataStore extends IndexedPhdFileDataStore{

    private static final String FAKE_READ_TYPE = "type: fake\n";
    private static final String WHOLE_READ_TAG = "WR";
    public IgnoreFakeReadsInNewblerMappedPhdBallFileDataStore(File phdBall,
            IndexedFileRange recordLocations) throws FileNotFoundException {
        super(phdBall, recordLocations);
    }

    @Override
    protected synchronized void visitPhd(String id, List<NucleotideGlyph> bases,
            List<PhredQuality> qualities, List<ShortGlyph> positions,
            Properties comments, List<PhdTag> tags) {
        for(PhdTag tag: tags){
            if(isFakeRead(tag)){
                super.visitPhd(id, bases, qualities, positions, comments,tags);
                break;
            }
        }
        
    }

    private boolean isFakeRead(PhdTag tag) {
        return tag.getTagName().equals(WHOLE_READ_TAG) && tag.getTagValue().contains(FAKE_READ_TYPE);
    }
    
}
