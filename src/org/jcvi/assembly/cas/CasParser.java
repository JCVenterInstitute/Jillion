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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.assembly.cas.alignment.CasAlignment;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegionType;
import org.jcvi.assembly.cas.alignment.CasAlignmentType;
import org.jcvi.assembly.cas.alignment.DefaultCasAlignment;
import org.jcvi.assembly.cas.alignment.DefaultCasMatch;
import org.jcvi.assembly.cas.alignment.score.CasAlignmentScore;
import org.jcvi.assembly.cas.alignment.score.CasAlignmentScoreBuilder;
import org.jcvi.assembly.cas.alignment.score.CasScoreType;
import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;
import org.jcvi.assembly.cas.alignment.score.DefaultCasScoringScheme;
import org.jcvi.io.IOUtil;

public class CasParser {
    private BigInteger offset;
    private  int numberOfBytesForContigPosition,numberOfBytesForContigNumber;
    private  long numberOfReads;
    private CasScoringScheme scoringScheme;
    
    private CasParser(File file, CasFileVisitor visitor, boolean parseMatches) throws IOException{
        parseMetaData(new FileInputStream(file),visitor);
        if(parseMatches){
            parseMatches(new FileInputStream(file),visitor);
        }
        visitor.visitEndOfFile();
    }
    private void parseMatches(InputStream in,
            CasFileVisitor visitor) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        try{
        IOUtil.blockingSkip(in, 16);
        for(int i=0; i<numberOfReads; i++){
            byte info = dataIn.readByte();
            boolean hasMatch= (info & 0x01)!=0;
            boolean hasMultipleMatches= (info & 0x02)!=0;
            boolean hasMultipleAlignments= (info & 0x04)!=0;
            boolean isPartOfPair= (info & 0x08)!=0;
            long totalNumberOfMatches=hasMatch?1:0, numberOfReportedAlignments=hasMatch?1:0;
            if(hasMultipleMatches){                
                totalNumberOfMatches = CasUtil.parseByteCountFrom(dataIn) +2;
            //    System.out.println("has multiple matches "+ totalNumberOfMatches);
            }
            if(hasMultipleAlignments){
                numberOfReportedAlignments = CasUtil.parseByteCountFrom(dataIn) +2;
             //   System.out.println("#alignments "+ numberOfReportedAlignments);
            }
            
            int score=0;
            CasAlignment chosenAlignment=null;
            if(hasMatch){
           
                long numberOfBytesInForThisMatch =CasUtil.parseByteCountFrom(dataIn);
                long contigSequenceId = CasUtil.readCasUnsignedInt(dataIn, this.numberOfBytesForContigNumber);
                long startPosition = CasUtil.readCasUnsignedInt(dataIn, this.numberOfBytesForContigPosition);
                boolean isreverse = dataIn.readBoolean();
                DefaultCasAlignment.Builder builder = new DefaultCasAlignment.Builder(
                                                    contigSequenceId, startPosition, 
                                                    isreverse);
                long count=0;
                
                while(count <numberOfBytesInForThisMatch){
                    short matchValue = CasUtil.readCasUnsignedByte(dataIn);
                    if(matchValue == 255){
                        builder.addPhaseChange(dataIn.readByte());
                        
                        count++;
                    }
                    else if(matchValue<128){
                        builder.addRegion(CasAlignmentRegionType.MATCH_MISMATCH, matchValue +1);
                        
                    }
                    else if(matchValue<192){
                        builder.addRegion(CasAlignmentRegionType.INSERT, matchValue -127);
                    }
                    else{
                        builder.addRegion(CasAlignmentRegionType.DELETION, matchValue -191);
                    }
                    count++;
                }
                chosenAlignment =builder.build();
            }
            visitor.visitMatch(new DefaultCasMatch(hasMatch, totalNumberOfMatches, numberOfReportedAlignments,
                    isPartOfPair, chosenAlignment,score));
        }
        }finally{
            IOUtil.closeAndIgnoreErrors(dataIn);
        }
        
    }
    private void parseMetaData(InputStream in, CasFileVisitor visitor) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        try{
            byte[] magicNumber = IOUtil.readByteArray(dataIn, 8);
            if(!Arrays.equals(CAS_MAGIC_NUMBER, magicNumber)){
                throw new IllegalArgumentException("input stream not a valid cas file wrong magic number");
            }
            
            visitor.visitFile();
            offset = CasUtil.readCasUnsignedLong(dataIn);
           IOUtil.blockingSkip(dataIn, offset.longValue()-16);
          
           
           long numberOfContigSequences = CasUtil.readCasUnsignedInt(dataIn);
           
           numberOfReads = CasUtil.readCasUnsignedInt(dataIn);
          
            visitor.visitMetaData(numberOfContigSequences, numberOfReads);
            String nameOfAssemblyProgram = CasUtil.parseCasStringFrom(dataIn);
            String version = CasUtil.parseCasStringFrom(dataIn);
            String parameters = CasUtil.parseCasStringFrom(dataIn);
            visitor.visitAssemblyProgramInfo(nameOfAssemblyProgram, version, parameters);
            
            long numberOfContigFiles =CasUtil.parseByteCountFrom(dataIn);
           visitor.visitNumberOfContigFiles(numberOfContigFiles);            
            for(long i=0; i< numberOfContigFiles; i++){
              boolean twoFiles =(dataIn.read() & 0x01)==1;
              long numberOfSequencesInFile = CasUtil.readCasUnsignedInt(dataIn);
              BigInteger residuesInFile = CasUtil.readCasUnsignedLong(dataIn);
              List<String> names = new ArrayList<String>();
              names.add(CasUtil.parseCasStringFrom(dataIn));
              if(twoFiles){
                  names.add(CasUtil.parseCasStringFrom(dataIn));
              }
              visitor.visitContigFileInfo(new DefaultCasFileInfo(names, numberOfSequencesInFile, residuesInFile));
            }
            
            long numberOfReadFiles =CasUtil.parseByteCountFrom(dataIn);
            visitor.visitNumberOfReadFiles(numberOfReadFiles);            
             for(long i=0; i< numberOfReadFiles; i++){
               boolean twoFiles =(dataIn.read() & 0x01)==1;
               long numberOfSequencesInFile = CasUtil.readCasUnsignedInt(dataIn);
               BigInteger residuesInFile = CasUtil.readCasUnsignedLong(dataIn);
               List<String> names = new ArrayList<String>();
               names.add(CasUtil.parseCasStringFrom(dataIn));
               if(twoFiles){
                   names.add(CasUtil.parseCasStringFrom(dataIn));
               }
               visitor.visitReadFileInfo(new DefaultCasFileInfo(names, numberOfSequencesInFile, residuesInFile));
             }
         
            CasScoreType scoreType = CasScoreType.valueOf((byte)dataIn.read());
            if(scoreType != CasScoreType.NO_SCORE){
                CasAlignmentScoreBuilder alignmentScoreBuilder = new CasAlignmentScoreBuilder()
                                    .firstInsertion(CasUtil.readCasUnsignedShort(dataIn))
                                    .insertionExtension(CasUtil.readCasUnsignedShort(dataIn))
                                    .firstDeletion(CasUtil.readCasUnsignedShort(dataIn))
                                    .deletionExtension(CasUtil.readCasUnsignedShort(dataIn))
                                    .match(CasUtil.readCasUnsignedShort(dataIn))
                                    .transition(CasUtil.readCasUnsignedShort(dataIn))
                                    .transversion(CasUtil.readCasUnsignedShort(dataIn))
                                    .unknown(CasUtil.readCasUnsignedShort(dataIn));
                if(scoreType == CasScoreType.COLOR_SPACE_SCORE){
                    alignmentScoreBuilder.colorSpaceError(dataIn.readShort());
                }
                CasAlignmentScore score = alignmentScoreBuilder.build();
                CasAlignmentType alignmentType = CasAlignmentType.valueOf((byte)dataIn.read());
                scoringScheme = new DefaultCasScoringScheme(scoreType, score, alignmentType);
                visitor.visitScoringScheme(scoringScheme);
                long maxContigLength=0;
                for(long i=0; i<numberOfContigSequences; i++){
                    long contigLength = CasUtil.readCasUnsignedInt(dataIn);
                    boolean isCircular = (dataIn.readUnsignedShort() & 0x01)==1;
                    visitor.visitContigDescription(new DefaultCasContigDescription(contigLength, isCircular));
                    maxContigLength = Math.max(maxContigLength, contigLength);
                }
                numberOfBytesForContigNumber = CasUtil.numberOfBytesRequiredFor(numberOfContigSequences);
                            
                numberOfBytesForContigPosition =CasUtil.numberOfBytesRequiredFor(maxContigLength);
                //contig pairs not currently used so ignore them
                
            }
        }
        finally{
            IOUtil.closeAndIgnoreErrors(dataIn);
        }
        
    }
    private static final byte[] CAS_MAGIC_NUMBER = new byte[]{
        (byte)0x43,
        (byte)0x4c,
        (byte)0x43,
        (byte)0x80,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x01
    };
    
    public static void parseCas(File file, CasFileVisitor visitor) throws IOException{
        new CasParser(file, visitor,true);        
    }
    public static void parseOnlyMetaData(File file, CasFileVisitor visitor) throws IOException{
        new CasParser(file, visitor,false);
        
    }
}
