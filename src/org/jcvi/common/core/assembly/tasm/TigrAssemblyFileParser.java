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

package org.jcvi.common.core.assembly.tasm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.ctg.ContigFileVisitor;
import org.jcvi.common.core.io.IOUtil;

/**
 * {@code TigrAssemblyFileParser} parses TIGR Assembler contig files.
 * @author dkatzel
 *
 *
 */
public final class TigrAssemblyFileParser {
    private static final String CR = "\n";
    /**
     * Each contig data is separated by a pipe ('|').
     */
    private static final String END_OF_CONTIG = "|";
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+.*$)");
   
    private TigrAssemblyFileParser(){
    	//can not instantiate.
    }
    /**
     * Parse the given TIGR Assembly file and call the appropriate 
     * visitXXX methods in the given {@link ContigFileVisitor}.
     * If the given visitor is a  {@link TigrAssemblyFileVisitor}
     * then additional visitXXX methods specific to {@link TigrAssemblyFileVisitor}
     * are called as well.
     * @param tasmFile the TIGR Assembly file.
     * @param visitor the {@link ContigFileVisitor} implementation to visit.
     * @throws FileNotFoundException if tasmFile does not exist.
     */
    public static void parse(File tasmFile, ContigFileVisitor visitor) throws FileNotFoundException{
        InputStream in = new FileInputStream(tasmFile);
        try{
            parse(in,visitor);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given TIGR Assembly {@link InputStream} and call the appropriate 
     * visitXXX methods in the given {@link ContigFileVisitor}.
     * If the given visitor is a  {@link TigrAssemblyFileVisitor}
     * then additional visitXXX methods specific to {@link TigrAssemblyFileVisitor}
     * are called as well.
     * @param inputStream an InputStream containing TIGR Assembly file data.
     * @param visitor the {@link ContigFileVisitor} implementation to visit.
     * @throws FileNotFoundException if tasmFile does not exist.
     */
    public static void parse(InputStream inputStream, ContigFileVisitor visitor){
        boolean isTigrAssemblyVisitor = visitor instanceof TigrAssemblyFileVisitor;
        
        Scanner scanner = new Scanner(inputStream, IOUtil.UTF_8_NAME).useDelimiter(CR);
        boolean inContigRecord=true;
        String currentSequenceName=null;
        int currentSequenceLeftEnd=-1;
        int currentSequenceRightEnd=-1;
        int currentOffset=0;
        String currentBases=null;
        String currentContigId=null;
        String currentContigConsensus =null;
        visitor.visitFile();
        if(isTigrAssemblyVisitor){
            ((TigrAssemblyFileVisitor)visitor).visitBeginContigBlock();
        }
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            visitor.visitLine(line+CR);
            Matcher matcher = KEY_VALUE_PATTERN.matcher(line);
            
            if(matcher.find()){
                String key = matcher.group(1);
                String value = matcher.group(2);
                if(inContigRecord){
                    if(isTigrAssemblyVisitor){
                        ((TigrAssemblyFileVisitor)visitor).visitContigAttribute(key, value);
                    }
                    if("asmbl_id".equals(key)){
                        currentContigId =value;
                    }
                    else if("lsequence".equals(key)){
                        currentContigConsensus = value;
                    }
                }else{
                    if(isTigrAssemblyVisitor){
                        ((TigrAssemblyFileVisitor)visitor).visitReadAttribute(key, value);
                    }
                    if("lsequence".equals(key)){
                        currentBases =value;
                    }
                    else if("seq_lend".equals(key)){
                        currentSequenceLeftEnd =Integer.parseInt(value);
                    }
                    else if("seq_rend".equals(key)){
                        currentSequenceRightEnd =Integer.parseInt(value);
                    }
                    else if("offset".equals(key)){
                        currentOffset =Integer.parseInt(value);
                    }
                    else if("seq_name".equals(key)){
                       currentSequenceName = value;
                    }
                }
            }else{
                if(isEndOfRecord(line)){
                    if(inContigRecord){
                        inContigRecord=false;
                        visitor.visitNewContig(currentContigId);
                        visitor.visitConsensusBasecallsLine(currentContigConsensus);
                        currentContigId=null;
                        currentContigConsensus =null;
                        if(isTigrAssemblyVisitor){
                            ((TigrAssemblyFileVisitor)visitor).visitEndContigBlock();
                            ((TigrAssemblyFileVisitor)visitor).visitBeginReadBlock();
                        }
                    }else{
                        //end of current read
                        visitRead(visitor, currentSequenceName,
                                currentSequenceLeftEnd,
                                currentSequenceRightEnd, currentOffset,
                                currentBases);
                        if(isTigrAssemblyVisitor){
                            ((TigrAssemblyFileVisitor)visitor).visitEndReadBlock();
                            ((TigrAssemblyFileVisitor)visitor).visitBeginReadBlock();
                        }                      
                        //reset current read data
                        currentSequenceName=null;
                        currentSequenceLeftEnd=-1;
                        currentSequenceRightEnd=-1;
                        currentOffset=0;
                        currentBases=null;
                    }
                }else if(isEndOfContig(line)){
                        visitRead(visitor, currentSequenceName,
                                currentSequenceLeftEnd,
                                currentSequenceRightEnd, currentOffset,
                                currentBases);
                    
                        inContigRecord=true;
                        if(isTigrAssemblyVisitor){         
                            ((TigrAssemblyFileVisitor)visitor).visitEndReadBlock();
                            ((TigrAssemblyFileVisitor)visitor).visitBeginContigBlock();
                        }
                        //reset current read data
                        currentSequenceName=null;
                        currentSequenceLeftEnd=-1;
                        currentSequenceRightEnd=-1;
                        currentOffset=0;
                        currentBases=null;
                }
                
            }
        }
        //visit last read if there is any data left
        if(currentSequenceName!=null){
            visitRead(visitor, currentSequenceName,
                    currentSequenceLeftEnd,
                    currentSequenceRightEnd, currentOffset,
                    currentBases);
           
        }
        if(isTigrAssemblyVisitor){
            ((TigrAssemblyFileVisitor)visitor).visitEndReadBlock();
        }
        visitor.visitEndOfFile();
    }
    private static void visitRead(ContigFileVisitor visitor,
            String currentSequenceName, int currentSequenceLeftEnd,
            int currentSequenceRightEnd, int currentOffset, String currentBases) {
        Direction dir = currentSequenceLeftEnd > currentSequenceRightEnd?
                                Direction.REVERSE:
                                Direction.FORWARD;
        final Range validRange;
        if(dir == Direction.REVERSE){
            validRange= Range.create(CoordinateSystem.RESIDUE_BASED, currentSequenceRightEnd, currentSequenceLeftEnd);
        }else{
            validRange= Range.create(CoordinateSystem.RESIDUE_BASED, currentSequenceLeftEnd, currentSequenceRightEnd);
        }
        
        visitor.visitNewRead(currentSequenceName, currentOffset, validRange, dir);
        visitor.visitReadBasecallsLine(currentBases);
    }
    
    /**
     * @param line
     * @return
     */
    private static boolean isEndOfContig(String line) {
        return line.trim().equals(END_OF_CONTIG);
    }

    private static final boolean isEndOfRecord(String line) {
        return line.trim().isEmpty();
    }
}
