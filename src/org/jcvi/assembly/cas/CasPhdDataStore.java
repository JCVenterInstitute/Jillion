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
 * Created on Jan 21, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.CasNucleotideDataStore;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.sanger.phd.PhdDataStore;

public class CasPhdDataStore {

    private final CasAssembly casAssembly;
    private final Date phdDate;
    private final PhredQuality artificalQuality;
    private final List<String> referenceIds = new ArrayList<String>();
    public CasPhdDataStore(CasAssembly casAssembly, CasDataStoreFactory casDataStoreFactory, PhredQuality artificalQuality,Date phdDate) throws IOException{
        this.casAssembly = casAssembly;
        this.phdDate = phdDate;
        this.artificalQuality = artificalQuality;
        for(File referenceFile : casAssembly.getReferenceFiles()){
        //    casDataStoreFactory
         //   iter.g
        }
    }
}
