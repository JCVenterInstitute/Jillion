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
 * Created on Sep 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;


public class DefaultAssemblyArchiveContigRecord implements AssemblyArchiveContigRecord{

    private final ContigConformation conformation;
    private final Contig<? extends PlacedRead> contig;
    private final AssemblyArchiveType type;
    private final String submitterReference;

    public DefaultAssemblyArchiveContigRecord(String submitterReference,
            Contig<? extends PlacedRead> contig, AssemblyArchiveType type) {
        this(submitterReference, contig, type, ContigConformation.LINEAR);
    }
    /**
     * @param submitterReference
     * @param contig
     * @param type
     * @param conformation
     */
    public DefaultAssemblyArchiveContigRecord(String submitterReference,
            Contig<? extends PlacedRead> contig, AssemblyArchiveType type,
            ContigConformation conformation) {
        this.submitterReference = submitterReference;
        this.contig = contig;
        this.type = type;
        this.conformation = conformation;
    }

    @Override
    public ContigConformation getConformation() {
        return conformation;
    }

    @Override
    public Contig<? extends PlacedRead> getContig() {
        return contig;
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
