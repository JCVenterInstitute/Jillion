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
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.assembly.slice.SliceMapFactory;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.trace.sanger.phd.Phd;
public class AceFileWriter {

    private static final String CONTIG_HEADER = "CO %s %d %d %d %s%n";
    private static final PhredQuality ACE_DEFAULT_HIGH_QUALITY_THRESHOLD = PhredQuality.valueOf(20);
    
    
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
            writeString(String.format("AS %d %d%n", numberOfContigs, numberOfReads), out);
            DataStore<Phd> phdDataStore = aceAssembly.getPhdDataStore();
            for(AceContig contig: aceDataStore){
                if(calculateBestSegments){
                    SliceMap sliceMap = sliceMapFactory.createNewSliceMap(
                            new DefaultCoverageMap.Builder(contig.getPlacedReads()).build(), 
                            aceAssembly.getQualityDataStore());                
                    AceFileWriter.writeAceFile(contig, sliceMap, phdDataStore, out, calculateBestSegments);
                }
                else{
                    AceFileWriter.writeAceFile(contig,  phdDataStore, out);
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
                AceFileParser.TAG_DATE_TIME_FORMATTER.print(wholeAssemblyTag.getCreationDate().getTime()),
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
                AceFileParser.TAG_DATE_TIME_FORMATTER.print(consensusTag.getCreationDate().getTime()),
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
                        AceFileParser.TAG_DATE_TIME_FORMATTER.print(readTag.getCreationDate().getTime())), out);
        
    }
    public static void writeAceFile(Contig<AcePlacedRead> contig,
            DataStore<Phd> phdDataStore, 
            OutputStream out) throws IOException, DataStoreException{
        final NucleotideEncodedGlyphs consensus = contig.getConsensus();
        
        writeString(String.format(CONTIG_HEADER, 
                contig.getId(), 
                consensus.getLength(),
                contig.getNumberOfReads(),
                0,
                "U"), //always uncomplemented for now...
                
                out);
        out.flush();
        writeString(String.format("%s%n%n",convertToAcePaddedBasecalls(consensus)), out);
        out.flush();
        writeFakeUngappedConsensusQualities(consensus, out);
        writeString(String.format("%n"), out);
        out.flush();
        for(AcePlacedRead read : contig.getPlacedReads()){
            long fullLength = phdDataStore.get(read.getId()).getBasecalls().getLength();
            writeAssembledFromRecords(read,fullLength,out);
        }
        out.flush();
        for(AcePlacedRead read : contig.getPlacedReads()){            
            writePlacedRead(read, phdDataStore.get(read.getId()),out);
        }
        out.flush();
    }
    public static void writeAceFile(Contig<AcePlacedRead> contig,
            SliceMap sliceMap,
            DataStore<Phd> phdDataStore, 
            OutputStream out, boolean calculateBestSegments) throws IOException, DataStoreException{
        final NucleotideEncodedGlyphs consensus = contig.getConsensus();
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
        writeString(String.format("%s%n%n",convertToAcePaddedBasecalls(consensus)), out);
        writeUngappedConsensusQualities(consensus,sliceMap, out);
        
        writeString(String.format("%n"), out);
        for(AcePlacedRead read : contig.getPlacedReads()){
            long fullLength = phdDataStore.get(read.getId()).getBasecalls().getLength();
            writeAssembledFromRecords(read,fullLength,out);
        }
        if(calculateBestSegments){
            writeString(bestSegmentBuilder.toString(),out);
            writeString(String.format("%n"), out);
        }
        for(AcePlacedRead read : contig.getPlacedReads()){            
            writePlacedRead(read, phdDataStore.get(read.getId()),out);
        }
        
        
    }
    private static void writeFakeUngappedConsensusQualities(NucleotideEncodedGlyphs consensus,
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
    private static void writeUngappedConsensusQualities(NucleotideEncodedGlyphs consensus,SliceMap sliceMap,
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

   
    private static void writeAssembledFromRecords(AcePlacedRead read, long fullLength,OutputStream out) throws IOException{
        Range validRange;
        if(read.getSequenceDirection()==SequenceDirection.REVERSE){
            validRange = AssemblyUtil.reverseComplimentValidRange(read.getValidRange(), fullLength);
        }
        else{
            validRange = read.getValidRange();
        }
        writeString(String.format("AF %s %s %d%n",
                read.getId(),
                read.getSequenceDirection()==SequenceDirection.FORWARD? "U":"C",
                        read.getStart()-validRange.getStart()+1),
                        out);
    }
    private static void writePlacedRead(AcePlacedRead read, Phd phd,OutputStream out ) throws IOException{
       
        final String readId = read.getId();
        final List<NucleotideGlyph> phdFullBases = phd.getBasecalls().decode();
        
        List<NucleotideGlyph> fullGappedValidRange;
        if(read.getSequenceDirection() == SequenceDirection.FORWARD){
            fullGappedValidRange = AssemblyUtil.buildGappedComplimentedFullRangeBases(read, 
                    phdFullBases);
        }else{
            final List<NucleotideGlyph> complimentedFullBases = NucleotideGlyph.reverseCompliment(phdFullBases);
            Range validRange = AssemblyUtil.reverseComplimentValidRange(read.getValidRange(),complimentedFullBases.size());
            
            fullGappedValidRange=new ArrayList<NucleotideGlyph>();
            fullGappedValidRange.addAll(complimentedFullBases.subList(0, (int)validRange.getStart()));
            fullGappedValidRange.addAll(read.getEncodedGlyphs().decode());
            fullGappedValidRange.addAll(complimentedFullBases.subList((int)validRange.getEnd()+1, complimentedFullBases.size()));
            
            
        }
           

        writeString(String.format("RD %s %d 0 0%n",
                        readId,
                        fullGappedValidRange.size()),out);
        writeString(convertToAcePaddedBasecalls(fullGappedValidRange,phd)
                            , out);
        writeString(String.format("%n"), out);
        writeRanges(read,fullGappedValidRange, out);
        writePhdRecord(read,out);
        
    }
    private static void writePhdRecord(AcePlacedRead read, OutputStream out) throws IOException{
        PhdInfo info =read.getPhdInfo();
        writeString(String.format("DS CHROMAT_FILE: %s PHD_FILE: %s TIME: %s%n", 
                
                info.getTraceName(),
                info.getPhdName(),
                AceFileParser.CHROMAT_DATE_TIME_FORMATTER.print(info.getPhdDate().getTime())
                
        ),out);
    }
    private static void writeRanges(AcePlacedRead read,List<NucleotideGlyph>fullGappedBasecalls, OutputStream out)
            throws IOException {
        int numberOfGaps = read.getEncodedGlyphs().getGapIndexes().size();
        Range ungappedValidRange = read.getValidRange().convertRange(CoordinateSystem.RESIDUE_BASED);
        Range gappedValidRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                ungappedValidRange.getLocalStart(),
                ungappedValidRange.getLocalEnd()+numberOfGaps);
        
        if(read.getSequenceDirection()==SequenceDirection.REVERSE){
            final int fullLength = fullGappedBasecalls.size();
            gappedValidRange = AssemblyUtil.reverseComplimentValidRange(gappedValidRange, fullLength);
           
        }

        
        writeString(String.format("%nQA %d %d %d %d%n",
                gappedValidRange.getLocalStart(), gappedValidRange.getLocalEnd(),
                gappedValidRange.getLocalStart(), gappedValidRange.getLocalEnd()
                ),
                out);
    }
    private static String convertToAcePaddedBasecalls(NucleotideEncodedGlyphs basecalls){
       return convertToAcePaddedBasecalls(basecalls.decode(),null);
    }
    private static String convertToAcePaddedBasecalls(List<NucleotideGlyph> basecalls,Phd phd){
        StringBuilder result = new StringBuilder();
        int numberOfGapsSoFar=0;
        for(int i=0; i< basecalls.size(); i++){
            NucleotideGlyph base = basecalls.get(i);
            if(base == NucleotideGlyph.Gap){
                result.append("*");
                numberOfGapsSoFar++;
            }
            else{
                if(phd!=null){
                    PhredQuality quality =phd.getQualities().get(i-numberOfGapsSoFar);
                    if(quality.compareTo(ACE_DEFAULT_HIGH_QUALITY_THRESHOLD)<0){
                        result.append(base.toString().toLowerCase());
                    }
                    else{
                        result.append(base);
                    }
                }else{
                    result.append(base);
                }
            }
        }
        return result.toString().replaceAll("(.{50})", "$1"+String.format("%n"));
    }
    private static void writeString(String s, OutputStream out) throws IOException{
        out.write(s.getBytes());
        
    }
}
