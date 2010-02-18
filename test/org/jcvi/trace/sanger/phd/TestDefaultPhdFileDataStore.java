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
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Test;

public class TestDefaultPhdFileDataStore extends AbstractTestPhd{

    @Test
    public void parse() throws IOException, DataStoreException{
        DefaultPhdFileDataStore sut = new DefaultPhdFileDataStore();
        
        PhdParser.parsePhd(RESOURCE.getFileAsStream(PHD_FILE), sut);
        Phd actual = sut.get("1095595674585");
        assertEquals(expectedQualities, actual.getQualities().decode());        
        assertEquals(expectedPositions, actual.getPeaks().getData().decode());      
        assertEquals(expectedBasecalls, NucleotideGlyph.convertToString(actual.getBasecalls().decode()));
        assertEquals(expectedProperties, actual.getComments());
    }
}
