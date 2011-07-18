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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.assembly.slice.SliceMapFactory;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;
public class AceFileWriter {

    private static final String CONTIG_HEADER = "CO %s %d %d %d %s%n";
   
    public static void writeAceFile(AceAssembly<AceContig> aceAssembly,OutputStream out) throws IOException, DataStoreException{
        writeAceFile(aceAssembly, null,out,false);
    }
    public static void writeAceFileHeader(int numberOfContigs, int numberOfReads, OutputStream out) throws IOException{
        writeString(String.format("AS %d %d%n%n", numberOfContigs, numberOfReads), out);
    }
    public static void writeAceFile(AceAssembly<AceContig> aceAssembly,SliceMapFactory sliceMapFactory, 
            OutputStream out, boolean calculateBestSegments) throws IOException, DataStoreException{
        int numberOfContigs =0;
        int numberOfReads =0;
        DataStore<AceContig> aceDataStore =aceAssembly.getContigDataStore();
        for(Contig<AcePlacedRead> contig: aceDataStore){
            numberOfContigs++;
            numberOfReads += contig.getNumberOfReads();
        }
        try{
            writeAceFileHeader(numberOfContigs, numberOfReads, out);
            PhdDataStore phdDataStore = aceAssembly.getPhdDataStore();
            for(AceContig contig: aceDataStore){
                if(calculateBestSegments){
                    SliceMap sliceMap = sliceMapFactory.createNewSliceMap(
                            new DefaultCoverageMap.Builder(contig.getPlacedReads()).build(), 
                            aceAssembly.getQualityDataStore());                
                    AceFileWriter.writeAceFile(contig, sliceMap, phdDataStore, out, calculateBestSegments);
                }
                else{
                    AceFileWriter.writeAceContig(contig,  phdDataStore, out);
                }
            }
            AceTagMap aceTagMap = aceAssembly.getAceTagMap();
            if(aceTagMap !=null){
                for(ReadAceTag readTag : aceTagMap.getReadTags()){
                    writeReadTag(readTag, out);
                }
                for(ConsensusAceTag consensusTag : aceTagMap.getConsensusTags()){
                    writeConsensusTag(consensusTag, out);
                }
                for(WholeAssemblyAceTag wholeAssemblyTag : aceTagMap.getWholeAssemblyTags()){
                    writeWholeAssemblyTag(wholeAssemblyTag, out);
                }
            }
        }finally{
            IOUtil.closeAndIgnoreErrors(out);
        }
    }
    private static void writeWholeAssemblyTag(
            WholeAssemblyAceTag wholeAssemblyTag, OutputStream out) throws IOException {
        writeString(String.format("WA{%n%s %s %s%n%s%n}%n", 
                wholeAssemblyTag.getType(),
                wholeAssemblyTag.getCreator(),                
                AceFileUtil.TAG_DATE_TIME_FORMATTER.print(wholeAssemblyTag.getCreationDate().getTime()),
                wholeAssemblyTag.getData()), out);

        
    }
    private static void writeConsensusTag(ConsensusAceTag consensusTag,
            OutputStream out) throws IOException {
        StringBuilder tagBodyBuilder = new StringBuilder();
        if(consensusTag.getData() !=null){
            tagBodyBuilder.append(consensusTag.getData());
        }
        if(!consensusTag.getComments().isEmpty()){
            for(String comment :consensusTag.getComments()){
                tagBodyBuilder.append(String.format("COMMENT{%n%sC}%n",comment));            
            }
        }
        writeString(String.format("CT{%n%s %s %s %d %d %s%s%n%s}%n", 
                consensusTag.getId(),
                consensusTag.getType(),
                consensusTag.getCreator(),
                consensusTag.getStart(),
                consensusTag.getEnd(),
                AceFileUtil.TAG_DATE_TIME_FORMATTER.print(consensusTag.getCreationDate().getTime()),
                consensusTag.isTransient()?" NoTrans":"",
                        tagBodyBuilder.toString()), out);
        
    }
    private static void writeReadTag(ReadAceTag readTag, OutputStream out) throws IOException {
        writeString(String.format("RT{%n%s %s %s %d %d %s%n}%n", 
                        readTag.getId(),
                        readTag.getType(),
                        readTag.getCreator(),
                        readTag.getStart(),
                        readTag.getEnd(),
                        AceFileUtil.TAG_DATE_TIME_FORMATTER.print(readTag.getCreationDate().getTime())), out);
        
    }
    public static void writeAceContig(Contig<AcePlacedRead> contig,
            PhdDataStore phdDataStore, 
            OutputStream out) throws IOException, DataStoreException{
        final NucleotideSequence consensus = contig.getConsensus();
        
        writeString(String.format(CONTIG_HEADER, 
                contig.getId(), 
                consensus.getLength(),
                contig.getNumberOfReads(),
                0,
                "U"), //always uncomplemented for now...
                
                out);
        out.flush();
        writeString(String.format("%s%n%n%n",AceFileUtil.convertToAcePaddedBasecalls(consensus)), out);
        out.flush();
        writeFakeUngappedConsensusQualities(consensus, out);
        writeString(String.format("%n"), out);
        out.flush();
        List<AssembledFrom> assembledFroms = getSortedAssembledFromsFor(contig);
        StringBuilder assembledFromBuilder = new StringBuilder();
        StringBuilder placedReadBuilder = new StringBuilder();
        
        for(AssembledFrom assembledFrom : assembledFroms){
            String id = assembledFrom.getId();
            final Phd phd = phdDataStore.get(id);
            final AcePlacedRead realPlacedRead = contig.getPlacedReadById(id);
             long fullLength = realPlacedRead.getUngappedFullLength();
            assembledFromBuilder.append(createAssembledFromRecord(realPlacedRead,fullLength));
            placedReadBuilder.append(createPlacedReadRecord(realPlacedRead, phd));
        }
        assembledFromBuilder.append(String.format("%n"));
        placedReadBuilder.append(String.format("%n"));
        writeString(assembledFromBuilder.toString(),out);
        out.flush();
        writeString(placedReadBuilder.toString(),out);
        out.flush();
    }
    private static List<AssembledFrom> getSortedAssembledFromsFor(
            Contig<AcePlacedRead> contig){
        List<AssembledFrom> assembledFroms = new ArrayList<AssembledFrom>(contig.getNumberOfReads());
        for(AcePlacedRead read : contig.getPlacedReads()){
            long fullLength =read.getUngappedFullLength();
            assembledFroms.add(AssembledFrom.createFrom(read, fullLength));
        }
        Collections.sort(assembledFroms);
        return assembledFroms;
    }
    public static void writeAceFile(Contig<AcePlacedRead> contig,
            SliceMap sliceMap,
            DataStore<Phd> phdDataStore, 
            OutputStream out, boolean calculateBestSegments) throws IOException, DataStoreException{
        final NucleotideSequence consensus = contig.getConsensus();
        StringBuilder bestSegmentBuilder = new StringBuilder();
        if(calculateBestSegments){
            System.out.println("calculating best segments...");
            AceBestSegmentMap bestSegments = new OnTheFlyAceBestSegmentMap(
                    sliceMap, consensus);
            int numberOfBestSegments=0;
            for(AceBestSegment bestSegment : bestSegments){
                numberOfBestSegments++;
                final Range gappedConsensusRange = bestSegment.getGappedConsensusRange().convertRange(CoordinateSystem.RESIDUE_BASED);
                bestSegmentBuilder.append(String.format("BS %d %d %s%n", 
                        gappedConsensusRange.getLocalStart(),
                        gappedConsensusRange.getLocalEnd(),
                        bestSegment.getReadName()));
            }
            writeString(String.format(CONTIG_HEADER, 
                    contig.getId(), 
                    consensus.getLength(),
                    contig.getNumberOfReads(),
                    numberOfBestSegments,
                    "U"), //always uncomplemented for now...
                    
                    out);
        }else{
        writeString(String.format(CONTIG_HEADER, 
                contig.getId(), 
                consensus.getLength(),
                contig.getNumberOfReads(),
                0,
                "U"), //always uncomplemented for now...
                
                out);
        }
        writeString(String.format("%s%n%n",AceFileUtil.convertToAcePaddedBasecalls(consensus)), out);
        writeUngappedConsensusQualities(consensus,sliceMap, out);
        
        writeString(String.format("%n"), out);
        List<AssembledFrom> assembledFroms = getSortedAssembledFromsFor(contig);
        
        for(AssembledFrom assembledFrom : assembledFroms){
            String id = assembledFrom.getId();
            long fullLength = phdDataStore.get(id).getBasecalls().getLength();
            writeAssembledFromRecords(contig.getPlacedReadById(id),fullLength,out);
        }
        out.flush();
        if(calculateBestSegments){
            writeString(bestSegmentBuilder.toString(),out);
            writeString(String.format("%n"), out);
        }
        out.flush();
        for(AssembledFrom assembledFrom : assembledFroms){
            String id = assembledFrom.getId();          
            AcePlacedRead read = contig.getPlacedReadById(id);
            writePlacedRead(read, phdDataStore.get(id),out);
        }
        out.flush();
        
    }
    private static void writeFakeUngappedConsensusQualities(NucleotideSequence consensus,
            OutputStream out) throws IOException {
        StringBuilder result = new StringBuilder();
        int numberOfQualitiesSoFar=0;
        for(int i=0; i< consensus.getLength(); i++){
            NucleotideGlyph base = consensus.get(i);
            if(base.isGap()){
                continue;
            }
            result.append(" 99");
            numberOfQualitiesSoFar++;
            if(numberOfQualitiesSoFar%50==0){
                result.append(String.format("%n"));
            }
        }
        writeString(String.format("BQ%n%s%n", result.toString()), out);
    }
    private static void writeUngappedConsensusQualities(NucleotideSequence consensus,SliceMap sliceMap,
            OutputStream out) throws IOException {
        StringBuilder result = new StringBuilder();
        int numberOfQualitiesSoFar=0;
        for(int i=0; i< consensus.getLength(); i++){
            NucleotideGlyph base = consensus.get(i);
            if(base.isGap()){
                continue;
            }
            Slice slice = sliceMap.getSlice(i);
            int sumOfQualities=0;
            for(SliceElement element : slice){
                sumOfQualities+=element.getQuality().getNumber().intValue();
                if(sumOfQualities >=99){
                    sumOfQualities =99;
                    //no point in continuing...
                    break;
                }
            }
            result.append(String.format(" %d", sumOfQualities));
            numberOfQualitiesSoFar++;
            if(numberOfQualitiesSoFar%50==0){
                result.append(String.format("%n"));
            }
        }
        writeString(String.format("BQ%n%s%n", result.toString()), out);
        
    }

    private static String createAssembledFromRecord(AcePlacedRead read, long fullLength){
        AssembledFrom assembledFrom = AssembledFrom.createFrom(read, fullLength);
        return AceFileUtil.createAssembledFromRecord(assembledFrom);
    }
    private static void writeAssembledFromRecords(AcePlacedRead read, long fullLength,OutputStream out) throws IOException{
        AssembledFrom assembledFrom = AssembledFrom.createFrom(read, fullLength);
        writeString(AceFileUtil.createAssembledFromRecord(assembledFrom), out);
    } 
    
    private static String createPlacedReadRecord(AcePlacedRead read, Phd phd){
        return AceFileUtil.createAcePlacedReadRecord(
                read.getId(),read.getEncodedGlyphs(),  
                read.getValidRange(), 
                read.getSequenceDirection(),
                phd, 
                read.getPhdInfo());
        
    }
    
    private static void writePlacedRead(AcePlacedRead read, Phd phd,OutputStream out ) throws IOException{
        writeString(AceFileUtil.createAcePlacedReadRecord(
                read.getId(),read.getEncodedGlyphs(),  read.getValidRange(), read.getSequenceDirection(),phd, read.getPhdInfo()),out);
        
    }
    
    private static void writeString(String s, OutputStream out) throws IOException{
        out.write(s.getBytes());
        
    }
}
