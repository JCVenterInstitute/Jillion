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
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;

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
            File aceIn = new File(commandLine.getOptionValue("ace"));
    
            final OutputStream fastaOut = new FileOutputStream(commandLine.getOptionValue("out"));
            final boolean gapped = commandLine.hasOption("g");
            AceFileVisitor fastaVisitor = new AbstractAceFileDataStore() {
                
                @Override
                protected void visitContig(AceContig contig) {
                    List<NucleotideGlyph>consensus = contig.getConsensus().decode();
                    if(!gapped){
                        consensus = NucleotideGlyph.convertToUngapped(consensus);
                    }
                    DefaultEncodedNucleotideFastaRecord fasta =
                        new DefaultEncodedNucleotideFastaRecord(contig.getId(),
                    NucleotideGlyph.convertToString(consensus));
                        try {
                            fastaOut.write(fasta.getStringRecord().toString().getBytes());
                        } catch (IOException e) {
                           throw new RuntimeException(e);
                        }
                    
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
                
                "create a fasta file from the contig consensus(es) of the given ace file",
                options,
                "Created by Danny Katzel");
    }
}
