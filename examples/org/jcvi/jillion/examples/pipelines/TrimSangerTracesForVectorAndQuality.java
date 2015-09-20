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
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaWriter;
import org.jcvi.jillion.fasta.qual.QualityFastaWriterBuilder;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.jcvi.jillion.trim.NucleotideTrimmer;
import org.jcvi.jillion.trim.QualityTrimmer;
import org.jcvi.jillion.trim.lucy.LucyQualityTrimmerBuilder;
import org.jcvi.jillion.trim.lucy.LucyVectorSpliceTrimmerBuilder;

public class TrimSangerTracesForVectorAndQuality {

    public static void main(String[] args) throws IOException {
        List<File> traceFiles = Arrays.asList(
                //sanger trace files go here, supports ab1, ztr and scf
                );
        
        //TODO put your actual splice sequences or linkers here
        NucleotideSequence upStreamSpliceSeq = new NucleotideSequenceBuilder("TTTTTT").build();
        NucleotideSequence downstreamSpliceSeq = new NucleotideSequenceBuilder("ACGTACGT").build();
        
        File seqOutFile = new File("/path/to/seq.fasta");
        File qualOutFile = new File("/path/to/qual.fasta");
        
        NucleotideTrimmer vectorTrimmer = new LucyVectorSpliceTrimmerBuilder(upStreamSpliceSeq, downstreamSpliceSeq)
                                                    .build();
        
        //this is the default trimming windows used by the JCVI Viral group since 2005
        QualityTrimmer qualityTrimmer = new LucyQualityTrimmerBuilder(30)
                                                    .addTrimWindow(30, 0.01F)
                                                    .addTrimWindow(10, 0.035F)
                                                    .build();
        
        
        try(NucleotideFastaWriter seqFastaWriter = new NucleotideFastaWriterBuilder(seqOutFile)
                                                            .build();
           QualityFastaWriter qualFastaWriter = new QualityFastaWriterBuilder(qualOutFile)
                                                               .build();
                
        ){
            for(File traceFile : traceFiles){
                //works for ab1, ztr or scf encoded files
                Chromatogram chromo = ChromatogramFactory.create(traceFile);
                
                Range vectorFreeRange = vectorTrimmer.trim(chromo.getNucleotideSequence());
                Range goodQuailtyRange = qualityTrimmer.trim(chromo.getQualitySequence());
                //the "clearRange" is the only part of the
                //sequence that is in the good quality region AND free of vector
                Range clearRange = vectorFreeRange.intersection(goodQuailtyRange);
                
                if(clearRange.isEmpty()){
                    //completely trimmed!
                    //don't write anything out.
                    continue;
                }
                String id = chromo.getId();
                
                NucleotideSequence trimmedSeq = chromo.getNucleotideSequence()
                                                        .toBuilder()
                                                        .trim(clearRange)
                                                        .build();
                
                QualitySequence trimmedQual = chromo.getQualitySequence()
                                                    .toBuilder()
                                                    .trim(clearRange)
                                                    .build();
                
                seqFastaWriter.write(id, trimmedSeq);
                qualFastaWriter.write(id, trimmedQual);
                
            }
            
        }//auto-close fasta writers
        
        

    }

}
