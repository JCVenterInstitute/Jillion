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
 * Created on Dec 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.Range;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideEncodedSequenceFastaRecord;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.SequenceDirection;

public class Ace2Fasta {


    public static void main(String[] args) throws IOException{
        Options options = new Options();
        
        options.addOption(new CommandLineOptionBuilder("ace", "path to ace file")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("out", "output path to fasta file to write")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("g", "print gapped consensus (default is ungapped)")
                            .longName("gapped")
                            .isFlag(true)
                            .build());
        options.addOption(CommandLineUtils.createHelpOption());
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
          
            final File aceIn = new File(commandLine.getOptionValue("ace"));
    
            final OutputStream fastaOut = new FileOutputStream(commandLine.getOptionValue("out"));
            final boolean gapped = commandLine.hasOption("g");
            //consed allows users to rename contigs, but instead of changing
            //the CO record in the ace, the new name is stored as a comment at
            //the end of the file.  contigIdMap parses those comments 
            //to get the most current names
            //for those the contigs, any contig without those comments
            //will not exist in the map
            final Map<String, String> contigIdMap = getContigIdMap(aceIn);
           
            AceFileVisitor fastaVisitor = new AbstractAceFileVisitor() {
                
                @Override
                protected void visitNewContig(String contigId, String consensusString) {
                    List<NucleotideGlyph>consensus = NucleotideGlyph.getGlyphsFor(consensusString);
                    if(!gapped){
                        consensus = NucleotideGlyph.convertToUngapped(consensus);
                    }
                    String id = contigIdMap.containsKey(contigId)?
                                        contigIdMap.get(contigId)
                                        : contigId;
                   
                    String comment = aceIn.getName()+" (whole contig)";
                    DefaultNucleotideEncodedSequenceFastaRecord fasta = new DefaultNucleotideEncodedSequenceFastaRecord(
                                                                    id,
                                                                    comment,
                                                                    consensus);
                    try {
                        fastaOut.write(fasta.toFormattedString().toString().getBytes());
                    } catch (IOException e) {
                       throw new RuntimeException("error writing Fasta Record for " + id,e);
                    }
                    
                    
                }
                
                @Override
                public boolean visitEndOfContig() {
                    return true;
                }
                
                @Override
                protected void visitAceRead(String readId, String validBasecalls,
                        int offset, SequenceDirection dir, Range validRange, PhdInfo phdInfo,int ungappedFullLength) {
                }
            };
            AceFileParser.parseAceFile(aceIn, fastaVisitor);
            IOUtil.closeAndIgnoreErrors(fastaOut);
        } catch (ParseException e1) {
            printHelp(options);
            System.exit(1);
        }
    }

    private static Map<String, String> getContigIdMap(final File aceIn)
            throws IOException {
        final Map<String, String> contigIdMap = new HashMap<String, String>();
        DefaultAceFileTagMap tagMap = new DefaultAceFileTagMap(aceIn);
        for(ConsensusAceTag consensusTag: tagMap.getConsensusTags()){
            String originalId = consensusTag.getId();
            if(ConsedUtil.isContigRename(consensusTag)){
                contigIdMap.put(originalId, ConsedUtil.getRenamedContigId(consensusTag));
            }
        }
        return contigIdMap;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ace2Fasta -ace <ace file> -out <output fasta>", 
                
                "create a fasta file from the contig consensus(es) of the given ace file."+
                "  The Ids used in the fasta are usually the contig ids except when "+
                "a user added a consed 'contigName' comment (renamed a contig inside consed), then the rename is used",
                options,
                "Created by Danny Katzel");
    }
}
