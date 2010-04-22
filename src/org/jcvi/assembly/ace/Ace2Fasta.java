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
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
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
                            .build());
        options.addOption(CommandLineUtils.createHelpOption());
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            final File aceIn = new File(commandLine.getOptionValue("ace"));
    
            final OutputStream fastaOut = new FileOutputStream(commandLine.getOptionValue("out"));
            final boolean gapped = commandLine.hasOption("g");
           
            final Map<String, String> contigIdMap = new HashMap<String, String>();
            DefaultAceFileTagMap tagMap = new DefaultAceFileTagMap();
            AceFileParser.parseAceFile(aceIn, tagMap);
            for(ConsensusAceTag consensusTag: tagMap.getConsensusTags()){
                String originalId = consensusTag.getId();
                if(ConsedUtil.isContigRename(consensusTag)){
                    contigIdMap.put(originalId, ConsedUtil.getRenamedContigId(consensusTag));
                }
            }
           
            AceFileVisitor fastaVisitor = new AbstractAceFileVisitor() {
                
                @Override
                protected void visitNewContig(String contigId, String consensusString) {
                    List<NucleotideGlyph>consensus = NucleotideGlyph.getGlyphsFor(consensusString);
                    if(!gapped){
                        consensus = NucleotideGlyph.convertToUngapped(consensus);
                    }
                    String id = contigIdMap.get(contigId);
                    if(id==null){
                        id =contigId;
                    }
                    String comment = aceIn.getName()+" (whole contig)";
                    DefaultEncodedNucleotideFastaRecord fasta =
                        new DefaultEncodedNucleotideFastaRecord(id,comment,
                    NucleotideGlyph.convertToString(consensus));
                        try {
                            fastaOut.write(fasta.getStringRecord().toString().getBytes());
                        } catch (IOException e) {
                           throw new RuntimeException(e);
                        }
                    
                    
                }
                
                @Override
                protected void visitEndOfContig() {
                    
                }
                
                @Override
                protected void visitAceRead(String readId, String validBasecalls,
                        int offset, SequenceDirection dir, Range validRange, PhdInfo phdInfo) {
                    
                }
            };
            AceFileParser.parseAceFile(aceIn, fastaVisitor);
            IOUtil.closeAndIgnoreErrors(fastaOut);
        } catch (ParseException e1) {
            printHelp(options);
            System.exit(1);
        }
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
