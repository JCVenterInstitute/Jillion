/*
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

public class DefaultReferenceCasFileLookup extends AbstractDefaultCasFileLookup{

    @Override
    public void visitContigFileInfo(CasFileInfo contigFileInfo) {
        checkNotYetInitialized();
        loadFiles(contigFileInfo);
    }

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {
        checkNotYetInitialized();
        
    }

}
