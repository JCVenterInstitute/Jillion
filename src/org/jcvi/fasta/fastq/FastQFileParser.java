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
        boolean inSeqBlock=false, inQualBlock=false;
        boolean visitCurrentBlock=true;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            visitor.visitLine(line);
            Matcher beginSeqMatcher =BEGIN_SEQ_PATTERN.matcher(line);
            if(beginSeqMatcher.find()){
                if(inQualBlock){
                    visitor.visitEndBlock();
                }
                String id = beginSeqMatcher.group(1);
                String optionalComment =beginSeqMatcher.group(3);
                visitCurrentBlock = visitor.visitBeginBlock(id, optionalComment);
                inSeqBlock=true;
                inQualBlock=false;
            }
            else if(visitCurrentBlock){
                Matcher beginQualityMatcher =BEGIN_QUALITY_PATTERN.matcher(line);
                if(beginQualityMatcher.find()){               
                    inSeqBlock=false;
                    inQualBlock = true;
                }
                else{
                    if(inSeqBlock){
                        NucleotideEncodedGlyphs encodedNucleotides = new DefaultNucleotideEncodedGlyphs(NucleotideGlyph.getGlyphsFor(line));
                        visitor.visitNucleotides(encodedNucleotides);
                    }
                    else if(inQualBlock){
                        visitor.visitEncodedQualities(line);
                    }
                }
            }
        }
        if(visitCurrentBlock && inQualBlock){
            visitor.visitEndBlock();
        }
        visitor.visitEndOfFile();
    }
}
