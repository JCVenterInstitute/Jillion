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

package org.jcvi.common.core.seq.trim;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public final class TestPrimerTrimmerUtil {

    public static NucleotideDataStore createDataStoreFor(NucleotideSequence...primers){
        Map<String, NucleotideSequence> map = new HashMap<String, NucleotideSequence>();
        for(int i=0; i<primers.length; i++){
            map.put("primer_"+i, primers[i]);
        }
        return new NucleotideDataStoreAdapter(MapDataStoreAdapter.adapt(map));
    }
}
