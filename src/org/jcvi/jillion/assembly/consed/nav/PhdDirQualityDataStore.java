/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.nav;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

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
    public PhdDirQualityDataStore(File phdDir) throws IOException {
        this.phdDir = phdDir;
        File phdBall = getPhdFileFor("phd.ball");
        if(phdBall ==null){
            phdBallDataStore = null;
        }else{
            phdBallDataStore = new PhdFileDataStoreBuilder(phdBall)
									.build();
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
        return new PhdFileDataStoreBuilder(phdFile).build().get(id);
        
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
    public boolean isClosed() {
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
