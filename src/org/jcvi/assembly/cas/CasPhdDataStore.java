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
