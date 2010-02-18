/*
 * Created on May 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.contigChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.ContigCheckerXMLWriter;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigChecker;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.ContigDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedContigFileDataStore;
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fasta.LargeQualityFastaFileDataStore;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.qualClass.QualityDataStoreAdapter;
import org.jcvi.io.idReader.IdReaderException;

public class ContigCheckerContigFile {

    /**
     * Run the generic contig checker from a contig file
     * @param args 
     *              0 - contigFile
     *              1 - qual File
     *              2 - outputDir
     *              3 - prefix
     *              4 - low seq cov threshold
     *              5 - high seq cov threshold
     *              6 percent read direction difference threshold
     * @throws IOException
     * @throws DataStoreException 
     * @throws Throwable 
     * @throws IdReaderException 
     */
    public static void main(String[] args) throws Throwable {
        if(args.length != 7){
            throw new IllegalArgumentException("invalid parameters");
        }
        File contigFile = new File(args[0]);
        File qualityFastaFile = new File(args[1]);
        String outputBasePath = args[2];
        String prefix =args[3];
        PrintWriter log = new PrintWriter(new FileOutputStream(outputBasePath+"/"+prefix+".log"),true);
        int lowSequenceCoverageThreshold=Integer.parseInt(args[4]);
        int highSequenceCoverageThreshold=Integer.parseInt(args[5]);
        int percentReadDirectionDifferenceTheshold=Integer.parseInt(args[6]);
        try{
        ContigDataStore<PlacedRead, Contig<PlacedRead>> contigDataStore = new MemoryMappedContigFileDataStore(contigFile);
        QualityDataStore qualityFastaMap = CachedDataStore.createCachedDataStore(
                DataStore.class, 
                new QualityDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(qualityFastaFile)))
                ,2000);
        
        for(Iterator<String>contigIdIterator=contigDataStore.getIds(); contigIdIterator.hasNext();){
            
            final String id = contigIdIterator.next();
            log.println(id);
            Contig<PlacedRead> contig = contigDataStore.get(id);
            
            ContigCheckerStruct<PlacedRead> struct = new ContigCheckerStruct<PlacedRead>(contig, qualityFastaMap, PhredQuality.valueOf(30));
            ContigChecker<PlacedRead> contigChecker = new ContigChecker<PlacedRead>(struct, percentReadDirectionDifferenceTheshold,
                    lowSequenceCoverageThreshold, highSequenceCoverageThreshold);
            contigChecker.run();
            ContigCheckerUtil.writeContigCheckerResults(outputBasePath, prefix,id,struct, contigChecker,true);
            
            final String contigPrefix = outputBasePath+"/"+prefix+"."+id;
            OutputStream xmlOut = new FileOutputStream(contigPrefix + ".contigChecker.xml");
            ContigCheckerXMLWriter<PlacedRead> xmlWriter = new ContigCheckerXMLWriter<PlacedRead>(xmlOut);
            xmlWriter.write(struct);
            xmlWriter.close();  
            
        }
        }catch(Throwable t){
           t.printStackTrace(log);
           throw t;
        }
        finally{
            log.close();
        }
    }

}
