/*
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import org.jcvi.assembly.cas.CasFileInfo;

public class ReadCasFileNucleotideDataStore extends
        AbstractCasFileNucleotideDataStore {

    public ReadCasFileNucleotideDataStore(
            CasDataStoreFactory casDataStoreFactory) {
        super(casDataStoreFactory);
    }

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {
        super.visitReadFileInfo(readFileInfo);
        loadNucleotidesFrom(readFileInfo);
    }
}
