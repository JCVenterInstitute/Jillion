/*
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

public class DefaultReadCasFileLookup extends AbstractDefaultCasFileLookup{

    @Override
    public void visitContigFileInfo(CasFileInfo contigFileInfo) {
        checkNotYetInitialized();
        
    }

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {
        checkNotYetInitialized();
        loadFiles(readFileInfo);
        
    }
}
