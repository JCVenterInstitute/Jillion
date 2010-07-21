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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.jcvi.Range;
import org.jcvi.assembly.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasFileQualityDataStore;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2SffCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.cas.read.ReadCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.SffTrimDataStore;
import org.jcvi.assembly.cas.var.DefaultVariationLogFile;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.jcvi.util.MultipleWrapper;
import org.jcvi.util.StringUtilities;

/**
 * @author dkatzel
 *
 *
 */
public class CasSNPMatrix {

    private static class CasSNPMatrixGenerator extends AbstractCasFileContigVisitor{
        private final long referenceIdToGenerateSNPsFor;
       // private final Map<String, Map<Integer, NucleotideGlyph>> snpMap = new HashMap<String, Map<Integer,NucleotideGlyph>>();
        private final List<Integer> gappedSNPCoordinates = new ArrayList<Integer>();
        private final  PrintWriter writer;
        /**
         * @param referenceIdLookup
         * @param readIdLookup
         * @param gappedReferenceMap
         * @param nucleotideDataStore
         * @param trimDataStore
         */
        public CasSNPMatrixGenerator(CasIdLookup referenceIdLookup,
                CasIdLookup readIdLookup,
                CasGappedReferenceMap gappedReferenceMap,
                DataStore<NucleotideEncodedGlyphs> nucleotideDataStore,
                TrimDataStore trimDataStore, 
                long referenceIdToGenerateSNPsFor, 
                List<Integer> gappedSNPCoordinates,
                PrintWriter writer) {
            super(referenceIdLookup, readIdLookup, gappedReferenceMap, nucleotideDataStore,
                    trimDataStore);
            this.referenceIdToGenerateSNPsFor =referenceIdToGenerateSNPsFor;
            this.gappedSNPCoordinates.addAll(gappedSNPCoordinates);
            this.writer = writer;

        }

       

        /**
        * {@inheritDoc}
        */
        @Override
        protected void visitPlacedRead(long referenceId,
                CasPlacedRead casPlacedRead) {
            if(referenceId == referenceIdToGenerateSNPsFor){
                
                String readId = casPlacedRead.getId();
                StringBuilder snps = new StringBuilder(readId).append("\t");
                Range readRange = Range.buildRange(casPlacedRead.getStart(), casPlacedRead.getEnd());
                List<NucleotideGlyph> basecalls = casPlacedRead.getEncodedGlyphs().decode();
                for(Integer snpCoordinate: gappedSNPCoordinates){
                    
                    if(readRange.intersects(snpCoordinate)){
                        long index =casPlacedRead.convertReferenceIndexToValidRangeIndex(snpCoordinate);
                       /* if(!snpMap.containsKey(readId)){
                            snpMap.put(readId, new HashMap<Integer, NucleotideGlyph>());
                        }
                        Map<Integer, NucleotideGlyph> readSNPs =snpMap.get(readId);
                        readSNPs.put(snpCoordinate, basecalls.get((int)index));
                        */
                        snps.append(basecalls.get((int)index));
                    }
                    snps.append("\t");
                }
                writer.printf("%s%n", snps.toString());
            }
            
        }

        
    }
    /**
     * @param args
     * @throws IOException 
     * @throws SFFDecoderException 
     */
    public static void main(String[] args) throws IOException, SFFDecoderException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("cas", "input cas file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("out", "output file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("id", "contig id")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("v", "file of variations log")
                        .isRequired(true)
                        .build());
       
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File casFile = new File(commandLine.getOptionValue("cas"));
            String contigId = commandLine.getOptionValue("id");
            boolean isGapped = false;
            String outputFilePath = commandLine.getOptionValue("out");
            File variationsFile = new File(commandLine.getOptionValue("v"));
            DefaultVariationLogFile  varaintMap = new DefaultVariationLogFile(variationsFile);
               
            final IlluminaFastQQualityCodec solexaQualityCodec = new IlluminaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
            MultiCasDataStoreFactory casDataStoreFactory = new MultiCasDataStoreFactory(
                    new H2SffCasDataStoreFactory(DirectoryFileServer.createTemporaryDirectoryFileServer(new File("/usr/local/scratch/dkatzel/")), EmptyDataStoreFilter.INSTANCE),               
                    new H2FastQCasDataStoreFactory(solexaQualityCodec),
                    new FastaCasDataStoreFactory(100)        
            );
            AbstractDefaultCasFileLookup readIdLookup = new DefaultReadCasFileLookup();
            AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup();
            AbstractCasFileNucleotideDataStore nucleotideDataStore = new ReadCasFileNucleotideDataStore(casDataStoreFactory);
            AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(casDataStoreFactory);
            
            DefaultCasFileQualityDataStore qualityDataStore = new DefaultCasFileQualityDataStore(casDataStoreFactory);
            System.out.println("parsing dataStore info...");
           CasParser.parseCas(casFile, MultipleWrapper.createMultipleWrapper(
                        CasFileVisitor.class, 
                        readIdLookup, 
                        referenceIdLookup,
                        nucleotideDataStore,
                        referenceNucleotideDataStore,
                        qualityDataStore));
           
            DefaultCasGappedReferenceMap gappedReferenceMap = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup);
            System.out.println("parsing gapped references...");
            CasParser.parseCas(casFile, gappedReferenceMap);
            
            long casReferenceId = referenceIdLookup.getCasIdFor(contigId);
            NucleotideEncodedGlyphs consensus =gappedReferenceMap.getGappedReferenceFor(casReferenceId);
            
            List<Integer> gappedSNPCoordinates = new ArrayList<Integer>();
            Map<Integer, Integer> gappedCoordinateToPositionMap = new HashMap<Integer, Integer>();
            for(long coordinate : varaintMap.getVariationsFor(contigId).keySet()){
                final int gappedOffset;
                if(isGapped){
                    gappedOffset = (int)coordinate-1;
                }
                else{
                    int ungappedOffset =(int) coordinate-1;
                    gappedOffset =consensus.convertUngappedValidRangeIndexToGappedValidRangeIndex(ungappedOffset);
                }
                gappedSNPCoordinates.add(gappedOffset);
                gappedCoordinateToPositionMap.put(gappedOffset, (int)coordinate);
            }
            SffTrimDataStore sffTrimDatastore = new SffTrimDataStore();
            for(File readFile : readIdLookup.getFiles()){
                String extension =FilenameUtils.getExtension(readFile.getName());
                if("sff".equals(extension)){
                    SffParser.parseSFF(readFile, sffTrimDatastore);
                }
            }
           
            PrintWriter writer = new PrintWriter(new FileOutputStream(outputFilePath),true);
            writer.printf("#id\t%s%n", contigId);
            writer.printf("#loc\t%s%n", StringUtilities.join("\t", varaintMap.getVariationsFor(contigId).keySet()));
            CasSNPMatrixGenerator snpGenerator = new CasSNPMatrixGenerator(referenceIdLookup,
                    readIdLookup,
                    gappedReferenceMap,
                    nucleotideDataStore,
                    sffTrimDatastore, 
                    casReferenceId, 
                    gappedSNPCoordinates,writer);
            System.out.println("parsing SNPs...");
            CasParser.parseCas(casFile, snpGenerator);
            /*
            for(Entry<String, Map<Integer, NucleotideGlyph>> entry : snpGenerator.getSnpMap().entrySet()){
                StringBuilder snps = new StringBuilder();
                Map<Integer, NucleotideGlyph> readSNPs = entry.getValue();
                for(Integer gappedSNPCoordinate : gappedSNPCoordinates){
                    if(readSNPs.containsKey(gappedSNPCoordinate)){                        
                        snps.append(readSNPs.get(gappedSNPCoordinate));
                    }
                    snps.append("\t");
                }
                writer.printf("%s\t%s%n", entry.getKey(),snps.toString());
            }
            */
            writer.close();
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "generateSNPMatrix -cas <cas file> -id <contig id> -v <variation log file> -out <output fasta> [OPTIONS]", 
                
                "create a SNP Matrix for all the reads at the given positions in the given cas file using the variation log file",
                options,
                "Created by Danny Katzel");
    }
}
