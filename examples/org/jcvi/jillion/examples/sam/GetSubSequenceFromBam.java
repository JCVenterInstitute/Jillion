package org.jcvi.jillion.examples.sam;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.sam.SamFileDataStore;
import org.jcvi.jillion.sam.SamFileDataStoreBuilder;
import org.jcvi.jillion.sam.SamRecord;

public class GetSubSequenceFromBam {

    public static void main(String[] args) throws IOException{
        File bam = new File("/path/to/bam");
        //if the file also has a .bai file that it will be auto detected for faster lookups
        try(SamFileDataStore datastore = new SamFileDataStoreBuilder(bam)
                                                .build();
                
                
          //this is the same as : samtools view aln.sorted.bam chr2:20,100,000-20,200,000 

            Stream<SamRecord> aligned = datastore.getAlignedRecords("chr2", 
                                                    Range.of(CoordinateSystem.RESIDUE_BASED, 20),
                                                    Range.of(CoordinateSystem.RESIDUE_BASED, 100_000, 20_200_00)
                                                    )
                                                .toStream();
                
                
                ){
            //get the query names of those reads that mapped
            Set<String> names = aligned.map(SamRecord::getQueryName)
                                        .collect(Collectors.toCollection(TreeSet::new));
            
        }
    }

}
