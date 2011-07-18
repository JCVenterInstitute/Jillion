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

package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.common.core.seq.Glyph;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSffFileDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestFilteredNucleotideDataStore<G extends Glyph> extends AbstractTestExampleSffFile{

private final DefaultSffFileDataStore dataStore;
    
    {
        dataStore = new DefaultSffFileDataStore();
        try {
            SffParser.parseSFF(SFF_FILE, dataStore);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse sff file");
        } 
    }
    private DataStoreFilter filter = new DefaultIncludeDataStoreFilter(
            Arrays.asList("FF585OX02HCMO2", "FF585OX02FHO5X"));
    private DataStore<? extends Sequence<G>> sut;
    
    protected abstract DataStore<? extends Sequence<G>> createSut(File sffFile, DataStoreFilter filter) throws Exception;
    
    protected abstract List<G> getRelaventDataFrom(Flowgram flowgram);
    @Before
    public void setup() throws Exception{
        sut = createSut(SFF_FILE, filter);
    }
    @After
    public void tearDown() throws IOException{
        sut.close();
    }
    @Test
    public void includedIdShouldBeInDataStore() throws DataStoreException{
        assertEquals(getRelaventDataFrom(FF585OX02FHO5X),
                sut.get("FF585OX02FHO5X").decode());
    }
    @Test
    public void includedIdShouldBeInDataStore2() throws DataStoreException{
        assertEquals(getRelaventDataFrom(FF585OX02HCMO2),
                sut.get("FF585OX02HCMO2").decode());
    }
    @Test
    public void excludedIdShouldNotBeInDataStore() throws DataStoreException{
        assertFalse(sut.contains("FF585OX02GMGGN"));
    }
}
