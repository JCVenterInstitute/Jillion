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
import org.jcvi.datastore.IndexedContigFileDataStore;
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fasta.LargeQualityFastaFileDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.QualityDataStoreAdapter;
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
        ContigDataStore<PlacedRead, Contig<PlacedRead>> contigDataStore = new IndexedContigFileDataStore(contigFile);
        QualityDataStore qualityFastaMap = CachedDataStore.createCachedDataStore(
                DataStore.class, 
                new QualityDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(qualityFastaFile)))
                ,2000);
        
        for(Iterator<String>contigIdIterator=contigDataStore.getIds(); contigIdIterator.hasNext();){
            
            final String id = contigIdIterator.next();
            log.println(id);
            Contig<PlacedRead> contig = contigDataStore.get(id);
            
            ContigCheckerStruct<PlacedRead> struct = new ContigCheckerStruct<PlacedRead>(contig, qualityFastaMap);
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
