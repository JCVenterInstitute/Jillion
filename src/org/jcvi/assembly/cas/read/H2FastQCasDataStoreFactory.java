/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.H2NucleotideFastQDataStore;
import org.jcvi.fasta.fastq.H2QualityFastQDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;

public class H2FastQCasDataStoreFactory implements CasDataStoreFactory{

    private final FastQQualityCodec fastqQualityCodec;
    private final boolean useTempfile;
    
    /**
     * @param fastqQualityCodec
     */
    public H2FastQCasDataStoreFactory(FastQQualityCodec fastqQualityCodec, boolean useTempFile) {
        this.fastqQualityCodec = fastqQualityCodec;
        this.useTempfile = useTempFile;
    }
    /**
     * @param fastqQualityCodec
     */
    public H2FastQCasDataStoreFactory(FastQQualityCodec fastqQualityCodec) {
        this(fastqQualityCodec, false);
    }
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        if(!"fastq".equals(FilenameUtils.getExtension(pathToDataStore))){
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
                    new File(pathToDataStore),
                    datastore);
        } catch (Exception e) {
            throw new CasDataStoreFactoryException("could not create FastQ H2 Nucleotide DataStore",e);
        } 
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        if(!"fastq".equals(FilenameUtils.getExtension(pathToDataStore))){
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
                    new File(pathToDataStore),
                    fastqQualityCodec,
                    datastore);
        } catch (Exception e) {
            throw new CasDataStoreFactoryException("could not create FastQ H2 Quality DataStore",e);
        } 
    }

}
