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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.jcvi.assembly.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2SffCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.cas.read.ReadCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.util.MathUtil;
import org.jcvi.util.MultipleWrapper;
import org.joda.time.Period;

/**
 * @author dkatzel
 *
 *
 */
public class CasProfileMatrix extends AbstractCasFileContigVisitor{
    /**
     * bases to use in the order required by the profile format.
     */
    private static final List<NucleotideGlyph> BASES_TO_USE = Arrays.asList(
            NucleotideGlyph.Amino,NucleotideGlyph.Thymine, NucleotideGlyph.Guanine,  NucleotideGlyph.Cytosine, NucleotideGlyph.Gap);
    
    
    private static final String DEFAULT_PREFIX = "cas2profile";
    private static final int DEFAULT_CACHE_SIZE = 2000;
    
    private static final File DEFAULT_TEMP_DIR = new File("/usr/local/scratch");
    
    private final Map<String, Map<Long, Map<NucleotideGlyph, Float>>> snpMap = new HashMap<String, Map<Long,Map<NucleotideGlyph,Float>>>();
    /**
     * @param referenceIdLookup
     * @param readIdLookup
     * @param gappedReferenceMap
     * @param nucleotideDataStore
     * @param trimDataStore
     */
    public CasProfileMatrix(CasIdLookup referenceIdLookup,
            CasIdLookup readIdLookup, CasGappedReferenceMap gappedReferenceMap,
            DataStore<NucleotideEncodedGlyphs> nucleotideDataStore) {
        super(referenceIdLookup, readIdLookup, gappedReferenceMap, nucleotideDataStore,
                TrimDataStoreUtil.EMPTY_DATASTORE);

    }
    /**
    * {@inheritDoc}
    */
    @Override
    protected void visitPlacedRead(long referenceId, CasPlacedRead casPlacedRead) {
        String contigName = this.getReferenceIdLookup().getLookupIdFor(referenceId);
        if(!snpMap.containsKey(contigName)){
            snpMap.put(contigName, new HashMap<Long, Map<NucleotideGlyph,Float>>());
        }
        Map<Long, Map<NucleotideGlyph,Float>> contigMap = snpMap.get(contigName);
        long i = casPlacedRead.getStart();
        for(NucleotideGlyph basecall : casPlacedRead.getEncodedGlyphs().decode()){
            Long referenceCoordinate = Long.valueOf(i);
            final Map<NucleotideGlyph,Float> profile;
            if(!contigMap.containsKey(referenceCoordinate)){
                profile = new EnumMap<NucleotideGlyph, Float>(NucleotideGlyph.class);
                for(NucleotideGlyph g: BASES_TO_USE){
                    profile.put(g, Float.valueOf(0));
                }
                contigMap.put(referenceCoordinate, profile);
            }else{
                profile = contigMap.get(referenceCoordinate);
            }
            if(profile.containsKey(basecall)){
                profile.put(basecall, profile.get(basecall) +1);
            }else if(basecall.isAmbiguity()){
                //give fractional amount to ambiguities
                Collection<NucleotideGlyph> nucleotides=basecall.getNucleotides();
                float increment = 1F/nucleotides.size();
                for(NucleotideGlyph g : nucleotides){
                    profile.put(g, profile.get(g) +increment);
                }
            }
            i++;
        }
        
    }
    public Map<String, Map<Long, Map<NucleotideGlyph, Float>>> getSnpMap() {
        return snpMap;
    }
    
    public static void main(String[] args) throws Throwable{
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("cas", "cas file")
                            .isRequired(true)
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("o", "output directory")
                            .longName("outputDir")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("tempDir", "temp directory")
                                                    .build());
        options.addOption(new CommandLineOptionBuilder("prefix", "file prefix for all generated files ( default "+DEFAULT_PREFIX +" )")                                
                                .build());
            CommandLine commandLine;
            PrintWriter logOut=null;
            PrintWriter currentWriter=null;
            ReadWriteDirectoryFileServer outputDir=null;
            long startTime = System.currentTimeMillis();
            try {
                commandLine = CommandLineUtils.parseCommandLine(options, args);
            
                int cacheSize = commandLine.hasOption("s")? Integer.parseInt(commandLine.getOptionValue("s")) : DEFAULT_CACHE_SIZE;
                
                File casFile = new File(commandLine.getOptionValue("cas"));
                File casWorkingDirectory = casFile.getParentFile();
                outputDir = 
                        DirectoryFileServer.createReadWriteDirectoryFileServer(commandLine.getOptionValue("o"));
                
                String prefix = commandLine.hasOption("prefix")? commandLine.getOptionValue("prefix"): DEFAULT_PREFIX;
                logOut = new PrintWriter(new FileOutputStream(outputDir.createNewFile(prefix+".log")),true);
                final ReadWriteDirectoryFileServer tempDir;
                if(!commandLine.hasOption("tempDir")){
                    //default to scratch
                    tempDir=DirectoryFileServer.createTemporaryDirectoryFileServer(DEFAULT_TEMP_DIR);
                }else{
                    File t =new File(commandLine.getOptionValue("tempDir"));
                    IOUtil.mkdirs(t);
                    tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer(t);
                }
                CasDataStoreFactory casDataStoreFactory = new MultiCasDataStoreFactory(
                        new H2SffCasDataStoreFactory(casWorkingDirectory,tempDir,EmptyDataStoreFilter.INSTANCE),               
                        new H2FastQCasDataStoreFactory(casWorkingDirectory,null,tempDir.getRootDir()),
                        new FastaCasDataStoreFactory(casWorkingDirectory,cacheSize)        
                );
                AbstractDefaultCasFileLookup readIdLookup = new DefaultReadCasFileLookup(EmptyCasTrimMap.getInstance(),casWorkingDirectory);
                AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup(casWorkingDirectory);
                AbstractCasFileNucleotideDataStore nucleotideDataStore = new ReadCasFileNucleotideDataStore(casDataStoreFactory);
                AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(casDataStoreFactory);
               
                CasParser.parseCas(casFile, MultipleWrapper.createMultipleWrapper(
                        CasFileVisitor.class, 
                        readIdLookup, referenceIdLookup,nucleotideDataStore,referenceNucleotideDataStore));
         
                DefaultCasGappedReferenceMap gappedReferenceMap = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup);
                CasParser.parseCas(casFile, gappedReferenceMap);
               
                CasProfileMatrix profileMatrix = new CasProfileMatrix(
                        referenceIdLookup, readIdLookup, gappedReferenceMap, nucleotideDataStore);
                CasParser.parseCas(casFile, profileMatrix);
                Random random = new Random();
                for(Entry<String, Map<Long, Map<NucleotideGlyph, Float>>> referenceEntry :profileMatrix.getSnpMap().entrySet()){
                    String contigName = referenceEntry.getKey();
                    
                    NucleotideEncodedGlyphs reference=gappedReferenceMap.getGappedReferenceFor(referenceIdLookup.getCasIdFor(contigName));
                    currentWriter = new PrintWriter(new FileOutputStream(outputDir.createNewFile(
                                    String.format("%s.%s.profile",prefix,contigName))),true);
                    long currentOffset=0;
                    for(Entry<Long, Map<NucleotideGlyph, Float>> profileEntry : referenceEntry.getValue().entrySet()){
                        long offset = profileEntry.getKey();
                        while(currentOffset <offset){
                            currentWriter.printf("%s\t0.00\t0.00\t0.00\t0.00\t0.00\n",reference.get((int)currentOffset));
                            currentOffset++;
                        }
                        
                        final Map<NucleotideGlyph, Float> map = profileEntry.getValue();
                        Float maxCount = MathUtil.maxOf(map.values());
                        List<NucleotideGlyph> maxNucleotides = new ArrayList<NucleotideGlyph>(2);
                        for(Entry<NucleotideGlyph, Float> entry : map.entrySet()){
                            if(entry.getValue().equals(maxCount)){
                                maxNucleotides.add(entry.getKey());
                            }
                        }
                        final NucleotideGlyph maxNucleotide;
                        if(maxNucleotides.size() ==1){
                            maxNucleotide = maxNucleotides.get(0);
                        }else{
                            maxNucleotide = maxNucleotides.get(random.nextInt(maxNucleotides.size()));
                        }
                        currentWriter.printf("%s\t%.02f\t%.02f\t%.02f\t%.02f\t%.02f\n",maxNucleotide,
                                map.get(BASES_TO_USE.get(0)),
                                map.get(BASES_TO_USE.get(1)),
                                map.get(BASES_TO_USE.get(2)),
                                map.get(BASES_TO_USE.get(3)),
                                map.get(BASES_TO_USE.get(4)));
                        currentOffset++;
                    }
                    while(currentOffset <reference.getLength()){
                        currentWriter.printf("%s\t0.00\t0.00\t0.00\t0.00\t0.00\n",reference.get((int)currentOffset));
                        currentOffset++;
                    }
                    IOUtil.closeAndIgnoreErrors(currentWriter);
                }
            }catch(Throwable t){
                t.printStackTrace(logOut);
                printHelp(options);
                throw t;
            }finally{
                if(logOut !=null){
                     long endTime = System.currentTimeMillis();
                     
                     logOut.printf("took %s%n", new Period(endTime - startTime));
                     logOut.flush();
                     logOut.close();
                }
                IOUtil.closeAndIgnoreErrors(currentWriter);
                outputDir.close();
            }
    }
        
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "cas2Consed -cas <cas file> -o <output dir> [-prefix <prefix> -s <cache_size>]", 
                
                "convert a clc .cas assembly file into a consed package",
                options,
                "Created by Danny Katzel");
    }
}
