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
package org.jcvi.jillion_experimental.primer;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;

/**
 * @author dkatzel
 *
 *
 */
public final class TestPrimerTrimmerUtil {

    public static NucleotideSequenceDataStore createDataStoreFor(NucleotideSequence...primers){
        Map<String, NucleotideSequence> map = new HashMap<String, NucleotideSequence>();
        for(int i=0; i<primers.length; i++){
            map.put("primer_"+i, primers[i]);
        }
        return DataStoreUtil.adapt(NucleotideSequenceDataStore.class,map);
    }
}
