/*
 * Created on Dec 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.newbler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.ace.AceAssembly;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AceFileVisitor;
import org.jcvi.assembly.ace.AceFileWriter;
import org.jcvi.assembly.ace.DefaultAceAssembly;
import org.jcvi.assembly.ace.DefaultAceFileTagMap;
import org.jcvi.assembly.contig.qual.LowestFlankingQualityValueStrategy;
import org.jcvi.assembly.slice.LargeSliceMapFactory;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DefaultAceFileDataStore;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.sanger.phd.MemoryMappedPhdFileDataStore;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdParser;
import org.jcvi.trace.sanger.phd.newbler.NewblerMappedPhdBallFileDataStore;
import org.jcvi.util.DefaultMemoryMappedFileRange;
import org.jcvi.util.MultipleWrapper;

public class RemoveReferenceFromNewblerMappedAce {
    private static final String DEFAULT_ACE_OUTPUT = "dereferenced.ace";
    public static void main(String[] args) throws IOException, DataStoreException{
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder(
                "ace", "path to ace file to convert")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder(
                "phd", "path to phd ball file to convert")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder(
                "out", String.format("path to new ace file to be created (default : %s",DEFAULT_ACE_OUTPUT))
                .build());
        options.addOption(new CommandLineOptionBuilder(
                "h", "show this message")
                .longName("help")
                .build());
        try{
            CommandLine commandLine =CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            File aceFile = new File(commandLine.getOptionValue("ace"));
            File phdFile = new File(commandLine.getOptionValue("phd"));
            final File aceOut;
            if(commandLine.hasOption("out")){
                aceOut= new File(commandLine.getOptionValue("out"));
            }else{
                aceOut = new File(DEFAULT_ACE_OUTPUT);
            }
            DefaultAceFileDataStore dataStore = new DefaultAceFileDataStore();
            MemoryMappedPhdFileDataStore phdDataStore = new NewblerMappedPhdBallFileDataStore(phdFile, new DefaultMemoryMappedFileRange());
            DefaultAceFileTagMap aceTagMap = new DefaultAceFileTagMap();
            AceFileParser.parseAceFile(aceFile, MultipleWrapper.createMultipleWrapper(AceFileVisitor.class,
                    dataStore,aceTagMap));
            
            PhdParser.parsePhd(new FileInputStream(phdFile), phdDataStore);
            
            Map<String, AceContig> contigsWithActualConsensus = new LinkedHashMap<String, AceContig>();
            for(AceContig contig : dataStore){            
                for(AceContig actualContig :NewblerMappedAceContigUtil.removeReferenceFrom(contig, phdDataStore)){
                    contigsWithActualConsensus.put(actualContig.getId(), actualContig);
                }
                
            }
            DataStore<AceContig> contigsWithActualConsensusDataStore = new SimpleDataStore<AceContig>(contigsWithActualConsensus);
            
            DataStore<Phd> cachedPhdDataStore = CachedDataStore.createCachedDataStore(TraceDataStore.class,phdDataStore,1000);
           
            AceAssembly<AceContig> aceAssembly = new DefaultAceAssembly<AceContig>(
                    contigsWithActualConsensusDataStore, cachedPhdDataStore,
                    Arrays.asList(phdFile),aceTagMap);
            
            AceFileWriter.writeAceFile(aceAssembly, 
                    new LargeSliceMapFactory(new LowestFlankingQualityValueStrategy()),
                    new FileOutputStream(aceOut), true);
            
        }
        catch(ParseException e){
            printHelp(options);
            System.exit(1);
        }
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "removeReferenceFromNewblerMappedAce -ace <newbler mapped ace file> -phd <phd ball file> [-out <output dir>]", 
                "Mapped Assemblies created by Newbler have ace files that don't follow ace conventions:"+ 
                "For example, the reference is marked as the contig consensus and the actual assembly consensus is"+
                " only a specially named read. This program strips the reference and correctly marks the consensus,"+
                "in a newly created ace file",
                options,
                "Created by Danny Katzel");
    }
}
