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
 * Created on Dec 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace.newbler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileWriter;
import org.jcvi.common.core.assembly.ace.AceTags;
import org.jcvi.common.core.assembly.ace.DefaultAceTagsFromAceFile;
import org.jcvi.common.core.assembly.ace.HiLowAceContigPhdDatastore;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.newbler.NewblerMappedAceContigUtil;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;

import org.jcvi.common.core.util.MultipleWrapper;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class RemoveReferenceFromNewblerMappedAce {
    private static final String DEFAULT_ACE_OUTPUT = "dereferenced.ace";
    public static void main(String[] args) throws IOException, DataStoreException{
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder(
                "ace", "path to ace file to convert")
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
            final File aceOut;
            if(commandLine.hasOption("out")){
                aceOut= new File(commandLine.getOptionValue("out"));
            }else{
                aceOut = new File(DEFAULT_ACE_OUTPUT);
            }
            File tempAce = new File(aceOut.getParentFile(),aceOut.getName()+".temp");
            FileOutputStream tempOut = new FileOutputStream(tempAce);
            FileOutputStream aceOutStream = new FileOutputStream(aceOut);
            
            AceContigDataStoreBuilder dataStoreBuilder = IndexedAceFileDataStore.createBuilder(aceFile);
            DefaultAceTagsFromAceFile.AceTagsFromFileBuilder aceTagsBuilder = DefaultAceTagsFromAceFile.createBuilder();
            AceFileParser.parse(aceFile, MultipleWrapper.createMultipleWrapper(AceFileVisitor.class,
                    dataStoreBuilder,aceTagsBuilder));
            int numberOfReads =0;
            int numberOfContigs=0;
            CloseableIterator<AceContig> iter = dataStoreBuilder.build().iterator();
            try{
            while(iter.hasNext()){
            	AceContig contig = iter.next();
                String contigId = contig.getId();
                PhdDataStore phdDataStore = HiLowAceContigPhdDatastore.create(aceFile, contigId);
                
                for(AceContig actualContig :NewblerMappedAceContigUtil.removeReferenceFrom(contig, phdDataStore)){
                    numberOfContigs++;
                    numberOfReads += actualContig.getNumberOfReads();
                    AceFileWriter.writeAceContig(actualContig, phdDataStore, tempOut);
                    
                }
            }
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter);
            }
            IOUtil.closeAndIgnoreErrors(tempOut);
            AceFileWriter.writeAceFileHeader(numberOfContigs, numberOfReads, aceOutStream);
            InputStream in = new FileInputStream(tempAce);
            IOUtil.copy(in, aceOutStream);
            AceTags aceTags = aceTagsBuilder.build();
            AceFileWriter.writeAceTags(aceTags, aceOutStream);
            IOUtil.closeAndIgnoreErrors(in,aceOutStream);
            
            
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
