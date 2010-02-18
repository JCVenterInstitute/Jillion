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
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffFileVisitor;

public class PhdParser {
    private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";

    private static final Pattern COMMENT_PATTERN = Pattern.compile("^\\s*(\\w+):\\s+(.*?)$");
    private static final Pattern CALLED_INFO_PATTERN = Pattern.compile("^\\s*(\\w)\\s+(\\d+)\\s+(\\d+)");
    private static final Pattern BEGIN_SEQUENCE_PATTERN = Pattern.compile("BEGIN_SEQUENCE\\s+(\\S+)");
    private static final Pattern BEGIN_TAG_PATTERN = Pattern.compile("^(\\S+)\\{\\s*$");
    private static final String END_ITEM = "}";
    
    
    public static void parsePhd(File phdFile, PhdFileVisitor visitor) throws FileNotFoundException{
          InputStream in = new FileInputStream(phdFile);
            try{
                parsePhd(in,visitor);
            }finally{
                IOUtil.closeAndIgnoreErrors(in);
            }
    }
    public static void parsePhd(InputStream in, PhdFileVisitor visitor){
        Scanner scanner = new Scanner(in).useDelimiter("\n");
        visitor.visitFile();
        Properties currentComments=null;
        boolean inComments=false;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            visitor.visitLine(line+"\n");
            Matcher beginSeqMatcher = BEGIN_SEQUENCE_PATTERN.matcher(line);
            if(beginSeqMatcher.find()){
                String id = beginSeqMatcher.group(1);
                visitor.visitBeginSequence(id);
            }
            else if(line.startsWith(BEGIN_COMMENT)){
                inComments=true;
                currentComments = new Properties();
            }
            else if(line.startsWith(END_COMMENT)){
                inComments=false;
                visitor.visitComment(currentComments);
            }
            else if(line.startsWith(BEGIN_DNA)){                
                visitor.visitBeginDna();
            }
            else if(line.startsWith(END_DNA)){
                visitor.visitEndDna();
            }
            else if(line.startsWith(END_SEQUENCE)){
                visitor.visitEndSequence();
            }
            
            else if(line.startsWith(END_ITEM)){
                visitor.visitEndTag();
            }
            else{
                Matcher infoPattern = CALLED_INFO_PATTERN.matcher(line);
            
                if(infoPattern.find()){
                    visitor.visitBasecall(
                            NucleotideGlyph.getGlyphFor(infoPattern.group(1).charAt(0)),
                            PhredQuality.valueOf(Byte.parseByte(infoPattern.group(2))),
                            Integer.parseInt(infoPattern.group(3)));
                }
                else{
                    Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                    if(inComments && commentMatcher.find()){
                        currentComments.put(commentMatcher.group(1), commentMatcher.group(2));
                    }
                    else{
                        Matcher tagMatcher = BEGIN_TAG_PATTERN.matcher(line);
                        if(tagMatcher.find()){
                            visitor.visitBeginTag(tagMatcher.group(1));
                        }
                    }
                }
                
            }
        }
        visitor.visitEndOfFile();
    }
}
