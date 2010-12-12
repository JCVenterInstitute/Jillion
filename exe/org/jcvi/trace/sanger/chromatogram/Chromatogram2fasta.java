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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.fasta.DefaultPositionFastaRecord;
import org.jcvi.fasta.DefaultQualityFastaRecord;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.StringIdParser;
import org.jcvi.trace.TraceDecoderException;

/**
 * {@code Chromatogram2fasta} is a program that will convert a list 
 * of chromatogram files into {@code .seq .qual and/or .pos}
 * (multi) FASTA files.
 * @author dkatzel
 *
 *
 */
public class Chromatogram2fasta {

    public static final String DEFAULT_FASTA_PREFIX = "chromatogram2fasta";
    
    private static class NULLOutputStream extends OutputStream{

		@Override
		public void write(int b) throws IOException {
			//no-op
		}
    }
    
    private static final NULLOutputStream NULL_OUTPUTSTREAM= new NULLOutputStream();
    
    private final OutputStream seqOut, posOut, qualOut;
    
    
    
    public Chromatogram2fasta(OutputStream seqOut, OutputStream qualOut,
			OutputStream posOut) {
    	if(seqOut ==null && qualOut ==null && posOut==null){
    		throw new NullPointerException("must have at least 1 non-null outputStream");
    	}
		this.seqOut = seqOut==null? NULL_OUTPUTSTREAM : seqOut;
		this.posOut = posOut==null? NULL_OUTPUTSTREAM : posOut;
		this.qualOut = qualOut==null? NULL_OUTPUTSTREAM :qualOut ;
	}

    public void writeChromatogram(String id, Chromatogram chromo) throws IOException{
        //small hit converting all to fastas even if not outputing them all
    	//is worth cleaner code, fix if this becomes a bottleneck.
    	seqOut.write(new DefaultEncodedNucleotideFastaRecord(id, chromo.getBasecalls())
                             .toString().getBytes());
    	qualOut.write(new DefaultQualityFastaRecord(id, chromo.getQualities())
                             .toString().getBytes());
    	posOut.write(new DefaultPositionFastaRecord(id, chromo.getPeaks().getData())
                             .toString().getBytes());
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
                
                String id = FilenameUtils.getBaseName(chromatogramFile.getName());
                BasicChromatogramFile chromo = new BasicChromatogramFile();
                ChromatogramParser.parse(chromatogramFile, chromo);
                
                chromo2Fasta.writeChromatogram(id, chromo);
               
            }
        }finally{
            IOUtil.closeAndIgnoreErrors(seqOut);
            IOUtil.closeAndIgnoreErrors(qualOut);
            IOUtil.closeAndIgnoreErrors(posOut);
        }
    } catch (ParseException e) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "chromatogram2fasta -i <file of chromatograms> [OPTIONS]", 
                
                "parse the chromtograms (either .scf or .ztr) whose paths are given in the -i option " +
                "and generate .seq .qual and/or .pos multi fasta files.  The ids in the fasta file(s) "+
                "will be the file name without the extension (ex : SEQNAME.scf will have a FASTA defline \">SEQNAME\")",
                options,
                "Created by Danny Katzel");
    }

    }

}
