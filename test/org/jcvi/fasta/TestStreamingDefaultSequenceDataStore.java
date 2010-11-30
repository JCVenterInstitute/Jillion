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

package org.jcvi.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
public class TestStreamingDefaultSequenceDataStore extends TestDefaultSequenceFastaDataStore{

    @Override
    protected DefaultNucleotideFastaFileDataStore parseFile(File file)
            throws IOException {
        InputStream in =null;
        try{
            in = new FileInputStream(file);
            DefaultNucleotideFastaFileDataStore datastore = new DefaultNucleotideFastaFileDataStore();
            FastaParser.parseFasta(in, datastore);
            return datastore;
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }

}
