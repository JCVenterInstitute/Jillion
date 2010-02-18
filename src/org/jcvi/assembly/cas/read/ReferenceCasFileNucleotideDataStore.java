/*
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import org.jcvi.assembly.cas.CasFileInfo;

public class ReferenceCasFileNucleotideDataStore  extends AbstractCasFileNucleotideDataStore {

    public ReferenceCasFileNucleotideDataStore(
            CasDataStoreFactory casDataStoreFactory) {
        super(casDataStoreFactory);
    }


    @Override
    public synchronized void visitContigFileInfo(CasFileInfo contigFileInfo) {
        super.visitContigFileInfo(contigFileInfo);
        loadNucleotidesFrom(contigFileInfo);
    }
    
}
