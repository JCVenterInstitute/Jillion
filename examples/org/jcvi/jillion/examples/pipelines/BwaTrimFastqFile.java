/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.examples.pipelines;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.*;
import org.jcvi.jillion.trim.BwaQualityTrimmer;
import org.jcvi.jillion.trim.QualityTrimmer;

public class BwaTrimFastqFile {

    public static void main(String[] args) throws IOException, DataStoreException {
        File inputFastq = new File("");
        
        File outputFastq = new File("");
//        jillion5_0_way(inputFastq, outputFastq);

        jillion5_3_way(inputFastq, outputFastq);
    }

    protected static void jillion5_0_way(File inputFastq, File outputFastq) throws IOException {
        int MIN_LENGTH = 64;

        QualityTrimmer bwaTrimmer = new BwaQualityTrimmer(PhredQuality.valueOf(20));

        try(FastqFileDataStore fastqDataStore = new FastqFileDataStoreBuilder(inputFastq)
                                                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                        .build();

            FastqWriter writer = new FastqWriterBuilder(outputFastq)
                                                        .qualityCodec(fastqDataStore.getQualityCodec())
                                                        .build();

            ThrowingStream<FastqRecord> stream = fastqDataStore.records();
            ){

            stream.throwingForEach( fastq ->{

                Range trimRange = bwaTrimmer.trim(fastq.getQualitySequence());
                if(trimRange.getLength() >= MIN_LENGTH){

                    writer.write( fastq, trimRange);
                }
            });
        }//autoclose writer
    }

    protected static void jillion5_3_way(File inputFastq, File outputFastq) throws IOException {
        int MIN_LENGTH = 64;
        //param is the threshold same as bwa -q option
        QualityTrimmer bwaTrimmer = new BwaQualityTrimmer(PhredQuality.valueOf(20));

        try(FastqFileReader.Results parsedFastq = FastqFileReader.read(inputFastq);

            FastqWriter writer = new FastqWriterBuilder(outputFastq)
                    .qualityCodec(parsedFastq.getCodec())
                    .build();

            ThrowingStream<FastqRecord> stream = parsedFastq.records();
        ){

            stream.throwingForEach( fastq ->{

                Range trimRange = bwaTrimmer.trim(fastq.getQualitySequence());
                if(trimRange.getLength() >= MIN_LENGTH){

                    writer.write( fastq, trimRange);
                }
            });
        }
    }
}
