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
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.assembly.cas.EmptyCasTrimMap;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.trace.fourFiveFour.flowgram.sff.H2NucleotideSffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.H2QualitySffDataStore;

public class H2SffCasDataStoreFactory extends AbstractCasDataStoreFactory{
    private final ReadWriteDirectoryFileServer databaseFileServer;
    public H2SffCasDataStoreFactory(){
        this(null,null);
    }
    public H2SffCasDataStoreFactory(File workingDir){
        this(workingDir,null);
    }
    public H2SffCasDataStoreFactory(ReadWriteDirectoryFileServer databaseFileServer){
        this(null, databaseFileServer);
    }
    public H2SffCasDataStoreFactory(File workingDir,ReadWriteDirectoryFileServer databaseFileServer){
        super(workingDir, EmptyCasTrimMap.getInstance());
        this.databaseFileServer = databaseFileServer;
    }
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(File sffFile)
            throws CasDataStoreFactoryException {       
        checkForSffExtension(sffFile);
        try {
            final H2NucleotideDataStore datastore;
            if(databaseFileServer==null){
                datastore = new H2NucleotideDataStore();
            }else{
                File tempFile = File.createTempFile("H2Sff", null,databaseFileServer.getRootDir());
                datastore = new H2NucleotideDataStore(databaseFileServer.createNewFile(tempFile.getName()));
            }
            return new H2NucleotideSffDataStore(sffFile,  datastore);
            
        } catch (Exception e) {
           throw new CasDataStoreFactoryException("could not create H2 Sff Nucleotide DataStore for "+ sffFile.getAbsolutePath(),e);
        } 
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(File sffFile)
            throws CasDataStoreFactoryException {
        checkForSffExtension(sffFile);
        try {
            return new H2QualitySffDataStore(sffFile, 
                    new H2QualityDataStore());
        } catch (Exception e) {
           throw new CasDataStoreFactoryException("could not create H2 Sff Quality DataStore for "+ sffFile.getAbsolutePath(),e);
        } 
    }
    private void checkForSffExtension(File sffFile)
            throws CasDataStoreFactoryException {
        if(!"sff".equals(FilenameUtils.getExtension(sffFile.getName()))){
            throw new CasDataStoreFactoryException("not a sff file");
        }
    }

}
