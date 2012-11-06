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
 * Created on Dec 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.assembly.ace.AceFileContigDataStore;
import org.jcvi.common.core.assembly.ace.AceFileDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.ConsensusAceTag;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;

public class Ace2Fasta {


    public static void main(String[] args) throws IOException, DataStoreException{
        Options options = new Options();
        
        options.addOption(new CommandLineOptionBuilder("ace", "path to ace file")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("out", "output path to fasta file to write")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("g", "print gapped consensus (default is ungapped)")
                            .longName("gapped")
                            .isFlag(true)
                            .build());
        options.addOption(CommandLineUtils.createHelpOption());
        
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        NucleotideSequenceFastaRecordWriter fastaWriter=null;
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
          
            final File aceIn = new File(commandLine.getOptionValue("ace"));
    
            fastaWriter = new NucleotideSequenceFastaRecordWriterBuilder(new File(commandLine.getOptionValue("out")))
            					.build();
            final boolean gapped = commandLine.hasOption("g");
            
            final File idFile;
            final DataStoreFilter filter;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                    includeList.removeAll(excludeList);
                }
                filter = DataStoreFilters.newIncludeFilter(includeList);
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = DataStoreFilters.newExcludeFilter(parseIdsFrom(idFile));
            }else{
                filter =DataStoreFilters.alwaysAccept();
            }
            AceFileContigDataStore contigDataStore = new AceFileDataStoreBuilder(aceIn)
													.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_MEMORY)
													.build();
            //consed allows users to rename contigs, but instead of changing
            //the CO record in the ace, the new name is stored as a comment at
            //the end of the file.  contigIdMap parses those comments 
            //to get the most current names
            //for those the contigs, any contig without those comments
            //will not exist in the map
            final Map<String, String> contigIdMap = getContigIdMap(contigDataStore);
            Iterator<String> ids = contigDataStore.idIterator();
            while(ids.hasNext()){
            	String contigId = ids.next();
            	if(!filter.accept(contigId)){
            		continue;
            	}
            	NucleotideSequenceBuilder consensusBuilder = new NucleotideSequenceBuilder(contigDataStore.get(contigId).getConsensusSequence());
               
                if(!gapped){
                	consensusBuilder.ungap();
                }
                String id = contigIdMap.containsKey(contigId)?
                                    contigIdMap.get(contigId)
                                    : contigId;
               
                String comment = aceIn.getName()+" (whole contig)";
                    fastaWriter.write(id,                                                                
                            consensusBuilder.build(),
                            comment);
                
            }
          
           
        } catch (ParseException e1) {
            printHelp(options);
            System.exit(1);
        }finally{
        	 IOUtil.closeAndIgnoreErrors(fastaWriter);
        }
    }

    private static Map<String, String> getContigIdMap(AceFileContigDataStore contigDataStore)
            throws IOException, DataStoreException {
        final Map<String, String> contigIdMap = new HashMap<String, String>();
        StreamingIterator<ConsensusAceTag> iter= contigDataStore.getConsensusTagIterator();
        try{
        	while(iter.hasNext()){
        		ConsensusAceTag consensusTag = iter.next();
        		if(ConsedUtil.isContigRename(consensusTag)){
        			 String originalId = consensusTag.getId();
                    contigIdMap.put(originalId, ConsedUtil.getRenamedContigId(consensusTag));
                }
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
        return contigIdMap;
    }
    
  
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ace2Fasta -ace <ace file> -out <output fasta>", 
                
                "create a fasta file from the contig consensus(es) of the given ace file."+
                "  The Ids used in the fasta are usually the contig ids except when "+
                "a user added a consed 'contigName' comment (renamed a contig inside consed), then the rename is used",
                options,
                "Created by Danny Katzel");
    }
    
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
}
