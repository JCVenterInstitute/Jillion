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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.IllegalStateException;

import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;

public class FastQFileParser {
    private static final Pattern BEGIN_SEQ_PATTERN = Pattern.compile("^@(\\S+)(\\s+)?(.+$)?");
    private static final Pattern BEGIN_QUALITY_PATTERN = Pattern.compile("^\\+(.+$)?");
   
    public static void parse(File fastQFile, FastQFileVisitor visitor ) throws FileNotFoundException{
        InputStream in = new FileInputStream(fastQFile);
        try{
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    public static void parse(InputStream in, FastQFileVisitor visitor ){
        Scanner scanner = new Scanner(in).useDelimiter("\n");
        visitor.visitFile();
        boolean visitCurrentBlock;
        while(scanner.hasNextLine()){
            String seqLine = scanner.nextLine();
            String basecalls = scanner.nextLine();
            String qualLine = scanner.nextLine();
            String qualities = scanner.nextLine();
            visitor.visitLine(seqLine+"\n");
            Matcher beginSeqMatcher =BEGIN_SEQ_PATTERN.matcher(seqLine);
            if(!beginSeqMatcher.find()){
                throw new IllegalStateException("invalid fastq file, could not parse seq id from "+ seqLine);
            }
            String id = beginSeqMatcher.group(1);
            String optionalComment =beginSeqMatcher.group(3);
            visitCurrentBlock = visitor.visitBeginBlock(id, optionalComment);
            if(visitCurrentBlock){
                visitor.visitLine(basecalls+"\n");
                NucleotideEncodedGlyphs encodedNucleotides = new DefaultNucleotideEncodedGlyphs(NucleotideGlyph.getGlyphsFor(basecalls));
                visitor.visitNucleotides(encodedNucleotides);
                visitor.visitLine(qualLine+"\n");
                Matcher beginQualityMatcher =BEGIN_QUALITY_PATTERN.matcher(qualLine);
                if(!beginQualityMatcher.find()){ 
                    throw new IllegalStateException("invalid fastq file, could not parse qual id from "+ qualLine);
                }
                visitQualitiesLine(visitor, qualities, scanner);
                visitor.visitEncodedQualities(qualities);
            }else{
                visitor.visitLine(basecalls+"\n");
                visitor.visitLine(qualLine+"\n");
                visitQualitiesLine(visitor, qualities, scanner);
            }
            visitor.visitEndBlock();
        }
        visitor.visitEndOfFile();
    }
    /**
     * The final line in a fastQFile may or may not have 
     * a new line at the end, this could throw off visitors
     * that are counting bytes.  It's safer to always skip
     * the final newline since parsers shouldn't care
     * @param visitor
     * @param qualities
     * @param scanner
     */
    private static void visitQualitiesLine(FastQFileVisitor visitor,String qualities, Scanner scanner){
        if(scanner.hasNextLine()){
            visitor.visitLine(qualities+"\n");
        }else{
            visitor.visitLine(qualities);
        }
    }
}
