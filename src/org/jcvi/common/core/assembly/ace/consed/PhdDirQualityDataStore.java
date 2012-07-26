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

package org.jcvi.common.core.assembly.ace.consed;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public class PhdDirQualityDataStore implements PhdDataStore{

    private final File phdDir;
    private final PhdDataStore phdBallDataStore;
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
     * @throws FileNotFoundException 
     */
    public PhdDirQualityDataStore(File phdDir) throws FileNotFoundException {
        this.phdDir = phdDir;
        File phdBall = getPhdFileFor("phd.ball");
        if(phdBall ==null){
            phdBallDataStore = null;
        }else{
            phdBallDataStore = DefaultPhdFileDataStore.create(phdBall);
        }
    }

    private File getPhdFileFor(String readId){
        int latestVersion= Integer.MIN_VALUE;
        File latestFile=null;
        for(File phd : phdDir.listFiles(new ReadPhdFileFilter(readId))){
            int version = Integer.parseInt(FileUtil.getExtension(phd.getName()));
            if(version>latestVersion){
                latestVersion = version;
                latestFile=phd;
            }
        }
        return latestFile;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Phd get(String id) throws DataStoreException {
        return getPhdFor(id);
    }

    private Phd getPhdFor(String id) throws DataStoreException{
        File phdFile = getPhdFileFor(id);
        if(phdFile ==null){
            if(phdBallDataStore==null){
                throw new IllegalArgumentException("could not find any phd files for "+ id);
                
            }
            return phdBallDataStore.get(id);
        }
        try {
            return DefaultPhdFileDataStore.create(phdFile).get(id);
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get phd file for "+id,e);
        }
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        
        boolean hasPhd= getPhdFileFor(id)!=null;
        if(!hasPhd && phdBallDataStore !=null){
            return phdBallDataStore.contains(id);
        }
        return hasPhd;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
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
    public StreamingIterator<Phd> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

}
