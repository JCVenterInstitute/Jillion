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
 * Created on Feb 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.contigChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.ContigCheckerXMLWriter;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AceFileVisitor;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.analysis.ContigChecker;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.ContigDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.trace.sanger.phd.H2PhdQualityDataStore;
import org.jcvi.util.DefaultMemoryMappedFileRange;

public class ContigCheckNextGenAceFile {
    private static final int DEFAULT_SEQ_DIR_THRESHOLD = 10;
    private static final int DEFAULT_LOW_COVERAGE_THRESHOLD = 10;
    private static final int DEFAULT_HIGH_COVERAGE_THRESHOLD = 2000;
    private static final String DEFAULT_PREFIX = "contigChecker";
    /**
     * @param args
     * @throws DataStoreException 
     * @throws IOException 
     */
    public static void main(String[] args) throws DataStoreException, IOException {
        Options options = new Options();
        options.addOption(CommandLineUtils.createHelpOption());
        options.addOption(new CommandLineOptionBuilder("ace", "ace file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("phd", "phdball file")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("o", "output directory")
                        .longName("outputDir")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("prefix", 
                String.format("prefix for all generated files (Default : %s)", DEFAULT_PREFIX))
                            .build());
        options.addOption(new CommandLineOptionBuilder("lowCoverage", 
                String.format("low coverage threshold (Default : %d)", DEFAULT_LOW_COVERAGE_THRESHOLD))
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("highCoverage", 
                String.format("high coverage threshold (Default : %d)", DEFAULT_HIGH_COVERAGE_THRESHOLD))
                            .build());
        options.addOption(new CommandLineOptionBuilder("dirTheshold", 
                String.format("sequence direction %% difference threshold (Default : %d)", DEFAULT_SEQ_DIR_THRESHOLD))
                            .build());
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            File aceFile = new File(commandLine.getOptionValue("ace"));
            File phdBallFile = new File(commandLine.getOptionValue("phd"));
            String outputBasePath = commandLine.getOptionValue("o");
            String prefix =commandLine.hasOption("prefix")?
                            commandLine.getOptionValue("prefix"):
                                DEFAULT_PREFIX;
            
            final int lowSequenceCoverageThreshold= commandLine.hasOption("lowCoverage")?
                                    Integer.parseInt(commandLine.getOptionValue("lowCoverage")):
                                        DEFAULT_LOW_COVERAGE_THRESHOLD;
            final int highSequenceCoverageThreshold=commandLine.hasOption("highCoverage")?
                                    Integer.parseInt(commandLine.getOptionValue("highCoverage")):
                                        DEFAULT_HIGH_COVERAGE_THRESHOLD;
            final int percentReadDirectionDifferenceTheshold=commandLine.hasOption("dirTheshold")?
                                    Integer.parseInt(commandLine.getOptionValue("dirTheshold")):
                                        DEFAULT_SEQ_DIR_THRESHOLD;
                                    
            ContigDataStore<AcePlacedRead, AceContig> contigDataStore = new MemoryMappedAceFileDataStore(aceFile, new DefaultMemoryMappedFileRange());
            QualityDataStore qualityDataStore = new H2PhdQualityDataStore(phdBallFile, new H2QualityDataStore());
           
            
            
            for(Iterator<String>contigIdIterator=contigDataStore.getIds(); contigIdIterator.hasNext();){
                
                final String id = contigIdIterator.next();
                System.out.println(id);
                AceContig contig =contigDataStore.get(id) ;
                
                ContigCheckerStruct<AcePlacedRead> struct = new ContigCheckerStruct<AcePlacedRead>(contig, 
                        qualityDataStore);
                ContigChecker<AcePlacedRead> contigChecker = new NextGenContigChecker(struct, percentReadDirectionDifferenceTheshold,
                        lowSequenceCoverageThreshold, highSequenceCoverageThreshold);
                contigChecker.run();
                ContigCheckerUtil.writeContigCheckerResults(outputBasePath, prefix,id,struct, contigChecker,false);
                final String contigPrefix = outputBasePath+"/"+prefix+"."+id;
                OutputStream xmlOut = new FileOutputStream(contigPrefix + ".contigChecker.xml");
                ContigCheckerXMLWriter<AcePlacedRead> xmlWriter = new ContigCheckerXMLWriter<AcePlacedRead>(xmlOut);
                xmlWriter.write(struct);
                xmlWriter.close();  
                
            }
        
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
       

    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "nextGenContigChecker -ace <ace file> -phd <phd file> -o <output dir> [OPTIONS]", 
                
                "contig check next generation ace file assembly (only does coverage checking)",
                options,
                "Created by Danny Katzel");
    }
}

