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

package org.jcvi.assemblyArchive;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;

/**
 * {@code LazyAssemblyArchiveContigRecord} is an {@link AssemblyArchiveContigRecord}
 * implementation that delays fetching the actual contig data
 * from the given Contig DataStore until {@link #getContig()} is called.
 * @author dkatzel
 *
 *
 */
public class LazyAssemblyArchiveContigRecord implements AssemblyArchiveContigRecord{
    private final ContigConformation conformation;
    private final String contigId;
    private final DataStore<? extends Contig> datastore;
    private final AssemblyArchiveType type;
    private final String submitterReference;
    
    public LazyAssemblyArchiveContigRecord(String submitterReference,
            DataStore<? extends Contig> datastore, String contigId,AssemblyArchiveType type) {
        this(submitterReference, datastore,contigId, type, ContigConformation.LINEAR);
    }
    /**
     * @param submitterReference
     * @param contig
     * @param type
     * @param conformation
     */
    public LazyAssemblyArchiveContigRecord(String submitterReference,
            DataStore<? extends Contig> datastore, String contigId, AssemblyArchiveType type,
            ContigConformation conformation) {
        this.submitterReference = submitterReference;
        this.contigId = contigId;
        this.datastore = datastore;
        this.type = type;
        this.conformation = conformation;
    }

    @Override
    public ContigConformation getConformation() {
        return conformation;
    }

    @Override
    public Contig<? extends PlacedRead> getContig() {
        try {
            return datastore.get(contigId);
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not get "+contigId+" from datastore",e);
        }
    }


    @Override
    public String getSubmitterReference() {
        return submitterReference;
    }

    @Override
    public AssemblyArchiveType getType() {
        return type;
    }
}
