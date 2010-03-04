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
 * Created on Sep 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.assembly.slice.SliceMapFactory;
import org.jcvi.assembly.slice.consensus.ConicConsensusCaller;
import org.jcvi.assembly.slice.consensus.ConsensusCaller;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.XMLUtil;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AssemblyArchiveXMLWriter<T extends PlacedRead> {

    private static final String XML_BEGIN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("MM/dd/yy HH:mm:ss:");
    
    public void  write(AssemblyArchive<T> assemblyArchive, SliceMapFactory sliceMapFactory, DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,OutputStream out) throws IOException{
        writeString(out, XML_BEGIN);
        String submitterReference = assemblyArchive.getSubmitterReference();
        writeString(out, String.format("<assembly submitter_reference=\"%s\" type = \"%s\">\n", 
                submitterReference==null?"":"submitter_reference=\""+submitterReference+"\"", assemblyArchive.getType()));
        writeString(out, createTag(AssemblyArchiveField.CENTER_NAME, assemblyArchive.getCenterName()));
        writeString(out, createTag(AssemblyArchiveField.DATE, 
                DATE_FORMAT.print(DateTimeUtils.currentTimeMillis())));
        writeString(out, createTag(AssemblyArchiveField.TAXON_ID, assemblyArchive.getTaxonId()));
        writeString(out, createTag(AssemblyArchiveField.DESCRIPTION, assemblyArchive.getDescription()));
        writeString(out, createTag(AssemblyArchiveField.STRUCTURE, assemblyArchive.getStructure()));
        writeString(out, createTag(AssemblyArchiveField.NUMBER_OF_CONTIGS, assemblyArchive.getNumberOfContigs()));
        writeString(out, createTag(AssemblyArchiveField.NUMBER_OF_CONSENSUS_BASES, assemblyArchive.getNumberOfContigBases()));
        writeString(out, createTag(AssemblyArchiveField.NUMBER_OF_TRACES, assemblyArchive.getNumberOfTraces()));
        writeString(out, createTag(AssemblyArchiveField.NUMBER_OF_BASES, assemblyArchive.getNumberOfTotalBasecalls()));
        writeString(out, createTag(AssemblyArchiveField.COVERAGE, String.format("%.02f",assemblyArchive.getCoverageRatio())));
        
        for(AssemblyArchiveContigRecord<T> contigRecord : assemblyArchive.getContigRecords()){
            SliceMap sliceMap = sliceMapFactory.createNewSliceMap(
                                        DefaultCoverageMap.buildCoverageMap(contigRecord.getContig().getPlacedReads()), 
                                        qualityDataStore);
            writeContig(out,contigRecord, sliceMap);
        }
        out.flush();
        
    }

    private void writeContig(OutputStream out, AssemblyArchiveContigRecord<T> contigRecord, SliceMap sliceMap) throws IOException {
        writeString(out, 
                String.format("<contig submitter_reference=\"%s\" conformation=\"%s\" type=\"%s\">%n",
                        contigRecord.getSubmitterReference(),
                        contigRecord.getConformation(),
                        contigRecord.getType()));
        Contig<T> contig = contigRecord.getContig();
        writeString(out, createTag(AssemblyArchiveContigField.NUMBER_OF_TRACES, contig.getNumberOfReads()));
        final NucleotideEncodedGlyphs consensus = contig.getConsensus();
        writeString(out, createTag(AssemblyArchiveContigField.NUMBER_OF_CONSENSUS_BASES, consensus.getUngappedLength()));
        long numberOfBasecalls =0;
        for(VirtualPlacedRead<T> read: contig.getVirtualPlacedReads()){
            numberOfBasecalls += read.getEncodedGlyphs().getUngappedLength();
        }
        writeString(out, createTag(AssemblyArchiveField.NUMBER_OF_BASES, numberOfBasecalls));
        writeGaps(out, contig);
        writeString(out, String.format("<%s source=\"%s\">%s</%s>\n",
                AssemblyArchiveContigField.CONSENSUS,
                ContigDataSubmissionType.INLINE,
                NucleotideGlyph.convertToString(
                        NucleotideGlyph.convertToUngapped(consensus.decode())),
                        AssemblyArchiveContigField.CONSENSUS));
        StringBuilder cumulativeQualities = new StringBuilder();
        ConsensusCaller consensusCaller = new ConicConsensusCaller(PhredQuality.valueOf(30));
        int offset=0;
        for(Slice slice : sliceMap){      
            if(!consensus.get(offset).isGap()){
                cumulativeQualities.append(" "+consensusCaller.callConsensus(slice).getConsensusQuality());
            }
            offset ++;
        }
        writeString(out, String.format("<conqualities source=\"INLINE\">%s</conqualities>%n",cumulativeQualities.substring(1)));
        writeTraceData(out, contig);
        writeString(out, "</contig>\n");
        out.flush();
    }

   

    private void writeTraceData(OutputStream out, Contig<T> contig) throws IOException {
        
        final NucleotideEncodedGlyphs consensus = contig.getConsensus();
        SortedSet<VirtualPlacedRead<T>> sortedReads = new TreeSet<VirtualPlacedRead<T>>(new TraceOrderComparator<VirtualPlacedRead<T>>());
        sortedReads.addAll(contig.getVirtualPlacedReads());
        
        for(VirtualPlacedRead<T> read: sortedReads ){
            writeString(out, "<trace>\n");
            writeString(out, createTag("trace_name",read.getId()));
            writeString(out, createTag("nbasecalls", read.getEncodedGlyphs().getUngappedLength()));
            Range validRange =read.getValidRange().convertRange(CoordinateSystem.RESIDUE_BASED);
            int numberOfGaps = read.getEncodedGlyphs().getGapIndexes().size();
            writeString(out, "<valid>\n");
            writeString(out, createTag("start",validRange.getLocalStart()));
            writeString(out, createTag("stop",validRange.getLocalEnd()));
            writeString(out, "</valid>\n");
            writeString(out, String.format("<tiling direction=\"%s\">%n", read.getSequenceDirection()));
            writeString(out, createTag("start",read.getStart()+1));
            writeString(out, createTag("stop",read.getEnd()+1));
            writeString(out, "</tiling>\n");
            writeString(out, String.format("<traceconsensus>%n"));
            
            int ungappedConsnesusStart =consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)read.getStart());
            
            int ungappedConsnesusEnd =consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)read.getEnd());
            writeString(out, createTag("start",ungappedConsnesusStart+1));
            writeString(out, createTag("stop",ungappedConsnesusEnd+1));
            writeString(out, "</traceconsensus>\n");
            if(numberOfGaps >0){
                writeString(out, createTag("ntracegaps", numberOfGaps));
                writeString(out, String.format("<tracegaps source=\"%s\">%s</tracegaps>%n",
                        ContigDataSubmissionType.INLINE,
                        createDeltaGapString(read.getEncodedGlyphs().getGapIndexes())));
            }
            writeString(out, "</trace>\n");
        }
        
       
    }

    private void writeGaps(OutputStream out, Contig<T> contig) throws IOException {
        final List<Integer> gapIndexes = contig.getConsensus().getGapIndexes();
        writeString(out, createTag(AssemblyArchiveField.NUMBER_OF_CONSENSUS_GAPS, gapIndexes.size()));
        writeString(out, String.format("<%s source = \"%s\">%s</%s>\n", 
                AssemblyArchiveField.CONSENSUS_GAP_LIST,
                ContigDataSubmissionType.INLINE,
                createDeltaGapString(gapIndexes),
                AssemblyArchiveField.CONSENSUS_GAP_LIST
                ));
   
    }

    private String createDeltaGapString(List<Integer> gapIndexes) {
        int previous=0;
        StringBuilder sb = new StringBuilder();
        for(Integer index : gapIndexes){
            sb.append(index - previous);
            sb.append(" ");
            previous = index+1;
        }
        return sb.toString().trim();
    }

    private static String createTag(Object field, Object value){
        return new StringBuilder(XMLUtil.beginTag(field))
                    .append(value)
                    .append(XMLUtil.endTag(field))
                    .append("\n")
                    .toString();
    }
    private static void writeString(OutputStream out, String format) throws IOException {
        out.write(format.getBytes());
        
    }
    /**
     * Traces are listed in order of their occurrence in the contig's tiling. 
     * The first order is by trace.tiling.start, 
     * then by length of the tiling range (shortest first), 
     * else by trace.ti, else by trace.trace_name. 
     * @author dkatzel
     *
     *
     */
    private static class TraceOrderComparator<T extends PlacedRead> implements Comparator<T>{
        
        @Override
        public int compare(T o1, T o2) {
            int startCoordinate = Long.valueOf(o1.getStart()).compareTo(o2.getStart());
            if(startCoordinate !=0){
                return startCoordinate;
            }
            int length = Long.valueOf(o1.getLength()).compareTo(o2.getLength());
            if(length !=0){
                return length;
            }
            return o1.getId().compareTo(o2.getId());
        }
        
    }
}
