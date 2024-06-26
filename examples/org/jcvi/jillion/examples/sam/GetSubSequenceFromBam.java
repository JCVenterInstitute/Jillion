/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
                                                    Range.of(CoordinateSystem.RESIDUE_BASED, 20_100_000, 20_200_00)
                                                    )
                                                .toStream();
                
                
                ){
            //get the query names of those reads that mapped
            Set<String> names = aligned.map(SamRecord::getQueryName)
                                        .collect(Collectors.toCollection(TreeSet::new));

            System.out.println(names);
            
        }
    }

}
