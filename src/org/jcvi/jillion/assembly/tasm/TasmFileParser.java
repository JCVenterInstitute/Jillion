/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.ctg.ContigFileVisitor;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code TasmFileParser} parses TIGR Assembler contig files
 * from a {@literal .tasm} file.
 * @author dkatzel
 *
 *
 */
public final class TasmFileParser {
    private static final String CR = "\n";
    /**
     * Each contig data is separated by a pipe ('|').
     */
    private static final String END_OF_CONTIG = "|";
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+.*$)");
   
    private TasmFileParser(){
    	//can not instantiate.
    }
    /**
     * Parse the given TIGR Assembly file and call the appropriate 
     * visitXXX methods in the given {@link ContigFileVisitor}.
     * If the given visitor is a  {@link TasmFileVisitor}
     * then additional visitXXX methods specific to {@link TasmFileVisitor}
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
     * If the given visitor is a  {@link TasmFileVisitor}
     * then additional visitXXX methods specific to {@link TasmFileVisitor}
     * are called as well.
     * @param inputStream an InputStream containing TIGR Assembly file data.
     * @param visitor the {@link ContigFileVisitor} implementation to visit.
     * @throws FileNotFoundException if tasmFile does not exist.
     */
    public static void parse(InputStream inputStream, ContigFileVisitor visitor){
        boolean isTigrAssemblyVisitor = visitor instanceof TasmFileVisitor;
        
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
            ((TasmFileVisitor)visitor).visitBeginContigBlock();
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
                        ((TasmFileVisitor)visitor).visitContigAttribute(key, value);
                    }
                    if("asmbl_id".equals(key)){
                        currentContigId =value;
                    }
                    else if("lsequence".equals(key)){
                        currentContigConsensus = value;
                    }
                }else{
                    if(isTigrAssemblyVisitor){
                        ((TasmFileVisitor)visitor).visitReadAttribute(key, value);
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
            	if(inContigRecord){
                    inContigRecord=false;
                    visitor.visitNewContig(currentContigId);
                    visitor.visitConsensusBasecallsLine(currentContigConsensus);
                    currentContigId=null;
                    currentContigConsensus =null;
                    if(isTigrAssemblyVisitor){
                        ((TasmFileVisitor)visitor).visitEndContigBlock();
                        ((TasmFileVisitor)visitor).visitBeginReadBlock();
                    }
            	}
                if(isEndOfRecord(line)){                    
                    //end of current read
                    visitRead(visitor, currentSequenceName,
                            currentSequenceLeftEnd,
                            currentSequenceRightEnd, currentOffset,
                            currentBases);
                    if(isTigrAssemblyVisitor){
                        ((TasmFileVisitor)visitor).visitEndReadBlock();
                        ((TasmFileVisitor)visitor).visitBeginReadBlock();
                    }                      
                    //reset current read data
                    currentSequenceName=null;
                    currentSequenceLeftEnd=-1;
                    currentSequenceRightEnd=-1;
                    currentOffset=0;
                    currentBases=null;
                }else if(isEndOfContig(line)){
                		if(currentSequenceName !=null){
                			visitRead(visitor, currentSequenceName,
                                    currentSequenceLeftEnd,
                                    currentSequenceRightEnd, currentOffset,
                                    currentBases);
                		}
                        inContigRecord=true;
                        if(isTigrAssemblyVisitor){         
                        	((TasmFileVisitor)visitor).visitEndReadBlock();
                            ((TasmFileVisitor)visitor).visitBeginContigBlock();
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
        if(currentSequenceName==null){
             visitor.visitNewContig(currentContigId);
             visitor.visitConsensusBasecallsLine(currentContigConsensus);
        	//no reads for this contig
        	if(isTigrAssemblyVisitor){
                ((TasmFileVisitor)visitor).visitEndContigBlock();
            }
        }else{
        //visit last read if there is any data left
            visitRead(visitor, currentSequenceName,
                    currentSequenceLeftEnd,
                    currentSequenceRightEnd, currentOffset,
                    currentBases);
            if(isTigrAssemblyVisitor){
                ((TasmFileVisitor)visitor).visitEndReadBlock();
            }
        }
       
        visitor.visitEndOfFile();
    }
    private static void visitRead(ContigFileVisitor visitor,
            String currentSequenceName, int currentSequenceLeftEnd,
            int currentSequenceRightEnd, int currentOffset, String currentBases) {
        Direction dir = currentSequenceLeftEnd > currentSequenceRightEnd
                               ? Direction.REVERSE
                               : Direction.FORWARD;
        final Range validRange;
        if(dir == Direction.REVERSE){
            validRange= Range.of(CoordinateSystem.RESIDUE_BASED, currentSequenceRightEnd, currentSequenceLeftEnd);
        }else{
            validRange= Range.of(CoordinateSystem.RESIDUE_BASED, currentSequenceLeftEnd, currentSequenceRightEnd);
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
