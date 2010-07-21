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

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public class AceReadPositionPrinter {

    private static class Printer extends AbstractAceFileVisitor{
        private String currentContigId=null;
        private NucleotideEncodedGlyphs consensus;
        @Override
        protected void visitNewContig(String contigId, String consensus) {
            currentContigId = contigId;
            this.consensus = new DefaultNucleotideEncodedGlyphs(
                        ConsedUtil.convertAceGapsToContigGaps(consensus));
        }
        
        @Override
        protected void visitEndOfContig() {
           
            
        }
        
        @Override
        protected void visitAceRead(String readId, String validBasecalls,
                int offset, SequenceDirection dir, Range validRange, PhdInfo phdInfo) {
            Range gappedOneBasedRange = Range.buildRangeOfLength(offset, validBasecalls.length()).convertRange(CoordinateSystem.RESIDUE_BASED);
            int nonGapStartPosition = AssemblyUtil.getRightFlankingNonGapIndex(consensus, (int)gappedOneBasedRange.getStart());
            
            int nonGapEndPosition = AssemblyUtil.getLeftFlankingNonGapIndex(consensus, (int)gappedOneBasedRange.getEnd());
            Range unGappedOneBasesRange = Range.buildRange(
                    consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(nonGapStartPosition),
                    consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(nonGapEndPosition)).convertRange(CoordinateSystem.RESIDUE_BASED);
            System.out.printf("%s\t%s\t%d\t%d\t%d\t%d\t%s%n", currentContigId, readId, 
                    unGappedOneBasesRange.getLocalStart(), unGappedOneBasesRange.getLocalEnd(),
                    gappedOneBasedRange.getLocalStart(), gappedOneBasedRange.getLocalEnd(),
                    dir.getCode());
            
        }
    }
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(CommandLineUtils.createHelpOption());
        options.addOption(new CommandLineOptionBuilder("a","ace file" ,"acefile to parse")
                            .longName("ace")
                            .isRequired(true)
                            .build());
        
            try {
                CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
                File aceFile = new File(commandLine.getOptionValue("a"));
                System.out.println("contigID\treadID\tstart\tend\tgapped start\tgapped end\tdir");
                AceFileVisitor visitor = new Printer();
                AceFileParser.parseAceFile(aceFile, visitor);
            } catch (ParseException e) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "aceReadPositionPrinter -ace <ace file>", 
                        
                        "parse an ace file and give the coordinates and directions of each read for each contig."+
                        "  Data is printed to STDOUT (all positions are RESIDUE 1-based)",                        
                        options,
                        "Created by Danny Katzel");
            }
         

    }

}
