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
import java.io.UncheckedIOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.sam.AbstractSamVisitor;
import org.jcvi.jillion.sam.SamFileDataStore;
import org.jcvi.jillion.sam.SamFileDataStoreBuilder;
import org.jcvi.jillion.sam.SamFileWriterBuilder;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.VirtualFileOffset;

public class SortSamToIndexedBam {
    /*
     * sort bam and create bam index
     * 
     * Using Samtools, this takes 2 commands:
     * 
     * samtools sort -T /tmp/aln.sorted -o aln.sorted.bam aln.bam 
     * samtools index aln.sorted.bam 
     * 
     * 
     * This has to parse the unsorted bam, sort it, write out the
     * sorted bam file. And then parse the sorted bam file to make the bam
     * index.
     * 
     * By using Jillion, this process can be done in a single parse by writing
     * out the sorted bam file AND the index at the same time
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        File inputBam = new File("aln.bam");
        File outputBam = new File("aln.sorted.bam");
       
        //using_old_version(inputBam, outputBam);
        //using_version_5_2(inputBam, outputBam);
        using_version_5_3(inputBam, outputBam);
    }
    /**
     * Jillion 5.3 added {@link org.jcvi.jillion.core.util.ThrowingStream}  so you 
     * can use Streams with lambdas that throw exceptions as well as static
     * helper methods on some interfaces such as SamWriter.
     */
    private static void using_version_5_3(File inputSam, File outputBam) throws IOException{
        try(SamFileDataStore datastore = SamFileDataStore.fromFile(inputSam);
            SamWriter writer = SamWriter.newSortedBamWriter(outputBam, datastore.getHeader());
                    
            ThrowingStream<SamRecord> stream = datastore.records();
          ){

                stream.throwingForEach(writer::writeRecord);
                
            }
    }
    /**
     * Jillion 5.3 added {@link org.jcvi.jillion.core.util.ThrowingStream}  so you 
     * can use Streams with lambdas that throw exceptions as well as static
     * helper methods on some interfaces such as SamWriter.
     */
    private static void using_version_5_3_helperMethod(File inputBam, File outputBam) throws IOException{
       SamWriter.writeSorted(inputBam, outputBam);
    }
    
    /**
     * Jillion 5.2 added {@link SamFileDataStore} so you don't have
     * to use the low level SamVisitor anymore which makes for cleaner code
     * and makes the sam package match all the other Jillion packages by adding
     * DataStore support.
     * 
     * @param inputBam
     * @param outputBam
     * @throws IOException
     * @throws DataStoreException
     */
    private static void using_version_5_2(File inputBam, File outputBam) throws IOException, DataStoreException{
        try(SamFileDataStore datastore = new SamFileDataStoreBuilder(inputBam).build();               
                
                
                SamWriter writer = new SamFileWriterBuilder(outputBam, datastore.getHeader())
                                           .reSortBy(SortOrder.COORDINATE)
                                            //create index with extra metadata
                                           .createBamIndex(true, true)                             
                                           .build();
                    
                StreamingIterator<SamRecord> iter = datastore.iterator();
                    ){

                
                while(iter.hasNext()){
                    writer.writeRecord(iter.next());
                }
            }
    }
    
    private static void using_old_version(File inputBam, File outputBam) throws IOException{
        SamParser parser = SamParserFactory.create(inputBam);
        
        try(SamWriter writer = new SamFileWriterBuilder(outputBam, parser.getHeader())
                                       .reSortBy(SortOrder.COORDINATE)
                                        //create index with extra metadata
                                       .createBamIndex(true, true)                             
                                       .build();
               ){
            
            parser.parse(new AbstractSamVisitor() {

                @Override
                public void visitRecord(SamVisitorCallback callback,
                        SamRecord record, VirtualFileOffset start,
                        VirtualFileOffset end) {
                    try {
                        writer.writeRecord(record);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
                
            });
            
        }
    }

}
