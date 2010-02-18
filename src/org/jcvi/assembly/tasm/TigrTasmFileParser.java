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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.tasm;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;
/**
 * Parse a TIGR .tasm file and convert it into a {@link Contig}.
 * 
 * @author dkatzel
 *
 *
 */
public class TigrTasmFileParser {

    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+)");
    
    public Contig<PlacedRead> parseContigFrom(InputStream tasmFile){
        Scanner scanner = new Scanner(tasmFile);
        return generateContigBuilder(scanner).build();
    }

    private DefaultContig.Builder generateContigBuilder(Scanner scanner) {
        DefaultContig.Builder contigBuilder =null;
        TasmReadBuilder readBuilder =null;
        NucleotideEncodedGlyphs consensus=null;
        String contigId =null;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = KEY_VALUE_PATTERN.matcher(line);
           
            if(matcher.find()){
                String key = matcher.group(1);
                String value = matcher.group(2);
                if(readBuilder ==null){
                    if(key.equals("asmbl_id")){
                        contigId = value;
                    }
                    else if(key.equals("lsequence")){
                        consensus = new DefaultNucleotideEncodedGlyphs(value);
                    }
                }else{
                    handleWithReadBuilder(readBuilder, key, value);
                }
               
            }
            else if(isEndOfRecord(line)){
                if(readBuilder ==null){
                    contigBuilder = new DefaultContig.Builder(contigId, consensus);
                    readBuilder = new TasmReadBuilder(contigBuilder);
                }
                else{
                   readBuilder.addRead();
                   readBuilder = new TasmReadBuilder(contigBuilder);
                }
            }
        }
        readBuilder.addRead();
        return contigBuilder;
    }

    private boolean isEndOfRecord(String line) {
        return line.trim().isEmpty();
    }

    private void handleWithReadBuilder(TasmReadBuilder readBuilder, String key,
            String value) {
        if(key.equals("lsequence")){
            readBuilder.bases(value);
        }
        else if(key.equals("seq_lend")){
            readBuilder.sequenceLeft(Integer.parseInt(value));
        }
        else if(key.equals("seq_rend")){
            readBuilder.sequenceRight(Integer.parseInt(value));
        }
        else if(key.equals("offset")){
            readBuilder.offset(Integer.parseInt(value));
        }
        else if(key.equals("seq_name")){
            readBuilder.name(value);
        }
    }
    
    private static class TasmReadBuilder{
        private final DefaultContig.Builder contigBuilder;
        private String sequenceName;
        private int sequenceLeftEnd=-1;
        private int sequenceRightEnd=-1;
        private int offset;
        private String bases;
        
        TasmReadBuilder(DefaultContig.Builder builder){
            contigBuilder = builder;
        }
        
        TasmReadBuilder bases(String bases){
            this.bases = bases;
            return this;
        }
        
        TasmReadBuilder name(String name){
            this.sequenceName = name;
            return this;
        }
        TasmReadBuilder sequenceLeft(int left){
            this.sequenceLeftEnd = left;
            return this;
        }
        TasmReadBuilder sequenceRight(int right){
            this.sequenceRightEnd = right;
            return this;
        }
        TasmReadBuilder offset(int offset){
            this.offset = offset;
            return this;
        }
        
        void addRead(){
            SequenceDirection dir = getSequenceDirection();
            
            Range validRange = getValidRange(dir);
            //create new read
            contigBuilder.addRead(sequenceName, offset, validRange, bases, dir);
        }

        private Range getValidRange(SequenceDirection dir) {
           
            if(dir == SequenceDirection.REVERSE){
                return Range.buildRange(sequenceRightEnd-1, sequenceLeftEnd-1);
            }
           return Range.buildRange(sequenceLeftEnd-1, sequenceRightEnd-1);
        }

        private SequenceDirection getSequenceDirection() {
            if(sequenceLeftEnd > sequenceRightEnd){
                return SequenceDirection.REVERSE;
            }
            return SequenceDirection.FORWARD;

        }
        
    }
   
}
