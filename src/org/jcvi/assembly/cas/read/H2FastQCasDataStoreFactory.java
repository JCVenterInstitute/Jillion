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
import org.jcvi.assembly.cas.CasTrimMap;
import org.jcvi.assembly.cas.EmptyCasTrimMap;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.H2NucleotideFastQDataStore;
import org.jcvi.fasta.fastq.H2QualityFastQDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;

public class H2FastQCasDataStoreFactory extends AbstractCasDataStoreFactory{

    private final FastQQualityCodec fastqQualityCodec;
    private final boolean useTempfile;
    /**
     * @param fastqQualityCodec
     */
    public H2FastQCasDataStoreFactory(FastQQualityCodec fastqQualityCodec, boolean useTempFile) {
        this(null, fastqQualityCodec, useTempFile);
    }
    public H2FastQCasDataStoreFactory(File workingDir,FastQQualityCodec fastqQualityCodec, boolean useTempFile) {
        this(workingDir, EmptyCasTrimMap.getInstance(), fastqQualityCodec, useTempFile);
    }
    public H2FastQCasDataStoreFactory(File workingDir,CasTrimMap trimMap,FastQQualityCodec fastqQualityCodec, boolean useTempFile) {
        this(workingDir, trimMap, fastqQualityCodec, EmptyDataStoreFilter.INSTANCE,useTempFile);
    }
    public H2FastQCasDataStoreFactory(File workingDir,CasTrimMap trimMap,FastQQualityCodec fastqQualityCodec, DataStoreFilter filter,boolean useTempFile) {
        super(workingDir,trimMap,filter);
        this.fastqQualityCodec = fastqQualityCodec;
        this.useTempfile = useTempFile;
    }
    /**
     * @param fastqQualityCodec
     */
    public H2FastQCasDataStoreFactory(FastQQualityCodec fastqQualityCodec) {
        this(null,fastqQualityCodec, false);
    }
    public H2FastQCasDataStoreFactory(File workingDir,FastQQualityCodec fastqQualityCodec) {
        this(workingDir,fastqQualityCodec, false);
    }
    public H2FastQCasDataStoreFactory(File workingDir,CasTrimMap trimMap,FastQQualityCodec fastqQualityCodec) {
        this(workingDir,trimMap,fastqQualityCodec, false);
    }
    
    
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(File fastq, DataStoreFilter filter)
            throws CasDataStoreFactoryException {
        
        if(!fastq.getName().contains("fastq")){
            throw new CasDataStoreFactoryException("not a fastq file");
        }
        try {
            final H2NucleotideDataStore datastore;
            if(useTempfile){
                datastore= new H2NucleotideDataStore(File.createTempFile("fastqNucDb", null));
            }else{
                datastore = new H2NucleotideDataStore();
            }
            return new H2NucleotideFastQDataStore(
                    fastq,
                    datastore,
                    filter);
        } catch (Exception e) {
            throw new CasDataStoreFactoryException("could not create FastQ H2 Nucleotide DataStore",e);
        } 
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(File fastq, DataStoreFilter filter)
            throws CasDataStoreFactoryException {
        if(!fastq.getName().contains("fastq")){
            throw new CasDataStoreFactoryException("not a fastq file");
        }
        try {
            final H2QualityDataStore datastore;
            if(useTempfile){
                datastore= new H2QualityDataStore(File.createTempFile("fastqQualDb", null));
            }else{
                datastore = new H2QualityDataStore();
            }
            return new H2QualityFastQDataStore(
                    fastq,
                    fastqQualityCodec,
                    datastore,
                    filter);
        } catch (Exception e) {
            throw new CasDataStoreFactoryException("could not create FastQ H2 Quality DataStore",e);
        } 
    }

}
