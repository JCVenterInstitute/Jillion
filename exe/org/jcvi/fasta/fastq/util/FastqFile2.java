package org.jcvi.fasta.fastq.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.ExcludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.IncludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqFileDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriter;
import org.jcvi.common.core.seq.fastx.fastq.FastqUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.FirstWordStringIdParser;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;

public class FastqFile2 {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File fastQFile = new File(args[args.length-1]);
        
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("o", "path to output file (required)")
        		.isRequired(true)
                .longName("output")
                .build());
        
        options.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        options.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOption(new CommandLineOptionBuilder("n", "number of ids in file (optional optimization)")
        
        .build());
        
        options.addOption(new CommandLineOptionBuilder("q", "re-encode quality values in output file.  " +
        													"Valid values are either 'SANGER' or 'ILLUMINA'")
        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        String[] argumentsOnly = Arrays.copyOf(args, args.length-1);
        if(args.length <2 || CommandLineUtils.helpRequested(argumentsOnly)){
            printHelp(options);
            System.exit(0);
        }
        PrintWriter out=null;
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, 
            		argumentsOnly);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            FastqQualityCodec qualityCodec =FastqUtil.guessQualityCodecUsed(fastQFile);
            File outputFile = new File(commandLine.getOptionValue("o"));
            IOUtil.mkdirs(outputFile.getParentFile());
        	out = new PrintWriter(outputFile);
           
        	FastqRecordWriterBuilder writerBuilder = new FastqRecordWriterBuilder(outputFile);
            final File idFile;
            final DataStoreFilter filter;
            Integer numberOfIds =commandLine.hasOption("n")?Integer.parseInt(commandLine.getOptionValue("n")):null;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile,numberOfIds);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(
                            new File(commandLine.getOptionValue("e")),numberOfIds);
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastXIdFilter(includeList);
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile,numberOfIds));
            }else{
            	filter=AcceptingDataStoreFilter.INSTANCE;
            }
            if(commandLine.hasOption("q")){
            	//re-encode qualities
            	FastqQualityCodec reEncodedQualityCodec = parseQualityCodec(commandLine.getOptionValue("q"));
            	 
            	writerBuilder.qualityCodec(reEncodedQualityCodec);
            }else{
            	//keep encoding what it was
            	writerBuilder.qualityCodec(qualityCodec);
            }
            FastqRecordWriter writer = writerBuilder.build();
        	StreamingIterator<FastqRecord> iter=null;
             try{
             	iter = new FastqFileDataStoreBuilder(fastQFile)
								.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
								.qualityCodec(qualityCodec)
								.filter(filter)
								.build()
								.iterator();
             	while(iter.hasNext()){
             		writer.write(iter.next());
             	}
             }finally{
             	IOUtil.closeAndIgnoreErrors(iter,writer);
             }
            
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }finally{
            IOUtil.closeAndIgnoreErrors(out);
        }
        
    }



    private static FastqQualityCodec parseQualityCodec(String optionValue) {
		String trimmedValue = optionValue.trim();
		if("SANGER".equalsIgnoreCase(trimmedValue)){
			return FastqQualityCodec.SANGER;
		}
		if("ILLUMINA".equalsIgnoreCase(trimmedValue)){
			return FastqQualityCodec.ILLUMINA;
		}
		throw new IllegalArgumentException(
				String.format("unknown quality encoding value '%s'",optionValue));
	}

   

	private static Set<String> parseIdsFrom(final File idFile, Integer numberOfIds)
            throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new FirstWordStringIdParser());
        final Set<String> ids; 
        if(numberOfIds ==null){
            ids= new HashSet<String>();
        }else{
            ids= new HashSet<String>(numberOfIds.intValue(),1F);
        }
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fastQFile  [-i | -e] idFile -o <output fastQ file> [OPTIONS] <fastq file>", 
                
                "filter  a fastQ file using the given ids to include/exclude and write the result to a new fastQ file",
                options,
                String.format("Example invocation%nfastQfile.pl -i ids.lst -o filtered.fastq original.fastq%nCreated by Danny Katzel"
                  ));
    }
    
   
}
