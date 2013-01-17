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
package org.jcvi.jillion.trace.sanger.phd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code PhdParser} parses .phd files (or phd.ball files).
 * @author dkatzel
 *
 *
 */
public final class PhdParser {
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
    /**
     * private constructor.
     */
    private PhdParser(){}
    /**
     * Parse the given .phd file and call the visitXXX methods on the given
     * {@link PhdFileVisitor}.
     * @param phdFile the .phd file to parse.
     * @param visitor the {@link PhdFileVisitor} to visit during
     * parsing.
     * @throws FileNotFoundException if the phdFile given does not exist.
     */
    public static void parsePhd(File phdFile, PhdFileVisitor visitor) throws FileNotFoundException{
          InputStream in = new FileInputStream(phdFile);
            try{
                parsePhd(in,visitor);
            }finally{
                IOUtil.closeAndIgnoreErrors(in);
            }
    }
    /**
     * Parse the given {@link InputStream} assuming it contains .phd data. 
     * PLEASE NOTE:  it is the caller's responsibility to close the inputStream
     * after parsing.
     * @param in the inputStram of .phd data.
     * @param visitor the {@link PhdFileVisitor} to visit during
     * parsing.
     */
    public static void parsePhd(InputStream in, PhdFileVisitor visitor){
        if(in ==null){
            throw new NullPointerException("input stream can not be null");
        }
        TextLineParser parser;
        try {
            parser = new TextLineParser(new BufferedInputStream(in));
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new IllegalStateException("error reading file");
            
        }
        visitor.visitFile();
        Properties currentComments=null;
        boolean inComments=false;
        boolean keepParsing=true;
        boolean parseCurrentPhd=true;
        boolean firstRecord=true;
        while(keepParsing && parser.hasNextLine()){
            String line;
            try {
            	line = parser.nextLine();
            } catch (IOException e) {
                throw new IllegalStateException("error reading file");
            }
            
            Matcher beginSeqMatcher = BEGIN_SEQUENCE_PATTERN.matcher(line);
            if(beginSeqMatcher.find()){
                if(!firstRecord){
                    keepParsing = visitor.visitEndPhd();                    
                }
                if(keepParsing){
                	visitor.visitLine(line);
                    firstRecord=false;
                    String id = beginSeqMatcher.group(1);
                    parseCurrentPhd = visitor.visitBeginPhd(id);
                    if(parseCurrentPhd){
                        visitor.visitBeginSequence(id);
                    }
                }
            }else{
            	visitor.visitLine(line);
            	if(line.startsWith(BEGIN_COMMENT)){
                    inComments=true;
                    currentComments = new Properties();
                }
                else if(line.startsWith(END_COMMENT)){
                    inComments=false;
                    if(parseCurrentPhd){
                        visitor.visitComment(currentComments);
                    }
                }
                else if(line.startsWith(BEGIN_DNA)){   
                    if(parseCurrentPhd){
                        visitor.visitBeginDna();
                    }
                }
                else if(line.startsWith(END_DNA)){
                    if(parseCurrentPhd){
                        visitor.visitEndDna();
                    }
                }
                else if(line.startsWith(END_SEQUENCE)){
                    if(parseCurrentPhd){
                        visitor.visitEndSequence();
                    }
                }
                
                else if(line.startsWith(END_ITEM)){
                    if(parseCurrentPhd){
                        visitor.visitEndTag();
                    }
                }
                else{
                    Matcher infoPattern = CALLED_INFO_PATTERN.matcher(line);
                
                    if(infoPattern.find()){
                        if(parseCurrentPhd){
                            visitor.visitBasecall(
                                Nucleotide.parse(infoPattern.group(1).charAt(0)),
                                PhredQuality.valueOf(Byte.parseByte(infoPattern.group(2))),
                                Integer.parseInt(infoPattern.group(3)));
                        }
                    }
                    else{
                        Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                        if(inComments && commentMatcher.find()){
                            if(parseCurrentPhd){
                                currentComments.put(commentMatcher.group(1), commentMatcher.group(2));
                            }
                        }
                        else{
                            Matcher tagMatcher = BEGIN_TAG_PATTERN.matcher(line);
                            if(tagMatcher.find() && parseCurrentPhd){
                                    visitor.visitBeginTag(tagMatcher.group(1));
                            }
                        }
                    }
                    
                }
            }
            }
            
        visitor.visitEndPhd();
        visitor.visitEndOfFile();
    }
}
