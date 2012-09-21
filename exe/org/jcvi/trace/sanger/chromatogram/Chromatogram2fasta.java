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

package org.jcvi.trace.sanger.chromatogram;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaRecordWriter;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogramBuilderVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramParser;
import org.jcvi.common.io.fileServer.DirectoryFileServer;
import org.jcvi.common.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.StringIdParser;

/**
 * {@code Chromatogram2fasta} is a program that will convert a list 
 * of chromatogram files into {@code .seq .qual and/or .pos}
 * (multi) FASTA files.
 * @author dkatzel
 *
 *
 */
public class Chromatogram2fasta implements Closeable{

    public static final String DEFAULT_FASTA_PREFIX = "chromatogram2fasta";
    
   
    private final NucleotideSequenceFastaRecordWriter seqOut;
    private final PositionSequenceFastaRecordWriter posOut;
    private final QualitySequenceFastaRecordWriter qualOut;
    
    
    
    public Chromatogram2fasta(OutputStream seqOut, OutputStream qualOut,
			OutputStream posOut) {
    	if(seqOut ==null && qualOut ==null && posOut==null){
    		throw new NullPointerException("must have at least 1 non-null outputStream");
    	}
		this.seqOut = seqOut==null? null : new NucleotideSequenceFastaRecordWriterBuilder(seqOut).build();
		this.posOut = posOut==null? null :new PositionSequenceFastaRecordWriterBuilder(posOut).build();
		this.qualOut = qualOut==null? null :new QualitySequenceFastaRecordWriterBuilder(qualOut).build();
	}

    public void writeChromatogram(String id, Chromatogram chromo) throws IOException{
    	if(seqOut !=null){
    		seqOut.write(id, chromo.getNucleotideSequence());
    	}
    	if(qualOut !=null){
    		qualOut.write(id, chromo.getQualitySequence());
    	}
    	if(posOut !=null){
    		posOut.write(id, chromo.getPositionSequence());
    	}
    }

    
	@Override
	public void close() throws IOException {
		IOUtil.closeAndIgnoreErrors(seqOut, qualOut,posOut);
		
	}

	/**
     * Executable to convert group of chromatogram files into multi fastas.
     * @param args
     * @throws IOException 
     * @throws TraceDecoderException 
     */
    public static void main(String[] args) throws IOException, TraceDecoderException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("i", "file of paths to scf and/or ztr files to process")
                        .longName("infile")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("o", "output directory to put fasta files (defaults to current directory)")
                        .longName("output_dir")                    
                        .build());
        options.addOption(new CommandLineOptionBuilder("prefix", String.format("prefix for fasta files to generate.  (default : %s)",DEFAULT_FASTA_PREFIX))                   
                        .build());
        options.addOption(new CommandLineOptionBuilder("s", "create a sequence fasta file with extension .seq")
                        .longName("seq")
                        .isFlag(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("q", "create a quality fasta file with extension .qual")
                        .longName("qual")
                        .isFlag(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("p", "create a position fasta file with extension .pos")
                        .longName("pos")
                        .isFlag(true)
                        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
       try {
        CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
        if(!commandLine.hasOption("s") && !commandLine.hasOption("p") && !commandLine.hasOption("q")){
            String message ="must specifiy -s -p and/or -q to generate any fasta records";
            System.err.println(message);
            throw new ParseException(message);
        }
        String outputDirPath = commandLine.hasOption("o")? commandLine.getOptionValue("o"): ".";
        ReadWriteDirectoryFileServer outputFileServer = DirectoryFileServer.createReadWriteDirectoryFileServer(outputDirPath);
        String prefix = commandLine.hasOption("prefix")? commandLine.getOptionValue("prefix"):DEFAULT_FASTA_PREFIX;
        File inFile = new File(commandLine.getOptionValue("i"));
        IdReader<String> chromatogramFiles = new DefaultFileIdReader<String>(inFile, new StringIdParser());
        final OutputStream seqOut = commandLine.hasOption("s")? 
                        new FileOutputStream(outputFileServer.createNewFile(prefix +".seq"))
                        : null;
                        
        final OutputStream qualOut = commandLine.hasOption("q")? 
                new FileOutputStream(outputFileServer.createNewFile(prefix +".qual"))
                : null;
        final OutputStream posOut = commandLine.hasOption("p")? 
                new FileOutputStream(outputFileServer.createNewFile(prefix +".pos"))
                : null;
        Chromatogram2fasta chromo2Fasta = new Chromatogram2fasta(seqOut,qualOut,posOut);
        try{
            for(String chromatogramFilePath : chromatogramFiles){
                if(chromatogramFilePath.trim().isEmpty()){
                    continue;
                }
                File chromatogramFile = new File(chromatogramFilePath);
                
                String id = FileUtil.getBaseName(chromatogramFile);
                BasicChromatogramBuilderVisitor builder = new BasicChromatogramBuilderVisitor(id);
                ChromatogramParser.parse(chromatogramFile, builder);
                
                chromo2Fasta.writeChromatogram(id, builder.build());
               
            }
        }finally{
            IOUtil.closeAndIgnoreErrors(chromo2Fasta);
        }
    } catch (ParseException e) {
        System.err.println(e.getMessage());
        printHelp(options);
    }

    }

    static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "chromatogram2fasta -i <file of chromatograms> [OPTIONS]", 
                
                "parse the chromtograms (either .scf or .ztr or ab1) whose paths are given in the -i option " +
                "and generate .seq .qual and/or .pos multi fasta files.  The ids in the fasta file(s) "+
                "will be the file name without the extension (ex : SEQNAME.scf will have a FASTA defline \">SEQNAME\")",
                options,
                "Created by Danny Katzel");
    }

}
