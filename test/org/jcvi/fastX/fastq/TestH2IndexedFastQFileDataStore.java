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

package org.jcvi.fastX.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.H2IndexedFileRange;
import org.jcvi.fastX.fastq.FastQQualityCodec;
import org.jcvi.fastX.fastq.IndexedFastaQFileDataStore;

/**
 * @author dkatzel
 *
 *
 */
public class TestH2IndexedFastQFileDataStore extends AbstractTestFastQFileDataStore{

    @Override
    protected IndexedFastaQFileDataStore createFastQFileDataStore(File file,
            FastQQualityCodec qualityCodec) throws IOException {
        try {
            return new IndexedFastaQFileDataStore(file, qualityCodec, new H2IndexedFileRange());
        } catch (DataStoreException e) {
            throw new IllegalStateException(e);
        }
    }

}
