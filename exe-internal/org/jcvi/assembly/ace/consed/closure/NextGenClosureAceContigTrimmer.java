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

package org.jcvi.assembly.ace.consed.closure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.jcvi.Range;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigDataStore;
import org.jcvi.assembly.ace.AceContigTrimmer;
import org.jcvi.assembly.ace.AceFileWriter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.trim.ElviraSangerContigEndTrimmer;
import org.jcvi.assembly.trim.PlacedReadTrimmer;
import org.jcvi.assembly.trim.TrimmerException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStore;
/**
 * @author dkatzel
 *
 *
 */
public class NextGenClosureAceContigTrimmer extends AceContigTrimmer{

    public static final int DEFAULT_MIN_SANGER_END_CLONE_CVG=2;
    public static final int DEFAULT_MIN_BI_DIRECTIONAL_END_COVERAGE =5;
    public static final int DEFAULT_IGNORE_END_CVG_THRESHOLD=10;
    /**
     * @param trimmers
     */
    public NextGenClosureAceContigTrimmer(int minSangerEndCloneCoverage,int minBiDirectionalEndCoverage, int ignoreThresholdEndCoverage){
        super( Arrays.<PlacedReadTrimmer<AcePlacedRead, AceContig>>asList(
                    new ElviraSangerContigEndTrimmer<AcePlacedRead, AceContig>(minSangerEndCloneCoverage,minBiDirectionalEndCoverage, ignoreThresholdEndCoverage)));
          
    }

    @Override
    protected String createNewContigId(String oldContigId, NucleotideEncodedGlyphs oldConsensus, Range newContigRange){
       String id= super.createNewContigId(oldContigId, oldConsensus, newContigRange);
       Pattern pattern = Pattern.compile("^(\\S+)_(\\d+)_(\\d+)$");
       Matcher trimmedSplitmatcher = pattern.matcher(id);
       if(!trimmedSplitmatcher.matches()){
           return id;
       }
       String untrimmedId = trimmedSplitmatcher.group(1);
       
       Matcher _0xMatcher = pattern.matcher(untrimmedId);
       if(!_0xMatcher.matches()){
           return id;
       }
       int trimmedLeft = Integer.parseInt(trimmedSplitmatcher.group(2));
       int trimmedRight = Integer.parseInt(trimmedSplitmatcher.group(3));
       String originalId = _0xMatcher.group(1);
       int _0xLeft = Integer.parseInt(_0xMatcher.group(2));
       return String.format("%s_%d_%d",originalId,_0xLeft +trimmedLeft-1, _0xLeft+trimmedRight);
    }
    
    public static void main(String args[]) throws IOException, TrimmerException, DataStoreException{
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("ace", "path to ace file")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("phd", "path to phd file")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("out", "path to new ace file")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("min_sanger", "min sanger end coveage default =" + DEFAULT_MIN_SANGER_END_CLONE_CVG)
                    .build());
        options.addOption(new CommandLineOptionBuilder("min_biDriection", "min bi directional end coveage default =" + DEFAULT_MIN_BI_DIRECTIONAL_END_COVERAGE)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("ignore_threshold", "min end coveage threshold to stop trying to trim default =" + DEFAULT_IGNORE_END_CVG_THRESHOLD)
        .build());
        
        CommandLine commandLine;
        PhdDataStore phdDataStore=null;
        AceContigDataStore datastore= null;
        try {
            commandLine = CommandLineUtils.parseCommandLine(options, args);
        
        
                int minSangerEndCloneCoverage = commandLine.hasOption("min_sanger")?
                                            Integer.parseInt(commandLine.getOptionValue("min_sanger")):
                                                DEFAULT_MIN_SANGER_END_CLONE_CVG;
                
                
                int minBiDirectionalEndCoverage = commandLine.hasOption("min_biDriection")?
                        Integer.parseInt(commandLine.getOptionValue("min_biDriection")):
                            DEFAULT_MIN_BI_DIRECTIONAL_END_COVERAGE;
        
                int ignoreThresholdEndCoverage = commandLine.hasOption("ignore_threshold")?
                        Integer.parseInt(commandLine.getOptionValue("ignore_threshold")):
                            DEFAULT_IGNORE_END_CVG_THRESHOLD;
        
            AceContigTrimmer trimmer = new NextGenClosureAceContigTrimmer(minSangerEndCloneCoverage, minBiDirectionalEndCoverage, ignoreThresholdEndCoverage);
            File aceFile = new File(commandLine.getOptionValue("ace"));
            File phdFile = new File(commandLine.getOptionValue("phd"));
            phdDataStore=new DefaultPhdFileDataStore(phdFile);
            datastore = new MemoryMappedAceFileDataStore(aceFile);
            File tempFile = File.createTempFile("nextGenClosureAceTrimmer", ".ace");
            tempFile.deleteOnExit();
            OutputStream tempOut = new FileOutputStream(tempFile);
            int numberOfContigs=0;
            int numberOfTotalReads=0;
            for(AceContig contig : datastore){
                AceContig trimmedAceContig= trimmer.trimContig(contig);
                if(trimmedAceContig!=null){
                    numberOfContigs++;
                    numberOfTotalReads+=trimmedAceContig.getNumberOfReads();
                    AceFileWriter.writeAceFile(trimmedAceContig, phdDataStore, tempOut);
                }

            }
            IOUtil.closeAndIgnoreErrors(tempOut);
            OutputStream masterAceOut = new FileOutputStream(new File(commandLine.getOptionValue("out")));
            masterAceOut.write(String.format("AS %d %d%n", numberOfContigs, numberOfTotalReads).getBytes());
            InputStream tempInput = new FileInputStream(tempFile);
            IOUtils.copy(tempInput, masterAceOut);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
        }finally{
            IOUtil.closeAndIgnoreErrors(phdDataStore,datastore);
        }
    }
    
    private static void printHelp(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                "NextGenClosureAceContigTrimmer -ace <ace> -phd <phd> -o <output ace> [OPTIONS]",
                "Trim an Ace contig using closure next-gen coverage rules",
                
                 options,
                 "Created by Danny Katzel");
        System.exit(1);
    }
}
