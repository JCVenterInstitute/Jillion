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
/*
 * Created on Feb 20, "2009" +
 *
 * @author "dkatzel" +
 */
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;
import org.junit.Test;

public abstract class AbstractTestSequenceFastaMapWithNoComment extends AbstractTestSequenceFastaDataStoreWithNoComment{
    @Test
    public void parseStream() throws IOException, DataStoreException{
        DataStore<NucleotideSequenceFastaRecord> sut = createDataStore(
        		RESOURCES.getFile(FASTA_FILE_PATH));
        assertEquals(1, sut.getNumberOfRecords());
        assertEquals(hrv_61, sut.get("hrv-61"));
    }

}
