/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sanger.phd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sanger.phd.DefaultPhd;
import org.jcvi.jillion.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.jillion.trace.sanger.phd.Phd;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStore;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStoreBuilder;
import org.jcvi.jillion.trace.sanger.phd.PhdParser;
import org.jcvi.jillion.trace.sanger.phd.PhdWriter;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhdWriter extends AbstractTestPhd{
    private String id = "1095595674585";
    @Test
    public void write() throws IOException, DataStoreException{
        Phd phd = new DefaultPhd(
        		id,
        		new NucleotideSequenceBuilder(expectedBasecalls).build(), 
        		expectedQualities, 
                expectedPositions,
                expectedProperties);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PhdWriter.writePhd(phd, out);
        PhdDataStore expected = DefaultPhdFileDataStore.create(RESOURCE.getFile(PHD_FILE));
        PhdDataStoreBuilder actual = DefaultPhdFileDataStore.createBuilder();
        PhdParser.parsePhd(new ByteArrayInputStream(out.toByteArray()), actual);
        assertEquals(expected.get(id),actual.build().get(id));
    }
}
