/*
 * Created on May 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.SnpXMLWriter;
import org.jcvi.assembly.contig.QualityClassContigMapXMLWriter;
import org.jcvi.assembly.contig.qual.LowestFlankingQualityValueStrategy;
import org.jcvi.assembly.coverage.DirectionalSequenceCoverageMapXMLCoverageWriter;
import org.jcvi.assembly.coverage.XMLCoverageWriter;
import org.jcvi.datastore.DataStoreException;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.ISODateTimeFormat;

public class ContigCheckerXMLWriter<T extends PlacedRead> implements Closeable{
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final String CONTIG_CHECKER_BEGIN_HEADER = "<contigChecker date=\"%s\">\n";
    private static final String CONTIG_CHECKER_END_HEADER = "</contigChecker>\n";
    
    private static final String CONTIG_BEGIN_TAG = "<contig id=\"%s\" length = \"%d\" num_reads=\"%d\">\n";
    private static final String CONTIG_END_TAG = "</contig>\n";
    
    private OutputStream out;
    private final XMLCoverageWriter<T> sequenceCoverageWriter = new DirectionalSequenceCoverageMapXMLCoverageWriter<T>();
    private final QualityClassContigMapXMLWriter qualityClassWriter = new QualityClassContigMapXMLWriter();
    private final SnpXMLWriter<T> snpWriter;
    public ContigCheckerXMLWriter(OutputStream out) throws IOException{
        this.out = out;
        snpWriter = new SnpXMLWriter<T>(new LowestFlankingQualityValueStrategy());
        out.write(XML_HEADER.getBytes());
        out.write(String.format(CONTIG_CHECKER_BEGIN_HEADER, 
                ISODateTimeFormat.dateTimeNoMillis().print(
                        DateTimeUtils.currentTimeMillis())).getBytes());
        out.flush();
    }
    
    public void write(ContigCheckerStruct<T> struct) throws IOException{
        Contig<T> contig =struct.getContig();
        out.write(String.format(CONTIG_BEGIN_TAG,
                contig.getId(),
                contig.getConsensus().getLength(),
                contig.getNumberOfReads()).getBytes());
        sequenceCoverageWriter.write(out, struct.getSequenceCoverageMap());
      //  qualityClassWriter.write(out, struct.getQualityClassMap().getQualityClassRegions());
        try {
            snpWriter.write(out, struct);
        } catch (DataStoreException e) {
            throw new IOException("error computing snps");
        }
        writeAdditionalTags(out, struct);
        out.write(CONTIG_END_TAG.getBytes());
        out.flush();
    }
    
    protected void writeAdditionalTags(OutputStream out, ContigCheckerStruct<T> struct) throws IOException{
        // no-op
        
    }

    public void close() throws IOException{
        out.write(CONTIG_CHECKER_END_HEADER.getBytes());
        out.flush();
    }
}
