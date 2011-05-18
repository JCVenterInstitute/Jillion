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

package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.util.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class PhdDirQualityDataStore implements PhdDataStore{

    private final File phdDir;
    
    private static class ReadPhdFileFilter implements FileFilter{
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
    /**
     * @param phdDir
     */
    public PhdDirQualityDataStore(File phdDir) {
        this.phdDir = phdDir;
    }

    private File getPhdFileFor(String readId){
        int latestVersion= Integer.MIN_VALUE;
        File latestFile=null;
        for(File phd : phdDir.listFiles(new ReadPhdFileFilter(readId))){
            int version = Integer.parseInt(FilenameUtils.getExtension(phd.getName()));
            if(version>latestVersion){
                latestVersion = version;
                latestFile=phd;
            }
        }
        if(latestFile==null){
            throw new IllegalArgumentException("could not find any phd files for "+ readId);
        }
        return latestFile;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Phd get(String id) throws DataStoreException {
        File phdFile = getPhdFileFor(id);
        
        try {
            return new DefaultPhdFileDataStore(phdFile).get(id);
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get phd file for "+id);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return getPhdFileFor(id)!=null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<Phd> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

}
