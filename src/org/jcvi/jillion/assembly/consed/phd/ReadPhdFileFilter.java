package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.FileFilter;

class ReadPhdFileFilter implements FileFilter{
    private final String readId;

    public ReadPhdFileFilter(String readId) {
        this.readId = readId;
    }



    /**
    * {@inheritDoc}
    */
    @Override
    public boolean accept(File pathname) {
        
        return pathname.getName().startsWith(readId);
    }
    
}