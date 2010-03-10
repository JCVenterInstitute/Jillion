import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.contig.qual.ZeroGapQualityValueStrategy;
import org.jcvi.assembly.slice.DefaultSliceMapFactory;
import org.jcvi.assemblyArchive.AssemblyArchive;
import org.jcvi.assemblyArchive.AssemblyArchiveType;
import org.jcvi.assemblyArchive.AssemblyArchiveXMLWriter;
import org.jcvi.assemblyArchive.DefaultAssemblyArchive;
import org.jcvi.assemblyArchive.DefaultAssemblyArchiveContigRecord;
import org.jcvi.assemblyArchive.LazyAssemblyArchiveContigRecord;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DefaultAceFileDataStore;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.trace.sanger.phd.H2PhdQualityDataStore;
import org.joda.time.Period;

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

/**
 * @author dkatzel
 *
 *
 */
public class MCEIERS_assemblyArchiveCreator {

    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        //BC193CG has 415,275 reads, took 74 hrs to convert from .cas
        
        File aceFile = new File("/local/ifs_projects/VHTNGS/sample_data/MCE_1_50_StandardPipeSFF/mapping/BC193CG/consed_edited/edit_dir/cas2consed.ace.1");

        File phdFile = new File("/usr/local/projects/VHTNGS/sample_data/MCE_1_50_StandardPipeSFF/mapping/BC193CG/consed_edited/phd_dir/cas2consed.phd.ball");
        long start = System.currentTimeMillis();
        //use default since we're going to have to store everything in memory anyway
        System.out.println("parsing ace file");
        MemoryMappedAceFileDataStore contigDataStore = new MemoryMappedAceFileDataStore(aceFile);
        AceFileParser.parseAceFile(aceFile, contigDataStore);
        System.out.println("parsing phd file");
        QualityDataStore qualityDataStore = new H2PhdQualityDataStore(
                                                    phdFile,
                                                new H2QualityDataStore(new File("/usr/local/scratch/dkatzel/asmArchiveTestQualDataStore")));
    
        DefaultAssemblyArchive.Builder<PlacedRead> builder = 
                            new DefaultAssemblyArchive.Builder<PlacedRead>("JCVI",0,
                                    "BC193CG MCEIRS",
                                    "Genomic Segments",
                                    AssemblyArchiveType.NEW);
        Iterator<String> iter = contigDataStore.getIds();
        while(iter.hasNext()){
            String contigId = iter.next();
            System.out.println("adding contig "+contigId);
            builder.addContigRecord( 
                    new LazyAssemblyArchiveContigRecord(
                            contigId,
                            contigDataStore,
                            contigId,
                            AssemblyArchiveType.NEW));
        }
        AssemblyArchive<PlacedRead> assemblyArchive = builder.build();
        System.out.println("writting xml file...");
        new AssemblyArchiveXMLWriter<PlacedRead>().write(assemblyArchive, 
                new DefaultSliceMapFactory(new ZeroGapQualityValueStrategy()),
                qualityDataStore,
                new FileOutputStream("/usr/local/scratch/dkatzel/MCEIRS_BC193CG.ASSEMBLY.xml"));
        long end = System.currentTimeMillis();
        System.out.println("took "+ new Period(end-start));
    }

}
