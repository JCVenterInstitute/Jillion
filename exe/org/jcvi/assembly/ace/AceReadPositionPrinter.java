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
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public class AceReadPositionPrinter {

    private static class Printer extends AbstractAceFileVisitor{
        private String currentContigId=null;
        private NucleotideSequence consensus;
        @Override
        protected void visitNewContig(String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complimented) {
            currentContigId = contigId;
            this.consensus = consensus;
        }
        
        @Override
        public boolean visitEndOfContig() {
           return true;
            
        }
        
        @Override
        protected void visitAceRead(String readId, String validBasecalls,
                int offset, Direction dir, Range validRange, PhdInfo phdInfo,
                int ungappedFullLength) {
            Range gappedOneBasedRange = Range.buildRangeOfLength(offset, validBasecalls.length());
            int nonGapStartPosition = AssemblyUtil.getRightFlankingNonGapIndex(consensus, (int)gappedOneBasedRange.getStart());
            
            int nonGapEndPosition = AssemblyUtil.getLeftFlankingNonGapIndex(consensus, (int)gappedOneBasedRange.getEnd());
            Range unGappedOneBasesRange = Range.buildRange(
                    consensus.getUngappedOffsetFor(nonGapStartPosition),
                    consensus.getUngappedOffsetFor(nonGapEndPosition));
            System.out.printf("%s\t%s\t%d\t%d\t%d\t%d\t%s%n", currentContigId, readId, 
                    unGappedOneBasesRange.getStart(CoordinateSystem.RESIDUE_BASED), unGappedOneBasesRange.getEnd(CoordinateSystem.RESIDUE_BASED),
                    gappedOneBasedRange.getStart(CoordinateSystem.RESIDUE_BASED), gappedOneBasedRange.getEnd(CoordinateSystem.RESIDUE_BASED),
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
