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

package org.jcvi.assembly.trim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.assembly.util.TrimDataStoreAdatper;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor;
import org.jcvi.common.core.util.CloseableIterator;

/**
 * {@code LucyTrimDataStore} is a TrimDataStore
 * that parses a lucy
 * seq fasta file and gets the trimpoints
 * from the fasta comments.
 * @author dkatzel
 *
 *
 */
public class LucySeqTrimDataStore implements TrimDataStore {

    private final TrimDataStore datastore;
    /**
     * 
     * @param lucySeqFile
     * @throws FileNotFoundException
     */
    public LucySeqTrimDataStore(File lucySeqFile) throws FileNotFoundException{
        final Map<String, Range> map = new LinkedHashMap<String, Range>();
        //our fasta visitor implementation
        //to parse the trim points from the comments
        FastaVisitor visitor = new AbstractFastaVisitor() {
            
            @Override
            public boolean visitRecord(String id, String comment, String entireBody) {
                //ex def line 
                //>name CLZ CLZ CLR CLR
                String[] trimpoints = comment.split("\\s+");
                Range range = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                        Long.parseLong(trimpoints[3]),
                        Long.parseLong(trimpoints[4])
                        );
                map.put(id, range);
                return true;
            }
        };
        FastaParser.parseFasta(lucySeqFile, visitor);
        datastore = TrimDataStoreAdatper.adapt(new SimpleDataStore<Range>(map));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Range get(String id) throws DataStoreException {
        return datastore.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws DataStoreException {
        return datastore.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
       datastore.close();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableIterator<Range> iterator() {
        return datastore.iterator();
    }

}
